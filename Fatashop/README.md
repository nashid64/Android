# Fatashop

A simple single-vendor e-commerce Android app where customers can browse and buy a variety of products. Built natively with Kotlin and powered by Firebase.

> The merchant (vendor) app is maintained as a separate project.

## Features

- Browse products from a single vendor
- User registration and login with Firebase Authentication
- User account information stored in Firebase
- Input validation on forms (email format, password strength, required fields)

## Tech Stack

| Layer    | Technology              |
|----------|-------------------------|
| Language | Kotlin                  |
| Platform | Android (native)        |
| Backend  | Firebase                |
| Auth     | Firebase Authentication |
| Build    | Gradle (Kotlin DSL)     |

## Project Structure

```
Fatashop/
├── app/
│   └── src/main/
│       ├── java/com/example/fatashop/   # Activities, adapters, models
│       └── res/                         # Layouts, drawables, values
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

## Related Projects

- Merchant app: _coming soon_

## Author

[nashid64](https://github.com/nashid64)
