// Copyright 2026 Samyaraj Das
// SPDX-License-Identifier: Apache-2.0

package com.elevate.monetization.core

public interface MonetizationProvider {
    public val provider: BillingProvider

    public suspend fun getProducts(requests: List<ProductRequest>): ProductQueryResult
}

public interface EntitlementRepository {
    public suspend fun get(customerId: String, entitlementId: String): Entitlement?

    public suspend fun put(entitlement: Entitlement)

    public suspend fun remove(customerId: String, entitlementId: String)
}

public sealed interface PurchaseResult {
    public data class Success(val purchase: Purchase) : PurchaseResult
    public data class Pending(val purchase: Purchase) : PurchaseResult
    public data object Cancelled : PurchaseResult
    public data class Failure(
        val code: String,
        val message: String,
        val cause: Throwable? = null,
    ) : PurchaseResult
}

public interface EntitlementResolver {
    public fun resolve(
        customerId: String,
        product: Product,
        purchase: Purchase,
    ): Entitlement
}

/**
 * Client-side safety rule: an entitlement is ACTIVE only after the purchase is PURCHASED
 * and acknowledged. In production, acknowledgement should happen server-side after
 * server verification. Pending or unacknowledged client purchases never activate access.
 */
public class DefaultEntitlementResolver(
    private val clock: () -> Long = { System.currentTimeMillis() },
) : EntitlementResolver {
    override fun resolve(
        customerId: String,
        product: Product,
        purchase: Purchase,
    ): Entitlement {
        require(product.productId in purchase.productIds) {
            "Purchase ${purchase.purchaseId ?: "<unknown>"} does not contain ${product.productId}"
        }

        val status = when {
            purchase.state == PurchaseState.PENDING -> EntitlementStatus.PENDING
            purchase.suspended -> EntitlementStatus.INACTIVE
            purchase.state == PurchaseState.PURCHASED && purchase.acknowledged -> EntitlementStatus.ACTIVE
            purchase.state == PurchaseState.PURCHASED -> EntitlementStatus.PENDING
            else -> EntitlementStatus.INACTIVE
        }

        return Entitlement(
            entitlementId = product.entitlementId,
            customerId = customerId,
            productId = product.productId,
            status = status,
            source = purchase.provider,
            validFromEpochMillis = purchase.purchasedAtEpochMillis.takeIf {
                status == EntitlementStatus.ACTIVE
            },
            validUntilEpochMillis = null,
        )
    }
}

/**
 * Small client-side coordinator for storing the SDK's local interpretation of a purchase.
 *
 * This is intentionally not the production entitlement authority; a secure backend must
 * verify Google Play purchases before granting durable access.
 */
public class ClientEntitlementCoordinator(
    private val repository: EntitlementRepository,
    private val resolver: EntitlementResolver = DefaultEntitlementResolver(),
) {
    public suspend fun process(
        customerId: String,
        product: Product,
        purchase: Purchase,
    ): Entitlement {
        val entitlement = resolver.resolve(customerId, product, purchase)
        repository.put(entitlement)
        return entitlement
    }

    public suspend fun get(
        customerId: String,
        entitlementId: String,
    ): Entitlement? = repository.get(customerId, entitlementId)
}
