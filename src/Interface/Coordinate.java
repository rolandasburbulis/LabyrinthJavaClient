package Interface;

import java.io.Serializable;

/**
 * This class represents a row, column coordinate on the
 * 	Labyrinth board
 * 
 * @author Adam Oest (amo9149@rit.edu)
 */
public class Coordinate implements Serializable
{
	// The board dimensions
	public static final int BOARD_DIM = 7;
	private static final long serialVersionUID = 4679448616847089820L;

	// Row and column coordinates
	private int row;
	private int col;
	
	/**
	 * Copy constructor
	 * @param c2
	 */
	public Coordinate(Coordinate c2)
	{
		this.row = c2.getRow();
		this.col = c2.getCol();
	}
	
	/**
	 * Throws an exception if invalid values are supplied
	 * @throws Exception
	 */
	public void checkValidity() throws Exception
	{
		if (row < 0 || row >= Coordinate.BOARD_DIM)
			throw new Exception ("Coordinate invalid row " + Integer.toString(row));
		if (col < 0 || col >= Coordinate.BOARD_DIM)
			throw new Exception ("Coordinate invalid col " + Integer.toString(col));
	}
	
	/**
	 * Main constructor
	 * @param row
	 * @param col
	 */
	public Coordinate(int row, int col)
	{
		this.row = row;
		this.col = col;
		
		assert this.row >= 0;
		assert this.col >= 0;
		assert this.row < Coordinate.BOARD_DIM;
		assert this.col < Coordinate.BOARD_DIM;
	}

	/**
	 * Returns the row coordinate
	 * @return
	 */
	public int getRow()
	{
		return row;
	}

	/**
	 * Returns the column coordinate
	 * @return
	 */
	public int getCol()
	{
		return col;
	}
	
	/**
	 * Equality checker
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj)
	{
		try
		{
			Coordinate c2 = (Coordinate) obj;
			return this.row == c2.getRow() && this.col == c2.getCol();
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/**
	 * Hash code
	 */
	@Override
	public int hashCode()
	{
		return this.row + 10 * this.col;
	}

	/**
	 * String representation
	 */
	@Override
	public String toString()
	{
		return "[" + row + ", " + col + "]";
	}
	
	/**
	 * List representation
	 */
	public int[] asList()
	{		
		return new int[]{row,col};
	}
}