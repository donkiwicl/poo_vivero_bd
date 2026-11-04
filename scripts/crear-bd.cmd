@echo off
rem Atajo para ejecutar crear-bd.ps1 con doble clic o desde cmd.exe, sin cambiar la politica de scripts.
rem Uso:  scripts\crear-bd.cmd            (o con parametros:  scripts\crear-bd.cmd -Clave otra)
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0crear-bd.ps1" %*
