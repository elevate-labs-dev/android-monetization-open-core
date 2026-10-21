# Kordelis Android Monetization Open-Core


Evaluation preview: provider-neutral core, Compose paywall and mock purchase sample.


## Scope


Original `core`, `compose-ui`, `sample-mock`, consumer example and included original documentation/build files use Apache-2.0. Copyright 2026 Samyaraj Das. Kordelis is the current project identity (formerly Elevate Labs). Third-party components retain their own licenses; see [LICENSE](LICENSE), [NOTICE](NOTICE) and [third-party notices](THIRD-PARTY-NOTICES.md).


Google Play adapter, live sample, server verification, RTDN handlers and Pro update service are excluded. Pro features have separate proprietary terms. This preview does not process real payments or verify production entitlement.


## Build and run


Use JDK 17 or a compatible Android Studio JBR, Android SDK Platform 37 and compatible build tools. Initial dependency retrieval requires internet. Set ANDROID_HOME or create an untracked local.properties with sdk.dir pointing to your SDK.


From this repository root on Windows:


```text
gradlew.bat :core:test :compose-ui:assembleDebug :sample-mock:assembleDebug
gradlew.bat :sample-mock:installDebug
gradlew.bat -p examples/consumer-app :app:assembleDebug
gradlew.bat -p examples/consumer-app :app:installDebug
```


On macOS/Linux use ./gradlew; if necessary run chmod +x gradlew first. Installation needs a connected emulator/device. No Firebase project, Play Console or credentials are needed.


The mock sample should report a successful mock purchase. The [separate consumer app](examples/consumer-app/README.md) exercises LOCKED -> mock ACTIVE -> reset LOCKED; its state is in-memory demonstration state, not durable authorization.


Run the root and consumer builds sequentially: they reference the same SDK module output directories. Concurrent Gradle builds can interfere with those outputs.


Read [limitations](docs/LIMITATIONS.md) and [feedback](FEEDBACK.md). Voluntary GitHub feedback intake is active. Free updates are infrequent and no response-time SLA is promised. This is not a production billing release.

