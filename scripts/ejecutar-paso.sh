#!/usr/bin/env bash
# Ejecuta un paso del caso.   Uso:  scripts/ejecutar-paso.sh 5      (pasos 2 a 7)
set -euo pipefail
cd "$(dirname "$0")/.."

n="${1:?Indica el número de paso (2 a 7). Ej: scripts/ejecutar-paso.sh 5}"
carpeta=$(ls -d paso-0"$n"-* 2>/dev/null | head -1 || true)
[ -n "$carpeta" ] || { echo "No existe el paso $n"; exit 1; }
[ "$n" != "1" ] || { echo "El paso 1 es solo SQL: mysql -uroot -p1234 -t < paso-01-sql/consultas.sql"; exit 0; }

cd "$carpeta"
if [ "$n" = "7" ]; then mvn javafx:run; else mvn -q compile exec:java; fi
