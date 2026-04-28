-- Add column as nullable
ALTER TABLE stores
    ADD COLUMN delivery_fee NUMERIC(10, 2);

-- Set default values for existing rows
UPDATE stores
SET delivery_fee = 0.0
WHERE delivery_fee IS NULL;

-- Make column NOT NULL
ALTER TABLE stores
    ALTER COLUMN delivery_fee SET NOT NULL;