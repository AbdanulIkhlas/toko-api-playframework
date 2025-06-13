-- # --- !Ups
CREATE TABLE produk (
                        id SERIAL PRIMARY KEY,
                        nama VARCHAR(255) NOT NULL,
                        kategori VARCHAR(255) NOT NULL,
                        harga DOUBLE PRECISION NOT NULL,
                        stok INT NOT NULL,
                        is_delete BOOLEAN NOT NULL DEFAULT FALSE
);
-- # --- !Downs
DROP TABLE IF EXISTS produk;