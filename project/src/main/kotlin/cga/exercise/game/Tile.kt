package cga.exercise.game

import cga.exercise.components.geometry.*
import cga.exercise.components.shader.ShaderProgram
import cga.framework.OBJLoader
import org.joml.Matrix4f

/**
 * ALERT: Only .obj consisting of 1 object with n meshes are allowed.
 * - objPath ==> OBJLoader.loadOBJ("assets/models/___.obj")
 */
class Tile(objPath: OBJLoader.OBJResult, objAttribs : Array<VertexAttribute>, tileMat : Material, wallMat : Material, modelMatrix: Matrix4f = Matrix4f(), parent: Transformable? = null): IRenderable, Transformable(modelMatrix, parent) {

    /** Contains meshes having vertexdata & indexdata **/
    val tileData : MutableList<Mesh> = mutableListOf()

    init {
        for (data in objPath.objects) {
            val tileData : MutableList<MutableList<OBJLoader.OBJMesh>> = mutableListOf()
            val tileIsWall : MutableList<Boolean> = mutableListOf()

            tileData.add(data.meshes)
            tileIsWall.add(data.isWall)

            var objectCounter = 0
            for (tileObject in tileData) {
                for((meshes, _) in tileObject.withIndex()) {
                    if (tileIsWall[objectCounter]) {
                        println("Wall detected")
                        this.tileData.add(Mesh(tileObject[meshes].vertexData,
                                tileObject[meshes].indexData,
                                objAttribs, wallMat))
                        objectCounter += 1
                    } else {
                        println("Tile detected")
                        this.tileData.add(Mesh(tileObject[meshes].vertexData,
                                tileObject[meshes].indexData,
                                objAttribs, tileMat))
                        objectCounter += 1
                    }
                }
            }
        }
    }

    override fun render(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("model_matrix", getLocalModelMatrix(), false)
        for (mesh in tileData) {
            mesh.render(shaderProgram)
        }
    }
}