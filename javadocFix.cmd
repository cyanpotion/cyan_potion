call mvn javadoc:fix
Y

cd ./src/
cd ./base/
call mvn javadoc:fix
Y

cd ../
cd ./coordinate
call mvn javadoc:fix
Y

cd ../
cd ./rpg_module
call mvn javadoc:fix
Y

cd ../
cd ./demo
call mvn javadoc:fix
Y

pause
