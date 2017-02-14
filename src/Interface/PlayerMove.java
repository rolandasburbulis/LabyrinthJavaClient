package Interface;

import java.util.Arrays;
import java.util.List;

/**
 * Class representing a single Labyrinth move
 * 
 * @author Adam Oest (amo9149@rit.edu)
 */
public class PlayerMove
{
	// Move state
	protected int playerId;
	protected List<Coordinate> path;
	protected Coordinate tileInsertion;
	protected int tileRotation;
	
	// Clockwise: 0 = 0 degrees ... 3 = 270 degrees
	public static int MIN_ROTATION = 0;
	public static int MAX_ROTATION = 3;
	
	public static int MAX_PLAYERS = 4;
	
	/**
	 * Constructor
	 * @param playerId
	 * @param move
	 * @param start
	 * @param end
	 */
	public PlayerMove(int playerId, List<Coordinate> path, 
		Coordinate tileInsertion, int tileRotation)
	{		
		this.playerId = playerId;
		this.path = path;
		this.tileInsertion = tileInsertion;
		this.tileRotation = tileRotation;
	}
	
	/**
	 * Throws an exception if invalid values are supplied
	 * 
	 * Use this!
	 * 
	 * @throws Exception
	 */
	public void checkValidity() throws Exception
	{
		if (tileRotation < PlayerMove.MIN_ROTATION 
				|| tileRotation > PlayerMove.MAX_ROTATION)
			throw new Exception ("PlayerMove invalid rotation " 
					+ Integer.toString(tileRotation));
		if (playerId < 1 || playerId > PlayerMove.MAX_PLAYERS)
			throw new Exception ("PlayerMove invalid playerId " 
					+ Integer.toString(playerId));
		if (path == null)
			throw new Exception ("PlayerMove path is null");
		if (tileInsertion == null)
			throw new Exception ("PlayerMove tileInsertion is null");
		
		this.tileInsertion.checkValidity();
		
		for (Coordinate c : this.path)
		{
			c.checkValidity();
		}
	}

	/**
	 * String representation of this move
	 */
	public String toString()
	{
		return "PlayerMove {\n" +
				"  playerId  = " + playerId + "\n" +
				"  path      = " + Arrays.toString(this.path.toArray()) + "\n" +
				"  insert    = " + this.tileInsertion + "\n" +
				"  rotation  = " + Integer.toString(this.tileRotation) + "\n}";
	}
	
	/**
	 * Gets the player who made this move
	 * @return
	 */
	public int getPlayerId()
	{
		return playerId;
	}

	/**
	 * Gets the tile rotation
	 * @return
	 */
	public int getTileRotation()
	{
		return tileRotation;
	}

	/**
	 * Gets the tile insertion coordinate
	 * @return
	 */
	public Coordinate getTileInsertion()
	{
		return tileInsertion;
	}

	/**
	 * Gets the movement path
	 * @return
	 */
	public List<Coordinate> getPath()
	{
		return path;
	}
}
