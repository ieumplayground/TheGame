
package com.ieum.paragrapy

import com.badlogic.gdx.Gdx
import java.util.Properties

object SaveRepository {
    private const val FILE = "paragrapy1_v1.properties"

    private fun pack(values: Collection<String>): String =
        values.joinToString("|") {
            java.util.Base64.getEncoder()
                .encodeToString(it.toByteArray(Charsets.UTF_8))
        }

    private fun unpack(value: String): MutableSet<String> =
        value.split("|")
            .filter { it.isNotBlank() }
            .mapNotNull {
                runCatching {
                    String(
                        java.util.Base64.getDecoder().decode(it),
                        Charsets.UTF_8
                    )
                }.getOrNull()
            }
            .toMutableSet()

    fun exists(): Boolean =
        Gdx.files.local(FILE).exists()

    fun save(s: GameState): Boolean = runCatching {
        val p = Properties()

        p.setProperty("stage", s.stage.toString())
        p.setProperty("roomId", s.roomId)
        p.setProperty("floor", s.floor.toString())
        p.setProperty("playerX", s.playerX.toString())
        p.setProperty("playerZ", s.playerZ.toString())
        p.setProperty("yaw", s.yaw.toString())
        p.setProperty("pitch", s.pitch.toString())
        p.setProperty("threat", s.threat.toString())
        p.setProperty("elapsedSeconds", s.elapsedSeconds.toString())
        p.setProperty("gameOver", s.gameOver.toString())
        p.setProperty("ended", s.ended.toString())
        p.setProperty("credits", s.credits.toString())

        p.setProperty("clues", pack(s.clues))
        p.setProperty("inventory", pack(s.inventory))
        p.setProperty("crafted", pack(s.crafted))
        p.setProperty("researched", pack(s.researched))
        p.setProperty("log", pack(s.log))

        val writer = java.io.StringWriter()
        p.store(writer, "Paragrapy 1 / V1")
        Gdx.files.local(FILE).writeString(writer.toString(), false)
        true
    }.getOrDefault(false)

    fun load(): GameState? = runCatching {
        val file = Gdx.files.local(FILE)
        if (!file.exists()) return null

        val p = Properties()
        file.reader().use { p.load(it) }

        GameState(
            screen = "GAME",
            stage = p.getProperty("stage", "1").toInt(),
            roomId = p.getProperty("roomId", "living_room"),
            floor = p.getProperty("floor", "1").toInt(),
            playerX = p.getProperty("playerX", "0").toFloat(),
            playerZ = p.getProperty("playerZ", "3").toFloat(),
            yaw = p.getProperty("yaw", "-90").toFloat(),
            pitch = p.getProperty("pitch", "0").toFloat(),
            threat = p.getProperty("threat", "0").toFloat(),
            elapsedSeconds = p.getProperty("elapsedSeconds", "0").toFloat(),
            gameOver = p.getProperty("gameOver", "false").toBoolean(),
            ended = p.getProperty("ended", "false").toBoolean(),
            credits = p.getProperty("credits", "false").toBoolean(),
            clues = unpack(p.getProperty("clues", "")),
            inventory = unpack(p.getProperty("inventory", "")),
            crafted = unpack(p.getProperty("crafted", "")),
            log = unpack(p.getProperty("log", ""))
                .toMutableList()
        )
    }.getOrNull()
}
