package cga.exercise.game

import cga.exercise.components.geometry.*
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.OBJLoader
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.GL_NEAREST
import org.lwjgl.opengl.GL11.GL_REPEAT
import org.lwjgl.opengl.GL33

/**
 * ALERT: Only .obj consisting of 1 object with n meshes are allowed.
 * - objPath ==> OBJLoader.loadOBJ("assets/models/___.obj")
 */
class Player(objPath: OBJLoader.OBJResult, objAttribs : Array<VertexAttribute>, material : Material, modelMatrix: Matrix4f = Matrix4f(), parent: Transformable? = null): IRenderable, Transformable(modelMatrix, parent) {

    /** Contains meshes having vertexdata & indexdata **/
    val playerData : MutableList<Mesh> = mutableListOf()
    init {
        var i = 0
        for (data in objPath.objects) {
            val groundVertexData = objPath.objects[i].meshes[0].vertexData
            val groundIndexData = objPath.objects[i].meshes[0].indexData
            playerData.add(Mesh(groundVertexData, groundIndexData, objAttribs, material))
            i++
        }
    }


    override fun render(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("model_matrix", getLocalModelMatrix(), false)
        for (mesh in playerData) {
            mesh.render(shaderProgram)
        }
    }

}