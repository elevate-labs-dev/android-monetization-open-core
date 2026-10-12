// Copyright 2026 Samyaraj Das
// SPDX-License-Identifier: Apache-2.0

package com.elevate.monetization.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class MockMonetizationProviderTest {
    private val product = Product(
        productId = "premium_monthly",
        type = ProductType.SUBSCRIPTION,
        title = "Premium",
        description = "Premium access",
        offers = listOf(
            ProductOffer("monthly", "monthly", Money("$4.99", "USD", 4_990_000)),
            ProductOffer("trial", "monthly", Money("$0.00", "USD", 0)),
        ),
    )

    @Test
    fun requiresExplicitOfferWhenMultipleOffersExist() = runTest {
        val provider = MockMonetizationProvider(listOf(product))
        val result = provider.purchase(PurchaseRequest(product.productId, ProductType.SUBSCRIPTION))
        val failure = assertIs<PurchaseResult.Failure>(result)
        assertEquals("OFFER_SELECTION_REQUIRED", failure.code)
    }

    @Test
    fun purchaseReturnsAcknowledgedPurchase() = runTest {
        val provider = MockMonetizationProvider(listOf(product))
        val result = provider.purchase(PurchaseRequest(product.productId, ProductType.SUBSCRIPTION, "monthly"))
        val success = assertIs<PurchaseResult.Success>(result)
        assertEquals(true, success.purchase.acknowledged)
        assertEquals(listOf(product.productId), success.purchase.productIds)
    }
}
