package com.ssafy.locket.data.repository.budget

import android.util.Log
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.data.network.api.BudgetService
import com.ssafy.locket.data.network.common.ApiResponse
import com.ssafy.locket.data.network.common.ApiResponseHandler
import com.ssafy.locket.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.budget.ShortFeedbackResponse.Companion.toDomainModel
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.finance.budget.SetBudget
import com.ssafy.locket.model.finance.budget.feedback.Feedback
import com.ssafy.locket.model.finance.budget.feedback.ShortFeedback
import com.ssafy.locket.model.finance.budget.status.BudgetStatus
import com.ssafy.locket.repository.budget.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetService: BudgetService,
    private val dataStore: UserDataStoreSource
) : BudgetRepository {
    override suspend fun setBudgetGoal(amount: Int): Flow<ResponseStatus<SetBudget>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first()?:-1
                budgetService.setBudgetGoal(SetBudget(amount,userId.toInt()))
            }.onEach { result ->
                when(result) {
                    is ApiResponse.Success -> {
                        Log.d("BudgetFragment",result.data.toString())
                        emit(ResponseStatus.Success(result.data))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }

    override suspend fun getBudgetStatus(
        year: Int,
        month: Int
    ): Flow<ResponseStatus<BudgetStatus>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                budgetService.getBudgetStatus(userId.toInt(),year,month)
            }.onEach { result ->
                when(result) {
                    is ApiResponse.Success -> {
                        Log.d("BudgetFragment","출력"+result.data.toString())
                        emit(ResponseStatus.Success(result.data))
                    }
                    is ApiResponse.Error -> {
                        Log.d("BudgetFragment",result.error.toString())
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }

    override suspend fun getShortFeedback(): Flow<ResponseStatus<ShortFeedback>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                budgetService.getShortFeedback(userId.toInt())
            }.onEach { result ->
                when(result) {
                    is ApiResponse.Success -> {
                        Log.d("BudgetFragment",result.data.toString())
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }
}