CREATE TABLE transactions (
  id UUID PRIMARY KEY,
  account_id UUID REFERENCES accounts (id) NOT NULL,
  type VARCHAR(20) NOT NULL,
  amount NUMERIC(10, 2) NOT NULL,
  created_at TIMESTAMP NOT NULL
);