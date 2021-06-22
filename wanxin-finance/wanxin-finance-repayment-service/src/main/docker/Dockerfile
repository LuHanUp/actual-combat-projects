FROM openjdk:8-jre-alpine

ENV VERSION 1.0-SNAPSHOT

RUN echo "http://mirrors.aliyun.com/alpine/v3.8/main" > /etc/apk/repositories \
    && echo "http://mirrors.aliyun.com/alpine/v3.8/community" >> /etc/apk/repositories \
    && apk update upgrade \
    && apk add --no-cache procps curl bash tzdata \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

ADD wanxin-finance-repayment-service-${VERSION}.jar wanxin-finance-repayment-service.jar
RUN bash -c 'touch /wanxin-finance-repayment-service.jar'

EXPOSE 53080

ENV JAVA_OPTS=""
ENTRYPOINT ["bash", "-c", "java $JAVA_OPTS -jar /wanxin-finance-repayment-service.jar"]
