package com.example.rangerapp.feature_map.domain.use_case.monitoring


data class MonitoringUseCases(
    val addMonitoring: AddMonitoring,
    val addIgnoreMonitoring: AddIgnoreMonitoring,
    val getMonitoring: GetMonitoring,
    val getMonitorings: GetMonitorings,
    val deleteMonitoring: DeleteMonitoring,
    val updateLocation: UpdateMonitoringLocation,
)

