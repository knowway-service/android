package com.knowway.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.knowway.data.model.department.DepartmentStoreResponse
import com.knowway.data.repository.AdminRepository
import retrofit2.HttpException
import java.io.IOException

class DepartmentStorePagingSource(
    private val repository: AdminRepository
) : PagingSource<Int, DepartmentStoreResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DepartmentStoreResponse> {
        val page = params.key ?: 0
        return try {
            val response = repository.getDepartmentStores(params.loadSize, page)
            val departmentStores = response.body()?.content ?: emptyList()

            val nextKey = if (departmentStores.size < params.loadSize) {
                null
            } else {
                page + 1
            }

            LoadResult.Page(
                data = departmentStores,
                prevKey = if (page == 0) null else page - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DepartmentStoreResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}