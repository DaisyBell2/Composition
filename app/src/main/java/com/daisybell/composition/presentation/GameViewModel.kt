package com.daisybell.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daisybell.composition.R
import com.daisybell.composition.data.GameRepositoryImpl
import com.daisybell.composition.domain.entity.GameResult
import com.daisybell.composition.domain.entity.GameSettings
import com.daisybell.composition.domain.entity.Level
import com.daisybell.composition.domain.entity.Question
import com.daisybell.composition.domain.usecase.GenerateQuestionUseCase
import com.daisybell.composition.domain.usecase.GetGameSettingsUseCase

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var gameSettings: GameSettings
    private lateinit var level: Level

    private val context = application
    private val repository = GameRepositoryImpl

    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private var timer: CountDownTimer? = null

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _enoughCount = MutableLiveData<Boolean>()
    val enoughCount: LiveData<Boolean>
        get() = _enoughCount

    private val _enoughPercent = MutableLiveData<Boolean>()
    val enoughPercent: LiveData<Boolean>
        get() = _enoughPercent

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult


    private var countOfRightAnswer = 0
    private var countOfQuestion = 0


    fun startGame(level: Level) {
        getGameSettings(level)
        startTimer()
        generateQuestion()
    }

    private fun chooseAnswer(number: Int) {
        checkAnswer(number)
        updateProgress()
        generateQuestion()
    }

    private fun updateProgress() {
        val percent = calculatePercentOfRightAnswers()
        _percentOfRightAnswers.value = percent
        _progressAnswers.value = String().format(
            context.resources.getString(R.string.progress_answers),
            countOfRightAnswer,
            gameSettings.minCountOfRightAnswers
        )
        _enoughCount.value = countOfRightAnswer >= gameSettings.minCountOfRightAnswers
        _enoughPercent.value = percent >= gameSettings.minPercentOfRightAnswers
    }

    private fun calculatePercentOfRightAnswers(): Int {
        return ((countOfRightAnswer / countOfQuestion.toDouble()) * 100).toInt()
    }

    private fun checkAnswer(number: Int) {
        val rightAnswer = question.value?.rightAnswer
        if (number == rightAnswer) {
            countOfRightAnswer++
        }
        countOfQuestion++
    }

    private fun getGameSettings(level: Level) {
        this.level = level
        this.gameSettings = getGameSettingsUseCase(level)
        _minPercent.value = gameSettings.minPercentOfRightAnswers
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * MILLIS_IN_SECONDS,
            MILLIS_IN_SECONDS
        ) {
            override fun onTick(p0: Long) {
                _formattedTime.value = formatTime(p0)
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer?.start()
    }

    internal fun formatTime(p0: Long): String {
        val seconds = p0 / MILLIS_IN_SECONDS
        val minutes = seconds / SECONDS_IN_MINUTES
        val leftSeconds = seconds - (minutes * SECONDS_IN_MINUTES)
        return String().format("%02d:%02d", minutes, leftSeconds)
    }

    internal fun finishGame() {
        _gameResult.value = GameResult(
            enoughCount.value == true && enoughPercent.value == true,
            countOfRightAnswer,
            countOfQuestion,
            gameSettings
        )
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        private const val MILLIS_IN_SECONDS = 1000L
        private const val SECONDS_IN_MINUTES = 60
    }

}