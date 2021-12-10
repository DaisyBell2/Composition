package com.daisybell.composition.domain.usecase

import com.daisybell.composition.domain.entity.GameSettings
import com.daisybell.composition.domain.entity.Level
import com.daisybell.composition.domain.repository.GameRepository

class GetGameSettingsUseCase(
    private val repository: GameRepository
) {

    operator fun invoke(level: Level): GameSettings {
        return repository.getGameSettings(level)
    }
}