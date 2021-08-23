package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.*
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
import java.util.*
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl

class Scene(private val window: GameWindow) {

    private val debugShader : ShaderProgram
    private val skyShader : ShaderProgram
    private val wallShader : ShaderProgram

    private val skyboxTex : CubeMap

    private val debugCam : TronCamera // => UNUSED
    private val isoCam : TronCamera

    private val skybox : Renderable

    private val isoCamAnchor = Transformable()
    private val isoCamAnchor2 = Transformable()
    private val isoCamAnchor3 = Transformable()
    private val isoCamAnchor4 = Transformable()
    private val isoCamList = mutableListOf<Transformable>()
    private val skyboxRotator = Transformable()

    private val player : Player
    private val tile001 : Tile
    private val tile002 : Tile
    private val tile003 : Tile
    private val tile004 : Tile
    private val tile005 : Tile
    private val tile006 : Tile
    private val tile007 : Tile
    private val tile008 : Tile
    private var tileList = mutableListOf<Tile?>()

    private val empty : EmptySpot
    private val keyObj : KeyObject
    private val keyObjGoal : KeyObject
    private val arrowNegZ : Player
    private val arrowPosX : Player
    private val arrowPosZ : Player
    private val arrowNegX : Player

    private val pointLight : PointLight

    init {
        /* initial opengl state */
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        /* attributes for each object loaded by OBJLoader */
        val attribPositionOBJ : VertexAttribute = VertexAttribute(3, GL_FLOAT, 32, 0)
        val attribTextureOBJ : VertexAttribute = VertexAttribute(2, GL_FLOAT, 32, 12)
        val attribNormalOBJ : VertexAttribute = VertexAttribute(3, GL_FLOAT, 32, 20)
        val objAttribs = arrayOf(attribPositionOBJ, attribTextureOBJ, attribNormalOBJ)

        /* initialized ShaderPrograms */
        debugShader = ShaderProgramStandard("assets/shaders/debug_vertex.glsl", "assets/shaders/debug_fragment.glsl")
        skyShader = ShaderProgramStandard("assets/shaders/skybox_vert.glsl","assets/shaders/skybox_frag.glsl")
        wallShader = ShaderProgramGeometry("assets/shaders/wall_vertex.glsl", "assets/shaders/wall_geometry.glsl", "assets/shaders/wall_fragment.glsl")

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
        val normWall = Texture2D("assets/textures/normal_wall.png", true)
        normWall.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST)
        val cgaSpec = Texture2D("assets/textures/spec_wall.png", true)
        normWall.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST)

        val tileMat = MaterialTiles(diffPalette01)
        val wallMat = MaterialWall(diffWall, normWall, cgaSpec)

        /* implemented camera */
        debugCam = TronCamera(parent = null)
        debugCam.rotateLocal(Math.toRadians(-35.0f), 0.0f, 0.0f)
        debugCam.translateLocal(Vector3f(0.0f, 0.0f, 10.0f))

        /* anchors & isometric camera */
        isoCamAnchor.scaleLocal(Vector3f(0.1f))
        isoCamAnchor.translateLocal(Vector3f(100.0f, 0.0f, 100.0f))
        isoCamAnchor.rotateLocal(0f, toRadians(45f), 0f)
        isoCamAnchor2.scaleLocal(Vector3f(0.1f))
        isoCamAnchor2.translateLocal(Vector3f(100.0f, 0.0f, -100.0f))
        isoCamAnchor2.rotateLocal(0f, toRadians(135f), 0f)
        isoCamAnchor3.scaleLocal(Vector3f(0.1f))
        isoCamAnchor3.translateLocal(Vector3f(-100.0f, 0.0f, -100.0f))
        isoCamAnchor3.rotateLocal(0f, toRadians(225f), 0f)
        isoCamAnchor4.scaleLocal(Vector3f(0.1f))
        isoCamAnchor4.translateLocal(Vector3f(-100.0f, 0.0f, 100.0f))
        isoCamAnchor4.rotateLocal(0f, toRadians(315f), 0f)
        isoCamList.add(isoCamAnchor)
        isoCamList.add(isoCamAnchor2)
        isoCamList.add(isoCamAnchor3)
        isoCamList.add(isoCamAnchor4)

        isoCam = TronCamera(parent = isoCamList[0], place = 0)
        isoCam.rotateLocal(Math.toRadians(-35.0f), 0.0f, 0.0f)
        isoCam.translateLocal(Vector3f(.0f, 50.0f, 120.0f))

        /* tiles */
        tile001 = Tile(OBJLoader.loadOBJ("assets/models/cga_tile001.obj"), objAttribs, tileMat, wallMat)
        tile002 = Tile(OBJLoader.loadOBJ("assets/models/cga_tile002.obj"), objAttribs, tileMat, wallMat)
        tile003 = Tile(OBJLoader.loadOBJ("assets/models/cga_tile003.obj"), objAttribs, tileMat, wallMat)
        tile004 = Tile(OBJLoader.loadOBJ("assets/models/cga_tile004.obj"), objAttribs, tileMat, wallMat)
        tile005 = Tile(OBJLoader.loadOBJ("assets/models/cga_tile005.obj"), objAttribs, tileMat, wallMat)
        tile006 = Tile(OBJLoader.loadOBJ("assets/models/cga_tile006.obj"), objAttribs, tileMat, wallMat)
        tile007 = Tile(OBJLoader.loadOBJ("assets/models/cga_tile007.obj"), objAttribs, tileMat, wallMat)
        tile008 = Tile(OBJLoader.loadOBJ("assets/models/cga_tile008.obj"), objAttribs, tileMat, wallMat)
        tile001.translateLocal(Vector3f(-10f, 0f, -10f))
        tile002.translateLocal(Vector3f(0f, 0f, -10f))
        tile003.translateLocal(Vector3f(10f, 0f, -10f))
        tile004.translateLocal(Vector3f(-10f, 0f, 0f))
        tile005.translateLocal(Vector3f(0f, 0f, 0f))
        tile006.translateLocal(Vector3f(10f, 0f, 0f))
        tile007.translateLocal(Vector3f(-10f, 0f, 10f))
        tile008.translateLocal(Vector3f(0f, 0f, 10f))
        tileList.add(tile001)
        tileList.add(tile002)
        tileList.add(tile003)
        tileList.add(tile004)
        tileList.add(tile005)
        tileList.add(tile006)
        tileList.add(tile007)
        tileList.add(tile008)
        tileList.add(null)

        empty = EmptySpot(tileList)

        /* player */
        player = Player(OBJLoader.loadOBJ("assets/models/cga_player.obj"), objAttribs, tileMat)
        player.translateLocal(Vector3f(0f, 1f, 0f))

        keyObj = KeyObject(OBJLoader.loadOBJ("assets/models/cga_key.obj"), objAttribs, tileMat)
        keyObj.translateLocal(Vector3f(10f, -3f, 0f))
        
        keyObjGoal = KeyObject(OBJLoader.loadOBJ("assets/models/cga_keygoal.obj"), objAttribs, tileMat)
        keyObjGoal.translateLocal(Vector3f(-1f, 0.7f, 13.5f))

        arrowNegZ = Player(OBJLoader.loadOBJ("assets/models/cga_arrow.obj"), objAttribs, tileMat)
        arrowPosX = Player(OBJLoader.loadOBJ("assets/models/cga_arrow.obj"), objAttribs, tileMat)
        arrowPosZ = Player(OBJLoader.loadOBJ("assets/models/cga_arrow.obj"), objAttribs, tileMat)
        arrowNegX = Player(OBJLoader.loadOBJ("assets/models/cga_arrow.obj"), objAttribs, tileMat)

        arrowPosZ.translateLocal(Vector3f(10f, -0.1f, 6f))
        arrowPosZ.rotateLocal(0f, toRadians(90f), 0f)
        arrowPosX.translateLocal(Vector3f(6f, -0.1f, 10f))
        arrowPosX.rotateLocal(0f, toRadians(180f), 0f)
        arrowNegZ.translateLocal(Vector3f(10f, -0.1f, 4f))
        arrowNegZ.rotateLocal(0f, toRadians(270f), 0f)
        arrowNegX.translateLocal(Vector3f(4f, -0.1f, 10f))
        
        /* PointLight for Normal Mapping*/
        pointLight = PointLight(Vector3f(0.0f, 0.0f, 0.0f), Vector3i(100, 100, 0), parent = isoCam.parent)
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glDepthFunc(GL_LEQUAL)
        skyShader.use()
        skyShader.setUniform("view_matrix", skyboxRotator.modelMatrix, false)
        skyShader.setUniform("projection_matrix", Matrix4f(), false)
        skyboxTex.bind(0, skyShader)
        skybox.render(skyShader)
        glDepthFunc(GL_LESS)

        debugShader.use()
        isoCam.bind(debugShader)

        player.render(debugShader)

        keyObj.render(debugShader)
        keyObjGoal.render(debugShader)

        arrowNegX.render(debugShader)
        arrowNegZ.render(debugShader)
        arrowPosZ.render(debugShader)
        arrowPosX.render(debugShader)

        wallShader.use()
        pointLight.bind(wallShader, 0)
        isoCam.bind(wallShader)

        tile001.render(wallShader)
        tile002.render(wallShader)
        tile003.render(wallShader)
        tile004.render(wallShader)
        tile005.render(wallShader)
        tile006.render(wallShader)
        tile007.render(wallShader)
        tile008.render(wallShader)
    }

    fun update(dt: Float, t: Float) {
        /* player movement, key follows player if it gets carried*/
        if(window.getKeyState(GLFW_KEY_W)) {
            player.translateLocal(Vector3f(-5f* dt, 0f, 0f))
            if(keyObj.getcarried) {
                keyObj.translateGlobal(Vector3f(player.getPosition().x-keyObj.getPosition().x, player.getPosition().y-keyObj.getPosition().y, player.getPosition().z-keyObj.getPosition().z))
            }
            if(window.getKeyState(GLFW_KEY_A))
                player.rotateLocal(0f, 5f * dt, 0f)
            if(window.getKeyState(GLFW_KEY_D))
                player.rotateLocal(0f, -5f * dt, 0f)
        }
        if(window.getKeyState(GLFW_KEY_S)) {
            player.translateLocal(Vector3f(1.5f * dt, 0f, 0f))
            if(keyObj.getcarried) {
                keyObj.translateGlobal(Vector3f(player.getPosition().x-keyObj.getPosition().x, player.getPosition().y-keyObj.getPosition().y, player.getPosition().z-keyObj.getPosition().z))
            }
            if(window.getKeyState(GLFW_KEY_A))
                player.rotateLocal(0f, -1.5f * dt, 0f)
            if(window.getKeyState(GLFW_KEY_D))
                player.rotateLocal(0f, 1.5f * dt, 0f)
        }

        /* automatic key rotation*/
        keyObj.rotateLocal(0f, -1.5f * dt, 0f)
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {
        /* rotate isometric camera */
        if(window.getKeyState(GLFW_KEY_1)) {
            if (isoCam.place < 3) {
                isoCam.place++
            } else {
                isoCam.place = 0
            }
            isoCam.parent = isoCamList[isoCam.place]
            pointLight.parent = isoCam.parent
        }
        if(window.getKeyState(GLFW_KEY_2)) {
            if (isoCam.place != 0) {
                isoCam.place--
            } else {
                isoCam.place = 3
            }
            isoCam.parent = isoCamList[isoCam.place]
            pointLight.parent = isoCam.parent
        }

        if(window.getKeyState(GLFW_KEY_C)){
            wallShader.setUniform("shaderType",0)
        }
        if(window.getKeyState(GLFW_KEY_X)){
            wallShader.setUniform("shaderType",1)
        }

        /* tile movement */
        //empty nach Norden
        if (window.getKeyState(GLFW_KEY_G)) {
            var i = 0
            for (data in tileList) {
                if (tileList[i] == empty.neighbourNegZ && tileList[i] != null) {
                    tileList[i]?.translateLocal(Vector3f(0f, 0f, 10f))
                    /* keyObj and keyObjGoal follow their tile movement*/
                    if(!keyObj.getcarried && tileList[i] == tile006) {
                        keyObj.translateGlobal(Vector3f(0f, 0f, 10f))
                    }
                    if (tileList[i] == tile008) {
                        keyObjGoal.translateGlobal(Vector3f(0f, 0f, 10f))
                    }
                    tileList = empty.moveNegZ(i)
                    /* arrow placement */
                    if(i != 2 && i != 1 && i != 0) {
                        arrowPosZ.translateLocal(Vector3f(10f, 0f, 0f))
                    }
                    if(i != 3 && i != 4 && i != 5) {
                        arrowNegZ.translateLocal(Vector3f(-10f, 0f, 0f))
                    }
                    arrowPosX.translateLocal(Vector3f(0f, 0f, 10f))
                    arrowNegX.translateLocal(Vector3f(0f, 0f, -10f))
                    return
                }
                i++
            }
        }
        //empty nach Süden
        if (window.getKeyState(GLFW_KEY_T)) {
            var i = 0
            for (data in tileList) {
                if (tileList[i] == empty.neighbourPosZ  && tileList[i] != null) {
                    tileList[i]?.translateLocal(Vector3f(0f, 0f, -10f))
                    /* keyObj and keyObjGoal follow their tile movement*/
                    if(!keyObj.getcarried && tileList[i] == tile006) {
                        keyObj.translateGlobal(Vector3f(0f, 0f, -10f))
                    }
                    if (tileList[i] == tile008) {
                        keyObjGoal.translateGlobal(Vector3f(0f, 0f, -10f))
                    }
                    tileList = empty.movePosZ(i)
                    /* arrow placement */
                    if(i != 6 && i != 7 && i != 8) {
                        arrowNegZ.translateLocal(Vector3f(10f, 0f, 0f))
                    }
                    if(i != 3 && i != 4 && i != 5) {
                        arrowPosZ.translateLocal(Vector3f(-10f, 0f, 0f))
                    }
                    arrowPosX.translateLocal(Vector3f(0f, 0f, -10f))
                    arrowNegX.translateLocal(Vector3f(0f, 0f, 10f))
                    return
                }
                i++
            }
        }
        //empty nach Westen
        if (window.getKeyState(GLFW_KEY_H)) {
            var i = 0
            for (data in tileList) {
                if (tileList[i] == empty.neighbourNegX && tileList[i] != null) {
                    tileList[i]?.translateLocal(Vector3f(10f, 0f, 0f))
                    /* keyObj and keyObjGoal follow their tile movement*/
                    if(!keyObj.getcarried && tileList[i] == tile006) {
                        keyObj.translateGlobal(Vector3f(10f, 0f, 0f))
                    }
                    if (tileList[i] == tile008) {
                        keyObjGoal.translateGlobal(Vector3f(10f, 0f, 0f))
                    }
                    tileList = empty.moveNegX(i)
                    /* arrow placement */
                    if(i != 0 && i != 3 && i != 6) {
                        arrowPosX.translateLocal(Vector3f(10f, 0f, 0f))
                    }
                    if(i != 1 && i != 4 && i != 7) {
                        arrowNegX.translateLocal(Vector3f(-10f, 0f, 0f))
                    }
                    arrowPosZ.translateLocal(Vector3f(0f, 0f, -10f))
                    arrowNegZ.translateLocal(Vector3f(0f, 0f, 10f))
                    return
                }
                i++
            }
        }

        //empty nach Osten
        if (window.getKeyState(GLFW_KEY_F)) {
            var i = 0
            for (data in tileList) {
                if (tileList[i] == empty.neighbourPosX && tileList[i] != null) {
                    tileList[i]?.translateLocal(Vector3f(-10f, 0f, 0f))
                    /* keyObj and keyObjGoal follow their tile movement*/
                    if(!keyObj.getcarried && tileList[i] == tile006) {
                        keyObj.translateGlobal(Vector3f(-10f, 0f, 0f))
                    }
                    if (tileList[i] == tile008) {
                        keyObjGoal.translateGlobal(Vector3f(-10f, 0f, 0f))
                    }
                    tileList = empty.movePosX(i)
                    /* arrow placement */
                    if(i != 2 && i != 5 && i != 8) {
                        arrowNegX.translateLocal(Vector3f(10f, 0f, 0f))
                    }
                    if(i != 1 && i != 4 && i != 7) {
                        arrowPosX.translateLocal(Vector3f(-10f, 0f, 0f))
                    }
                    arrowPosZ.translateLocal(Vector3f(0f, 0f, 10f))
                    arrowNegZ.translateLocal(Vector3f(0f, 0f, -10f))
                    return
                }
                i++
            }
        }

        /* keyObject */
        if(window.getKeyState(GLFW_KEY_E)) {
            if(keyObj.getcarried) {
                if(player.getPosition().x <= keyObjGoal.getPosition().x+1.5 && player.getPosition().x >= keyObjGoal.getPosition().x-1.5 && player.getPosition().z <= keyObjGoal.getPosition().z+1.5 && player.getPosition().z >= keyObjGoal.getPosition().z-1.5) {
                    keyObj.getcarried = false
                    keyObj.atgoal = true
                    keyObj.translateGlobal(Vector3f(0f, -3f, 0f))
                }

            } else {
                if(!keyObj.atgoal && player.getPosition().x <= keyObj.getPosition().x+1.5 && player.getPosition().x >= keyObj.getPosition().x-1.5 && player.getPosition().z <= keyObj.getPosition().z+1.5 && player.getPosition().z >= keyObj.getPosition().z-1.5) {
                    keyObj.getcarried = true
                    //keyObj.modelMatrix = player.modelMatrix
                    keyObj.translateGlobal(Vector3f(player.getPosition().x-keyObj.getPosition().x, player.getPosition().y-keyObj.getPosition().y, player.getPosition().z-keyObj.getPosition().z))
                }
            }
        }


    }

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