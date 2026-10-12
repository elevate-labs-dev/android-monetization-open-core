// Copyright 2026 Samyaraj Das
// SPDX-License-Identifier: Apache-2.0

package com.example.sdkconsumer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elevate.monetization.core.MockMonetizationProvider
import com.elevate.monetization.core.Money
import com.elevate.monetization.core.Product
import com.elevate.monetization.core.ProductOffer
import com.elevate.monetization.core.ProductRequest
import com.elevate.monetization.core.ProductType
import com.elevate.monetization.core.PurchaseRequest
import com.elevate.monetization.core.PurchaseResult
import com.elevate.monetization.ui.MonetizationPaywall
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val product = Product(
        productId = "premium_monthly",
        type = ProductType.SUBSCRIPTION,
        title = "Premium Monthly",
        description = "Mock-only product for SDK integration",
        offers = listOf(
            ProductOffer(
                offerId = "monthly",
                basePlanId = "monthly",
                price = Money("$4.99", "USD", 4_990_000),
            ),
        ),
        entitlementId = "premium",
    )

    private val provider = MockMonetizationProvider(listOf(product))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var hasMockAccess by remember { mutableStateOf(false) }
            var products by remember { mutableStateOf(emptyList<Product>()) }
            var selectedId by remember { mutableStateOf<String?>(null) }
            var status by remember { mutableStateOf("Loading mock catalogâ€¦") }
            var isLoading by remember { mutableStateOf(true) }
            var loadError by remember { mutableStateOf<String?>(null) }
            var retryCount by remember { mutableStateOf(0) }

            LaunchedEffect(retryCount) {
                isLoading = true
                loadError = null
                try {
                    val result = provider.getProducts(
                        listOf(ProductRequest(product.productId, product.type, product.entitlementId)),
                    )
                    products = result.products
                    selectedId = result.products.firstOrNull()?.productId
                    status = if (result.products.isEmpty()) "No mock products available" else "Mock provider ready"
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Throwable) {
                    products = emptyList()
                    selectedId = null
                    loadError = "Could not load the mock catalog."
                    status = "Could not load the mock catalog."
                } finally {
                    isLoading = false
                }
            }

            MaterialTheme {
                Surface {
                    Column {
                        Text("Status: $status", modifier = Modifier.padding(24.dp))
                        Text(if (hasMockAccess) "Demo access: ACTIVE (mock only)" else "Demo access: LOCKED")
                        androidx.compose.material3.Button(onClick = { hasMockAccess = false }) { Text("Reset demo access") }
                        MonetizationPaywall(
                            products = products,
                            isLoading = isLoading,
                            selectedProductId = selectedId,
                            loadError = loadError,
                            onRetry = { retryCount += 1 },
                            onProductSelected = { selectedId = it.productId },
                            onPurchase = {
                                val selected = products.firstOrNull { it.productId == selectedId }
                                    ?: return@MonetizationPaywall
                                lifecycleScope.launch {
                                    status = when (
                                        val result = provider.purchase(
                                            PurchaseRequest(
                                                productId = selected.productId,
                                                productType = selected.type,
                                                offerId = selected.primaryOffer?.offerId,
                                            ),
                                        )
                                    ) {
                                        is PurchaseResult.Success -> { hasMockAccess = true; "Mock purchase successful" }
                                        is PurchaseResult.Pending -> "Mock purchase pending"
                                        PurchaseResult.Cancelled -> "Mock purchase cancelled"
                                        is PurchaseResult.Failure -> "Mock purchase failed: ${result.code}"
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
