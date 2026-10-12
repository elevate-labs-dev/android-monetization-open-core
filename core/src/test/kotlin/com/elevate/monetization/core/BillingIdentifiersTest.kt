// Copyright 2026 Samyaraj Das
// SPDX-License-Identifier: Apache-2.0

package com.elevate.monetization.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BillingIdentifiersTest {
    @Test
    fun sha256IsDeterministicAndDoesNotReturnInput() {
        val result = BillingIdentifiers.sha256("customer-123")
        assertEquals(64, result.length)
        assertEquals(result, BillingIdentifiers.sha256("customer-123"))
        assert(result != "customer-123")
    }

    @Test
    fun rejectsBlankIdentifier() {
        assertFailsWith<IllegalArgumentException> { BillingIdentifiers.sha256("  ") }
    }
}
