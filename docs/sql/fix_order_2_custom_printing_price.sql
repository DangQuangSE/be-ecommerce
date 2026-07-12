BEGIN;

DO $$
DECLARE
    target_order_id BIGINT := 2;
    text_unit_price NUMERIC(19, 2);
    image_unit_price NUMERIC(19, 2);
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM orders
        WHERE id = target_order_id
          AND status = 'PENDING'
          AND payment_method = 'COD'
          AND payment_completed = FALSE
    ) THEN
        RAISE EXCEPTION 'Order % is not an unpaid PENDING COD order', target_order_id;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM order_items AS other_item
        WHERE other_item.custom_design_id IN (
            SELECT target_item.custom_design_id
            FROM order_items AS target_item
            WHERE target_item.order_id = target_order_id
              AND target_item.custom_design_id IS NOT NULL
        )
          AND other_item.order_id <> target_order_id
    ) THEN
        RAISE EXCEPTION 'A custom design from order % is shared by another order', target_order_id;
    END IF;

    SELECT unit_price INTO STRICT text_unit_price
    FROM printing_price_configs
    WHERE type = 'TEXT';

    SELECT unit_price INTO STRICT image_unit_price
    FROM printing_price_configs
    WHERE type = 'IMAGE';

    UPDATE custom_designs AS design
    SET total_printing_price =
            design.num_text_lines * text_unit_price
            + design.num_images * image_unit_price
    WHERE design.id IN (
        SELECT order_item.custom_design_id
        FROM order_items AS order_item
        WHERE order_item.order_id = target_order_id
          AND order_item.custom_design_id IS NOT NULL
    );

    UPDATE orders AS customer_order
    SET total_amount = recalculated.subtotal - COALESCE(customer_order.discount_amount, 0)
    FROM (
        SELECT order_item.order_id,
               SUM(
                   order_item.price * order_item.quantity
                   + COALESCE(custom_design.total_printing_price, 0)
               ) AS subtotal
        FROM order_items AS order_item
        LEFT JOIN custom_designs AS custom_design
               ON custom_design.id = order_item.custom_design_id
        WHERE order_item.order_id = target_order_id
        GROUP BY order_item.order_id
    ) AS recalculated
    WHERE customer_order.id = recalculated.order_id
      AND customer_order.id = target_order_id;
END $$;

COMMIT;
