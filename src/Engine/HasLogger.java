package Engine;

/**
 * Superclass providing logging functionality
 * 
 * @author Adam Oest (amo9149@rit.edu)
 */
public abstract class HasLogger
{
	protected Config c;
	protected Logger l;
	protected String LOG_NAME;
	
	/**
	 * Writes a message to the console only
	 * @param message
	 */
	protected void writeToConsole(String message)
	{
		if (c.isStdoutLogging())
		{
			System.out.print(message);
		}
	}
	
	/**
	 * Logs a message if logging is enabled
	 * @param message
	 */
	protected void log(String message)
	{		
		if (c.isFileLogging())
		{
			this.l.writeMsg(LOG_NAME, message);
		}
		
		if (c.isStdoutLogging())
		{
			System.out.println(this.l.msg(LOG_NAME, message));
		}
	}
	
	/**
	 * Logs a message if logging is enabled
	 * @param message
	 */
	protected void error(String message)
	{		
		if (c.isFileLogging())
		{
			this.l.writeError(LOG_NAME, message);
		}
		
		if (c.isStdoutLogging())
		{
			System.err.println(this.l.error(LOG_NAME, message));
		}
	}
	
	/**
	 * Logs a message if logging is enabled
	 * @param message
	 */
	protected void error(String message, Exception e)
	{		
		e.printStackTrace();
		
		this.error(message);
	}
	
	/**
	 * Exit with an error message
	 * @param message
	 */
	protected void die(String message)
	{
		this.error(message);
		
		System.err.println(this.l.error(LOG_NAME,
			"Exiting due to error"));
		System.exit(0);
	}
	
	/**
	 * Exit with an error message
	 * @param message
	 */
	protected void die(String message, Exception e)
	{
		this.error(message, e);
		
		System.err.println(this.l.error(LOG_NAME,
			"Exiting due to error"));
		System.exit(0);
	}
}
