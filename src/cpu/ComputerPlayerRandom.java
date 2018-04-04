package cpu;

import java.util.Random;
import viergewinnt.IPlayer;
import viergewinnt.VierGewinnt;

/** A random computer player class */
public class ComputerPlayerRandom implements IPlayer{
	private VierGewinnt.Token token;
	
	public int getNextColumn(VierGewinnt.Token[][] board){
		Random generator = new Random();
		int column = -1;
		
		while(column < 0 || column > board.length - 1){
			
			column=generator.nextInt(7);
			if(column >= 0 && column < board.length){
				int topRow = board[0].length-1;
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
		return "Random";
	}
}
