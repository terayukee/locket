package com.ssafy.locket.usecase.user

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.user.UserInfo
import com.ssafy.locket.repository.user.DataStoreRepository
import com.ssafy.locket.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(): Flow<ResponseStatus<UserInfo>> {
        val userId = dataStoreRepository.userId.firstOrNull() ?: -1
        return userRepository.getUserInfo(userId)
            .onEach { response ->
                if (response is ResponseStatus.Success) {
                    response.data.let { userInfo ->
                        dataStoreRepository.saveUser(userInfo)
                    }
                }
            }
    }
}