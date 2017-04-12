package Players.AIPlayer;

import java.io.*;
import java.util.HashSet;
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
    private static Set<Integer> hasExitLookupTable;

    static {
        hasExitLookupTable = new HashSet<>();

        hasExitLookupTable.add(MazePathType.I.getId() * 100 + MazePathOrientation.ZERO.getId() * 10 + CompassDirection.NORTH.ordinal());
        hasExitLookupTable.add(MazePathType.I.getId() * 100 + MazePathOrientation.ZERO.getId() * 10 + CompassDirection.SOUTH.ordinal());
        hasExitLookupTable.add(MazePathType.I.getId() * 100 + MazePathOrientation.ONE_HUNDRED_EIGHTY.getId() * 10 + CompassDirection.NORTH.ordinal());
        hasExitLookupTable.add(MazePathType.I.getId() * 100 + MazePathOrientation.ONE_HUNDRED_EIGHTY.getId() * 10 + CompassDirection.SOUTH.ordinal());
        hasExitLookupTable.add(MazePathType.I.getId() * 100 + MazePathOrientation.NINETY.getId() * 10 + CompassDirection.EAST.ordinal());
        hasExitLookupTable.add(MazePathType.I.getId() * 100 + MazePathOrientation.NINETY.getId() * 10 + CompassDirection.WEST.ordinal());
        hasExitLookupTable.add(MazePathType.I.getId() * 100 + MazePathOrientation.TWO_HUNDRED_SEVENTY.getId() * 10 + CompassDirection.EAST.ordinal());
        hasExitLookupTable.add(MazePathType.I.getId() * 100 + MazePathOrientation.TWO_HUNDRED_SEVENTY.getId() * 10 + CompassDirection.WEST.ordinal());
        hasExitLookupTable.add(MazePathType.L.getId() * 100 + MazePathOrientation.ZERO.getId() * 10 + CompassDirection.WEST.ordinal());
        hasExitLookupTable.add(MazePathType.L.getId() * 100 + MazePathOrientation.ZERO.getId() * 10 + CompassDirection.NORTH.ordinal());
        hasExitLookupTable.add(MazePathType.L.getId() * 100 + MazePathOrientation.NINETY.getId() * 10 + CompassDirection.NORTH.ordinal());
        hasExitLookupTable.add(MazePathType.L.getId() * 100 + MazePathOrientation.NINETY.getId() * 10 + CompassDirection.EAST.ordinal());
        hasExitLookupTable.add(MazePathType.L.getId() * 100 + MazePathOrientation.ONE_HUNDRED_EIGHTY.getId() * 10 + CompassDirection.EAST.ordinal());
        hasExitLookupTable.add(MazePathType.L.getId() * 100 + MazePathOrientation.ONE_HUNDRED_EIGHTY.getId() * 10 + CompassDirection.SOUTH.ordinal());
        hasExitLookupTable.add(MazePathType.L.getId() * 100 + MazePathOrientation.TWO_HUNDRED_SEVENTY.getId() * 10 + CompassDirection.SOUTH.ordinal());
        hasExitLookupTable.add(MazePathType.L.getId() * 100 + MazePathOrientation.TWO_HUNDRED_SEVENTY.getId() * 10 + CompassDirection.WEST.ordinal());
        hasExitLookupTable.add(MazePathType.T.getId() * 100 + MazePathOrientation.ZERO.getId() * 10 + CompassDirection.NORTH.ordinal());
        hasExitLookupTable.add(MazePathType.T.getId() * 100 + MazePathOrientation.ZERO.getId() * 10 + CompassDirection.SOUTH.ordinal());
        hasExitLookupTable.add(MazePathType.T.getId() * 100 + MazePathOrientation.ZERO.getId() * 10 + CompassDirection.WEST.ordinal());
        hasExitLookupTable.add(MazePathType.T.getId() * 100 + MazePathOrientation.NINETY.getId() * 10 + CompassDirection.NORTH.ordinal());
        hasExitLookupTable.add(MazePathType.T.getId() * 100 + MazePathOrientation.NINETY.getId() * 10 + CompassDirection.EAST.ordinal());
        hasExitLookupTable.add(MazePathType.T.getId() * 100 + MazePathOrientation.NINETY.getId() * 10 + CompassDirection.WEST.ordinal());
        hasExitLookupTable.add(MazePathType.T.getId() * 100 + MazePathOrientation.ONE_HUNDRED_EIGHTY.getId() * 10 + CompassDirection.NORTH.ordinal());
        hasExitLookupTable.add(MazePathType.T.getId() * 100 + MazePathOrientation.ONE_HUNDRED_EIGHTY.getId() * 10 + CompassDirection.EAST.ordinal());
        hasExitLookupTable.add(MazePathType.T.getId() * 100 + MazePathOrientation.ONE_HUNDRED_EIGHTY.getId() * 10 + CompassDirection.SOUTH.ordinal());
        hasExitLookupTable.add(MazePathType.T.getId() * 100 + MazePathOrientation.TWO_HUNDRED_SEVENTY.getId() * 10 + CompassDirection.EAST.ordinal());
        hasExitLookupTable.add(MazePathType.T.getId() * 100 + MazePathOrientation.TWO_HUNDRED_SEVENTY.getId() * 10 + CompassDirection.SOUTH.ordinal());
        hasExitLookupTable.add(MazePathType.T.getId() * 100 + MazePathOrientation.TWO_HUNDRED_SEVENTY.getId() * 10 + CompassDirection.WEST.ordinal());
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

        return Tile.hasExitLookupTable.contains(this.mazePathType.getId() * 100 +
                                                this.mazePathOrientation.getId() * 10 +
                                                compassDirection.ordinal());
    }

    Tile createCopy() {
        Tile copy = null;

        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            out.close();

            final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            copy = (Tile) in.readObject();
        }
        catch(final IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return copy;
    }
}