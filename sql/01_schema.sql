-- ============================================================
-- Vivero Hojas del Sur · esquema de la base de datos (MySQL 8 / MariaDB 10.5+)
-- Ejecutar con: scripts/crear-bd.sh   (recrea todo desde cero)
-- ============================================================
CREATE DATABASE IF NOT EXISTS vivero
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE vivero;

DROP TABLE IF EXISTS planta;
DROP TABLE IF EXISTS proveedor;

-- Tabla "padre": un proveedor abastece muchas plantas.
CREATE TABLE proveedor (
    id     INT          PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    ciudad VARCHAR(80)  NOT NULL
);

-- Una sola tabla guarda las dos subclases del modelo (PlantaInterior y PlantaExterior).
-- La columna "tipo" dice cuál es; las columnas propias de cada subclase pueden ser NULL.
CREATE TABLE planta (
    id                    INT           PRIMARY KEY AUTO_INCREMENT,
    tipo                  VARCHAR(10)   NOT NULL,
    nombre                VARCHAR(100)  NOT NULL,
    altura_cm             INT           NOT NULL,
    cantidad              INT           NOT NULL,
    volumen_maceta_litros DECIMAL(5,2)  NULL,                -- solo INTERIOR
    especie               VARCHAR(100)  NULL,                -- solo EXTERIOR
    resistente_heladas    BOOLEAN       NULL,                -- solo EXTERIOR
    lista_despacho        BOOLEAN       NOT NULL DEFAULT FALSE,
    proveedor_id          INT           NULL,                -- clave foránea (opcional)

    -- Las mismas reglas que valida el modelo Java (clase Planta)
    CONSTRAINT chk_planta_tipo     CHECK (tipo IN ('INTERIOR', 'EXTERIOR')),
    CONSTRAINT chk_planta_altura   CHECK (altura_cm BETWEEN 1 AND 300),
    CONSTRAINT chk_planta_cantidad CHECK (cantidad BETWEEN 1 AND 500),

    CONSTRAINT fk_planta_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor (id)
);
