@echo off
cd /d %~dp0
if not exist bin mkdir bin
gcc license_validator.c -O2 -o bin\license_validator.exe
