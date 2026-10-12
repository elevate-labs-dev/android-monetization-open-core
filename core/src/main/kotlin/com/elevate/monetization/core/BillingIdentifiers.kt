// Copyright 2026 Samyaraj Das
// SPDX-License-Identifier: Apache-2.0

package com.elevate.monetization.core

import java.security.MessageDigest

/** Helpers for generating deterministic one-way identifiers suitable for Play Billing metadata. */
public object BillingIdentifiers {
    /**
     * Returns a lowercase SHA-256 hex digest. Pass a stable app-level identifier, not a raw secret.
     * The resulting 64-character string can be supplied as an obfuscated account/profile ID.
     */
    public fun sha256(value: String): String {
        require(value.isNotBlank()) { "Identifier must not be blank" }
        val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
        return digest.joinToString(separator = "") { "%02x".format(it) }
    }
}
