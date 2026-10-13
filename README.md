# Rastreador de Gastos

Aplicación Android en Kotlin con Material Design 3. Incluye dashboard, historial, registro validado, reportes por categoría, configuración de presupuesto y un explorador dual de Perfil, Fotos, Video, Web y Botones.

## Abrir y compilar

1. Abre esta carpeta desde Android Studio.
2. Usa JDK 17 y permite que Gradle sincronice el proyecto.
3. Instala Android SDK Platform 35 y Build Tools desde el SDK Manager.
4. Ejecuta la configuración `app` en un emulador o dispositivo con Android 8.0 o superior.

Los gastos y el presupuesto se guardan localmente en `SharedPreferences` como JSON. No se requiere servidor ni base de datos externa. El reproductor de video usa un recurso MP4 de demostración remoto y requiere Internet.
