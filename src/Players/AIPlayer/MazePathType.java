package Players.AIPlayer;

/**
 * Represents maze path type
 */
enum MazePathType {
    L,
    T,
    I;

    /**
     * Gets the value of MazePathType matching the specified id.  Throws
     * IllegalArgumentException if none of the values of MazePathType match
     * the specified id.
     *
     * @param id - identifies the value of MazePathType which is to be returned
     *
     * @return value of MazePathType matching the specified id
     *
     * @throws IllegalArgumentException if none of the values of MazePathType
     * match the specified id
     */
    public static MazePathType fromId(final int id) {
        for(MazePathType mazePathType : MazePathType.values()) {
            if(mazePathType.ordinal() == id) {
                return mazePathType;
            }
        }

        throw new IllegalArgumentException("Can not cast id " + id + " into a MazePathType");
    }
}