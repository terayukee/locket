package com.ssafy.locket.data.repository

import com.ssafy.locket.data.remote.api.AnalysisService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalysisRepository @Inject constructor(private val analysisService: AnalysisService){
}