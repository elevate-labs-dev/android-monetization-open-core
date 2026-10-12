// Copyright 2026 Samyaraj Das
// SPDX-License-Identifier: Apache-2.0

package com.elevate.monetization.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultEntitlementResolverTest {
    private val product = Product(
        productId = "premium_monthly",
        type = ProductType.SUBSCRIPTION,
        title = "Premium",
        description = "Premium access",
        offers = listOf(
            ProductOffer(
                offerId = "monthly",
                basePlanId = "monthly",
                price = Money("$4.99", "USD", 4_990_000),
            ),
        ),
        entitlementId = "premium",
    )

    private val resolver = DefaultEntitlementResolver { 1_000L }

    @Test
    fun pendingPurchaseNeverActivatesEntitlement() {
        val purchase = Purchase(
            purchaseId = "p1",
            productIds = listOf(product.productId),
            purchaseToken = "secret",
            provider = BillingProvider.GOOGLE_PLAY,
            state = PurchaseState.PENDING,
            purchasedAtEpochMillis = 900L,
            acknowledged = false,
        )

        val entitlement = resolver.resolve("customer-1", product, purchase)

        assertEquals(EntitlementStatus.PENDING, entitlement.status)
        assertFalse(entitlement.isActive)
    }

    @Test
    fun purchasedButUnacknowledgedPurchaseDoesNotActivateEntitlement() {
        val purchase = Purchase(
            purchaseId = "p2",
            productIds = listOf(product.productId),
            purchaseToken = "secret",
            provider = BillingProvider.GOOGLE_PLAY,
            state = PurchaseState.PURCHASED,
            purchasedAtEpochMillis = 900L,
            acknowledged = false,
        )

        val entitlement = resolver.resolve("customer-1", product, purchase)

        assertEquals(EntitlementStatus.PENDING, entitlement.status)
        assertFalse(entitlement.isActive)
    }

    @Test
    fun purchasedAndAcknowledgedPurchaseActivatesEntitlement() {
        val purchase = Purchase(
            purchaseId = "p3",
            productIds = listOf(product.productId),
            purchaseToken = "secret",
            provider = BillingProvider.GOOGLE_PLAY,
            state = PurchaseState.PURCHASED,
            purchasedAtEpochMillis = 900L,
            acknowledged = true,
        )

        val entitlement = resolver.resolve("customer-1", product, purchase)

        assertEquals(EntitlementStatus.ACTIVE, entitlement.status)
        assertTrue(entitlement.isActive)
        assertEquals(900L, entitlement.validFromEpochMillis)
    }

    @Test
    fun suspendedPurchasedSubscriptionNeverActivatesEntitlement() {
        val purchase = Purchase(
            purchaseId = "p4",
            productIds = listOf(product.productId),
            purchaseToken = "secret",
            provider = BillingProvider.GOOGLE_PLAY,
            state = PurchaseState.PURCHASED,
            purchasedAtEpochMillis = 900L,
            acknowledged = true,
            suspended = true,
        )

        val entitlement = resolver.resolve("customer-1", product, purchase)

        assertEquals(EntitlementStatus.INACTIVE, entitlement.status)
        assertFalse(entitlement.isActive)
    }

}
