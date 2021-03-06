package cpu;

import java.util.concurrent.TimeoutException;
import viergewinnt.Connect4Move;
import viergewinnt.IPlayer;
import viergewinnt.VierGewinnt;

/**
 * A computer player class
 */
public class ComputerPlayerSearchTree implements IPlayer {

    private VierGewinnt.Token token;
    private VierGewinnt.Token optoken;
    private int DEPTH = 6;
    private static final boolean ITERATIVE_DEEPENING = false;
    private static final int TIME_OUT_MS = 2000;
    private static final int MAX_DEPTH = 10;
    private static long startTime;

    private int playerToMoveNum; // 0 or 1 for which player to go
    private int latestRow = -1; // latest row added to by makeMove method
    private int latestCol = -1; // latest column added to by makeMove method
    private int movesDone; // number of moves made
    private int evalValue; // evaluation of unblocked four-in-row for both players

    public static final int[] HOW_GOOD = {0, 2, 10 ^ 2, 10 ^ 3, 10 ^ 8}; // index is # of unblocked four-in-row potentials

    private static final int[] MOVES_BY_COL = {3, 4, 2, 5, 1, 6, 0};

    public int getNextColumn(VierGewinnt.Token[][] board) {
        int move = 0;
        VierGewinnt.Token[][] boardCopy = VierGewinnt.copyBoard(board);
        if (ITERATIVE_DEEPENING) {
            move = getMoveIterativeDepth(boardCopy);
        } else {
            try {
                move = getMoveFixedDepth(boardCopy);
            } catch (TimeoutException ignored) {
            }
        }
        return move;
    }

