package Engine;

import Interface.Coordinate;
import Interface.PlayerMove;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class reads a configuration file
 * 
 * Currently, all fields are hard-coded.  This should be changed in the
 * future.
 * 
 * @author Adam Oest (amo9149@rit.edu)
 */
public class Config
{
	// Config fields
	private String apiKey;
	private boolean ui;
	private boolean autoPlay;
	private int animationSpeed;
	private boolean fileLogging;
	private boolean stdoutLogging;
	private List<String> playerModules;	
	private float playerMoveLimit;
	private boolean https;
	private String webServiceDomain;
	private List<PlayerMove> preMoves;
	private boolean remote;
	private List<List<Integer>> treasures;
	private int seed = 0;
	private int moveCountLimit = 250;
	private boolean partOne;
	
	/**
	 * Generate empty config
	 */
	public Config()
	{
		this.preMoves = new ArrayList<PlayerMove>();
		this.playerModules = new ArrayList<String>();
		this.treasures = new ArrayList<List<Integer> >();
	}
	
	/**
	 * Generate config from a file
	 * @param file
	 * @throws Exception
	 */
	public Config(String file, Map<String, String> override) throws Exception
	{
		this.preMoves = new ArrayList<PlayerMove>();
		this.treasures = new ArrayList<List<Integer> >();
		
		BufferedReader br = new BufferedReader(new FileReader(file));  
		String line;
				
		while ((line = br.readLine()) != null)  
		{  
			line = line.replaceAll("\\#.*$", "").trim();
			
			if (line.length() > 0)
			{
				String[] fragments = line.split(" ", 2);
				
				if (fragments.length == 2)
				{
					String key = fragments[0];
					String value = fragments[1].trim();
					
					// See if user changed the argument on the command line
					if (override.containsKey(key))
					{
						value = override.get(key).trim();
					}
					
					// Do it the old way :(
					if ("API_KEY".equals(key))
					{
						this.apiKey = value;
					}
					else if ("PLAYER_MODULES".equals(key))
					{
						this.playerModules = this.parsePlayerModules(value);
					}
					else if ("REMOTE".equals(key))
					{
						this.remote = this.isTrue(value);
					}
					else if ("UI".equals(key))
					{
						this.ui = this.isTrue(value);
					}
					else if ("AUTO_PLAY".equals(key))
					{
						this.autoPlay = this.isTrue(value);
					}
					else if ("PART_ONE".equals(key))
					{
						this.partOne = this.isTrue(value);
					}
					else if ("ANIMATION_SPEED".equals(key))
					{
						this.animationSpeed = Math.abs(Integer.parseInt(value));
					}
					else if ("FILE_LOGGING".equals(key))
					{
						this.fileLogging = this.isTrue(value);
					}
					else if ("STDOUT_LOGGING".equals(key))
					{
						this.stdoutLogging = this.isTrue(value);
					}
					else if ("PRE_MOVE".equals(key))
					{
						this.preMoves.add(this.parseMove(value));
					}
					else if ("TREASURES".equals(key))
					{
						if (!this.isFalse(value))
						{
							this.treasures = this.parseTreasures(value);
						}						
					}
					else if ("SEED".equals(key))
					{
						if (!this.isFalse(value))
						{
							this.seed = Math.abs(Integer.parseInt(value)) 
								% 1000000000;
						}
					}
					else if ("PLAYER_MOVE_LIMIT".equals(key))
					{
						this.playerMoveLimit = Float.parseFloat(value);
					}
					else if ("MOVE_COUNT_LIMIT".equals(key))
					{
						this.moveCountLimit = (Integer.parseInt(value));
					}
					else if ("WEB_SERVICE_DOMAIN".equals(key))
					{
						this.webServiceDomain = value;
					}
					else if ("HTTPS".equals(key))
					{
						this.https = this.isTrue(value);
					}
				}				
			}
		}
	}
	
	/**
	 * Parses player module names
	 * @param value
	 * @return
	 */
	private List<String> parsePlayerModules(String value)
	{
		List<String> modules = new ArrayList<String>();
		
		String[] playerModules = value.split(",");
		
		for (int i = 0; i < playerModules.length; i++)
		{
			modules.add(playerModules[i].trim());
		}
		
		return modules;
	}
	
	/**
	 * Checks if a string represents the boolean true
	 * @param value
	 * @return
	 */
	private boolean isTrue(String value)
	{
		return "true".equals(value.toLowerCase());
	}
	
	/**
	 * Checks if a string represents the boolean false
	 * @param value
	 * @return
	 */
	private boolean isFalse(String value)
	{
		return "false".equals(value.toLowerCase());
	}
	
	/**
	 * Parses the config file treasure list 
	 * Format: 
	 * [1,2,3,4,5,6],[7,8,9,10,11,12]
	 *   player 1   player 2
	 */
	private List<List<Integer>> parseTreasures(String value) throws Exception
	{
		String ovalue = value;
		
		try
		{
			value = value.replace(" ", "");
			value = value.replace("],[",";");
			value = value.replace("]","").replace("[", "");
			
			List<List<Integer>> masterList = new ArrayList<List<Integer> >();
			String[] fragments = value.split(";");
			
			for (int i = 0; i < fragments.length; i++)
			{
				List<Integer> playerList = new ArrayList<Integer>();
				
				String[] tstring = fragments[i].split(",");
				
				for (int j = 0; j < tstring.length; j++)
				{
					playerList.add(Integer.parseInt(tstring[j].trim()));
				}
				
				masterList.add(playerList);
			}
			
			return masterList;
		}
		catch (Exception e)
		{
			throw new Exception("Failed to parse treasure list " + ovalue);
		}
	}
	
