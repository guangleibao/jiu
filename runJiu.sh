java -classpath <PATH_TO_BMC_JAVA_SDK_JAR>/oracle-bmc-java-sdk-full-1.2.13.jar:\
<PATH_TO_LOCAL_REPO>/jiu/target/jiu-1.0-SNAPSHOT-jar-with-dependencies.jar:\
<PATH_TO_LOCAL_REPO>/jiu/target/classes \
-Dfile.encoding=UTF-8 -Dorg.slf4j.simpleLogger.defaultLogLevel=error \
bglutil.jiu.Jiu ${*}
