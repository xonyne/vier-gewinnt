package cpu;

import viergewinnt.*;
import java.util.Random;
import java.util.Arrays;

/** A computer player class */
public class ComputerPlayerRulesAdvanced implements IPlayer{

	private VierGewinnt.Token token;
	private VierGewinnt.Token optoken;
	private final boolean DEV_MODE = true;
	private int zug=0;
	
        Random generator = new Random();
	
	boolean [] scolumn;
	
	
	public int getNextColumn(VierGewinnt.Token[][] board){
            
        int nrOfColumns = VierGewinnt.getNrOfColumns(board);
        int nrOfRows = VierGewinnt.getNrOfRows(board);
        scolumn = new boolean [nrOfColumns+1];
	
	token = this.getToken();
	if (this.getToken()==token.player1) optoken = token.player2;
	if (this.getToken()==token.player2) optoken = token.player1;
//---------------------------------------------------------------------------------------------------------------		
//Einen 4er bauen - waagrecht ->
		int row;
		int pl1=0;
		int pl2=0;
		int column=0;
		
		
		Arrays.fill(scolumn,false);
		
		//waagrecht ***_
		for(row=0 ; row<nrOfRows ; row++){
			pl2=0;
			for(column=0 ; column<nrOfColumns ; column++){
				if (board[column][row]==token.player2) {pl2++;}
				if (board[column][row]!=token.player2) {pl2=0;}
				if (row==0 && pl2==3 && column<(nrOfColumns-1) && board[column+1][row]==token.empty)
					{zug++ ; if(DEV_MODE) System.out.println("waagrecht 4er gebaut ***_ (Reihe 1)");return column+1; }
				if (row>0 && pl2==3 && column<(nrOfColumns-1) && board[column+1][row]==token.empty && 
					board[column+1][row-1]!=token.empty)
					{zug++ ; if(DEV_MODE) System.out.println("waagrecht 4er gebaut ***_ (Reihe > 1)");return column+1;}
			}
		}
//		waagrecht _***
		for(row=0 ; row<nrOfRows ; row++){
			pl2=0;
			for(column=(nrOfColumns-1) ; column>0 ; column--){
				if (board[column][row]==token.player2) {pl2++;}
				if (board[column][row]!=token.player2) {pl2=0;}
				if (row==0 && pl2==3 && (board[column-1][row]==token.empty))
					{zug++ ; column=column-1 ; if(DEV_MODE) System.out.println("waagrecht 4er gebaut _*** (Reihe = 1)");return column;}
				if (row>0 && pl2==3 && (board[column-1][row]==token.empty && 
					board[column-1][row-1]!=token.empty))
					{zug++ ; column=column-1 ;if(DEV_MODE) System.out.println("waagrecht 4er gebaut _*** (Reihe > 1)");return column;}
			}
		}
//		waagrecht mit Lücke **-*		
		for(row=0 ; row<nrOfRows ; row++){
			for(column=0 ; column<(nrOfColumns-3) ; column++){
				pl2=0;
				if (board[column][row]==token) {pl2++;}
				if (row==0 && board[column+2][row]==token.empty) {pl2++;}
				if (row>0 && board[column+2][row]==token.empty && board[column+2][row-1]!=token.empty)
					{pl2++;}
				if (board[column+3][row]==token) {pl2++;}
				if (pl2==4) {zug++ ; if(DEV_MODE) System.out.println("waagrecht 4er gebaut **_*");return column+2;}
			}
		}
//		waagrecht mit Lücke *-**
		for(row=0 ; row<nrOfRows ; row++){
			for(column=0 ; column<(nrOfColumns-3) ; column++){
				pl2=0;
				if (board[column][row]==token) {pl2++;}
				if (row>0 && board[column+1][row]==token.empty && board[column+2][row-1]!=token.empty)
					{pl2++;}
				if (board[column+2][row]==token) {pl2++;}
				if (board[column+3][row]==token) {pl2++;}								
				if (pl2==4) {zug++ ;  if(DEV_MODE) System.out.println("waagrecht 4er gebaut *_**");return column+1;}
			}
		}
		//diagonal / mit Lücke **-*		
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=0 ; column<(nrOfColumns-3) ; column++){
				pl2=0;
				if (board[column][row]==token) {pl2++;}
				if (board[column+1][row+1]==token) {pl2++;}
				if (board[column+2][row+2]==token.empty && board[column+2][row+1]!=token.empty)
					{pl2++;}
				if (board[column+3][row+3]==token) {pl2++;}				
				if (pl2==4) {zug++ ;  if(DEV_MODE) System.out.println("diagonal / 4er gebaut **_*");return column+2;}
			}
		}
