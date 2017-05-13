package Players.AIPlayer;

/**
 * Represents a maze path orientation
 */
enum MazePathOrientation {
    ZERO,
    NINETY,
    ONE_HUNDRED_EIGHTY,
    TWO_HUNDRED_SEVENTY;

    /**
     * Gets the value of MazePathOrientation matching the specified id.  Throws
     * IllegalArgumentException if none of the values of MazePathOrientation match
     * the specified id.
     *
     * @param id - identifies the value of MazePathOrientation which is to be returned
     *
     * @return value of MazePathOrientation matching the specified id
     * 
     * @throws IllegalArgumentException if none of the values of MazePathOrientation
     * match the specified id
     */
    public static MazePathOrientation fromId(final int id) {
        for(MazePathOrientation mazePathOrientation : MazePathOrientation.values()) {
            if(mazePathOrientation.ordinal() == id) {
                return mazePathOrientation;
            }
        }

        throw new IllegalArgumentException("Can not cast id " + id + " into a MazePathOrientation");
    }
}