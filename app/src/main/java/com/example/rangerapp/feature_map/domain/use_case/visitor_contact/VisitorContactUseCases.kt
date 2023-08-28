package com.example.rangerapp.feature_map.domain.use_case.visitor_contact

data class VisitorContactUseCases(
    val getVisitorContacts: GetVisitorContacts,
    val deleteVisitorContact: DeleteVisitorContacts,
    val addVisitorContact: AddVisitorContact,
    val addIgnoreVisitorContact: AddIgnoreVisitorContact,
    val getVisitorContact: GetVisitorContact,
    val updateLocation: UpdateVisitorContactLocation,
)
