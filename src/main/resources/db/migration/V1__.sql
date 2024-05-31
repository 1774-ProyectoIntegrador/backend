CREATE TABLE attachments
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    url        VARCHAR(255)                            NOT NULL,
    file_name  VARCHAR(255)                            NOT NULL,
    file_key   VARCHAR(255)                            NOT NULL,
    product_id BIGINT,
    CONSTRAINT pk_attachments PRIMARY KEY (id)
);

CREATE TABLE product_categories
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name           VARCHAR(255)                            NOT NULL,
    description    VARCHAR(255),
    slug           VARCHAR(255)                            NOT NULL,
    attachments_id BIGINT,
    CONSTRAINT pk_product_categories PRIMARY KEY (id)
);

CREATE TABLE product_features
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255)                            NOT NULL,
    description VARCHAR(255),
    icon        VARCHAR(255)                            NOT NULL,
    product_id  BIGINT                                  NOT NULL,
    CONSTRAINT pk_product_features PRIMARY KEY (id)
);

CREATE TABLE products
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255)                            NOT NULL,
    description VARCHAR(255)                            NOT NULL,
    stock       INTEGER                                 NOT NULL,
    price       DOUBLE PRECISION                        NOT NULL,
    rent_type   SMALLINT                                NOT NULL,
    category_id BIGINT,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL,
    roles   VARCHAR(255)
);

CREATE TABLE users
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    first_name VARCHAR(255)                            NOT NULL,
    last_name  VARCHAR(255)                            NOT NULL,
    email      VARCHAR(255)                            NOT NULL,
    password   VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE product_categories
    ADD CONSTRAINT uc_product_categories_attachments UNIQUE (attachments_id);

ALTER TABLE product_categories
    ADD CONSTRAINT uc_product_categories_name UNIQUE (name);

ALTER TABLE product_categories
    ADD CONSTRAINT uc_product_categories_slug UNIQUE (slug);

ALTER TABLE products
    ADD CONSTRAINT uc_products_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE attachments
    ADD CONSTRAINT FK_ATTACHMENTS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES product_categories (id);

ALTER TABLE product_categories
    ADD CONSTRAINT FK_PRODUCT_CATEGORIES_ON_ATTACHMENTS FOREIGN KEY (attachments_id) REFERENCES attachments (id);

ALTER TABLE product_features
    ADD CONSTRAINT FK_PRODUCT_FEATURES_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_on_user FOREIGN KEY (user_id) REFERENCES users (id);