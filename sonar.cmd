call mvn clean deploy -Dmaven.javadoc.skip=false
call mvn sonar:sonar -Dsonar.projectKey=cyanpotion_cyan_potion -Dsonar.organization=cyanpotion -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=133240d1ee4abeaa8b7ba1ef36cab1a2b789ddcb
cd ./src/
cd ./base/
call mvn clean deploy -Dmaven.javadoc.skip=false
call mvn sonar:sonar -Dsonar.projectKey=cyan_potion_base -Dsonar.organization=cyanpotion -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=553972c4c1b0147c484f07f8741d76b24d51415c
cd ../
cd ./coordinate
call mvn clean deploy -Dmaven.javadoc.skip=false
call mvn sonar:sonar -Dsonar.projectKey=cyan_potion_coordinate -Dsonar.organization=cyanpotion -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=3b4953bb762221ad019f1a4d2cfd37b0830e326f
cd ../
cd ./rpg_module
call mvn clean deploy -Dmaven.javadoc.skip=false
call mvn sonar:sonar -Dsonar.projectKey=cyan_potion_rpg_module -Dsonar.organization=cyanpotion -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=e50335ce1fed53dfa9ad4ce70995957336cf8e7e
cd ../
cd ./demo
call mvn clean deploy -Dmaven.javadoc.skip=false
pause