//		diagonal / mit Lücke *-**		
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=0 ; column<(nrOfColumns-3) ; column++){
				pl2=0;
				if (board[column][row]==token) {pl2++;}
				if (board[column+1][row+1]==token.empty && board[column+1][row]!=token.empty)
					{pl2++;}
				if (board[column+2][row+2]==token) {pl2++;}
				if (board[column+3][row+3]==token) {pl2++;}
				if (pl2==4) {zug++ ;  if(DEV_MODE) System.out.println("diagonal / 4er gebaut *_**");return column+1;}		
			}
		}
//		diagonal \ mit Lücke **-*		
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=3 ; column<(nrOfColumns-3) ; column++){
				pl2=0;
				if (board[column][row]==token) {pl2++;}
				if (board[column-1][row+1]==token) {pl2++;}
				if (board[column-2][row+2]==token.empty && board[column-2][row+1]!=token.empty)
					{pl2++;}
				if (board[column-3][row+3]==token) {pl2++;}				
				if (pl2==4) {zug++ ; if(DEV_MODE) System.out.println("diagonal \\ 4er gebaut **_*");return column-2;}
			}
		}
//		diagonal \ mit Lücke *-**		
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=3 ; column<(nrOfColumns-3) ; column++){
				pl2=0;
				if (board[column][row]==token) {pl2++;}
				if (board[column-1][row+1]==token.empty && board[column-1][row]!=token.empty)
					{pl2++;}
				if (board[column-2][row+2]==token) {pl2++;}
				if (board[column-3][row+3]==token) {pl2++;}
				if (pl2==4) {zug++ ; if(DEV_MODE) System.out.println("diagonal \\ 4er gebaut *_**");return column-1;}		
			}
		}
//		senkrecht		
		for (column=0 ; column<nrOfColumns; column++){
			pl2=0;
			for(row=0 ; row<nrOfRows ; row++){
				if (board[column][row]==token) {pl2++;}
				if (board[column][row]!=token) {pl2=0;}
				if (pl2==3 && row<5 && (board[column][row+1]==token.empty))
					{zug++ ; if(DEV_MODE) System.out.println("senkrecht 4er gebaut");return column;}
			}
		}
//		diagonal /
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=0 ; column<(nrOfColumns-3) ; column++){
				pl2=0;
				if (board[column][row]==token) pl2++;
				if (board[column+1][row+1]==token) pl2++;
				if (board[column+2][row+2]==token) pl2++;
				if (pl2==3 && (board[column+3][row+2]!=token.empty) && 
					(board[column+3][row+3]==token.empty))
					{zug++ ; column=column+3; if(DEV_MODE) System.out.println("diagonal / 4er gebaut ***_");return column;} 
			}
		}
//		diagonal \
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=3 ; column<nrOfColumns ; column++){
				pl2=0;
				if (board[column][row]==token) pl2++;
				if (board[column-1][row+1]==token) pl2++;
				if (board[column-2][row+2]==token) pl2++;
				if (pl2==3 && (board[column-3][row+2]!=token.empty) && 
					(board[column-3][row+3]==token.empty))
					{zug++ ; column=column-3; if(DEV_MODE) System.out.println("diagonal \\ 4er gebaut _***");return column;} 
			}
		}
		
//		diagonal _/
		for(row=1 ; row<(nrOfRows-2) ; row++){
			for(column=1 ; column<(nrOfColumns-2) ; column++){
				pl1=0;
				if (board[column][row]==token) pl1++;
				if (board[column+1][row+1]==token) pl1++;
				if (board[column+2][row+2]==token) pl1++;
				if (row==1 && pl1==3 && (board[column-1][row-1]==token.empty))
					{zug++ ; if(DEV_MODE) System.out.println("diagonal /  4er gebaut _***");return column-1;}
				if (row>1 && pl1==3 && (board[column-1][row-2]!=token.empty) && 
					(board[column-1][row-1]==token.empty))
					{zug++ ; if(DEV_MODE) System.out.println("diagonal /  4er gebaut _***");return column-1;} 
			}
		}
