package cga.exercise.game

import org.joml.Matrix4f

/**
 * TileSpot possesses 0..1 Tile().
 */
class TileSpot(var tile: Tile?) {

    /** [Zero] means that the TileSpot isn't set in the level. **/
    internal var posID: Int = 0

    /** Checks whether TileSpot is empty or not **/
    val isEmpty: Boolean
        get() = (tile != null)
}