	/**
	 * Parses a string list [[0,0],[1,0]...] into a list of Coordinates
	 * @param value
	 * @return
	 */
	private List<Coordinate> parseList(String value)
	{
		List<Coordinate> out = new ArrayList<Coordinate>();
		
		value = value.replace(" ", "");
		value = value.replace("],[",";");
		value = value.replace("]","").replace("[", "");
		
		String[] coords = value.split(";");
		
		if ("".equals(value))
		{
			return out;
		}
		
		for(int i = 0; i < coords.length; i++)
		{
			String[] coord = coords[i].split(",");
			
			out.add(new Coordinate(
					Integer.parseInt(coord[0]), 
					Integer.parseInt(coord[1])
			));
		}
		
		return out;
	}
	
	/**
	 * Converts a python move constructor to a PlayerMove object
	 * @param value
	 * @return
	 * @throws Exception 
	 */
	private PlayerMove parseMove(String value) throws Exception
	{	
		String cleaned =
			value.replace("PlayerMove(", "").replace(")", "").trim();
		
		List<String> allMatches = new ArrayList<String>();
		
		// This looks scary, but it's essentially just splitting the string and allowing whitespace
		Matcher m = 
			Pattern.compile("^(\\d)[ ]*,[ ]*\\[([\\[\\]0-9, ]*)\\][ ]*,[ ]*([\\[\\]0-9, ]+)[ ]*,[ ]*(\\d)[ ]*$")
		     .matcher(cleaned);
		while (m.find())
		{			 
		   allMatches.add(m.group(1).trim());
		   allMatches.add(m.group(2).trim());
		   allMatches.add(m.group(3).trim());
		   allMatches.add(m.group(4).trim());
		}
		
		if (allMatches.size() == 0)
		{
			throw new Exception("Failed to parse PRE_MOVE " + value);
		}
		
		PlayerMove move;
		
		try 
		{
			move = new PlayerMove(
				Integer.parseInt(allMatches.get(0)),
				this.parseList(allMatches.get(1)),
				this.parseList(allMatches.get(2)).get(0),
				Integer.parseInt(allMatches.get(3))
			);
			
			try
			{
				move.checkValidity();		 
			}
			catch (Exception e)
			{
				throw new Exception("\nError while parsing PRE_MOVES: " 
					+ e.getMessage() + "\n" + move.toString());
			}
		}
		catch (Exception e)
		{
			throw new Exception("Failed to parse PRE_MOVE " + value);
		}
				
		return move;
	}
	
	// Getters

	public int getMoveCountLimit()
	{
		return moveCountLimit;
	}
	
	public List<List<Integer>> getTreasures()
	{
		return treasures;
	}
	
	public int getSeed()
	{
		return seed;
	}
	
	public String getApiKey() 
	{
		return apiKey;
	}

	public boolean isUi()
	{
		return ui;
	}

	public boolean isAutoPlay()
	{
		return autoPlay;
	}

	public int getAnimationSpeed()
	{
		return animationSpeed;
	}

	public boolean isFileLogging()
	{
		return fileLogging;
	}

	public boolean isStdoutLogging()
	{
		return stdoutLogging;
	}

	public List<String> getPlayerModules()
	{
		return playerModules;
	}
	
	public float getPlayerMoveLimit()
	{
		return playerMoveLimit;
	}

	public boolean isHttps()
	{
		return https;
	}

	public String getWebServiceDomain()
	{
		return webServiceDomain;
	}

	public List<PlayerMove> getPreMoves()
	{
		return preMoves;
	}
	
	public boolean isRemote()
	{
		return remote;
	}

	// Setters
	
	public void setApiKey(String apiKey)
	{
		this.apiKey = apiKey;
	}

	public void setUi(boolean ui)
	{
		this.ui = ui;
	}

	public void setAutoPlay(boolean autoPlay)
	{
		this.autoPlay = autoPlay;
	}

	public void setAnimationSpeed(int animationSpeed)
	{
		this.animationSpeed = animationSpeed;
	}

	public void setFileLogging(boolean fileLogging)
	{
		this.fileLogging = fileLogging;
	}

	public void setStdoutLogging(boolean stdoutLogging)
	{
		this.stdoutLogging = stdoutLogging;
	}

	public void setPlayerMoveLimit(float playerMoveLimit)
	{
		this.playerMoveLimit = playerMoveLimit;
	}

	public void setHttps(boolean https)
	{
		this.https = https;
	}
	
	public void setRemote(boolean r)
	{
		this.remote = r;
	}

	public void setWebServiceDomain(String webServiceDomain)
	{
		this.webServiceDomain = webServiceDomain;
	}

	public void setTreasures(List<List<Integer>> treasures)
	{
		this.treasures = treasures;
	}

	public void setSeed(int seed)
	{
		this.seed = seed;
	}

	public void setMoveCountLimit(int moveCountLimit)
	{
		this.moveCountLimit = moveCountLimit;
	}

	public void setPartOne(boolean partOne) {
		this.partOne = partOne;
	}

	public boolean isPartOne() {
		return partOne;
	}
}