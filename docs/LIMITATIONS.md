# Open-Core preview limitations

Included: provider-neutral models/contracts, mock purchase provider, Compose paywall primitives, mock sample and independent consumer example.

The mock provider simulates success. It does not charge money, contact Google Play, verify purchases, provide durable server-authorized access, or demonstrate pending payments, renewal, expiry, pause, replacement, RTDN or retry delivery. The consumer example's in-memory access flag is a UI demonstration and must not authorize paid features.

Google Play integration, live sample and backend belong to the separately licensed Pro edition. Pro release readiness is still under review. This preview does not claim production readiness of the complete SDK.

The pinned build targets SDK 37; Android app minimum API is 24. Public dependencies require connectivity on first build. See INSTALLATION.md and COMPATIBILITY.md for toolchain details. A successful cached offline build does not prove a first-time network build.

Free receives infrequent feature updates. Critical/security issues and glaring defects are intended to be addressed; no release schedule, guaranteed response time, SLA or paid support entitlement is promised. Apache-2.0 permissions for versions already received do not expire. The Pro annual one-app restrictions do not apply to Open-Core.

Third-party notices and release-candidate consumer checks must pass before public distribution. The public release will supply its version, contents and checksum. Feedback is voluntary and no marketing telemetry is added to the SDK.
