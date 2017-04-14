package Engine;

import Interface.PlayerModule;
import Interface.PlayerMove;
import Model.ModelProxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The main game controller determines the flow of execution
 * for this game
 * 
 * @author Adam Oest (amo9149@rit.edu)
 */
public class Controller extends HasLogger
{
	// Model reference
	private ModelProxy model;

	// Winner info
	private int winnerId;
	private String winnerName;
	
	// Game info
	private List<Integer> initializedPlayers;
		
	/**
	 * Initializes the controller
	 * @param c
	 * @param l
	 * @throws IOException
	 */
	public Controller(Config c, Logger l) throws IOException
	{
		this.c = c;
		this.l = l;
		this.LOG_NAME = "Engine";
		
		this.model = new ModelProxy(c, l);
		this.initializedPlayers = new ArrayList<Integer>();
	}
	
	/**
	 * Starts the game
	 * @throws Exception 
	 * @throws IOException 
	 */
	public void run() throws Exception
	{
		this.log(GlobalConfig.GAME_NAME + " Java Client v" + GlobalConfig.VERSION + " by " +
			GlobalConfig.AUTHOR + " starting up");
		
		// Check number of players
		if (this.c.getPlayerModules().size() > 4)
		{
			this.die("Only up to four players are allowed");
		}
		
		// Remote game checks
		if (this.c.isRemote() && this.c.getPlayerModules().size() != 1)
		{
			this.die("Only one player can be specified in remote mode");
		}
		  
		int join = 0;
		int myPlayerNo = -1;
		if (this.c.isRemote())
		{			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.println("Do you wish to host the game? (yes/no)");
						
			String res = br.readLine().trim();
			
			// Join a game
			if (res.length() != 3)
			{
				Map<Integer, Lobby> lobbies = this.model.getLobbies();
				
				if (lobbies.size() == 0)
				{
					this.die("No lobbies available.  Please host a game instead");
				}
				
				System.out.format("%8s", "Game Id");
				System.out.format("%13s", " |     Host ");
				System.out.format("%18s", " |  Opponent Name | ");
				System.out.format("%5s", " #");
				System.out.println("\n--------------------------------------------------\n");
				
				for (int i : lobbies.keySet())
				{
					Lobby l = lobbies.get(i);
					
					System.out.format("%8s", l.gameId);
					System.out.format("%13s", l.host);
					System.out.format("%18s", l.opponent);
					System.out.format("%7s\n", l.opponentNo);
				}
				
				join = -1;
				
				while (!lobbies.keySet().contains(join))
				{
					System.out.println("Which game id would you like to join? ");
					try
					{
						join = Integer.parseInt(br.readLine());
					}
					catch (Exception e)
					{
						
					}
				}
				
				myPlayerNo = 0;
				if (1 == lobbies.get(join).opponentNo)
				{
					myPlayerNo = 1;
				}
			}
			// Host a game
			else
			{
				System.out.println("Which player would you like to be? (1 or 2)");
								
				if ("2".equals(br.readLine()))
				{
					myPlayerNo = 1;
					this.log("You have chosen to be player 2.");
				}
				else
				{
					myPlayerNo = 0;
					this.log("You have chosen to be player 1.");
				}
			}
		}
			
		// Dynamically load players
		List<PlayerModule> players = new ArrayList<PlayerModule>();
		List<String> playerNames = new ArrayList<String>();
		
		if (myPlayerNo == 1)
		{
			players.add(null);
			playerNames.add(null);
		}
		
		for (String m : this.c.getPlayerModules())
		{
			this.log("Loading player " + m);
			
			try
			{
				players.add((PlayerModule) 
					Class.forName(GlobalConfig.PLAYER_FOLDER + "." 
						+ m + "." + m)
					.getConstructor().newInstance()
				);
				
				playerNames.add(m);
			}
			catch (Exception e)
			{
				this.error("Failed to load player " + m 
						+ " (" + e.getMessage() + ")");
			}
		}
		
		if (myPlayerNo == 0)
		{
			players.add(null);
			playerNames.add(null);
		}
		
		// Verify that at least one module was loaded
		if (players.size() == 0)
		{
			this.die("No players were loaded");
		}
		
		if (players.size() == 3)
		{
			this.die("Cannot proceed with only 3 players");
		}
		
		this.log("Loaded " + Integer.toString(players.size()) + " player" 
			+ (players.size() != 1 ? "s" : ""));
		
		// Initialize the game
		try
		{
			// Remote or local game?
			if (join > 0)
			{
	            this.model.joinGame(playerNames, players, join);
			}
			else
			{
	            this.model.initGame(playerNames, players, this.c.isRemote() ? 2 : players.size());
			}
			
			if (!this.model.hasStarted())
			{
				this.log("Awaiting challenge from other players...");
			}
			
			while (!this.model.hasStarted())
			{
				this.model.syncState();
				System.out.print(".");
				if (this.model.hasStarted())
				{
					System.out.print("\n");
				}
				else
				{
					Thread.sleep(1000);
				}
			}			
		} 
		catch (Exception e)
		{
			this.die("Error during game initialization", e);
		}
				
		// Initialize player modules
		for (Integer playerNo : this.model.getValidPlayers())
		{
			if (this.model.isOwnPlayer(playerNo - 1))
			{
				try
				{
					this.log("Calling init() for player " + playerNo);
					
					this.model.initializePlayer(this.l, playerNo);
					
					this.initializedPlayers.add(playerNo);
				}
				catch (Exception e)
				{
					this.error("Invalidating player " + Integer.toString(playerNo) +
						" due to error in init", e);
					
					try
					{
						this.invalidatePlayerOnServer(playerNo, "Error in init");
					}
					catch (Exception e2)
					{
						this.die("Failed to invalidate player remotely", e2);
					}
				}
			}
		}
		
		// Open the UI
		if (this.c.isUi() && this.model.openUiImmediate())
		{
			try
			{
				if (this.c.isRemote())
				{
					this.model.openUi(this.model.getOwnPlayers().get(0) + 1);
				}
				else
				{
					this.model.openUi();
	
				}
			}
			catch(Exception e)
			{
				this.error("Failed to open web UI");
			}
		}
		
		// Main execution
		try
		{
			this.mainLoop();
		}
		catch (Exception e)
		{
			this.die("Error in main loop", e);
		}
		
		this.log("Game has ended");
		this.winnerId = this.model.getWinner();
		
		if (this.winnerId > 0)
		{
			this.winnerName = this.model.getPlayerName(this.winnerId);
		}
		else
		{
			this.winnerName = null;
		}	
	}
	
