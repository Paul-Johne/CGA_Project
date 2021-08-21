package cga.exercise.components.camera

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.*

class TronCamera(parent : Transformable? = Transformable(), var place : Int = 0, val fov: Float = Math.toRadians(90.0f), val ratio : Float = (16f/9f), val near : Float = 0.1f, val far : Float = 100.0f): ICamera, Transformable(parent = parent) {

    override fun getCalculateViewMatrix(): Matrix4f {
        return Matrix4f().lookAt(getWorldPosition(), getWorldPosition().sub(getWorldZAxis()), getWorldYAxis())
    }

    override fun getCalculateProjectionMatrix(): Matrix4f {
        return Matrix4f().perspective(fov, ratio, near, far)
    }

    override fun bind(shader: ShaderProgram) {
        shader.setUniform("view_matrix", getCalculateViewMatrix(), false)
        shader.setUniform("projection_matrix", getCalculateProjectionMatrix(), false)
    }
}