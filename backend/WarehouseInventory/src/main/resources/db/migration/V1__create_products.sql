CREATE TABLE products
(
    id                  UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    sku                 VARCHAR(100)             NOT NULL UNIQUE,
    name                VARCHAR(255)             NOT NULL,
    description         TEXT,

    weight_value        NUMERIC(10, 3),
    weight_unit         VARCHAR(10),

    dim_width           NUMERIC(10, 3),
    dim_height          NUMERIC(10, 3),
    dim_depth           NUMERIC(10, 3),
    dim_unit            VARCHAR(5),

    category            VARCHAR(50)              NOT NULL,
    storage_requirement VARCHAR(50)              NOT NULL,

    min_stock_level     NUMERIC(12, 3)           NOT NULL DEFAULT 0,
    reorder_point       NUMERIC(12, 3)           NOT NULL DEFAULT 0,
    active              BOOLEAN                  NOT NULL DEFAULT TRUE,

    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100)
);