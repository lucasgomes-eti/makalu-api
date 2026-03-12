-- Add column as nullable
ALTER TABLE stores
    ADD COLUMN location geometry(Point, 4326);

-- Set default location (0 longitude, 0 latitude) for existing rows
UPDATE stores
SET location = ST_SetSRID(ST_MakePoint(0.0, 0.0), 4326)
WHERE location IS NULL;

-- Make column NOT NULL
ALTER TABLE stores
    ALTER COLUMN location SET NOT NULL;

-- Optional but recommended: spatial index
CREATE INDEX idx_stores_location
    ON stores
        USING GIST (location);
