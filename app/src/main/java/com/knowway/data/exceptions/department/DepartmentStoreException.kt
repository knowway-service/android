package com.knowway.data.exceptions.department

open class DepartmentStoreException(message: String) : Exception(message)

class DeptNetworkException() : DepartmentStoreException("네트워크 통신 중 연결에 실패했습니다.")
class DeptByLocationApiException() : DepartmentStoreException("위치 기반으로 백화점 목록을 불러오기에 실패했습니다.")
class DeptUnknownException() : DepartmentStoreException("알 수 없는 에러가 발생했습니다.")
class DeptByBranchApiException() : DepartmentStoreException("지점명으로 백화점 목록을 불러오기에 실패했습니다.")