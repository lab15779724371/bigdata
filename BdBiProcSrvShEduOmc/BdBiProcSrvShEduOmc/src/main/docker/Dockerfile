FROM docker.tfitsoft.com/base/centos7:jdk8
ADD BdBiProcSrvShEduOmc.jar BdBiProcSrvShEduOmc.jar
ARG sentry_porperties
ENV JAVA_OPTS="-server -Xmx1g -Xms1g -Xmn256m -XX:SurvivorRatio=4 -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m -XX:+UseParallelGC -XX:ParallelGCThreads=4 -XX:+UseParallelOldGC -XX:+UseAdaptiveSizePolicy -XX:+PrintGCDetails -XX:+PrintTenuringDistribution -XX:+PrintGCTimeStamps -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/logs/gateway -Xloggc:/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=10M"
ENV SENTRY_DSN=https://43c3fe06d48c4072ae40eebd2914c658@sentry.sunshinelunch.com/24?$sentry_porperties
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /BdBiProcSrvShEduOmc.jar" ]
VOLUME ["/opt/logs/BdBiProcSrvShEduOmc"]
ARG port




