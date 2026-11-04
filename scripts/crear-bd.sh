#!/usr/bin/env bash
# Crea (o recrea desde cero) la base de datos "vivero" con sus datos de ejemplo.
# Uso:   scripts/crear-bd.sh                 (usuario root / clave 1234 en localhost)
#        DB_USER=root DB_PASS=otra DB_HOST=127.0.0.1 scripts/crear-bd.sh
set -euo pipefail
cd "$(dirname "$0")/.."

DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASS="${DB_PASS:-1234}"

for archivo in sql/01_schema.sql sql/02_datos.sql; do
  echo ">> $archivo"
  mysql --default-character-set=utf8mb4 -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" < "$archivo"
done
echo "Base de datos 'vivero' lista:"
mysql --default-character-set=utf8mb4 -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" -e "SELECT COUNT(*) AS plantas FROM vivero.planta; SELECT COUNT(*) AS proveedores FROM vivero.proveedor;"
