package com.yelysei.hobbyharbor.model.userhobbies

import com.yelysei.hobbyharbor.model.hobbies.entities.Hobby
import com.yelysei.hobbyharbor.model.userhobbies.entities.Action
import com.yelysei.hobbyharbor.model.userhobbies.entities.UserHobby
import kotlinx.coroutines.flow.Flow

interface UserHobbiesRepository  {
    suspend fun getUserHobbyById(id: Int): Flow<UserHobby>

    suspend fun getUserHobbies(): Flow<List<UserHobby>>

    /**
     * @throws UserHobbyAlreadyAddedException
     */
    suspend fun addUserHobby(hobby: Hobby, goal: Int)

    suspend fun addUserHobbyExperience(progressId: Int, action: Action)

    suspend fun updateUserHobbyExperience(progressId: Int, action: Action)

    suspend fun getActionsByProgressId(progressId: Int): Flow<List<Action>>
}