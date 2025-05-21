CREATE TABLE meja (
    id         UUID         NOT NULL DEFAULT gen_random_uuid(),
    nomor_meja VARCHAR(255) NOT NULL,
    status     VARCHAR(255) NOT NULL,

    CONSTRAINT pk_meja PRIMARY KEY (id),
    CONSTRAINT uc_meja_nomormeja UNIQUE (nomor_meja)
);