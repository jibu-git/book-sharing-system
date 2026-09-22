# ===== 阶段 1：构建（多模块 Maven 项目）=====
FROM maven:3.8-openjdk-8 AS build
ENV MAVEN_OPTS="-Xmx768m"
WORKDIR /app

# 先只复制 pom.xml，利用缓存避免每次全量下载依赖
COPY pom.xml .
COPY ruoyi-admin/pom.xml ruoyi-admin/
COPY ruoyi-common/pom.xml ruoyi-common/
COPY ruoyi-framework/pom.xml ruoyi-framework/
COPY ruoyi-generator/pom.xml ruoyi-generator/
COPY ruoyi-quartz/pom.xml ruoyi-quartz/
COPY ruoyi-system/pom.xml ruoyi-system/
RUN mvn dependency:go-offline -B -pl ruoyi-admin -am

# 复制源码并打包
COPY ruoyi-admin/src ruoyi-admin/src
COPY ruoyi-common/src ruoyi-common/src
COPY ruoyi-framework/src ruoyi-framework/src
COPY ruoyi-generator/src ruoyi-generator/src
COPY ruoyi-quartz/src ruoyi-quartz/src
COPY ruoyi-system/src ruoyi-system/src
RUN mvn clean package -DskipTests -pl ruoyi-admin -am

# ===== 阶段 2：运行（精简 JRE）=====
FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=build /app/ruoyi-admin/target/*.jar app.jar
RUN mkdir -p /home/ruoyi/uploadPath
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=docker"]