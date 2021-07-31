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
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl

class Scene(private val window: GameWindow) {

    // scene setup
    init {
        /* initial opengl state */
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        /* attributes for each object loaded by OBJLoader */
        val attribPositionObj : VertexAttribute = VertexAttribute(3, GL_FLOAT, 32, 0)
        val attribTextureObj : VertexAttribute = VertexAttribute(2, GL_FLOAT, 32, 12)
        val attribNormalObj : VertexAttribute = VertexAttribute(3, GL_FLOAT, 32, 20)
        val objAttribs = arrayOf(attribPositionObj, attribTextureObj, attribNormalObj)

        /* BGM */
        val audioInputStream : AudioInputStream = AudioSystem.getAudioInputStream(File("assets/music/雨の上がる音が聞こえる@roku.wav"))
        val clip : Clip = AudioSystem.getClip()
        clip.open(audioInputStream)
        clip.loop(Clip.LOOP_CONTINUOUSLY)
        val gainControl : FloatControl = clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
        gainControl.value = -12.0f // decreased by 12dB => (1/4 of default volume)
        clip.start()
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        // shader.use, light.bind and mesh.render
    }

    fun update(dt: Float, t: Float) {
        /**
        if(window.getKeyState(GLFW_KEY_W)) {
            //someTransformation
            if(window.getKeyState(GLFW_KEY_A))
                //someRotation
            if(window.getKeyState(GLFW_KEY_D))
                //someRotation
        }
        if(window.getKeyState(GLFW_KEY_S)) {
            //someTransformation
            if(window.getKeyState(GLFW_KEY_A))
                //someRotation
            if(window.getKeyState(GLFW_KEY_D))
                //someRotation
        }*/

        // add changes due to time here
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    var lastMousePosX : Double = 0.0
    var lastMousePosY : Double = 0.0

    fun onMouseMove(xpos: Double, ypos: Double) {
        //val pitch = (lastMousePosY - ypos).toFloat() * 0.001f
        //val yaw = (lastMousePosX - xpos).toFloat() * 0.001f
        //val roll = 0.0f

        //KAMERA.rotateAroundPoint(0.0f, yaw, roll, Vector3f(0.0f))
        //lastMousePosX = xpos
        //lastMousePosY = ypos
    }

    fun cleanup() {
        // executed when gane is closed
        println("cleanup..")
    }
}