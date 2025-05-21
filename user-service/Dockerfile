# Etapa de construcción
FROM openjdk:17-slim as builder
  
  # Establecer el directorio de trabajo
WORKDIR /workspace/app
  
  # Copiar el archivo pom.xml y descargar las dependencias
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

    # Copiar el código fuente y compilar la aplicación
COPY src src
RUN ./mvnw package -DskipTests

  # Etapa de ejecución Crear la imagen al final
FROM openjdk:17-slim
VOLUME /tmp

#Copiar el archivo jar de la etapa de compilación
COPY --from=builder /workspace/app/target/*.jar app.jar

# exponer el puerto 8081
EXPOSE 8081

# Definir el punto de entrada para la aplicación
ENTRYPOINT ["java","-jar","/app.jar"]
