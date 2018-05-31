package cpu;

import java.util.Random;
import viergewinnt.IPlayer;
import viergewinnt.VierGewinnt;

/** A random computer player class */
public class ComputerPlayerCheapRushTrick implements IPlayer{
	private VierGewinnt.Token token;
        private int zug=0;
	
	public int getNextColumn(VierGewinnt.Token[][] board){
		int nrOfColumns = VierGewinnt.getNrOfColumns(board);
                int nrOfRows = VierGewinnt.getNrOfRows(board);
                
                Random generator = new Random();
		
                int column = -1;
                int[] cheapRushTrickArray = {3,4,5,2};
                
                if (zug<4){ 
                    column = cheapRushTrickArray[zug];
                    zug++;
                } else {
                    while(column < 0 || column > board.length - 1){

                            column=generator.nextInt(nrOfColumns);
                            if(column >= 0 && column < board.length){
                                    int topRow = nrOfRows-1;
                                    if(board[column][topRow] != VierGewinnt.Token.empty){
                                            column = -1;
                                    }
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
		return "CPU Cheap Rush Trick";
	}
}
