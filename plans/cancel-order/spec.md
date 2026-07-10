# Spec: Cancel Order

**Date:** 2026-07-09
**Status:** Ready

---

## Problem Statement
Customers currently have no way to cancel an order after placing it. This feature allows customers to self-cancel orders before they are shipped, automatically restocks inventory, and provides a workflow for admins to manually process refunds for online payments (VNPAY).

---

## User Stories

<!-- P1 = MVP (must ship), P2 = nice-to-have, P3 = future/out-of-scope -->

- **[P1]** As a customer, I want to cancel my order (if not yet shipped) so that I can stop an unwanted purchase.
  Accepted when: Customer can cancel orders in PENDING, CONFIRMED, or PROCESSING state. Customer cannot cancel SHIPPED or DELIVERED orders.

- **[P1]** As a customer, I want to input my reason for cancellation so that the store knows why I cancelled.
  Accepted when: A required free-text input field is provided during the cancellation process.

- **[P1]** As a customer who paid via VNPAY, I want to be informed about the manual refund process so that I know how I will get my money back.
  Accepted when: A confirmation dialog displays "Admin sẽ liên hệ hoàn tiền qua số điện thoại mà khách hàng đã đặt hàng" and requires agreement before cancelling.

- **[P1]** As an admin, I want to easily identify cancelled VNPAY orders that need a refund so that I can process them manually.
  Accepted when: The system tracks `refundStatus` for orders and sets it to PENDING for cancelled VNPAY orders.

- **[P1]** As an inventory manager, I want stock levels to automatically increase when an order is cancelled so that inventory remains accurate.
  Accepted when: Cancelling an order automatically adds the item quantities back to the database.

---

## Functional Requirements

<!-- Number each. Be specific. "User can upload file <= 5MB (jpg/png/pdf)" not "user can upload files" -->

1. FR-01: System must allow cancellation for orders with status `PENDING`, `CONFIRMED`, `PROCESSING`.
2. FR-02: System must reject cancellation for orders with status `SHIPPED`, `DELIVERED`, `CANCELLED`, `RETURN_REQUESTED`, `RETURNED`, `REFUNDED`.
3. FR-03: System must record a mandatory cancellation reason provided by the customer as a text string.
4. FR-04: On the frontend, if the order was paid via VNPAY, a warning dialog must display: "Admin sẽ liên hệ hoàn tiền qua số điện thoại mà khách hàng đã đặt hàng". The user must confirm this dialog to proceed.
5. FR-05: When an order is successfully cancelled, its status changes to `CANCELLED`.
6. FR-06: If the cancelled order was paid via VNPAY, a new field `refundStatus` on the Order must be set to `PENDING`.
7. FR-07: Upon successful cancellation, the quantity of each product in the order must be added back to the product's available stock.

---

## Non-Functional Requirements

<!-- Use numbers, not adjectives. "p95 latency < 500ms" not "fast" -->

- Data Integrity: Inventory restoration must be handled in the same database transaction as the order status update to prevent discrepancies.

---

## Success Criteria

<!-- Measurable outcomes. Each must be independently verifiable. -->

- [ ] Cancellation API: Returns 200 OK when cancelling an eligible order.
- [ ] Cancellation API: Returns 400 Bad Request when attempting to cancel a SHIPPED order.
- [ ] Inventory DB: Stock level is verified to increase by the exact cancelled amount.

---

## Out of Scope

- Automated refund integration with VNPAY payment gateway (Admin will do this manually/offline).

---

## Assumptions

- Orders can have payment methods like "VNPAY" and "COD".
- The `refundStatus` is a new field that will be added to the `Order` entity.
