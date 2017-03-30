package Players.ai;

import Interface.Coordinate;

import java.util.*;

/**
 * Represents the game board
 */
public class Board {
    private Tile[][] board;
    private Set<Coordinate> validTileInsertionLocations;
    private Coordinate notValidForNextMove;

    public Board(final List<Coordinate> playerHomes, final List<List<List<Integer>>> board) {
        setupValidTileInsertionLocations();
        setupBoard(playerHomes, board);
    }

    /**
     * Insert the specified tile at the location identified by the specified coordinate
     * and return the tile that was pushed out as as result the inserting the specified tile.
     * Illegal argument exception is thrown if the location where the tile should be inserted
     * at is not a valid insertion location.
     *
     * @param tileToInsert - tile to be inserted
     * @param tileInsertionLocation - location where the tile to be inserted will be inserted at
     *
     * @return Tile that was pused out as a result of the inserted tile
     * @throws IllegalArgumentException if the location where the tile should be inserted at
     * is not a valid insertion location
     */
    public Tile insertTile(final Tile tileToInsert, final Coordinate tileInsertionLocation) {
        Tile shiftedOutTile;

        if(!this.validTileInsertionLocations.contains(tileInsertionLocation))
        {
            throw new IllegalArgumentException("Specified tile insertion location is not a valid tile insertion location.");
        }

        final int rowToInsertTileAt = tileInsertionLocation.getRow();
        final int columnToInsertTileAt = tileInsertionLocation.getCol();
        Coordinate newNotValidForNextMove;

        //Insert on the north side of the board
        if(rowToInsertTileAt == 1) {
            Tile tileToShiftDown = null;
            Tile tileToShiftIn = tileToInsert;

            for(int rowIndex = 1; rowIndex <= Coordinate.BOARD_DIM; rowIndex++) {
                tileToShiftDown = this.board[rowIndex - 1][columnToInsertTileAt - 1];
                this.board[rowIndex - 1][columnToInsertTileAt - 1] = tileToShiftIn;
                tileToShiftIn = tileToShiftDown;
            }

            shiftedOutTile = tileToShiftDown;
            newNotValidForNextMove = new Coordinate(Coordinate.BOARD_DIM, columnToInsertTileAt);
        //Insert on the south side of the board
        } else if(rowToInsertTileAt == Coordinate.BOARD_DIM) {
            Tile tileToShiftUp = null;
            Tile tileToShiftIn = tileToInsert;

            for(int rowIndex = Coordinate.BOARD_DIM; rowIndex > 0; rowIndex--) {
                tileToShiftUp = this.board[rowIndex - 1][columnToInsertTileAt - 1];
                this.board[rowIndex - 1][columnToInsertTileAt - 1] = tileToShiftIn;
                tileToShiftIn = tileToShiftUp;
            }

            shiftedOutTile = tileToShiftUp;
            newNotValidForNextMove = new Coordinate(1, columnToInsertTileAt);
        //Insert on the west side of the board
        } else if(columnToInsertTileAt == 1) {
            Tile tileToShiftRight = null;
            Tile tileToShiftIn = tileToInsert;

            for(int columnIndex = 1; columnIndex <= Coordinate.BOARD_DIM; columnIndex++) {
                tileToShiftRight = this.board[rowToInsertTileAt - 1][columnIndex - 1];
                this.board[rowToInsertTileAt - 1][columnIndex - 1] = tileToShiftIn;
                tileToShiftIn = tileToShiftRight;
            }

            shiftedOutTile = tileToShiftRight;
            newNotValidForNextMove = new Coordinate(rowToInsertTileAt, Coordinate.BOARD_DIM);
        //Insert on the east side of the board
        } else {
            Tile tileToShiftLeft = null;
            Tile tileToShiftIn = tileToInsert;

            for(int columnIndex = Coordinate.BOARD_DIM; columnIndex > 0; columnIndex--) {
                tileToShiftLeft = this.board[rowToInsertTileAt - 1][columnIndex - 1];
                this.board[rowToInsertTileAt - 1][columnIndex - 1] = tileToShiftIn;
                tileToShiftIn = tileToShiftLeft;
            }

            shiftedOutTile = tileToShiftLeft;
            newNotValidForNextMove = new Coordinate(rowToInsertTileAt, 1);
        }

        if(this.notValidForNextMove != null) {
            this.validTileInsertionLocations.add(this.notValidForNextMove);
        }

        this.validTileInsertionLocations.remove(newNotValidForNextMove);
        this.notValidForNextMove = newNotValidForNextMove;

        if(shiftedOutTile.hasPlayer()) {
            this.board[rowToInsertTileAt][columnToInsertTileAt].setPlayers(shiftedOutTile.getPlayers());
            shiftedOutTile.clearPlayers();
        }

        return shiftedOutTile;
    }

    private void setupValidTileInsertionLocations() {
        this.validTileInsertionLocations = new HashSet<Coordinate>();

        //Valid insertion locations on the north and south sides of the board
        for(int colIndex = 2; colIndex < Coordinate.BOARD_DIM; colIndex+=2) {
            //North side
            this.validTileInsertionLocations.add(new Coordinate(1, colIndex));
            //South side
            this.validTileInsertionLocations.add(new Coordinate(7, colIndex));
        }

        //Valid insertion locations on the east and west sides of the board
        for(int rowIndex = 2; rowIndex < Coordinate.BOARD_DIM; rowIndex+=2) {
            //East side
            this.validTileInsertionLocations.add(new Coordinate(rowIndex, 7));
            //West side
            this.validTileInsertionLocations.add(new Coordinate(rowIndex, 1));
        }
    }

    private void setupBoard(final List<Coordinate> playerHomes, final List<List<List<Integer>>> board) {
        Map<Coordinate, Integer> playerHomeToIdMap = new HashMap<>();

        for(int player = 1; player <= playerHomes.size(); player++) {
            playerHomeToIdMap.put(playerHomes.get(player - 1), player);
        }

        this.board = new Tile[Coordinate.BOARD_DIM][Coordinate.BOARD_DIM];

        for(int rowIndex = 1; rowIndex <= Coordinate.BOARD_DIM; rowIndex++) {
            for(int columnIndex = 1; columnIndex <= Coordinate.BOARD_DIM; columnIndex++) {
                final List<Integer> tileInfoList = board.get(rowIndex - 1).get(columnIndex - 1);

                final Coordinate currentTileCoordinate = new Coordinate(rowIndex, columnIndex);

                if(playerHomeToIdMap.containsKey(currentTileCoordinate)) {
                    this.board[rowIndex][columnIndex] = new Tile(tileInfoList, playerHomeToIdMap.get(currentTileCoordinate));
                } else {
                    this.board[rowIndex][columnIndex] = new Tile(tileInfoList);
                }
            }
        }
    }
}