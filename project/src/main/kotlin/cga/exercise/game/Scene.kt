package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import org.lwjgl.opengl.GL33.*
import org.joml.*
import org.joml.Math.toRadians
import org.lwjgl.glfw.GLFW.*
import java.awt.Point
import javax.xml.crypto.dsig.Transform
import kotlin.system.exitProcess

/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    //private val staticShader: ShaderProgram
    private val tronShader: ShaderProgram

    //private val firstMesh : Mesh
    //private val pjMesh : Mesh
    //private val krMesh : Mesh

    //private val sphereMesh : Mesh
    private val groundMesh : Mesh

    //private val translSphere = Matrix4f() // identity matrix
    //private val translGround = Matrix4f() // identity matrix

    //private val ground : Transformable = Transformable()
    //private val sphere : Transformable = Transformable(parent = ground)
    private val ground : Renderable
    //private val sphere : Renderable
    private val lightCycle : Renderable?

    private val tronCam : TronCamera

    private val pointLight : PointLight
    private val spotLight : SpotLight

    private val pointLightEdge1 : PointLight
    private val pointLightEdge2 : PointLight
    private val pointLightEdge3 : PointLight
    private val pointLightEdge4 : PointLight

    // scene setup
    init {
        //staticShader = ShaderProgram("assets/shaders/simple_vert.glsl", "assets/shaders/simple_frag.glsl")
        tronShader = ShaderProgram("assets/shaders/tron_vert.glsl","assets/shaders/tron_frag.glsl")

        /* initial opengl state */
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        val attribPosition1 : VertexAttribute = VertexAttribute(3, GL_FLOAT, 24, 0)
        val attribColor1 : VertexAttribute = VertexAttribute(3, GL_FLOAT, 24, 12)
        val posAndColorAttributes = arrayOf(attribPosition1, attribColor1)

        val firstVertices : FloatArray = floatArrayOf(
            -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f,  0.5f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.0f,  1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            -0.5f,  0.5f, 0.0f, 0.0f, 1.0f, 0.0f)

        val firstIndices : IntArray = intArrayOf(
            0, 1, 2,
            0, 2, 4,
            4, 2, 3)

        val pjVertices : FloatArray = floatArrayOf(
            -0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 1.0f, // 0
            -0.4f, -0.5f, 0.0f, 1.0f, 1.0f, 1.0f, // 1
            -0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,  // 2
            -0.4f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,  // 3
            -0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 1.0f,  // 4
            -0.4f, 0.5f, 0.0f, 1.0f, 1.0f, 1.0f,  // 5
            -0.4f, 0.4f, 0.0f, 1.0f, 1.0f, 1.0f,  // 6
            -0.1f, 0.4f, 0.0f, 1.0f, 1.0f, 1.0f,  // 7
            -0.1f, 0.5f, 0.0f, 1.0f, 1.0f, 1.0f,  // 8
            0.0f, 0.5f, 0.0f, 1.0f, 1.0f, 1.0f,   // 9
            0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,   //10
            -0.1f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,  //11
            -0.4f, 0.1f, 0.0f, 1.0f, 1.0f, 1.0f,  //12
            -0.1f, 0.1f, 0.0f, 1.0f, 1.0f, 1.0f,  //13
            0.0f, -0.2f, 0.0f, 1.0f, 1.0f, 1.0f,  //14
            0.1f, -0.2f, 0.0f, 1.0f, 1.0f, 1.0f,  //15
            0.0f, -0.5f, 0.0f, 1.0f, 1.0f, 1.0f,  //16
            0.1f, -0.5f, 0.0f, 1.0f, 1.0f, 1.0f,  //17
            0.1f, -0.4f, 0.0f, 1.0f, 1.0f, 1.0f,  //18
            0.5f, -0.4f, 0.0f, 1.0f, 1.0f, 1.0f,  //19
            0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 1.0f,  //20
            0.4f, -0.4f, 0.0f, 1.0f, 1.0f, 1.0f,  //21
            0.4f, 0.5f, 0.0f, 1.0f, 1.0f, 1.0f,   //22
            0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 1.0f    //23
        )

        val pjIndices : IntArray = intArrayOf(
            0, 1, 2,
            2, 1, 3,
            2, 3, 4,
            4, 3, 5,
            5, 6, 7,
            5, 7, 8,
            8, 10, 9,
            8, 11, 10,
            3, 11, 12,
            12, 11, 13,
            14, 16, 15,
            15, 16, 17,
            18, 17, 19,
            17, 20, 19,
            21, 19, 22,
            22, 19, 23
        )

        val krVertices : FloatArray = floatArrayOf(

            -0.25f, 0.5f, 0.0f, 1.0f, 1.0f, 1.0f, //0
            -0.75f, 0.5f, 0.0f, 1.0f, 1.0f, 1.0f, //1
            -0.75f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, //2
            -0.75f, -0.5f, 0.0f, 1.0f, 1.0f, 1.0f, //3
             -0.25f, -0.5f, 0.0f, 1.0f, 1.0f, 1.0f, //4
            0.25f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, //5
            0.75f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, //6
            0.75f, 0.5f, 0.0f, 1.0f, 1.0f, 1.0f, //7
            0.25f, 0.5f, 0.0f, 1.0f, 1.0f, 1.0f, //8
            0.25f, -0.5f, 0.0f, 1.0f, 1.0f, 1.0f, //9
            0.75f, -0.5f, 0.0f, 1.0f, 1.0f, 1.0f  //10
        )

        val krIndices : IntArray = intArrayOf(
                0, 1, 2,
                2, 3, 4,
                5, 6, 7,
                7, 8, 5,
                5, 9, 10
        )

        //firstMesh = Mesh(firstVertices, firstIndices, posAndColorAttributes)
        //pjMesh = Mesh(pjVertices, pjIndices, posAndColorAttributes)
        //krMesh = Mesh(krVertices, krIndices, posAndColorAttributes)

        /* attributes for each object loaded by OBJLoader */
        val attribPositionObj : VertexAttribute = VertexAttribute(3, GL_FLOAT, 32, 0)
        val attribTextureObj : VertexAttribute = VertexAttribute(2, GL_FLOAT, 32, 12)
        val attribNormalObj : VertexAttribute = VertexAttribute(3, GL_FLOAT, 32, 20)
        val objAttrib = arrayOf(attribPositionObj, attribTextureObj, attribNormalObj)

        /* SPHERE */
        //val sphereRes : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/sphere.obj")
        //val sphereObjMeshList : MutableList<OBJLoader.OBJMesh> = sphereRes.objects[0].meshes

        //val sphereVertexData = sphereObjMeshList[0].vertexData
        //val sphereIndexData = sphereObjMeshList[0].indexData

        //sphereMesh = Mesh(sphereVertexData, sphereIndexData, objAttrib)

        /* 3.5 */
        val diffTex = Texture2D("assets/textures/ground_diff.png",true)
        diffTex.setTexParams(GL_REPEAT,GL_REPEAT, GL_NEAREST, GL_NEAREST)
        val emitTex = Texture2D("assets/textures/ground_emit.png", true)
        emitTex.setTexParams(GL_REPEAT,GL_REPEAT, GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST) //GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR
        val specTex =  Texture2D("assets/textures/ground_spec.png",true)
        specTex.setTexParams(GL_REPEAT,GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val groundMat = Material(diffTex, emitTex, specTex, 60.0f, Vector2f(64.0f), Vector3i(0, 255, 0))

        /* 3.6 */
        lightCycle = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", toRadians(-90.0f),toRadians(90.0f),0.0f)
        if(lightCycle == null) {
            System.err.println("Can't load light Cycle")
            exitProcess(1)
        }

        lightCycle.scaleLocal(Vector3f(0.8f))

        /* GROUND */
        val groundRes : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj")
        val groundObjMeshList : MutableList<OBJLoader.OBJMesh> = groundRes.objects[0].meshes

        val groundVertexData = groundObjMeshList[0].vertexData
        val groundIndexData = groundObjMeshList[0].indexData

        groundMesh = Mesh(groundVertexData, groundIndexData, objAttrib, groundMat) //Material groundMat zugewiesen

        /* Instantiate Translation Matrices */
        ground = Renderable(mutableListOf(groundMesh))
        //sphere = Renderable(mutableListOf(sphereMesh), parent = ground)

        /* Translation of the objects */
        /*translSphere.scale(Vector3f(0.5f))

        translGround.rotate(Math.toRadians(90.0f), Vector3f(1.0f,0.0f,0.0f))
        translGround.scale(Vector3f(0.03f))*/

        /* Scene Graph */
        //sphere.scaleLocal(Vector3f(0.5f))

        //ground.rotateLocal(Math.toRadians(90.0f),0.0f,0.0f)
        //ground.scaleLocal(Vector3f(0.03f))

        /* Camera */
        tronCam = TronCamera(parent = lightCycle)
        tronCam.rotateLocal(Math.toRadians(-35.0f), 0.0f, 0.0f)
        tronCam.translateLocal(Vector3f(0.0f, 0.0f, 4.0f))

        /* 4. */
        pointLight = PointLight(Vector3f(0.0f, 1.0f, 0.0f), parent = lightCycle)
        pointLightEdge1 = PointLight(Vector3f(-20.0f, 1.0f, 20.0f), Vector3i(255, 255, 255), parent = null)
        pointLightEdge2 = PointLight(Vector3f(-20.0f, 1.0f, -20.0f), Vector3i(255, 255, 255), parent = null)
        pointLightEdge3 = PointLight(Vector3f(20.0f, 1.0f, -20.0f), Vector3i(255, 255, 255), parent = null)
        pointLightEdge4 = PointLight(Vector3f(20.0f, 1.0f, 20.0f), Vector3i(255, 255, 255), parent = null)

        spotLight = SpotLight(Vector3f(0.0f, 1.0f, -1.0f), Vector3i(255, 255, 255), 20.0f, 45.0f, parent = lightCycle)
        //spotLight.rotateLocal(toRadians(-25.0f), 0.0f, 0.0f)
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        /*
        staticShader.use()

        firstMesh.render()
        pjMesh.render()
        krMesh.render()
        */

        tronShader.use()

        tronCam.bind(tronShader)

        /*
        tronShader.setUniform("model_matrix",ground.getLocalModelMatrix(),false) // translGround
        groundMesh.render()

        tronShader.setUniform("model_matrix",sphere.getLocalModelMatrix(),false) // translSphere
        sphereMesh.render()
         */

        pointLight.bind(tronShader, 0)
        pointLightEdge1.bind(tronShader, 1)
        pointLightEdge2.bind(tronShader, 2)
        pointLightEdge3.bind(tronShader, 3)
        pointLightEdge4.bind(tronShader, 4)

        spotLight.bind(tronShader, "spotLight", tronCam.getCalculateViewMatrix())

        ground.render(tronShader) //FRAGE : ground und lightCycle jeweils einmal Renderpipeline durchlaufen?
        lightCycle?.render(tronShader)
    }

    fun update(dt: Float, t: Float) {
        if(window.getKeyState(GLFW_KEY_W)) {
            lightCycle?.translateLocal(Vector3f(0f, 0f, -5f * dt))
            if(window.getKeyState(GLFW_KEY_A))
                lightCycle?.rotateLocal(0f, 5f * dt, 0f)
            if(window.getKeyState(GLFW_KEY_D))
                lightCycle?.rotateLocal(0f, -5f * dt, 0f)
        }
        if(window.getKeyState(GLFW_KEY_S)) {
            lightCycle?.translateLocal(Vector3f(0f, 0f, 1.5f * dt))
            if(window.getKeyState(GLFW_KEY_A))
                lightCycle?.rotateLocal(0f, -1.5f * dt, 0f)
            if(window.getKeyState(GLFW_KEY_D))
                lightCycle?.rotateLocal(0f, 1.5f * dt, 0f)
        }

        val redPart = ((Math.sin(t) + 1.0f)/2 * 255).toInt()
        val greenPart = ((Math.sin(t * 2) + 1f)/2 * 255).toInt()
        val bluePart = ((Math.sin(t * 3) + 1f)/2 * 255).toInt()

        lightCycle?.meshes?.get(2)?.material?.emitColor = Vector3i(redPart, greenPart, bluePart)
        pointLight.lightCol = Vector3i(redPart, greenPart, bluePart)
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    var lastMousePosX : Double = 0.0
    var lastMousePosY : Double = 0.0

    fun onMouseMove(xpos: Double, ypos: Double) {
        //val pitch = (lastMousePosY - ypos).toFloat() * 0.001f // 0.002f
        val yaw = (lastMousePosX - xpos).toFloat() * 0.001f // 0.002f
        val roll = 0.0f

        tronCam.rotateAroundPoint(0.0f, yaw, roll, Vector3f(0.0f))
        lastMousePosX = xpos
        lastMousePosY = ypos
    }

    fun cleanup() {}
}