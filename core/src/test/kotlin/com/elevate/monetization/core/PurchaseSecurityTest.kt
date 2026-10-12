// Copyright 2026 Samyaraj Das
// SPDX-License-Identifier: Apache-2.0

package com.elevate.monetization.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PurchaseSecurityTest {
    @Test
    fun toStringRedactsPurchaseToken() {
        val purchase = Purchase(
            purchaseId = "order-1",
            productIds = listOf("premium_monthly"),
            purchaseToken = "highly-sensitive-token",
            provider = BillingProvider.GOOGLE_PLAY,
            state = PurchaseState.PURCHASED,
            purchasedAtEpochMillis = 1_000L,
            acknowledged = true,
        )

        val rendered = purchase.toString()

        assertFalse(rendered.contains("highly-sensitive-token"))
        assertTrue(rendered.contains("purchaseToken=[REDACTED]"))
    }
}
