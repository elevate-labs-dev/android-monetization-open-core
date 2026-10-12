// Copyright 2026 Samyaraj Das
// SPDX-License-Identifier: Apache-2.0

pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "independent-sdk-consumer"
include(":app", ":core", ":compose-ui")
project(":core").projectDir = file("../../core")
project(":compose-ui").projectDir = file("../../compose-ui")
