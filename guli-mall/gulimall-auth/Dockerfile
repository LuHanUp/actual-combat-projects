FROM java:8
EXPOSE 13000

VOLUME /tmp
ADD target/*.jar  /app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=prod"]

# 其他的微服务的dockerfile按照这个文件修改即可