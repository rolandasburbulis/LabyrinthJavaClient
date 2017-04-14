package Model;

import Engine.*;
import Interface.Coordinate;
import Interface.PlayerModule;
import Interface.PlayerMove;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

/**
 * A dummy model that replicates the state sent by the 
 * 	game server
 * 
 * @author Adam Oest (amo9149@rit.edu)
 */
public class ModelProxy extends HasLogger
{	
	// Network proxy reference
	private NetworkProxy net;
	
	// API key from the config file
	private String apiKey;
	
	// List of moves to be made before players get control
	private List<PlayerMove> preMoves;
	
	// Players
	private List<PlayerModule> players;
	private List<String> playerNames;
	
	// Current game state
	private GameState state;
	
	// Current game information
	private String secretKey;
	private String publicKey;
	private int gameId;
	private int timeOffset;
	
	// Own player (for multi-client games, else first player)
	private List<Integer> ownPlayers;
	private boolean gameStarted;
	
	// Name of the human player, as defined by the server
	private String humanPlayer;

	// Labyrinth-specific
	private List<List<List<Object>>> board;
	private List<List<Integer>> treasures;
	private List<Object> extra;
	private List<Coordinate> playerHomes;

	private Integer seed;
		
	/**
	 * Constructor
	 * @param c
	 * @param l
	 */
	public ModelProxy(Config c, Logger l)
	{
		// Set the api key
		this.apiKey = c.getApiKey();
		
		// Initialize the network proxy
		this.net = new NetworkProxy(c.getWebServiceDomain(), 
			GlobalConfig.FANCY_URL, c.isHttps());
		
		// Set remaining fields
		this.preMoves = c.getPreMoves();
		
		this.c = c;
		this.l = l;
		
		this.LOG_NAME = "Model ";
		
		this.ownPlayers = new ArrayList<Integer>();
	}
	
	/**
	 * Initialize the game
	 * @param playerNames
	 * @param players
	 * @return
	 * @throws Exception
	 */
	public boolean initGame(List<String> playerNames, 
		List<PlayerModule> players, int numPlayers) throws Exception
	{		
		this.playerNames = playerNames;
		this.players = players;
		
		// Generate post parameters
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("apikey", this.apiKey));
		params.add(new BasicNameValuePair("numplayers", Integer.toString(numPlayers)));
		params.add(new BasicNameValuePair("seed", Integer.toString(this.c.getSeed())));
		params.add(new BasicNameValuePair("maxmoves", Integer.toString(this.c.getMoveCountLimit())));
		params.add(new BasicNameValuePair("treasures", this.c.getTreasures().toString()));
		
		int i = 0;
		for (String player : this.playerNames)
		{
			if (player != null)
			{
				this.ownPlayers.add(i);
				params.add(new BasicNameValuePair("playernames[" + Integer.toString(i) + "]", player));
			}
			i++;
		}
		
		Map<Object, Object> data = this.processData
			(
				this.net.callRemoteFunction("initgame", params
			)		
		);
		
		// Set state
		this.initState(data);
		
