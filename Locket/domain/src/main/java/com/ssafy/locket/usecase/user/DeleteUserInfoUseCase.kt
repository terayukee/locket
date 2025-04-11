package com.ssafy.locket.usecase.user

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.user.UserInfo
import com.ssafy.locket.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteUserInfoUseCase @Inject constructor(private val userRepository: UserRepository){
    suspend operator fun invoke(userId: Long): Flow<ResponseStatus<Unit>> {
        return userRepository.deleteUserInfo(userId)
    }
}