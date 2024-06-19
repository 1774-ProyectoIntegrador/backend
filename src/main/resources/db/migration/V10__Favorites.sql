CREATE TABLE favorites
(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_id BIGINT                                  NOT NULL,
    CONSTRAINT pk_favorites PRIMARY KEY (id)
);

CREATE TABLE products_favorites
(
    favorite_id BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    CONSTRAINT pk_products_favorites PRIMARY KEY (favorite_id, product_id)
);

ALTER TABLE favorites
    ADD CONSTRAINT FK_FAVORITES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE products_favorites
    ADD CONSTRAINT fk_profav_on_product FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE products_favorites
    ADD CONSTRAINT fk_profav_on_product_favorite FOREIGN KEY (favorite_id) REFERENCES favorites (id);