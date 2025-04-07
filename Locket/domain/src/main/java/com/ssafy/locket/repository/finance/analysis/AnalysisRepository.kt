package com.ssafy.locket.repository.finance.analysis

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.finance.budget.feedback.Feedback
import kotlinx.coroutines.flow.Flow

interface AnalysisRepository {
    suspend fun getFeedback(year:Int,month:Int) : Flow<ResponseStatus<Feedback>>
}