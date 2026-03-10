-- Insert admin user (password: admin123)
INSERT INTO users (email, password, first_name, last_name, enabled) VALUES
('admin@shopmall.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', 'User', TRUE);

-- Insert admin role
INSERT INTO user_roles (user_id, role) VALUES
(1, 'ADMIN');

-- Insert categories
INSERT INTO categories (name, description) VALUES
('Electronics', 'Electronic devices and accessories'),
('Clothing', 'Fashion and clothing items'),
('Books', 'Books and publications'),
('Home & Garden', 'Home and garden products'),
('Sports', 'Sports and fitness equipment'),
('Toys', 'Toys and games');

-- Insert subcategories for Electronics
INSERT INTO categories (name, description, parent_id) VALUES
('Smartphones', 'Mobile phones and smartphones', 1),
('Laptops', 'Laptop computers', 1),
('Tablets', 'Tablet devices', 1),
('Audio', 'Audio equipment and headphones', 1);

-- Insert subcategories for Clothing
INSERT INTO categories (name, description, parent_id) VALUES
('Men', 'Men''s clothing', 2),
('Women', 'Women''s clothing', 2),
('Kids', 'Children''s clothing', 2);

-- Insert sample products
INSERT INTO products (name, description, price, stock_quantity, category_id) VALUES
('iPhone 15 Pro', 'Latest Apple iPhone with A17 chip, 128GB', 999.99, 50, 7),
('Samsung Galaxy S24', 'Flagship Samsung smartphone with AI features', 899.99, 40, 7),
('MacBook Pro 14"', 'Apple M3 chip, 16GB RAM, 512GB SSD', 1999.99, 25, 8),
('Dell XPS 15', 'Intel i9, 32GB RAM, 1TB SSD, 4K Display', 1799.99, 30, 8),
('iPad Pro 12.9"', 'M2 chip, 256GB, Wi-Fi + Cellular', 1099.99, 35, 9),
('Sony WH-1000XM5', 'Premium noise-canceling headphones', 399.99, 60, 10),
('Men''s Cotton T-Shirt', '100% cotton, comfortable fit', 29.99, 200, 11),
('Women''s Summer Dress', 'Lightweight and stylish summer dress', 59.99, 100, 12),
('Kids Running Shoes', 'Comfortable and durable running shoes', 44.99, 150, 13);

-- Set some products as featured
UPDATE products SET featured = TRUE WHERE id IN (1, 2, 3, 6);

-- Insert sample regular user (password: user123)
INSERT INTO users (email, password, first_name, last_name, phone, address) VALUES
('user@example.com', '$2a$10$V3/GYLxlGYhxlGYhxlGYuO9xYGxYGYxYGYxYGYxYGYxYGYxYGYxYGY', 'John', 'Doe', '555-1234', '123 Main St');

INSERT INTO user_roles (user_id, role) VALUES
(2, 'USER');

-- Insert sample reviews
INSERT INTO reviews (user_id, product_id, rating, comment, verified_purchase) VALUES
(2, 1, 5, 'Amazing phone! The camera quality is outstanding.', TRUE),
(2, 6, 4, 'Great headphones with excellent noise cancellation.', TRUE);
