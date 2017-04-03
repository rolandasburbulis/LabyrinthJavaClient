package Players.AIPlayer;

/**
 * Represents a treasure type
 */
public enum TreasureType {
    NONE(-1),
    T1(0),
    T2(1),
    T3(2),
    T4(3),
    T5(4),
    T6(5),
    T7(6),
    T8(7),
    T9(8),
    T10(9),
    T11(10),
    T12(11),
    T13(12),
    T14(13),
    T15(14),
    T16(15),
    T17(16),
    T18(17),
    T19(18),
    T20(19),
    T21(20),
    T22(21),
    T23(22),
    T24(23);

    private int id;

    TreasureType(final int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static TreasureType fromId(final int id) {
        for(TreasureType treasureType : TreasureType.values()) {
            if(treasureType.getId() == id) {
                return treasureType;
            }
        }

        throw new IllegalArgumentException("Can not cast id " + id + " into a TreasureType");
    }
}