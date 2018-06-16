package cpu;

import java.util.Random;
import viergewinnt.IPlayer;
import viergewinnt.VierGewinnt;

/** A random computer player class */
public class ComputerPlayerRandom implements IPlayer{
	private VierGewinnt.Token token;
	
	public int getNextColumn(VierGewinnt.Token[][] board){
		int nrOfColumns = VierGewinnt.getNrOfColumns(board);
                int nrOfRows = VierGewinnt.getNrOfRows(board);
                
                Random generator = new Random();
		
                int column = -1;
		
		while(column < 0 || column > board.length){
			
			column=generator.nextInt(nrOfColumns);
			if(column >= 0 && column < board.length){
				int topRow = nrOfRows-1;
				if(board[column][topRow] != VierGewinnt.Token.empty){
					column = -1;
				}
			}
		}
		return column;
	}
	
	public void setToken(VierGewinnt.Token token){
		this.token = token;
	}
	public VierGewinnt.Token getToken(){
		return this.token;
	}
	
	public String getProgrammers(){
		return "CPU Random";
	}
}
