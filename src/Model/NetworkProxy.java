package Model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;

import org.jsoup.*;
import org.apache.http.message.BasicNameValuePair;
import com.fasterxml.jackson.databind.ObjectMapper;

import Engine.GlobalConfig;

/**
 * Class that facilitates network communication
 * 	with the Quoridor Web Engine
 * 
 * @author Adam Oest (amo9149@rit.edu)
 */
public class NetworkProxy
{
	// The URL to the web service
	private String serviceUrl;
	
	// Whether or not fancy URLs are used
	private boolean fancyUrl;
	
	// The filename and get parameter for non-fancy URLs
	private static final String regularUrlFile = "index.php";
	private static final String regularUrlAction = "do";
	
	/**
	 * Constructor.  Initializes the web gateway.
	 */
	public NetworkProxy(String serviceUrl, boolean fancyUrl, boolean https)
	{
		this.serviceUrl = "http" + (https ? "s" : "") 
			+ "://" + serviceUrl + "/";
		this.fancyUrl = fancyUrl;
	}
	
	/**
	 * Opens a browser window with a framework URL
	 * 
	 * @param fn
	 * @param args
	 * @throws Exception
	 */
	public void openBrowserWindow(String url) throws Exception
	{
		if(Desktop.isDesktopSupported())
		{
			Desktop.getDesktop().browse(new URI(url));
		}
		else
		{
			throw new Exception("Unable to open web browser");
		}
	}
	
	/**
	 * Calls a function remotely and returns the result
	 * @param fn
	 * @param args
	 * @return
	 * @throws IOException
	 */
	public Map<Object, Object> callRemoteFunction(String fn, 
		List<BasicNameValuePair> args) throws IOException
	{
		if (GlobalConfig.DEBUG)
		{
			System.out.println("NetworkProxy calling " + fn);
			
			for (BasicNameValuePair p : args)
			{
				System.out.println("      " + p.toString());
			}
		}
		
		String data = this.getUrlContents(this.buildUrl(fn), args);
		
		if (GlobalConfig.DEBUG)
		{
			System.out.println("NetworkProxy got response " + data);
		}
		
		return new ObjectMapper().readValue(data, 
				new HashMap<Object, Object>().getClass());
	}
	
	/**
	 * Calls a function remotely and returns the result
	 * @param fn
	 * @return
	 * @throws IOException
	 */
	public Map<Object, Object> callRemoteFunction(String fn) 
		throws IOException
	{
		return this.callRemoteFunction(fn, new ArrayList<BasicNameValuePair>());
	}
	
	/**
	 * Retrieves the contents of a URL
	 * @param url
	 * @param args
	 * @return
	 * @throws IOException
	 */
	synchronized private String 
		getUrlContents(String url, List<BasicNameValuePair> args)
		throws IOException
	{	
		Connection c = Jsoup.connect(url);
		
		for(BasicNameValuePair p : args)
		{
			c.data(p.getName(), p.getValue());
		}
		
		return c.timeout(GlobalConfig.TIMEOUT).post().text().trim();
	}
	
	/**
	 * Generates a URL with no GET parameters
	 * @param fn
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private String buildUrl(String fn) throws UnsupportedEncodingException
	{
		return this.buildUrl(fn, new ArrayList<BasicNameValuePair>());
	}
	
	/**
	 * Generates a full framework URL
	 * @param fn
	 * @param args
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String buildUrl(String fn,  List<BasicNameValuePair> args) 
		throws UnsupportedEncodingException
	{
		String s;
		if (this.fancyUrl)
		{
			s = this.buildFancyUrl(fn, args);
		}
		else
		{
			List<BasicNameValuePair> newargs = 
				new ArrayList<BasicNameValuePair>(args);
			newargs.add(0, 
				new BasicNameValuePair(NetworkProxy.regularUrlAction, fn));
			s = this.buildRegularUrl(fn, newargs);
		}
		
		return s;
	}
	
	/**
	 * Fancy URL variant
	 * @param fn
	 * @param args
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String buildFancyUrl(String fn, List<BasicNameValuePair> args) 
		throws UnsupportedEncodingException
	{
		String qs = "";
		for(BasicNameValuePair p : args)
		{
			qs += p.getName() + "." 
				+ URLEncoder.encode(p.getValue(), "UTF-8") + "/";
		}
	       
		return this.serviceUrl + fn + "/" + qs;    
	}
	
	/**
	 * Basic URL variant
	 * @param fn
	 * @param args
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String buildRegularUrl(String fn, List<BasicNameValuePair> args) 
		throws UnsupportedEncodingException
	{
		String url = this.serviceUrl + NetworkProxy.regularUrlFile + '?';
		
		for (BasicNameValuePair p : args)
		{
			url += p.getName() + "=" 
				+ URLEncoder.encode(p.getValue(), "UTF-8") + "&"; 
		}
		
		if (args.size() > 0)
		{
			url = url.substring(0, url.length() - 1);
		}
		
		return url;
	}
}