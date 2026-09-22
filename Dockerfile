# ===== 阶段 1：构建（多模块 Maven 项目）=====
FROM maven:3.8-openjdk-8 AS build
ENV MAVEN_OPTS="-Xmx768m"
WORKDIR /app

# 使用阿里云 Maven 镜像，加速依赖下载
COPY settings.xml /root/.m2/settings.xml

# 复制全部模块源码
COPY pom.xml .
COPY ruoyi-admin/pom.xml ruoyi-admin/
COPY ruoyi-common/pom.xml ruoyi-common/
COPY ruoyi-framework/pom.xml ruoyi-framework/
COPY ruoyi-generator/pom.xml ruoyi-generator/
COPY ruoyi-quartz/pom.xml ruoyi-quartz/
COPY ruoyi-system/pom.xml ruoyi-system/
COPY ruoyi-admin/src ruoyi-admin/src
COPY ruoyi-common/src ruoyi-common/src
COPY ruoyi-framework/src ruoyi-framework/src
COPY ruoyi-generator/src ruoyi-generator/src
COPY ruoyi-quartz/src ruoyi-quartz/src
COPY ruoyi-system/src ruoyi-system/src

# 直接打包（跳过测试）
RUN mvn clean package -DskipTests -pl ruoyi-admin -am

# ===== 阶段 2：运行（精简 JRE）=====
FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=build /app/ruoyi-admin/target/*.jar app.jar
RUN mkdir -p /home/ruoyi/uploadPath
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=docker"]