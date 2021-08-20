package cga.exercise.components.shader

import org.joml.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL33.*
import java.nio.FloatBuffer
import java.nio.file.Files
import java.nio.file.Paths

sealed class ShaderProgram() {
    abstract fun use()
    abstract fun cleanup()
    abstract fun setUniform(name: String, value: Float) : Boolean
    abstract fun setUniform(name: String, value: Int): Boolean
    abstract fun setUniform(name: String, value: Vector2f): Boolean
    abstract fun setUniform(name: String, value: Vector3f): Boolean
    abstract fun setUniform(name: String, value: Vector3i): Boolean
    abstract fun setUniform(name: String, value: Matrix4f, transpose: Boolean): Boolean
}

class ShaderProgramStandard(vertexShaderPath: String, fragmentShaderPath: String) : ShaderProgram() {
    private var programID: Int = 0

    // Matrix buffers for setting matrix uniforms. Prevents allocation for each uniform
    private val m4x4buf: FloatBuffer = BufferUtils.createFloatBuffer(16)

    /**
     * Sets the active shader program of the OpenGL render pipeline to this shader
     * if this isn't already the currently active shader
     */
    override fun use() {
        val curprog = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM)
        if (curprog != programID) GL20.glUseProgram(programID)
    }

    /**
     * Frees the allocated OpenGL objects
     */
    override fun cleanup() {
        GL20.glDeleteProgram(programID)
    }

    /**
     * Sets a single float uniform
     * @param name  Name of the uniform variable in the shader
     * @param value Value
     * @return returns false if the uniform was not found in the shader
     */
    override fun setUniform(name: String, value: Float): Boolean {
        if (programID == 0) return false
        val loc = GL20.glGetUniformLocation(programID, name)
        if (loc != -1) {
            GL20.glUniform1f(loc, value)
            return true
        }
        return false
    }

    override fun setUniform(name: String, value: Int): Boolean {
        if (programID == 0) return false
        val loc = GL20.glGetUniformLocation(programID, name)
        if (loc != -1) {
            GL20.glUniform1i(loc, value)
            return true
        }
        return false
    }

    override fun setUniform(name: String, value: Vector2f): Boolean {
        if (programID == 0) return false
        val loc = GL20.glGetUniformLocation(programID, name)
        if (loc != -1) {
            GL20.glUniform2f(loc, value.x, value.y)
            return true
        }
        return false
    }

    override fun setUniform(name: String, value: Vector3f): Boolean {
        if (programID == 0) return false
        val loc = GL20.glGetUniformLocation(programID, name)
        if (loc != -1) {
            GL20.glUniform3f(loc, value.x, value.y, value.z)
            return true
        }
        return false
    }

    override fun setUniform(name: String, value: Vector3i): Boolean {
        if (programID == 0) return false
        val loc = GL20.glGetUniformLocation(programID, name)
        if (loc != -1) {
            GL20.glUniform3i(loc, value.x, value.y, value.z)
            return true
        }
        return false
    }

    override fun setUniform(name: String, value: Matrix4f, transpose: Boolean): Boolean {
        if (programID == 0) return false
        val loc = glGetUniformLocation(programID, name) // looks for uniform in current program/"shader container"
        if (loc != -1) {
            glUniformMatrix4fv(loc, transpose, value.get(m4x4buf))
            return true
        }
        return false
    }


    /**
     * Creates a shader object from vertex and fragment shader paths
     * @param vertexShaderPath      vertex shader path
     * @param fragmentShaderPath    fragment shader path
     * @throws Exception if shader compilation failed, an exception is thrown
     */
    init {
        val vPath = Paths.get(vertexShaderPath)
        val fPath = Paths.get(fragmentShaderPath)
        val vSource = String(Files.readAllBytes(vPath))
        val fSource = String(Files.readAllBytes(fPath))
        val vShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER)
        if (vShader == 0) throw Exception("Vertex shader object couldn't be created.")
        val fShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER)
        if (fShader == 0) {
            GL20.glDeleteShader(vShader)
            throw Exception("Fragment shader object couldn't be created.")
        }
        GL20.glShaderSource(vShader, vSource)
        GL20.glShaderSource(fShader, fSource)
        GL20.glCompileShader(vShader)
        if (GL20.glGetShaderi(vShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            val log = GL20.glGetShaderInfoLog(vShader)
            GL20.glDeleteShader(fShader)
            GL20.glDeleteShader(vShader)
            throw Exception("Vertex shader compilation failed:\n$log")
        }
        GL20.glCompileShader(fShader)
        if (GL20.glGetShaderi(fShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            val log = GL20.glGetShaderInfoLog(fShader)
            GL20.glDeleteShader(fShader)
            GL20.glDeleteShader(vShader)
            throw Exception("Fragment shader compilation failed:\n$log")
        }
        programID = GL20.glCreateProgram()
        if (programID == 0) {
            GL20.glDeleteShader(vShader)
            GL20.glDeleteShader(fShader)
            throw Exception("Program object creation failed.")
        }
        GL20.glAttachShader(programID, vShader)
        GL20.glAttachShader(programID, fShader)
        GL20.glLinkProgram(programID)
        if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            val log = GL20.glGetProgramInfoLog(programID)
            GL20.glDetachShader(programID, vShader)
            GL20.glDetachShader(programID, fShader)
            GL20.glDeleteShader(vShader)
            GL20.glDeleteShader(fShader)
            throw Exception("Program linking failed:\n$log")
        }
        GL20.glDetachShader(programID, vShader)
        GL20.glDetachShader(programID, fShader)
        GL20.glDeleteShader(vShader)
        GL20.glDeleteShader(fShader)
    }
}

