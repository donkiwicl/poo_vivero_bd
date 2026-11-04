@echo off
rem Uso:  scripts\ejecutar-paso.cmd 5      (pasos 2 a 7)
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0ejecutar-paso.ps1" %*