		if (!this.state.isValid())
		{
			this.error("Invalid game state!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Initializes state based on data response
	 * @param data
	 */
	private void initState(Map<Object,Object> data)
	{
		// Basic state
		this.secretKey = (String) data.get("secret");
		this.publicKey = (String) data.get("public");
		this.gameId = (Integer) data.get("gameid");
		this.humanPlayer = (String) data.get("humanplayer");
		this.gameStarted = (Boolean) data.get("started");
		this.timeOffset = 
			(Integer) data.get("time") - 
			(int) (System.currentTimeMillis() / 1000);

		// Parse current game state
		this.state = new GameState(data.get("status"));

		// Everything below is game-specific
		
		this.seed = (Integer) data.get("seed");

		if (this.seed != this.c.getSeed())
		{
			this.log("Auto-generated seed: " + Integer.toString(this.seed));
		}
		
		// Parse initial state
		this.board = (List<List<List<Object> > >) data.get("board");
		this.treasures = (List<List<Integer>>) data.get("treasures");
		this.extra = (List<Object>) data.get("extra");
		
		Map<String, List<Integer>> locations = (Map<String, List<Integer>>)
		 data.get("playerlocations");
		
		this.playerHomes = new ArrayList<Coordinate>();
		for (String s : locations.keySet())
		{
			this.playerHomes.add(
				new Coordinate(
						 locations.get(s).get(0),
						 locations.get(s).get(1)
				)
			);	
		}
	}
	
	/**
	 * Joins an existing game
	 * @param playerNames
	 * @param players
	 * @return
	 * @throws Exception
	 */
	public boolean joinGame(List<String> playerNames, 
		List<PlayerModule> players, int gameId) throws Exception
	{		
		this.playerNames = playerNames;
		this.players = players;
		
		// Generate post parameters
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("apikey", this.apiKey));
		params.add(new BasicNameValuePair("gameid", Integer.toString(gameId)));
		
		int i = 0;
		for (String player : this.playerNames)
		{
			if (player != null)
			{
				this.ownPlayers.add(i);
				params.add(new BasicNameValuePair("playername", player));
				break;
			}
			i++;
		}
		
		Map<Object, Object> data = this.processData
			(
				this.net.callRemoteFunction("joingame", params
			)		
		);
		
		// Set state
		this.initState(data);
		
		if (!this.state.isValid())
		{
			this.error("Invalid game state!");
			return false;
		}
		
		return true;
	}
	
	public Map<Integer, Lobby> getLobbies() 
		throws IOException, Exception
	{
		// Generate post parameters
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("apikey", this.apiKey));
		
		Map<Object, Object> data = this.processData
		(
			this.net.callRemoteFunction("getlobbies", params
		));
		
		Map<Integer, Lobby> lobbies = new HashMap<Integer, Lobby>();		
		
		try
		{
			Map<String, Map<String, Object>> rawdata = 
				(Map<String, Map<String, Object>>) data.get("lobbies");	
									
			for (String i : rawdata.keySet())
			{
				int gameid = Integer.parseInt(i.trim());
				List<String> clients = (ArrayList<String>) rawdata.get(i).get("clients");
				Map<String, String> players = (HashMap<String,String>) rawdata.get(i).get("players");
				String opponentPlayer = (String) players.keySet().toArray()[0];
				
				lobbies.put(gameid, new Lobby(gameid, 
						clients.get(0), players.get(opponentPlayer), 
						Integer.parseInt(opponentPlayer)));
			}
		} catch(Exception e) {}
			
		return lobbies;
	}
	
	/**
	 * Synchronizes the game states
	 * @throws Exception 
	 * @throws IOException 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws Exception
	 */
	public void syncState() throws IOException, Exception 
	{
		int time = this.time();
		
		List<BasicNameValuePair> params = Arrays.asList(
				new BasicNameValuePair("secret", this.getSecrect(time, this.ownPlayers.get(0) + 1)), 
				new BasicNameValuePair("time", Integer.toString(time)),
				new BasicNameValuePair("gameid", Integer.toString(this.gameId)),
				new BasicNameValuePair("playerno", Integer.toString(this.ownPlayers.get(0) + 1))
		);
		
		Map<Object, Object> data = this.processData(
				this.net.callRemoteFunction("gamestate", params));
		
		Boolean started = (Boolean) data.get("started");
		
		if (started)
		{
			this.gameStarted = true;
			this.state = new GameState(data.get("status")); 
		}	
	}
	
	/**
	 * Opens the game UI
	 * @throws Exception
	 */
	public void openUi() throws Exception
	{
		this.openUi(0);
	}
	
	public void openUi(int playerno) throws Exception
	{
		String url = this.net.buildUrl("gameview",
			Arrays.asList(
				new BasicNameValuePair("animations", 
					Integer.toString(this.c.getAnimationSpeed())),
				new BasicNameValuePair("seed", 
					Integer.toString(this.seed)),
				new BasicNameValuePair("gameid", Integer.toString(this.gameId)),
				new BasicNameValuePair("myplayer", Integer.toString(playerno)),
				new BasicNameValuePair("public", this.publicKey)
			)		
		);
		
		this.net.openBrowserWindow(url);
		
		this.log("GUI accessible at:\n" + url);
	}
	
