package cga.exercise.game

/**
 * A Level includes all parts for a scene
 * - tileSpots [ n TileSpot-Objects ]
 * - player [ the moveable character in the scene ]
 * - camera TODO [ rotating, isometric and zoomable camera]
 *
 * Level (one spot SHOULD BE empty):
 *
 * 1 | 2 | 3
 *
 * 4 | 5 | 6
 *
 * 7 | 8 | 9
 */
class Level(vararg tileSpots: TileSpot, player: Player) {

    /** Contains TileSpots in a MutableList [should be 9 for the demo] **/
    val tileSpotList = mutableListOf<TileSpot>()
        init {
            for (spot in tileSpots)
                tileSpotList.add(spot)
        }

    val nextFree: Int
        get() {
            for (spot in tileSpotList)
                if (spot.isEmpty) return spot.posID
            return 0
        }

    fun movePlayer(player: Player) {
        TODO()
    }

    fun moveTileToEmptyTileSpot() {
        TODO()

    }

    fun renderLevel() {
        TODO("call TileSpot::render() to render Renderable Tile AND Player")
    }
}