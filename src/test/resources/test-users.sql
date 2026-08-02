DELETE FROM users;

INSERT INTO users (
    id, email, password, enabled, role, created_at, updated_at
)
VALUES
(
    gen_random_uuid(),
    'user@test.com',
    '$2a$10$mv/dicDl4BxAK2NlvZ01fem1B/PcUM8A3ZSlym2PW.v7xrjZiF3.e',
    true,
    'USER',
    NOW(),
    NOW()
),
(
    gen_random_uuid(),
    'admin@test.com',
    '$2a$10$mv/dicDl4BxAK2NlvZ01fem1B/PcUM8A3ZSlym2PW.v7xrjZiF3.e',
    true,
    'ADMIN',
    NOW(),
    NOW()
);