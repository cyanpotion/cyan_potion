call mvn clean site -Dmaven.javadoc.skip=false
cd ./src/
cd ./base/
call mvn clean site -Dmaven.javadoc.skip=false
cd ../
cd ./coordinate
call mvn clean site -Dmaven.javadoc.skip=false
cd ../
cd ./rpg_module
call mvn clean site -Dmaven.javadoc.skip=false
cd ../
cd ./demo
call mvn clean site -Dmaven.javadoc.skip=false
pause