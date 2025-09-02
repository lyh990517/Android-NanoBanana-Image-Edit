# NanoBanana - AI Image Editor

An Android app that uses Google's Gemini AI to transform and edit your images with creative prompts.
<br>
<img src="https://github.com/user-attachments/assets/fa7e4138-fc0c-48aa-b8cf-02e9756f6455" width="250"/>
<img src="https://github.com/user-attachments/assets/97fa5bf1-c46d-4e6a-8da4-1ff7b98d07b3" width="250"/>
<img src="https://github.com/user-attachments/assets/3b38d3fa-ca3e-4bd2-a431-954347e6f22d" width="250"/>

## Features

- **AI-Powered Image Editing**: Transform your photos using Google's Gemini 2.5 Flash model
- **Multiple Style Presets**: Choose from various creative transformation styles:
    - Korean historical transformation (Chosun Dynasty 1900s)
    - Collectible figure creation with packaging
    - Rock-paper-scissors game scenarios
    - Shopping at Costco (3D style)
- **Custom Prompts**: Enter your own text prompts for unique transformations
- **Multi-Image Support**: Select multiple images from your gallery
- **Save Results**: Save edited images directly to your device's gallery
- **Modern UI**: Beautiful Material Design 3 interface with Jetpack Compose

## Requirements

- Android 9.0 (API level 28) or higher
- Google AI API key (Gemini API)
- Internet connection for AI processing

## Setup

1. Clone this repository
2. Open the project in Android Studio
3. Get a Google AI API key:
    - Visit [Google AI Studio](https://ai.google.dev/)
    - Create a new API key
    - Copy the key for use in the app
4. Build and run the app

## Usage

1. **API Key Setup**:
    - Open the app and enter your Google AI API key in the settings section
    - Tap "Save" to store the key securely

2. **Select Images**:
    - Tap "Select Images" to choose photos from your gallery
    - You can select multiple images for transformation

3. **Choose Style**:
    - Swipe through the available style presets
    - Each style has a preview image and description

4. **Generate**:
    - Tap "Generate" to start the AI transformation
    - Wait for the processing to complete

5. **Save Result**:
    - Once generated, tap "Save" to store the result in your gallery
    - Use "Reset" to start over with new images

## Architecture

- **MVVM Pattern**: Clean separation of UI, business logic, and data
- **Jetpack Compose**: Modern declarative UI toolkit
- **Coroutines**: Asynchronous processing for smooth user experience
- **SharedPreferences**: Secure storage for API keys
- **OkHttp**: Efficient HTTP client for API communication

## Dependencies

- **Jetpack Compose**: UI framework
- **Material 3**: Design system
- **Kotlin Coroutines**: Async programming
- **OkHttp**: HTTP client
- **Firebase BOM**: Firebase integration
- **Kotlinx Serialization**: JSON handling

## Project Structure

```
app/src/main/java/com/yunho/nanobanana/
├── MainActivity.kt              # Main activity and UI composition
├── NanoBanana.kt               # Core business logic and state management
├── NanoBananaService.kt        # AI service and API communication
├── components/                 # Reusable UI components
│   ├── ApiKeySetting.kt       # API key configuration UI
│   ├── Generate.kt            # Generation button
│   ├── PickedImages.kt        # Selected images display
│   ├── Prompt.kt              # Text prompt input
│   ├── ResultImage.kt         # Generated image display
│   ├── Save.kt                # Save functionality
│   ├── SelectImages.kt        # Image selection UI
│   └── StylePicker.kt         # Style preset carousel
└── extension/                 # Utility extensions
```

## Build Information

- **Compile SDK**: 36
- **Min SDK**: 28
- **Target SDK**: 36
- **Version**: 1.0
- **Kotlin**: Latest stable
- **Java**: 11

## License

This project is for educational and personal use. Please ensure you comply with Google's AI API terms of service when using this application.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## Support

For issues and questions, please create an issue in the GitHub repository.