	/**
	 * Invalidates a player
	 * @param playerNo
	 * @param reason
	 * @throws IOException
	 * @throws Exception
	 */
	public void invalidatePlayer(int playerNo, String reason)
		throws IOException, Exception 
	{
		int time = this.time();
		
		List<BasicNameValuePair> params = Arrays.asList(
			new BasicNameValuePair("secret", this.getSecrect(time, playerNo)), 
			new BasicNameValuePair("time", Integer.toString(time)),
			new BasicNameValuePair("gameid", Integer.toString(this.gameId)),
			new BasicNameValuePair("playerno", Integer.toString(playerNo)),
			new BasicNameValuePair("reason", reason)
		);
				
		this.state = new GameState(
			this.processData(
				this.net.callRemoteFunction("invalidateplayer", params)		
		).get("status")); 
	}
	
	/**
	 * Converts tile representation
	 * @param s
	 * @return
	 */
	public int tileStringToInt(String s)
	{
		if ("T".equals(s))
		{
			return 1;
		}
		else if ("L".equals(s))
		{
			return 0;
		}
		else if ("I".equals(s))
		{
			return 2;
		}
		
		return -1;
	}
	
	/**
	 * Makes a new move
	 * @param m
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean makeMove(PlayerMove m) 
		throws IOException, Exception
	{
		int time = this.time();
			 
		List<BasicNameValuePair> params = Arrays.asList(
			new BasicNameValuePair("secret", 
				this.getSecretMakeMove(time, m)), 
			new BasicNameValuePair("time", Integer.toString(time)),
			new BasicNameValuePair("gameid", Integer.toString(this.gameId)),
			new BasicNameValuePair("playerno", 
				Integer.toString(m.getPlayerId())),
			new BasicNameValuePair("rotation", 
				Integer.toString(m.getTileRotation())),
			new BasicNameValuePair("r", 
				Integer.toString(m.getTileInsertion().getRow())),
			new BasicNameValuePair("c", 
				Integer.toString(m.getTileInsertion().getCol())),
			new BasicNameValuePair("path", 
					m.getPath().toString())
		);
		
		Map <Object, Object> data = this.processData(
			this.net.callRemoteFunction("makemove", params)		
		);
		
		this.state = new GameState(data.get("status")); 
				
		if (data.get("validationerror").getClass() != Boolean.class)
		{
			this.error((String) data.get("validationerror"));
			return false;
		}
		
		return true;
	}
	
	/**
	 * Performs part one validation
	 */
	public void execPlayerValidation() throws Exception
	{
		PlayerModule player = this.getPlayerModule(1);
				
		this.log("Starting part one validation for " + 
				player.getClass().toString().split("\\.")[1]);
		
		this.error("Feature not implemented!");

		// TODO put any part one code here
	}
	
	/**
	 * Gets the last move that was made
	 * @return
	 */
	public PlayerMove getLastMove()
	{
		return this.state.getLastMove();
	}
	
	/**
	 * Returns a list of valid players
	 * @return
	 */
	public List<Integer> getValidPlayers()
	{
		List<Integer> output = new ArrayList<Integer>();
		
		for (Integer key : this.state.getPlayerValidity().keySet())
		{
			if (this.state.getPlayerValidity().get(key))
			{
				output.add(key);
			}
		}
		
		return output;
	}
	
	/**
	 * Is the given player valid?
	 * @param playerNo
	 * @return
	 */
	public Boolean isValidPlayer(int playerNo)
	{
		return this.state.getPlayerValidity().get(playerNo);
	}
	
	/**
	 * Gets the game winner, if one exists
	 * @return
	 */
	public int getWinner()
	{
		return this.state.getWinner();
	}
	
	/**
	 * Gets the name of the human player module
	 * @return
	 */
	public String getHumanPlayerName()
	{
		return this.humanPlayer;
	}
	
	/**
	 * Is the game over?
	 * @return
	 */
	public Boolean isGameOver()
	{
		return !this.state.isValid() || this.state.isGameOver();
	}
	
