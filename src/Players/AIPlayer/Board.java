package Players.AIPlayer;

import Interface.Coordinate;

import java.io.*;
import java.util.*;

/**
 * Represents the game board
 */
public class Board implements Serializable {
    private static final long serialVersionUID = 3060504898708106389L;

    private Tile[][] board;
    private Set<Coordinate> validTileInsertionLocations;
    private Coordinate invalidInsertionLocation;
    private Map<Integer, Coordinate> playerLocations;
    private Map<Integer, Queue<TreasureType>> playerTreasures;
    private Map<TreasureType, Coordinate> treasureLocations;

    Board(final List<Coordinate> playerHomes,
          final List<List<Integer>> treasures,
          final List<List<List<Integer>>> board) {
        initValidTileInsertionLocations();
        initBoard(playerHomes, treasures, board);
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
    Tile insertTile(final Tile tileToInsert, final Coordinate tileInsertionLocation) {
        final Tile shiftedOutTile;

        if(!this.validTileInsertionLocations.contains(tileInsertionLocation))
        {
            throw new IllegalArgumentException("Specified tile insertion location is not a valid tile insertion location.");
        }

        if(this.invalidInsertionLocation != null) {
            this.validTileInsertionLocations.add(this.invalidInsertionLocation);
        }

        final int rowToInsertTileAt = tileInsertionLocation.getRow();
        final int columnToInsertTileAt = tileInsertionLocation.getCol();

        Tile tileToShiftIn = tileToInsert;

        //Insert on the north side of the board
        if(rowToInsertTileAt == 0) {
            Tile tileToShiftDown = null;

            for(int rowIndex = 0; rowIndex < Coordinate.BOARD_DIM; rowIndex++) {
                tileToShiftDown = this.board[rowIndex][columnToInsertTileAt];
                this.board[rowIndex][columnToInsertTileAt] = tileToShiftIn;
                tileToShiftIn = tileToShiftDown;
            }

            shiftedOutTile = tileToShiftDown;
            this.invalidInsertionLocation = new Coordinate(Coordinate.BOARD_DIM - 1, columnToInsertTileAt);
        //Insert on the south side of the board
        } else if(rowToInsertTileAt == (Coordinate.BOARD_DIM - 1)) {
            Tile tileToShiftUp = null;

            for(int rowIndex = Coordinate.BOARD_DIM - 1; rowIndex >= 0; rowIndex--) {
                tileToShiftUp = this.board[rowIndex][columnToInsertTileAt];
                this.board[rowIndex][columnToInsertTileAt] = tileToShiftIn;
                tileToShiftIn = tileToShiftUp;
            }

            shiftedOutTile = tileToShiftUp;
            this.invalidInsertionLocation = new Coordinate(0, columnToInsertTileAt);
        //Insert on the west side of the board
        } else if(columnToInsertTileAt == 0) {
            Tile tileToShiftRight = null;

            for(int columnIndex = 0; columnIndex < Coordinate.BOARD_DIM; columnIndex++) {
                tileToShiftRight = this.board[rowToInsertTileAt][columnIndex];
                this.board[rowToInsertTileAt][columnIndex] = tileToShiftIn;
                tileToShiftIn = tileToShiftRight;
            }

            shiftedOutTile = tileToShiftRight;
            this.invalidInsertionLocation = new Coordinate(rowToInsertTileAt, Coordinate.BOARD_DIM - 1);
        //Insert on the east side of the board
        } else {
            Tile tileToShiftLeft = null;

            for(int columnIndex = Coordinate.BOARD_DIM - 1; columnIndex >= 0; columnIndex--) {
                tileToShiftLeft = this.board[rowToInsertTileAt][columnIndex];
                this.board[rowToInsertTileAt][columnIndex] = tileToShiftIn;
                tileToShiftIn = tileToShiftLeft;
            }

            shiftedOutTile = tileToShiftLeft;
            this.invalidInsertionLocation = new Coordinate(rowToInsertTileAt, 0);
        }

        this.validTileInsertionLocations.remove(this.invalidInsertionLocation);

        if(shiftedOutTile.hasPlayer()) {
            this.board[rowToInsertTileAt][columnToInsertTileAt].addPlayers(shiftedOutTile.getPlayers());
            shiftedOutTile.removeAllPlayers();
        }

        updatePlayerAndTreasureLocations();

        return shiftedOutTile;
    }

    Set<Coordinate> getValidTileInsertionLocations() {
        return this.validTileInsertionLocations;
    }

    void movePlayer(final int player, final Coordinate destinationLocation) {
        final Coordinate currentPlayerLocation = this.playerLocations.get(player);

        this.board[currentPlayerLocation.getRow()][currentPlayerLocation.getCol()].removePlayer(player);

        final Tile destinationTile = this.board[destinationLocation.getRow()][destinationLocation.getCol()];

        destinationTile.addPlayer(player);

        if(destinationTile.getTreasureType().equals(this.playerTreasures.get(player).peek())) {
            this.playerTreasures.get(player).poll();
        }

        this.playerLocations.put(player, destinationLocation);
    }

    Coordinate getPlayerLocation(final int player) {
        return this.playerLocations.get(player);
    }

    Coordinate getPlayerNextTreasureLocation(final int player) {
        return this.treasureLocations.get(this.playerTreasures.get(player).peek());
    }

    Tile getTile(final int rowIndex, final int columnIndex) {
        return this.board[rowIndex][columnIndex];
    }

    public void print() {
        for(int rowIndex = 0; rowIndex < this.board.length; rowIndex++) {
            for(int columnIndex = 0; columnIndex < this.board[rowIndex].length; columnIndex++) {
                final Tile tile = this.board[rowIndex][columnIndex];
                System.out.print("(" + rowIndex + ", " + columnIndex + "):  ");
                System.out.print(tile.getMazePathType().name() + " ");
                System.out.print(tile.getMazePathOrientation().name() + " ");
                System.out.print(tile.getTreasureType().name() + " ");

                for(int player : tile.getPlayers()) {
                    System.out.print(player + " ");
                }

                System.out.println();
            }
        }
    }

    Board createCopy() {
        Board copy = null;

        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            out.close();

            final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            copy = (Board) in.readObject();
        }
        catch(final IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return copy;
    }

    private void initValidTileInsertionLocations() {
        this.validTileInsertionLocations = new HashSet<>();

        for(int index = 1; index < Coordinate.BOARD_DIM; index+=2) {
            //North side
            this.validTileInsertionLocations.add(new Coordinate(0, index));
            //South side
            this.validTileInsertionLocations.add(new Coordinate(6, index));
            //East side
            this.validTileInsertionLocations.add(new Coordinate(index, 6));
            //West side
            this.validTileInsertionLocations.add(new Coordinate(index, 0));
        }
    }

    private void initBoard(final List<Coordinate> playerHomes,
                           final List<List<Integer>> treasures,
                           final List<List<List<Integer>>> board) {
        final Map<Coordinate, Integer> playerHomeToIdMap = new HashMap<>();
        this.playerLocations = new HashMap<>();

        for(int player = 1; player <= playerHomes.size(); player++) {
            final Coordinate playerHome = playerHomes.get(player - 1);

            playerHomeToIdMap.put(playerHome, player);
            this.playerLocations.put(player, playerHome);
        }

        initPlayerTreasures(treasures);

        this.board = new Tile[Coordinate.BOARD_DIM][Coordinate.BOARD_DIM];
        this.treasureLocations = new HashMap<>();

        for(int rowIndex = 0; rowIndex < Coordinate.BOARD_DIM; rowIndex++) {
            for(int columnIndex = 0; columnIndex < Coordinate.BOARD_DIM; columnIndex++) {
                final List<Integer> tileInfoList = board.get(rowIndex).get(columnIndex);

                final Coordinate currentTileCoordinate = new Coordinate(rowIndex, columnIndex);

                final TreasureType treasureType = TreasureType.fromId(tileInfoList.get(2));

                if(!treasureType.equals(TreasureType.NONE)) {
                    this.treasureLocations.put(treasureType, currentTileCoordinate);
                }

                if(playerHomeToIdMap.containsKey(currentTileCoordinate)) {
                    this.board[rowIndex][columnIndex] = new Tile(MazePathType.fromId(tileInfoList.get(0)),
                                                                 MazePathOrientation.fromId(tileInfoList.get(1)),
                                                                 TreasureType.fromId(tileInfoList.get(2)),
                                                                 playerHomeToIdMap.get(currentTileCoordinate));
                } else {
                    this.board[rowIndex][columnIndex] = new Tile(MazePathType.fromId(tileInfoList.get(0)),
                                                                 MazePathOrientation.fromId(tileInfoList.get(1)),
                                                                 TreasureType.fromId(tileInfoList.get(2)));
                }
            }
        }
    }

    private void initPlayerTreasures(List<List<Integer>> playerTreasures) {
        this.playerTreasures = new HashMap<>();

        for(int player = 1; player <= playerTreasures.size(); player++) {
            final Queue<TreasureType> playerTreasuresQueue = new LinkedList<>();

            for(Integer treasureId : playerTreasures.get(player - 1)) {
                final TreasureType treasureType = TreasureType.fromId(treasureId);
                playerTreasuresQueue.add(treasureType);
            }

            this.playerTreasures.put(player, playerTreasuresQueue);
        }
    }

    private void updatePlayerAndTreasureLocations() {
        this.playerLocations.clear();
        this.treasureLocations.clear();

        for(int rowIndex = 0; rowIndex < Coordinate.BOARD_DIM; rowIndex++) {
            for(int columnIndex = 0; columnIndex < Coordinate.BOARD_DIM; columnIndex++) {
                final Tile tile = this.board[rowIndex][columnIndex];
                final Coordinate coordinate = new Coordinate(rowIndex, columnIndex);

                for(int player : tile.getPlayers()) {
                    this.playerLocations.put(player, coordinate);
                }

                final TreasureType treasureType = tile.getTreasureType();

                if(!treasureType.equals(TreasureType.NONE)) {
                    this.treasureLocations.put(treasureType, coordinate);
                }
            }
        }
    }
}