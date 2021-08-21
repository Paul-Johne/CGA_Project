package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.shader.ShaderProgramGeometry
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector3i

sealed class Material () {
    abstract fun bind(shaderProgram: ShaderProgram)
}

class MaterialInternship(var diff: Texture2D,
                         var emit: Texture2D,
                         var specular: Texture2D,
                         var shininess: Float = 50.0f,
                         var tcMultiplier : Vector2f = Vector2f(1.0f),
                         var emitColor : Vector3i = Vector3i(255)) : Material() {  //ermöglicht Angabe zur Wiederholung einer Textur in s und t Richtung

    override fun bind(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("tcMultiplier", tcMultiplier)

        diff.bind(0)
        emit.bind(1)
        specular.bind(2)

        shaderProgram.setUniform("diffTex", 0)
        shaderProgram.setUniform("emitTex",1)
        shaderProgram.setUniform("specTex", 2)
        shaderProgram.setUniform("shininess", shininess)
        shaderProgram.setUniform("emitColor", Vector3f(emitColor).mul(1f/255f))
    }

    fun unbind() {
        diff.unbind()
        emit.unbind()
        specular.unbind()
    }
}

class MaterialTiles(var diffPalette: Texture2D,
                    var tcMultiplier: Vector2f = Vector2f(1.0f)) : Material() {

    override fun bind(shaderProgram: ShaderProgram) {
        diffPalette.bind(0) // same textureUnit as MaterialWall (before Normal Mapping)

        shaderProgram.setUniform("diffPalette", 0)
        shaderProgram.setUniform("tcMultiplier", tcMultiplier)
    }

    fun unbind() {
        diffPalette.unbind()
    }
}

class MaterialWall(var diffWall: Texture2D,
                   var normWall: Texture2D,
                   var tcMultiplier: Vector2f = Vector2f(1.0f)) : Material() {

    override fun bind(shaderProgram: ShaderProgram) {
        diffWall.bind(0) // same textureUnit as MaterialTiles (before Normal Mapping)
        shaderProgram.setUniform("diffWall", 0)

        if (shaderProgram is ShaderProgramGeometry) {
            normWall.bind(1)
            shaderProgram.setUniform("normWall", 1)
        }

        shaderProgram.setUniform("tcMultiplier", tcMultiplier)
    }

    fun unbind() {
        diffWall.unbind()
        normWall.unbind()
    }
}