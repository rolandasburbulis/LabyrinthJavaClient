package Engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class writes log messages to a file
 * 
 * @author Adam Oest (amo9149@rit.edu)
 */
public class Logger
{
	private BufferedWriter writer;
	private String filename;
	private final String LOG_NAME = "Logger";
	private boolean closed = true;
	
	/**
	 * Initializes with the default filename
	 * @throws IOException 
	 */
	public Logger() throws IOException
	{
		this.filename = this.generateFilePath();
		this.openFile();
	}
	
	/**
	 * Initializes with a given filename
	 * @param filename
	 * @throws IOException 
	 */
	public Logger(String filename) throws IOException
	{
		this.filename = filename;
		this.openFile();
	}
	
	/**
	 * Writes the log file and opens it
	 * @throws IOException
	 */
	private void openFile() throws IOException
	{
		File f = new File(this.filename);
		if(!f.exists()) f.createNewFile();
		FileWriter fstream = new FileWriter(this.filename);
		this.writer = new BufferedWriter(fstream);
		this.closed = false;
	}
	
	/**
	 * Generates the file name to use
	 * @return
	 */
	public String generateFilePath()
	{
		Date d = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd_HH-mm-ss");
		String date = sdf.format(d);
		
		return GlobalConfig.LOG_FOLDER + "/log" + date + ".txt"; 
	}
	
	/**
	 * Writes a complete log message
	 * @param source
	 * @param message
	 */
	public String msg(String source, String message)
	{
		Date d = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
		String date = sdf.format(d);
		
		return date	+ " " + source +  " " + message;
	}
	
	/**
	 * Writes a complete log message
	 * @param source
	 * @param message
	 */
	public String error(String source, String message)
	{
		return this.msg(source, "Error: " + message);
	}
	
	/**
	 * Writes a complete log message
	 * @param source
	 * @param message
	 */
	public void writeMsg(String source, String message)
	{		
		this.writeln(this.msg(source, message));
	}
	
	/**
	 * Writes a complete log message
	 * @param source
	 * @param message
	 */
	public void writeError(String source, String message)
	{		
		this.writeln(this.error(source, message));
	}
	
	/**
	 * Writes a line with arbitrary contents
	 * @param message
	 * @throws IOException
	 */
	public void writeln(String message)
	{
		this.write(message + "\r\n");
	}
	
	/**
	 * Writes arbitrary contents to the file
	 * @param message
	 * @throws IOException
	 */
	public void write(String message)
	{
		if (!this.closed)
		{
			try
			{
				this.writer.write(message);
				this.writer.flush();	
			}
			catch (IOException e)
			{
				System.err.println(this.error(this.LOG_NAME, 
					"IOException"));
			}
		}
		else
		{
			System.err.println(this.error(this.LOG_NAME, 
				"Log file not open!"));
		}
	}
	
	/**
	 * Closes the log file
	 * @throws IOException
	 */
	public void closeFile() throws IOException
	{
		this.closed = true;
		this.writer.close();
	}
}
