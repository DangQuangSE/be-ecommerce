# Brainstorm: Cancel Order Feature

**Date:** 2026-07-09

## Ideas Explored
- **Cancellation conditions**: Should we allow cancellation at all stages? (Narrowed to PENDING, CONFIRMED, PROCESSING).
- **Refund Management**: How does admin track VNPAY refunds? Evaluated adding a specific `refundStatus` field vs combining order status and payment status.
- **Inventory Restoration**: Do we restock automatically or manually upon cancellation?
- **Frontend UI**: Should cancellation reasons be predefined options or free text?

## User's Direction
- User wants to allow order cancellation only if it has not been SHIPPED (so PENDING, CONFIRMED, PROCESSING).
- Customer must enter a reason in a blank text field.
- For VNPAY payments, the customer will see a warning message: "Admin sẽ liên hệ hoàn tiền qua số điện thoại mà khách hàng đã đặt hàng". If agreed, the order is cancelled and a success message is shown.
- Admin handles the refund manually. System tracks this via a new `refundStatus` field (set to PENDING for VNPAY).
- System automatically restores inventory for the cancelled order.

## Open Questions
None.

## Risks
- Manual refund process could be forgotten by admin if they don't regularly check the `refundStatus`.
- Race condition: User cancels order exactly when admin changes status to SHIPPED. (Requires transaction/lock handling on backend).
