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
    private int nrOfColumns;
    private int nrOfRows;
    private Token[][] board;
    private static IPlayer[] players = new IPlayer[2]; // two players
    private int currentPlayer;
    private static boolean randomWins;
    private static ArrayList<String> spielzuege = new ArrayList<String>();
    
    public int getNrOfColumns(){
        return this.nrOfColumns;
    }
    
    public int getNrOfRows(){
        return this.nrOfRows;
    }
    
    private VierGewinnt(int columns, int rows) {
        this.nrOfColumns = columns;
        this.nrOfColumns = rows;
        this.board = new Token[columns][rows];
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
        while (!solved && !this.isBoardFull()) {
            // get player's next "move"
            insertCol = players[currentPlayer].getNextColumn(this.board);
            // insert the token and get the row where it landed
            insertRow = this.insertToken(insertCol, players[currentPlayer].getToken());
            // check if the game is over
            solved = this.checkVierGewinnt(insertCol, insertRow);
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

    /**
     * Checks if every position is occupied
     */
    private boolean isBoardFull() {

        for (Token[] column : this.board) {
            if (column[column.length - 1] == Token.empty) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks for at least four equal tokens in a row in either direction,
     * starting from the given position.
     */
    /**
     * Checks for at least four equal tokens in a row in either direction,
     * starting from the given position.
     */
    private boolean checkVierGewinnt(int col, int row) {

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {

                int horizon = 0, vertical = 0, diag1 = 0, diag2 = 0;
                for (int count = 0; count < 4; count++) {

                    if (i + count < board.length) {
                        if (board[i + count][j] == board[col][row]) {
                            horizon++;
                        }
                    }

                    if (j + count < board[0].length) {
                        if (board[i][j + count] == board[col][row]) {
                            vertical++;
                        }
                    }

                    if ((i + count < board.length) && (j + count < board[0].length)) {
                        if (board[i + count][j + count] == board[col][row]) {
                            diag1++;
                        }
                    }

                    if ((i - count >= 0) && (j + count < board[0].length)) {
                        if (board[i - count][j + count] == board[col][row]) {
                            diag2++;
                        }
                    }
                }
                if (horizon == 4 || vertical == 4 || diag1 == 4 || diag2 == 4) {
                    return true;
                }
            }
        }
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

        System.out.println();
        System.out.println("-------------Vier Gewinnt--------------");
        System.out.println();
        // initialize players
        System.out.println("0. Human Player");
        System.out.println("1. Computer Player \"Random\"");
        System.out.println("2. Computer Player \"Roland 1\"");
        System.out.println("3. Computer Player \"Roland 2\"");
        System.out.println("4. Computer Player \"Roland 3\"");
        System.out.println("5. Computer Player \"Roland 4\"");

        System.out.print("Choose player 1 (0-5): ");
        int pl1 = new Scanner(System.in).nextInt();

        System.out.print("Choose player 2 (0-5): ");
        int pl2 = new Scanner(System.in).nextInt();

        System.out.print("Choose board size (columns x rows, default is 7x6):");
        String boardSize = new Scanner(System.in).next();
        int nrOfColumns;
        int nrOfRows;
        if (!boardSize.isEmpty()) {
            String[] numbers = boardSize.split("x");
            nrOfColumns = Integer.valueOf(numbers[0]);
            nrOfRows = Integer.valueOf(numbers[1]);
        } else {
            nrOfColumns = 7;
            nrOfRows = 6;
        }

        if (pl1 == 0 || pl2 == 0) {
            humanPlays = true;
        } else {
            System.out.print("Number of games: ");
            nrOfGames = new Scanner(System.in).nextInt();

            System.out.print("Stop simulation when \"Random wins\" (y/n)? ");
            String answer = new Scanner(System.in).next();
            if (answer.toLowerCase().equals("y")) {
                stopSimulation = true;
            }
        }

        switch (pl1) {
            case 0:
                players[0] = new HumanPlayer();
                break;
            case 1:
                players[0] = new ComputerPlayerRandom();
                break;
            case 2:
                players[0] = new ComputerPlayer();
                break;
            case 3:
                players[0] = new ComputerPlayer2();
                break;
            case 4:
                players[0] = new ComputerPlayer3();
                break;
            case 5:
                players[0] = new ComputerPlayer4();
                break;
        }

        switch (pl2) {
            case 0:
                players[1] = new HumanPlayer();
                break;
            case 1:
                players[1] = new ComputerPlayerRandom();
                break;
            case 2:
                players[1] = new ComputerPlayer();
                break;
            case 3:
                players[1] = new ComputerPlayer2();
                break;
            case 4:
                players[1] = new ComputerPlayer3();
                break;
            case 5:
                players[1] = new ComputerPlayer4();
                break;
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