	/**
	 * Is the game state valid?
	 * @return
	 */
	public Boolean isValid()
	{
		return this.state.isValid();
	}
	
	/**
	 * Returns a reference to the request player module
	 * @param playerNo
	 * @return
	 */
	public PlayerModule getPlayerModule(int playerNo)
	{
		return this.players.get(playerNo - 1);
	}
	
	/**
	 * Returns the name of the requested player module
	 * @param playerNo
	 * @return
	 */
	public String getPlayerName(int playerNo)
	{
		return this.playerNames.get(playerNo - 1);
	}
	
	/**
	 * Gets the id of the current player
	 * @return
	 */
	public int getCurrentPlayer()
	{
		return this.state.getCurrentPlayer();
	}
	
	/**
	 * Gets the number of moves made so far
	 * @return
	 */
	public int getNumMoves()
	{
		return this.state.getNumMoves();
	}
	
	/**
	 * Gets the number of players in this game
	 * @return
	 */
	public int getNumPlayers()
	{
		return this.players.size();
	}
	
	/**
	 * Returns whether or not to open the UI right after initialization
	 * (will return false in part one mode)
	 * @return
	 */
	public boolean openUiImmediate()
	{
		return this.getNumPlayers() != 1 || !this.c.isPartOne();
	}
	
	/**
	 * Gets current player locations
	 * @return
	 */
	public List<Coordinate> getPlayerHomes()
	{
		return this.playerHomes;
	}
	
	/**
	 * Gets players that are currently valid except for the one 
	 * 	passed in
	 * @param playerNo
	 * @return
	 */
	public List<Integer> getValidPlayersExcept(int playerNo)
	{
		List<Integer> players = new ArrayList<Integer>(this.getValidPlayers());
		Iterator<Integer> it = players.iterator();
		while (it.hasNext())
		{
		    if (it.next().equals(playerNo))
		    {
		        it.remove();
		        break;
		    }
		}
		
		return players;
	}
	
	/**
	 * Do any preMoves remain?
	 * @return
	 */
	public boolean hasPreMoves()
	{
		return this.preMoves.size() > 0;
	}
	
	/**
	 * Gets the next preMove to be made
	 */
	public PlayerMove getPreMove()
	{
		PlayerMove m = this.preMoves.get(0);
		this.preMoves.remove(0);
		
		return m;
	}
            
	/**
	 * Should we perform part 1 validation?
	 */
	public boolean doValidate()
	{
		//return this.c.isPartOne() && !this.hasPreMoves() 
		//	&& this.players.size() == 1;
		
		return this.c.isPartOne();
	}
	
	/**
	 * Gets the client's own player (for multi-client games)
	 * @return
	 */
	public List<Integer> getOwnPlayers()
	{
		return this.ownPlayers;
	}
	
	/**
	 * Does the passed player id belong to this client?
	 * @param playerId
	 * @return
	 */
	public boolean isOwnPlayer(int playerId)
	{
		return this.ownPlayers.contains(playerId);
	}
	
	/**
	 * Has the game started?
	 * @return
	 */
	public boolean hasStarted()
	{
		return this.gameStarted;
	}
	
	/**
	 * Gets the initial treasure list
	 * @return
	 */
	public List<List<Integer>> getTreasures()
	{
		return this.treasures;
	}

	/**
	 * Gets the game board
	 * @return
	 */
	public List<List<List<Object>>> getBoard()
	{
		return this.board;
	}

	/**
	 * Gets the extra tile
	 * @return
	 */
	public List<Object> getExtra()
	{
		return this.extra;
	}
	
	// Private methods
	
	/**
	 * Returns the current UNIX timestamp
	 * @return
	 */
	private int time()
	{
		return (int) (System.currentTimeMillis() / 1000) + this.timeOffset;
	}
	
