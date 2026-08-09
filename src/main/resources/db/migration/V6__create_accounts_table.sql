CREATE TABLE accounts (
  id UUID PRIMARY KEY,
  customer_id UUID REFERENCES customers (id) NOT NULL,
  iban VARCHAR(255) UNIQUE NOT NULL,
  balance NUMERIC(10, 2) NOT NULL DEFAULT '0.00',
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);