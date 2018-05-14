package viergewinnt;

import cpu.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;

public class VierGewinnt {

    private static long pl1wins;
    private static long pl2wins;
    private static long draws;
    private static int max;

    private Token[][] board;
    private static IPlayer[] players = new IPlayer[2]; // two players
    private int currentPlayer;
    private static boolean randomWins;
    private static ArrayList<String> spielzuege = new ArrayList<String>();
    private static boolean AUTOSTART = false;

    public static int getNrOfColumns(Token[][] board) {
        return board.length;
    }

    public static int getNrOfRows(Token[][] board) {
        return board[0].length;
    }

    private VierGewinnt(int columns, int rows) {
        this.board = new Token[columns][rows];
    }

    private Token[][] getBoard() {
        return this.board;
    }

    // Enum for the different tokens (empty, O, X)
    public enum Token {
        empty(" "),
        player1("O"),
        player2("X");
        private String representation; // string representation of value

        Token(String s) {
            this.representation = s;
        }

        public String toString() {
            return this.representation;
        }
    }
    
    public static Token[][] copyBoard(Token[][] originalBoard){
        int nrOfColumns=VierGewinnt.getNrOfColumns(originalBoard);
        int nrOfRows=VierGewinnt.getNrOfRows(originalBoard);
        VierGewinnt.Token[][] newBoard = new VierGewinnt.Token[nrOfColumns][nrOfRows];
        for (int i = 0; i < nrOfColumns; i++) {
            for (int j=0; j < nrOfRows; j++) {
               newBoard[i][j]=originalBoard[i][j];
            }
        }
        return newBoard;
    }

    /**
     * initialize board and players and start the game
     */
    public void play() {
        //Delete all former turns
        spielzuege.clear();

        // initialize the board
        for (Token[] column : this.board) {
            Arrays.fill(column, Token.empty);
        }

        players[0].setToken(Token.player1);
        players[1].setToken(Token.player2);
        // play...
        boolean solved = false;
        currentPlayer = 0;
        int insertCol = 0, insertRow; // starting from 0
        int zugCounter = 0;
        while (!solved && !this.isBoardFull(this.board)) {
            // get player's next "move"
            insertCol = players[currentPlayer].getNextColumn(this.board);
            // insert the token and get the row where it landed
            insertRow = this.insertToken(insertCol, players[currentPlayer].getToken());
            // check if the game is over
            solved = this.isGameOver(this.board);
            //save Spielzug
            zugCounter++;
            spielzuege.add((zugCounter < 10 ? "0" + zugCounter : zugCounter) + "(" + players[currentPlayer].getToken() + "): " + (insertCol + 1));
            //switch to other player
            if (!solved) {
                currentPlayer = (currentPlayer + 1) % 2;
            }
        }
        System.out.println(displayBoard(this.board));
        if (solved) {
            System.out.println("Player " + players[currentPlayer].getToken() + " wins!");
            if (players[currentPlayer].getToken() == Token.player1) {
                pl1wins++;
            } else {
                pl2wins++;
            }
            if (players[currentPlayer].getProgrammers() == "Random") {
                randomWins = true;
            }
        } else {
            draws++;
            System.out.println("Draw! Game over.");
        }
    }

    /**
     * Inserts the token at the specified column (if possible) and returns the
     * row where the token landed
     */
    private int insertToken(int column, Token tok) {
        boolean full = true;

        //check if the selected column is valid
//		if (column < 0 || column > board.length) System.exit(1); 
        for (int i = 0; i <= board[column].length - 1; i++) {
            if (board[column][i] == Token.empty) {
                board[column][i] = tok;
                full = false;
                return i;
            }
        }

        //if (full) System.exit(1);
        return 0;
    }

    public static boolean isValidMove(int col, Token[][] board) {
        // move is valid if the top row isn't full
        return board[col][VierGewinnt.getNrOfRows(board) - 1] == Token.empty;
    }

