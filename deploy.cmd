call mvn clean deploy -Dmaven.javadoc.skip=false -Possrh -Pauto_clean_delombok -Pdelombok
cd ./src/
cd ./base/
call mvn clean deploy -Dmaven.javadoc.skip=false -Possrh -Pauto_clean_delombok -Pdelombok
cd ../
cd ./coordinate
call mvn clean deploy -Dmaven.javadoc.skip=false -Possrh -Pauto_clean_delombok -Pdelombok
cd ../
cd ./rpg_module
call mvn clean deploy -Dmaven.javadoc.skip=false -Possrh -Pauto_clean_delombok -Pdelombok
cd ../
cd ./demo
call mvn clean deploy -Dmaven.javadoc.skip=false -Possrh -Pauto_clean_delombok -Pdelombok
pause
