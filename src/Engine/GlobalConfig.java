package Engine;

/**
 * This class contains configuration constants
 * 
 * @author Adam Oest (amo9149@rit.edu)
 */
public class GlobalConfig
{	
	// Network connection timeout (ms)
	public static final int TIMEOUT = 15000;
	
	// Folder containing log files
	public static final String LOG_FOLDER = "Logs";
	
	// Folder containing player packages
	public static final String PLAYER_FOLDER = "Players";
	
	// Name of the game
	public static final String GAME_NAME = "Labyrinth";
	
	// Client version
	public static final String VERSION = "1.0.0";
	
	// Client author
	public static final String AUTHOR = "ADAM OEST";
	
	// Whether or not we are using fancy URLs
	public static final boolean FANCY_URL = true;

	// Default config file name
	public static final String DEF_CONFIG = "config.txt";
	
	// Debug mode on/off
	public static final boolean DEBUG = false;
	
	// How often do we poll the server for human moves? (ms)
	public static final int POLL_INTERVAL = 750;
	
	// Salt
	public static final String RND = "iuuhAsf92hasdZZ";
}