	/**
	 * Invalidates a player locally that has already been invalidated
	 * by the server
	 * @param playerNo
	 * @throws IOException
	 * @throws Exception
	 */
	public void invalidatePlayer(int playerNo) 
		throws Exception
	{
		this.log("Invalidating player " + Integer.toString(playerNo));
		
		for (Integer playerNo2 : this.model.getValidPlayers())
		{
			if (this.model.isValidPlayer(playerNo2) &&
				this.initializedPlayers.contains(playerNo2))
			{
				try
				{
					this.log("Notifying player " + Integer.toString(playerNo2) +
						" that player " + Integer.toString(playerNo) + 
						" was invalidated");
					
					this.model.getPlayerModule(playerNo2)
						.playerInvalidated(playerNo);
				}
				catch (Exception e)
				{
					this.error("Engine invalidating player " 
					+ Integer.toString(playerNo2) 
					+ " due to error in playerInvalidated", e);
					
					this.invalidatePlayerOnServer(playerNo2, 
						"Error in playerInvalidated");
				}
			}
		}
	}
	
	/**
	 * Invalidates a player and forces the server to perform an invalidation
	 * as well
	 * @param playerNo
	 * @param reason
	 * @throws IOException
	 * @throws Exception
	 */
	public void invalidatePlayerOnServer(int playerNo, String reason) 
		throws Exception
	{
		this.log("Invalidating player " + Integer.toString(playerNo));
		
		this.model.invalidatePlayer(playerNo, reason);
		
		for (Integer playerNo2 : this.model.getValidPlayers())
		{
			if (this.model.isValidPlayer(playerNo2) &&
				this.initializedPlayers.contains(playerNo2))
			{
				try
				{
					this.log("Notifying player " + Integer.toString(playerNo2) +
						" that player " + Integer.toString(playerNo) + 
						" was invalidated");
					
					this.model.getPlayerModule(playerNo2)
						.playerInvalidated(playerNo);
				}
				catch (Exception e)
				{
					this.error("Engine invalidating player " 
					+ Integer.toString(playerNo2) 
					+ " due to error in playerInvalidated", e);
					
					this.invalidatePlayerOnServer(playerNo2, 
						"Error in playerInvalidated");
				}
			}
		}
		
	}
	
