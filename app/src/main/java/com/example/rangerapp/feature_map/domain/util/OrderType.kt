package com.example.rangerapp.feature_map.domain.util

sealed class OrderType {
    object Ascending : OrderType()
    object Descending : OrderType()
}
