package cga.exercise.game

import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import org.joml.Matrix4f
import javax.xml.crypto.dsig.Transform

/**
 *  n1 nördlich
 *  n2 östlich
 *  n3 südlich
 *  n4 westlich
 */

class EmptySpot(tileList : MutableList<Tile?>, modelMatrix: Matrix4f = Matrix4f(), parent: Transformable? = null) : Transformable(modelMatrix, parent) {
    var neighbourNegZ : Tile? = null //N
    var neighbourPosX : Tile? = null //O
    var neighbourPosZ : Tile? = null //S
    var neighbourNegX : Tile? = null //W
    var emptyPlace : Int = 8
    val listofTiles = tileList
    //zu Beginn im Norden und Westen ein Tile, Rest null
    init {
        neighbourNegZ = tileList[5]
        neighbourNegX = tileList[7]
        //neighbourPosX = tileList[1]
        //neighbourPosZ = tileList[3]
        print(neighbourNegZ)
        print(neighbourPosX)
        print(neighbourPosZ)
        print(neighbourNegX)
        print("\n")
    }

    //empty nach Norden, vermutlich bugless
    fun moveNegZ(place : Int) : MutableList<Tile?>{
        print(place)

        val oldList = listofTiles
        val tmp = listofTiles[place]
        listofTiles[emptyPlace] = oldList[place]
        listofTiles[place] = oldList[emptyPlace]
        emptyPlace = place

        if (place-3 > -1) {
            neighbourNegZ = listofTiles[emptyPlace-3]
        } else {
            neighbourNegZ = null
        }
        if (neighbourPosX != null) {
            neighbourPosX = listofTiles[emptyPlace+1]
        }
        if (neighbourNegX != null) {
            neighbourNegX = listofTiles[emptyPlace-1]
        }
        if (place+3 < 9) {
            neighbourPosZ = listofTiles[emptyPlace+3]
        }
        return listofTiles
    }


    //empty nach Süden
    fun movePosZ(place : Int) : MutableList<Tile?>{
        print(place)

        val oldList = listofTiles
        val tmp = listofTiles[place]
        listofTiles[emptyPlace] = oldList[place]
        listofTiles[place] = oldList[emptyPlace]
        emptyPlace = place


        if (place+3 < 9) {
            neighbourPosZ = listofTiles[emptyPlace+3]
        } else {
            neighbourPosZ = null
        }
        if (neighbourPosX != null) {
            neighbourPosX = listofTiles[emptyPlace+1]
        }
        if (neighbourNegX != null) {
            neighbourNegX = listofTiles[emptyPlace-1]
        }
        if (place-3 > 0) {
            neighbourNegZ = listofTiles[emptyPlace-3]
        }
        return listofTiles
    }
    //empty nach Westen
    fun moveNegX(place : Int) : MutableList<Tile?>{
        print(place)
        val oldList = listofTiles
        val tmp = listofTiles[place]
        listofTiles[emptyPlace] = oldList[place]
        listofTiles[place] = oldList[emptyPlace]
        emptyPlace = place

        if (place-1 != -1 && place-1 != 2 && place-1 != 5) {
            neighbourNegX = listofTiles[emptyPlace-1]
        } else {
            neighbourNegX = null
        }
        if (neighbourPosZ != null) {
            neighbourPosZ = listofTiles[emptyPlace+3]
        }
        if (neighbourNegZ != null) {
            neighbourNegZ = listofTiles[emptyPlace-3]
        }
        if (place+1 < 9) {
            neighbourPosX =listofTiles[emptyPlace+1]
        }
        return listofTiles
    }

    //empty nach Osten
    fun movePosX(place : Int) : MutableList<Tile?> {
        print(place)
        val oldList = listofTiles
        val tmp = listofTiles[place]
        listofTiles[emptyPlace] = oldList[place]
        listofTiles[place] = oldList[emptyPlace]
        emptyPlace = place

        if (place+1 != 3 && place+1 != 6 && place+1 != 9) {
            neighbourPosX = listofTiles[emptyPlace+1]
        } else {
            neighbourPosX = null
        }
        if (neighbourPosZ != null) {
            neighbourPosZ = listofTiles[emptyPlace+3]
        }
        if (neighbourNegZ != null) {
            neighbourNegZ = listofTiles[emptyPlace-3]
        }
        if (place-1 > 0) {
            neighbourNegX =listofTiles[emptyPlace-1]
        }
        return listofTiles
    }

}