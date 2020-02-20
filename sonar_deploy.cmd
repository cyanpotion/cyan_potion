call mvn clean deploy -Dmaven.javadoc.skip=false -Possrh
call mvn sonar:sonar ^
-Dsonar.scm.exclusions.disabled=true ^
-Dsonar.projectKey=cyanpotion_cyan_potion ^
-Dsonar.organization=cyanpotion ^
-Dsonar.host.url=https://sonarcloud.io ^
-Dsonar.log.level=DEBUG ^
-Dsonar.language=java ^
-Dsonar.java.source=1.8 ^
-Dsonar.sourceEncoding=UTF-8 ^
-Dsonar.java.coveragePlugin=jacoco ^
-Dsonar.login=133240d1ee4abeaa8b7ba1ef36cab1a2b789ddcb

cd ./src/
cd ./base/
call mvn clean deploy -Dmaven.javadoc.skip=false -Possrh
call mvn sonar:sonar ^
-Dsonar.scm.exclusions.disabled=true ^
-Dsonar.sources=src/main/,target/generated-sources/ ^
-Dsonar.tests=src/test/,target/generated-test-sources/ ^
-Dsonar.exclusions=src/main/lombok/** ^
-Dsonar.inclusions=src/main/java/**,target/generated-sources/delombok/** ^
-Dsonar.test.exclusions=src/test/lombok/** ^
-Dsonar.test.inclusions=src/test/java/**,target/generated-test-sources/delombok/** ^
-Dsonar.projectKey=cyan_potion_base ^
-Dsonar.organization=cyanpotion ^
-Dsonar.host.url=https://sonarcloud.io ^
-Dsonar.log.level=DEBUG ^
-Dsonar.language=java ^
-Dsonar.java.source=1.8 ^
-Dsonar.sourceEncoding=UTF-8 ^
-Dsonar.java.binaries=target/classes/ ^
-Dsonar.java.test.binaries=target/test-classes/ ^
-Dsonar.java.coveragePlugin=jacoco ^
-Dsonar.jacoco.reportPaths=target/jacoco.exec ^
-Dsonar.junit.reportPaths=target/surefire-reports/ ^
-Dsonar.login=553972c4c1b0147c484f07f8741d76b24d51415c

cd ../
cd ./coordinate
call mvn clean deploy -Dmaven.javadoc.skip=false -Possrh
call mvn sonar:sonar ^
-Dsonar.scm.exclusions.disabled=true ^
-Dsonar.sources=src/main/,target/generated-sources/ ^
-Dsonar.tests=src/test/,target/generated-test-sources/ ^
-Dsonar.exclusions=src/main/lombok/** ^
-Dsonar.inclusions=src/main/java/**,target/generated-sources/delombok/** ^
-Dsonar.test.exclusions=src/test/lombok/** ^
-Dsonar.test.inclusions=src/test/java/**,target/generated-test-sources/delombok/** ^
-Dsonar.projectKey=cyan_potion_coordinate ^
-Dsonar.organization=cyanpotion ^
-Dsonar.host.url=https://sonarcloud.io ^
-Dsonar.log.level=DEBUG ^
-Dsonar.language=java ^
-Dsonar.java.source=1.8 ^
-Dsonar.sourceEncoding=UTF-8 ^
-Dsonar.java.binaries=target/classes/ ^
-Dsonar.java.test.binaries=target/test-classes/ ^
-Dsonar.java.coveragePlugin=jacoco ^
-Dsonar.jacoco.reportPaths=target/jacoco.exec ^
-Dsonar.junit.reportPaths=target/surefire-reports/ ^
-Dsonar.login=3b4953bb762221ad019f1a4d2cfd37b0830e326f

cd ../
cd ./rpg_module
call mvn clean deploy -Dmaven.javadoc.skip=false -Possrh
call mvn sonar:sonar ^
-Dsonar.scm.exclusions.disabled=true ^
-Dsonar.sources=src/main/,target/generated-sources/ ^
-Dsonar.tests=src/test/,target/generated-test-sources/ ^
-Dsonar.exclusions=src/main/lombok/** ^
-Dsonar.inclusions=src/main/java/**,target/generated-sources/delombok/** ^
-Dsonar.test.exclusions=src/test/lombok/** ^
-Dsonar.test.inclusions=src/test/java/**,target/generated-test-sources/delombok/** ^
-Dsonar.projectKey=cyan_potion_rpg_module ^
-Dsonar.organization=cyanpotion ^
-Dsonar.host.url=https://sonarcloud.io ^
-Dsonar.log.level=DEBUG ^
-Dsonar.language=java ^
-Dsonar.java.source=1.8 ^
-Dsonar.sourceEncoding=UTF-8 ^
-Dsonar.java.binaries=target/classes/ ^
-Dsonar.java.test.binaries=target/test-classes/ ^
-Dsonar.java.coveragePlugin=jacoco ^
-Dsonar.jacoco.reportPaths=target/jacoco.exec ^
-Dsonar.junit.reportPaths=target/surefire-reports/ ^
-Dsonar.login=e50335ce1fed53dfa9ad4ce70995957336cf8e7e

cd ../
cd ./demo
call mvn clean deploy -Dmaven.javadoc.skip=false -Possrh

cd ../
cd ../
build.cmd
pause
