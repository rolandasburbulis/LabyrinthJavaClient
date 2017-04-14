package Model;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class NetworkMessage
{
	Map<String, String> json;
	
	/**
	 * Constructor
	 * @param json
	 */
	public NetworkMessage(Map<String, String> json)
	{
		this.json = json;
	}
	
	/**
	 * Gets a key
	 * @param key
	 */
	public String getKey(String key)
	{
		if (this.json.containsKey(key))
		{
			return this.json.get(key);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Gets all keys
	 */
	public Set<String> getKeys()
	{
		return this.json.keySet();
	}
	
	/**
	 * Gets all values
	 */
	public Collection<String> getValues()
	{
		return this.json.values();
	}
	
	/**
	 * Gets all key-value pairs
	 */
	public Collection<BasicNameValuePair> getItems()
	{
		Collection<BasicNameValuePair> items = 
			new ArrayList<BasicNameValuePair>();
		
		for (String s : this.getKeys())
		{
			items.add(new BasicNameValuePair(s, this.getKey(s)));
		}
		
		return items;
	}
}
