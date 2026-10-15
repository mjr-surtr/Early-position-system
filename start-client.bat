@echo off
chcp 936 >nul
title Early Potions - Minecraft Forge 1.20.1
cd /d "%~dp0"

if not exist "gradlew.bat" (
    echo [错误] 未找到 gradlew.bat，请把本脚本放在项目根目录（与 gradlew.bat 同一层）。
    echo.
    pause
    exit /b 1
)

set TASK=runClient
if /i "%~1"=="server" set TASK=runServer

echo ============================================================
echo   Early Potions - 启动 %TASK%
echo   Forge 1.20.1 / earlypotions
echo ============================================================
echo   首次运行需要下载依赖，请耐心等待；窗口会一直保持打开。
echo.

call ".\gradlew.bat" %TASK% --console=plain

if errorlevel 1 (
    echo.
    echo [错误] 启动失败，退出码 %errorlevel%。
    echo        若为 0xC0000005 原生崩溃，请确认 run\config\fml.toml 中 earlyWindowControl = false。
    echo.
    pause
)
