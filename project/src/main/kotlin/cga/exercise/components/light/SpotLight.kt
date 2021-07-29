package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector3i
import org.joml.Math.cos
import org.joml.Math.toRadians

class SpotLight(position: Vector3f,
                lightCol: Vector3i,
                val innerCone: Float,
                val outerCone: Float,
                parent : Transformable?) : ISpotLight, PointLight(position = position, lightCol = lightCol, parent = parent) {

    override fun bind(shaderProgram: ShaderProgram, name: String, viewMatrix: Matrix4f) {
        super.bind(shaderProgram, name)
        shaderProgram.setUniform("${name}Direction", viewMatrix.transformDirection(getWorldZAxis().negate())) //World Space Vektor -> View Space Vektor
        shaderProgram.setUniform("${name}InnerCone", cos(toRadians(innerCone)))
        shaderProgram.setUniform("${name}OuterCone", cos(toRadians(outerCone)))
    }
}