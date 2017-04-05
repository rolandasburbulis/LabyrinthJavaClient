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
        //System.out.println("Initial board");
        //this.board.print();
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

        //Create a copy of the current board with the extra tile inserted in the chosen insertion location
        //and with the chosen tile orientation
        Board tempBoard = this.board.createCopy();
        this.extraTile.setMazePathOrientation(MazePathOrientation.fromId(randomTileOrientation));
        tempBoard.insertTile(this.extraTile, chosenInsertionLocation);

        return new PlayerMove(this.playerId, generateRandomPath(tempBoard), chosenInsertionLocation, randomTileOrientation);
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

    private List<Coordinate> generateRandomPath(final Board board) {
        final List<Coordinate> path = new ArrayList<>();

        final Coordinate currentPlayerLocation = board.getPlayerLocation(this.playerId);

        path.add(currentPlayerLocation);

        Tile currentPlayerLocationTile = board.getTile(currentPlayerLocation.getRow(), currentPlayerLocation.getCol());

        final List<Coordinate> possiblePaths = new ArrayList<>();

        //check neighboring tile to the north
        if(currentPlayerLocation.getRow() > 0) {
            Tile northTile = board.getTile(currentPlayerLocation.getRow() - 1, currentPlayerLocation.getCol());

            if(currentPlayerLocationTile.hasExit(CompassDirection.NORTH) && northTile.hasExit(CompassDirection.SOUTH)) {
                possiblePaths.add(new Coordinate(currentPlayerLocation.getRow() - 1, currentPlayerLocation.getCol()));
            }
        }

        //check neighboring tile to the south
        if(currentPlayerLocation.getRow() < Coordinate.BOARD_DIM - 1) {
            Tile southTile = board.getTile(currentPlayerLocation.getRow() + 1, currentPlayerLocation.getCol());

            if(currentPlayerLocationTile.hasExit(CompassDirection.SOUTH) && southTile.hasExit(CompassDirection.NORTH)) {
                possiblePaths.add(new Coordinate(currentPlayerLocation.getRow() + 1, currentPlayerLocation.getCol()));
            }
        }

        //check neighboring tile to the west
        if(currentPlayerLocation.getCol() > 0) {
            Tile westTile = board.getTile(currentPlayerLocation.getRow(), currentPlayerLocation.getCol() - 1);

            if(currentPlayerLocationTile.hasExit(CompassDirection.WEST) && westTile.hasExit(CompassDirection.EAST)) {
                possiblePaths.add(new Coordinate(currentPlayerLocation.getRow(), currentPlayerLocation.getCol() - 1));
            }
        }

        //check neighboring tile to the east
        if(currentPlayerLocation.getCol() < Coordinate.BOARD_DIM - 1) {
            Tile eastTile = board.getTile(currentPlayerLocation.getRow(), currentPlayerLocation.getCol() + 1);

            if(currentPlayerLocationTile.hasExit(CompassDirection.EAST) && eastTile.hasExit(CompassDirection.WEST)) {
                possiblePaths.add(new Coordinate(currentPlayerLocation.getRow(), currentPlayerLocation.getCol() + 1));
            }
        }

        if(possiblePaths.size() > 0) {
            final Iterator<Coordinate> possiblePathsIterator = possiblePaths.iterator();
            int randomPossiblePath = new Random().nextInt(possiblePaths.size());

            while(randomPossiblePath > 0) {
                possiblePathsIterator.next();
                randomPossiblePath--;
            }

            path.add(possiblePathsIterator.next());
        }

        return path;
    }
}