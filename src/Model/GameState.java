package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Interface.Coordinate;
import Interface.PlayerMove;

/**
 * This class parses JSON in order to replicate the Quoridor
 *  game state locally
 * @author Adam
 *
 */
public class GameState
{
	// State fields
	private int numMoves;
	private int winner;
	private boolean gameOver;
	private boolean valid;
	private Map<Integer, Boolean> validPlayers;
	private String errorMessage;
	private int currentPlayer;
	private PlayerMove lastMove;

	/**
	 * Generates state from a json map
	 * @param json
	 */
	public GameState(Object json)
	{
		Map<Object, Object> jsonMap = (Map<Object, Object>) json;
		
		// Moves
		this.numMoves = (Integer) jsonMap.get("nummoves");
		
		// Winner
		if (jsonMap.get("winner").getClass() == Boolean.class)
		{
			this.winner = 0;
		}
		else
		{
			this.winner = (Integer) jsonMap.get("winner");
		}
		
		// Game over
		this.gameOver = (Boolean) jsonMap.get("gameover");
		
		// Validity
		this.valid = (Boolean) jsonMap.get("valid");
		
		// Valid Players
		Map<String, Boolean> valids = (Map<String, Boolean>)
		 jsonMap.get("validplayers");
		
		this.validPlayers = new HashMap<Integer, Boolean>();
		for (String s : valids.keySet())
		{
			this.validPlayers.put(Integer.parseInt(s), valids.get(s));
		}
		
		// Error message
		if (jsonMap.get("error").getClass() == Boolean.class)
		{
			this.errorMessage = null;
		}
		else
		{
			this.errorMessage = (String) jsonMap.get("error");
		}
		
		// Current player
		this.currentPlayer = (Integer) jsonMap.get("currentplayer");
						
		if (jsonMap.get("lastmove").getClass() == Boolean.class)
		{
			this.lastMove = null;
		}
		else
		{
			Map<String, Object> moveMapping = 
				(Map<String, Object>) jsonMap.get("lastmove");
			
			// Convert path to list of coordinates
			List<Coordinate> pathList = new ArrayList<Coordinate>();
			List<List<Integer> > path = 
				(List<List<Integer> >) moveMapping.get("path");
			
			for (List<Integer> seg : path)
			{
				pathList.add(new Coordinate(seg.get(0), seg.get(1)));
			}
			
			this.lastMove = new PlayerMove(
				(Integer) moveMapping.get("playerNo"),
				pathList,
				new Coordinate(
						(Integer) moveMapping.get("r"),
						(Integer) moveMapping.get("c")
				),
				(Integer) moveMapping.get("rotation")
			);
		}
	}
	
	// Getters and setters
	
	public PlayerMove getLastMove()
	{
		return this.lastMove;
	}
	
	public Boolean hasLastMove()
	{
		return this.lastMove != null;
	}

	public int getNumMoves()
	{
		return numMoves;
	}

	public int getWinner()
	{
		return winner;
	}

	public boolean isGameOver()
	{
		return gameOver;
	}

	public boolean isValid()
	{
		return valid;
	}

	public boolean hasError()
	{
		return this.errorMessage != null;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public int getCurrentPlayer()
	{
		return currentPlayer;
	}
		
	public Map<Integer, Boolean> getPlayerValidity()
	{
		return validPlayers;
	}

	@Override
	public String toString()
	{
		return "GameState [currentPlayer=" + currentPlayer + ", errorMessage="
				+ errorMessage + ", gameOver=" + gameOver + ", lastMove="
				+ lastMove + ", numMoves="
				+ numMoves
				+ ", valid=" + valid + ", validPlayers=" + validPlayers
				+ ", winner=" + winner
				+ "]";
	}
}
