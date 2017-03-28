package Players.ai;

/**
 * Represents a tile of the game board
 */
public class Tile {
    private MazePathType mazePathType;
    private TreasureType treasureType;
    private MazePathOrientation mazePathOrientation;
    private byte playerId;

    public Tile(final MazePathType mazePathType,
                final MazePathOrientation mazePathOrientation,
                final TreasureType treasureType,
                final byte playerId) {
        this.mazePathType = mazePathType;
        this.mazePathOrientation = mazePathOrientation;
        this.treasureType = treasureType;
        this.playerId = playerId;
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

    public byte getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(final byte playerId) {
        this.playerId = playerId;
    }
}