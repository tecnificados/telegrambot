# Telegram Bot

Prueba de concepto de un bot de Telegram desarrollado en Java 21. El bot responde a mensajes y ubicaciones, consulta datos meteorológicos y de mercado, y puede enviar imágenes de cartas y mapas.

## Funcionalidades

### Comandos, mensajes y botones

Al recibir `/start`, el bot muestra una bienvenida con la lista de comandos y funciones. Acepta también `/start` con el nombre del bot o con un parámetro de inicio. El bot responde a otros mensajes de texto con un saludo y repite el mensaje recibido. Usa español cuando el idioma de Telegram del usuario es `es` y, en los demás casos, responde en inglés.

`/start` puede enviarse en cualquier idioma; las funciones siguientes solo se activan cuando el idioma de Telegram del usuario es español.

Para usuarios cuyo idioma de Telegram es español, reconoce estos textos (sin distinguir mayúsculas y minúsculas):

| Texto | Acción |
| --- | --- |
| `carta` o `card` | Envía una carta aleatoria de la baraja. |
| `mano` o `hand` | Baraja las cartas y envía una imagen con cinco cartas. |
| `ibex` o `ibex 35` | Consulta Supabase y muestra el último valor disponible por empresa, agrupado por la primera letra del nombre. |
| `prueba` o `test` | Muestra dos botones para indicar si hay condensación. Al elegir uno, responde y elimina los botones del mensaje. |

### Ubicaciones

Cuando recibe una ubicación compartida, el bot responde con las coordenadas en formato GeoJSON y trata de enviar una imagen del mapa. La imagen se genera con Selenium y Chrome en modo headless; el mapa usa Leaflet y teselas de OpenStreetMap.

### Mensaje meteorológico diario

Al arrancar, el bot programa un mensaje diario a las 12:00 en la zona `Europe/Madrid`, dirigido a los identificadores configurados en `bot.daily.chat.ids`. Consulta Open-Meteo para las coordenadas de Madrid y muestra máximas, mínimas, punto de rocío y una indicación de condensación probable para los dos primeros días de la respuesta.

## Requisitos

- Java 21 o superior.
- Maven.
- Para generar imágenes de mapas: Chrome/Chromium compatible con Selenium y acceso a Internet para cargar Leaflet y las teselas del mapa.
- Un bot de Telegram y credenciales de Supabase si se quieren usar las funciones de IBEX.

## Configuración

La aplicación carga `bot.properties` desde el classpath (`src/main/resources/bot.properties` en este proyecto). Debe contener estas propiedades:

```properties
bot.username=nombre_del_bot
bot.token=token_de_telegram
bot.supabase.api=clave_de_supabase
bot.supabase.url=https://proyecto.supabase.co
bot.daily.chat.ids=123456789,987654321
```

`bot.daily.chat.ids` acepta uno o varios identificadores de chat separados por comas. Se eliminan los espacios alrededor de cada identificador. Si la propiedad está vacía o falta, el bot no programa el envío diario.

No publiques ni compartas el token o la clave de Supabase. El archivo de propiedades se incluye como recurso al empaquetar la aplicación; para despliegues, evita incorporar secretos al JAR y adapta la carga de configuración para usar variables de entorno o un gestor de secretos.

La consulta de mercado espera una tabla/recurso REST de Supabase llamado `ibexdata` con los campos `name`, `value` y `datet`, accesible mediante la clave configurada. El código transforma `datet` en `date` para procesar los datos.

## Compilar y ejecutar

Desde la raíz del proyecto:

```bash
mvn clean package
```

Maven genera un JAR ejecutable con dependencias en `target/telegrambot-0.0.1-SNAPSHOT-jar-with-dependencies.jar`.

```bash
java -jar target/telegrambot-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

El bot usa *long polling* para recibir actualizaciones. Al iniciar, registra el bot y activa el programador del mensaje diario. El proceso debe permanecer en ejecución para atender mensajes y enviar ese aviso.

## Estructura del código

```text
src/main/java/telegrambot/
├── Main.java                 # Arranque del bot y tarea diaria
├── MySimpleBot.java           # Actualizaciones y respuestas de Telegram
├── BotConfig.java             # Lectura de bot.properties
├── DewPoint.java              # Consulta y cálculo meteorológico
├── SupabaseRead.java          # Lectura de datos del IBEX
├── Utils.java                 # Mapas, imágenes, agrupación y utilidades
├── bean/IbexBean.java         # Modelo de datos de empresas del IBEX
├── cards/                     # Cartas y carga de la hoja de sprites
└── model/openmeteo/           # Modelos de la respuesta meteorológica

src/main/resources/
├── bot.properties             # Configuración (contiene secretos; no publicar)
├── cards3.png                 # Hoja de sprites usada para la baraja
└── logback.xml                # Registro en consola y en logs/app.log
```

También hay otras imágenes de cartas (`cards.png` y `cards2.png`) en recursos; el cargador actual utiliza `cards3.png`.

## Registro y límites conocidos

Logback escribe en consola y en `logs/app.log`, con rotación diaria y retención de diez días. Revisa qué información se registra antes de desplegar el bot: la implementación actual registra la respuesta completa de Supabase y `BotConfig.toString()` incluye el token de Telegram.

El horario del aviso diario se calcula al iniciar y después se repite cada 24 horas; no se recalcula diariamente alrededor de los cambios de horario de verano. Las funciones de mapa dependen de Chrome, Selenium y recursos web externos.
