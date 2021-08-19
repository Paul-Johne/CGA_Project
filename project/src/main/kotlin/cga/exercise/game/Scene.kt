package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.CubeMap
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.OBJLoader
import org.lwjgl.opengl.GL33.*
import org.joml.*
import org.joml.Math.toRadians
import org.lwjgl.glfw.GLFW.*
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl

class Scene(private val window: GameWindow) {

    private val debugShader : ShaderProgram
    private val skyShader : ShaderProgram

    private val skyboxTex : CubeMap

    private val debugCam : TronCamera // => UNUSED
    private val isoCam : TronCamera

    private val cubeMap : Renderable
    private val skybox : Renderable

    private val tile003BENCH : Renderable
    private val tile003GROUND : Renderable
    private val tile003TREE : Renderable
    private val tile003WALL : Renderable
    private val tile003WATER : Renderable

    private val isoCamAnchor = Transformable()
    private val isoCamAnchor2 = Transformable()
    private val skyboxRotator = Transformable()

    init {
        /* initial opengl state */
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()
        glEnable(GL_BLEND); GLError.checkThrow()
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); GLError.checkThrow()
        glBlendEquation(GL_FUNC_ADD); GLError.checkThrow()

        /* attributes for each object loaded by OBJLoader */
        val attribPositionOBJ : VertexAttribute = VertexAttribute(3, GL_FLOAT, 32, 0)
        val attribTextureOBJ : VertexAttribute = VertexAttribute(2, GL_FLOAT, 32, 12)
        val attribNormalOBJ : VertexAttribute = VertexAttribute(3, GL_FLOAT, 32, 20)
        val objAttribs = arrayOf(attribPositionOBJ, attribTextureOBJ, attribNormalOBJ)

        /* initialized ShaderPrograms */
        debugShader = ShaderProgram("assets/shaders/debug_vertex.glsl", "assets/shaders/debug_fragment.glsl")
        skyShader = ShaderProgram("assets/shaders/skybox_vert.glsl","assets/shaders/skybox_frag.glsl")

        /* BGM */
        val audioInputStream : AudioInputStream = AudioSystem.getAudioInputStream(File("assets/music/雨の上がる音が聞こえる@roku.wav"))
        val clip : Clip = AudioSystem.getClip()
        clip.open(audioInputStream)
        clip.loop(Clip.LOOP_CONTINUOUSLY)
        val gainControl : FloatControl = clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
        gainControl.value = -12.0f // decreased by 12dB => (1/4 of default volume)
        clip.start()

        /* CubeMap - Textures */
        val cubeFaces = arrayListOf<String>(
                "assets/textures/CubeMap/1left.png",
                "assets/textures/CubeMap/2right.png",
                "assets/textures/CubeMap/3bottom.png",
                "assets/textures/CubeMap/4top.png",
                "assets/textures/CubeMap/5back.png",
                "assets/textures/CubeMap/6front.png"
        )

        skyboxTex = CubeMap(cubeFaces, false)
        skyboxTex.setTexParams()

        /* CubeMap - Cube => UNUSED */
        val cubeVBO = floatArrayOf(
                // pos, pos, pos, texCoord, texCoord
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, // 0
                 0.5f, -0.5f, -0.5f, 1.0f, 0.0f, // 1
                 0.5f,  0.5f, -0.5f, 1.0f, 1.0f, // 2
                -0.5f,  0.5f, -0.5f, 0.0f, 1.0f, // 3
                -0.5f, -0.5f,  0.5f, 0.0f, 0.0f, // 4
                 0.5f, -0.5f,  0.5f, 1.0f, 0.0f, // 5
                 0.5f,  0.5f,  0.5f, 1.0f, 1.0f, // 6
                -0.5f,  0.5f,  0.5f, 0.0f, 1.0f, // 7
                -0.5f,  0.5f,  0.5f, 1.0f, 0.0f, // 8
                -0.5f,  0.5f, -0.5f, 1.0f, 1.0f, // 9
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f, // 10
                 0.5f,  0.5f,  0.5f, 1.0f, 0.0f, // 11
                 0.5f, -0.5f, -0.5f, 0.0f, 1.0f, // 12
                 0.5f, -0.5f,  0.5f, 0.0f, 0.0f, // 13
                 0.5f, -0.5f, -0.5f, 1.0f, 1.0f, // 14
                -0.5f,  0.5f,  0.5f, 0.0f, 0.0f  // 15
        )
        val cubeIBO = intArrayOf(
                0, 1, 2,
                2, 3, 0,
                4, 5, 6,
                6, 7, 4,
                8, 9, 10,
                10, 4, 8,
                11, 2, 12,
                12, 13, 11,
                10, 14, 5,
                5, 4, 10,
                3, 2, 11,
                11, 15, 3
        )

