package Players.AIPlayer;

import Interface.Coordinate;
import Interface.PlayerMove;

import java.util.*;

/**
 * Represents game controller
 */
class GameController {
    private int playerId;
    private Board board;
    private Tile extraTile;

    /**
     * @param playerId, the id of this player
	 * @param playerHomes, starting locations for each player, in order
     * @param treasures, ordered list of treasures for each player
     * @param board 2-d list of [Tile ID, Rotation, Treasure]
     *        Tile IDs:  0 = L tile, 1 = T tile, 2 = I tile
     *        Treasures: -1 = no treasure, 0-23 = corresponding treasure
     *        Rotations: 0 = 0 degrees, 1 = 90 degrees, 2 = 180 degrees,
     *        			 3 = 270 degrees, all clockwise
     * @param extraTile contains [Extra Tile ID, Treasure]
     */
    GameController(final int playerId,
                   final List<Coordinate> playerHomes,
                   final List<List<Integer>> treasures,
                   final List<List<List<Integer>>> board,
                   final List<Integer> extraTile) {
        this.playerId = playerId;
        this.board = new Board(playerHomes, treasures, board);
        this.extraTile = new Tile(MazePathType.fromId(extraTile.get(0)),
                                  TreasureType.fromId(extraTile.get(1)));
    }

    PlayerMove findBestMove() {
        Coordinate bestTileInsertionLocation = null;
        MazePathOrientation bestMazePathOrientation = null;
        List<Coordinate> bestPathToNextTreasure = null;
        int bestManhattanDistanceToTreasure = Integer.MAX_VALUE;

        for(Coordinate tileInsertionLocation : this.board.getValidTileInsertionLocations()) {
            for(MazePathOrientation mazePathOrientation : MazePathOrientation.values()) {
                //Ignore 180 and 270 degree maze path orientation for 'I' maze type path, as they are equivalent
                //to 0 and 90 degree maze path orientations.
                if(extraTile.getMazePathType().equals(MazePathType.I) &&
                        (mazePathOrientation.equals(MazePathOrientation.ONE_HUNDRED_EIGHTY) ||
                                mazePathOrientation.equals(MazePathOrientation.TWO_HUNDRED_SEVENTY))) {
                    continue;
                }

                //Create a copy of the current board with the extra tile inserted in the chosen insertion location
                //and with the chosen tile orientation
                final Board tempBoard = this.board.createCopy();
                this.extraTile.setMazePathOrientation(mazePathOrientation);
                final Tile tempExtraTile = tempBoard.insertTile(this.extraTile, tileInsertionLocation);

                final List<Coordinate> pathToNextTreasure = findBestPathToNextTreasure(tempBoard);

                int manhattanDistanceToTreasure = Integer.MAX_VALUE;

                if(!tempExtraTile.getTreasureType().equals(tempBoard.getNextTreasureForPlayer(this.playerId))) {
                    manhattanDistanceToTreasure = calculateManhattanDistance(pathToNextTreasure.get(pathToNextTreasure.size() - 1),
                                                                             tempBoard.getNextTreasureLocationForPlayer(this.playerId));
                }

                if(manhattanDistanceToTreasure < bestManhattanDistanceToTreasure) {
                    bestTileInsertionLocation = tileInsertionLocation;
                    bestMazePathOrientation = mazePathOrientation;
                    bestPathToNextTreasure = pathToNextTreasure;
                    bestManhattanDistanceToTreasure = manhattanDistanceToTreasure;
                }
            }
        }

        return new PlayerMove(this.playerId, bestPathToNextTreasure, bestTileInsertionLocation, bestMazePathOrientation.getId());
    }

    void handlePlayerMove(final PlayerMove playerMove) {
        this.extraTile.setMazePathOrientation(MazePathOrientation.fromId(playerMove.getTileRotation()));
        this.extraTile = this.board.insertTile(this.extraTile, playerMove.getTileInsertion());

        final List<Coordinate> playerPath = playerMove.getPath();

        this.board.movePlayer(playerMove.getPlayerId(), playerPath.get(playerPath.size() - 1));
    }

    private List<Coordinate> findBestPathToNextTreasure(final Board board) {
        final List<Coordinate> path = new ArrayList<>();

        final Coordinate currentPlayerLocation = board.getPlayerLocation(this.playerId);

        path.add(currentPlayerLocation);

        final Tile currentPlayerLocationTile = board.getTile(currentPlayerLocation.getRow(), currentPlayerLocation.getCol());

        //final List<Coordinate> possiblePaths = new ArrayList<>();

        //check neighboring tile to the north
        if(currentPlayerLocation.getRow() > 0) {
            final Tile northTile = board.getTile(currentPlayerLocation.getRow() - 1, currentPlayerLocation.getCol());

            if(currentPlayerLocationTile.hasExit(CompassDirection.NORTH) && northTile.hasExit(CompassDirection.SOUTH)) {
                path.add(new Coordinate(currentPlayerLocation.getRow() - 1, currentPlayerLocation.getCol()));
                return path;
            }
        }

        //check neighboring tile to the south
        if(currentPlayerLocation.getRow() < Coordinate.BOARD_DIM - 1) {
            final Tile southTile = board.getTile(currentPlayerLocation.getRow() + 1, currentPlayerLocation.getCol());

            if(currentPlayerLocationTile.hasExit(CompassDirection.SOUTH) && southTile.hasExit(CompassDirection.NORTH)) {
                path.add(new Coordinate(currentPlayerLocation.getRow() + 1, currentPlayerLocation.getCol()));
                return path;
            }
        }

        //check neighboring tile to the west
        if(currentPlayerLocation.getCol() > 0) {
            final Tile westTile = board.getTile(currentPlayerLocation.getRow(), currentPlayerLocation.getCol() - 1);

            if(currentPlayerLocationTile.hasExit(CompassDirection.WEST) && westTile.hasExit(CompassDirection.EAST)) {
                path.add(new Coordinate(currentPlayerLocation.getRow(), currentPlayerLocation.getCol() - 1));
                return path;
            }
        }

        //check neighboring tile to the east
        if(currentPlayerLocation.getCol() < Coordinate.BOARD_DIM - 1) {
            final Tile eastTile = board.getTile(currentPlayerLocation.getRow(), currentPlayerLocation.getCol() + 1);

            if(currentPlayerLocationTile.hasExit(CompassDirection.EAST) && eastTile.hasExit(CompassDirection.WEST)) {
                path.add(new Coordinate(currentPlayerLocation.getRow(), currentPlayerLocation.getCol() + 1));
                return path;
            }
        }

        /*if(possiblePaths.size() > 0) {
            final Iterator<Coordinate> possiblePathsIterator = possiblePaths.iterator();
            int randomPossiblePath = new Random().nextInt(possiblePaths.size());

            while(randomPossiblePath > 0) {
                possiblePathsIterator.next();
                randomPossiblePath--;
            }

            path.add(possiblePathsIterator.next());
        }*/

        return path;
    }

    private int calculateManhattanDistance(final Coordinate coordinate1, final Coordinate coordinate2) {
        return Math.abs(coordinate2.getRow() - coordinate1.getRow()) + Math.abs(coordinate2.getCol() - coordinate1.getCol());
    }
}