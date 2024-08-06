package com.knowway.data.model.department

data class DepartmentStorePageResponse(
    val totalElements: Int,
    val totalPages: Int,
    val size: Int,
    val content: List<DepartmentStoreResponse>,
    val number: Int,
    val sort: Sort,
    val first: Boolean,
    val last: Boolean,
    val numberOfElements: Int,
    val pageable: Pageable,
    val empty: Boolean
)