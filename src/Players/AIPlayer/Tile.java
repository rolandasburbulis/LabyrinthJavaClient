package Players.AIPlayer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a tile of the game board
 */
public class Tile implements Serializable {
    private static final long serialVersionUID = 584189200277769504L;
    private MazePathType mazePathType;
    private MazePathOrientation mazePathOrientation;
    private TreasureType treasureType;
    private Set<Integer> players;
    private static Set<String> hasExitLookupTable;

    static {
        hasExitLookupTable = new HashSet<>();

        hasExitLookupTable.add(MazePathType.I.toString() + MazePathOrientation.ZERO.toString() + CompassDirection.NORTH.toString());
        hasExitLookupTable.add(MazePathType.I.toString() + MazePathOrientation.ZERO.toString() + CompassDirection.SOUTH.toString());
        hasExitLookupTable.add(MazePathType.I.toString() + MazePathOrientation.ONE_HUNDRED_EIGHTY.toString() + CompassDirection.NORTH.toString());
        hasExitLookupTable.add(MazePathType.I.toString() + MazePathOrientation.ONE_HUNDRED_EIGHTY.toString() + CompassDirection.SOUTH.toString());
        hasExitLookupTable.add(MazePathType.I.toString() + MazePathOrientation.NINETY.toString() + CompassDirection.EAST.toString());
        hasExitLookupTable.add(MazePathType.I.toString() + MazePathOrientation.NINETY.toString() + CompassDirection.WEST.toString());
        hasExitLookupTable.add(MazePathType.I.toString() + MazePathOrientation.TWO_HUNDRED_SEVENTY.toString() + CompassDirection.EAST.toString());
        hasExitLookupTable.add(MazePathType.I.toString() + MazePathOrientation.TWO_HUNDRED_SEVENTY.toString() + CompassDirection.WEST.toString());
        hasExitLookupTable.add(MazePathType.L.toString() + MazePathOrientation.ZERO.toString() + CompassDirection.WEST.toString());
        hasExitLookupTable.add(MazePathType.L.toString() + MazePathOrientation.ZERO.toString() + CompassDirection.NORTH.toString());
        hasExitLookupTable.add(MazePathType.L.toString() + MazePathOrientation.NINETY.toString() + CompassDirection.NORTH.toString());
        hasExitLookupTable.add(MazePathType.L.toString() + MazePathOrientation.NINETY.toString() + CompassDirection.EAST.toString());
        hasExitLookupTable.add(MazePathType.L.toString() + MazePathOrientation.ONE_HUNDRED_EIGHTY.toString() + CompassDirection.EAST.toString());
        hasExitLookupTable.add(MazePathType.L.toString() + MazePathOrientation.ONE_HUNDRED_EIGHTY.toString() + CompassDirection.SOUTH.toString());
        hasExitLookupTable.add(MazePathType.L.toString() + MazePathOrientation.TWO_HUNDRED_SEVENTY.toString() + CompassDirection.SOUTH.toString());
        hasExitLookupTable.add(MazePathType.L.toString() + MazePathOrientation.TWO_HUNDRED_SEVENTY.toString() + CompassDirection.WEST.toString());
        hasExitLookupTable.add(MazePathType.T.toString() + MazePathOrientation.ZERO.toString() + CompassDirection.NORTH.toString());
        hasExitLookupTable.add(MazePathType.T.toString() + MazePathOrientation.ZERO.toString() + CompassDirection.SOUTH.toString());
        hasExitLookupTable.add(MazePathType.T.toString() + MazePathOrientation.ZERO.toString() + CompassDirection.WEST.toString());
        hasExitLookupTable.add(MazePathType.T.toString() + MazePathOrientation.NINETY.toString() + CompassDirection.NORTH.toString());
        hasExitLookupTable.add(MazePathType.T.toString() + MazePathOrientation.NINETY.toString() + CompassDirection.EAST.toString());
        hasExitLookupTable.add(MazePathType.T.toString() + MazePathOrientation.NINETY.toString() + CompassDirection.WEST.toString());
        hasExitLookupTable.add(MazePathType.T.toString() + MazePathOrientation.ONE_HUNDRED_EIGHTY.toString() + CompassDirection.NORTH.toString());
        hasExitLookupTable.add(MazePathType.T.toString() + MazePathOrientation.ONE_HUNDRED_EIGHTY.toString() + CompassDirection.EAST.toString());
        hasExitLookupTable.add(MazePathType.T.toString() + MazePathOrientation.ONE_HUNDRED_EIGHTY.toString() + CompassDirection.SOUTH.toString());
        hasExitLookupTable.add(MazePathType.T.toString() + MazePathOrientation.TWO_HUNDRED_SEVENTY.toString() + CompassDirection.EAST.toString());
        hasExitLookupTable.add(MazePathType.T.toString() + MazePathOrientation.TWO_HUNDRED_SEVENTY.toString() + CompassDirection.SOUTH.toString());
        hasExitLookupTable.add(MazePathType.T.toString() + MazePathOrientation.TWO_HUNDRED_SEVENTY.toString() + CompassDirection.WEST.toString());
    }

    public Tile(final MazePathType mazePathType,
                final TreasureType treasureType) {
        this.mazePathType = mazePathType;
        this.treasureType = treasureType;
        this.players = new HashSet<>();
    }

    public Tile(final MazePathType mazePathType,
                final MazePathOrientation mazePathOrientation,
                final TreasureType treasureType) {
        this(mazePathType, treasureType);
        this.mazePathOrientation = mazePathOrientation;
    }

    public Tile(final MazePathType mazePathType,
                final MazePathOrientation mazePathOrientation,
                final TreasureType treasureType,
                final int player) {
        this(mazePathType, mazePathOrientation, treasureType);
        this.players.add(player);
    }

    public MazePathType getMazePathType() {
        return this.mazePathType;
    }

    public MazePathOrientation getMazePathOrientation() {
        return this.mazePathOrientation;
    }

    public TreasureType getTreasureType() {
        return this.treasureType;
    }

    public void setMazePathOrientation(final MazePathOrientation mazePathOrientation) {
        this.mazePathOrientation = mazePathOrientation;
    }

    public Set<Integer> getPlayers() {
        return this.players;
    }

    public void addPlayers(final Set<Integer> players) {
        this.players.addAll(players);
    }

    public void addPlayer(final int player) {
        this.players.add(player);
    }

    public void removePlayer(final int player) {
        this.players.remove(player);
    }

    public void removeAllPlayers() {
        this.players.clear();
    }

    public boolean hasPlayer() {
        return !this.players.isEmpty();
    }

    public boolean hasExit(final CompassDirection compassDirection) {
        if(this.mazePathOrientation == null) {
            throw new IllegalStateException("This tile does not have an orientation set, therefore checking if it has an exit in a particular compass direction is not valid.");
        }

        return Tile.hasExitLookupTable.contains(this.mazePathType.toString() +
                                                this.mazePathOrientation.toString() +
                                                compassDirection.toString());
    }
}