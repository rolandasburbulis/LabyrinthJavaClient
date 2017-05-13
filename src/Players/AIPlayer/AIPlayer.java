package Players.AIPlayer;

import Engine.Logger;
import Interface.Coordinate;
import Interface.PlayerModule;
import Interface.PlayerMove;

import java.util.List;
import java.util.Set;

/**
 * AIPlayer Player
 */
public class AIPlayer implements PlayerModule
{
	private Logger l;
	private int playerId;
	private GameController gameController;
	
	/**
	 * Initializes the AIPlayer with the specified parameters
	 * 
	 * @param logger - reference to the logger class
	 * @param playerId- the id of this player
	 * @param playerHomes - starting locations for each player, in order
	 * @param treasures - ordered list of treasures for each player
	 * @param board - 2-d list of [Tile ID, Rotation, Treasure]
	 * Tile IDs:  0 = L tile, 1 = T tile, 2 = I tile
	 * Rotations: 0 = 0 degrees, 1 = 90 degrees, 2 = 180 degrees,
	 *            3 = 270 degrees, all clockwise
	 * Treasures: -1 = no treasure, 0-23 = corresponding treasure
	 * @param extra - contains [Extra Tile ID, Treasure]
	 */
	public void init(final Logger logger,
					 final int playerId,
					 final List<Coordinate> playerHomes,
					 final List<List<Integer>> treasures,
					 final List<List<List<Integer>>> board,
					 final List<Integer> extra) {
		this.l = logger;
		this.playerId = playerId;

		//SHOULDN'T NEED TO DO playerHomes.subList(...) but engine is return 4 players always at the moment
		this.gameController = new GameController(playerId, playerHomes.subList(0, treasures.size()),
                treasures, board, extra);
		
		log("Loaded");
	}

	/**
	 * Called when it's the AI player's turn to make a move.  The AI player generates
     * and returns its next move.
     *
	 * @return the move that the AI player wants to make
	 */
	public PlayerMove move() {
		log("Move was requested...");

		return this.gameController.findBestMove();
	}

	/**
	 * Notifies the AI player that a specified move was just made.  The AI player updates
     * the state of the game with this move.  It is assumed that all moves are given in
     * the order that they are made.  It is also assumed that all passed moves are valid.
     *
	 * @param m - the move that was just made
	 */
	public void lastMove(final PlayerMove m) {
		log("Last move: " + m.toString());

		this.gameController.handlePlayerMove(m);
	}

	/**
	 * Notifies the AI player that the specified opponent player made a bad move and has
	 * been invalidated
	 * 
	 * @param playerId - the id of the invalid player
	 */
	public void playerInvalidated(final int playerId) {
		log("Player " + Integer.toString(playerId) + " was invalidated :(");
		
	}
		
	/**
	 * Returns all the reachable cells which are adjacent to the specified coordinate.
     * This is not implemented as it is not called by the engine.
	 * 
	 * @param c - the coordinate whose reachable adjacent cells are to be returned
     *
	 * @return a set of reachable coordinates which are adjacent to the specified
     * coordinate
	 */
	public Set<Coordinate> getNeighbors(final Coordinate c) {
		return null;
	}
	
	/**
	 * Returns any valid path between the two specified coordinates.  This is not
     * implemented as it is not called by the engine.
	 * 
	 * @param start - the starting coordinate from which any valid path to the
     * specified end coordinate should be returned
	 * @param end - the end coordinate to which any valid path from the specified
     * start coordinate should be returned
     *
	 * @return an ordered list of Coordinate objects representing a path from the
     * specified start coordinate to the specified end coordinate
	 */
	public List<Coordinate> getPath(final Coordinate start, final Coordinate end) {
		return null;
	}

	private void log(final String msg) {
		String message = 
			this.l.msg("AI (P" + Integer.toString(this.playerId) +")", msg);
		
		this.l.writeln(message);
		System.out.println(message);
	}
}