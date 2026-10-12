// Copyright 2026 Samyaraj Das
// SPDX-License-Identifier: Apache-2.0

package com.elevate.monetization.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

public class MockMonetizationProvider(
    products: List<Product>,
    private val clock: () -> Long = { System.currentTimeMillis() },
) : MonetizationProvider {
    override val provider: BillingProvider = BillingProvider.MOCK

    private val productMap: Map<String, Product> = products.associateBy(Product::productId)
    private val events = MutableSharedFlow<Purchase>(extraBufferCapacity = 16)
    private var nextPurchaseNumber: Long = 1

    public val purchaseEvents: Flow<Purchase> = events.asSharedFlow()

    override suspend fun getProducts(requests: List<ProductRequest>): ProductQueryResult =
        ProductQueryResult(
            products = requests.mapNotNull { request ->
                productMap[request.productId]?.copy(entitlementId = request.entitlementId)
            },
            unfetched = requests.filter { it.productId !in productMap }.map {
                ProductQueryFailure(it.productId, it.type, -1, "Unknown mock product")
            },
        )

    public suspend fun purchase(request: PurchaseRequest): PurchaseResult {
        val product = productMap[request.productId]
            ?: return PurchaseResult.Failure("PRODUCT_NOT_FOUND", "Unknown product: ${request.productId}")

        val offer = when {
            product.offers.isEmpty() -> null
            request.offerId == null && product.offers.size == 1 -> product.offers.single()
            request.offerId != null -> product.offers.firstOrNull { it.offerId == request.offerId }
            else -> null
        } ?: return PurchaseResult.Failure(
            "OFFER_SELECTION_REQUIRED",
            "Select an offer for ${product.productId}",
        )

        val purchaseNumber = nextPurchaseNumber++
        if (request.productType != product.type) {
            return PurchaseResult.Failure(
                "PRODUCT_TYPE_MISMATCH",
                "${product.productId} is configured as ${product.type}",
            )
        }

        val purchase = Purchase(
            purchaseId = "mock-purchase-$purchaseNumber",
            productIds = listOf(product.productId),
            purchaseToken = "mock-token-$purchaseNumber",
            provider = provider,
            state = PurchaseState.PURCHASED,
            purchasedAtEpochMillis = clock(),
            acknowledged = true,
            autoRenewing = product.type == ProductType.SUBSCRIPTION,
            subscriptionState = if (product.type == ProductType.SUBSCRIPTION) {
                SubscriptionState.ACTIVE
            } else {
                null
            },
        )
        events.tryEmit(purchase)
        // Keep the local variable meaningful: this verifies offer selection in the mock path.
        check(offer.offerId.isNotBlank())
        return PurchaseResult.Success(purchase)
    }
}
