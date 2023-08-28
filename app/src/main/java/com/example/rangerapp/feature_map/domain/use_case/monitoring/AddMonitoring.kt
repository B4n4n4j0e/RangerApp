package com.example.rangerapp.feature_map.domain.use_case.monitoring

import com.example.rangerapp.feature_map.domain.model.InvalidMonitoringException
import com.example.rangerapp.feature_map.domain.model.Monitoring
import com.example.rangerapp.feature_map.domain.repository.MonitoringRepository

class AddMonitoring(
    private val repository: MonitoringRepository
) {

    @kotlin.jvm.Throws(InvalidMonitoringException::class)
    suspend operator fun invoke(monitoring: Monitoring) {
        repository.insert(monitoring)


    }

}