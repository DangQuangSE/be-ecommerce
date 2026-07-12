package com.sport_pro_be.modules.order.service;

import com.sport_pro_be.exception.BadRequestException;
import com.sport_pro_be.exception.ResourceNotFoundException;
import com.sport_pro_be.modules.auth.domain.User;
import com.sport_pro_be.modules.auth.repository.UserRepository;
import com.sport_pro_be.modules.membership.interfaces.ITierService;
import com.sport_pro_be.modules.order.constant.OrderMessageConstant;
import com.sport_pro_be.modules.order.domain.Order;
import com.sport_pro_be.modules.order.enums.OrderStatus;
import com.sport_pro_be.modules.order.enums.PaymentMethod;
import com.sport_pro_be.modules.order.repository.OrderRepository;
import com.sport_pro_be.modules.cart.domain.Cart;
import com.sport_pro_be.modules.cart.repository.CartRepository;
import com.sport_pro_be.modules.order.domain.OrderItem;
import com.sport_pro_be.modules.product.domain.ProductVariant;
import com.sport_pro_be.modules.product.repository.ProductVariantRepository;
import com.sport_pro_be.modules.order.enums.RefundStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderTransitionService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ITierService tierService;
    private final Clock clock;
    private final CartRepository cartRepository;
    private final ProductVariantRepository productVariantRepository;

    @Transactional
    public Order recordVerifiedPayment(Long orderId) {
        Order order = lock(orderId);
        if (order.getPaymentMethod() != PaymentMethod.BANK_TRANSFER) {
            throw new BadRequestException("Verified online payment requires BANK_TRANSFER");
        }
        if (!order.isPaymentCompleted()) {
            fulfillOnlineOrder(order);
            order.recordPayment(nowUtc());
        }
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancel(Long orderId, Long userId, String reason) {
        Order order = lock(orderId);
        if (!order.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(OrderMessageConstant.ORDER_NOT_FOUND);
        }
        if (!isAllowed(order.getStatus(), OrderStatus.CANCELLED)) {
            throw new BadRequestException("Order cannot be cancelled in its current status");
        }
        order.setCancelReason(reason);
        boolean stockWasDecremented = order.getPaymentMethod() != PaymentMethod.BANK_TRANSFER || order.isPaymentCompleted();
        if (order.getPaymentMethod() == PaymentMethod.BANK_TRANSFER && order.isPaymentCompleted()) {
            order.setRefundStatus(RefundStatus.PENDING);
        }
        if (stockWasDecremented) restoreStock(order);
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = lock(orderId);
        if (order.getStatus() == status) {
            return order;
        }
        if (!isAllowed(order.getStatus(), status)) {
            throw new BadRequestException("Invalid order status transition: " + order.getStatus() + " -> " + status);
        }
        if (status == OrderStatus.DELIVERED) {
            deliver(order);
        } else {
            order.setStatus(status);
            if (status == OrderStatus.RETURNED || status == OrderStatus.REFUNDED) {
                reverseSpending(order);
            }
        }
        return orderRepository.save(order);
    }

    @Transactional
    public Order lock(Long orderId) {
        return orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(OrderMessageConstant.ORDER_NOT_FOUND));
    }

    private void deliver(Order order) {
        LocalDateTime occurredAt = nowUtc();
        if (order.getPaymentMethod() == PaymentMethod.COD) {
            if (!order.isPaymentCompleted()) {
                order.recordPayment(occurredAt);
            }
        } else if (!order.isPaymentCompleted()) {
            throw new BadRequestException("Online-payment order must be paid before delivery");
        }
        if (order.getDeliveredAt() == null) {
            order.recordDelivery(occurredAt);
        }
        order.setStatus(OrderStatus.DELIVERED);
        creditSpending(order);
    }

    private void fulfillOnlineOrder(Order order) {
        Cart cart = cartRepository.findByUserId(order.getUser().getId()).orElse(null);
        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getProductVariant();
            int remaining = variant.getStockQuantity() - item.getQuantity();
            if (remaining < 0) {
                throw new BadRequestException(String.format(OrderMessageConstant.INSUFFICIENT_STOCK,
                        variant.getProduct().getName(), variant.getSize()));
            }
            variant.setStockQuantity(remaining);
            productVariantRepository.save(variant);
            if (cart != null && cart.getItems() != null) {
                Long designId = item.getCustomDesign() == null ? null : item.getCustomDesign().getId();
                cart.getItems().removeIf(cartItem -> Objects.equals(cartItem.getProductVariant().getId(), variant.getId())
                        && Objects.equals(cartItem.getCustomDesign() == null ? null : cartItem.getCustomDesign().getId(), designId)
                        && Objects.equals(cartItem.getQuantity(), item.getQuantity()));
            }
        }
        if (cart != null) cartRepository.save(cart);
        if (order.getCoupon() != null) order.getCoupon().setUsedCount(order.getCoupon().getUsedCount() + 1);
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getProductVariant();
            variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
            productVariantRepository.save(variant);
        }
    }

    private void creditSpending(Order order) {
        if (order.isSpendingCredited()) {
            return;
        }
        User user = order.getUser();
        user.setTotalSpending(user.getTotalSpending().add(order.getTotalAmount()));
        tierService.updateUserTier(user);
        userRepository.save(user);
        order.markSpendingCredited();
    }

    private void reverseSpending(Order order) {
        if (!order.isSpendingCredited() || order.isSpendingReversed()) {
            return;
        }
        User user = order.getUser();
        user.setTotalSpending(user.getTotalSpending().subtract(order.getTotalAmount()).max(java.math.BigDecimal.ZERO));
        tierService.updateUserTier(user);
        userRepository.save(user);
        order.markSpendingReversed();
    }

    private LocalDateTime nowUtc() {
        return LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    }

    private boolean isAllowed(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case PENDING -> to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED;
            case CONFIRMED -> to == OrderStatus.PROCESSING || to == OrderStatus.CANCELLED;
            case PROCESSING -> to == OrderStatus.SHIPPED || to == OrderStatus.CANCELLED;
            case SHIPPED -> to == OrderStatus.DELIVERED || to == OrderStatus.CANCELLED;
            case DELIVERED -> to == OrderStatus.RETURN_REQUESTED;
            case RETURN_REQUESTED -> to == OrderStatus.RETURNED || to == OrderStatus.DELIVERED;
            case RETURNED -> to == OrderStatus.REFUNDED;
            case CANCELLED, REFUNDED -> false;
        };
    }
}
