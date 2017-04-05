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
        boolean hasExit = false;

        if(this.mazePathOrientation == null) {
            throw new IllegalStateException("This tile does not have an orientation set, therefore checking if it has an exit in a particular compass direction is not valid.");
        }

        if(this.mazePathType.equals(MazePathType.I)) {
            if(this.mazePathOrientation.equals(MazePathOrientation.ZERO) || this.mazePathOrientation.equals(MazePathOrientation.ONE_HUNDRED_EIGHTY)) {
                if(compassDirection.equals(CompassDirection.NORTH)) {
                    hasExit = true;
                } else if(compassDirection.equals(CompassDirection.EAST)) {
                    hasExit = false;
                } else if(compassDirection.equals(CompassDirection.SOUTH)) {
                    hasExit = true;
                } else {
                    hasExit = false;
                }
            } else {
                if (compassDirection.equals(CompassDirection.NORTH)) {
                    hasExit = false;
                } else if (compassDirection.equals(CompassDirection.EAST)) {
                    hasExit = true;
                } else if (compassDirection.equals(CompassDirection.SOUTH)) {
                    hasExit = false;
                } else {
                    hasExit = true;
                }
            }
        } else if(this.mazePathType.equals(MazePathType.L)) {
            if(this.mazePathOrientation.equals(MazePathOrientation.ZERO)) {
                if(compassDirection.equals(CompassDirection.NORTH)) {
                    hasExit = true;
                } else if(compassDirection.equals(CompassDirection.EAST)) {
                    hasExit = false;
                } else if(compassDirection.equals(CompassDirection.SOUTH)) {
                    hasExit = false;
                } else {
                    hasExit = true;
                }
            } else if(this.mazePathOrientation.equals(MazePathOrientation.NINETY)) {
                if(compassDirection.equals(CompassDirection.NORTH)) {
                    hasExit = true;
                } else if(compassDirection.equals(CompassDirection.EAST)) {
                    hasExit = true;
                } else if(compassDirection.equals(CompassDirection.SOUTH)) {
                    hasExit = false;
                } else {
                    hasExit = false;
                }
            } else if(this.mazePathOrientation.equals(MazePathOrientation.ONE_HUNDRED_EIGHTY)) {
                if(compassDirection.equals(CompassDirection.NORTH)) {
                    hasExit = false;
                } else if(compassDirection.equals(CompassDirection.EAST)) {
                    hasExit = true;
                } else if(compassDirection.equals(CompassDirection.SOUTH)) {
                    hasExit = true;
                } else {
                    hasExit = false;
                }
            } else {
                if(compassDirection.equals(CompassDirection.NORTH)) {
                    hasExit = false;
                } else if(compassDirection.equals(CompassDirection.EAST)) {
                    hasExit = false;
                } else if(compassDirection.equals(CompassDirection.SOUTH)) {
                    hasExit = true;
                } else {
                    hasExit = true;
                }
            }
        } else {
            if(this.mazePathOrientation.equals(MazePathOrientation.ZERO)) {
                if(compassDirection.equals(CompassDirection.NORTH)) {
                    hasExit = true;
                } else if(compassDirection.equals(CompassDirection.EAST)) {
                    hasExit = false;
                } else if(compassDirection.equals(CompassDirection.SOUTH)) {
                    hasExit = true;
                } else {
                    hasExit = true;
                }
            } else if(this.mazePathOrientation.equals(MazePathOrientation.NINETY)) {
                if(compassDirection.equals(CompassDirection.NORTH)) {
                    hasExit = true;
                } else if(compassDirection.equals(CompassDirection.EAST)) {
                    hasExit = true;
                } else if(compassDirection.equals(CompassDirection.SOUTH)) {
                    hasExit = false;
                } else {
                    hasExit = true;
                }
            } else if(this.mazePathOrientation.equals(MazePathOrientation.ONE_HUNDRED_EIGHTY)) {
                if(compassDirection.equals(CompassDirection.NORTH)) {
                    hasExit = true;
                } else if(compassDirection.equals(CompassDirection.EAST)) {
                    hasExit = true;
                } else if(compassDirection.equals(CompassDirection.SOUTH)) {
                    hasExit = true;
                } else {
                    hasExit = false;
                }
            } else {
                if(compassDirection.equals(CompassDirection.NORTH)) {
                    hasExit = false;
                } else if(compassDirection.equals(CompassDirection.EAST)) {
                    hasExit = true;
                } else if(compassDirection.equals(CompassDirection.SOUTH)) {
                    hasExit = true;
                } else {
                    hasExit = true;
                }
            }
        }

        return hasExit;
    }
}