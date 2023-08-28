package com.example.rangerapp.feature_map.domain.util

sealed class MapEntryOrder(val orderType: OrderType) {
    class Title(orderType: OrderType) : MapEntryOrder(orderType)
    class Date(orderType: OrderType) : MapEntryOrder(orderType)
    class Color(orderType: OrderType) : MapEntryOrder(orderType)

    fun copy(orderType: OrderType): MapEntryOrder {
        return when (this) {
            is Title -> Title(orderType)
            is Date -> Date(orderType)
            is Color -> Color(orderType)
        }
    }
}
