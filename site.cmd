call mvn clean site -Dmaven.javadoc.skip=false -Pauto_clean_delombok
cd ./src/
cd ./base/
call mvn clean site -Dmaven.javadoc.skip=false -Pauto_clean_delombok
cd ../
cd ./coordinate
call mvn clean site -Dmaven.javadoc.skip=false -Pauto_clean_delombok
cd ../
cd ./rpg_module
call mvn clean site -Dmaven.javadoc.skip=false -Pauto_clean_delombok
cd ../
cd ./demo
call mvn clean site -Dmaven.javadoc.skip=false -Pauto_clean_delombok
pause
