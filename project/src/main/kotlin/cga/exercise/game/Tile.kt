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
            val tile003Data : MutableList<MutableList<OBJLoader.OBJMesh>> = mutableListOf()
            val tile003IsWall : MutableList<Boolean> = mutableListOf()

            for (data in objPath.objects) {
                tile003Data.add(data.meshes)
                tile003IsWall.add(data.isWall)
            }

            var objectCounter = 0
            for (tileObject in tile003Data) {
                for((meshes, _) in tileObject.withIndex()) {
                    if (tile003IsWall[objectCounter]) {
                        println("Wall detected")
                        tileData.add(Mesh(tileObject[meshes].vertexData,
                                tileObject[meshes].indexData,
                                objAttribs, wallMat))
                        objectCounter += 1
                    } else {
                        println("Tile detected")
                        tileData.add(Mesh(tileObject[meshes].vertexData,
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