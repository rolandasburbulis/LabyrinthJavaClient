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

        switch (this.mazePathType) {
            case I:
                switch (this.mazePathOrientation) {
                    case ZERO:
                    case ONE_HUNDRED_EIGHTY:
                        switch (compassDirection) {
                            case NORTH:
                            case SOUTH:
                                hasExit = true;
                                break;
                        }

                        break;
                    case NINETY:
                    case TWO_HUNDRED_SEVENTY:
                        switch (compassDirection) {
                            case EAST:
                            case WEST:
                                hasExit = true;
                                break;
                        }

                        break;
                }

                break;
            case L:
                switch (this.mazePathOrientation) {
                    case ZERO:
                        switch (compassDirection) {
                            case WEST:
                            case NORTH:
                                hasExit = true;
                                break;
                        }

                        break;
                    case NINETY:
                        switch (compassDirection) {
                            case NORTH:
                            case EAST:
                                hasExit = true;
                                break;
                        }

                        break;
                    case ONE_HUNDRED_EIGHTY:
                        switch (compassDirection) {
                            case EAST:
                            case SOUTH:
                                hasExit = true;
                                break;
                        }

                        break;
                    case TWO_HUNDRED_SEVENTY:
                        switch (compassDirection) {
                            case SOUTH:
                            case WEST:
                                hasExit = true;
                                break;
                        }

                        break;
                }

                break;
            case T:
                switch(this.mazePathOrientation) {
                    case ZERO:
                        switch (compassDirection) {
                            case NORTH:
                            case SOUTH:
                            case WEST:
                                hasExit = true;
                                break;
                        }

                        break;
                    case NINETY:
                        switch (compassDirection) {
                            case NORTH:
                            case EAST:
                            case WEST:
                                hasExit = true;
                                break;
                        }

                        break;
                    case ONE_HUNDRED_EIGHTY:
                        switch (compassDirection) {
                            case NORTH:
                            case EAST:
                            case SOUTH:
                                hasExit = true;
                                break;
                        }

                        break;
                    case TWO_HUNDRED_SEVENTY:
                        switch (compassDirection) {
                            case EAST:
                            case SOUTH:
                            case WEST:
                                hasExit = true;
                                break;
                        }

                        break;
                }

                break;
        }

        return hasExit;
    }
}