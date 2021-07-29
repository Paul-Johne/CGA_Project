package cga.exercise.components.geometry

import org.joml.Matrix4f
import org.joml.Vector3f

/** changed var parent to val parent
 *  modelMatrix => V = View Transformation
 */
open class Transformable(var modelMatrix: Matrix4f = Matrix4f(), val parent: Transformable? = null) {

    /**
     * Rotates object around its own origin.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     */
    fun rotateLocal(pitch: Float, yaw: Float, roll: Float) {
        modelMatrix.rotateXYZ(pitch, yaw, roll)
    }

    /**
     * Rotates object around given rotation center.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     * @param altMidpoint rotation center
     */
    fun rotateAroundPoint(pitch: Float, yaw: Float, roll: Float, altMidpoint: Vector3f) {
        val point = Vector3f(altMidpoint)
        /**
         * 1) tM = I x T x R x T
         * 2) tM x mM
         */
        val transposedMatrix = Matrix4f().translate(point).rotateXYZ(pitch, yaw, roll).translate(point.negate()) //.mul(modelMatrix)
        modelMatrix = transposedMatrix.mul(modelMatrix)
    }

    /**
     * Translates object based on its own coordinate system/own origin.
     * @param deltaPos delta positions
     */
    fun translateLocal(deltaPos: Vector3f) {
        modelMatrix.translate(deltaPos)
    }

    /**
     * Translates object based on its parent coordinate system.
     * Hint: global operations will be left-multiplied
     * @param deltaPos delta positions (x, y, z)
     */
    fun translateGlobal(deltaPos: Vector3f) {
        val translation = Matrix4f().setTranslation(deltaPos)
        modelMatrix = translation.mul(modelMatrix)
    }

    /**
     * Scales object related to its own origin
     * @param scale scale factor (x, y, z)
     */
    fun scaleLocal(scale: Vector3f) {
        modelMatrix.scale(scale)
    }

    /**
     * Returns position based on aggregated translations.
     * Hint: last column of model matrix
     * @return position
     */
    fun getPosition(): Vector3f = modelMatrix.getTranslation(Vector3f())

    /**
     * Returns position based on aggregated translations incl. parents.
     * Hint: last column of world model matrix
     * @return position
     */
    fun getWorldPosition(): Vector3f = getWorldModelMatrix().getTranslation(Vector3f())

    /**
     * Returns x-axis of object coordinate system
     * Hint: first normalized column of model matrix
     * @return x-axis
     */
    fun getXAxis(): Vector3f {
        val localMatrix = getLocalModelMatrix()
        return Vector3f(localMatrix.m00(), localMatrix.m01(), localMatrix.m02()).normalize()
    }

    /**
     * Returns y-axis of object coordinate system
     * Hint: second normalized column of model matrix
     * @return y-axis
     */
    fun getYAxis(): Vector3f {
        val localMatrix = getLocalModelMatrix()
        return Vector3f(localMatrix.m10(), localMatrix.m11(), localMatrix.m12()).normalize()
    }

    /**
     * Returns z-axis of object coordinate system
     * Hint: third normalized column of model matrix
     * @return z-axis
     */
    fun getZAxis(): Vector3f {
        val localMatrix = getLocalModelMatrix()
        return Vector3f(localMatrix.m20(), localMatrix.m21(), localMatrix.m22()).normalize()
    }

    /**
     * Returns x-axis of world coordinate system
     * Hint: first normalized column of world model matrix
     * @return x-axis
     */
    fun getWorldXAxis(): Vector3f {
        val worldMatrix = getWorldModelMatrix()
        return Vector3f(worldMatrix.m00(), worldMatrix.m01(), worldMatrix.m02()).normalize()
    }

    /**
     * Returns y-axis of world coordinate system
     * Hint: second normalized column of world model matrix
     * @return y-axis
     */
    fun getWorldYAxis(): Vector3f {
        val worldMatrix = getWorldModelMatrix()
        return Vector3f(worldMatrix.m10(), worldMatrix.m11(), worldMatrix.m12()).normalize()
    }

    /**
     * Returns z-axis of world coordinate system
     * Hint: third normalized column of world model matrix
     * @return z-axis
     */
    fun getWorldZAxis(): Vector3f {
        val worldMatrix = getWorldModelMatrix()
        return Vector3f(worldMatrix.m20(), worldMatrix.m21(), worldMatrix.m22()).normalize()
    }

    /**
     * Returns multiplication of world and object model matrices.
     * Multiplication has to be recursive for all parents.
     * Hint: scene graph
     * @return world modelMatrix
     */
    fun getWorldModelMatrix(): Matrix4f = parent?.getWorldModelMatrix()?.mul(modelMatrix) ?: getLocalModelMatrix()

    /**
     * Returns object model matrix
     * @return modelMatrix
     */
    fun getLocalModelMatrix(): Matrix4f = Matrix4f(modelMatrix)
}