call sonar-scanner.bat -X
cd ./src/
cd ./base/
call sonar-scanner.bat -X
cd ../
cd ./coordinate
call sonar-scanner.bat -X
cd ../
cd ./rpg_module
call sonar-scanner.bat -X
pause
