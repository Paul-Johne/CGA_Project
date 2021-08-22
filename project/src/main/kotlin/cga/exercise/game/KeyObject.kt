package cga.exercise.game

import cga.exercise.components.geometry.*
import cga.exercise.components.shader.ShaderProgram
import cga.framework.OBJLoader
import org.joml.Matrix4f

class KeyObject(objPath: OBJLoader.OBJResult, objAttribs : Array<VertexAttribute>, material : Material, modelMatrix: Matrix4f = Matrix4f(), parent: Transformable? = null): IRenderable, Transformable(modelMatrix, parent) {

    /** Contains meshes having vertexdata & indexdata **/
    val keyObjectData : MutableList<Mesh> = mutableListOf()
    var getcarried : Boolean = false
    var atgoal : Boolean = false
    init {
        var i = 0
        for (data in objPath.objects) {
            val groundVertexData = objPath.objects[i].meshes[0].vertexData
            val groundIndexData = objPath.objects[i].meshes[0].indexData
            keyObjectData.add(Mesh(groundVertexData, groundIndexData, objAttribs, material))
            i++
        }
    }

    override fun render(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("model_matrix", getLocalModelMatrix(), false)
        for (mesh in keyObjectData) {
            mesh.render(shaderProgram)
        }
    }
}