package com.knowway.data.exceptions.location

open class LocationException(message: String) : Exception(message)

class LocationPermissionException: LocationException("위치 권한이 없습니다.")
class LocationLoadException() : LocationException("위치 불러오기에 실패했습니다.")
class LocationUnknownException() : LocationException("알 수 없는 에러가 발생했습니다.")