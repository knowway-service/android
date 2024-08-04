package com.knowway.data.model.admin

data class AdminRecord(
    val title: String,
    val audioFileUrl: String,
    var isExpanded: Boolean = false
)
