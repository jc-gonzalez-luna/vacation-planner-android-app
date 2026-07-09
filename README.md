# Vacation Planner Android App

A modern Android application built with Kotlin, Jetpack Compose, Room, and MVVM architecture.  
Developed as part of the WGU Software Engineering Capstone (D424).  
The app allows users to plan vacations, store trip details locally, and interact with a clean, responsive UI.

## Features

- Create, edit, and delete vacation plans
- Offline storage using Room database
- Jetpack Compose UI with modern Android design patterns
- MVVM architecture with ViewModel separation
- Destination search and trip detail views
- Local persistence for trip lists
- Lightweight, fast, and built with current Android best practices

## Tech Stack

| Category | Technology |
|---------|------------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM + ViewModel |
| Local Storage | Room Database |
| IDE | Android Studio |
| Build System | Gradle |

## Project Structure

app/
├── androidTest/
├── main/
│   ├── manifests/
│   ├── kotlin+java/
│   │   └── com.example.d308vacationplanner/
│   │       ├── dao/
│   │       ├── database/
│   │       ├── entities/
│   │       ├── repository/
│   │       ├── ui/
│   │       │   ├── alerts/
│   │       │   ├── components/
│   │       │   ├── navigation/
│   │       │   ├── screens/
│   │       │   ├── theme/
│   │       │   ├── utils/
│   │       │   └── viewmodel/
│   │       └── MainActivity.kt

## Getting Started

### Clone the repository

git clone https://github.com/jc-gonzalez-luna/vacation-planner-android-app.git

### Open in Android Studio

1. Open Android Studio
2. Select "Open" and choose the project folder
3. Allow Gradle to sync
4. Build and run on an emulator or physical device


## Testing

- Unit tests for ViewModels
- DAO tests for Room database
- Optional UI tests for Compose components

## License

MIT License. You are free to use, modify, and distribute this project.

## Acknowledgments

This project was developed as part of the WGU Software Engineering Capstone (D424), demonstrating modern Android development practices including Compose, MVVM, and offline-first architecture.

## Portfolio

This project is featured on my portfolio:  
https://jc-gonzalez-luna.github.io/portfolio




