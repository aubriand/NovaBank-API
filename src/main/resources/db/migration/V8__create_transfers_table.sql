CREATE TABLE transfers (
  id UUID PRIMARY KEY,
  source_account_id UUID REFERENCES accounts (id) NOT NULL,
  destination_account_id UUID REFERENCES accounts (id) CHECK (source_account_id != destination_account_id) NOT NULL,
  amount NUMERIC(10,2) CHECK (amount > 0) NOT NULL,
  created_at TIMESTAMP NOT NULL
);
