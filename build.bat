@echo off
echo Building VEX CLIENT for Minecraft 1.21.11...
gradle build
if errorlevel 1 (
  echo Build failed. Check the error above.
  pause
  exit /b 1
)
echo.
echo Build complete. Check build\libs for the mod JAR.
pause
