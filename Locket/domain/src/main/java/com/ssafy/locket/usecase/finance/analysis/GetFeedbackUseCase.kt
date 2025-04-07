package com.ssafy.locket.usecase.finance.analysis

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.finance.budget.feedback.Feedback
import com.ssafy.locket.repository.finance.analysis.AnalysisRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFeedbackUseCase @Inject constructor(
    private val analysisRepository: AnalysisRepository
){
    suspend operator fun invoke(year: Int,month: Int): Flow<ResponseStatus<Feedback>> {
        return analysisRepository.getFeedback(year,month)
    }
}

