package com.knowway.data.model.department

import com.google.gson.annotations.SerializedName

data class DepartmentStoreResponse(
    val totalPages: Int,
    val totalElements: Int,
    val size: Int,
    val content: List<DepartmentStore>, // 실제 데이터
    val number: Int,
    val sort: Sort,
    val first: Boolean,
    val last: Boolean,
    val numberOfElements: Int,
    val pageable: Pageable,
    val empty: Boolean
)