package Players.AIPlayer;

import Interface.Coordinate;
import Interface.PlayerMove;

import java.util.*;

/**
 * Represents game controller
 */
class GameController {
    private int playerId;
    private int nextOpponentPlayerId;
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
        this.nextOpponentPlayerId = this.playerId % playerHomes.size() + 1;
        this.board = new Board(playerHomes, treasures, board);
        this.extraTile = new Tile(MazePathType.fromId(extraTile.get(0)),
                                  TreasureType.fromId(extraTile.get(1)));
    }

    PlayerMove findBestMove() {
        final List<PlayerMove> bestPlayerMoves = new ArrayList<>();
        int myBestManhattanDistanceToGoal = Integer.MAX_VALUE;
        int nextOpponentWorstManhattanDistanceToGoal = 0;

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

                final Coordinate myNextGoalCoordinate = getNextGoalCoordinateForPlayer(tempBoard, this.playerId, newTempExtraTile);

                if(myNextGoalCoordinate == null) {
                    continue;
                }

                final Coordinate myCurrentLocationCoordinate = tempBoard.getPlayerLocation(this.playerId);
                final List<Coordinate> myPathTowardsNextGoal;

                if(myNextGoalCoordinate.equals(myCurrentLocationCoordinate)) {
                    List<Coordinate> path = new ArrayList<>();
                    path.add(myCurrentLocationCoordinate);
                    myPathTowardsNextGoal = path;
                } else {
                    myPathTowardsNextGoal = findBestPathTowardsNextGoalForPlayer(tempBoard, this.playerId, myNextGoalCoordinate);
                }

                int opponentManhattanDistanceToGoal = -1;

                if(myPathTowardsNextGoal.get(myPathTowardsNextGoal.size() - 1).equals(myNextGoalCoordinate)) {
                    if(myBestManhattanDistanceToGoal != 0) {
                        bestPlayerMoves.clear();
                        myBestManhattanDistanceToGoal = 0;
                        nextOpponentWorstManhattanDistanceToGoal = 0;
                    }

                    opponentManhattanDistanceToGoal = calculateNextOpponentBestManhattanDistanceToGoal(tempBoard, newTempExtraTile);
                } else {
                    final int myManhattanDistanceToGoal = calculateManhattanDistance(myPathTowardsNextGoal.get(myPathTowardsNextGoal.size() - 1), myNextGoalCoordinate);

                    if(myManhattanDistanceToGoal <= myBestManhattanDistanceToGoal) {
                        if(myManhattanDistanceToGoal < myBestManhattanDistanceToGoal) {
                            bestPlayerMoves.clear();
                            myBestManhattanDistanceToGoal = myManhattanDistanceToGoal;
                            nextOpponentWorstManhattanDistanceToGoal = 0;
                        }

                        opponentManhattanDistanceToGoal = calculateNextOpponentBestManhattanDistanceToGoal(tempBoard, newTempExtraTile);
                    }
                }

                if(opponentManhattanDistanceToGoal >= nextOpponentWorstManhattanDistanceToGoal) {
                    if(opponentManhattanDistanceToGoal > nextOpponentWorstManhattanDistanceToGoal) {
                        bestPlayerMoves.clear();
                        nextOpponentWorstManhattanDistanceToGoal = opponentManhattanDistanceToGoal;
                    }

                    bestPlayerMoves.add(new PlayerMove(this.playerId, myPathTowardsNextGoal, tileInsertionLocation, mazePathOrientation.ordinal()));
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

    private Coordinate getNextGoalCoordinateForPlayer(final Board board, final int playerId, final Tile extraTile) {
        final TreasureType nextTreasureForPlayer = board.getNextTreasureForPlayer(playerId);

        ///If the player collected all treasures
        if(nextTreasureForPlayer == null) {
            return board.getPlayerHome(playerId);
            //If the player hasn't collected all of their treasures and the next treasure they need to collect is
            //on the board (not on extra tile)
        } else if(!extraTile.getTreasureType().equals(nextTreasureForPlayer)) {
            return board.getNextTreasureLocationForPlayer(playerId);
        }

        //If the next treasure player needs to collect is on the extra tile
        return null;
    }

    private int calculateNextOpponentBestManhattanDistanceToGoal(final Board board, final Tile extraTile) {
        int nextOpponentBestManhattanDistanceToGoal = Integer.MAX_VALUE;

        for(Coordinate tileInsertionLocation : board.getValidTileInsertionLocations()) {
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
                final Board tempBoard = board.createCopy();
                final Tile tempExtraTile = extraTile.createCopy();
                tempExtraTile.setMazePathOrientation(mazePathOrientation);
                final Tile newTempExtraTile = tempBoard.insertTile(tempExtraTile, tileInsertionLocation);

                final Coordinate nextOpponentNextGoalCoordinate = getNextGoalCoordinateForPlayer(tempBoard, this.nextOpponentPlayerId, newTempExtraTile);

                int nextOpponentManhattanDistanceToGoal = Integer.MAX_VALUE;

                if(nextOpponentNextGoalCoordinate != null) {
                    final Coordinate nextOpponentCurrentLocationCoordinate = tempBoard.getPlayerLocation(this.nextOpponentPlayerId);

                    if(nextOpponentCurrentLocationCoordinate.equals(nextOpponentNextGoalCoordinate)) {
                        return 0;
                    } else {
                        final List<Coordinate> nextOpponentPathTowardsNextGoal = findBestPathTowardsNextGoalForPlayer(tempBoard, this.nextOpponentPlayerId, nextOpponentNextGoalCoordinate);

                        if(nextOpponentPathTowardsNextGoal.get(nextOpponentPathTowardsNextGoal.size() - 1).equals(nextOpponentNextGoalCoordinate)) {
                            return 0;
                        } else {
                            nextOpponentManhattanDistanceToGoal = calculateManhattanDistance(nextOpponentPathTowardsNextGoal.get(nextOpponentPathTowardsNextGoal.size() - 1), nextOpponentNextGoalCoordinate);
                        }
                    }
                }

                if(nextOpponentManhattanDistanceToGoal < nextOpponentBestManhattanDistanceToGoal) {
                    nextOpponentBestManhattanDistanceToGoal = nextOpponentManhattanDistanceToGoal;
                }
            }
        }

        return nextOpponentBestManhattanDistanceToGoal;
    }

    private List<Coordinate> findBestPathTowardsNextGoalForPlayer(final Board board,
                                                                  final int playerId,
                                                                  final Coordinate goalCoordinate) {
        final Map<Coordinate, Coordinate> reachableCoordinates = findAllReachableCoordinates(board,
                                                                                             null,
                                                                                             board.getPlayerLocation(playerId),
                                                                                             goalCoordinate,
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

        Stack<Coordinate> bestReversePathTowardsNextGoal = new Stack<>();
        Coordinate pathCoordinate = bestReachableCoordinates.get(new Random().nextInt(bestReachableCoordinates.size()));

        do {
            bestReversePathTowardsNextGoal.push(pathCoordinate);
            pathCoordinate = reachableCoordinates.get(pathCoordinate);
        }
        while(pathCoordinate != null);

        List<Coordinate> bestPathTowardsNextGoal = new ArrayList<>();

        while(!bestReversePathTowardsNextGoal.empty()) {
            bestPathTowardsNextGoal.add(bestReversePathTowardsNextGoal.pop());
        }

        return bestPathTowardsNextGoal;
    }

    private Map<Coordinate, Coordinate> findAllReachableCoordinates(final Board board,
                                                                    final Coordinate arrivedFromLocationCoordinate,
                                                                    final Coordinate currentLocationCoordinate,
                                                                    final Coordinate nextGoalCoordinate,
                                                                    final HashMap<Coordinate, Coordinate> reachableCoordinates) {
        final Tile currentLocationTile = board.getTile(currentLocationCoordinate.getRow(), currentLocationCoordinate.getCol());

        reachableCoordinates.put(currentLocationCoordinate, arrivedFromLocationCoordinate);

        //check neighboring tile to the north
        if(currentLocationCoordinate.getRow() > 0) {
            final Coordinate northTileCoordinate = new Coordinate(currentLocationCoordinate.getRow() - 1, currentLocationCoordinate.getCol());
            final Tile northTile = board.getTile(northTileCoordinate.getRow(), northTileCoordinate.getCol());

            if(currentLocationTile.hasExit(CompassDirection.NORTH) && northTile.hasExit(CompassDirection.SOUTH)) {
                if(northTileCoordinate.equals(nextGoalCoordinate)) {
                    reachableCoordinates.put(northTileCoordinate, currentLocationCoordinate);

                    return reachableCoordinates;
                } else if(!reachableCoordinates.containsKey(northTileCoordinate)) {
                    reachableCoordinates.putAll(findAllReachableCoordinates(board, currentLocationCoordinate, northTileCoordinate, nextGoalCoordinate, reachableCoordinates));
                }
            }
        }

        if(!reachableCoordinates.containsKey(nextGoalCoordinate)) {
            //check neighboring tile to the south
            if (currentLocationCoordinate.getRow() < Coordinate.BOARD_DIM - 1) {
                final Coordinate southTileCoordinate = new Coordinate(currentLocationCoordinate.getRow() + 1, currentLocationCoordinate.getCol());
                final Tile southTile = board.getTile(currentLocationCoordinate.getRow() + 1, currentLocationCoordinate.getCol());

                if (currentLocationTile.hasExit(CompassDirection.SOUTH) && southTile.hasExit(CompassDirection.NORTH)) {
                    if (southTileCoordinate.equals(nextGoalCoordinate)) {
                        reachableCoordinates.put(southTileCoordinate, currentLocationCoordinate);

                        return reachableCoordinates;
                    } else if (!reachableCoordinates.containsKey(southTileCoordinate)) {
                        reachableCoordinates.putAll(findAllReachableCoordinates(board, currentLocationCoordinate, southTileCoordinate, nextGoalCoordinate, reachableCoordinates));
                    }
                }
            }

            if(!reachableCoordinates.containsKey(nextGoalCoordinate)) {
                //check neighboring tile to the west
                if(currentLocationCoordinate.getCol() > 0) {
                    final Coordinate westTileCoordinate = new Coordinate(currentLocationCoordinate.getRow(), currentLocationCoordinate.getCol() - 1);
                    final Tile westTile = board.getTile(currentLocationCoordinate.getRow(), currentLocationCoordinate.getCol() - 1);

                    if(currentLocationTile.hasExit(CompassDirection.WEST) && westTile.hasExit(CompassDirection.EAST)) {
                        if(westTileCoordinate.equals(nextGoalCoordinate)) {
                            reachableCoordinates.put(westTileCoordinate, currentLocationCoordinate);

                            return reachableCoordinates;
                        } else if(!reachableCoordinates.containsKey(westTileCoordinate)) {
                            reachableCoordinates.putAll(findAllReachableCoordinates(board, currentLocationCoordinate, westTileCoordinate, nextGoalCoordinate, reachableCoordinates));
                        }
                    }
                }

                if(!reachableCoordinates.containsKey(nextGoalCoordinate)) {
                    //check neighboring tile to the east
                    if(currentLocationCoordinate.getCol() < Coordinate.BOARD_DIM - 1) {
                        final Coordinate eastTileCoordinate = new Coordinate(currentLocationCoordinate.getRow(), currentLocationCoordinate.getCol() + 1);
                        final Tile eastTile = board.getTile(currentLocationCoordinate.getRow(), currentLocationCoordinate.getCol() + 1);

                        if(currentLocationTile.hasExit(CompassDirection.EAST) && eastTile.hasExit(CompassDirection.WEST)) {
                            if(eastTileCoordinate.equals(nextGoalCoordinate)) {
                                reachableCoordinates.put(eastTileCoordinate, currentLocationCoordinate);

                                return reachableCoordinates;
                            } else if(!reachableCoordinates.containsKey(eastTileCoordinate)) {
                                reachableCoordinates.putAll(findAllReachableCoordinates(board, currentLocationCoordinate, eastTileCoordinate, nextGoalCoordinate, reachableCoordinates));
                            }
                        }
                    }
                }
            }
        }

        return reachableCoordinates;
    }

    private int calculateManhattanDistance(final Coordinate coordinate1, final Coordinate coordinate2) {
        return Math.abs(coordinate2.getRow() - coordinate1.getRow()) + Math.abs(coordinate2.getCol() - coordinate1.getCol());
    }
}