        val attribPosCube : VertexAttribute = VertexAttribute(3, GL_FLOAT, 20, 0)
        val attribTexCube : VertexAttribute = VertexAttribute(2, GL_FLOAT, 20, 12)
        val cubeAttribs = arrayOf(attribPosCube, attribTexCube)

        cubeMap = Renderable(mutableListOf(Mesh(cubeVBO, cubeIBO, cubeAttribs, null)))

        /* CubeMap - Skybox */
        val skyboxVBO = floatArrayOf(
                // pos, pos, pos
                -1.0f,  1.0f, -1.0f,//0
                -1.0f, -1.0f, -1.0f,//1
                 1.0f, -1.0f, -1.0f,//2
                 1.0f,  1.0f, -1.0f,//3
                -1.0f, -1.0f,  1.0f,//4
                -1.0f,  1.0f,  1.0f,//5
                 1.0f, -1.0f,  1.0f,//6
                 1.0f,  1.0f,  1.0f//7
        )
        val skyboxIBO = intArrayOf(
                0, 1, 2,
                2, 3, 0,
                4, 1, 0,
                0, 5, 4,
                2, 6, 7,
                7, 3, 2,
                4, 5, 7,
                7, 6, 4,
                0, 3, 7,
                7, 5, 0,
                1, 4, 2,
                2, 4, 6
        )

        val attribPosSky : VertexAttribute = VertexAttribute(3, GL_FLOAT, 12, 0)
        val skyboxAttribs = arrayOf(attribPosSky)

        skybox = Renderable(mutableListOf(Mesh(skyboxVBO, skyboxIBO, skyboxAttribs, null)))