    /**
     * Find the first empty row in a column -1 if the column is full (no empty
     * row)
     *
     * @param col the column to check
     */
    public static int findOpenRow(int col, Token[][] board) {
        // find the first row that isn't filled
        for (int i = 0; i < VierGewinnt.getNrOfRows(board); i++) {
            if (board[col][i] == Token.empty) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Finds the first occupied slot of a column
     *
     * @param col the column to check
     * @param <error>
     * @return the first occupied slot of a column
     */
    public static int findTop(int col, VierGewinnt.Token[][] board) {
        // find the top of the closed row
        int row = VierGewinnt.getNrOfRows(board) - 1;

        while (board[col][row] == Token.empty && row > 0) {
            row--;
        }

        return row;

    }

    /**
     * Checks if every position is occupied
     */
    public static boolean isBoardFull(Token[][] board) {

        for (Token[] column : board) {
            if (column[column.length - 1] == Token.empty) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks for at least four equal tokens in a row in either direction
     */
    public static boolean isGameOver(Token[][] board) {
        for (int t = 1; t <= 2; t++) {
            Token token = Token.values()[t];
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {

                    int horizon = 0, vertical = 0, diag1 = 0, diag2 = 0;
                    for (int count = 0; count < 4; count++) {

                        if (i + count < board.length) {
                            if (board[i + count][j] == token) {
                                horizon++;
                            }
                        }

                        if (j + count < board[0].length) {
                            if (board[i][j + count] == token) {
                                vertical++;
                            }
                        }

                        if ((i + count < board.length) && (j + count < board[0].length)) {
                            if (board[i + count][j + count] == token) {
                                diag1++;
                            }
                        }

                        if ((i - count >= 0) && (j + count < board[0].length)) {
                            if (board[i - count][j + count] == token) {
                                diag2++;
                            }
                        }
                    }
                    if (horizon == 4 || vertical == 4 || diag1 == 4 || diag2 == 4) {
                        //System.out.println("Token " + token + " wins the game.");
                        return true;
                    }
                }
            }
        }
        //System.out.println("No winner at the moment.");
        return false;
    }

    /**
     * returns a "graphical" representation of the board
     */
    public static String displayBoard(Token[][] myBoard) {
        String rowDelimiter = "+";
        String rowNumbering = " ";
        for (int col = 0; col < myBoard.length; col++) {
            rowDelimiter += "---+";
            rowNumbering += " " + (col + 1) + "  ";
        }
        rowDelimiter += "\n";

        String rowStr;
        String presentation = rowDelimiter;
        for (int row = myBoard[0].length - 1; row >= 0; row--) {
            rowStr = "| ";
            for (int col = 0; col < myBoard.length; col++) {
                rowStr += myBoard[col][row].toString() + " | ";
            }
            presentation += rowStr + "\n" + rowDelimiter;
        }
        presentation += rowNumbering;
        return presentation;
    }

    public static void getSpielzuege() {
        System.out.println();
        System.out.println("*************Spielzüge*************");
        System.out.println("Zug    Column");
        for (int i = spielzuege.size() - 1; i >= 0; i--) {
            System.out.println(spielzuege.get(i));
        }
    }

    /**
     * main mathod, starts the program
     */
    public static void main(String args[]) {
        int nrOfGames = 1;
        boolean humanPlays = false;
        boolean stopSimulation = false;
        int nrOfColumns;
        int nrOfRows;
        if (!AUTOSTART) {
            System.out.println();
            System.out.println("-------------Vier Gewinnt--------------");
            System.out.println();
            // initialize players
            System.out.println("1. Human Player");
            System.out.println("2. Computer Player \"Random\"");
            System.out.println("3. Computer Player \"Simple rules\"");
            System.out.println("4. Computer Player \"Advanced Rules\"");
            System.out.println("5. Computer Player \"Cheap Rush Trick\"");
            System.out.println("6. Computer Player \"Search Tree Alpha Beta\"");

            System.out.print("Choose player 1 (1-6): ");
            int pl1 = new Scanner(System.in).nextInt();

            System.out.print("Choose player 2 (1-6): ");
            int pl2 = new Scanner(System.in).nextInt();

            System.out.print("Choose board size (columns x rows, default is 7x6):");
            String boardSize = new Scanner(System.in).next();
            
            if (!boardSize.isEmpty()) {
                String[] numbers = boardSize.split("x");
                nrOfColumns = Integer.valueOf(numbers[0]);
                nrOfRows = Integer.valueOf(numbers[1]);
            } else {
                nrOfColumns = 7;
                nrOfRows = 6;
            }

            if (pl1 == 1 || pl2 == 1) {
                humanPlays = true;
            } else {
                System.out.print("Number of games: ");
                nrOfGames = new Scanner(System.in).nextInt();

                if (pl1 == 2 || pl2 == 2) {
                    System.out.print("Stop simulation when \"Random wins\" (y/n)? ");
                    String answer = new Scanner(System.in).next();
                    if (answer.toLowerCase().equals("y")) {
                        stopSimulation = true;
                    } else {
                        stopSimulation = false;
                    }
                }
            }

            switch (pl1) {
                case 1:
                    players[0] = new HumanPlayer();
                    break;
                case 2:
                    players[0] = new ComputerPlayerRandom();
                    break;
                case 3:
                    players[0] = new ComputerPlayerRulesSimple();
                    break;
                case 4:
                    players[0] = new ComputerPlayerRulesAdvanced();
                    break;
                case 5:
                    players[0] = new ComputerPlayerCheapRushTrick();
                    break;
                case 6:
                    players[0] = new ComputerPlayerSearchTreeAlphaBeta();
                    break;
            }

            switch (pl2) {
                case 1:
                    players[1] = new HumanPlayer();
                    break;
                case 2:
                    players[1] = new ComputerPlayerRandom();
                    break;
                case 3:
                    players[1] = new ComputerPlayerRulesSimple();
                    break;
                case 4:
                    players[1] = new ComputerPlayerRulesAdvanced();
                    break;
                case 5:
                    players[1] = new ComputerPlayerCheapRushTrick();
                    break;
                case 6:
                    players[1] = new ComputerPlayerSearchTreeAlphaBeta();
                    break;
            }
        } else {
            nrOfColumns=7;
            nrOfRows=6;
            humanPlays = true;
            stopSimulation = false;
            players[0] = new HumanPlayer();
            players[1] = new ComputerPlayerSearchTreeAlphaBeta();
        }

        VierGewinnt game = new VierGewinnt(nrOfColumns, nrOfRows);

        if (humanPlays) {
            game.play();
        } else {
            for (int i = 1; i <= nrOfGames; i++) {
                game.play();
                if (randomWins && stopSimulation) {
                    System.out.println("Simulation interrupted at game " + i + "!");
                    nrOfGames = i;
                    getSpielzuege();
                    break;
                }
            }
        }

        System.out.println();
        System.out.println("*************Statistics*************");
        System.out.println("Games played: " + nrOfGames);
        System.out.println(players[0].getProgrammers() + ": " + pl1wins + " (" + (100 / (float) nrOfGames) * pl1wins + "%)");
        System.out.println(players[1].getProgrammers() + ": " + pl2wins + " (" + (100 / (float) nrOfGames) * pl2wins + "%)");
        System.out.println("Draws: " + draws + " (" + (100 / (float) nrOfGames) * draws + "%)");
    }
}