class ShaderProgramGeometry(vertexShaderPath: String, geometryShaderPath: String, fragmentShaderPath: String) : ShaderProgram() {
    private var programID: Int = 0

    // Matrix buffers for setting matrix uniforms. Prevents allocation for each uniform
    private val m4x4buf: FloatBuffer = BufferUtils.createFloatBuffer(16)

    /**
     * Sets the active shader program of the OpenGL render pipeline to this shader
     * if this isn't already the currently active shader
     */
    override fun use() {
        val curprog = glGetInteger(GL_CURRENT_PROGRAM)
        if (curprog != programID) glUseProgram(programID)
    }

    /**
     * Frees the allocated OpenGL objects
     */
    override fun cleanup() {
        glDeleteProgram(programID)
    }

    /**
     * Sets a single float uniform
     * @param name  Name of the uniform variable in the shader
     * @param value Value
     * @return returns false if the uniform was not found in the shader
     */
    override fun setUniform(name: String, value: Float): Boolean {
        if (programID == 0) return false
        val loc = glGetUniformLocation(programID, name)
        if (loc != -1) {
            glUniform1f(loc, value)
            return true
        }
        return false
    }

    override fun setUniform(name: String, value: Int): Boolean {
        if (programID == 0) return false
        val loc = glGetUniformLocation(programID, name)
        if (loc != -1) {
            glUniform1i(loc, value)
            return true
        }
        return false
    }

    override fun setUniform(name: String, value: Vector2f): Boolean {
        if (programID == 0) return false
        val loc = glGetUniformLocation(programID, name)
        if (loc != -1) {
            glUniform2f(loc, value.x, value.y)
            return true
        }
        return false
    }

    override fun setUniform(name: String, value: Vector3f): Boolean {
        if (programID == 0) return false
        val loc = glGetUniformLocation(programID, name)
        if (loc != -1) {
            glUniform3f(loc, value.x, value.y, value.z)
            return true
        }
        return false
    }

