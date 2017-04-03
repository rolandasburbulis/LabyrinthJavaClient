package Players.AIPlayer;

/**
 * Represents maze path type
 */
public enum MazePathType {
    L(0),
    T(1),
    I(2);

    private int id;

    MazePathType(final int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static MazePathType fromId(final int id) {
        for(MazePathType mazePathType : MazePathType.values()) {
            if(mazePathType.getId() == id) {
                return mazePathType;
            }
        }

        throw new IllegalArgumentException("Can not cast id " + id + " into a MazePathType");
    }
}