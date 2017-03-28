package Players.ai;

import Interface.Coordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent AI player game board
 */
public class Board {
    private List<List<Tile>> board;

    public Board(final List<List<List<Integer>>> board) {
        this.board = new ArrayList<>();

        for(int rowIndex = 0; rowIndex < Coordinate.BOARD_DIM; rowIndex++) {
            List<Tile> row = new ArrayList<Tile>();

            for(int colIndex = 0; colIndex < Coordinate.BOARD_DIM; colIndex++) {
                final List<Integer> tileInfoList = board.get(rowIndex).get(colIndex);

                final int mazePathTypeId = tileInfoList.get(0);
                final int mazePathOrientationId = tileInfoList.get(1);
                final int treasureTypeId = tileInfoList.get(2);

                Tile tile = new Tile(MazePathType.fromId(mazePathTypeId),
                                     MazePathOrientation.fromId(mazePathOrientationId),
                                     TreasureType.fromId(treasureTypeId),
                                     (byte)-1);

                row.add(tile);
            }

            this.board.add(rowIndex, row);
        }
    }

    /**
     * Insert the specified tile at the location identified by the specified coordinate
     * and return the tile that was pushed out as as result the inserting the specified tile.
     * Illegal argument exception is thrown if the location where the tile should be inserted
     * at is not a valid insertion location.
     *
     * @param tileToInsert - tile to be inserted
     * @param insertionLocation - location where the tile to be inserted will be inserted at
     *
     * @return Tile that was pused out as a result of the inserted tile
     * @throws IllegalArgumentException if the location where the tile should be inserted at
     * is not a valid insertion location
     */
    public Tile insertTile(final Tile tileToInsert, final Coordinate insertionLocation) {
        return null;
    }
}
