call mvn clean deploy -Dmaven.javadoc.skip=false -Possrh
cd ./src/
cd ./base/
call mvn clean deploy -Dmaven.javadoc.skip=false -Possrh
cd ../
cd ./coordinate
call mvn clean deploy -Dmaven.javadoc.skip=false -Possrh
cd ../
cd ./rpg_module
call mvn clean deploy -Dmaven.javadoc.skip=false -Possrh
cd ../
cd ./demo
call mvn clean deploy -Dmaven.javadoc.skip=false -Possrh
pause
