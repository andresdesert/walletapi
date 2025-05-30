@echo off
setlocal enabledelayedexpansion

:: Rutas
set LOGFILE=%USERPROFILE%\Desktop\Test-error.log
set TEMP_LOG=%TEMP%\test_output.log
del "%LOGFILE%" >nul 2>&1
del "%TEMP_LOG%" >nul 2>&1

echo ========================================
echo Ejecutando tests en perfil "test"...
echo Por favor espere...
echo ========================================

:: Verificar Maven
where mvn >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven no está instalado o no está en el PATH.
    exit /b 1
)

:: Ejecutar tests con resumen y guardar salida
mvn clean test -Dspring.profiles.active=test -Dsurefire.printSummary=true > "%TEMP_LOG%" 2>&1

:: Buscar errores importantes y guardar
findstr /R /C:"^\[ERROR\]" "%TEMP_LOG%" > "%LOGFILE%"

:: Mostrar resumen general
echo.
findstr /C:"BUILD FAILURE" "%TEMP_LOG%" >nul
if %errorlevel%==0 (
    echo ❌ Tests fallaron. Revisa el log: "%LOGFILE%"
    echo --------------------------------------------------
    type "%LOGFILE%"
    start "" "%LOGFILE%"
    exit /b 1
) else (
    echo ✅ Todos los tests pasaron correctamente.
    echo --------------------------------------------------
    findstr /C:"Tests run:" "%TEMP_LOG%"
    exit /b 0
)


Comandos:


mvn surefire-report:report

target/site/surefire-report.html

mvn clean test '-Dspring.profiles.active=test'
