<#
.SYNOPSIS
  Crea (o recrea desde cero) la base de datos "vivero" con sus datos de ejemplo. Equivalente de crear-bd.sh para Windows.
.EXAMPLE
  .\scripts\crear-bd.ps1
  .\scripts\crear-bd.ps1 -Clave otra -Usuario root -Servidor 127.0.0.1 -Puerto 3306
.NOTES
  Requiere el cliente "mysql" en el PATH (viene con MySQL Server / MySQL Shell / MariaDB).
  Si no está en el PATH, pasa la ruta con -MysqlExe "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
#>
param(
    [string]$Servidor = "127.0.0.1",
    [int]$Puerto = 3306,
    [string]$Usuario = "root",
    [string]$Clave = "1234",
    [string]$MysqlExe = "mysql"
)
$ErrorActionPreference = "Stop"
$raiz = Split-Path -Parent $PSScriptRoot

if (-not (Get-Command $MysqlExe -ErrorAction SilentlyContinue)) {
    Write-Error "No se encontró '$MysqlExe'. Agrega la carpeta bin de MySQL al PATH o usa -MysqlExe con la ruta completa."
}

function Invocar-Mysql([string[]]$extra) {
    & $MysqlExe --default-character-set=utf8mb4 -h $Servidor -P $Puerto -u $Usuario "-p$Clave" @extra
    if ($LASTEXITCODE -ne 0) { throw "mysql terminó con error (código $LASTEXITCODE)" }
}

foreach ($archivo in @("sql/01_schema.sql", "sql/02_datos.sql")) {
    Write-Host ">> $archivo"
    # "source" lee el archivo directamente: evita problemas de codificación al redirigir con PowerShell
    $ruta = (Join-Path $raiz $archivo).Replace("\", "/")
    Invocar-Mysql @("-e", "source $ruta")
}

Write-Host "Base de datos 'vivero' lista:"
Invocar-Mysql @("-e", "SELECT COUNT(*) AS plantas FROM vivero.planta; SELECT COUNT(*) AS proveedores FROM vivero.proveedor;")
