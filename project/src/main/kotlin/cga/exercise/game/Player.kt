package cga.exercise.game

import cga.framework.OBJLoader

/**
 * ALERT: Only .obj consisting of 1 object with n meshes are allowed.
 * - objPath ==> OBJLoader.loadOBJ("assets/models/___.obj")
 */
class Player(objPath: OBJLoader.OBJResult) {

    /** Contains meshes having vertexdata & indexdata **/
    val meshList = mutableListOf<OBJLoader.OBJMesh>()
    init {
        for (mesh in objPath.objects[0].meshes)
            meshList.add(mesh)
    }
}