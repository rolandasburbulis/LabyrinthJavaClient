package Players.ai;

public enum TreasureType {
    NONE(-1),
    T1(1),
    T2(2),
    T3(3),
    T4(4),
    T5(5),
    T6(6),
    T7(7),
    T8(8),
    T9(9),
    T10(10),
    T11(11),
    T12(12),
    T13(13),
    T14(14),
    T15(15),
    T16(16),
    T17(17),
    T18(18),
    T19(19),
    T20(20),
    T21(21),
    T22(22),
    T23(23);

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