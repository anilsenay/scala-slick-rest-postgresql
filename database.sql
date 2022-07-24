/* TABLES */
CREATE TABLE IF NOT EXISTS "users" (
                                       id SERIAL UNIQUE primary key,
                                       name VARCHAR NOT NULL,
                                       surname VARCHAR NOT NULL,
                                       phone VARCHAR,
                                       email VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS "user_password" (
                                       user_id BIGINT UNIQUE primary key,
                                       password VARCHAR NOT NULL,
                                       salt VARCHAR NOT NULL,
                                       CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id) ON delete cascade
);

CREATE TABLE IF NOT EXISTS "address" (
                                         id SERIAL UNIQUE primary key,
                                         title VARCHAR NOT NULL,
                                         city VARCHAR NOT NULL,
                                         region VARCHAR NOT NULL,
                                         zipcode VARCHAR NOT NULL,
                                         full_address VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS "user_address" (
                                              address_id BIGINT,
                                              user_id BIGINT,
                                              CONSTRAINT fk_address FOREIGN KEY(address_id) REFERENCES address(id) ON delete cascade,
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id),
    PRIMARY KEY(address_id, user_id)
    );

CREATE TABLE IF NOT EXISTS "brand" (
                                       id SERIAL UNIQUE primary key,
                                       name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS "category" (
                                          id SERIAL UNIQUE primary key,
                                          category_name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS "product" (
    id SERIAL UNIQUE primary key,
    brand_id BIGINT,
    category_id BIGINT,
    product_name VARCHAR NOT NULL,
    information VARCHAR,
    cover_photo_index smallint DEFAULT 0,
    price DECIMAL(12,2),
    sale_price DECIMAL(12,2),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_brand FOREIGN KEY(brand_id) REFERENCES brand(id),
    CONSTRAINT fk_category FOREIGN KEY(category_id) REFERENCES category(id)
    );

CREATE TABLE IF NOT EXISTS "product_photo" (
                                               id SERIAL UNIQUE primary key,
                                               product_id BIGINT,
                                               url varchar NOT NULL,
                                               CONSTRAINT fk_product FOREIGN KEY(product_id) REFERENCES product(id) ON delete cascade
    );

CREATE TABLE IF NOT EXISTS "product_size" (
                                              id SERIAL UNIQUE,
                                              product_id BIGINT,
                                              size varchar NOT NULL,
                                              CONSTRAINT fk_product FOREIGN KEY(product_id) REFERENCES product(id) ON delete cascade
                                            PRIMARY KEY(product_id, size)
    );

CREATE TABLE IF NOT EXISTS "orders" (
                                        id SERIAL UNIQUE primary key,
                                        user_id BIGINT,
                                        address_id BIGINT,
                                        total_price decimal(12,2) NOT NULL,
    order_status smallint DEFAULT 0,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id),
    CONSTRAINT fk_address FOREIGN KEY(address_id) REFERENCES address(id),
    CONSTRAINT fk_status FOREIGN KEY(order_status) REFERENCES order_status(id)
    );

CREATE TABLE IF NOT EXISTS "order_product" (
                                               order_id BIGINT,
                                               product_id BIGINT,
                                               quantity smallint DEFAULT 1 NOT NULL,
                                               size varchar,
                                               price decimal(12,2) NOT NULL,
                                               CONSTRAINT fk_product FOREIGN KEY(product_id) REFERENCES product(id),
    CONSTRAINT fk_order FOREIGN KEY(order_id) REFERENCES orders(id) ON delete cascade,
    PRIMARY KEY(order_id, product_id)
    );

CREATE TABLE IF NOT EXISTS "order_status" (
                                              id SERIAL UNIQUE primary key,
                                              status VARCHAR NOT NULL
);

/* DATA */
INSERT INTO users(name, surname, phone, email)
VALUES ('anil', 'senay', '34848383923', 'anil@senay.com');
INSERT INTO users(name, surname, phone, email) VALUES ('john', 'doe', '3424124121', 'johndoe@gmail.com');

INSERT INTO address(title, city, region, zipcode, full_address)
VALUES ('home', 'Istanbul', 'Turkey', '34567', 'Maltepe Istanbul');

INSERT INTO user_address(user_id, address_id)
VALUES ((SELECT id from users WHERE name='anil'), (SELECT id from address WHERE title='home'));

INSERT INTO brand(name) VALUES ('nike');
INSERT INTO brand(name) VALUES ('adidas');

INSERT INTO category(category_name) VALUES ('T-Shirt');
INSERT INTO category(category_name) VALUES ('Jeans');
INSERT INTO category(category_name) VALUES ('Shoes');

INSERT INTO product(product_name, brand_id, category_id, cover_photo_index, price, sale_price, information)
VALUES ('Nike AirForce 1', (SELECT id from brand WHERE name='nike'), (SELECT id FROM category WHERE category_name='Shoes'), 1, 2099, 1999, 'Nike Airforce 1 Yellow');

INSERT INTO product_photo(url, product_id) VALUES ('http://imageurl.com/image1.png', (SELECT id FROM product LIMIT 1));
INSERT INTO product_photo(url, product_id) VALUES ('http://imageurl.com/image2.png', (SELECT id FROM product LIMIT 1));
INSERT INTO product_photo(url, product_id) VALUES ('http://imageurl.com/image3.png', (SELECT id FROM product LIMIT 1));

INSERT INTO orders(address_id, user_id, total_price) VALUES ((SELECT id FROM address LIMIT 1), (SELECT id from users WHERE name='anil'), 1999);
INSERT INTO order_product(order_id, product_id, quantity) VALUES ((SELECT id FROM orders LIMIT 1), (SELECT id FROM product LIMIT 1), 1);

Insert Into order_status (status) VALUES ('Preparing');
Insert Into order_status (status) VALUES ('Cancelled');
Insert Into order_status (status) VALUES ('Shipped');
Insert Into order_status (status) VALUES ('Completed');