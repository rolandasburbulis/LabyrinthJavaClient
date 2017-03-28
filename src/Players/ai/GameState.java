package Players.ai;

import Interface.Coordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents AI player game state
 */
public class GameState {
    private Board board;
    private Tile extraTile;

    /**
     * @param logger, reference to the logger class
	 * @param playerId, the id of this player
	 * @param playerHomes, starting locations for each player, in order
	 * @param treasures, ordered list of treasures for each player
	 * @param board 2-d list of [Tile ID, Rotation, Treasure]
     *        Tile IDs:  0 = L tile, 1 = T tile, 2 = I tile
	 *        Treasures: -1 = no treasure, 0-23 = corresponding treasure
	 *        Rotations: 0 = 0 degrees, 1 = 90 degrees, 2 = 180 degrees,
     *        			 3 = 270 degrees, all clockwise
	 * @param extra contains [Extra Tile ID, Treasure]
     */
    public GameState(final List<Coordinate> playerHomes,
                     final List<List<Integer>> treasures,
                     final List<List<List<Integer>>> board,
                     final List<Integer> extra) {
        this.board = new Board(board);
    }
}