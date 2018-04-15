package cpu;

import viergewinnt.*;

/**mein ansatz: computer sucht in dieser Reihenfolge nach Möglichkeiten:
1. Kann er selbst einen 4er kompletisieren?
2. Kann er einen 4er vom hp verhindern? (bei mehreren, welchen?)
3. Kann er einen 3er des hp verhindern? (nicht unbedingt optimal)
4. Kann er selbst einen 3er erweitern?

5. Random
Für meine eigene Übersicht, habe ich die 2 For-schleifen immer wieder neu geschrieben,
dies ginge natürlich auch noch viel effizienter!

*/




import java.util.Random;


/** A computer player class */
public class ComputerPlayerRulesSimple implements IPlayer{

	private VierGewinnt.Token token;
	
	Random generator = new Random();
        
	public int getNextColumn(VierGewinnt.Token[][] board){

		int nrOfColumns = VierGewinnt.getNrOfColumns(board);
                int nrOfRows = VierGewinnt.getNrOfRows(board);
                               
                int row;
		int pl1=0;
		int pl2=0;
		int column=0;
//              *** Einen 4er bauen ***
//              waagrecht ->
		for(row=0 ; row<nrOfRows ; row++){
			pl2=0;
			for(column=0 ; column<nrOfColumns ; column++){
				if (board[column][row]==token.player2) {pl2++;}
				if (board[column][row]!=token.player2) {pl2=0;}
				if (pl2==3 && column<(nrOfColumns-1) && (board[column+1][row]==token.empty)) {return column+1;}
			}
		}
		
//		waagrecht <-
		for(row=0 ; row<nrOfRows ; row++){
			pl2=0;
			for(column=(nrOfColumns-1) ; column>0 ; column--){
				if (board[column][row]==token.player2) {pl2++;}
				if (board[column][row]!=token.player2) {pl2=0;}
				if (pl2==3 && column<(nrOfColumns-1) && (board[column-1][row]==token.empty)) {return column-1;}
			}
		}

//		senkrecht		
		for (column=0 ; column<nrOfColumns ; column++){
			pl2=0;
			for(row=0 ; row<nrOfRows ; row++){
				if (board[column][row]==token.player2) {pl2++;}
				if (board[column][row]!=token.player2) {pl2=0;}
				if (pl2==3 && row<(nrOfRows-1) && (board[column][row+1]==token.empty)) {return column;}
			}
		}
		
//		diagonal /
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=0 ; column<(nrOfColumns-3) ; column++){
				pl2=0;
				if (board[column][row]==token.player2) pl2++;
				if (board[column+1][row+1]==token.player2) pl2++;
				if (board[column+2][row+2]==token.player2) pl2++;
				if (pl2==3 && (board[column+3][row+2]!=token.empty) && (board[column+3][row+3]==token.empty))
					{column=column+3; return column;} 
			}
		}
		
//		diagonal \
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=3 ; column<nrOfColumns ; column++){
				pl2=0;
				if (board[column][row]==token.player2) pl2++;
				if (board[column-1][row+1]==token.player2) pl2++;
				if (board[column-2][row+2]==token.player2) pl2++;
				if (pl2==3 && (board[column-3][row+2]!=token.empty) && (board[column-3][row+3]==token.empty))
					{column=column-3; return column;} 
			}
		}
				
//einen 4er des hp verhindern - waagrecht ->

		column=0;

		for(row=0 ; row<nrOfRows ; row++){
			pl1=0;
			for(column=0 ; column<nrOfColumns ; column++){
				if (board[column][row]==token.player1) {pl1++;}
				if (board[column][row]!=token.player1) {pl1=0;}
				if (pl1==3 && column<6 && (board[column+1][row]==token.empty)) {return column+1;}
			}
		}
//		waagrecht <-
		for(row=0 ; row<nrOfRows ; row++){
			pl1=0;
			for(column=(nrOfColumns-1) ; column>0 ; column--){
				if (board[column][row]==token.player1) {pl1++;}
				if (board[column][row]!=token.player1) {pl1=0;}
				if (pl1==3 && (board[column-1][row]==token.empty)) {column=column-1 ; return column;}
			}
		}
		
		
//		senkrecht
		for(column=0 ; column<nrOfColumns ; column++){
			pl1=0;
			pl2=0;
			for(row=0 ; row<nrOfRows; row++){
				if (board[column][row]==token.player1) {pl1++;}
				if (board[column][row]!=token.player1) {pl1=0;}
				if (pl1==3 && row<5 && (board[column][row+1]==token.empty)) {return column;}
			}
		}



//		diagonal /
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=0 ; column<(nrOfColumns-3) ; column++){
				pl1=0;
				if (board[column][row]==token.player1) pl1++;
				if (board[column+1][row+1]==token.player1) pl1++;
				if (board[column+2][row+2]==token.player1) pl1++;
				if (pl1==3 && (board[column+3][row+2]!=token.empty) && (board[column+3][row+3]==token.empty))
					{column=column+3; return column;} 
					
			}
		}

//		diagonal \
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=3 ; column<nrOfColumns ; column++){
				pl1=0;
				if (board[column][row]==token.player1) pl1++;
				if (board[column-1][row+1]==token.player1) pl1++;
				if (board[column-2][row+2]==token.player1) pl1++;
				if (pl1==3 && (board[column-3][row+2]!=token.empty) && (board[column-3][row+3]==token.empty))
					{column=column-3; return column;} 
					
			}
		}		
//              random
		column=generator.nextInt(nrOfColumns);	
		return column;
	}
	
	public void setToken(VierGewinnt.Token token){
		this.token = token;
	}
	public VierGewinnt.Token getToken(){
		return this.token;
	}
	
	public String getProgrammers(){
		return "CPU Rules Simple";
	}
}
