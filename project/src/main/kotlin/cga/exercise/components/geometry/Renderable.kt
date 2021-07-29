package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

class Renderable(val meshes: MutableList<Mesh>, modelMatrix: Matrix4f = Matrix4f(), parent: Transformable? = null): IRenderable, Transformable(modelMatrix, parent) {

    override fun render(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("model_matrix", getLocalModelMatrix(), false)
        for (mesh in meshes) {
            mesh.render(shaderProgram)
        }
    }
}