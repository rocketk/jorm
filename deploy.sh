mvn clean install
mvn source:jar -Dmaven.test.skip=true
mvn deploy -Dmaven.test.skip=true