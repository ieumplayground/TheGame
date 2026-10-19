
package com.ieum.paragrapy

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3

data class ClueObject(
    val id: String,
    val name: String,
    val description: String,
    val position: Vector3,
    val instance: ModelInstance
)

class WorldManager {
    private val builder = ModelBuilder()
    private val models = mutableListOf<Model>()
    private val objects = mutableListOf<ModelInstance>()

    val clues = mutableListOf<ClueObject>()

    val environment = Environment().apply {
        set(ColorAttribute(ColorAttribute.AmbientLight, 0.48f, 0.48f, 0.52f, 1f))
        add(
            DirectionalLight().set(
                0.7f, 0.7f, 0.68f,
                -1f, -2f, -1f
            )
        )
    }

    fun buildHouse() {
        clearWorld()

        val floor = createBox(
            12f, 0.2f, 12f,
            Color(0.25f, 0.24f, 0.22f, 1f)
        )
        objects.add(ModelInstance(floor, 0f, -0.15f, 0f))

        val wallMaterial = Color(0.34f, 0.34f, 0.33f, 1f)

        addWall(12f, 3.2f, 0.2f, 0f, 1.5f, -6f, wallMaterial)
        addWall(12f, 3.2f, 0.2f, 0f, 1.5f, 6f, wallMaterial)
        addWall(0.2f, 3.2f, 12f, -6f, 1.5f, 0f, wallMaterial)
        addWall(0.2f, 3.2f, 12f, 6f, 1.5f, 0f, wallMaterial)

        // 방 사이를 나누는 벽
        addWall(0.2f, 3f, 5f, 0f, 1.4f, -3.5f, wallMaterial)
        addWall(5f, 3f, 0.2f, -3.5f, 1.4f, 0f, wallMaterial)

        addClue(
            "clock", "멈춘 시계",
            "시계 바늘이 특정 시각에서 멈춰 있다.",
            -3f, 0.5f, -2f,
            Color(0.75f, 0.65f, 0.35f, 1f)
        )

        addClue(
            "photo", "깨진 액자",
            "사진 속 인물의 얼굴은 잘 보이지 않는다.",
            2.5f, 0.6f, -2.5f,
            Color(0.45f, 0.6f, 0.7f, 1f)
        )

        addClue(
            "note", "낯선 메모",
            "자신의 필체인지 확신할 수 없는 메모다.",
            -2.5f, 0.4f, 2f,
            Color(0.75f, 0.72f, 0.62f, 1f)
        )

        addClue(
            "blueprint", "집의 설계도",
            "설계도에는 실제로 보이지 않는 공간이 표시되어 있다.",
            3f, 0.4f, 2.5f,
            Color(0.35f, 0.55f, 0.85f, 1f)
        )
    }

    private fun addWall(
        w: Float, h: Float, d: Float,
        x: Float, y: Float, z: Float,
        color: Color
    ) {
        val model = createBox(w, h, d, color)
        objects.add(ModelInstance(model, x, y, z))
    }

    private fun addClue(
        id: String,
        name: String,
        description: String,
        x: Float, y: Float, z: Float,
        color: Color
    ) {
        val model = createBox(0.45f, 0.7f, 0.25f, color)
        val instance = ModelInstance(model, x, y, z)
        objects.add(instance)

        clues.add(
            ClueObject(
                id, name, description,
                Vector3(x, y, z), instance
            )
        )
    }

    private fun createBox(
        w: Float, h: Float, d: Float, color: Color
    ): Model {
        val material = Material(
            ColorAttribute.createDiffuse(color)
        )

        val model = builder.createBox(
            w, h, d,
            material,
            VertexAttributes.Usage.Position or
                VertexAttributes.Usage.Normal
        )

        models.add(model)
        return model
    }

    fun render(
        batch: ModelBatch,
        camera: PerspectiveCamera
    ) {
        batch.begin(camera)
        for (instance in objects) {
            batch.render(instance, environment)
        }
        batch.end()
    }

    fun nearestClue(position: Vector3): ClueObject? =
        clues.minByOrNull {
            it.position.dst(position)
        }?.takeIf {
            it.position.dst(position) < 2.2f
        }

    fun clearWorld() {
        models.forEach { it.dispose() }
        models.clear()
        objects.clear()
        clues.clear()
    }

    fun dispose() {
        clearWorld()
    }
}
