package Players.AIPlayer;

import Interface.Coordinate;
import Interface.PlayerMove;

import java.util.*;

/**
 * Represents game controller
 */
public class GameController {
    private int playerId;
    private Board board;
    private Tile extraTile;
    private Map<Integer, Queue<TreasureType>> treasures;

    /**
     * @param playerId, the id of this player
	 * @param playerHomes, starting locations for each player, in order
     * @param board 2-d list of [Tile ID, Rotation, Treasure]
     *        Tile IDs:  0 = L tile, 1 = T tile, 2 = I tile
     *        Treasures: -1 = no treasure, 0-23 = corresponding treasure
     *        Rotations: 0 = 0 degrees, 1 = 90 degrees, 2 = 180 degrees,
     *        			 3 = 270 degrees, all clockwise
     * @param extraTile contains [Extra Tile ID, Treasure]
	 * @param treasures, ordered list of treasures for each player
     */
    public GameController(final int playerId,
                          final List<Coordinate> playerHomes,
                          final List<List<List<Integer>>> board,
                          final List<Integer> extraTile,
                          final List<List<Integer>> treasures) {
        this.playerId = playerId;
        this.board = new Board(playerHomes, board);
        System.out.println("Initial board");
        this.board.print();
        this.extraTile = new Tile(MazePathType.fromId(extraTile.get(0)),
                                  TreasureType.fromId(extraTile.get(1)));
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

    public PlayerMove generateRandomMove() {
        final Set<Coordinate> validTileInsertionLocations = this.board.getValidTileInsertionLocations();
        final Iterator<Coordinate> validTileInsertionLocationsIterator = validTileInsertionLocations.iterator();
        int randomTileInsertionLocation = new Random().nextInt(validTileInsertionLocations.size());

        while(randomTileInsertionLocation > 0) {
            validTileInsertionLocationsIterator.next();
            randomTileInsertionLocation--;
        }

        final Coordinate chosenInsertionLocation = validTileInsertionLocationsIterator.next();

        final int randomTileOrientation;

        if(this.extraTile.getMazePathType().equals(MazePathType.I)) {
            randomTileOrientation = new Random().nextInt(2);
        } else {
            randomTileOrientation = new Random().nextInt(4);
        }

        return new PlayerMove(this.playerId, generateRandomPath(), chosenInsertionLocation, randomTileOrientation);
        //return new PlayerMove(this.playerId, generateRandomPath(), new Coordinate(6, 1), 0);
    }

    public void handlePlayerMove(final PlayerMove playerMove) {
        //System.out.println("Before player " + playerMove.getPlayerId() + " move:");
        //this.board.print();

        this.extraTile.setMazePathOrientation(MazePathOrientation.fromId(playerMove.getTileRotation()));
        this.extraTile = this.board.insertTile(this.extraTile, playerMove.getTileInsertion());
        this.board.movePlayer(playerMove.getPlayerId(), playerMove.getPath().get(playerMove.getPath().size() - 1));

        //System.out.println("After player " + playerMove.getPlayerId() + " move:");
        //this.board.print();
    }

    private List<Coordinate> generateRandomPath() {
        final List<Coordinate> path = new ArrayList<>();

        path.add(this.board.getPlayerLocation(this.playerId));

        return path;
    }
}