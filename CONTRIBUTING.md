# Contributing

## Package Structure

The project follows a feature-based package organization:

```
com.marlon.portalusuario/
  core/
    theme/          Colors, typography, theme definitions
    components/     Shared composables used across features
    di/             Hilt DI modules (AppModule, ServiceModule)
    network/        Network-related utilities
    database/       Room database, DAOs, entities
    exceptions/     Custom exception types
    util/           Shared utilities and extensions
  feature/
    splash/         Splash screen
    intro/          Onboarding intro
    main/           Main activity with drawer navigation
    mobileservices/ Mobile services (balance, plans, bonuses)
    settings/       App settings
    permissions/    Runtime permission requests
    une/            UNE electricity service
    notifications/  Firebase push notifications
    profile/        User profile
    trafficbubble/  Floating traffic bubble overlay
    telephony/      USSD-based features (SMS, Voz, PlanAmigos, etc.)
    logviewer/      Error log viewer
    widgets/        App widgets
```

## How to Migrate

1. Identify the feature your code belongs to.
2. Move the files to the corresponding `feature/<name>/` package.
3. Update imports and package declarations.
4. Keep shared code in `core/`.

## Code Style

- Kotlin conventions per `.editorconfig`.
- Run `./gradlew ktlintCheck detekt` before committing.
