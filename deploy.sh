mvn clean install -Dmaven.test.skip=true
mvn source:jar -Dmaven.test.skip=true
mvn deploy -Dmaven.test.skip=true