# Newton's Quest

Un juego educativo sobre física y las leyes de Newton.

## Estructura del Proyecto

El proyecto ha sido refactorizado siguiendo el principio de responsabilidad única (SRP), dividiendo el monolítico `GameController.java` en componentes más pequeños y específicos.

### Estructura de Componentes

- **GameController.java**: Actúa como coordinador entre los diferentes componentes.
- **Componentes**:
  - `RenderManager.java`: Gestiona toda la renderización del juego.
  - `InputManager.java`: Maneja la entrada del usuario.
  - `AudioManager.java`: Gestiona la reproducción de audio.
  - `LevelManager.java`: Gestiona el sistema de niveles y fórmulas.
  - `AppleManager.java`: Gestiona la generación y control de manzanas.
  - `ResourceManager.java`: Carga y gestiona recursos (imágenes).
  - `ScoreManager.java`: Gestiona puntuación y vidas.

### Comunicación entre Componentes

La comunicación entre componentes se realiza mediante callbacks, lo que permite un acoplamiento bajo entre los diferentes módulos. Cada componente expone métodos para configurar sus callbacks y el `GameController` se encarga de conectarlos adecuadamente.

## Requisitos

- Java 24 o superior
- JavaFX 24
- MySQL Connector/J 8.3.0 o compatible

## Configuración y Ejecución

### Configuración Rápida

1. Ejecuta `setup.bat` para configurar la estructura de directorios y copiar las dependencias necesarias.
2. Ejecuta `run_game.bat` para compilar y ejecutar el juego.

### Desde NetBeans

1. Abrir el proyecto en NetBeans
2. Asegurarse de que las dependencias de JavaFX están correctamente configuradas
3. Asegurarse de que MySQL Connector/J está en el classpath
4. Ejecutar el proyecto desde NetBeans (clase principal: `Main._Main`)

### Desde línea de comandos

1. Compilar el proyecto:

```
javac -cp "src;ruta-a-mysql-connector.jar" src/Main/_Main.java src/Controlador/*.java src/Controlador/componentes/*.java -d build/classes --module-path "ruta-a-javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml,javafx.media
```

2. Ejecutar el proyecto:

```
java --module-path "ruta-a-javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml,javafx.media --enable-native-access=javafx.graphics --add-opens=java.base/sun.misc=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED --add-exports javafx.base/com.sun.javafx=ALL-UNNAMED -cp "build/classes;ruta-a-mysql-connector.jar" Main._Main
```

## Base de Datos

El juego utiliza una base de datos MySQL para almacenar información de usuarios y puntuaciones. Asegúrate de que la base de datos está configurada correctamente en la clase `ConexionDB.java`.

## Mejoras Realizadas en la Refactorización

1. **Separación de responsabilidades**: Cada componente tiene una única responsabilidad.
2. **Código más mantenible**: La estructura modular facilita el mantenimiento y la extensión.
3. **Menor acoplamiento**: Los componentes se comunican a través de interfaces bien definidas.
4. **Mayor cohesión**: Cada clase tiene un propósito claro y cohesivo.
5. **Mejor testabilidad**: Los componentes pueden probarse de forma aislada.

## Estructura de Archivos

```
newtons-quest/
├── src/
│   ├── Controlador/
│   │   ├── GameController.java
│   │   ├── LoginController.java
│   │   ├── MainController.java
│   │   ├── MapController.java
│   │   └── componentes/
│   │       ├── RenderManager.java
│   │       ├── InputManager.java
│   │       ├── AudioManager.java
│   │       ├── LevelManager.java
│   │       ├── AppleManager.java
│   │       ├── ResourceManager.java
│   │       └── ScoreManager.java
│   ├── Modelo/
│   │   ├── Apple.java
│   │   ├── ConexionDB.java
│   │   └── Player.java
│   ├── Vista/
│   │   └── recursos/
│   └── Main/
│       └── _Main.java
├── build/
└── nbproject/
```

## Créditos

Desarrollado como parte del curso de Algoritmos y Estructuras de Datos.
