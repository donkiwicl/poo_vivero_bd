-- ============================================================
-- Vivero Hojas del Sur · datos de ejemplo
-- ============================================================
SET NAMES utf8mb4;
USE vivero;

INSERT INTO proveedor (nombre, ciudad) VALUES
    ('Viveros del Maipo',   'Santiago'),
    ('Flora Sur',           'Temuco'),
    ('Verde Costa',         'Valparaíso');

INSERT INTO planta (tipo, nombre, altura_cm, cantidad, volumen_maceta_litros, proveedor_id) VALUES
    ('INTERIOR', 'Monstera',        80, 15, 6.00, 1),
    ('INTERIOR', 'Ficus Lyrata',   120,  8, 8.50, 1),
    ('INTERIOR', 'Potus',           40, 30, 2.00, 3),
    ('INTERIOR', 'Cactus Estrella', 15, 50, 0.50, 2);

INSERT INTO planta (tipo, nombre, altura_cm, cantidad, especie, resistente_heladas, lista_despacho, proveedor_id) VALUES
    ('EXTERIOR', 'Lavanda',        50, 40, 'Lavandula angustifolia', TRUE,  FALSE, 2),
    ('EXTERIOR', 'Hortensia',      70, 12, 'Hydrangea macrophylla',  FALSE, FALSE, 3),
    ('EXTERIOR', 'Roble Pellín',  200,  5, 'Nothofagus obliqua',     TRUE,  TRUE,  2),
    ('EXTERIOR', 'Rosa Trepadora',150, 20, 'Rosa sp.',               FALSE, FALSE, 1);
