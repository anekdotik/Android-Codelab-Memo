# Memo Reminders App

An Android application that empowers users to create **location-based reminders (memos)** and receive notifications when they enter a specified geographical area.

Built with modern Android development practices: **Clean Architecture**, **Kotlin**, **Hilt**, and persistent storage using **Room**.

---

## Features

- **Create Memos**  
  Easily add new memos with a title and description.

- **Location-Based Reminders**  
  Attach a specific location to a memo using an interactive map.

- **Geofencing Integration**  
  Leverages Google Play Services Geofencing API to detect when a user enters the defined reminder area.

- **Notification System**  
  Delivers timely notifications upon geofence entry — even when the app is in the background or closed.

- **Data Persistence**  
  Stores memos in a local SQLite database using Room.

- **Robust Permission Handling**  
  Comprehensive handling for `ACCESS_FINE_LOCATION`, `ACCESS_BACKGROUND_LOCATION`, and `POST_NOTIFICATIONS` to ensure a smooth user experience.

- **Boot-Time Geofence Re-registration**  
  Automatically re-registers all active geofences after device reboot, ensuring reminders are always active.

- **Modern Development Stack**  
  Utilizes Kotlin, Coroutines, Flow, and Hilt for a scalable, maintainable, and efficient codebase.

- **Gradle Kotlin DSL & Version Catalogs**  
  Modern Gradle configuration for improved build script readability and dependency management.

---

## Architecture

The project strictly adheres to **Clean Architecture** principles, promoting a clear separation of concerns, testability, and maintainability.

### Presentation Layer
- **UI**: Implemented using Android Fragments and View Binding
- **ViewModel**: Manages UI state and handles UI-related events using `StateFlow` and `SharedFlow`. Interacts with Use Cases.

### Domain Layer
- **Use Cases**: Encapsulate single-purpose business logic operations. They orchestrate the application's core features.
- **Domain Models**: Pure Kotlin data classes representing core entities, independent of any framework.
- **Repository Interfaces**: Define contracts for data operations, ensuring domain layer's independence from data sources.

### Data Layer
- **Repository Implementations**: Implement the repository interfaces defined in the domain layer. Coordinate data flow from various sources.
- **Local Data Source**: Uses Room for local database persistence.
- **Remote/External Data Source**: Integrates with Google Play Services (Location API for Geofencing and Maps SDK).
- **Mappers**: Convert DTOs from data sources into domain models and vice versa.

### Dependency Flow
```
Presentation → Domain ← Data
```

---

Technologies Used

| Category                   | Tools & Libraries                                      |
|----------------------------|--------------------------------------------------------|
| **Language**               | Kotlin                                                 |
| **Concurrency**            | Kotlin Coroutines & Flow                               |
| **Dependency Injection**   | Hilt                                                   |
| **UI Toolkit**             | Android Fragments, View Binding, Material Components   |
| **Persistence**            | Room Database                                          |
| **Navigation**             | Jetpack Navigation Component                           |
| **Location Services**      | Google Play Services Location (Geofencing) & Maps SDK  |
| **Build System**           | Gradle Kotlin DSL with Version Catalogs                |

---

## Demo


https://github.com/user-attachments/assets/68b1c5a2-d1b3-4e99-ad15-6335b0834fca