	/**
	 * The main game loop
	 * @throws Exception 
	 */
	private void mainLoop() throws Exception
	{
		while (!this.model.isGameOver())
		{
			if (this.model.hasPreMoves() || !this.model.doValidate())
			{
				if (!this.c.isAutoPlay())
				{
					this.log("Hit ENTER to continue...");
					System.in.read();
				}
				
				this.nextTurn();
			}
			
			if (this.model.doValidate())
			{
				this.model.execPlayerValidation();
				this.log("Player validation completed");
				
				if (this.c.isUi())
				{
					try
					{
						this.model.openUi();
					} 
					catch (Exception e)
					{
						this.error("Failed to open web UI");
					}
				}
				
				break;
			}
			
			if (!this.model.isValid())
			{
				this.error("All players are invalid!");
				break;
			}
		}
	}
	
	/**
	 * This function is called when a move is to be made
	 */
	private void nextTurn()
	{
		PlayerMove nextMove = null;
		Boolean valid = false;
		long timeLimit = (long) (this.c.getPlayerMoveLimit() * 1000);
		int playerNo = this.model.getCurrentPlayer();
		
		try
		{
			if (!this.model.isOwnPlayer(playerNo - 1))
			{
				this.log("Awaiting remote move #" 
						+ Integer.toString(this.model.getNumMoves() + 1));
					
				while (playerNo == this.model.getCurrentPlayer() && !this.model.isGameOver())
				{
					this.model.syncState();
					Thread.sleep(GlobalConfig.POLL_INTERVAL);
					
					this.writeToConsole(".");
				}
				
				if (!this.model.isValidPlayer(playerNo))
				{
					this.error("Invalidating player " +
							Integer.toString(playerNo) + 
							" because they made an invalid move remotely");
					valid = false;
				}
				else
				{
					valid = true;
					nextMove = this.model.getLastMove();
					
					this.log("Downloaded remote move " + nextMove.toString());
				}
			}
			else if(this.c.isUi() && ! this.model.hasPreMoves() 
				&& this.model.getHumanPlayerName().equals(
					this.model.getPlayerName(this.model.getCurrentPlayer()))
			)
			{
				this.log("Processing human move #" 
					+ Integer.toString(this.model.getNumMoves() + 1));
				
				int moveNum = this.model.getNumMoves();
				
				this.log("Waiting for human move...");
				
				while(this.model.getNumMoves() == moveNum)
				{
					this.model.syncState();
					Thread.sleep(GlobalConfig.POLL_INTERVAL);
					
					this.writeToConsole(".");
				}
				
				this.writeToConsole("\n");
				
				nextMove = this.model.getLastMove();
				valid = true;
				
				this.log("Downloaded human move");
			}
			else
			{	
				this.log("Processing move #" 
						+ Integer.toString(this.model.getNumMoves() + 1));
				
				if (!this.model.hasPreMoves())
				{	
					this.log("Getting move from player " 
						+ Integer.toString(playerNo));
					
					getMove m = new getMove(playerNo);
					long timeStart = System.currentTimeMillis();
					m.run();
					
					while (m.finished == false && 
						(System.currentTimeMillis() - timeStart < timeLimit))
					{
						Thread.sleep(100);
					}
					
					if (m.nextMove != null)
					{
						nextMove = m.nextMove;
					}
					
					m.join(100);
				}
				else
				{
					this.log("Getting preMove from list");
					nextMove = this.model.getPreMove();
				}

				if (nextMove != null)
				{
					this.log("Validating move");
					
		            valid = this.model.makeMove(nextMove);
		            
		            if (!valid)
		            {
			            this.invalidatePlayer(playerNo);
		            }
				}
				else
				{
					this.error("Player " + Integer.toString(playerNo) 
						+ " timed out or returned no move.");
					this.invalidatePlayerOnServer(playerNo, 
						"Timed out or returned null in move()");
				}     
			}
			
			if (nextMove != null && valid)
			{
				this.log("Move validated. Notifying other players");
            	for (Integer otherPlayer : this.model.getValidPlayers())
            	{
            		if (this.model.isValidPlayer(otherPlayer) && this.model.isOwnPlayer(otherPlayer - 1))
            		{
            			moveInfo mi = new moveInfo(otherPlayer, nextMove);
    					long timeStart = System.currentTimeMillis();
    					mi.run();
    					
    					while (!mi.moveInfoState 
    							&& (System.currentTimeMillis() - timeStart 
    								< timeLimit))
    					{
    						Thread.sleep(100);
    					}
    					
    					if (!mi.moveInfoState)
    					{
    						this.error("Player " + Integer.toString(playerNo) + 
    							" timed out or raised an " +
    							"exception in moveInfo");

    						this.invalidatePlayerOnServer(otherPlayer, 
    							"Error or timeout in moveInfo");
    					}
    					
    					mi.join(100);	
            		}
            	}
			}

            this.log("Finished processing player " + playerNo + "'s move");
		}
		catch (Exception e)
		{
			this.die("Error in nextMove", e);
		}	
	}
	
