package Players.AIPlayer;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a tile of the game board
 */
class Tile  {
    private MazePathType mazePathType;
    private MazePathOrientation mazePathOrientation;
    private TreasureType treasureType;
    private Set<Integer> players;
    private static boolean[][][] hasExitLookupTable;

    /**
     * Initializes hasExitLookupTable to return true for the MazeTypePath, MazePathOrientation and CompassDirection
     * combinations which cause the tile to have an exit
     */
    static {
        hasExitLookupTable = new boolean[MazePathType.values().length]
                [MazePathOrientation.values().length]
                [CompassDirection.values().length];

        for(int mazePathType = 0; mazePathType < MazePathType.values().length; mazePathType++) {
            for(int mazePathOrientation = 0; mazePathOrientation < MazePathOrientation.values().length;
                mazePathOrientation++) {
                for(int compassDirection = 0; compassDirection < CompassDirection.values().length;
                    compassDirection++) {
                    hasExitLookupTable[mazePathType][mazePathOrientation][compassDirection] = false;
                }
            }
        }

        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.ZERO.ordinal()]
                [CompassDirection.NORTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.ZERO.ordinal()]
                [CompassDirection.SOUTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.ONE_HUNDRED_EIGHTY.ordinal()]
                [CompassDirection.NORTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.ONE_HUNDRED_EIGHTY.ordinal()]
                [CompassDirection.SOUTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.NINETY.ordinal()]
                [CompassDirection.EAST.ordinal()] = true;
        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.NINETY.ordinal()]
                [CompassDirection.WEST.ordinal()] = true;
        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.TWO_HUNDRED_SEVENTY.ordinal()]
                [CompassDirection.EAST.ordinal()] = true;
        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.TWO_HUNDRED_SEVENTY.ordinal()]
                [CompassDirection.WEST.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.ZERO.ordinal()]
                [CompassDirection.WEST.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.ZERO.ordinal()]
                [CompassDirection.NORTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.NINETY.ordinal()]
                [CompassDirection.NORTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.NINETY.ordinal()]
                [CompassDirection.EAST.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.ONE_HUNDRED_EIGHTY.ordinal()]
                [CompassDirection.EAST.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.ONE_HUNDRED_EIGHTY.ordinal()]
                [CompassDirection.SOUTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.TWO_HUNDRED_SEVENTY.ordinal()]
                [CompassDirection.SOUTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.TWO_HUNDRED_SEVENTY.ordinal()]
                [CompassDirection.WEST.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.ZERO.ordinal()]
                [CompassDirection.SOUTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.ZERO.ordinal()]
                [CompassDirection.WEST.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.ZERO.ordinal()]
                [CompassDirection.NORTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.NINETY.ordinal()]
                [CompassDirection.WEST.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.NINETY.ordinal()]
                [CompassDirection.NORTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.NINETY.ordinal()]
                [CompassDirection.EAST.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.ONE_HUNDRED_EIGHTY.ordinal()]
                [CompassDirection.NORTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.ONE_HUNDRED_EIGHTY.ordinal()]
                [CompassDirection.EAST.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.ONE_HUNDRED_EIGHTY.ordinal()]
                [CompassDirection.SOUTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.TWO_HUNDRED_SEVENTY.ordinal()]
                [CompassDirection.EAST.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.TWO_HUNDRED_SEVENTY.ordinal()]
                [CompassDirection.SOUTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.TWO_HUNDRED_SEVENTY.ordinal()]
                [CompassDirection.WEST.ordinal()] = true;
    }

    /**
     * Initializes the Tile with the specified parameters.  No tile orientation is
     * set for the tile and it is set to have no players.  It is used to initialize
     * the extra tile, as the extra tile has no orientation and no players on it.
     *
     * @param mazePathType - maze path type of the tile
     * @param treasureType - treasure type located on the tile
     */
    Tile(final MazePathType mazePathType,
         final TreasureType treasureType) {
        this.mazePathType = mazePathType;
        this.treasureType = treasureType;
        this.players = new HashSet<>();
    }

    /**
     * Initializes the Tile with the specified parameters.  It is set to have
     * no players.
     *
     * @param mazePathType - maze path type of the tile
     * @param mazePathOrientation - maze path orientation of the tile
     * @param treasureType - treasure type located on the tile
     */
    Tile(final MazePathType mazePathType,
         final MazePathOrientation mazePathOrientation,
         final TreasureType treasureType) {
        this(mazePathType, treasureType);
        this.mazePathOrientation = mazePathOrientation;
    }

    /**
     * Initializes the Tile with the specified parameters
     *
     * @param mazePathType - maze path type of the tile
     * @param mazePathOrientation - maze path orientation of the tile
     * @param treasureType - treasure type located on the tile
     * @param player - player located on the tile
     */
    Tile(final MazePathType mazePathType,
         final MazePathOrientation mazePathOrientation,
         final TreasureType treasureType,
         final int player) {
        this(mazePathType, mazePathOrientation, treasureType);
        this.players.add(player);
    }

    /**
     * Gets the maze path type of the tile
     *
     * @return maze path type of the tile
     */
    MazePathType getMazePathType() {
        return this.mazePathType;
    }

    /**
     * Gets the maze path orientation of the tile
     *
     * @return maze path orientation of the tile
     */
    MazePathOrientation getMazePathOrientation() {
        return this.mazePathOrientation;
    }

    /**
     * Sets the maze path orientation of the tile
     *
     * @param mazePathOrientation - maze path orientation of the tile
     */
    void setMazePathOrientation(final MazePathOrientation mazePathOrientation) {
        this.mazePathOrientation = mazePathOrientation;
    }

    /**
     * Gets the treasure type located on the tile
     *
     * @return treasure type located on the tile
     */
    TreasureType getTreasureType() {
        return this.treasureType;
    }

    /**
     * Gets the set of players located on the tile
     *
     * @return set of players located on the tile
     */
    Set<Integer> getPlayers() {
        return this.players;
    }

    /**
     * Adds the specified set of players to the set of players located on the tile
     *
     * @param players set of players to be added to the set of players located on
     * the tile
     */
    void addPlayers(final Set<Integer> players) {
        this.players.addAll(players);
    }

    /**
     * Add the specified player to the set of players located on the tile
     *
     * @param player player to be added to the set of players located on the tile
     */
    void addPlayer(final int player) {
        this.players.add(player);
    }

    /**
     * Removes the specified player from the set of players located on the tile
     *
     * @param player player to be removed from the set of players located on the tile
     */
    void removePlayer(final int player) {
        this.players.remove(player);
    }

    /**
     * Removes all players from the tile
     */
    void removeAllPlayers() {
        this.players.clear();
    }

    /**
     * Checks if the tile has any players on it
     *
     * @return True if the tile has any players on it; false otherwise
     */
    boolean hasPlayer() {
        return !this.players.isEmpty();
    }

    /**
     * Checks if the tile has an exit in the specified compass direction.  Throws
     * IllegalStateException if the tile does not have orientation set.
     *
     * @param compassDirection - compass direction for which the tile is to be
     * checked to see if it has an exit
     *
     * @return True if the tile has an exit in the specified compass direction; false
     * otherwise
     *
     * @throws IllegalStateException if the tile does not have orientation set
     */
    boolean hasExit(final CompassDirection compassDirection) {
        if(this.mazePathOrientation == null) {
            throw new IllegalStateException("This tile does not have an orientation set, therefore checking " +
                "if it has an exit in a particular compass direction is not valid.");
        }

        return Tile.hasExitLookupTable[this.mazePathType.ordinal()][this.mazePathOrientation.ordinal()]
                [compassDirection.ordinal()];
    }
}