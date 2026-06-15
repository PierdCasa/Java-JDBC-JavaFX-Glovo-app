USE glovo_db;

-- 1. Locations
INSERT INTO locations (x, y, address) VALUES 
(10.0, 15.0, 'Str. Victoriei 12'),
(20.0, 25.0, 'Bd. Unirii 45'),
(15.0, 20.0, 'Str. Floreasca 10');

-- 2. Wallets
INSERT INTO wallets (balance) VALUES 
(0.0),   -- Wallet for Restaurant 1 (La Italiano)
(0.0),   -- Wallet for Restaurant 2 (Burger House)
(500.0); -- Wallet for Customer (Maria)

-- 3. Users (Customer)
INSERT INTO users (role, first_name, second_name, phone, email, password, wallet_id, delivery_address, location_id, loyalty_points) VALUES 
('CUSTOMER', 'Maria', 'Dumitrescu', '0774567890', 'maria@email.com', 'maria123', 3, 'Str. Floreasca 10', 3, 0);

-- 4. Categories
INSERT INTO categories (name) VALUES 
('Pizza'),
('Burgeri'),
('Deserturi'),
('Bauturi');

-- 5. Ingredients
INSERT INTO ingredients (name, extra_price, locked) VALUES 
('Extra Branza', 4.0, FALSE),
('Jalapeno', 2.5, FALSE),
('Bacon', 5.0, FALSE);

-- 6. Restaurants
INSERT INTO restaurants (name, location, rating, wallet_id, location_coords_id) VALUES 
('La Italiano', 'Str. Victoriei 12', 4.8, 1, 1),
('Burger House', 'Bd. Unirii 45', 4.5, 2, 2);

-- 7. Products
INSERT INTO products (name, category_id, description, price) VALUES 
('Pizza Margherita', 1, 'Sos rosii, mozzarella', 28.5),
('Pizza Pepperoni', 1, 'Sos rosii, mozzarella, salam', 35.0),
('Classic Burger', 2, 'Vita, salata, rosii, sos', 32.0),
('Tiramisu', 3, 'Mascarpone, cafea', 22.0),
('Coca-Cola', 4, '330ml', 8.0);

-- 8. Restaurant Products
INSERT INTO restaurant_products (restaurant_id, product_id) VALUES 
(1, 1), (1, 2), (1, 4), (1, 5), -- La Italiano
(2, 3), (2, 5);                 -- Burger House

-- 9. Product Ingredients
INSERT INTO product_ingredients (product_id, ingredient_id, is_locked) VALUES 
(1, 1, FALSE), -- Margherita -> Extra Branza
(3, 2, FALSE), -- Burger -> Jalapeno
(3, 3, FALSE); -- Burger -> Bacon
