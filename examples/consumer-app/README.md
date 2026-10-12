# Integrate Open-Core in a separate test app

This standalone Gradle project uses application ID `com.example.sdkconsumer`. It references the unchanged `core` and `compose-ui` source modules two directories above; it is not the supplied sample-mock app. No Play account, Firebase or signing credential is needed.

Prerequisites: JDK 17 or the compatible Android Studio JBR, Android SDK Platform 37 and the build tools required by the pinned Android plugin. Set ANDROID_HOME to your SDK location. First-time builds require internet access for public dependencies.

From the extracted Open-Core root:

```text
gradlew.bat -p examples/consumer-app :app:assembleDebug
gradlew.bat -p examples/consumer-app :app:installDebug
```

On macOS/Linux substitute `./gradlew`. Open **Independent SDK Consumer** on an Android device/emulator (API 24+).

1. Expect `Mock provider ready` and `Demo access: LOCKED`.
2. Select the mock product and complete the paywall action. Expect `Mock purchase successful` and `Demo access: ACTIVE (mock only)`.
3. Select **Reset demo access**. Expect `Demo access: LOCKED`.

The boolean in this example demonstrates consumer UI wiring only. It is held in memory, resets on recreation, and is not a verified or persistent entitlement. Do not use it to authorize real paid features. The mock provider does not contact Play or implement subscription lifecycle verification. See ../../docs/LIMITATIONS.md.

To use a different test app, reproduce the module declarations in settings.gradle.kts and dependencies in app/build.gradle.kts, then adapt the activity. Do not modify SDK modules just to satisfy this exercise. Report the version, stage reached, expected/actual output and sanitized build errors using the supplied feedback template.
