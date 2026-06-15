
CREATE DATABASE IF NOT EXISTS glovo_db;

USE glovo_db;

CREATE TABLE IF NOT EXISTS locations (
    location_id INT  AUTO_INCREMENT PRIMARY KEY,
    x  DOUBLE  NOT NULL,
    y  DOUBLE  NOT NULL,
    address VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS wallets (
    wallet_id INT  AUTO_INCREMENT PRIMARY KEY,
    balance  DOUBLE NOT NULL DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS users (
    user_id INT  AUTO_INCREMENT PRIMARY KEY,
    role ENUM('CUSTOMER', 'DELIVERY_MAN', 'ADMIN') NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    second_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20)  NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    wallet_id INT NOT NULL,

    -- Customer
    delivery_address VARCHAR(255) DEFAULT NULL,
    location_id INT DEFAULT NULL,
    loyalty_points INT DEFAULT 0,

    -- DeliveryMan
    license_plate VARCHAR(20) DEFAULT NULL,
    vehicle_type ENUM('BICYCLE', 'MOTORCYCLE', 'CAR') DEFAULT NULL,
    current_location_id INT DEFAULT NULL,
    available BOOLEAN DEFAULT TRUE,

    CONSTRAINT fk_users_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(wallet_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_users_location FOREIGN KEY (location_id) REFERENCES locations(location_id)
        ON DELETE SET NULL,
    CONSTRAINT fk_users_current_location FOREIGN KEY (current_location_id) REFERENCES locations(location_id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS restaurants (
    restaurant_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    location VARCHAR(255) NOT NULL,
    rating DOUBLE NOT NULL DEFAULT 0.0,
    wallet_id INT NOT NULL,
    location_coords_id INT DEFAULT NULL,

    CONSTRAINT fk_restaurants_wallet
        FOREIGN KEY (wallet_id) REFERENCES wallets(wallet_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_restaurants_location
        FOREIGN KEY (location_coords_id) REFERENCES locations(location_id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS grocery_stores (
    grocery_store_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    location VARCHAR(255) NOT NULL,
    rating DOUBLE NOT NULL DEFAULT 0.0,
    wallet_id INT NOT NULL,
    location_coords_id INT DEFAULT NULL,

    CONSTRAINT fk_grocery_stores_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(wallet_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_grocery_stores_location FOREIGN KEY (location_coords_id) REFERENCES locations(location_id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category_id INT DEFAULT NULL,
    description TEXT DEFAULT NULL,
    price DOUBLE NOT NULL,

    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(category_id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS ingredients (
    ingredient_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    extra_price DOUBLE NOT NULL DEFAULT 0.0,
    locked BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS product_ingredients (
    product_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (product_id, ingredient_id),

    CONSTRAINT fk_pi_product FOREIGN KEY (product_id) REFERENCES products(product_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_pi_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS restaurant_products (
    restaurant_id INT NOT NULL,
    product_id INT NOT NULL,

    PRIMARY KEY (restaurant_id, product_id),

    CONSTRAINT fk_rp_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(restaurant_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_rp_product FOREIGN KEY (product_id) REFERENCES products(product_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS grocery_store_products (
    grocery_store_id INT NOT NULL,
    product_id INT NOT NULL,

    PRIMARY KEY (grocery_store_id, product_id),

    CONSTRAINT fk_gsp_grocery_store FOREIGN KEY (grocery_store_id) REFERENCES grocery_stores(grocery_store_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_gsp_product FOREIGN KEY (product_id) REFERENCES products(product_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS promo_codes (
    promo_code_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_percent DOUBLE NOT NULL DEFAULT 0.0,
    free_delivery BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    max_uses INT NOT NULL DEFAULT 1,
    current_uses INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    delivery_man_id INT DEFAULT NULL,
    restaurant_id INT NOT NULL,
    order_price DOUBLE NOT NULL DEFAULT 0.0,
    delivery_fee DOUBLE NOT NULL DEFAULT 0.0,
    tip_amount DOUBLE NOT NULL DEFAULT 0.0,
    platform_commission DOUBLE NOT NULL DEFAULT 0.0,
    status ENUM('PENDING', 'ACCEPTED', 'PREPARING', 'PICKED_UP', 'ARRIVING', 'DELIVERED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    preparing_time INT NOT NULL DEFAULT 0,
    delivery_time INT NOT NULL DEFAULT 0,
    order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    promo_code_id INT DEFAULT NULL,

    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_orders_delivery_man FOREIGN KEY (delivery_man_id) REFERENCES users(user_id)
        ON DELETE SET NULL,
    CONSTRAINT fk_orders_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(restaurant_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_orders_promo_code FOREIGN KEY (promo_code_id) REFERENCES promo_codes(promo_code_id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DOUBLE NOT NULL DEFAULT 0.0,

    CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES orders(order_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_oi_product FOREIGN KEY (product_id) REFERENCES products(product_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS order_item_customizations (
    order_item_id INT NOT NULL,
    ingredient_id INT NOT NULL,

    PRIMARY KEY (order_item_id, ingredient_id),

    CONSTRAINT fk_oic_order_item FOREIGN KEY (order_item_id) REFERENCES order_items(order_item_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_oic_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    wallet_id INT NOT NULL,
    type ENUM('PAYMENT', 'REFUND', 'TIP', 'COMMISSION', 'DELIVERY_FEE', 'RESTAURANT_PAYOUT', 'LOYALTY_REDEEM', 'DEPOSIT') NOT NULL,
    amount DOUBLE NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    related_order_id INT DEFAULT NULL,

    CONSTRAINT fk_transactions_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(wallet_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_transactions_order FOREIGN KEY (related_order_id) REFERENCES orders(order_id)
        ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    restaurant_id INT DEFAULT NULL,
    grocery_store_id INT DEFAULT NULL,
    order_id INT DEFAULT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT DEFAULT NULL,
    date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reviews_customer FOREIGN KEY (customer_id) REFERENCES users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_reviews_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(restaurant_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_reviews_grocery_store FOREIGN KEY (grocery_store_id) REFERENCES grocery_stores(grocery_store_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES orders(order_id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS ratings (
    rating_id INT AUTO_INCREMENT PRIMARY KEY,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT DEFAULT NULL
);



-- CREATE INDEX idx_users_email ON users(email);
-- CREATE INDEX idx_users_role ON users(role);
-- CREATE INDEX idx_orders_customer ON orders(customer_id);
-- CREATE INDEX idx_orders_delivery ON orders(delivery_man_id);
-- CREATE INDEX idx_orders_restaurant ON orders(restaurant_id);
-- CREATE INDEX idx_orders_status ON orders(status);
-- CREATE INDEX idx_orders_date ON orders(order_date);
-- CREATE INDEX idx_transactions_wallet ON transactions(wallet_id);
-- CREATE INDEX idx_transactions_type ON transactions(type);
-- CREATE INDEX idx_reviews_restaurant ON reviews(restaurant_id);
-- CREATE INDEX idx_reviews_customer ON reviews(customer_id);


