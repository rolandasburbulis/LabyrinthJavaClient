package Players.ai;

import Engine.Logger;
import Interface.Coordinate;
import Interface.PlayerModule;
import Interface.PlayerMove;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * AI Player
 */
public class AIPlayer implements PlayerModule
{
	private Logger l;
	private int playerId;
	
	/**
	 * Initializes your player module.  In this method, be sure to
	 * set up your data structures and pre-populate them with the starting
	 * board configuration.  All state should be stored in your player class.
	 * 
	 * @param logger, reference to the logger class
	 * @param playerId, the id of this player
	 * @param playerHomes, starting locations for each player, in order
	 * @param treasures, ordered list of treasures for each player
	 * @param board 2-d list of [Tile ID, Rotation, Treasure]
	 *        Tile IDs:  0 = L tile, 1 = T tile, 2 = I tile
	 *        Treasures: -1 = no treasure, 0-23 = corresponding treasure
	 *        Rotations: 0 = 0 degrees, 1 = 90 degrees, 2 = 180 degrees,
	 *        			 3 = 270 degrees, all clockwise
	 * @param extra contains [Extra Tile ID, Treasure]
	 */
	public void init(Logger logger, int playerId, List<Coordinate> playerHomes,
			List<List<Integer>> treasures, List<List<List<Integer>>> board,
			List<Integer> extra) 
	{
		this.l = logger;
		this.playerId = playerId;
		
		// TODO initialize board and set other state
		
		log("Loaded");
	}

	/**
	 * Called when it's your player's turn to make a move
	 * This function needs to return the move that you want to make
	 * If you return an invalid move, your player will be invalidated
	 * @return a PlayerMove object
	 */
	public PlayerMove move()
	{		
		log("Move was requested...");
		
		return new PlayerMove(this.playerId, new ArrayList<Coordinate>(), new Coordinate(3,3), 3);
	}

	/**
	 * Notifies you that a move was just made.  Use this function
	 * to update your board state accordingly.  You may assume that 
	 * all moves are given to you in the order that they are made.
	 * You may also assume that all the passed moves are valid.
	 * @param m the move
	 */
	public void lastMove(PlayerMove m)
	{
		log("Last move: " + m.toString());
	}

	/**
	 * Notifies you that an opponent player made a bad move 
	 * and has been invalidated.
	 * 
	 * @param playerId, the id of the invalid player
	 */
	public void playerInvalidated(int playerId)
	{
		log("Player " + Integer.toString(playerId) + " was invalidated :(");
		
	}
		
	/**
	 * Returns all the cells adjacent to the given coordinate
	 * 
	 * The system calls this function only to verify that your implementation
	 * is correct.  You may also use it to test your code.
	 * 
	 * @param c the coordinate to check
	 * @return a set of reachable adjacent coordinates
	 */
	public Set<Coordinate> getNeighbors(Coordinate c)
	{
		return null;
	}
	
	/**
	 * Returns any valid path between two coordinates
	 * 
	 * The system calls this function only to verify that your implementation
	 * is correct.  You may also use it to test your code.
	 * 
	 * @param start the start coordinate
	 * @param end the end coordinate
	 * @return an ordered list of Coordinate objects representing a path
	 */
	public List<Coordinate> getPath(Coordinate start, Coordinate end)
	{
		return null;
	}
	
	/**
	 * Sample log function.  Use this in your debugging!
	 * @param msg
	 */
	private void log(String msg)
	{
		String message = 
			this.l.msg("HUMAN (P" + Integer.toString(this.playerId) +")", msg);
		
		this.l.writeln(message);
		System.out.println(message);
	}
}