// Copyright 2026 Samyaraj Das
// SPDX-License-Identifier: Apache-2.0

package com.elevate.monetization.core

public class InMemoryEntitlementRepository : EntitlementRepository {
    private val values = mutableMapOf<String, Entitlement>()

    private fun key(customerId: String, entitlementId: String): String =
        "$customerId::$entitlementId"

    override suspend fun get(customerId: String, entitlementId: String): Entitlement? =
        values[key(customerId, entitlementId)]

    override suspend fun put(entitlement: Entitlement) {
        values[key(entitlement.customerId, entitlement.entitlementId)] = entitlement
    }

    override suspend fun remove(customerId: String, entitlementId: String) {
        values.remove(key(customerId, entitlementId))
    }
}