//		diagonal \_
		for(row=1 ; row<(nrOfRows-2) ; row++){
			for(column=2 ; column<(nrOfColumns-1) ; column++){
				pl1=0;
				if (board[column][row]==token) pl1++;
				if (board[column-1][row+1]==token) pl1++;
				if (board[column-2][row+2]==token) pl1++;
				if (row==1 && pl1==3 && (board[column+1][row-1]==token.empty))
					{zug++ ; if(DEV_MODE) System.out.println("diagonal 4er gebaut \\ ***_");return column+1;}
				if (row>1 && pl1==3 && (board[column+1][row-2]!=token.empty) &&
					(board[column+1][row-1]==token.empty))
					{zug++ ; if(DEV_MODE) System.out.println("diagonal 4er gebaut \\ ***_");return column+1;} 	
			}
		}		

//-------------------------------------------------------------------------------------------------	
//einen 4er des hp verhindern - waagrecht ->
		column=0;
		for(row=0 ; row<nrOfRows ; row++){
			pl1=0;
			for(column=0 ; column<nrOfColumns ; column++){
				if (board[column][row]==token.player1) {pl1++;}
				if (board[column][row]!=token.player1) {pl1=0;}
				if (row==0 && pl1==3 && column<6 && board[column+1][row]==token.empty)
					{zug++ ; if(DEV_MODE) System.out.println("waagrecht 4er verhindert ***_ (Reihe 1)");return column+1;}
				if (row>0 && pl1==3 && column<6 && board[column+1][row]==token.empty && 
					board[column+1][row-1]!=token.empty)
					{zug++ ; if(DEV_MODE) System.out.println("waagrecht 4er verhindert ***_ (Reihe > 1)");return column+1;}
				if (row>1 && pl1==3 && column<6 && board[column+1][row]==token.empty && 
					board[column+1][row-1]==token.empty && board[column+1][row-2]!=token.empty)
					{if(DEV_MODE) System.out.println("waagrecht Lücke darf nicht gefüllt werden") ; scolumn[column+1]=true;}
			}
		}
//		waagrecht <-
		for(row=0 ; row<nrOfRows ; row++){
			pl1=0;
			for(column=(nrOfColumns-1) ; column>0 ; column--){
				if (board[column][row]==token.player1) {pl1++;}
				if (board[column][row]!=token.player1) {pl1=0;}
				if (row==0 && pl1==3 && (board[column-1][row]==token.empty))
					{zug++ ; column=column-1 ; if(DEV_MODE) System.out.println("waagrecht  4er verhindert ***_ (Reihe 1)");return column;}
				if (row>0 && pl1==3 && (board[column-1][row]==token.empty && 
					board[column-1][row-1]!=token.empty))
					{zug++ ; column=column-1 ; if(DEV_MODE) System.out.println("waagrecht 4er verhindert _*** (Reihe > 1)");return column;}
				if (row>1 && pl1==3 && board[column-1][row]==token.empty && 
					board[column-1][row-1]==token.empty && board[column-1][row-2]!=token.empty)
					{if(DEV_MODE) System.out.println("waagrecht Lücke darf nicht gefüllt werden") ; scolumn[column-1]=true;}
			}
		}
//		waagrecht mit Lücke **-*		
		for(row=0 ; row<nrOfRows ; row++){
			for(column=0 ; column<(nrOfColumns-3) ; column++){
				pl1=0;
				if (board[column][row]==optoken) {pl1++;}
				if (board[column+1][row]==optoken) {pl1++;}
				if (row==0 && board[column+2][row]==token.empty) {pl1++;}
				if (row>0 && board[column+2][row]==token.empty && board[column+2][row-1]!=token.empty)
					{pl1++;}
				if (board[column+3][row]==optoken) {pl1++;}
								
				if (pl1==4) {zug++ ; if(DEV_MODE) System.out.println("waagrecht 4er verhindert **_*");return column+2;}
				if (row==1 && pl1==4 && board[column+2][row-1]==token.empty) 
					{scolumn[column+2]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}
				if (row>1 && pl1==4 && board[column+2][row-1]==token.empty && board[column+2][row-2]!=token.empty) 
					{scolumn[column+2]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}
			}
		}
