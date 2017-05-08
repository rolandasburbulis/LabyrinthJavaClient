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

    static {
        hasExitLookupTable = new boolean[MazePathType.values().length]
                [MazePathOrientation.values().length]
                [CompassDirection.values().length];

        for(int mazePathType = 0; mazePathType < MazePathType.values().length; mazePathType++) {
            for(int mazePathOrientation = 0; mazePathOrientation < MazePathOrientation.values().length; mazePathOrientation++) {
                for(int compassDirection = 0; compassDirection < CompassDirection.values().length; compassDirection++) {
                    hasExitLookupTable[mazePathType][mazePathOrientation][compassDirection] = false;
                }
            }
        }

        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.ZERO.ordinal()][CompassDirection.NORTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.ZERO.ordinal()][CompassDirection.SOUTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.ONE_HUNDRED_EIGHTY.ordinal()][CompassDirection.NORTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.ONE_HUNDRED_EIGHTY.ordinal()][CompassDirection.SOUTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.NINETY.ordinal()][CompassDirection.EAST.ordinal()] = true;
        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.NINETY.ordinal()][CompassDirection.WEST.ordinal()] = true;
        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.TWO_HUNDRED_SEVENTY.ordinal()][CompassDirection.EAST.ordinal()] = true;
        hasExitLookupTable[MazePathType.I.ordinal()][MazePathOrientation.TWO_HUNDRED_SEVENTY.ordinal()][CompassDirection.WEST.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.ZERO.ordinal()][CompassDirection.WEST.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.ZERO.ordinal()][CompassDirection.NORTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.NINETY.ordinal()][CompassDirection.NORTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.NINETY.ordinal()][CompassDirection.EAST.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.ONE_HUNDRED_EIGHTY.ordinal()][CompassDirection.EAST.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.ONE_HUNDRED_EIGHTY.ordinal()][CompassDirection.SOUTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.TWO_HUNDRED_SEVENTY.ordinal()][CompassDirection.SOUTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.L.ordinal()][MazePathOrientation.TWO_HUNDRED_SEVENTY.ordinal()][CompassDirection.WEST.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.ZERO.ordinal()][CompassDirection.SOUTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.ZERO.ordinal()][CompassDirection.WEST.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.ZERO.ordinal()][CompassDirection.NORTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.NINETY.ordinal()][CompassDirection.WEST.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.NINETY.ordinal()][CompassDirection.NORTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.NINETY.ordinal()][CompassDirection.EAST.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.ONE_HUNDRED_EIGHTY.ordinal()][CompassDirection.NORTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.ONE_HUNDRED_EIGHTY.ordinal()][CompassDirection.EAST.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.ONE_HUNDRED_EIGHTY.ordinal()][CompassDirection.SOUTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.TWO_HUNDRED_SEVENTY.ordinal()][CompassDirection.EAST.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.TWO_HUNDRED_SEVENTY.ordinal()][CompassDirection.SOUTH.ordinal()] = true;
        hasExitLookupTable[MazePathType.T.ordinal()][MazePathOrientation.TWO_HUNDRED_SEVENTY.ordinal()][CompassDirection.WEST.ordinal()] = true;
    }

    Tile(final MazePathType mazePathType,
         final TreasureType treasureType) {
        this.mazePathType = mazePathType;
        this.treasureType = treasureType;
        this.players = new HashSet<>();
    }

    Tile(final MazePathType mazePathType,
         final MazePathOrientation mazePathOrientation,
         final TreasureType treasureType) {
        this(mazePathType, treasureType);
        this.mazePathOrientation = mazePathOrientation;
    }

    Tile(final MazePathType mazePathType,
         final MazePathOrientation mazePathOrientation,
         final TreasureType treasureType,
         final int player) {
        this(mazePathType, mazePathOrientation, treasureType);
        this.players.add(player);
    }

    MazePathType getMazePathType() {
        return this.mazePathType;
    }

    MazePathOrientation getMazePathOrientation() {
        return this.mazePathOrientation;
    }

    TreasureType getTreasureType() {
        return this.treasureType;
    }

    void setMazePathOrientation(final MazePathOrientation mazePathOrientation) {
        this.mazePathOrientation = mazePathOrientation;
    }

    Set<Integer> getPlayers() {
        return this.players;
    }

    void addPlayers(final Set<Integer> players) {
        this.players.addAll(players);
    }

    void addPlayer(final int player) {
        this.players.add(player);
    }

    void removePlayer(final int player) {
        this.players.remove(player);
    }

    void removeAllPlayers() {
        this.players.clear();
    }

    boolean hasPlayer() {
        return !this.players.isEmpty();
    }

    boolean hasExit(final CompassDirection compassDirection) {
        if(this.mazePathOrientation == null) {
            throw new IllegalStateException("This tile does not have an orientation set, therefore checking if it has an exit in a particular compass direction is not valid.");
        }

        return Tile.hasExitLookupTable[this.mazePathType.ordinal()][this.mazePathOrientation.ordinal()][compassDirection.ordinal()];
    }
}