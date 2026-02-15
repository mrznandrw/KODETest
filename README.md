# Тестовое KODE

Приложение со списком работников с сортировкой и поиском. 

Выполнено не в качестве тестового, а для собственной практики из-за наличия подробного
[задания](https://github.com/appKODE/trainee-test-android)
и [дизайна](https://www.figma.com/design/GRRKONipVClULsfdCAuVs1/KODE-Trainee-DEV-Весна%6024?node-id=11-14413&p=f).
## Демонстрация работы

![dark_theme](https://github.com/mrznandrw/KODETest/blob/main/docs/gif/dark_theme.gif?raw=true)
![light_theme](https://github.com/mrznandrw/KODETest/blob/main/docs/gif/light_theme.gif?raw=true)

## Функционал
- Темная тема
- Pull-to-refresh
- Поиск
- Сортировка

## Стэк
- Kotlin
- Jetpack Compose
- Retrofit
- Coil
- Dagger 2
- Navigation 3
- Coroutines + Flow
- Single Activity
- MVVM + Clean Architecture
- Unidirectional data flow (UDF)

## Архитектура
Проект следует Clean Architecture с разделением на слои:
- Presentation (UI + ViewModel)
- Domain (Use Cases)
- Data (Repository + DataSource)

## Установка
Скачайте и установите APK файл со страницы [релизов](https://github.com/mrznandrw/KODETest/releases)

## Сборка
1. Клонируйте репозиторий:
```
git clone https://github.com/mrznandrw/KODETest.git
```
2. Откройте проект в Android Studio Panda или новее
3. Соберите проект