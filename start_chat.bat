@echo off
echo Starting Chat Server...
start javaw ChatServer
timeout /t 2 >nul

echo Starting Chat Client...
start javaw ChatClientGUI

echo Chat system started.
pause
