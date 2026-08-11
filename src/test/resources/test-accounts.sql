INSERT INTO accounts (
    id, customer_id, iban, balance, created_at, updated_at
)
VALUES
(
    '732ec5a0-94e2-4c3d-8c19-2a4629120d10',
    '732ec5a0-94e2-4c3d-8c19-2a4629120d07',
    'NB1234567891234567891234567913246579',
    100.00,
    NOW(),
    NOW()
),
(
    '732ec5a0-94e2-4c3d-8c19-2a4629120d11',
    '732ec5a0-94e2-4c3d-8c19-2a4629120d07',
    'NB1234567891234567891234567913246580',
    100.00,
    NOW(),
    NOW()
);