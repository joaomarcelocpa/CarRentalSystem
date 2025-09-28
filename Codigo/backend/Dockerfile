# Multi-stage build para otimizar o tamanho da imagem
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de dependências primeiro (para cache do Docker)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Baixar dependências (isso será cacheado se o pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Construir a aplicação
RUN mvn clean package -DskipTests

# Stage de produção
FROM eclipse-temurin:17-jre-alpine

# Instalar dependências necessárias
RUN apk add --no-cache \
    curl

# Criar usuário não-root para segurança
RUN addgroup -g 1001 appuser && adduser -D -u 1001 -G appuser appuser

# Definir diretório de trabalho
WORKDIR /app

# Copiar o JAR construído do stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Mudar ownership para o usuário não-root
RUN chown -R appuser:appuser /app

# Mudar para usuário não-root
USER appuser

# Expor porta
EXPOSE 8080

# Configurar variáveis de ambiente
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/test/public || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