    private int getMoveIterativeDepth(VierGewinnt.Token[][] board) {
        int bestMoveDepth = 0;
        Connect4Move bestMove = new Connect4Move(-Integer.MAX_VALUE, -10);
        int depth = 0;
        startTime = System.currentTimeMillis();
        try {
            while (depth < MAX_DEPTH) {      
                // pick the move
                // start alpha-beta with neg and pos infinities
                bestMove = pickMove(board, depth, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                bestMoveDepth = depth;
                depth++;
            }
        } catch (TimeoutException ignored) {
            System.out.println("---- Best move found at depth :" + bestMoveDepth + " ---");
            System.out.println("---- Best move column :" + (bestMove.move + 1) + " ---");
            System.out.println("---- Best move value :" + bestMove.value + " ---");
        }
        return bestMove.move;
    }

    private int getMoveFixedDepth(VierGewinnt.Token[][] board) throws TimeoutException {
        int depth = DEPTH;
        // pick the move
        // start alpha-beta with neg and pos infinities
        Connect4Move chosenMoveObj = pickMove(board, depth, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        int chosenMove = chosenMoveObj.move;
        return chosenMove;
    }

    /**
     * Uses game tree search with alpha-beta pruning to pick player's move low
     * and high define the current range for the best move
     *
     * @param state the current state of the game
     * @param depth the number of moves to look ahead in game tree search
     * @param low a value that the player can achieve by some other move
     * @param high a value that the opponent can force by a different line of
     * play
     *
     * @return the move chosen
     */
    private Connect4Move pickMove(VierGewinnt.Token[][] board, int depth, int low, int high) throws TimeoutException {
        if (ITERATIVE_DEEPENING && (System.currentTimeMillis() - startTime > TIME_OUT_MS)) {
            throw new TimeoutException();
        }

        Connect4Move[] movesArray; // order of moves

        // grab the available moves, sorted by value
        movesArray = checkMoves(board);

        // dummy move that will be replaced with evaluation
        Connect4Move bestMove = new Connect4Move(-Integer.MAX_VALUE, -10);

        // Use alpha-beta pruning to pick the move
        for (int i = 0; i < 7 && bestMove.value < high; i++) {
            // grab the move from list
            int column = movesArray[i].move;

            if (VierGewinnt.isValidMove(column, board)) {
                Connect4Move currentMove;

                // grab value of current position to restore later
                int localEvalValue = this.evalValue;

                makeMove(column, board);

                if (VierGewinnt.isGameOver(board)) {
                    // Is game over because board is full?
                    if (VierGewinnt.isBoardFull(board)) {
                        currentMove = new Connect4Move(0, column); // assign value of 0
                    }

                    // if it's comp's turn, then this must be a win scenario
                    currentMove = new Connect4Move(HOW_GOOD[4], column);

                } // keep going if depth available
                else if (depth >= 1) {
                    // Switch player perspective
                    // Reduce depth by 1
                    currentMove = pickMove(board, depth - 1, -high, -low);

                    // transfer values back while changing perspective
                    currentMove.value = (currentMove.value * -1);
                    currentMove.move = column;

                } else {
                    currentMove = new Connect4Move(evaluate(board), column);
                    
                }

                // Is the current move better than what we've found so far?
                if (currentMove.value > bestMove.value) {
                    bestMove = currentMove; // replace
                    //System.out.println("Best move column: #" + (bestMove.move + 1) + ", value: " + bestMove.value);
                    low = Math.max(bestMove.value, low); // update the achievable lower bound value
                }
                //System.out.println("column: " + currentMove.move + ", " + currentMove.value);
                // undo move before trying next move
                undoMove(column, localEvalValue, board);
                
               // System.out.println("Current move column: #" + (currentMove.move + 1) + ", value: " + currentMove.value);

            }
            
        }
        
        return bestMove;
    }

    /**
     * Check the move list for their associated values Then sort them by value
     *
     * @param state the current state of the game
     * @return an array of moves sorted by their values
     */
    private Connect4Move[] checkMoves(VierGewinnt.Token[][] board) {
        int stateEval; // evaluation of current state based on unblocked 4 in rows
        int nrOfColumns = VierGewinnt.getNrOfColumns(board);
        Connect4Move[] movesArray = new Connect4Move[nrOfColumns];

        stateEval = this.evalValue;

        // go through each column in move list
        for (int i = 0; i < nrOfColumns; i++) {
            int theMove = MOVES_BY_COL[i];

            movesArray[i] = new Connect4Move(-Integer.MAX_VALUE, theMove);
            if (VierGewinnt.isValidMove(theMove, board)) {
                // try the move
                makeMove(theMove, board);

                // now evaluate the new state and store value to check against later
                movesArray[i].value = this.evalValue;

                // undo the state before checking again
                undoMove(theMove, stateEval, board);
            }
        }

        // sort the move lists by values
        for (int i = 1; i < nrOfColumns; i++) {
            for (int compare = i; (compare >= 1 && movesArray[compare].value
                    > movesArray[compare - 1].value);
                    compare--) {
                // placeholder to prevent clobbering
                Connect4Move placeholder = movesArray[compare];
                movesArray[compare] = movesArray[compare - 1];
                movesArray[compare - 1] = placeholder;
            }

        }

        // new set of moves with updated values
        return movesArray;
    }

    /**
     * Undo the move to avoid creating a new state each time
     *
     * @param column column to undo
     * @param stateEval static evaluation at that time
     */
    public void undoMove(int column, int stateEval, VierGewinnt.Token[][] board) {
        int row = VierGewinnt.findTop(column, board);

        // change back to empty
        board[column][row] = VierGewinnt.Token.empty;

        // change other parameters to original
        playerToMoveNum = 1 - playerToMoveNum;

        evalValue = stateEval;
        movesDone--;
    }

    /**
     * Make a move, dropping a checker in the given column
     *
     * @param col the column to get the new checker
     * @param board
     */
    public void makeMove(int col, VierGewinnt.Token[][] board) {
        // first check if the move is valid
        if (VierGewinnt.isValidMove(col, board)) {
            int openRow = VierGewinnt.findOpenRow(col, board);

            // Switch player
            playerToMoveNum = 1 - playerToMoveNum;

            // Switch evaluation for player and computer
            evalValue = -1 * evalValue;

            // Evaluation steps
            evalValue = evalValue - evalAdjust(openRow, col, board); // adjust the evaluation for the move
            board[col][openRow] = getActivePlayerToken(board); // add the checker
            evalValue = evalValue + evalAdjust(openRow, col, board); // reevaluate with new piece in place

            // Increment moves done
            movesDone++;

            // Update latest row/cols
            latestRow = openRow;
            latestCol = col;
        } else {
            // because it was not a valid move
            throw new IllegalStateException("Column is full!");
        }
    }

    /**
     * Calibrates the position after a move is made (called with makeMove)
     *
     * @param openRow the row in which the move will be made (the first open row
     * in a col)
     * @param column the column in which the move was made
     * @return a new evaluation value
     */
    private int evalAdjust(int openRow, int column, VierGewinnt.Token[][] board) {
        // declare offsets for position evaluation
        int leftOffset, rightOffset, leftBound, rightBound;

        VierGewinnt.Token mainPlayer = getActivePlayerToken(board);
        VierGewinnt.Token opponent = getInactivePlayerToken(board);

        leftBound = Math.max(column - 3, 0);
        rightBound = Math.min(6, column + 3);

        // evaluate horizontal possibilities
        int horizValue = evalPossibilities(mainPlayer, opponent, leftBound, rightBound,
                openRow, 0, board);

        // declare offset values for the diagonal
        leftOffset = Math.min(Math.min(openRow, column), 3);
        rightOffset = Math.min(Math.min(5 - openRow, 6 - column), 3);
        int offsetOpenRow = openRow - leftOffset;
        int offsetRightColumn = column + rightOffset;
        int offsetLeftColumn = column - leftOffset;
        int diagonalDelta = 1;

        // evaluate diagonal 1
        int diagValueOne = evalPossibilities(mainPlayer, opponent, offsetLeftColumn, offsetRightColumn,
                offsetOpenRow, diagonalDelta, board);

        // change offset values for next diagonal
        leftOffset = Math.min(Math.min(5 - openRow, column), 3);
        rightOffset = Math.min(Math.min(openRow, 6 - column), 3);

        offsetOpenRow = openRow + leftOffset;
        offsetRightColumn = column + rightOffset;
        offsetLeftColumn = column - leftOffset;
        diagonalDelta = -1;

        // evaluate diagonal 2 (opp direction)
        int diagValueTwo = evalPossibilities(mainPlayer, opponent, offsetLeftColumn,
                offsetRightColumn, offsetOpenRow, diagonalDelta, board);

        // evaluate vertical Connect 4 possibilities
        int verticalValue = connect4Verticals(mainPlayer, opponent, openRow, column, board);

        // now return the total value of horizontal, vertical and diagonals
        int sum = verticalValue + horizValue + diagValueOne + diagValueTwo;

        return sum;
    }

    /**
     * Method for evaluating the Connect 4 verticals and assigning them based on
     * their strength i.e. how close they form a connect 4
     *
     * @param mainPlayer the player whose turn it is
     * @param opponent the player's opponent
     * @param row row we are checking
     * @param column column we are checking
     * @return sum int that is a representation of the position strength
     */
    private int connect4Verticals(VierGewinnt.Token mainPlayer, VierGewinnt.Token opponent, int row, int column, VierGewinnt.Token[][] board) {
        // the bottom of a connect 4 is minimum row 0 or 3 checkers down
        int possibleBottom;
        possibleBottom = Math.max(0, row - 3);
        int possibleTop = possibleBottom + 4;

        // counters to calculate eval values
        int playerCount = 0;
        int opponentCount = 0;
        int verticalValue = 0;

        // Check for the Connect 4 from the bottom up
        for (int checkRow = possibleBottom; checkRow < possibleTop; checkRow++) {
            if (board[column][checkRow] == opponent) {
                opponentCount = opponentCount + 1;
            } else if (board[column][checkRow] == mainPlayer) {
                playerCount = playerCount + 1;
            }
        }

        // if there isn't the other player's piece in the way, weight by position strength
        verticalValue = applyWeights(playerCount, opponentCount, verticalValue);

        // return this sum for the analysis of the verticals
        return verticalValue;
    }

    /**
     * Public helper method to apply weights after looking at Connect 4
     * possibilities
     *
     * @param playerCount the number of pieces player has in the connect 4 line
     * @param opponentCount the number of pieces opponent has in the connect 4
     * line
     * @param sum the weighted sum so far
     * @return the new sum after applying the weights.
     */
    public static int applyWeights(int playerCount, int opponentCount, int sum) {
        // apply the weights based on the previous connect 4 possibilities
        if (playerCount == 0) {
            sum = sum - HOW_GOOD[opponentCount];
        } else if (opponentCount == 0) {
            sum = sum + HOW_GOOD[playerCount];
        }

        return sum;
    }

    /**
     * Evaluates the possibilities for diagonal and horizontal connect fours
     *
     * @param mainPlayer the main player
     * @param opponent the other player
     * @param leftBound the leftside bound of the connect 4
     * @param rightBound the rightside bound of the connect 4
     * @param currentRow the open row that the move piece would go into
     * @param offsetRow the offset for diagonals (1 or -1), 0 for horizontals
     * @return
     */
    private int evalPossibilities(VierGewinnt.Token mainPlayer, VierGewinnt.Token opponent, int leftBound,
            int rightBound, int currentRow, int offsetRow, VierGewinnt.Token[][] board) {

        // declare local variables
        int boundDiff = rightBound - leftBound;
        int opponentCount = 0;
        int playerCount = 0;
        int sum = 0;
        int checkColumn = leftBound;
        int checkRow = currentRow;

        // -4 or 4 depending on which type of diagonal
        // 0 if checking horizontal
        int diagonalDelta = offsetRow * 4;

        if (boundDiff < 3) {
            return 0;
        }

        // ++ for row and column for diagonals
        // ++ for column for horizontals
        for (checkColumn = checkColumn;
                checkColumn <= leftBound + 3;
                checkRow += offsetRow) {

            // check whose pieces belong to whom
            if (board[checkColumn][checkRow] == opponent) {
                opponentCount = opponentCount + 1;
            } else if (board[checkColumn][checkRow] == mainPlayer) {
                playerCount = playerCount + 1;
            }
            checkColumn = checkColumn + 1;

        }

        // apply the weights based on the previous connect 4 possibilities
        sum = applyWeights(playerCount, opponentCount, sum);

        // ++ for row and column for diagonals
        // ++ for column for horizontals
        for (checkColumn = checkColumn;
                checkColumn <= rightBound;
                checkRow += offsetRow) {
            if (board[(checkColumn - 4)][(checkRow - diagonalDelta)] == opponent) {
                opponentCount = opponentCount - 1;
            }

            if (board[(checkColumn - 4)][(checkRow - diagonalDelta)] == mainPlayer) {
                playerCount = playerCount - 1;
            }

            if (board[checkColumn][checkRow] == opponent) {
                opponentCount = opponentCount + 1;
            }

            if (board[checkColumn][checkRow] == mainPlayer) {
                playerCount = playerCount + 1;
            }

            // apply the weights
            sum = applyWeights(playerCount, opponentCount, sum);

            checkColumn = checkColumn + 1;
        }

        //	    System.out.println("total value is " + sum + "\n");
        return sum;
    }
    
    private boolean canMakeFour(VierGewinnt.Token[][] board, VierGewinnt.Token token) {
    int row = 0;
    int column = 0;
    int nrOfRows = VierGewinnt.getNrOfRows(board);
    int nrOfColumns = VierGewinnt.getNrOfColumns(board);
    int zug;
    int pl2;
    int pl1;
    
    //waagrecht ***_
		for(row=0 ; row<nrOfRows ; row++){
			pl2=0;
			for(column=0 ; column<nrOfColumns ; column++){
				if (board[column][row]==token.player2) {pl2++;}
				if (board[column][row]!=token.player2) {pl2=0;}
				if (row==0 && pl2==3 && column<(nrOfColumns-1) && board[column+1][row]==token.empty)
					{return true;}
				if (row>0 && pl2==3 && column<(nrOfColumns-1) && board[column+1][row]==token.empty && 
					board[column+1][row-1]!=token.empty)
					{return true;}
			}
		}
//		waagrecht _***
		for(row=0 ; row<nrOfRows ; row++){
			pl2=0;
			for(column=(nrOfColumns-1) ; column>0 ; column--){
				if (board[column][row]==token.player2) {pl2++;}
				if (board[column][row]!=token.player2) {pl2=0;}
				if (row==0 && pl2==3 && (board[column-1][row]==token.empty))
					{return true;}
				if (row>0 && pl2==3 && (board[column-1][row]==token.empty && 
					board[column-1][row-1]!=token.empty))
					{return true;}
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
				if (pl2==4) {return true;}
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
				if (pl2==4) {return true;}
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
				if (pl2==4) {return true;}
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
				if (pl2==4) {return true;}		
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
				if (pl2==4) {return true;}
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
				if (pl2==4) {return true;}		
			}
		}
//		senkrecht		
		for (column=0 ; column<nrOfColumns; column++){
			pl2=0;
			for(row=0 ; row<nrOfRows ; row++){
				if (board[column][row]==token) {pl2++;}
				if (board[column][row]!=token) {pl2=0;}
				if (pl2==3 && row<5 && (board[column][row+1]==token.empty))
					{return true;}
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
					{return true;} 
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
					{return true;} 
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
					{return true;}
				if (row>1 && pl1==3 && (board[column-1][row-2]!=token.empty) && 
					(board[column-1][row-1]==token.empty))
					{return true;} 
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
					{return true;}
				if (row>1 && pl1==3 && (board[column+1][row-2]!=token.empty) &&
					(board[column+1][row-1]==token.empty))
					{return true;} 	
			}
		}
                return false;
    }

    /**
     * Evaluate position by finding unblocked 4 in a rows
     *
     * @param board
     * @return a total int evaluation of unblocked four-in-rows for opp and
     * computer
     */
    public int evaluate(VierGewinnt.Token[][] board) {
        // grab the checker pieces and board
        VierGewinnt.Token player = getActivePlayerToken(board);
        VierGewinnt.Token opponent = getInactivePlayerToken(board);
        //VierGewinnt.Token player = VierGewinnt.Token.player1;
        //VierGewinnt.Token opponent = VierGewinnt.Token.player2;

        // value that evaluates the unblocked four-in-rows
        int totalEvaluation = 0;
        
        if (canMakeFour(board, player) || canMakeFour(board, opponent)) return HOW_GOOD[4];
        
        // Evaluate patterns for winning
        //
        //   . X X . .   => unblocked on both sides so we can connect 4
        //  by placing another piece to become
        //  . X X X .
        for (int checkColumn = 0; checkColumn < VierGewinnt.getNrOfColumns(board) - 4; checkColumn++) {
            // if 0 is empty, followed by 2 of my pieces and two more empty, this is a pattern
            if (board[checkColumn][0] == VierGewinnt.Token.empty
                    && board[checkColumn + 1][0] == player
                    && board[checkColumn + 2][0] == player
                    && board[checkColumn + 3][0] == VierGewinnt.Token.empty
                    && board[checkColumn + 4][0] == VierGewinnt.Token.empty) {
                return HOW_GOOD[4];
            } else if (board[checkColumn][0] == VierGewinnt.Token.empty
                    && board[checkColumn + 1][0] == VierGewinnt.Token.empty
                    && board[checkColumn + 2][0] == player
                    && board[checkColumn + 3][0] == player
                    && board[checkColumn + 4][0] == VierGewinnt.Token.empty) {
                return HOW_GOOD[4];
            }
        }

        // Evaluate unblocked verticals
        // all potential ver 4-in-row start from at most from row 2
        for (int column = 0; column < VierGewinnt.getNrOfColumns(board); column++) {
            for (int row = 0; row < 3; row++) {
                int compCount = 0;
                int oppCount = 0;

                for (int checkRow = row; checkRow < row + 4; checkRow++) {
                    if (board[column][checkRow] == player) {
                        compCount++;
                    } else if (board[column][checkRow] == opponent) {
                        oppCount++;
                    }
                }

                totalEvaluation = applyWeights(oppCount, compCount, totalEvaluation);
            }
        }

        // Evaluate unblocked horizontals
        // all potential hor 4-in-row start from at most from halfway col
        for (int column = 0; column <= VierGewinnt.getNrOfColumns(board) - 4; column++) {
            for (int row = 0; row < VierGewinnt.getNrOfRows(board); row++) {
                // counters for computer and opponent
                int compCount = 0;
                int oppCount = 0;

                for (int checkColumn = column; checkColumn < column + 4; checkColumn++) {
                    // check whose checker it is and increment their counter
                    if (board[checkColumn][row] == player) {
                        compCount++;
                    } else if (board[checkColumn][row] == opponent) {
                        oppCount++;
                    }
                }

                totalEvaluation = applyWeights(oppCount, compCount, totalEvaluation);
            }
        }

        // Evaluate unblocked diagonals (up to right)
        // up to right diagonal start at most from row 2, column 3
        for (int column = 0; column < 4; column++) {
            for (int row = 0; row < 3; row++) {
                int compCount = 0;
                int oppCount = 0;

                int checkRow = row; // need a checkrow parameter for diag
                for (int checkColumn = column; checkRow < row + 4; checkColumn++) {
                    if (board[checkColumn][checkRow] == player) {
                        compCount++;
                    } else if (board[checkColumn][checkRow] == opponent) {
                        oppCount++;
                    }

                    checkRow++; // adjust for diagonal
                }

                totalEvaluation = applyWeights(oppCount, compCount, totalEvaluation);
            }
        }

        // Evaluate unblocked diagonals (down to right)
        // down to right diagonal start at most from row 3, column 3
        for (int column = 0; column < 4; column++) {
            for (int row = 3; row <= 5; row++) {
                int compCount = 0;
                int oppCount = 0;

                int checkRow = row; // need a checkrow parameter for diag
                for (int checkColumn = column; checkColumn < column + 4; checkColumn++) {
                    if (board[checkColumn][checkRow] == player) {
                        compCount++;
                    } else if (board[checkColumn][checkRow] == opponent) {
                        oppCount++;
                    }

                    checkRow--; // adjust for diagonal
                }

                totalEvaluation = applyWeights(oppCount, compCount, totalEvaluation);
            }
        }

        return totalEvaluation;

    }

    private VierGewinnt.Token getActivePlayerToken(VierGewinnt.Token[][] board) {
        int player1count = 0;
        int player2count = 0;
        for (int cols = 0; cols < VierGewinnt.getNrOfColumns(board); cols++) {
            for (int rows = 0; rows < VierGewinnt.getNrOfRows(board); rows++) {
                if (board[cols][rows] == VierGewinnt.Token.player1) {
                    player1count++;
                }
                if (board[cols][rows] == VierGewinnt.Token.player2) {
                    player2count++;
                }
            }
        }
        if (player1count > player2count) {
            return VierGewinnt.Token.player2;
        } else {
            return VierGewinnt.Token.player1;
        }
    }

    private VierGewinnt.Token getInactivePlayerToken(VierGewinnt.Token[][] board) {
        int player1count = 0;
        int player2count = 0;
        for (int cols = 0; cols < VierGewinnt.getNrOfColumns(board); cols++) {
            for (int rows = 0; rows < VierGewinnt.getNrOfRows(board); rows++) {
                if (board[cols][rows] == VierGewinnt.Token.player1) {
                    player1count++;
                }
                if (board[cols][rows] == VierGewinnt.Token.player2) {
                    player2count++;
                }
            }
        }
        if (player1count > player2count) {
            return VierGewinnt.Token.player1;
        } else {
            return VierGewinnt.Token.player2;
        }
    }

    public void setToken(VierGewinnt.Token token) {
        this.token = token;
    }

    public VierGewinnt.Token getToken() {
        return this.token;
    }

    public String getProgrammers() {
        return "CPU Search Tree";
    }
}
