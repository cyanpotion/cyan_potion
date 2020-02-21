call mvn clean site -Dmaven.javadoc.skip=false -Pauto_clean_delombok -Pdelombok
cd ./src/
cd ./base/
call mvn clean site -Dmaven.javadoc.skip=false -Pauto_clean_delombok -Pdelombok
cd ../
cd ./coordinate
call mvn clean site -Dmaven.javadoc.skip=false -Pauto_clean_delombok -Pdelombok
cd ../
cd ./rpg_module
call mvn clean site -Dmaven.javadoc.skip=false -Pauto_clean_delombok -Pdelombok
cd ../
cd ./demo
call mvn clean site -Dmaven.javadoc.skip=false -Pauto_clean_delombok -Pdelombok
pause
