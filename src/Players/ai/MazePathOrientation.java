package Players.ai;

public enum MazePathOrientation {
    ZERO(0),
    NINETY(1),
    ONE_HUNDRED_EIGHTY(2),
    TWO_HUNDRED_SEVENTY(3);

    private int id;

    MazePathOrientation(final int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static MazePathOrientation fromId(final int id) {
        for(MazePathOrientation mazePathOrientation : MazePathOrientation.values()) {
            if(mazePathOrientation.getId() == id) {
                return mazePathOrientation;
            }
        }

        throw new IllegalArgumentException("Can not cast id " + id + " into a MazePathOrientation");
    }
}