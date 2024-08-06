package com.knowway.data.exceptions.mainpage

open class MainPageException(message: String) : Exception(message)

class MainPageNetworkException() : MainPageException("네트워크 통신 중 연결에 실패했습니다.")
class MainPageApiException() : MainPageException("백화점 지점 및 층 정보를 바탕으로 녹음 목록을 불러오기에 실패했습니다.")
class MainPageUnknownException() : MainPageException("알 수 없는 에러가 발생했습니다.")
