package com.knowway.data.model.admin

data class AdminRecord(
    val recordId: String,
    val recordTitle: String,
    val recordPath: String,
    var isExpanded: Boolean = false
)