    override fun setUniform(name: String, value: Vector3i): Boolean {
        if (programID == 0) return false
        val loc = glGetUniformLocation(programID, name)
        if (loc != -1) {
            glUniform3i(loc, value.x, value.y, value.z)
            return true
        }
        return false
    }

    override fun setUniform(name: String, value: Matrix4f, transpose: Boolean): Boolean {
        if (programID == 0) return false
        val loc = glGetUniformLocation(programID, name) // looks for uniform in current program/"shader container"
        if (loc != -1) {
            glUniformMatrix4fv(loc, transpose, value.get(m4x4buf))
            return true
        }
        return false
    }

    /**
     * Creates a shader object from vertex and fragment shader paths
     * @param vertexShaderPath      vertex shader path
     * @param geometryShaderPath    geometry shader path
     * @param fragmentShaderPath    fragment shader path
     * @throws Exception if shader compilation failed, an exception is thrown
     */
    init {
        val vPath = Paths.get(vertexShaderPath)
        val gPath = Paths.get(geometryShaderPath)
        val fPath = Paths.get(fragmentShaderPath)

        val vSource = String(Files.readAllBytes(vPath))
        val gSource = String(Files.readAllBytes(gPath))
        val fSource = String(Files.readAllBytes(fPath))

        val vShader = glCreateShader(GL_VERTEX_SHADER)
        if (vShader == 0) throw Exception("Vertex shader object couldn't be created.")

        val gShader = glCreateShader(GL_GEOMETRY_SHADER)
        if (gShader == 0) {
            glDeleteShader(vShader)
            throw Exception("Geometry shader object couldn't be created.")
        }

        val fShader = glCreateShader(GL_FRAGMENT_SHADER)
        if (fShader == 0) {
            glDeleteShader(vShader)
            glDeleteShader(gShader)
            throw Exception("Fragment shader object couldn't be created.")
        }

        glShaderSource(vShader, vSource)
        glShaderSource(gShader, gSource)
        glShaderSource(fShader, fSource)

        glCompileShader(vShader)
        if (glGetShaderi(vShader, GL_COMPILE_STATUS) == GL_FALSE) {
            val log = glGetShaderInfoLog(vShader)
            glDeleteShader(vShader)
            glDeleteShader(gShader)
            glDeleteShader(fShader)
            throw Exception("Vertex shader compilation failed:\n$log")
        }

        glCompileShader(gShader)
        if (glGetShaderi(gShader, GL_COMPILE_STATUS) == GL_FALSE) {
            val log = glGetShaderInfoLog(gShader)
            glDeleteShader(vShader)
            glDeleteShader(gShader)
            glDeleteShader(fShader)
            throw Exception("Geometry shader compilation failed:\n$log")
        }

        glCompileShader(fShader)
        if (glGetShaderi(fShader, GL_COMPILE_STATUS) == GL_FALSE) {
            val log = glGetShaderInfoLog(fShader)
            glDeleteShader(vShader)
            glDeleteShader(gShader)
            glDeleteShader(fShader)
            throw Exception("Fragment shader compilation failed:\n$log")
        }

        programID = glCreateProgram()
        if (programID == 0) {
            glDeleteShader(vShader)
            glDeleteShader(gShader)
            glDeleteShader(fShader)
            throw Exception("ShaderProgramGeometry creation failed.")
        }
        glAttachShader(programID, vShader)
        glAttachShader(programID, gShader)
        glAttachShader(programID, fShader)

        glLinkProgram(programID)
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            val log = glGetProgramInfoLog(programID)
            glDetachShader(programID, vShader)
            glDetachShader(programID, fShader)
            glDeleteShader(vShader)
            glDeleteShader(fShader)
            throw Exception("Program linking failed:\n$log")
        }
        glDetachShader(programID, vShader)
        glDetachShader(programID, gShader)
        glDetachShader(programID, fShader)
        glDeleteShader(vShader)
        glDeleteShader(gShader)
        glDeleteShader(fShader)
    }
}