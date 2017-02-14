import java.util.HashMap;
import java.util.Map;

import Engine.Config;
import Engine.GlobalConfig;
import Engine.Logger;
import Engine.Controller;

/**
 * Java implementation of the RIT Quoridor Game Web Client
 * 
 * Usage: LabyrinthClient [config file name]
 * 
 * @author Adam Oest (amo9149@rit.edu)
 */
public class LabyrinthClient
{
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		// Determine config file name
		String configFile = GlobalConfig.DEF_CONFIG;
		
		// Config parameters
		Map<String, String> override = new HashMap<String, String>();
		for (int i = 0; i < args.length; i += 2)
		{
			if(i + 1 < args.length)
			{
				override.put(args[i], args[i + 1]);
			}
			else
			{
				configFile = args[i];
			}
		}
				
		// Initialize the controller
		Controller c = new Controller(
			new Config(configFile, override),
			new Logger()
		);
		
		// Run the game
		c.run();
		
		if (c.getWinnerId() > 0)
		{
			if (null != c.getWinnerName())
			{
				System.out.println("Player " + Integer.toString(c.getWinnerId()) + 
					" (" + c.getWinnerName() + ") won the game!");
			}
			else
			{
				System.out.println("Player " + Integer.toString(c.getWinnerId()) + 
					" won the game!");
			}
		}
		else
		{
			System.out.println("No winner.");
		}
	}
}
