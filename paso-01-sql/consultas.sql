-- ============================================================
-- Paso 1 · Nivelación SQL con el Vivero Hojas del Sur
-- Requiere haber ejecutado scripts/crear-bd.sh
-- Abre este archivo en PhpStorm/DataGrip/Workbench y ejecuta cada bloque por separado.
-- ============================================================
USE vivero;

-- ---------- 1. SELECT y WHERE: leer y filtrar ----------
SELECT * FROM planta;
SELECT nombre, altura_cm, cantidad FROM planta WHERE tipo = 'INTERIOR';
SELECT nombre FROM planta WHERE altura_cm > 100 AND cantidad < 20;
SELECT nombre FROM planta WHERE nombre LIKE 'R%';              -- empieza con R
SELECT nombre FROM planta WHERE tipo = 'EXTERIOR' AND resistente_heladas IS TRUE;

-- ---------- 2. ORDER BY: ordenar ----------
SELECT nombre, altura_cm FROM planta ORDER BY altura_cm DESC;
SELECT tipo, nombre, cantidad FROM planta ORDER BY tipo, cantidad DESC;

-- ---------- 3. GROUP BY y HAVING: agrupar y resumir ----------
SELECT tipo, COUNT(*) AS variedades, SUM(cantidad) AS unidades, AVG(altura_cm) AS altura_promedio
FROM planta
GROUP BY tipo;

-- HAVING filtra GRUPOS (WHERE filtra FILAS, antes de agrupar)
SELECT proveedor_id, SUM(cantidad) AS unidades
FROM planta
GROUP BY proveedor_id
HAVING SUM(cantidad) > 50;

-- ---------- 4. JOIN: combinar tablas por la clave foránea ----------
SELECT p.nombre AS planta, pr.nombre AS proveedor, pr.ciudad
FROM planta p
INNER JOIN proveedor pr ON pr.id = p.proveedor_id
ORDER BY pr.nombre, p.nombre;

-- LEFT JOIN: incluye proveedores aunque no tengan plantas
SELECT pr.nombre AS proveedor, COUNT(p.id) AS plantas
FROM proveedor pr
LEFT JOIN planta p ON p.proveedor_id = pr.id
GROUP BY pr.id, pr.nombre;

-- ---------- 5. INSERT, UPDATE, DELETE: el resto del CRUD ----------
INSERT INTO planta (tipo, nombre, altura_cm, cantidad, volumen_maceta_litros, proveedor_id)
VALUES ('INTERIOR', 'Helecho', 35, 25, 3.00, 3);

UPDATE planta SET cantidad = cantidad - 5 WHERE nombre = 'Helecho';   -- ¡siempre con WHERE!
SELECT * FROM planta WHERE nombre = 'Helecho';

DELETE FROM planta WHERE nombre = 'Helecho';                          -- ¡siempre con WHERE!

-- ---------- 6. Errores comunes (descomenta de a uno para verlos) ----------
-- UPDATE planta SET cantidad = 0;                                   -- sin WHERE: modifica TODAS las filas
-- INSERT INTO planta (tipo, nombre, altura_cm, cantidad) VALUES ('INTERIOR', 'Fuera de rango', 999, 1);  -- viola CHECK altura
-- INSERT INTO planta (tipo, nombre, altura_cm, cantidad, proveedor_id) VALUES ('INTERIOR','Huérfana',10,1,99); -- viola FOREIGN KEY
-- DELETE FROM proveedor WHERE id = 1;                               -- hay plantas que dependen de él
