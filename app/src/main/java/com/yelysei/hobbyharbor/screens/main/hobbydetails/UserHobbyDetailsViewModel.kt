package com.yelysei.hobbyharbor.screens.main.hobbydetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yelysei.hobbyharbor.model.UserHobbyIsNotLoadedException
import com.yelysei.hobbyharbor.model.results.PendingResult
import com.yelysei.hobbyharbor.model.results.SuccessResult
import com.yelysei.hobbyharbor.model.results.takeSuccess
import com.yelysei.hobbyharbor.model.userhobbies.UserHobbiesRepository
import com.yelysei.hobbyharbor.model.userhobbies.entities.Action
import com.yelysei.hobbyharbor.model.userhobbies.entities.UserHobby
import com.yelysei.hobbyharbor.model.userhobbies.room.entities.ProgressDbEntity
import com.yelysei.hobbyharbor.screens.main.LiveResult
import com.yelysei.hobbyharbor.screens.main.MutableLiveResult
import kotlinx.coroutines.launch

class UserHobbyDetailsViewModel(
    uhId: Int,
    private val userHobbiesRepository: UserHobbiesRepository,
) : ViewModel() {

    private val _userHobby = MutableLiveResult<UserHobby>(PendingResult())
    val userHobby: LiveResult<UserHobby> = _userHobby

    init {
        viewModelScope.launch {
            userHobbiesRepository.getUserHobbyById(uhId).collect {
                _userHobby.value = SuccessResult(it)
            }
        }
    }

    private fun userExperienceInteraction(
        interaction: UserExperienceInteraction,
        from: Long,
        till: Long,
        id: Int? = null
    ) {
        val userHobby = _userHobby.value.takeSuccess() ?: throw UserHobbyIsNotLoadedException()
        val progressId = userHobby.progress.id

        val action = Action(
            id = id ?: 0,
            startTime = from,
            endTime = till
        )
        viewModelScope.launch {
            when (interaction) {
                UserExperienceInteraction.ADD -> {
                    userHobbiesRepository.addUserHobbyExperience(progressId, action)
                }

                UserExperienceInteraction.UPDATE -> {
                    userHobbiesRepository.updateUserHobbyExperience(progressId, action)
                }
            }
        }

    }

    fun addUserExperience(from: Long, till: Long) {
        userExperienceInteraction(UserExperienceInteraction.ADD, from, till)
    }

    fun editUserExperience(from: Long, till: Long, actionId: Int) {
        userExperienceInteraction(UserExperienceInteraction.UPDATE, from, till, actionId)
    }

    fun updateProgress(goal: Int) {
        val userHobby = _userHobby.value.takeSuccess() ?: throw UserHobbyIsNotLoadedException()
        val progressId = userHobby.progress.id

        val progressDbEntity = ProgressDbEntity(
            id = progressId,
            goal = goal
        )
        viewModelScope.launch {
            userHobbiesRepository.updateProgress(progressDbEntity)
        }
    }
}

enum class UserExperienceInteraction {
    UPDATE, ADD
}