	/**
	 * Processes the data returned from the server
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private Map<Object, Object> processData(Map<Object, Object> data)
		throws Exception
	{
		if (GlobalConfig.DEBUG)
		{
			try
			{
				System.out.println("Got response: ");
				for (Object o : data.keySet())
				{
					System.out.println("      " + (String) o.toString() 
							+ " => " + (String) data.get(o).toString());
				}
			
			}
			catch (Exception e)
			{
				System.out.println("Malformed response?");
			}
		}
		
		if (!data.containsKey("error"))
		{
			throw new Exception("Malformed network data");
		}
		
		if (data.get("error").getClass() != Boolean.class)
		{
			throw new Exception((String) data.get("error"));
		}
		
		if (!data.containsKey("data"))
		{
			throw new Exception("Malformed network data");
		}
		
		return (Map<Object, Object>) data.get("data");
	}
		
	/**
	 * Generates a secret key for the given timestamp and player
	 * @param timeStamp
	 * @param player
	 * @return
	 */
	private String getSecrect(int timeStamp, int player) 
	{
		try
		{		
	        String s =  GlobalConfig.RND + "_" 
	        	+ Integer.toString(timeStamp) + "_" 
	        	+ Integer.toString(this.gameId) + "_" 
	        	+ Integer.toString(player) + "_" 
	        	+ this.secretKey;
	        
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(s.getBytes("UTF-8"));
			String d = new BigInteger(1, md.digest()).toString(16);  
			
			// Pad with zeros if needed
			while (d.length() != 32)
			{
				d = "0" + d;  
			}
			
			return d;
		}
		catch(Exception e)
		{
			
		}
		
		return "";
	}
	
	/**
	 * Generates a secret key for moves
	 * @param timeStamp
	 * @param m
	 * @return
	 */
	private String getSecretMakeMove(int timeStamp, PlayerMove m) 
	{
		try
		{
			String s = 
				GlobalConfig.RND + "_" 
				+ Integer.toString(timeStamp) + "_" 
				+ Integer.toString(this.gameId) + "_" 
				+ Integer.toString(m.getPlayerId()) + "_" 
				+ Integer.toString(m.getTileInsertion().getRow()) + "_"
				+ Integer.toString(m.getTileInsertion().getCol()) + "_"
				+ Integer.toString(m.getTileRotation()) + "_" 
				+ Integer.toString(m.getPath().size()) + "_" 
				+ this.secretKey;
				
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(s.getBytes("UTF-8"));
			String d = new BigInteger(1, md.digest()).toString(16);  
			// Pad with zeros if needed
			while (d.length() != 32)
			{
				d = "0" + d;  
			}
			return d;
		}
		catch(Exception e)
		{
			
		}
		
		return "";
	}

	/**
	 * Initializes a player module
	 * 
	 * This is done in the model class since it varies from game to game
	 * 
	 * @param l
	 * @param playerNo
	 */
	public void initializePlayer(Logger l, Integer playerNo)
	{
		// Data structure conversion code
		List<Integer> extra = new ArrayList<Integer>();
		List<List<List<Integer>>> board =
			new ArrayList<List<List<Integer > > >();
		
		// Convert extra tile to array of ints
		Integer tileTreasure = -1;
		try
		{
			tileTreasure = (Integer) this.getExtra().get(2);
		}
		catch (Exception e){}
		
		extra.add(this.tileStringToInt((String) this.getExtra().get(0)));
		extra.add(tileTreasure);
		
		// Convert tiles to arrays of ints
		for (int r = 0; r < this.getBoard().size(); r++)
		{
			board.add(new ArrayList<List<Integer> >());
			
			for (int c = 0; c < this.getBoard().get(r).size(); c++)
			{
				List<Object> oldTile = this.getBoard().get(r).get(c);
				
				List<Integer> newTile = new ArrayList<Integer>(3);
				
				tileTreasure = -1;
				try
				{
					tileTreasure = (Integer) oldTile.get(2);
				}
				catch (Exception e){}
				
				newTile.add(this.tileStringToInt((String) oldTile.get(0)));
				newTile.add((Integer) oldTile.get(1));
				newTile.add(tileTreasure);
				
				board.get(r).add(newTile);
			}
		}
		// End conversion
		
		this.getPlayerModule(playerNo).init(
			l,
			playerNo,
			this.getPlayerHomes(),
			this.getTreasures(),
			board,
			extra
		);	
	}
}
