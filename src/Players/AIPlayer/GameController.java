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
     * Initializes the GameController with the specified parameters
     *
     * @param playerId - the id of this player
	 * @param playerHomes - starting locations for each player, in order
     * @param treasures - ordered list of treasures for each player
     * @param board - 2-d list of [Tile ID, Rotation, Treasure]
     * Tile IDs:  0 = L tile, 1 = T tile, 2 = I tile
     * Rotations: 0 = 0 degrees, 1 = 90 degrees, 2 = 180 degrees,
     *  	      3 = 270 degrees, all clockwise
     * Treasures: -1 = no treasure, 0-23 = corresponding treasure
     * @param extraTile - contains [Extra Tile ID, Treasure]
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

    /**
     * Generates and returns the player's next best move
     *
     * @return player's next best move
     */
    PlayerMove findBestMove() {
        final List<PlayerMove> bestPlayerMoves = new ArrayList<>();
        int myBestManhattanDistanceToGoal = Integer.MAX_VALUE;
        int nextOpponentWorstManhattanDistanceToGoal = 0;

        //Consider all valid tile insertion locations
        for(Coordinate tileInsertionLocation : this.board.getValidTileInsertionLocations()) {
            //Consider all extra tile orientations
            for(MazePathOrientation mazePathOrientation : MazePathOrientation.values()) {
                //Ignore 180 and 270 degree maze path orientation for 'I' maze type path, as they are equivalent
                //to 0 and 90 degree maze path orientations.
                if(extraTile.getMazePathType().equals(MazePathType.I) &&
                        (mazePathOrientation.equals(MazePathOrientation.ONE_HUNDRED_EIGHTY) ||
                                mazePathOrientation.equals(MazePathOrientation.TWO_HUNDRED_SEVENTY))) {
                    continue;
                }

                //Create a copy of the current board and extra tile and insert the extra tile in the chosen insertion
                //location with the chosen tile orientation
                final Board tempBoard = (Board)Cloner.deepCopy(this.board);
                final Tile tempExtraTile = (Tile)Cloner.deepCopy(this.extraTile);
                tempExtraTile.setMazePathOrientation(mazePathOrientation);
                final Tile newTempExtraTile = tempBoard.insertTile(tempExtraTile, tileInsertionLocation);

                //Get the next goal coordinate for the player, which is the coordinate of the tile having the player's
                //next treasure or, if the player already collected all of its treasures, the coordinate of the  player's
                //home tile.
                final Coordinate myNextGoalCoordinate = getNextGoalCoordinateForPlayer(tempBoard, this.playerId, newTempExtraTile);

                //Goal coordinate would be null if it is not reachable, which would happen if player's next treasure
                //is on the resulting extra tile after extra tile insertion
                if(myNextGoalCoordinate == null) {
                    continue;
                }

                final Coordinate myCurrentLocationCoordinate = tempBoard.getPlayerLocation(this.playerId);
                final List<Coordinate> myPathTowardsNextGoal;

                //If the player is already on the tile in needs to reach next, the current location is the desired path
                //(no pawn move)
                if(myNextGoalCoordinate.equals(myCurrentLocationCoordinate)) {
                    List<Coordinate> path = new ArrayList<>();
                    path.add(myCurrentLocationCoordinate);
                    myPathTowardsNextGoal = path;
                //If the player's goal coordinate is not the player's current location, find the best path towards the next goal
                } else {
                    myPathTowardsNextGoal = findBestPathTowardsNextGoalForPlayer(tempBoard, this.playerId, myNextGoalCoordinate);
                }

                int nextOpponentManhattanDistanceToGoal = -1;

                //If the last coordinate in the path is the goal coordinate, that means we have a path to the goal coordinate
                if(myPathTowardsNextGoal.get(myPathTowardsNextGoal.size() - 1).equals(myNextGoalCoordinate)) {
                    //If this is the first path we found leading all the way to the goal coordinate
                    if(myBestManhattanDistanceToGoal != 0) {
                        bestPlayerMoves.clear();
                        myBestManhattanDistanceToGoal = 0;
                        nextOpponentWorstManhattanDistanceToGoal = 0;
                    }

                    //Calculate next opponent's best Manhattan distance to their next goal coordinate, considering all
                    //possible insertions that the next opponent would be allowed to do after this move
                    nextOpponentManhattanDistanceToGoal = calculateNextOpponentBestManhattanDistanceToGoal(tempBoard, newTempExtraTile);
                //If the last coordinate in the path is not the goal coordinate, the goal coordinate is not reachable
                } else {
                    //Calculate Manhattan distance from the closest approach to the next goal coordinate to the goal coordinate
                    final int myManhattanDistanceToGoal = calculateManhattanDistance(myPathTowardsNextGoal.get(myPathTowardsNextGoal.size() - 1), myNextGoalCoordinate);

                    //If the Manhattan distance from the closest approach to the next goal coordinate to the goal coordinate is
                    //better than or equal than the approach to the goal coordinate from previously considered insertions
                    if(myManhattanDistanceToGoal <= myBestManhattanDistanceToGoal) {
                        //If the Manhattan distance from the closest approach to the next goal coordinate to the goal coordinate is
                        //better than the approach to the goal coordinate from previously considered insertions
                        if(myManhattanDistanceToGoal < myBestManhattanDistanceToGoal) {
                            bestPlayerMoves.clear();
                            myBestManhattanDistanceToGoal = myManhattanDistanceToGoal;
                            nextOpponentWorstManhattanDistanceToGoal = 0;
                        }

                        //Calculate next opponent's best Manhattan distance to their next goal coordinate, considering all
                        //possible insertions that the next opponent would be allowed to do after this move
                        nextOpponentManhattanDistanceToGoal = calculateNextOpponentBestManhattanDistanceToGoal(tempBoard, newTempExtraTile);
                    }
                }

                //If the opponent's best Manhattan distance distance to their next goal coordinate is worse than or equal
                //than the approach to their goal coordinate from previously considered insertions
                if(nextOpponentManhattanDistanceToGoal >= nextOpponentWorstManhattanDistanceToGoal) {
                    //If the opponent's best Manhattan distance distance to their next goal coordinate is worse than the
                    //approach to their goal coordinate from previously considered insertions
                    if(nextOpponentManhattanDistanceToGoal > nextOpponentWorstManhattanDistanceToGoal) {
                        bestPlayerMoves.clear();
                        nextOpponentWorstManhattanDistanceToGoal = nextOpponentManhattanDistanceToGoal;
                    }

                    //Save this move
                    bestPlayerMoves.add(new PlayerMove(this.playerId, myPathTowardsNextGoal, tileInsertionLocation, mazePathOrientation.ordinal()));
                }
            }
        }

        //Return a random move from the list of equally good moves
        return bestPlayerMoves.get(new Random().nextInt(bestPlayerMoves.size()));
    }

    /**
     * 	Notifies the player that a specified move was just made.  The AI player updates
     * the state of the game with this move.  It is assumed that all moves are given in
     * the order that they are made.  It is also assumed that all passed moves are valid.
     *
     * @param playerMove - the move that was just made
     */
    void handlePlayerMove(final PlayerMove playerMove) {
        //Set extra tile to the orientation specified in the specified player move
        this.extraTile.setMazePathOrientation(MazePathOrientation.fromId(playerMove.getTileRotation()));
        //Insert the extra tile at the insertion location specified in the specified player move
        this.extraTile = this.board.insertTile(this.extraTile, playerMove.getTileInsertion());

        final List<Coordinate> playerPath = playerMove.getPath();

        //Move the player specified in the specified player move to the destination location specified
        //in the specified player move
        this.board.movePlayer(playerMove.getPlayerId(), playerPath.get(playerPath.size() - 1));
    }

    private Coordinate getNextGoalCoordinateForPlayer(final Board board, final int playerId,
                                                      final Tile extraTile) {
        final TreasureType nextTreasureForPlayer = board.getNextTreasureForPlayer(playerId);

        ///If the player collected all treasures
        if(nextTreasureForPlayer == null) {
            return board.getPlayerHome(playerId);
        //If the player hasn't collected all of their treasures and the next treasure they need
        //to collect is on the board (not on extra tile)
        } else if(!extraTile.getTreasureType().equals(nextTreasureForPlayer)) {
            return board.getNextTreasureLocationForPlayer(playerId);
        }

        //If the next treasure player needs to collect is on the extra tile
        return null;
    }

    private int calculateNextOpponentBestManhattanDistanceToGoal(final Board board,
                                                                 final Tile extraTile) {
        int nextOpponentBestManhattanDistanceToGoal = Integer.MAX_VALUE;

        for(Coordinate tileInsertionLocation : board.getValidTileInsertionLocations()) {
            for(MazePathOrientation mazePathOrientation : MazePathOrientation.values()) {
                //Ignore 180 and 270 degree maze path orientation for 'I' maze type path, as they
                //are equivalent to 0 and 90 degree maze path orientations.
                if(extraTile.getMazePathType().equals(MazePathType.I) &&
                        (mazePathOrientation.equals(MazePathOrientation.ONE_HUNDRED_EIGHTY) ||
                                mazePathOrientation.equals(MazePathOrientation.TWO_HUNDRED_SEVENTY))) {
                    continue;
                }

                //Create a copy of the current board and extra tile and insert the extra tile in the chosen insertion
                //location with the chosen tile orientation
                final Board tempBoard = (Board)Cloner.deepCopy(board);
                final Tile tempExtraTile = (Tile)Cloner.deepCopy(extraTile);
                tempExtraTile.setMazePathOrientation(mazePathOrientation);
                final Tile newTempExtraTile = tempBoard.insertTile(tempExtraTile, tileInsertionLocation);

                final Coordinate nextOpponentNextGoalCoordinate = getNextGoalCoordinateForPlayer(tempBoard,
                        this.nextOpponentPlayerId, newTempExtraTile);

                int nextOpponentManhattanDistanceToGoal = Integer.MAX_VALUE;

                if(nextOpponentNextGoalCoordinate != null) {
                    final Coordinate nextOpponentCurrentLocationCoordinate = tempBoard.getPlayerLocation(
                            this.nextOpponentPlayerId);

                    if(nextOpponentCurrentLocationCoordinate.equals(nextOpponentNextGoalCoordinate)) {
                        return 0;
                    } else {
                        final List<Coordinate> nextOpponentPathTowardsNextGoal = findBestPathTowardsNextGoalForPlayer(
                                tempBoard, this.nextOpponentPlayerId, nextOpponentNextGoalCoordinate);

                        if(nextOpponentPathTowardsNextGoal.get(nextOpponentPathTowardsNextGoal.size() - 1)
                                .equals(nextOpponentNextGoalCoordinate)) {
                            return 0;
                        } else {
                            nextOpponentManhattanDistanceToGoal = calculateManhattanDistance(
                                    nextOpponentPathTowardsNextGoal.get(nextOpponentPathTowardsNextGoal.size() - 1),
                                    nextOpponentNextGoalCoordinate);
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
                null, board.getPlayerLocation(playerId), goalCoordinate, new HashMap<>());

        final List<Coordinate> bestReachableCoordinates = new ArrayList<>();

        if(reachableCoordinates.containsKey(goalCoordinate)) {
            bestReachableCoordinates.add(goalCoordinate);
        } else {
            int bestManhattanDistanceReachableCoordinateToGoal = Integer.MAX_VALUE;

            for(Coordinate reachableCoordinate : reachableCoordinates.keySet()) {
                final int manhattanDistanceReachableCoordinateToGoal = calculateManhattanDistance(reachableCoordinate,
                        goalCoordinate);

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
            final Coordinate northTileCoordinate = new Coordinate(currentLocationCoordinate.getRow() - 1,
                    currentLocationCoordinate.getCol());
            final Tile northTile = board.getTile(northTileCoordinate.getRow(), northTileCoordinate.getCol());

            if(currentLocationTile.hasExit(CompassDirection.NORTH) && northTile.hasExit(CompassDirection.SOUTH)) {
                if(northTileCoordinate.equals(nextGoalCoordinate)) {
                    reachableCoordinates.put(northTileCoordinate, currentLocationCoordinate);

                    return reachableCoordinates;
                } else if(!reachableCoordinates.containsKey(northTileCoordinate)) {
                    reachableCoordinates.putAll(findAllReachableCoordinates(board, currentLocationCoordinate,
                            northTileCoordinate, nextGoalCoordinate, reachableCoordinates));
                }
            }
        }

        if(!reachableCoordinates.containsKey(nextGoalCoordinate)) {
            //check neighboring tile to the south
            if (currentLocationCoordinate.getRow() < Coordinate.BOARD_DIM - 1) {
                final Coordinate southTileCoordinate = new Coordinate(currentLocationCoordinate.getRow() + 1,
                        currentLocationCoordinate.getCol());
                final Tile southTile = board.getTile(currentLocationCoordinate.getRow() + 1,
                        currentLocationCoordinate.getCol());

                if (currentLocationTile.hasExit(CompassDirection.SOUTH) && southTile.hasExit(CompassDirection.NORTH)) {
                    if (southTileCoordinate.equals(nextGoalCoordinate)) {
                        reachableCoordinates.put(southTileCoordinate, currentLocationCoordinate);

                        return reachableCoordinates;
                    } else if (!reachableCoordinates.containsKey(southTileCoordinate)) {
                        reachableCoordinates.putAll(findAllReachableCoordinates(board, currentLocationCoordinate,
                                southTileCoordinate, nextGoalCoordinate, reachableCoordinates));
                    }
                }
            }

            if(!reachableCoordinates.containsKey(nextGoalCoordinate)) {
                //check neighboring tile to the west
                if(currentLocationCoordinate.getCol() > 0) {
                    final Coordinate westTileCoordinate = new Coordinate(currentLocationCoordinate.getRow(),
                            currentLocationCoordinate.getCol() - 1);
                    final Tile westTile = board.getTile(currentLocationCoordinate.getRow(),
                            currentLocationCoordinate.getCol() - 1);

                    if(currentLocationTile.hasExit(CompassDirection.WEST) && westTile.hasExit(CompassDirection.EAST)) {
                        if(westTileCoordinate.equals(nextGoalCoordinate)) {
                            reachableCoordinates.put(westTileCoordinate, currentLocationCoordinate);

                            return reachableCoordinates;
                        } else if(!reachableCoordinates.containsKey(westTileCoordinate)) {
                            reachableCoordinates.putAll(findAllReachableCoordinates(board, currentLocationCoordinate,
                                    westTileCoordinate, nextGoalCoordinate, reachableCoordinates));
                        }
                    }
                }

                if(!reachableCoordinates.containsKey(nextGoalCoordinate)) {
                    //check neighboring tile to the east
                    if(currentLocationCoordinate.getCol() < Coordinate.BOARD_DIM - 1) {
                        final Coordinate eastTileCoordinate = new Coordinate(currentLocationCoordinate.getRow(),
                                currentLocationCoordinate.getCol() + 1);
                        final Tile eastTile = board.getTile(currentLocationCoordinate.getRow(),
                                currentLocationCoordinate.getCol() + 1);

                        if(currentLocationTile.hasExit(CompassDirection.EAST) && eastTile.hasExit(CompassDirection.WEST)) {
                            if(eastTileCoordinate.equals(nextGoalCoordinate)) {
                                reachableCoordinates.put(eastTileCoordinate, currentLocationCoordinate);

                                return reachableCoordinates;
                            } else if(!reachableCoordinates.containsKey(eastTileCoordinate)) {
                                reachableCoordinates.putAll(findAllReachableCoordinates(board, currentLocationCoordinate,
                                        eastTileCoordinate, nextGoalCoordinate, reachableCoordinates));
                            }
                        }
                    }
                }
            }
        }

        return reachableCoordinates;
    }

    private int calculateManhattanDistance(final Coordinate coordinate1, final Coordinate coordinate2) {
        return Math.abs(coordinate2.getRow() - coordinate1.getRow()) +
                Math.abs(coordinate2.getCol() - coordinate1.getCol());
    }
}