package Players.ai;

import java.util.List;

/**
 * Represents a tile of the game board
 */
public class Tile {
    private MazePathType mazePathType;
    private MazePathOrientation mazePathOrientation;
    private TreasureType treasureType;
    private int playerId;

    public Tile(final List<Integer> tile, final int playerId) {
        this.mazePathType = MazePathType.fromId(tile.get(0));
        this.mazePathOrientation = MazePathOrientation.fromId(tile.get(1));
        this.treasureType = TreasureType.fromId(tile.get(2));
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

    public int getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
}