	/**
	 * This class fetches a move from a player.  It enforces the configured
	 * time limit
	 * @author Adam
	 */
	private class getMove extends Thread
	{
		// Finished is set to true once the call returns
		public PlayerMove nextMove;
		public Boolean finished;
		
		// Internal state
		private int playerNo;
		
		/**
		 * Constructor to be called before running
		 * @param playerNo
		 */
		public getMove(int playerNo)
		{
			this.playerNo = playerNo;
			this.finished = false;
		}
		
		/**
		 * Main execution
		 */
		public void run()
		{
			try
			{
				this.nextMove = model.getPlayerModule(this.playerNo).move();
				this.finished = true;
			}
			catch(Exception e)
			{
				error("Error in getMove", e);
			}
		}
	}

	/**
	 * This class inform a player of others' moves.  It enforces the
	 * configured time limit
	 */
	private class moveInfo extends Thread
	{
		// Is set to true once the call returns
		public Boolean moveInfoState;
		
		// Internal state
		private PlayerMove move;
		private int playerNo;

		/**
		 * Constructor - to be called before running
		 * @param playerNo
		 * @param move
		 */
		public moveInfo(int playerNo, PlayerMove move)
		{
			this.move = move;
			this.moveInfoState = false;
			this.playerNo = playerNo;
		}
		
		/**
		 * Main execution
		 */
		public void run()
		{
			try
			{
				log("Notifying player " + Integer.toString(this.playerNo) + 
					" of last move");
				model.getPlayerModule(this.playerNo).lastMove(this.move);
				this.moveInfoState = true;
			}
			catch (Exception e)
			{
				error("Error in moveInfo", e);
			}
		}
	}
	
	/**
	 * Returns the id of the winning player, or 0 if there was no winner
	 * @return
	 */
	public int getWinnerId()
	{
		return this.winnerId;
	}

	/**
	 * Returns the name of the winning player, or null if there was no winner
	 * @return
	 */
	public String getWinnerName()
	{
		return this.winnerName;
	}
}
