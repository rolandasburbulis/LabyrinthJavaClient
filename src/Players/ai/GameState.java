package Players.ai;

import Interface.Coordinate;

import java.util.*;

/**
 * Represents game state
 */
public class GameState {
    private Board board;
    private Tile extraTile;
    private Map<Integer, Queue<TreasureType>> treasures;

    /**
	 * @param playerHomes, starting locations for each player, in order
	 * @param treasures, ordered list of treasures for each player
	 * @param board 2-d list of [Tile ID, Rotation, Treasure]
     *        Tile IDs:  0 = L tile, 1 = T tile, 2 = I tile
	 *        Treasures: -1 = no treasure, 0-23 = corresponding treasure
	 *        Rotations: 0 = 0 degrees, 1 = 90 degrees, 2 = 180 degrees,
     *        			 3 = 270 degrees, all clockwise
	 * @param extraTile contains [Extra Tile ID, Treasure]
     */
    public GameState(final List<Coordinate> playerHomes,
                     final List<List<Integer>> treasures,
                     final List<List<List<Integer>>> board,
                     final List<Integer> extraTile) {
        this.board = new Board(playerHomes, board);
        this.extraTile = new Tile(extraTile);
        setupTreasures(treasures);
    }

    private void setupTreasures(List<List<Integer>> treasures) {
        this.treasures = new HashMap<>();

        for(int player = 1; player <= treasures.size(); player++) {
            final Queue<TreasureType> playerTreasures = new LinkedList<TreasureType>();

            for(Integer treasureId : treasures.get(player - 1)) {
                TreasureType treasureType = TreasureType.fromId(treasureId);
                playerTreasures.add(treasureType);
            }

            this.treasures.put(player, playerTreasures);
        }
    }
}