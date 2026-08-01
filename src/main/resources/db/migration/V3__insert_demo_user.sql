INSERT INTO users (
  id,
  email,
  password,
  enabled,
  created_at,
  updated_at
) VALUES (
  '550e8400-e29b-41d4-a716-446655440000', 
  'user@demo.fr', 
  '$2a$10$FA9YM2PqzF5iuGCsjHpiKOPQLK/HZ4KKitBN.rnlyQdbHAsBPgugi', 
  TRUE, 
  CURRENT_TIMESTAMP, 
  CURRENT_TIMESTAMP
);