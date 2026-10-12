// Copyright 2026 Samyaraj Das
// SPDX-License-Identifier: Apache-2.0

package com.elevate.monetization.core

public enum class ProductType {
    /** One-time product. Google Play calls these one-time products. */
    IN_APP,
    SUBSCRIPTION,
}

public enum class PurchaseState {
    UNKNOWN,
    PENDING,
    PURCHASED,
}

public enum class SubscriptionState {
    UNKNOWN,
    PENDING,
    ACTIVE,
    GRACE_PERIOD,
    ACCOUNT_HOLD,
    PAUSED,
    CANCELLED,
    PENDING_PURCHASE_CANCELLED,
    EXPIRED,
}

public enum class EntitlementStatus {
    ACTIVE,
    INACTIVE,
    PENDING,
    EXPIRED,
}

public enum class BillingProvider {
    MOCK,
    GOOGLE_PLAY,
    APP_STORE,
}

public data class Money(
    val formatted: String,
    val currencyCode: String,
    val amountMicros: Long? = null,
)

public data class PricingPhase(
    val price: Money,
    /** ISO-8601 billing period, for example P1M or P1Y. */
    val billingPeriod: String? = null,
    val billingCycleCount: Int? = null,
    val recurrenceMode: String? = null,
)

public data class ProductOffer(
    /** Provider-facing stable key exposed by this SDK, not a purchase token. */
    val offerId: String,
    val basePlanId: String? = null,
    val price: Money,
    val pricingPhases: List<PricingPhase> = emptyList(),
    val offerTags: Set<String> = emptySet(),
)

public data class Product(
    val productId: String,
    val type: ProductType,
    val title: String,
    val description: String,
    val offers: List<ProductOffer>,
    /** Application entitlement key. Defaults to productId when not configured. */
    val entitlementId: String = productId,
) {
    public val primaryOffer: ProductOffer?
        get() = offers.firstOrNull()
}

public data class Purchase(
    /** May be null. Never use this as the backend primary key. */
    val purchaseId: String?,
    val productIds: List<String>,
    /** Sensitive credential. Never log this value. */
    val purchaseToken: String,
    val provider: BillingProvider,
    val state: PurchaseState,
    val purchasedAtEpochMillis: Long,
    val acknowledged: Boolean,
    /**
     * True when the provider reports a purchased subscription that is suspended.
     * Suspended purchases must never be treated as active client-side entitlement.
     */
    val suspended: Boolean = false,
    val autoRenewing: Boolean? = null,
    val subscriptionState: SubscriptionState? = null,
    /** One-way identifier supplied to Google at purchase time, when configured. */
    val obfuscatedAccountId: String? = null,
    /** One-way profile identifier supplied to Google at purchase time, when configured. */
    val obfuscatedProfileId: String? = null,
) {
    override fun toString(): String =
        "Purchase(purchaseId=$purchaseId, productIds=$productIds, purchaseToken=[REDACTED], " +
            "provider=$provider, state=$state, purchasedAtEpochMillis=$purchasedAtEpochMillis, " +
            "acknowledged=$acknowledged, suspended=$suspended, autoRenewing=$autoRenewing, " +
            "subscriptionState=$subscriptionState)"
}

public data class Subscription(
    val productId: String,
    val state: SubscriptionState,
    val currentPeriodEndEpochMillis: Long? = null,
    val autoRenewing: Boolean? = null,
)

public data class Entitlement(
    val entitlementId: String,
    val customerId: String,
    val productId: String,
    val status: EntitlementStatus,
    val source: BillingProvider,
    val validFromEpochMillis: Long? = null,
    val validUntilEpochMillis: Long? = null,
) {
    public val isActive: Boolean
        get() = status == EntitlementStatus.ACTIVE
}

public data class ProductRequest(
    val productId: String,
    val type: ProductType,
    val entitlementId: String = productId,
)

public data class ProductQueryFailure(
    val productId: String,
    val type: ProductType,
    val code: Int,
    val message: String,
)

public data class ProductQueryResult(
    val products: List<Product>,
    val unfetched: List<ProductQueryFailure> = emptyList(),
)

public data class PurchaseRequest(
    val productId: String,
    val productType: ProductType,
    val offerId: String? = null,
    /** Existing Play purchase token when performing a subscription change. */
    val oldPurchaseToken: String? = null,
    /** Existing Play product ID being replaced. */
    val oldProductId: String? = null,
    val replacementMode: SubscriptionReplacementMode? = null,
    /** Prefer a one-way/obfuscated identifier, never a raw account email or user ID. */
    val obfuscatedAccountId: String? = null,
    /** Optional one-way identifier for an in-app profile/avatar. */
    val obfuscatedProfileId: String? = null,
    /** Google Play disclosure for personalized prices in applicable markets. */
    val isOfferPersonalized: Boolean = false,
)

public enum class SubscriptionReplacementMode {
    WITH_TIME_PRORATION,
    CHARGE_PRORATED_PRICE,
    CHARGE_FULL_PRICE,
    WITHOUT_PRORATION,
    DEFERRED,
    KEEP_EXISTING,
}
