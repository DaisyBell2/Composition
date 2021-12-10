package com.daisybell.composition.domain.repository

import com.daisybell.composition.domain.entity.GameSettings
import com.daisybell.composition.domain.entity.Level
import com.daisybell.composition.domain.entity.Question

interface GameRepository {

    fun generateQuestion(
        maxSumValue: Int,
        countOfOptions: Int
    ): Question

    fun getGameSettings(level: Level): GameSettings

}