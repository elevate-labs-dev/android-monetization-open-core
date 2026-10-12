// Copyright 2026 Samyaraj Das
// SPDX-License-Identifier: Apache-2.0

package com.elevate.monetization.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elevate.monetization.core.Product

@Composable
public fun MonetizationPaywall(
    products: List<Product>,
    isLoading: Boolean,
    selectedProductId: String?,
    onProductSelected: (Product) -> Unit,
    onPurchase: () -> Unit,
    modifier: Modifier = Modifier,
    loadError: String? = null,
    onRetry: (() -> Unit)? = null,
) {
    if (isLoading) {
        CircularProgressIndicator(modifier = modifier.padding(24.dp))
        return
    }

    if (loadError != null || products.isEmpty()) {
        Column(
            modifier = modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(loadError ?: "No products are available right now.")
            if (onRetry != null) {
                Button(onClick = onRetry) { Text("Retry") }
            }
        }
        return
    }

    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Choose your plan", style = MaterialTheme.typography.headlineSmall)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(products, key = Product::productId) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onProductSelected(product) },
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(product.title, style = MaterialTheme.typography.titleMedium)
                        Text(product.description, style = MaterialTheme.typography.bodyMedium)
                        product.primaryOffer?.let {
                            Text(it.price.formatted, style = MaterialTheme.typography.titleLarge)
                        }
                        if (product.productId == selectedProductId) {
                            Text("Selected", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
        Button(
            onClick = onPurchase,
            enabled = selectedProductId != null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Continue")
        }
    }
}
