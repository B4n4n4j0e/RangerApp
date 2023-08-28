package com.example.rangerapp.feature_map.domain.use_case.biotop

data class BiotopUseCases(
    val getBiotop: GetBiotop,
    val deleteBiotops: DeleteBiotops,
    val addBiotop: AddBiotop,
    val addIgnoreBiotop: AddIgnoreBiotop,
    val updateLocation: UpdateBiotopLocation,
    val getBiotops: GetBiotops
)
