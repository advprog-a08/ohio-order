CREATE TABLE checkout (
    id       UUID NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    state    VARCHAR(255),
    order_id UUID NOT NULL,

    CONSTRAINT uc_checkout_order UNIQUE (order_id),
    CONSTRAINT fk_checkout_on_order FOREIGN KEY (order_id) REFERENCES orders (id)
);