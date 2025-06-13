-- # --- !Ups
CREATE TABLE kategori (
                          id SERIAL PRIMARY KEY,
                          nama VARCHAR(255) NOT NULL,
                          is_delete BOOLEAN NOT NULL DEFAULT FALSE
);
ALTER TABLE produk DROP COLUMN kategori;
ALTER TABLE produk ADD COLUMN kategori_id INT;
ALTER TABLE produk ADD CONSTRAINT fk_kategori
    FOREIGN KEY (kategori_id) REFERENCES kategori(id);

-- # --- !Downs
ALTER TABLE produk DROP CONSTRAINT IF EXISTS fk_kategori;
ALTER TABLE produk DROP COLUMN IF EXISTS kategori_id;
ALTER TABLE produk ADD COLUMN kategori VARCHAR(255);
DROP TABLE IF EXISTS kategori;