//		waagrecht mit Lücke *-**		
		for(row=0 ; row<nrOfRows ; row++){
			for(column=0 ; column<(nrOfColumns-3) ; column++){
				pl1=0;
				if (board[column][row]==optoken) {pl1++;}
				if (row==0 && board[column+1][row]==token.empty) {pl1++;}
				if (row>0 && board[column+1][row]==token.empty && board[column+1][row-1]!=token.empty)
					{pl1++;}
				if (board[column+2][row]==optoken) {pl1++;}
				if (board[column+3][row]==optoken) {pl1++;}
				if (pl1==4) {zug++ ; if(DEV_MODE) System.out.println("waagrecht 4er verhindert *_**");return column+1;}		
				if (row==1 && pl1==4 && board[column+1][row-1]==token.empty) 
					{scolumn[column+1]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}
				if (row>1 && pl1==4 && board[column+1][row-1]==token.empty && board[column+1][row-2]!=token.empty) 
					{scolumn[column+1]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}			
			
			}
		}
//		diagonal / mit Lücke **-*		
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=0 ; column<(nrOfColumns-3) ; column++){
				pl1=0;
				if (board[column][row]==optoken) {pl1++;}
				if (board[column+1][row+1]==optoken) {pl1++;}
				if (board[column+2][row+2]==token.empty && board[column+2][row+1]!=token.empty)
					{pl1++;}
				if (board[column+3][row+3]==optoken) {pl1++;}				
				if (pl1==4) {zug++ ; if(DEV_MODE) System.out.println("diagonal /  4er verhindert **_*");return column+2;}
				if (pl1==4 && board[column+2][row+1]==token.empty && board[column+2][row]!=token.empty)
					{scolumn[column+2]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}
			}
		}
//		diagonal / mit Lücke *-**		
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=0 ; column<(nrOfColumns-3) ; column++){
				pl1=0;
				if (board[column][row]==optoken) {pl1++;}
				if (board[column+1][row+1]==token.empty && board[column+1][row]!=token.empty)
					{pl1++;}
				if (board[column+2][row+2]==optoken) {pl1++;}
				if (board[column+3][row+3]==optoken) {pl1++;}
				if (pl1==4) {zug++ ; if(DEV_MODE) System.out.println("diagonal /  4er verhindert *_**");return column+1;}
				
				if (row==1 && pl1==4 && board[column+1][row]==token.empty)
					{scolumn[column+1]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}
				if (row>1 && pl1==4 && board[column+1][row]==token.empty && board[column+1][row-1]!=token.empty)
					{scolumn[column+1]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}
			}
		}
//		diagonal \ mit Lücke **-*		
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=3 ; column<nrOfColumns ; column++){
				pl1=0;
				if (board[column][row]==optoken) {pl1++;}
				if (board[column-1][row+1]==optoken) {pl1++;}
				if (board[column-2][row+2]==token.empty && board[column-2][row+1]!=token.empty)
					{pl1++;}
				if (board[column-3][row+3]==optoken) {pl1++;}				
				if (pl1==4) {zug++ ; if(DEV_MODE) System.out.println("diagonal \\  4er verhindert **_*");return column-2;}
				if (pl1==4 && board[column-2][row+1]==token.empty && board[column-2][row]!=token.empty)
					{scolumn[column-2]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}
				
			}
		}
//		diagonal \ mit Lücke *-**		
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=3 ; column<nrOfColumns ; column++){
				pl1=0;
				if (board[column][row]==optoken) {pl1++;}
				if (board[column-1][row+1]==token.empty && board[column-1][row]!=token.empty)
					{pl1++;}
				if (board[column-2][row+2]==optoken) {pl1++;}
				if (board[column-3][row+3]==optoken) {pl1++;}
				if (pl1==4) {zug++ ; if(DEV_MODE) System.out.println("diagonal 4er \\ verhindert *_**");return column-1;}
				
				if (row==1 && pl1==4 && board[column-1][row]==token.empty)
					{scolumn[column-1]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}
				if (row>1 && pl1==4 && board[column-1][row]==token.empty && board[column-1][row-1]!=token.empty)
					{scolumn[column-1]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}
			}
		}		
		
		
