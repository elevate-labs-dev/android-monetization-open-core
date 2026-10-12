// Copyright 2026 Samyaraj Das
// SPDX-License-Identifier: Apache-2.0

package com.elevate.monetization.core

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ClientEntitlementCoordinatorTest {
    @Test
    fun storesResolverResult() = runTest {
        val repository = InMemoryEntitlementRepository()
        val coordinator = ClientEntitlementCoordinator(repository)
        val product = Product(
            productId = "premium",
            type = ProductType.SUBSCRIPTION,
            title = "Premium",
            description = "Premium access",
            offers = listOf(ProductOffer("monthly", price = Money("$4.99", "USD"))),
        )
        val purchase = Purchase(
            purchaseId = "p1",
            productIds = listOf("premium"),
            purchaseToken = "token",
            provider = BillingProvider.MOCK,
            state = PurchaseState.PURCHASED,
            purchasedAtEpochMillis = 1000,
            acknowledged = true,
        )

        val entitlement = coordinator.process("customer", product, purchase)

        assertEquals(EntitlementStatus.ACTIVE, entitlement.status)
        assertEquals(entitlement, coordinator.get("customer", "premium"))
    }
}
