package com.sport_pro_be.modules.order.service;

import com.sport_pro_be.modules.order.constant.OrderMessageConstant;
import com.sport_pro_be.exception.BadRequestException;
import com.sport_pro_be.exception.ResourceNotFoundException;
import com.sport_pro_be.modules.audit.annotation.Loggable;
import com.sport_pro_be.modules.auth.domain.User;
import com.sport_pro_be.modules.auth.repository.UserRepository;
import com.sport_pro_be.modules.cart.domain.Cart;
import com.sport_pro_be.modules.cart.domain.CartItem;
import com.sport_pro_be.modules.cart.repository.CartRepository;
import com.sport_pro_be.modules.order.domain.Order;
import com.sport_pro_be.modules.order.domain.OrderItem;
import com.sport_pro_be.modules.notification.service.NotificationService;
import com.sport_pro_be.modules.notification.enums.NotificationType;
import com.sport_pro_be.modules.order.dto.OrderItemResponse;
import com.sport_pro_be.modules.order.dto.OrderRequest;
import com.sport_pro_be.modules.order.dto.OrderResponse;
import com.sport_pro_be.modules.order.enums.OrderStatus;
import com.sport_pro_be.modules.order.enums.PaymentMethod;
import com.sport_pro_be.modules.order.interfaces.IOrderService;
import com.sport_pro_be.modules.order.repository.OrderItemRepository;
import com.sport_pro_be.modules.order.repository.OrderRepository;
import com.sport_pro_be.modules.product.domain.ProductVariant;
import com.sport_pro_be.modules.product.domain.ProductImage;
import com.sport_pro_be.modules.product.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final com.sport_pro_be.modules.coupon.interfaces.ICouponService couponService;
    private final com.sport_pro_be.modules.membership.interfaces.ITierService tierService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    @Loggable(action = "PLACE_ORDER", module = "ORDER")
    public OrderResponse placeOrder(Long userId, OrderRequest request) {
        log.info("Placing order for user id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(OrderMessageConstant.USER_NOT_FOUND));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(OrderMessageConstant.CART_EMPTY));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException(OrderMessageConstant.CART_EMPTY);
        }

        List<CartItem> itemsToOrder;
        if (request.getCartItemIds() != null && !request.getCartItemIds().isEmpty()) {
            itemsToOrder = cart.getItems().stream()
                    .filter(item -> request.getCartItemIds().contains(item.getId()))
                    .collect(Collectors.toList());

            if (itemsToOrder.isEmpty()) {
                throw new BadRequestException(OrderMessageConstant.INVALID_SELECTED_ITEMS);
            }
        } else {
            itemsToOrder = new ArrayList<>(cart.getItems());
        }

        // BANK_TRANSFER = VNPay online transfer (defer stock/cart until IPN success)
        boolean deferFulfillment = request.getPaymentMethod() == PaymentMethod.BANK_TRANSFER;

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        String customerName = request.getCustomerName();
        if (customerName != null) {
            customerName = customerName.trim();
            if (customerName.isEmpty()) {
                customerName = null;
            }
        }

        Order order = Order.builder()
                .user(user)
                .shippingAddress(request.getShippingAddress())
                .phoneNumber(request.getPhoneNumber())
                .customerName(customerName)
                .paymentMethod(request.getPaymentMethod())
                .status(OrderStatus.PENDING)
                .paymentCompleted(false)
                .totalAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .build();

        // Must save order first to satisfy foreign key for OrderItem
        order = orderRepository.save(order);

        for (CartItem cartItem : itemsToOrder) {
            ProductVariant variant = cartItem.getProductVariant();

            if (variant.getStockQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException(String.format(OrderMessageConstant.INSUFFICIENT_STOCK,
                        variant.getProduct().getName(), variant.getSize()));
            }

            if (!deferFulfillment) {
                variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());
                productVariantRepository.save(variant);
            }

            // Determine product price
            BigDecimal itemPrice = variant.getSalePrice() != null ? variant.getSalePrice() : variant.getOriginalPrice();
            BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            // Add printing price if this item has a custom design (snapshot price stored in
            // design)
            if (cartItem.getCustomDesign() != null) {
                itemTotal = itemTotal.add(cartItem.getCustomDesign().getTotalPrintingPrice());
            }
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productVariant(variant)
                    .quantity(cartItem.getQuantity())
                    .price(itemPrice)
                    .customDesign(cartItem.getCustomDesign())
                    .build();

            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);

        // Handle Coupon
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            com.sport_pro_be.modules.coupon.domain.Coupon coupon = couponService
                    .validateAndGetCoupon(request.getCouponCode(), user, totalAmount);
            discountAmount = couponService.calculateDiscount(coupon, totalAmount);
            order.setCoupon(coupon);
            if (!deferFulfillment) {
                coupon.setUsedCount(coupon.getUsedCount() + 1);
            }
        }

        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(totalAmount.subtract(discountAmount));
        order.setItems(orderItems);
        orderRepository.save(order);

        if (!deferFulfillment) {
            cart.getItems().removeAll(itemsToOrder);
            cartRepository.save(cart);
        }

        try {
            notificationService.createAdminNotification(
                    "Đơn hàng mới: #" + order.getId(),
                    "Đơn hàng mới được đặt bởi " + (order.getCustomerName() != null ? order.getCustomerName() : user.getFullName()),
                    NotificationType.NEW_ORDER,
                    order.getId(),
                    order.getCustomerName() != null ? order.getCustomerName() : user.getFullName()
            );
        } catch (Exception e) {
            log.error("Failed to create admin notification for order: {}", order.getId(), e);
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(this::mapToOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(OrderMessageConstant.ORDER_NOT_FOUND));
        return mapToOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(String search, OrderStatus status, Pageable pageable) {
        String cleanSearch = (search == null || search.trim().isEmpty()) ? "" : search.trim();
        return orderRepository.searchOrders(cleanSearch, status, pageable).map(this::mapToOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetailsAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(OrderMessageConstant.ORDER_NOT_FOUND));
        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    @Loggable(action = "UPDATE_ORDER_STATUS", module = "ORDER")
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(OrderMessageConstant.ORDER_NOT_FOUND));

        if (status == OrderStatus.DELIVERED && order.getStatus() != OrderStatus.DELIVERED) {
            User user = order.getUser();
            user.setTotalSpending(user.getTotalSpending().add(order.getTotalAmount()));
            tierService.updateUserTier(user);
            userRepository.save(user);
        }

        order.setStatus(status);
        orderRepository.save(order);

        try {
            notificationService.createCustomerNotification(
                    order.getUser().getId(),
                    "Trạng thái đơn hàng",
                    "Đơn hàng #" + order.getId() + " của bạn hiện đang ở trạng thái: " + status.name(),
                    NotificationType.ORDER_STATUS_CHANGED,
                    order.getId()
            );
        } catch (Exception e) {
            log.error("Failed to create customer notification for order: {}", order.getId(), e);
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    @Loggable(action = "CANCEL_ORDER", module = "ORDER")
    public OrderResponse cancelOrder(Long userId, Long orderId, String reason) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(OrderMessageConstant.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED && order.getStatus() != OrderStatus.PROCESSING) {
            throw new BadRequestException("Order cannot be cancelled in its current status");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelReason(reason);

        boolean stockWasDecremented = true;
        if (order.getPaymentMethod() == PaymentMethod.BANK_TRANSFER && !order.isPaymentCompleted()) {
            stockWasDecremented = false;
        }

        if (order.getPaymentMethod() == PaymentMethod.BANK_TRANSFER && order.isPaymentCompleted()) {
            order.setRefundStatus(com.sport_pro_be.modules.order.enums.RefundStatus.PENDING);
        }

        if (stockWasDecremented) {
            for (OrderItem item : order.getItems()) {
                ProductVariant variant = item.getProductVariant();
                variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
                productVariantRepository.save(variant);
            }
        }

        orderRepository.save(order);

        String customerMessage = "Đơn hàng #" + order.getId() + " của bạn đã được hủy thành công.";
        if (order.getPaymentMethod() == PaymentMethod.BANK_TRANSFER && order.isPaymentCompleted()) {
            customerMessage += " Nhân viên sẽ liên hệ hoàn tiền qua số điện thoại đặt hàng của bạn.";
        }

        try {
            notificationService.createCustomerNotification(
                    order.getUser().getId(),
                    "Đơn hàng đã được hủy",
                    customerMessage,
                    NotificationType.ORDER_STATUS_CHANGED,
                    order.getId()
            );
        } catch (Exception e) {
            log.error("Failed to create customer notification for cancelled order: {}", order.getId(), e);
        }

        try {
            notificationService.createAdminNotification(
                    "Đơn hàng bị hủy: #" + order.getId(),
                    "Khách hàng đã hủy đơn hàng với lý do: " + reason,
                    NotificationType.ORDER_CANCELLED,
                    order.getId(),
                    order.getCustomerName() != null ? order.getCustomerName() : order.getUser().getFullName()
            );
        } catch (Exception e) {
            log.error("Failed to create admin notification for cancelled order: {}", order.getId(), e);
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public void fulfillOrder(Long orderId) {
        Order order = orderRepository.findFulfillmentGraphById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(OrderMessageConstant.ORDER_NOT_FOUND));

        if (order.isPaymentCompleted()) {
            return;
        }

        Cart cart = cartRepository.findByUserId(order.getUser().getId()).orElse(null);

        for (OrderItem orderItem : order.getItems()) {
            ProductVariant variant = orderItem.getProductVariant();
            int newStock = variant.getStockQuantity() - orderItem.getQuantity();
            if (newStock < 0) {
                throw new BadRequestException(String.format(OrderMessageConstant.INSUFFICIENT_STOCK,
                        variant.getProduct().getName(), variant.getSize()));
            }
            variant.setStockQuantity(newStock);
            productVariantRepository.save(variant);

            if (cart != null && cart.getItems() != null) {
                Long orderDesignId = orderItem.getCustomDesign() != null
                        ? orderItem.getCustomDesign().getId()
                        : null;
                cart.getItems().removeIf(cartItem -> {
                    Long cartDesignId = cartItem.getCustomDesign() != null
                            ? cartItem.getCustomDesign().getId()
                            : null;
                    return cartItem.getProductVariant().getId().equals(variant.getId())
                            && Objects.equals(cartDesignId, orderDesignId)
                            && cartItem.getQuantity().equals(orderItem.getQuantity());
                });
            }
        }

        if (cart != null) {
            cartRepository.save(cart);
        }

        if (order.getCoupon() != null) {
            com.sport_pro_be.modules.coupon.domain.Coupon coupon = order.getCoupon();
            coupon.setUsedCount(coupon.getUsedCount() + 1);
        }
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> {
                    OrderItemResponse.OrderItemResponseBuilder builder = OrderItemResponse.builder()
                            .id(item.getId())
                            .productVariantId(item.getProductVariant().getId())
                            .productName(item.getProductVariant().getProduct().getName())
                            .sku(item.getProductVariant().getSku())
                            .size(item.getProductVariant().getSize())
                            .color(item.getProductVariant().getColor() != null
                                    ? item.getProductVariant().getColor().getName()
                                    : item.getProductVariant().getColorOld())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .isReviewed(item.getReview() != null);

                    if (item.getCustomDesign() != null) {
                        builder.customDesignId(item.getCustomDesign().getId())
                                .designImageUrl(item.getCustomDesign().getDesignImageUrl())
                                .backDesignImageUrl(item.getCustomDesign().getBackDesignImageUrl())
                                .printingPrice(item.getCustomDesign().getTotalPrintingPrice());
                    } else {
                        String defaultImageUrl = item.getProductVariant().getProduct().getImages().stream()
                                .filter(img -> Boolean.TRUE.equals(img.getIsThumbnail()))
                                .map(ProductImage::getImageUrl)
                                .findFirst()
                                .orElseGet(() -> item.getProductVariant().getProduct().getImages().stream()
                                        .map(ProductImage::getImageUrl)
                                        .findFirst()
                                        .orElse(null));
                        builder.designImageUrl(defaultImageUrl);
                    }
                    return builder.build();
                })
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .shippingAddress(order.getShippingAddress())
                .phoneNumber(order.getPhoneNumber())
                .customerName(order.getCustomerName())
                .paymentCompleted(order.isPaymentCompleted())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .vnpTxnRef(order.getVnpTxnRef())
                .createdAt(order.getCreatedAt())
                .cancelReason(order.getCancelReason())
                .refundStatus(order.getRefundStatus())
                .items(itemResponses)
                .build();
    }
}