//		senkrecht
		for(column=0 ; column<nrOfColumns ; column++){
			pl1=0;
			for(row=0 ; row<nrOfRows ; row++){
				if (board[column][row]==optoken) {pl1++;}
				if (board[column][row]!=optoken) {pl1=0;}
				if (pl1==3 && row<5 && (board[column][row+1]==token.empty)) {zug++ ; if(DEV_MODE) System.out.println("senkrecht 4er verhindert");return column;}
			}
		}
//		diagonal /_
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=0 ; column<(nrOfColumns-3) ; column++){
				pl1=0;
				if (board[column][row]==optoken) pl1++;
				if (board[column+1][row+1]==optoken) pl1++;
				if (board[column+2][row+2]==optoken) pl1++;
				if (pl1==3 && (board[column+3][row+2]!=token.empty) && 
					(board[column+3][row+3]==token.empty))
					{zug++ ; column=column+3; if(DEV_MODE) System.out.println("diagonal /  4er verhindert ***_");return column;} 		
				if (pl1==3 && board[column+3][row+3]==token.empty && board[column+3][row+2]==token.empty && 
					board[column+3][row+1]!=token.empty)
					{scolumn[column+3]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}
			
			}
		}
//		diagonal _\
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=3 ; column<nrOfColumns ; column++){
				pl1=0;
				if (board[column][row]==optoken) pl1++;
				if (board[column-1][row+1]==optoken) pl1++;
				if (board[column-2][row+2]==optoken) pl1++;
				if (pl1==3 && (board[column-3][row+2]!=token.empty) &&
					(board[column-3][row+3]==token.empty))
					{zug++ ; column=column-3; if(DEV_MODE) System.out.println("diagonal 4er verhindert \\ _***");return column;} 			
				if (pl1==3 && board[column-3][row+3]==token.empty && board[column-3][row+2]==token.empty && 
					board[column-3][row+1]!=token.empty)
					{scolumn[column-3]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}
			
			}
		}
		
//		diagonal _/
		for(row=1 ; row<(nrOfRows-2) ; row++){
			for(column=1 ; column<(nrOfColumns-2) ; column++){
				pl1=0;
				if (board[column][row]==optoken) pl1++;
				if (board[column+1][row+1]==optoken) pl1++;
				if (board[column+2][row+2]==optoken) pl1++;
				if (row==1 && pl1==3 && (board[column-1][row-1]==token.empty))
					{zug++ ; if(DEV_MODE) System.out.println("diagonal /  4er verhindert _***");return column-1;}
				if (row>1 && pl1==3 && (board[column-1][row-2]!=token.empty) && 
					(board[column-1][row-1]==token.empty))
					{zug++ ; if(DEV_MODE) System.out.println("diagonal /  4er verhindert _***");return column-1;} 
				if (row==2 && pl1==3 && board[column-1][row-1]==token.empty && board[column-1][row-2]==token.empty)
					{scolumn[column-1]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}	
				
				if (row>2 && pl1==3 && board[column-1][row-1]==token.empty && board[column-1][row-2]==token.empty && 
					board[column-1][row-3]!=token.empty)
					{scolumn[column-1]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}
			}
		}
//		diagonal \_
		for(row=1 ; row<(nrOfRows-2) ; row++){
			for(column=2 ; column<(nrOfColumns-1) ; column++){
				pl1=0;
				if (board[column][row]==optoken) pl1++;
				if (board[column-1][row+1]==optoken) pl1++;
				if (board[column-2][row+2]==optoken) pl1++;
				if (row==1 && pl1==3 && (board[column+1][row-1]==token.empty))
					{zug++ ; if(DEV_MODE) System.out.println("diagonal 4er verhindert \\ ***_");return column+1;}
				if (row>1 && pl1==3 && (board[column+1][row-2]!=token.empty) &&
					(board[column+1][row-1]==token.empty))
					{zug++ ; if(DEV_MODE) System.out.println("diagonal 4er verhindert \\ ***_");return column+1;}
	
				if (row==2 && pl1==3 && board[column+1][row-1]==token.empty && board[column+1][row-2]==token.empty)
					{scolumn[column+1]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}
				if (row>2 && pl1==3 && board[column+1][row-1]==token.empty && board[column+1][row-2]==token.empty && 
					board[column+1][row-3]!=token.empty)
					{scolumn[column+1]=true ; if(DEV_MODE) System.out.println("lücke darf nicht gefüllt werden");}
			}
		}
