package com.smarso.tetris

enum class Sound {
    START, GAME_OVER, PAUSE, MOVE, ROTATE, WIPE, LEVEL_UP
}

interface Soundtrack {
    fun play(sound: Sound, variant: Int = 0)
}