# Documentación de la Refactorización de Newton's Quest

## Objetivo de la Refactorización

El objetivo principal de esta refactorización fue dividir el monolítico `GameController.java` (con más de 1000 líneas) en componentes más pequeños y específicos, siguiendo el principio de responsabilidad única (SRP) de SOLID.

## Enfoque

Se utilizó un enfoque de "componentes especializados" donde cada componente:

1. Tiene una responsabilidad única y bien definida
2. Expone una interfaz clara para comunicarse con otros componentes
3. Mantiene su propio estado interno
4. Se comunica con otros componentes mediante callbacks

## Componentes Creados

### 1. RenderManager

**Responsabilidad**: Gestionar todas las operaciones de renderizado.

**Funcionalidades principales**:

- Renderizar el fondo del juego
- Renderizar el jugador
- Renderizar las manzanas
- Renderizar la interfaz de usuario (puntuación, vidas, etc.)
- Renderizar mensajes especiales (pausa, game over, etc.)

### 2. InputManager

**Responsabilidad**: Manejar toda la entrada del usuario.

**Funcionalidades principales**:

- Gestionar eventos de teclado para mover al jugador
- Gestionar eventos de teclado para pausar/reanudar el juego
- Gestionar eventos de teclado para mostrar/ocultar la interfaz
- Proporcionar callbacks para notificar de acciones del usuario

### 3. AudioManager

**Responsabilidad**: Gestionar la reproducción de audio.

**Funcionalidades principales**:

- Reproducir música de fondo
- Reproducir efectos de sonido (recoger manzana, perder vida, etc.)
- Controlar el volumen
- Pausar/reanudar audio

### 4. LevelManager

**Responsabilidad**: Gestionar el sistema de niveles y fórmulas.

**Funcionalidades principales**:

- Controlar la progresión de niveles
- Gestionar el desbloqueo de fórmulas físicas
- Proporcionar información sobre el nivel actual
- Notificar cuando se desbloquea una nueva fórmula

### 5. AppleManager

**Responsabilidad**: Gestionar la generación y control de manzanas.

**Funcionalidades principales**:

- Generar manzanas aleatorias
- Controlar la física de caída de las manzanas
- Detectar colisiones con el jugador o el suelo
- Gestionar diferentes tipos de manzanas

### 6. ResourceManager

**Responsabilidad**: Cargar y gestionar recursos.

**Funcionalidades principales**:

- Cargar todas las imágenes del juego
- Proporcionar acceso a los recursos
- Gestionar la carga eficiente de recursos

### 7. ScoreManager

**Responsabilidad**: Gestionar puntuación y vidas.

**Funcionalidades principales**:

- Mantener la puntuación del jugador
- Gestionar las vidas del jugador
- Notificar cuando el jugador pierde una vida
- Notificar cuando el juego termina (game over)

## Sistema de Comunicación entre Componentes

Para mantener un bajo acoplamiento entre componentes, se implementó un sistema de comunicación basado en callbacks:

1. Cada componente define los callbacks que necesita para comunicarse con otros componentes
2. El `GameController` actúa como "coordinador" conectando los callbacks correctamente
3. Los componentes pueden comunicarse sin conocerse directamente

Ejemplo de configuración de callbacks en el `GameController`:

```java
// Configurar callbacks del InputManager
inputManager.setCallbacks(
    this::togglePause,                 // onPauseToggle
    this::returnToMainMenu,            // onReturnToMenu
    this::toggleUI,                    // onUIToggle
    this::showFormulaDetails           // onFormulaDetails
);

// Configurar callbacks del ScoreManager
scoreManager.setCallbacks(
    this::setGameOver,                 // onGameOver
    score -> levelManager.checkLevelProgress(score) // onScoreChange
);
```

## Beneficios de la Refactorización

1. **Código más mantenible**: Cada componente es más pequeño y fácil de entender.
2. **Mejor organización**: La estructura del código refleja mejor las responsabilidades.
3. **Facilidad para añadir nuevas características**: Se pueden extender componentes específicos sin afectar a otros.
4. **Mejor testabilidad**: Los componentes pueden probarse de forma aislada.
5. **Mayor legibilidad**: El código es más fácil de leer y entender.
6. **Reducción de errores**: El ámbito limitado de cada componente reduce la probabilidad de errores.
7. **Desarrollo en paralelo**: Diferentes desarrolladores pueden trabajar en diferentes componentes simultáneamente.

## Resultados Cuantitativos

- **Antes de la refactorización**: `GameController.java` tenía más de 1000 líneas.
- **Después de la refactorización**:
  - `GameController.java`: ~370 líneas
  - 7 clases de componentes con un promedio de ~200 líneas cada una
  - Mayor cohesión en cada clase
  - Menor acoplamiento entre clases

## Lecciones Aprendidas

1. La importancia de aplicar el principio de responsabilidad única desde el inicio
2. La utilidad de los sistemas de comunicación basados en callbacks para mantener bajo acoplamiento
3. La necesidad de planificar adecuadamente la estructura de componentes antes de comenzar la refactorización
4. La importancia de las pruebas después de cada refactorización para garantizar que todo sigue funcionando

## Conclusión

La refactorización ha sido exitosa, transformando un controlador monolítico en un sistema de componentes bien estructurado que sigue los principios SOLID. El código es ahora más mantenible, extensible y comprensible, lo que facilitará el desarrollo futuro del juego.