//---------------------------------------------------------------------------------------------------------		
//einen 3er des hp verhindern - waagrecht ->
		column=0;
		for(row=0 ; row<nrOfRows ; row++){
			pl1=0;
			for(column=0 ; column<nrOfColumns ; column++){
				if (board[column][row]==optoken) {pl1++;}
				if (board[column][row]!=optoken) {pl1=0;}
				if (scolumn[column+1]!=true && row==0 && pl1==2 && column<(nrOfColumns-1) && (board[column+1][row]==token.empty))
					{zug++ ; if(DEV_MODE) System.out.println("waagrecht 3er verhindert **_");return column+1;}
				if (scolumn[column+1]!=true && row>0 && pl1==2 && column<(nrOfColumns-1) && (board[column+1][row]==token.empty) && board[column+1][row-1]!=token.empty)
					{zug++ ; if(DEV_MODE) System.out.println("waagrecht 3er verhindert **_");return column+1;}
			}
		}
//		waagrecht <-
		for(row=0 ; row<nrOfRows ; row++){
			pl1=0;
			for(column=(nrOfColumns-1) ; column>0 ; column--){
				if (board[column][row]==optoken) {pl1++;}
				if (board[column][row]!=optoken) {pl1=0;}
				if (scolumn[column-1]!=true && row==0 && pl1==2 && (board[column-1][row]==token.empty)) 
					{zug++ ; column=column-1 ; if(DEV_MODE) System.out.println("waagrecht 3er verhindert _**");return column;}
				if (scolumn[column-1]!=true &&row>0 && pl1==2 && (board[column-1][row]==token.empty) && board[column-1][row-1]!=token.empty) 
					{zug++ ; column=column-1 ; if(DEV_MODE) System.out.println("waagrecht 3er verhindert _**");return column;}
			}
		}
//		senkrecht
		for(column=0 ; column<nrOfColumns ; column++){
			pl1=0;
			pl2=0;
			for(row=0 ; row<nrOfRows ; row++){
				if (board[column][row]==optoken) {pl1++;}
				if (board[column][row]!=optoken) {pl1=0;}
				if (scolumn[column]!=true && pl1==2 && row<5 && (board[column][row+1]==token.empty)) 
					{zug++ ; if(DEV_MODE) System.out.println("senkrecht 3er verhindert");return column;}
			}
		}
//		diagonal /
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=0 ; column<(nrOfColumns-3) ; column++){
				pl1=0;
				if (board[column][row]==optoken) pl1++;
				if (board[column+1][row+1]==optoken) pl1++;
				if (board[column+2][row+2]==optoken) pl1++;
				if (scolumn[column+2]!=true && pl1==2 && (board[column+2][row+1]!=token.empty) && 
					(board[column+2][row+2]==token.empty))
					{zug++ ; column=column+2; if(DEV_MODE) System.out.println("diagonal / 3er verhindert ***_");return column;} 
					
			}
		}
//		diagonal \
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=3 ; column<nrOfColumns ; column++){
				pl1=0;
				if (board[column][row]==optoken) pl1++;
				if (board[column-1][row+1]==optoken) pl1++;
				if (board[column-2][row+2]==optoken) pl1++;
				if (scolumn[column-2]!=true && pl1==2 && (board[column-2][row+1]!=token.empty) && 
					(board[column-2][row+2]==token.empty))
					{zug++ ; column=column-2; if(DEV_MODE) System.out.println("diagonal \\ 3er verhindert _***"); return column;} 
					
			}
		}
