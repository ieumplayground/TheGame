
package com.ieum.paragrapy

data class GameState(
    var screen: String = "MENU",
    var stage: Int = 1,
    var roomId: String = "living_room",
    var floor: Int = 1,
    var playerX: Float = 0f,
    var playerZ: Float = 3f,
    var yaw: Float = 0f,
    var pitch: Float = 0f,
    var threat: Float = 0f,
    var elapsedSeconds: Float = 0f,
    var paused: Boolean = false,
    var gameOver: Boolean = false,
    var ended: Boolean = false,
    var credits: Boolean = false,
    val clues: MutableSet<String> = linkedSetOf(),
    val inventory: MutableSet<String> = linkedSetOf(),
    val crafted: MutableSet<String> = linkedSetOf(),
    val researched: MutableSet<String> = linkedSetOf(),
    val log: MutableList<String> = mutableListOf(
        "눈을 떴다.",
        "여기는 평범한 집이다.",
        "주변을 조사해 보자."
    )
) {
    fun addLog(message: String) {
        log.add(message)
        while (log.size > 10) log.removeAt(0)
    }
}
