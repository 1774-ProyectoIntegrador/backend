ALTER TABLE product_categories
    DROP CONSTRAINT fk6mvxwehy351e5srwl68j08dbp;

ALTER TABLE profiles
    DROP CONSTRAINT fkfhviemkseacl9tnevr0aaywbp;

ALTER TABLE user_roles
    DROP CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f;

ALTER TABLE users
    ADD role VARCHAR(255);

DROP TABLE addresses CASCADE;

DROP TABLE profiles CASCADE;

DROP TABLE user_roles CASCADE;

ALTER TABLE product_categories
    DROP COLUMN attachment_id;

ALTER TABLE products
    ALTER COLUMN description TYPE VARCHAR(800) USING (description::VARCHAR(800));