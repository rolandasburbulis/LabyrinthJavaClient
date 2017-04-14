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
        final List<PlayerMove> bestPlayerMoves = new ArrayList<>();
        int bestManhattanDistanceToGoal = Integer.MAX_VALUE;

        for(Coordinate tileInsertionLocation : this.board.getValidTileInsertionLocations()) {
            for(MazePathOrientation mazePathOrientation : MazePathOrientation.values()) {
                //Ignore 180 and 270 degree maze path orientation for 'I' maze type path, as they are equivalent
                //to 0 and 90 degree maze path orientations.
                if(extraTile.getMazePathType().equals(MazePathType.I) &&
                        (mazePathOrientation.equals(MazePathOrientation.ONE_HUNDRED_EIGHTY) ||
                                mazePathOrientation.equals(MazePathOrientation.TWO_HUNDRED_SEVENTY))) {
                    continue;
                }

                //Create a copy of the current board and extra tile and insert the extra tile in the chosen insertion
                // location with the chosen tile orientation
                final Board tempBoard = this.board.createCopy();
                final Tile tempExtraTile = this.extraTile.createCopy();
                tempExtraTile.setMazePathOrientation(mazePathOrientation);
                final Tile newTempExtraTile = tempBoard.insertTile(tempExtraTile, tileInsertionLocation);

                final TreasureType nextTreasureForPlayer = tempBoard.getNextTreasureForPlayer(this.playerId);
                final Coordinate nextGoalCoordinate;

                ///If the player collected all treasures
                if(nextTreasureForPlayer == null) {
                    nextGoalCoordinate = tempBoard.getPlayerHome(this.playerId);
                //If the player hasn't collected all of their treasures and the next treasure they need to collect is
                //on the board (not on extra tile)
                } else if(!newTempExtraTile.getTreasureType().equals(nextTreasureForPlayer)) {
                    nextGoalCoordinate = tempBoard.getNextTreasureLocationForPlayer(this.playerId);
                //If the next treasure player needs to collect is on the extra tile
                } else {
                    continue;
                }

                final List<Coordinate> pathTowardsNextGoal = findBestPathTowardsNextGoal(tempBoard, nextGoalCoordinate);

                if(pathTowardsNextGoal.get(pathTowardsNextGoal.size() - 1).equals(nextGoalCoordinate)) {
                    return new PlayerMove(this.playerId, pathTowardsNextGoal, tileInsertionLocation, mazePathOrientation.ordinal());
                }

                final int manhattanDistanceToGoal = calculateManhattanDistance(pathTowardsNextGoal.get(pathTowardsNextGoal.size() - 1), nextGoalCoordinate);

                if(manhattanDistanceToGoal <= bestManhattanDistanceToGoal) {
                    final PlayerMove playerMove = new PlayerMove(this.playerId, pathTowardsNextGoal, tileInsertionLocation, mazePathOrientation.ordinal());

                    if(manhattanDistanceToGoal < bestManhattanDistanceToGoal) {
                        bestPlayerMoves.clear();
                        bestManhattanDistanceToGoal = manhattanDistanceToGoal;
                    }

                    bestPlayerMoves.add(playerMove);
                }
            }
        }

        return bestPlayerMoves.get(new Random().nextInt(bestPlayerMoves.size()));
    }

    void handlePlayerMove(final PlayerMove playerMove) {
        this.extraTile.setMazePathOrientation(MazePathOrientation.fromId(playerMove.getTileRotation()));
        this.extraTile = this.board.insertTile(this.extraTile, playerMove.getTileInsertion());

        final List<Coordinate> playerPath = playerMove.getPath();

        this.board.movePlayer(playerMove.getPlayerId(), playerPath.get(playerPath.size() - 1));
    }

    private List<Coordinate> findBestPathTowardsNextGoal(final Board board,
                                                         final Coordinate goalCoordinate) {
        final Map<Coordinate, Coordinate> reachableCoordinates = findAllReachableCoordinates(board,
                                                                                             null,
                                                                                             board.getPlayerLocation(this.playerId),
                                                                                             new HashMap<>());

        final List<Coordinate> bestReachableCoordinates = new ArrayList<>();

        if(reachableCoordinates.containsKey(goalCoordinate)) {
            bestReachableCoordinates.add(goalCoordinate);
        } else {
            int bestManhattanDistanceReachableCoordinateToGoal = Integer.MAX_VALUE;

            for(Coordinate reachableCoordinate : reachableCoordinates.keySet()) {
                final int manhattanDistanceReachableCoordinateToGoal = calculateManhattanDistance(reachableCoordinate, goalCoordinate);

                if(manhattanDistanceReachableCoordinateToGoal <= bestManhattanDistanceReachableCoordinateToGoal) {
                    if(manhattanDistanceReachableCoordinateToGoal < bestManhattanDistanceReachableCoordinateToGoal) {
                        bestReachableCoordinates.clear();
                        bestManhattanDistanceReachableCoordinateToGoal = manhattanDistanceReachableCoordinateToGoal;
                    }

                    bestReachableCoordinates.add(reachableCoordinate);
                }
            }
        }

        Stack<Coordinate> reverseBestPathTowardsNextGoal = new Stack<>();
        Coordinate pathCoordinate = bestReachableCoordinates.get(new Random().nextInt(bestReachableCoordinates.size()));

        do {
            reverseBestPathTowardsNextGoal.push(pathCoordinate);
            pathCoordinate = reachableCoordinates.get(pathCoordinate);
        }
        while(pathCoordinate != null);

        List<Coordinate> bestPathTowardsNextGoal = new ArrayList<>();

        while(!reverseBestPathTowardsNextGoal.empty()) {
            bestPathTowardsNextGoal.add(reverseBestPathTowardsNextGoal.pop());
        }

        return bestPathTowardsNextGoal;
    }

    private Map<Coordinate, Coordinate> findAllReachableCoordinates(final Board board,
                                                                    final Coordinate arrivedFromLocationCoordinate,
                                                                    final Coordinate currentLocationCoordinate,
                                                                    final HashMap<Coordinate, Coordinate> reachableCoordinates) {
        final Tile currentLocationTile = board.getTile(currentLocationCoordinate.getRow(), currentLocationCoordinate.getCol());

        reachableCoordinates.put(currentLocationCoordinate, arrivedFromLocationCoordinate);

        //check neighboring tile to the north
        if(currentLocationCoordinate.getRow() > 0) {
            final Coordinate northTileCoordinate = new Coordinate(currentLocationCoordinate.getRow() - 1, currentLocationCoordinate.getCol());
            final Tile northTile = board.getTile(northTileCoordinate.getRow(), northTileCoordinate.getCol());

            if(currentLocationTile.hasExit(CompassDirection.NORTH) && northTile.hasExit(CompassDirection.SOUTH) && !reachableCoordinates.containsKey(northTileCoordinate)) {
                reachableCoordinates.putAll(findAllReachableCoordinates(board, currentLocationCoordinate, northTileCoordinate, reachableCoordinates));
            }
        }

        //check neighboring tile to the south
        if(currentLocationCoordinate.getRow() < Coordinate.BOARD_DIM - 1) {
            final Coordinate southTileCoordinate = new Coordinate(currentLocationCoordinate.getRow() + 1, currentLocationCoordinate.getCol());
            final Tile southTile = board.getTile(currentLocationCoordinate.getRow() + 1, currentLocationCoordinate.getCol());

            if(currentLocationTile.hasExit(CompassDirection.SOUTH) && southTile.hasExit(CompassDirection.NORTH) && !reachableCoordinates.containsKey(southTileCoordinate)) {
                reachableCoordinates.putAll(findAllReachableCoordinates(board, currentLocationCoordinate, southTileCoordinate, reachableCoordinates));
            }
        }

        //check neighboring tile to the west
        if(currentLocationCoordinate.getCol() > 0) {
            final Coordinate westTileCoordinate = new Coordinate(currentLocationCoordinate.getRow(), currentLocationCoordinate.getCol() - 1);
            final Tile westTile = board.getTile(currentLocationCoordinate.getRow(), currentLocationCoordinate.getCol() - 1);

            if(currentLocationTile.hasExit(CompassDirection.WEST) && westTile.hasExit(CompassDirection.EAST) && !reachableCoordinates.containsKey(westTileCoordinate)) {
                reachableCoordinates.putAll(findAllReachableCoordinates(board, currentLocationCoordinate, westTileCoordinate, reachableCoordinates));
            }
        }

        //check neighboring tile to the east
        if(currentLocationCoordinate.getCol() < Coordinate.BOARD_DIM - 1) {
            final Coordinate eastTileCoordinate = new Coordinate(currentLocationCoordinate.getRow(), currentLocationCoordinate.getCol() + 1);
            final Tile eastTile = board.getTile(currentLocationCoordinate.getRow(), currentLocationCoordinate.getCol() + 1);

            if(currentLocationTile.hasExit(CompassDirection.EAST) && eastTile.hasExit(CompassDirection.WEST) && !reachableCoordinates.containsKey(eastTileCoordinate)) {
                reachableCoordinates.putAll(findAllReachableCoordinates(board, currentLocationCoordinate, eastTileCoordinate, reachableCoordinates));
            }
        }

        return reachableCoordinates;
    }

    private int calculateManhattanDistance(final Coordinate coordinate1, final Coordinate coordinate2) {
        return Math.abs(coordinate2.getRow() - coordinate1.getRow()) + Math.abs(coordinate2.getCol() - coordinate1.getCol());
    }
}