        /* Texture2Ds for Object-Materials */
        val diffPalette01 = Texture2D("assets/textures/diffuse_palette01.png", true)
        diffPalette01.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST)
        val diffWall = Texture2D("assets/textures/diffuse_wall.png", true)
        diffWall.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST)

        val tileMat : Material = MaterialTiles(diffPalette01)
        val wallMat : Material = MaterialWall(diffWall)

        /* loaded tiles with OBJLoader */
        val tile003Res = OBJLoader.loadOBJ("assets/models/cga_tile003.obj")

        /* processed tileData */
        val tile003Data : MutableList<MutableList<OBJLoader.OBJMesh>> = mutableListOf()
        val tile003IsWall : MutableList<Boolean> = mutableListOf()

        for (data in tile003Res.objects) {
            tile003Data.add(data.meshes)
            tile003IsWall.add(data.isWall)
        }

        println("Wall-Detection: $tile003IsWall")

        // tile???Data[OBJECT][MESH] ==> MutableList<Mesh>
        val tile003MeshList : MutableList<Mesh> = mutableListOf()
        var objectCounter : Int = 0

        for (tileObject in tile003Data) {
            for((meshes, _) in tileObject.withIndex()) {

                if (tile003IsWall[objectCounter]) {
                    println("Wall detected")
                    tile003MeshList.add(Mesh(tileObject[meshes].vertexData,
                                             tileObject[meshes].indexData,
                                             objAttribs, wallMat))
                    objectCounter += 1
                } else {
                    println("Tile detected")
                    tile003MeshList.add(Mesh(tileObject[meshes].vertexData,
                                             tileObject[meshes].indexData,
                                             objAttribs, tileMat))
                    objectCounter += 1
                }

            }
        }
        objectCounter = 0 // resetting counter for next tile

        tile003BENCH = Renderable(mutableListOf(tile003MeshList[0]))
        tile003GROUND = Renderable(mutableListOf(tile003MeshList[1]))
        tile003TREE = Renderable(mutableListOf(tile003MeshList[2]))
        tile003WALL = Renderable(mutableListOf(tile003MeshList[3]))
        tile003WATER = Renderable(mutableListOf(tile003MeshList[4]))

        /* implemented camera */
        debugCam = TronCamera(parent = null)
        debugCam.rotateLocal(Math.toRadians(-35.0f), 0.0f, 0.0f)
        debugCam.translateLocal(Vector3f(0.0f, 0.0f, 10.0f))

        /* anchors */
        isoCamAnchor.scaleLocal(Vector3f(0.1f))
        isoCamAnchor.translateLocal(Vector3f(100.0f, 0.0f, 100.0f))
        isoCamAnchor.rotateLocal(0f, toRadians(45f), 0f)
        isoCamAnchor2.scaleLocal(Vector3f(0.1f))
        isoCamAnchor2.translateLocal(Vector3f(100.0f, 0.0f, -100.0f))
        isoCamAnchor2.rotateLocal(0f, toRadians(135f), 0f)

        isoCam = TronCamera(parent = isoCamAnchor)
        isoCam.rotateLocal(Math.toRadians(-35.0f), 0.0f, 0.0f)
        isoCam.translateLocal(Vector3f(.0f, 50.0f, 120.0f))
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glDepthFunc(GL_LEQUAL)
        skyShader.use()
        skyShader.setUniform("view_matrix", Matrix4f(), false)
        skyShader.setUniform("projection_matrix", skyboxRotator.modelMatrix, false)
        skyboxTex.bind(0, skyShader)
        skybox.render(skyShader)
        glDepthFunc(GL_LESS)

        debugShader.use()
        //debugCam.bind(debugShader)
        isoCam.bind(debugShader)

        tile003GROUND.render(debugShader)
        tile003WATER.render(debugShader)
        tile003BENCH.render(debugShader)
        tile003TREE.render(debugShader)
        tile003WALL.render(debugShader)
    }

    fun update(dt: Float, t: Float) {
        /* Hier wird nach der "Vorwärts/Rückwärts"-Abfrage eine weitere Abfrage gestellt, die dafür sorgt, dass solange keine Kollision zwischen LIGHTCYCLE
        * und TILEONE stattfindet, das LIGHTCYCLE vorwärts fährt. Wenn eine Kollision stattfindet, dann wird das LIGHCYCLE um 173f nach hinten versetzt. */
        /**
        if(lightCycle != null){
            if(window.getKeyState(GLFW_KEY_W)) {
                if(!detectCollision(lightCycle, tileOne, 0.5f, 1.5f, 5.0f, 5.0f)){
                    lightCycle.translateLocal(Vector3f(0f, 0f, -5f * dt))
                }
                else{
                    lightCycle.translateLocal(Vector3f(0f, 0f, 1/3f))
                }
            if(window.getKeyState(GLFW_KEY_A))
                lightCycle.rotateLocal(0f, 5f * dt, 0f)
            if(window.getKeyState(GLFW_KEY_D))
                lightCycle.rotateLocal(0f, -5f * dt, 0f)
            }
        else if(window.getKeyState(GLFW_KEY_S)) {
            if(!detectCollision(lightCycle, tileOne, 0.5f, 1.5f, 5.0f, 5.0f)){
                lightCycle.translateLocal(Vector3f(0f, 0f, 5.0f * dt))
            }
            else{
                lightCycle.translateLocal(Vector3f(0f, 0f, -1/3f))
            }
            if(window.getKeyState(GLFW_KEY_A))
                lightCycle.rotateLocal(0f, -1.5f * dt, 0f)
            if(window.getKeyState(GLFW_KEY_D))
                lightCycle.rotateLocal(0f, 1.5f * dt, 0f)
            }

        }*/

        /* rotate isometric camera */
        if(window.getKeyState(GLFW_KEY_1)) {
            isoCam.parent = isoCamAnchor
        }
        if(window.getKeyState(GLFW_KEY_2)) {
            isoCam.parent = isoCamAnchor2
        }
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    var lastMousePosX : Double = 0.0
    //var lastMousePosY : Double = 0.0

    fun onMouseMove(xpos: Double, ypos: Double) {
        /**
        //val pitch = (lastMousePosY - ypos).toFloat() * 0.001f
        val yaw = (lastMousePosX - xpos).toFloat() * 0.001f
        val roll = 0.0f

        debugCam.rotateAroundPoint(0.0f, yaw, roll, Vector3f(0.0f))
        lastMousePosX = xpos
        //lastMousePosY = ypos
        */
    }

    fun onMouseScroll(xoffset: Double, yoffset: Double) {
        isoCam.translateLocal(Vector3f(0.0f , 0.0f , -20*yoffset.toFloat()))
        skyboxRotator.rotateLocal(0.0f, Math.toRadians(10.0f), 0.0f) // better: skyboxRotator.modelMatrix = anchor.modelMatrix
    }

    fun cleanup() {
        // executed when gane is closed
        println("cleanup..")
    }

    fun detectCollision(a : Renderable, b : Renderable, a_width : Float, a_length : Float, b_width : Float, b_length : Float) : Boolean =
            (((a.getPosition().x - a_width <= b.getPosition().x + b_width) &&
              (a.getPosition().x + a_width >= b.getPosition().x - b_width)) &&
             ((a.getPosition().z - a_length <= b.getPosition().z + b_length) &&
              (a.getPosition().z + a_length >= b.getPosition().z - b_length)))
}