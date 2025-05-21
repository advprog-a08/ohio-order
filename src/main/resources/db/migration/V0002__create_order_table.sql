CREATE TABLE orders (
    id         UUID NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    meja_id    UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_orders_on_meja FOREIGN KEY (meja_id) REFERENCES meja (id)
);

CREATE TABLE order_items (
    id             UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    menu_item_id   VARCHAR(255),
    menu_item_name VARCHAR(255),
    price          DOUBLE PRECISION NOT NULL,
    quantity       INTEGER NOT NULL,
    order_id       UUID,

    CONSTRAINT fk_order_items_on_order FOREIGN KEY (order_id) REFERENCES orders (id)
);

CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();