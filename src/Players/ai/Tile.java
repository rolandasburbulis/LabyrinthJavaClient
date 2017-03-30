package Players.ai;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a tile of the game board
 */
public class Tile {
    private MazePathType mazePathType;
    private MazePathOrientation mazePathOrientation;
    private TreasureType treasureType;
    private Set<Integer> players;

    public Tile(final List<Integer> tile) {
        this.mazePathType = MazePathType.fromId(tile.get(0));
        this.mazePathOrientation = MazePathOrientation.fromId(tile.get(1));
        this.treasureType = TreasureType.fromId(tile.get(2));
        this.players = new HashSet<>();
    }

    public Tile(final List<Integer> tile, int player) {
        this(tile);
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
}