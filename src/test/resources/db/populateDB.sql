
INSERT INTO users (username, password, email) VALUES
('userTest', '100', 'user1@example.com'),
('adminTest', '100', 'user2@example.com'),
('userTestWithEmptyCart', '100', 'user3@example.com');

INSERT INTO users_roles (user_id, role_id) VALUES
((SELECT id FROM users WHERE username = 'userTest'), (SELECT id FROM roles WHERE name = 'ROLE_USER')),
((SELECT id FROM users WHERE username = 'adminTest'), (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')),
((SELECT id FROM users WHERE username = 'userTestWithEmptyCart'), (SELECT id FROM roles WHERE name = 'ROLE_USER'));

INSERT INTO products (name, description, price, quantity) VALUES
('Product 1', 'Description 1', 5.0, 7.0),
('Product 2', 'Description 2', 5.0, 7.0);

INSERT INTO cart_items (product_id, quantity, user_id) VALUES
((SELECT id FROM products WHERE name = 'Product 1'), 7.0, (SELECT id FROM users WHERE username = 'adminTest')),
((SELECT id FROM products WHERE name = 'Product 2'), 7.0, (SELECT id FROM users WHERE username = 'userTest'));

INSERT INTO orders (user_id, created_at, status) VALUES
((SELECT id FROM users WHERE username = 'userTest'), NOW(), 1);

INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
((SELECT id FROM orders WHERE status = 1), (SELECT id FROM products WHERE name = 'Product 2'), 7.0, 5.0);

