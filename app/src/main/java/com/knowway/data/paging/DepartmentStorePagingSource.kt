package com.knowway.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.knowway.data.model.department.DepartmentStore
import com.knowway.data.network.AdminDepartmentService
import retrofit2.HttpException
import java.io.IOException

class DepartmentStorePagingSource(
    private val service: AdminDepartmentService
) : PagingSource<Int, DepartmentStore>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DepartmentStore> {
        val page = params.key ?: 0
        return try {
            val response = service.getDepartmentStores(params.loadSize, page)
            val departmentStores = response.body()?.content ?: emptyList()
            LoadResult.Page(
                data = departmentStores,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (departmentStores.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DepartmentStore>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
