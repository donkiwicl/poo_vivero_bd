<#
.SYNOPSIS  Ejecuta un paso del caso (2 a 7). Equivalente de ejecutar-paso.sh.
.EXAMPLE   .\scripts\ejecutar-paso.ps1 5
#>
param([Parameter(Mandatory = $true)][ValidateRange(1, 7)][int]$Paso)
$ErrorActionPreference = "Stop"
$raiz = Split-Path -Parent $PSScriptRoot

if ($Paso -eq 1) {
    Write-Host "El paso 1 es solo SQL: abre paso-01-sql\consultas.sql en tu cliente (Workbench, DataGrip...) y ejecuta un bloque a la vez."
    exit 0
}
$carpeta = Get-ChildItem -Path $raiz -Directory -Filter ("paso-0{0}-*" -f $Paso) | Select-Object -First 1
if (-not $carpeta) { Write-Error "No existe el paso $Paso" }

Push-Location $carpeta.FullName
try {
    if ($Paso -eq 7) { mvn javafx:run } else { mvn -q compile exec:java }
    if ($LASTEXITCODE -ne 0) { throw "Maven terminó con error (código $LASTEXITCODE)" }
} finally { Pop-Location }