//-------------------------------------------------------------------------------------------------------------------
// Einen 3er bauen - waagrecht ->
		for(row=0 ; row<nrOfRows ; row++){
			pl2=0;
			for(column=0 ; column<nrOfColumns ; column++){
				if (board[column][row]==token) {pl2++;}
				if (board[column][row]!=token) {pl2=0;}
				if (scolumn[column+1]!=true && row==0 && pl2==2 && column<6 && board[column+1][row]==token.empty) 
					{zug++ ; if(DEV_MODE) System.out.println("waagrecht 3er gebaut  **_");return column+1;}
				if (scolumn[column+1]!=true && row>0 && pl2==2 && column<6 && board[column+1][row]==token.empty && board[column+1][row-1]!=token.empty) 
					{zug++ ; if(DEV_MODE) System.out.println("waagrecht 3er gebaut  **_");return column+1;}
			}
		}
//		waagrecht <-
		for(row=0 ; row<nrOfRows ; row++){
			pl2=0;
			for(column=(nrOfColumns-1) ; column>0 ; column--){
				if (board[column][row]==token) {pl2++;}
				if (board[column][row]!=token) {pl2=0;}
				if (scolumn[column-1]!=true && row==0 && pl2==2 && column<6 && (board[column-1][row]==token.empty)) 
					{zug++ ; if(DEV_MODE) System.out.println("waagrecht 3er gebaut  _**");return column-1;}
				if (scolumn[column-1]!=true && row>0 && pl2==2 && column<6 && (board[column-1][row]==token.empty) && board[column-1][row-1]!=token.empty) 
					{zug++ ; if(DEV_MODE) System.out.println("waagrecht 3er gebaut  _**");return column-1;}
			}
		}
//		senkrecht		
		for (column=0 ; column<nrOfColumns ; column++){
			pl2=0;
			for(row=0 ; row<nrOfRows ; row++){
				if (board[column][row]==token) {pl2++;}
				if (board[column][row]!=token) {pl2=0;}
				if (scolumn[column]!=true && pl2==2 && row<5 && (board[column][row+1]==token.empty)) 
					{zug++ ; if(DEV_MODE) System.out.println("senkrecht 3er gebaut");return column;}
			}
		}
//		diagonal /
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=0 ; column<(nrOfColumns-3) ; column++){
				pl2=0;
				if (board[column][row]==token) pl2++;
				if (board[column+1][row+1]==token) pl2++;
				if (board[column+2][row+2]==token) pl2++;
				if (scolumn[column+2]!=true && pl2==2 && (board[column+2][row+1]!=token.empty) && 
					(board[column+2][row+2]==token.empty))
					{zug++ ; column=column+2; if(DEV_MODE) System.out.println("diagonal / 3er gebaut  **_");return column;} 
			}
		}
//		diagonal \
		for(row=0 ; row<(nrOfRows-3) ; row++){
			for(column=3 ; column<nrOfColumns ; column++){
				pl2=0;
				if (board[column][row]==token) pl2++;
				if (board[column-1][row+1]==token) pl2++;
				if (board[column-2][row+2]==token) pl2++;
				if (scolumn[column-2]!=true && pl2==3 && (board[column-2][row+1]!=token.empty) && 
					(board[column-2][row+2]==token.empty))
					{zug++ ; column=column-2; if(DEV_MODE) System.out.println("diagonal \\ 3er gebaut  _**");return column;} 
			}
		}
//---------------------------------------------------------------------------------------------------------------------------
//		Random
//		Die ersten 20 Züge sollen tendentiell in die Mitte gesetzt werden
                int min;
                int max;
                if (nrOfColumns % 2 == 0) {
                    min = nrOfColumns / 2;
                    max = (nrOfColumns / 2) + 1;
                } else {
                    min = (nrOfColumns -1) / 2;
                    max = ((nrOfColumns -1) / 2)+2;
                }
		if (zug<20) {column=generator.nextInt((max - min) + 1) + min;}
		else column=generator.nextInt(nrOfColumns);
		int stop=0;
		while (board[column][nrOfRows-1]!=token.empty || scolumn[column]==true){
			column=generator.nextInt(nrOfColumns);
			stop++;
			if (stop==20) {Arrays.fill(scolumn,false);}
		}
		System.out.println("random gesetzt");
		stop=0;
		return column;
	}
	
	public void setToken(VierGewinnt.Token token){
		this.token = token;
	}
	public VierGewinnt.Token getToken(){
		return this.token;
	}
	
	public String getProgrammers(){
		return "CPU Rules Advanced";
	}
}
