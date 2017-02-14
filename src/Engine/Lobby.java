package Engine;

/**
 * Class representing a remote game lobby
 * that can be joined by another player
 * 
 * @author Adam
 */
public class Lobby
{
	public int gameId;
	public String host;
	public String opponent;
	public int opponentNo;
	
    public Lobby(int gameId, String host, String opponent, int opponentNo)
    {
    	this.gameId = gameId;
    	this.host = host;
    	this.opponent = opponent;
    	this.opponentNo = opponentNo;
    }  
}
