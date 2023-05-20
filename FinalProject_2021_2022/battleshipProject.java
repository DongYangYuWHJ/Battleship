package FinalProject_2021_2022;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

@SuppressWarnings("serial")
public class battleshipProject extends JFrame implements ActionListener {
		
	//create and initialize both boards:
	String[][] myBoard = new String[10][10];//original myboard, not in GUI
	String[][] enemyBoard = new String[10][10];
	String[] battleShipNames = {"Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer"};
	int[] AIBattleShipHealths = {5,4,3,3,2};
	int[] playerBattleShipHealths = {5,4,3,3,2};
	
	
	
	//global GUI 2D arrays:
	JButton[][] enemyBoardLayerGUI = new JButton[10][10];
	JButton[][] myBoardGUI = new JButton[10][10];//convert myBoard from String array to GUI
	
	
	
	//other:
	boolean basePlacementMethodClicked = false;//used as an indicator to see this method should be used or not
	ArrayList<Integer> grayButtonsIndex = new ArrayList<Integer>();//used to store the gray buttons(unconfirmed buttons) detected
	
	int[] previousBaseLocation = new int[1];
	String[] previousDirection = {""};
	
	//these 2 record previous clicked button: the location and dir
	boolean[] inBoundary = {true};//object: can be changed in methods, 
	
	
	
	//battleships
	BattleShip Carrier = new BattleShip(battleShipNames[0],playerBattleShipHealths[0]);
	BattleShip Battleship = new BattleShip(battleShipNames[1],playerBattleShipHealths[1]);
	BattleShip Cruiser = new BattleShip(battleShipNames[2],playerBattleShipHealths[2]);
	BattleShip Submarine = new BattleShip(battleShipNames[3],playerBattleShipHealths[3]);
	BattleShip Destroyer = new BattleShip(battleShipNames[4],playerBattleShipHealths[4]);
	BattleShip[] ships = {Carrier, Battleship, Cruiser, Submarine, Destroyer};
	
	ArrayList<BattleShip> unusedShip = new ArrayList<BattleShip>();
	ArrayList<BattleShip> unusedShipBackup = new ArrayList<BattleShip>();//used when an ship is used but denied(click other button after choosing the ship), to make sure the "used ship" is in the unused ship
	//this backup will not have that object only when confirm the ship location
	
	BattleShip[] chosenShip = new BattleShip[1];//only store the chosen ship, which will be placed in the GUI
	
	boolean startTheBattle = false;
	
	
	
	//Buttons for placing battleships
	JButton carrierBut = new JButton("Carrier: 5 holes");
	JButton battleshipBut = new JButton("Battleship: 4 holes");
	JButton cruiserBut = new JButton("Cruiser: 3 holes");
	JButton submarineBut = new JButton("Submarine: 3 holes");
	JButton destroyerBut = new JButton("Destroyer: 2 holes");
	
	//Buttons for display:
	JButton vertical = new JButton("Vertical");
	JButton horizontal = new JButton("Horizontal");
	JButton confirmAShip = new JButton("confirm this location");
	JButton removeShip = new JButton("remove a confirmed ship");
	JButton startBattle = new JButton("start the battle");
	
	//Hint(Player Guide)
	JLabel seperation;
	
	
	
	//screen label:(show who wins and so on)
	JLabel totalAttackPlayer = new JLabel("Player Total Attacks #: ");
	JLabel totalHitPlayer = new JLabel("Player Total Hits #: ");
	JLabel totalMissPlayer = new JLabel("Player Total Misses #: ");
	JLabel shipLeftPlayer = new JLabel("Player Ships Left #: ");
		
	JLabel totalAttackEnemy = new JLabel("AI Total Attacks #: ");
	JLabel totalHitEnemy = new JLabel("AI Total Hits #: ");
	JLabel totalMissEnemy = new JLabel("AI Total Misses #: ");
	JLabel shipLeftEnemy= new JLabel("AI Ships Left #: ");
		
	JLabel display = new JLabel("Hi! Place your ships by clicking buttons below!");
		
	
	
	
	//implementing OOP (for combat AI)
	CombatAttribute var = new CombatAttribute();	
		
	//Game ends / Game difficulty
	static int playerTotalHealth = 5 + 4 + 3 + 3 + 2; 
	static int enemyTotalHealth = 5 + 4 + 3 + 3 + 2;
	static String mode = "";	
	
	
	//Arraylist for combat AI method
	ArrayList<int[]> AIavailableSquares = new ArrayList<int[]>();//arraylist of arrays of size 2
	ArrayList<String[]> AIenemySquares = new ArrayList<String[]>();//arraylist of arrays of size 3
	
	
	
	public static boolean playerCheckSpotVertical(int index2D, int shipLength, JButton[][] myBoardGUI) {//base location assumed
        //vertically up(index--)
        int row = index2D/10;
        int column = index2D%10;
        if(row+1 - shipLength < 0) {//out of boundary
            for(int i = 0; i < row+1 ; i++) {
                if(myBoardGUI[row-i][column].getBackground().equals(Color.black)) {//there is a ship above and may overlap
                    return false;//
                }
            }
        }else {//in boundary
            for(int i = 0; i < shipLength; i++) {
                if(myBoardGUI[row - i][column].getBackground().equals(Color.black)) {//overlap ship
                    return false;
                }
            }
        }
        return true;//if no overlap
    }
	
	
	
	public static boolean playerCheckSpotHorizontal(int index2D, int shipLength, JButton[][] myBoardGUI) {
        int row = index2D/10;
        int column = index2D%10;
        if(column-1 + shipLength > 9) {//out of boundary
            for(int i = 0; i < 10-column ; i++) {
                if(myBoardGUI[row][column+i].getBackground().equals(Color.black)) {//there is a ship above and may overlap
                    return false;//
                }
            }
        }else {//in boundary
            for(int i = 0; i < shipLength; i++) {
                if(myBoardGUI[row][column+i].getBackground().equals(Color.black)) {//overlap ship
                    return false;
                }
            }
        }
        return true;//if no overlap
    }
	
	
	//battle AI  method:
	public int[] checkAvailableSquares (int atkRow, int atkColumn, int i, int j, boolean adjacent) {
		int[] arr = new int[2];
		
		try {
			
			arr[0] = atkRow + i;
			arr[1] = atkColumn + j;
			myBoard[arr[0]][arr[1]] = myBoard[arr[0]][arr[1]];
		} catch (ArrayIndexOutOfBoundsException e) {
			arr[0] = -1;
			arr[1] = -1;
			return arr;
		}
		
		if (myBoard[atkRow + i][atkColumn + j].equals("destroyed") || myBoard[atkRow + i][atkColumn + j].equals("attacked")) {
			arr[0] = -1;
			arr[1] = -1;
			return arr;
		}
		
		
		if (adjacent) {
			for (int k = 1; k <= AIavailableSquares.size(); k++) {
				if (AIavailableSquares.get(k-1)[0] == atkRow + i && AIavailableSquares.get(k-1)[1] == atkColumn + j) {
					arr[0] = -1;
					arr[1] = -1;
				}
			}
		}
		
		return arr;
		
	}

	
	//player battle method
	//A method to find the button pressed
    public static int[] findButton(JButton c, JButton[][] board) {
        int[] arr = new int[2];
        for (int i = 1; i <= board.length; i++) {
            for (int j = 1; j <= board[0].length; j++) {
                if (c.equals(board[i-1][j-1])) {
                     arr[0] = i-1;
                     arr[1] = j-1;
                }
            }
        }
        return arr;//index
    }

    

    public void actionPerformed(JButton c, JButton[][] enemyBoardLayerGUI, String[][] enemyBoard) {

        int[] arr = findButton(c, enemyBoardLayerGUI);//Find the button the player pressed

        enemyBoardLayerGUI[arr[0]][arr[1]].removeActionListener(this);//Remove the action listener so player can no longer click on it again

        switch(enemyBoard[arr[0]][arr[1]]) {//Change that button's corresponding value in the game board 2D array
        case "blank":
            var.setHitsAndMisses(2);//PlayerMisses++
            enemyBoard[arr[0]][arr[1]] = "attacked";
            enemyBoardLayerGUI[arr[0]][arr[1]].setText("O");
            enemyBoardLayerGUI[arr[0]][arr[1]].setBackground(Color.green);

            break;
        case "field":
            var.setHitsAndMisses(2);//PlayerMisses++
            enemyBoard[arr[0]][arr[1]] = "attacked";
            enemyBoardLayerGUI[arr[0]][arr[1]].setText("O");
            enemyBoardLayerGUI[arr[0]][arr[1]].setBackground(Color.green);

            break;
        default:
            var.setHitsAndMisses(1);//PlayerHits++
            enemyTotalHealth--;
            int hitShip = -1;
			if (enemyBoard[arr[0]][arr[1]].equals("Carrier")) {
				hitShip = 0;
				
			}else if(enemyBoard[arr[0]][arr[1]].equals("Battleship")) {
				hitShip = 1;
				
			}else if(enemyBoard[arr[0]][arr[1]].equals("Cruiser")) {
				hitShip = 2;
				
			}else if(enemyBoard[arr[0]][arr[1]].equals("Submarine")) {
				hitShip = 3;
				
			}else if(enemyBoard[arr[0]][arr[1]].equals("Destroyer")) {
				hitShip = 4;
				
			}
			try {
				AIBattleShipHealths[hitShip]--;
			} catch(ArrayIndexOutOfBoundsException e) {
				//Ignores
			}
            
            enemyBoard[arr[0]][arr[1]] = "destroyed";
            enemyBoardLayerGUI[arr[0]][arr[1]].setText("X");
            enemyBoardLayerGUI[arr[0]][arr[1]].setForeground(Color.white);
            enemyBoardLayerGUI[arr[0]][arr[1]].setBackground(Color.red);
            
        }
    } 
	
	
	
	public static boolean CheckSpotAI(int row, int column, int direction, int length, String board[][]) {
		if (direction == 0) {
			if ((10 - column) < length) {
				return false;
			}else {
			for (int i = 0; i < length; i++) {
				if (!board[row][column + i].equals("blank")){
					return false;
				}
			}
			}
		}else {
			if((10 - row) < length) {
				return false;
			}else {
				for (int k = 0; k < length; k++) {
				if (!board[row + k][column].equals("blank")) {
					return false;
				}
			}
			}	
		}
		return true;
	}
	
	
	
	public void AIBattleshipPlacement() throws IOException{
		for (int i = 0; i < enemyBoard.length; i++) {
			for (int k = 0; k < enemyBoard[0].length; k++){
				enemyBoard[i][k] = "blank";
			}
		}
		for (int i = 0; i < 5; i++) {
		if (var.Difficulty().equals("simple")) {
			
				int row = (int)(Math.random()*7) + 1;
				int column = (int)(Math.random()*5) + 2;
				int direction = (int)(Math.random()*4);
				
					
					while (!CheckSpotAI(row,column,direction, AIBattleShipHealths[i],enemyBoard)) {
						row = (int)(Math.random()*7) + 1;
						column = (int)(Math.random()*5) + 2;
						direction = (int)(Math.random()*4);
					}
					if (direction == 0) {
						for (int k = 0; k < AIBattleShipHealths[i]; k++) {
							enemyBoard[row][column + k] = battleShipNames[i];
						}
					}else {
						for (int q = 0; q < AIBattleShipHealths[i]; q++) {
							enemyBoard[row + q][column] = battleShipNames[i];

						}
					}
				}
			
		if (var.Difficulty().equals("advanced")) {
			int direction = (int)(Math.random()*2);
			if (direction == 0) {
				int row = (int)(Math.random()*10);
				while(row > 3 && row < 7) {
					row = (int)(Math.random()*10);
				}
				int column = (int)(Math.random()*9);
				while (!CheckSpotAI(row,column,direction, AIBattleShipHealths[i],enemyBoard)) {
					row = (int)(Math.random()*10);
					while(row > 3 && row < 7) {
						row = (int)(Math.random()*10);
					}
					column = (int)(Math.random()*9);
				}
				for (int k = 0; k < AIBattleShipHealths[i]; k++) {
					enemyBoard[row][column + k] = battleShipNames[i];
					CreateField(row,column + k);
				}
			} else if (direction == 1) {
				int row = (int)(Math.random()*9);
				int column = (int)(Math.random()*9);
				while(column > 3 && column < 6) {
					column = (int)(Math.random()*10);
				}
				while (!CheckSpotAI(row,column,direction, AIBattleShipHealths[i],enemyBoard)) {
					row = (int)(Math.random()*9);
					column = (int)(Math.random()*9);
					while(column > 3 && column < 6) {
						column = (int)(Math.random()*10);
					}
				}
				for (int k = 0; k < AIBattleShipHealths[i]; k++) {
					enemyBoard[row + k][column] = battleShipNames[i];
					CreateField(row + k,column);
				}
			}
		}
		}
		//write into a file
        PrintWriter fw = new PrintWriter("AIbattleshipPlacement.txt");
        for (int i = 0; i < 10; i++) {
        	for (int k = 0; k < 10; k++) {
        		if(enemyBoard[i][k].equals("field")) {
        			fw.print("blank, ");
        		}else {
        			fw.print(enemyBoard[i][k] + ", ");
        		}
        		
        	}
        	fw.println();
        }

        fw.close();

        System.out.println("EnemyBoard successfully recorded into file 'AIbattleshipPlacement.txt'.");
	}
	
	
	
	public void CreateField(int row, int column) {
		if ((row != 9 && column != 9) && (row != 9 && column != 0) && (row != 0 && column != 9) && (row != 9 && column != 0)) {

			if (enemyBoard[row + 1][column + 1].equals("blank")) {
				enemyBoard[row + 1][column + 1] = "field";
			}if (enemyBoard[row + 1][column - 1].equals("blank")) {
				enemyBoard[row + 1][column - 1] = "field";
			}if (enemyBoard[row + 1][column].equals("blank")) {
				enemyBoard[row + 1][column] = "field";
			}if (enemyBoard[row][column + 1].equals("blank")) {
				enemyBoard[row][column + 1] = "field";
			}if (enemyBoard[row][column - 1].equals("blank")) {
				enemyBoard[row][column - 1] = "field";
			}if (enemyBoard[row - 1][column + 1].equals("blank")) {
				enemyBoard[row - 1][column + 1] = "field";
			}if (enemyBoard[row - 1][column].equals("blank")) {
				enemyBoard[row - 1][column] = "field";
			}if (enemyBoard[row - 1][column - 1].equals("blank")) {
				enemyBoard[row - 1][column - 1] = "field";
			}
		}
	}
	
	
	//methods:
	//method: create a GUI version of corresponding 2D array
	public static JButton[][] convertGUI(String[][] boardArray){
		JButton[][] boardGUI = new JButton[boardArray.length][boardArray[0].length];	
		
		for(int row = 0; row < boardGUI.length;row++) {
            for(int column = 0; column < boardGUI[0].length;column++) {//use if statement            	

                switch (boardArray[row][column]) {
                case "blank":
                    boardGUI[row][column] = new JButton("");
                    boardGUI[row][column].setOpaque(true);
                    boardGUI[row][column].setBackground(Color.white);
                    boardGUI[row][column].setForeground(Color.orange);
                    break;
                case "field":
                	boardGUI[row][column] = new JButton("");
                    boardGUI[row][column].setOpaque(true);
                    boardGUI[row][column].setBackground(Color.white);
                    boardGUI[row][column].setForeground(Color.orange);
                    break;
                case "attacked":
                    boardGUI[row][column] = new JButton(".");
                    boardGUI[row][column].setOpaque(true);
                    boardGUI[row][column].setBackground(Color.decode("00CCFF"));
                    boardGUI[row][column].setForeground(Color.orange);
                    break;
                case "destroyed":
                    boardGUI[row][column] = new JButton("X");
                    boardGUI[row][column].setOpaque(true);
                    boardGUI[row][column].setForeground(Color.red);
                    break;
                case "0":
                    boardGUI[row][column] = new JButton("");
                    boardGUI[row][column].setOpaque(true);
                    boardGUI[row][column].setBackground(Color.black);
                    boardGUI[row][column].setText("Carrier");
                    boardGUI[row][column].setForeground(Color.orange);
                    break;
                case "1":
                    boardGUI[row][column] = new JButton("");
                    boardGUI[row][column].setOpaque(true);
                    boardGUI[row][column].setBackground(Color.black);
                    boardGUI[row][column].setText("Battleship");
                    boardGUI[row][column].setForeground(Color.orange);
                    break;
                case "2":
                    boardGUI[row][column] = new JButton("");
                    boardGUI[row][column].setOpaque(true);
                    boardGUI[row][column].setBackground(Color.black);
                    boardGUI[row][column].setText("Cruiser");
                    boardGUI[row][column].setForeground(Color.orange);
                    break;
                case "3":
                	boardGUI[row][column] = new JButton("");
                    boardGUI[row][column].setOpaque(true);
                    boardGUI[row][column].setBackground(Color.black);
                    boardGUI[row][column].setText("Submarine");
                    boardGUI[row][column].setForeground(Color.orange);
                    break;
                case "4":
                	boardGUI[row][column] = new JButton("");
                    boardGUI[row][column].setOpaque(true);
                    boardGUI[row][column].setBackground(Color.black);
                    boardGUI[row][column].setText("Destroyer");
                    boardGUI[row][column].setForeground(Color.orange);
                    break;
                }
                boardGUI[row][column].setPreferredSize(new Dimension (60,60));//set size
                boardGUI[row][column].setFont(new Font("Serif",Font.PLAIN, 10));

            }
		}
		
		return boardGUI;
	}
		
	
	//method:find the index of clicked button: 
	//only available if you indicate which 2D array it belongs to
	public static int buttonIndex(Object buttonClicked, JButton[][] arrayGUI) {
		int index2D = -1;//this int is ranges from 0-99, totally 100 numbers, the tenth digit is the row of it the single digit is the column of it
		//for example, 99 means the row of index 9 and the column of index 9, which is the 10th row and 10th column
		//if -1, then no index is found
		
		for (int x = 0; x < arrayGUI.length; x++) {
            for (int y = 0; y < arrayGUI[0].length; y++) {
                if (buttonClicked.equals(arrayGUI[x][y])) {
                    index2D = 10*x + y;
                }
            }
        }
		
		return index2D;
		
	}
	
	
	//method: place player's battleship
	//method: determine the base location of the method
	public static void PlayerBattleshipBaseLocation(BattleShip[] chosenShip, ArrayList<BattleShip> unusedShip, int index2D, JButton[][] myBoardGUI, boolean[] inBoundary) {//remember to show field!!!!!!!!!!!!!!!
		
		//assign values
		chosenShip[0].setBattleshipIndex(index2D);
		
		
		int row = index2D/10;//tenth digit
		int column = index2D%10;//single digit
		//clicked button location
		
		
		if(unusedShip.contains(chosenShip[0])) {//if the ship has not been chosen
			
			unusedShip.remove(unusedShip.indexOf(chosenShip[0]));//remove the used battleship
			
			//next:
			//see if the ship is out of range:
			//originally assume the ship is placed vertically
			//thus, only consider whether the length(row) is out of range or not
			if ((row+1 - chosenShip[0].getBattleshipHealth()) < 0 ) {
				//out of range
				for(int i = row; i >= 0; i--) {//change the button to black until the boundary
					
					//only in GUI, only when the location is confirmed, add it to original array
					myBoardGUI[i][column].setText(chosenShip[0].getBattleshipName());
					myBoardGUI[i][column].setOpaque(true);
					myBoardGUI[i][column].setBackground(Color.gray);//indicate that it is a unconfirmed ship
					myBoardGUI[row][column].setForeground(Color.orange);
				}
				inBoundary[0] = false;
			}else {//smaller or equal to 9, in the range
				for(int i = row; i > row-chosenShip[0].getBattleshipHealth(); i--) {
					myBoardGUI[i][column].setText(chosenShip[0].getBattleshipName());
					myBoardGUI[i][column].setOpaque(true);
					myBoardGUI[i][column].setBackground(Color.gray);//indicate that it is a unconfirmed ship
					myBoardGUI[row][column].setForeground(Color.orange);
				}
				inBoundary[0] = true;
			}
			
		} else {//if the ship is already chosen
			return;//end the method
		}
		
		//at the end
		chosenShip[0] = null;//change the only value to "null" to indicate that the ship is already changed???
	}	
	
	
	
	//method: check the board if there is an gray buttons(unconfirmed ship)
	public static void checkUnconfirmedShip (JButton[][] board, ArrayList<Integer> grayButtonsIndex) {
		//clear grayButtonIndex
		grayButtonsIndex.clear();
		for(int row = 0; row < board.length; row++) {
			for(int column = 0; column < board[0].length; column++){
				if(board[row][column].getBackground().equals(Color.gray)) {
					grayButtonsIndex.add(10*row + column);//add to array list
				}
			}
		}
	}
	
	
	
	public void confirmButtonLocation(JButton[][] board, String[] previousDirection, boolean[] inBoundary, ArrayList<BattleShip> unusedShipBackup) {		
		
		ArrayList<Integer> unsureButtonsIndex = new ArrayList<Integer>();
		checkUnconfirmedShip (board, unsureButtonsIndex);
		Collections.sort(unsureButtonsIndex);//from lowest to greatest	
		
		if(inBoundary[0]) {//true, in the range
			
			//paint it black
			for(int i = 0; i < unsureButtonsIndex.size();i++) {
				int row = unsureButtonsIndex.get(i)/10;
				int column = unsureButtonsIndex.get(i)%10;
				board[row][column].setBackground(Color.black);//finialized
			}
			
			//remove unusedButtonsBackup
			if(unsureButtonsIndex.size() > 0) {
			int rowSample = unsureButtonsIndex.get(1)/10;
			int columnSample = unsureButtonsIndex.get(1)%10;
			
			String name = board[rowSample][columnSample].getText();//text are names of battleships
			for(int i = 0; i < unusedShipBackup.size(); i++) {
				if ( unusedShipBackup.get(i).getBattleshipName().equals(name) ) {//find the ship from unusedShipBackup list
					unusedShipBackup.remove(i);
				}
			}
			
			previousDirection[0] = "";
			}
			
		}else {
			display.setText("Out of boundary, Please relocate the ship");
			System.out.println("Out of boundary, Please relocate the ship");
		}
		
	}	
	
	
	
	//method: remove a ship
	public static void removeButtons(JButton[][] board, ArrayList<BattleShip> unusedShip, ArrayList<BattleShip> unusedShipBackup, BattleShip Carrier, BattleShip Battleship, BattleShip Cruiser, BattleShip Submarine, BattleShip Destroyer) {       
		
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.println("Please select a ship to remove.");
		System.out.println("Enter 0 for Carrier, 1 for Battleship, 2 for Cruiser, 3 for Submarine, 4 for Destroyer: ");
		int entered = scan.nextInt();
		String text = "";
		if(entered == 0) {
			text = "Carrier";
			unusedShip.add(Carrier);
			unusedShipBackup.add(Carrier);
		}else if(entered == 1) {
			text = "Battleship";
			unusedShip.add(Battleship);
			unusedShipBackup.add(Battleship);
		}else if(entered == 2) {
			text = "Cruiser";
			unusedShip.add(Cruiser);
			unusedShipBackup.add(Cruiser);
		}else if(entered == 3) {
			text = "Submarine";
			unusedShip.add(Submarine);
			unusedShipBackup.add(Submarine);
		}else if(entered == 4) {
			text = "Destroyer";
			unusedShip.add(Destroyer);
			unusedShipBackup.add(Destroyer);
		}
		
		
		for(int row = 0; row < board.length; row++) {
			for(int column = 0; column < board[row].length; column++) {
				if( (board[row][column].getText().equals(text))&&(board[row][column].getBackground().equals(Color.black)) ) {
					board[row][column].setBackground(Color.white);
					board[row][column].setText("");
					
					
				}
			}
		}
		//add removed ships back
		
	}
	
	
	//method: start the battle
    public boolean startBattle(JButton[][] board) {
        int numBlack = 0;
        for(int row = 0; row < board.length; row++ ) {
            for(int column = 0; column < board[row].length; column++) {
                if(board[row][column].getBackground().equals(Color.black)) {
                    numBlack++;
                }
            }
        }
        int numBlackSuppose = 5+4+3+3+2;

        if(numBlackSuppose == numBlack) {


            for(int row = 0; row < board.length;row++) {
                for(int column = 0; column < board[0].length;column++) {//use if statement

                    switch (board[row][column].getText()) {
                    case "":
                        myBoard[row][column] = "blank";
                        break;

                    case "Carrier":
                    	myBoard[row][column] = "0";
                        break;
                    case "Battleship":
                    	myBoard[row][column] = "1";
                        break;
                    case "Cruiser":
                    	myBoard[row][column] = "2";
                        break;
                    case "Submarine":
                    	myBoard[row][column] = "3";
                        break;
                    case "Destroyer":
                    	myBoard[row][column] = "4";
                        break;

                    }

                }
            }
          
            
    		carrierBut.removeActionListener(this);
    		battleshipBut.removeActionListener(this);
    		cruiserBut.removeActionListener(this);
    		submarineBut.removeActionListener(this);
    		destroyerBut.removeActionListener(this);
    		vertical.removeActionListener(this);
    		horizontal.removeActionListener(this);
    		confirmAShip.removeActionListener(this);
    		removeShip.removeActionListener(this);
    		startBattle.removeActionListener(this);
    		
    		System.out.println("Determining who will go first..."); //Make a coin toss to determine whether the player or the AI will go first
    		int toss1 = 1;
    		switch(toss1) {//var.toss()
    		case 1:
    			display.setText("The coin landed on heads, Player shall go first.");
    			System.out.println("The coin landed on heads, the Player shall go first.");
    			break;
    		case 0:
    			display.setText("The coin landed on tails, AI shall go first.");
    			System.out.println("The coin landed on tails, the AI shall go first.");
    			combatAI(); //Attack first
				totalAttackEnemy.setText("AI Total Attacks #: " + (var.hitsAndMisses(3)+var.hitsAndMisses(4)));
				totalHitEnemy.setText("AI Total Hits #: "+var.hitsAndMisses(3));
				totalMissEnemy.setText("AI Total Misses #: "+var.hitsAndMisses(4));
				shipLeftEnemy.setText("AI Ships Left #: "+5);
    			break;
    		}
    		
    		seperation.setText("Attack Your Enemy -->");
    		
    		for(int row = 0; row < myBoardGUI.length; row++) {
    			for(int column = 0; column < myBoardGUI[row].length; column++) {
    				myBoardGUI[row][column].removeActionListener(this);
    			}
    		}
            return true;
            


        }else {
            System.out.println("Not all ships are placed properly");
            return false;
        }
    }
	
	
	
	
	public battleshipProject(Scanner scan) throws IOException{
		
		var.setDifficulty(mode);//set difficulty	
		
		//call the method to generate an AI battleship Placement or import from file
		String ans = "";
		while(!ans.equals("1") && !ans.equals("2")) {
			System.out.print("Please select the method for AI battleship placement. \n"+"Enter 1 to let AI place battleships itself; Enter 2 to read from file(select a placement for AI): ");
			ans = scan.nextLine();
			if (ans.equals("1")) {
				AIBattleshipPlacement();
				
				
			} else if (ans.equals("2")) {
				boolean fileFound = true;
				do {
					try {
						
						System.out.print("Please enter the name of the file: ");
						ans = scan.nextLine();
						
						File file = new File(ans);
						Scanner sc = new Scanner(file);
						
						for (int i = 1; i <= enemyBoard.length; i++) {
							for (int j = 1; j <= enemyBoard[0].length;j++)  {
								enemyBoard[i-1][j-1] = sc.next();
							}
						}
						sc.close();
						fileFound = true;
					} catch(FileNotFoundException e) {
						System.out.println("Please enter a valid file name.");
						fileFound = false;
					}
				} while (!fileFound);
				ans = "2";
			} else {
				System.out.println("Invalid input, please enter again: ");
			}
			
		} 
		System.out.println("Proceeding...\n" + "__________________________________________________________________________________________________________________________________________________________________");
		
		
		setTitle("Battleship Playboard");
		setSize(1920,1080); //The program would work the best on PC with 1920x1080 displays
		
		//initialization for unusedShip
		unusedShip.add(Carrier);
		unusedShip.add(Battleship);
		unusedShip.add(Cruiser);
		unusedShip.add(Submarine);
		unusedShip.add(Destroyer);
		//for unusedShipBackup
		unusedShipBackup.add(Carrier);
		unusedShipBackup.add(Battleship);
		unusedShipBackup.add(Cruiser);
		unusedShipBackup.add(Submarine);
		unusedShipBackup.add(Destroyer);
		
		//setup panels
		
		
		//super-board panel
		JPanel superBoard = new JPanel();
		superBoard.setPreferredSize(new Dimension (1400,600));
		
		
		//panel--play board
		JPanel playBoard = new JPanel();
		playBoard.setPreferredSize(new Dimension(600,600));
		GridLayout boardLayout = new GridLayout(11,11);
		playBoard.setLayout(boardLayout);//11x11
		superBoard.add(playBoard);
		
		seperation = new JLabel( "  <-- Place Your Ship");
		seperation.setPreferredSize(new Dimension(130, 450));
		superBoard.add(seperation);
		
		
		//panel--enemy play board
		JPanel enemyPlayBoard = new JPanel();
		enemyPlayBoard.setPreferredSize(new Dimension(600,600));
		enemyPlayBoard.setLayout(boardLayout);//11x11
		superBoard.add(enemyPlayBoard);
		
		
		FlowLayout normalLayout = new FlowLayout();
		superBoard.setLayout(normalLayout);
		add(superBoard);
		
		
		//add display JLabel
		
		display.setFont(new Font("Serif", Font.PLAIN, 25));
		display.setPreferredSize(new Dimension(500,50));
		add(display);
		
		
		//panel--buttons: used to display functional buttons
		JPanel buttonPanel = new JPanel();
		//set size
				
		buttonPanel.setLayout(normalLayout);
		add(buttonPanel);
		
		//panel--screen(show hit and miss):
		JPanel screenPanel = new JPanel();
		screenPanel.setLayout(normalLayout);
		add(screenPanel);
		
		//add 2 sub panels:
		GridLayout displayLayout = new GridLayout(3,3);
		
		JPanel playerScreen = new JPanel();
		playerScreen.setLayout(displayLayout);
		playerScreen.setPreferredSize(new Dimension(600,80));
		screenPanel.add(playerScreen);
		
		JPanel enemyScreen = new JPanel();
		enemyScreen.setLayout(displayLayout);
		enemyScreen.setPreferredSize(new Dimension(600,80));
		screenPanel.add(enemyScreen);
		
		//add labels
		JLabel playerIndicate = new JLabel("Player");
		playerScreen.add(playerIndicate);
		playerScreen.add(totalAttackPlayer);
		playerScreen.add(totalHitPlayer);
		playerScreen.add(totalMissPlayer);
		playerScreen.add(shipLeftPlayer);
		
		JLabel enemyIndicate = new JLabel("AI");
		enemyScreen.add(enemyIndicate);
		enemyScreen.add(totalAttackEnemy);
		enemyScreen.add(totalHitEnemy);
		enemyScreen.add(totalMissEnemy);
		enemyScreen.add(shipLeftEnemy);
		
		
		
		//setup buttons
		//buttons for my board
		for(int row = 0; row < myBoard.length; row++) {//initialize the player board array
			for(int column = 0; column<myBoard[row].length; column ++) {
				
				myBoard[row][column] = "blank";
			}
		}
		
		//elements in button panel:
		//begin with lower case
		buttonPanel.add(carrierBut);
		buttonPanel.add(battleshipBut);
		buttonPanel.add(cruiserBut);
		buttonPanel.add(submarineBut);
		buttonPanel.add(destroyerBut);
		buttonPanel.add(vertical);
		buttonPanel.add(horizontal);
		buttonPanel.add(confirmAShip);
		buttonPanel.add(removeShip);
		buttonPanel.add(startBattle);
		
		//actionLister
		carrierBut.addActionListener(this);
		battleshipBut.addActionListener(this);
		cruiserBut.addActionListener(this);
		submarineBut.addActionListener(this);
		destroyerBut.addActionListener(this);
		horizontal.addActionListener(this);
		vertical.addActionListener(this);
		confirmAShip.addActionListener(this);
		removeShip.addActionListener(this);
		startBattle.addActionListener(this);
		
		
		
		//add to player board
		JButton[][] myBoardGUICopy = convertGUI(myBoard);//convert myBoard from String array to GUI
		
		
		for(int row = 0; row < myBoard.length; row++) {//convert the copy into the actual one
			for(int column = 0; column<myBoard[row].length; column ++) {
				
				myBoardGUI[row][column] = myBoardGUICopy[row][column];
				
			}
		}
		
		
		JLabel myLabel = new JLabel("Player");
		playBoard.add(myLabel);
		
		for(int i = 0; i < 10; i++) {
			JLabel columnLabel = new JLabel("    " + Integer.toString(i+1) + "  ");
			playBoard.add(columnLabel);
		}
		
		
		int charInitialIntMy = 97;
		for(int row = 0; row < myBoard.length; row++) {//initialize the player board array
			char rowAlpha = (char)charInitialIntMy;
			JLabel rowLabel = new JLabel(Character.toString(rowAlpha));
			playBoard.add(rowLabel);
			charInitialIntMy ++;
			
			for(int column = 0; column<myBoard[row].length; column ++) {
				
				playBoard.add(myBoardGUI[row][column]);
				myBoardGUI[row][column].addActionListener(this);
			}
		}
		
		
		
		
		
	
		//add to enemy board layer--to cover the real of enemyBoard, the player cannot see actual enemy board
		
		JLabel enemyLabel = new JLabel("A.I.");
		enemyPlayBoard.add(enemyLabel);
		
		for(int i = 0; i < 10; i++) {
			JLabel columnLabel = new JLabel("    " + Integer.toString(i+1) + "  ");
			enemyPlayBoard.add(columnLabel);
		}
		
		int charInitialIntEnemy = 97;
		for(int row = 0; row < enemyBoardLayerGUI.length;row++) {
			
			char rowAlpha = (char)charInitialIntEnemy;
			JLabel rowLabel = new JLabel(Character.toString(rowAlpha));
			enemyPlayBoard.add(rowLabel);
			charInitialIntEnemy ++;
			for(int column = 0; column< enemyBoardLayerGUI[row].length;column++){
				enemyBoardLayerGUI[row][column] = new JButton("");
				enemyBoardLayerGUI[row][column].setPreferredSize(new Dimension(60,60));
				enemyBoardLayerGUI[row][column].setOpaque(true);
				enemyBoardLayerGUI[row][column].setFont(new Font("Serif",Font.PLAIN, 12));
				
				enemyPlayBoard.add(enemyBoardLayerGUI[row][column]);
				enemyBoardLayerGUI[row][column].addActionListener(this);
			}
		}
		
		
		
		//set up JFrame
		setLayout(normalLayout);
		setVisible(true);
		
	}
	
	
	
	
	//when clicked:
	public void actionPerformed(ActionEvent event) {
		
		JButton clickedButton = (JButton) event.getSource();
		String text = event.getActionCommand();
		
		
		
		if(!startTheBattle) {
			System.out.println(text);
			
			int index2D = buttonIndex(clickedButton, myBoardGUI);//
			
			if  (text.equals("Carrier: 5 holes")) {					
				chosenShip[0] = Carrier;//change its value to the chosen ship this time --> to prepare for the base ship location
				basePlacementMethodClicked = true;
				
			}else if(text.equals("Battleship: 4 holes")){
				chosenShip[0] = Battleship;
				basePlacementMethodClicked = true;
			}else if(text.equals("Cruiser: 3 holes")) {
				chosenShip[0] = Cruiser;
				basePlacementMethodClicked = true;
			}else if(text.equals("Submarine: 3 holes")) {
				chosenShip[0] = Submarine;
				basePlacementMethodClicked = true;
			}else if (text.equals("Destroyer: 2 holes")) {
				chosenShip[0] = Destroyer;
				basePlacementMethodClicked = true;
			//above: click ship base placement
			//if clicked any other buttons, the unconfirmed buttons will be removed
			
			
			
			}else if(text.equals("Vertical")) {

				//Vertical direction place
				
				//delete all gray(unconfirmed buttons)
				String removedName = "";
				checkUnconfirmedShip (myBoardGUI, grayButtonsIndex);//check unconfirmed buttons
				for(int i = 0; i < grayButtonsIndex.size();i++) {
					int index = grayButtonsIndex.get(i);
					int rowGray = index/10;
					int columnGray = index%10;
					removedName = myBoardGUI[rowGray][columnGray].getText();
					myBoardGUI[rowGray][columnGray].setText("");
					myBoardGUI[rowGray][columnGray].setBackground(Color.white);//remove the base location
					
				}
				//unusedShip add the ship again, from unusedShipBackup
				unusedShip.clear();//clear it self
				for(int i = 0; i < unusedShipBackup.size();i++) {//check 1 by 1, to see if there is a difference
					//reload
					unusedShip.add(unusedShipBackup.get(i));//let the unusedShip be equal to the back up unusedShip
					//back up will be used only when the final location is confirmed
				}
				
				int lengthOfShip = 0;
				for(int i = 0; i < ships.length;i++) {
					if(removedName.equals(ships[i].getBattleshipName())) {
						lengthOfShip = ships[i].getBattleshipHealth();//the length
					}
				}
				
				//start rotate the ??? battleship needs to update
				//use previousBaseLocation
				//grayButtonsIndex.size() is actually the length of the ship
				int rowPreviousLocation = previousBaseLocation[0]/10;
				int columnPreviousLocation = previousBaseLocation[0]%10;
				if(rowPreviousLocation+1-lengthOfShip < 0) {//row: place it Vertically up, out of boundary
					for(int i = rowPreviousLocation; i>=0; i-- ) {
						myBoardGUI[i][columnPreviousLocation].setBackground(Color.gray);//gray color
						myBoardGUI[i][columnPreviousLocation].setText(removedName);
						myBoardGUI[i][columnPreviousLocation].setForeground(Color.orange);
					}
					inBoundary[0] = false;
				}else{//in the range
					for(int i = rowPreviousLocation; i>rowPreviousLocation-lengthOfShip; i-- ) {
						myBoardGUI[i][columnPreviousLocation].setBackground(Color.gray);//gray color
						myBoardGUI[i][columnPreviousLocation].setText(removedName);
						myBoardGUI[i][columnPreviousLocation].setForeground(Color.orange);
					}
					inBoundary[0] = true;
				}
				
				previousDirection[0] = "Vertical";//add to previous dir
				
			}else if(text.equals("Horizontal")) {
                String removedName = "";
                //Horizontal direction place

                
                //delete all gray(unconfirmed buttons)
                checkUnconfirmedShip (myBoardGUI, grayButtonsIndex);//check unconfirmed buttons
                for(int i = 0; i < grayButtonsIndex.size();i++) {
                    int index = grayButtonsIndex.get(i);
                    int rowGray = index/10;
                    int columnGray = index%10;
                    removedName = myBoardGUI[rowGray][columnGray].getText();
                    myBoardGUI[rowGray][columnGray].setText("");
                    myBoardGUI[rowGray][columnGray].setBackground(Color.white);//remove the base location
                    
                }
                //unusedShip add the ship again, from unusedShipBackup
                unusedShip.clear();//clear it self
                for(int i = 0; i < unusedShipBackup.size();i++) {//check 1 by 1, to see if there is a difference
                    
                    unusedShip.add(unusedShipBackup.get(i));//let the unusedShip be equal to the back up unusedShip
                    //back up will be used only when the final location is confirmed
                }
                
                int lengthOfShip = 0;
                for(int i = 0; i < ships.length;i++) {
                    if(removedName.equals(ships[i].getBattleshipName())) {
                        lengthOfShip = ships[i].getBattleshipHealth();//the length
                    }
                }
                
                if(playerCheckSpotHorizontal(previousBaseLocation[0], lengthOfShip, myBoardGUI)) {
                    
                    
                    //start rotate the battleship needs to update
                    //use previousBaseLocation
                    //grayButtonsIndex.size() is actually the length of the ship
                    int rowPreviousLocation = previousBaseLocation[0]/10;
                    int columnPreviousLocation = previousBaseLocation[0]%10;
                    
                    if(columnPreviousLocation-1+lengthOfShip > 9) {//column: place it Vertically right, out of boundary
                        for(int i = columnPreviousLocation; i<=9; i++ ) {
                            myBoardGUI[rowPreviousLocation][i].setBackground(Color.gray);//gray color
                            myBoardGUI[rowPreviousLocation][i].setText(removedName);
                            myBoardGUI[rowPreviousLocation][i].setForeground(Color.orange);
                        }
                        
                        inBoundary[0] = false;
                    }else{//in the range
                        for(int i = columnPreviousLocation; i<columnPreviousLocation+lengthOfShip; i++ ) {
                            myBoardGUI[rowPreviousLocation][i].setBackground(Color.gray);//gray color
                            myBoardGUI[rowPreviousLocation][i].setText(removedName);
                            myBoardGUI[rowPreviousLocation][i].setForeground(Color.orange);
                        }
                        inBoundary[0] = true;
                    }
                    
                    previousDirection[0] = "Horizontal";//add to previous dir
                }else {
                    System.out.println("please choose a proper location for your ship");
                }
			}else if(text.equals("confirm this location")) {
				confirmButtonLocation(myBoardGUI, previousDirection, inBoundary, unusedShipBackup);
			}else if(text.equals("remove a confirmed ship")) {
				display.setText("Please enter the ship you want to remove in the console");
				removeButtons(myBoardGUI, unusedShip, unusedShipBackup, Carrier, Battleship, Cruiser, Submarine, Destroyer);
				display.setText("You removed the ship");
			}else if(text.equals("start the battle")) {
				startTheBattle = startBattle(myBoardGUI);
				//
			}
			
			
			
			if  ((basePlacementMethodClicked == true)&&(index2D >= 0)) {//only work when the placement button is already chosen

				display.setText("You placed the ship \"" + chosenShip[0].getBattleshipName() + "\"");
                if (playerCheckSpotVertical(index2D, chosenShip[0].getBattleshipHealth(), myBoardGUI)) {
                    checkUnconfirmedShip (myBoardGUI, grayButtonsIndex);//check unconfirmed buttons
                    for(int i = 0; i < grayButtonsIndex.size();i++) {
                        int index = grayButtonsIndex.get(i);
                        int rowGray = index/10;
                        int columnGray = index%10;
                        myBoardGUI[rowGray][columnGray].setText("");
                        myBoardGUI[rowGray][columnGray].setBackground(Color.white);//remove the base location

                    }
                    //unusedShip add the ship again, from unusedShipBackup
                    unusedShip.clear();//clear it self
                    for(int i = 0; i < unusedShipBackup.size();i++) {//check 1 by 1, to see if there is a difference

                        unusedShip.add(unusedShipBackup.get(i));//let the unusedShip be equal to the back up unusedShip
                        //back up will be used only when the final location is confirmed
                    }


                    PlayerBattleshipBaseLocation(chosenShip, unusedShip, index2D, myBoardGUI, inBoundary);
                    basePlacementMethodClicked = false;

                    previousBaseLocation[0] = index2D;//store this index2D in an arraylist to store the location
                    System.out.println(previousBaseLocation[0]);
                }else {
                	display.setText("Please enter a proper location");
                    System.out.println("Please enter a proper location");
                }

            }
			
			
		}else {
			
			//start battle
			
			
			int[] indexArr = findButton(clickedButton, enemyBoardLayerGUI);
			String attackedName = enemyBoard[indexArr[0]][indexArr[1]];
			if(attackedName.equals("field")) {
				attackedName = "blank";
			}
			display.setText("you just hit \""+attackedName+"\"");
			//player attack first
			
			if (playerTotalHealth != 0 && enemyTotalHealth !=0) {
				//Player attacks
				actionPerformed(clickedButton, enemyBoardLayerGUI, enemyBoard);
				
				//get how many ship left:
				int playerShipLeft = 5;//initial 5, -1 if health is 0
				for(int i = 0; i < playerBattleShipHealths.length;i++) {
					if(playerBattleShipHealths[i]==0) {
						playerShipLeft--;
					}
				}
				
				totalAttackPlayer.setText("Player Total Attacks #: " + (var.hitsAndMisses(1) + var.hitsAndMisses(2)));
				totalHitPlayer.setText("Player Total Hits #: "+var.hitsAndMisses(1));
				totalMissPlayer.setText("Player Total Misses #: "+var.hitsAndMisses(2));
				shipLeftPlayer.setText("Player Ships Left #: "+playerShipLeft);
				
				
				if(playerTotalHealth == 0) {
					display.setText("AI wins! GG for the developers!");
					System.out.println("AI wins!");
					//Remove all actionlistener from the buttons
					for(int i = 1; i <= enemyBoardLayerGUI.length;i++) {
						for(int j = 1; j <= enemyBoardLayerGUI[0].length;j++) {
							enemyBoardLayerGUI[i-1][j-1].removeActionListener(this);
						}
					}
				} else if(enemyTotalHealth == 0) {
					display.setText("Player wins! GG for the great player!");
					System.out.println("Player wins!");
					//Remove all actionlistener from the buttons
					for(int i = 1; i <= enemyBoardLayerGUI.length;i++) {
						for(int j = 1; j <= enemyBoardLayerGUI[0].length;j++) {
							enemyBoardLayerGUI[i-1][j-1].removeActionListener(this);
						}
					}
				}
				
				
			} 
			
			if (playerTotalHealth != 0 && enemyTotalHealth !=0) {
				//AI attacks
				combatAI();
				
				//get how many ship left:
				int enemyShipLeft = 5;//initial 5, -1 if health is 0
				for(int i = 0; i < AIBattleShipHealths.length;i++) {
					if(AIBattleShipHealths[i]==0) {
						enemyShipLeft--;
					}
				}
				
				totalAttackEnemy.setText("AI Total Attacks #: " + (var.hitsAndMisses(3)+var.hitsAndMisses(4)));
				totalHitEnemy.setText("AI Total Hits #: "+var.hitsAndMisses(3));
				totalMissEnemy.setText("AI Total Misses #: "+var.hitsAndMisses(4));
				shipLeftEnemy.setText("AI Ships Left #: "+enemyShipLeft);
				
				if(playerTotalHealth == 0) {
					display.setText("AI wins! GG for the developers!");
					shipLeftPlayer.setText("ship left: 0");
					System.out.println("AI wins!");
					//Remove all actionlistener from the buttons
					for(int i = 1; i <= enemyBoardLayerGUI.length;i++) {
						for(int j = 1; j <= enemyBoardLayerGUI[0].length;j++) {
							enemyBoardLayerGUI[i-1][j-1].removeActionListener(this);
						}
					}
				} else if(enemyTotalHealth == 0) {
					display.setText("Player wins! GG for the great player!");
					shipLeftPlayer.setText("ship left: 0");
					System.out.println("Player wins!");
					//Remove all actionlistener from the buttons
					for(int i = 1; i <= enemyBoardLayerGUI.length;i++) {
						for(int j = 1; j <= enemyBoardLayerGUI[0].length;j++) {
							enemyBoardLayerGUI[i-1][j-1].removeActionListener(this);
						}
					}
				}
			}

			
			//detect ship left:
			//get how many ship left:
			int playerShipLeft = 5;//initial 5, -1 if health is 0
			for(int i = 0; i < playerBattleShipHealths.length;i++) {
				if(playerBattleShipHealths[i]==0) {
					playerShipLeft--;
				}
			}
			
				
			int enemyShipLeft = 5;//initial 5, -1 if health is 0
			for(int i = 0; i < AIBattleShipHealths.length;i++) {
				if(AIBattleShipHealths[i]==0) {
					enemyShipLeft--;
				}
			}
			
			shipLeftPlayer.setText("ship left: "+playerShipLeft);
			shipLeftEnemy.setText("ship left: "+enemyShipLeft);
			
		}
			
	}
	
	
	
	//AI attacks! >:D
	//Made by Eddie Fan
	public void combatAI() {
		//AI attack next
		int atkRow = 0, atkColumn = 0;
		int[] tempavArr = new int[2];
		
		
		if (var.Difficulty().equals("simple")) { //Simple AI
			
			do { //Randomly select a square to attack
				
				atkRow = (int)(Math.random()*10);
				atkColumn = (int)(Math.random()*10);
			} while (myBoard[atkRow][atkColumn].equals("attacked") ||myBoard[atkRow][atkColumn].equals("destroyed"));
			
			if (myBoard[atkRow][atkColumn].equals("blank")) { //If the AI misses
				var.setHitsAndMisses(4);
				myBoard[atkRow][atkColumn] = "attacked";
				myBoardGUI[atkRow][atkColumn].setText("attacked");
				myBoardGUI[atkRow][atkColumn].setBackground(Color.green);
			} else { //If the AI hits a battleship
				var.setHitsAndMisses(3);
				int hitShip = Integer.parseInt(myBoard[atkRow][atkColumn]);
				playerBattleShipHealths[hitShip]--;
				playerTotalHealth--;
				myBoard[atkRow][atkColumn] = "destroyed";
				myBoardGUI[atkRow][atkColumn].setText("destroyed");
				myBoardGUI[atkRow][atkColumn].setBackground(Color.red);
				//The ship number # "Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer"
			}
			
			
			
		} else { //Advanced AI
			
			if (var.atkMode()) { //If the last move hit a battleship
				int rand = (int) (Math.random() * AIenemySquares.size());
				switch (var.atkDirection()) {
					case "U":
					case "D":
						while(AIenemySquares.get(rand)[2].equals("L") ||AIenemySquares.get(rand)[2].equals("R")) {
							rand = (int) (Math.random() * AIenemySquares.size());
						}
						break;
					case "L":
					case "R":
						while(AIenemySquares.get(rand)[2].equals("U") ||AIenemySquares.get(rand)[2].equals("D")) {
							rand = (int) (Math.random() * AIenemySquares.size());
						}
						break;
				}
				atkRow = Integer.parseInt(AIenemySquares.get(rand)[0]);
				atkColumn = Integer.parseInt(AIenemySquares.get(rand)[1]);
				var.detectTempAtkDir(AIenemySquares.get(rand)[2]);
			} else if (var.atkRandomizor() != 0) { //The first five random moves
				switch(var.atkRandomizor()) {
					case 5: //Top left corner
						atkRow = (int)(Math.random()*4) +1;
						atkColumn = (int)(Math.random()*3) +1;
						switch((atkRow+atkColumn)%2) {
							case 1:
								var.setLightOrDarkSquare("L");
								break;
							case 0:
								var.setLightOrDarkSquare("D");
								break;
						}
						break;
					case 4: //Bottom right corner
						atkColumn = (int)(Math.random()*3) +6;
						switch(var.lightOrDarkSquare()) {
						case "L":
							do {
								atkRow = (int)(Math.random()*4) +5;
							} while ((atkRow+atkColumn)%2 != 1);
							break;
						case "D":
							do {
								atkRow = (int)(Math.random()*4) +5;
							} while ((atkRow+atkColumn)%2 != 0);
							break;	
						}
						break;
					case 3: //The middle
						atkColumn = (int)(Math.random()*2) +4;
						switch(var.lightOrDarkSquare()) {
							case "L":
								do {
									atkRow = (int)(Math.random()*6) +2;
								} while ((atkRow+atkColumn)%2 != 1);
								break;
							case "D":
								do {
									atkRow = (int)(Math.random()*6) +2;
								} while ((atkRow+atkColumn)%2 != 0);
								break;	
						}
						break;

					case 2: //Bottom left corner
						atkColumn = (int)(Math.random()*3) +1;
						switch(var.lightOrDarkSquare()) {
						case "L":
							do {
								atkRow = (int)(Math.random()*4) +5;
							} while ((atkRow+atkColumn)%2 != 1);
							break;
						case "D":
							do {
								atkRow = (int)(Math.random()*4) +5;
							} while ((atkRow+atkColumn)%2 != 0);
							break;	
					}
					break;
					case 1: //Top right corner
						atkColumn = (int)(Math.random()*3) +6;
						switch(var.lightOrDarkSquare()) {
							case "L":
								do {
									atkRow = (int)(Math.random()*4) +1;
								} while ((atkRow+atkColumn)%2 != 1);
								break;
							case "D":
								do {
									atkRow = (int)(Math.random()*4) +1;
								} while ((atkRow+atkColumn)%2 != 0);
								break;	
						}
						break;
				}
				tempavArr[0] = atkRow;
				tempavArr[1] = atkColumn;
				AIavailableSquares.add(tempavArr);
				var.setRandomizor();
			} else if (AIavailableSquares.size() == 0) { //There is no avaliable moves
				System.out.println("Ran out of available moves, finding new squares....");
				switch(var.lightOrDarkSquare()) {
					case "L":
						do {
							atkRow = (int)(Math.random()*10);
							atkColumn = (int)(Math.random()*10);
						} while ((atkRow+atkColumn)%2 != 1 || myBoard[atkRow][atkColumn].equals("destroyed") || myBoard[atkRow][atkColumn].equals("attacked"));
						break;
					case "D":
						do {
							atkRow = (int)(Math.random()*10);
							atkColumn = (int)(Math.random()*10);
						} while ((atkRow+atkColumn)%2 != 0 || myBoard[atkRow][atkColumn].equals("destroyed") || myBoard[atkRow][atkColumn].equals("attacked"));
						break;	
				}
				tempavArr[0] = atkRow;
				tempavArr[1] = atkColumn;
				AIavailableSquares.add(tempavArr);
			} else { //In a normal scenario
				int rand = (int) (Math.random() * AIavailableSquares.size());
				atkRow = AIavailableSquares.get(rand)[0];
				atkColumn = AIavailableSquares.get(rand)[1];
			}
			
			
			//Remove atkRow atkColumn from AIavaliableSquares / AIenemySquares
			if (!var.atkMode()) {
				for (int i = 1; i <= AIavailableSquares.size(); i++) {
					if (AIavailableSquares.get(i-1)[0] == atkRow && AIavailableSquares.get(i-1)[1] == atkColumn) {
						AIavailableSquares.remove(i-1);
					}
				}
				
			} else {
				for (int i = 1; i <= AIenemySquares.size(); i++) {
					if (Integer.parseInt(AIenemySquares.get(i-1)[0]) == atkRow && Integer.parseInt(AIenemySquares.get(i-1)[1]) == atkColumn) {
						AIenemySquares.remove(i-1);
					}
				}
			}
			
			
			System.out.println("AI Attacks: Square: " + atkRow +" "+ atkColumn);
			//Start attacking
			if (myBoard[atkRow][atkColumn].equals("blank")) {
				
				var.setHitsAndMisses(4);
				myBoard[atkRow][atkColumn] = "attacked";
				myBoardGUI[atkRow][atkColumn].setText("O");
				myBoardGUI[atkRow][atkColumn].setForeground(Color.black);
				myBoardGUI[atkRow][atkColumn].setBackground(Color.green);
				
				if (!var.atkMode()) {
					//Check four diagonal square, if overlap, don't add
					tempavArr = checkAvailableSquares(atkRow, atkColumn, -1, -1, true);
					if (tempavArr[0] != -1 && tempavArr[1] != -1) {
						AIavailableSquares.add(tempavArr);
					}
					
					tempavArr = checkAvailableSquares(atkRow, atkColumn, -1, 1, true);
					if (tempavArr[0] != -1 && tempavArr[1] != -1) {
						AIavailableSquares.add(tempavArr);
					}
					
					tempavArr = checkAvailableSquares(atkRow, atkColumn, 1, -1, true);
					if (tempavArr[0] != -1 && tempavArr[1] != -1) {
						AIavailableSquares.add(tempavArr);
					}
					
					tempavArr = checkAvailableSquares(atkRow, atkColumn, 1, 1, true);
					if (tempavArr[0] != -1 && tempavArr[1] != -1) {
						AIavailableSquares.add(tempavArr);
					}
					
				}
				
				
			} else {

				if (myBoard[atkRow][atkColumn].equals(var.currentShip())) {
					for (int i = 1; i <= AIavailableSquares.size(); i++) {
						if (AIavailableSquares.get(i-1)[0] == atkRow && AIavailableSquares.get(i-1)[1] == atkColumn) {
							AIavailableSquares.remove(i-1);
						}
					}
					
					int hitShip = Integer.parseInt(myBoard[atkRow][atkColumn]);
					playerBattleShipHealths[hitShip]--;
					playerTotalHealth--;
					
					var.setHitsAndMisses(3);
					myBoard[atkRow][atkColumn] = "destroyed";
					myBoardGUI[atkRow][atkColumn].setText("X");
					myBoardGUI[atkRow][atkColumn].setForeground(Color.white);
					myBoardGUI[atkRow][atkColumn].setBackground(Color.red);
					
					System.out.println("Enemy Ship Health:" + playerBattleShipHealths[hitShip]);
					
					
					if(playerBattleShipHealths[hitShip] == 0) {
						var.changeAtkMode(false);
						var.detectAtkDirection("N");
						var.setCurrentShip("N");
						AIenemySquares.clear();

						System.out.println("Attack mode OFF");
					} else {
						
						if (var.atkDirection().equals("N")) {
							var.detectAtkDirection(var.tempAtkDir());
						}
						
						switch (var.atkDirection()) {
							case "L":
							case "R":
								//Check square left of / right of
								String[] tempaeArr1 = new String[3];
								String[] tempaeArr2 = new String[3];
								tempavArr = checkAvailableSquares(atkRow, atkColumn, 0, 1, false);
								if (tempavArr[0] != -1 && tempavArr[1] != -1) {
									tempaeArr1[0] = Integer.toString(tempavArr[0]);
									tempaeArr1[1] = Integer.toString(tempavArr[1]);
									tempaeArr1[2] = "R";
									AIenemySquares.add(tempaeArr1);
								}
								tempavArr = checkAvailableSquares(atkRow, atkColumn, 0, -1, false);
								if (tempavArr[0] != -1 && tempavArr[1] != -1) {
									tempaeArr2[0] = Integer.toString(tempavArr[0]);
									tempaeArr2[1] = Integer.toString(tempavArr[1]);
									tempaeArr2[2] = "L";
									AIenemySquares.add(tempaeArr2);
								}
								break;
							case "D":
							case "U":
								//Check square above / below
								String[] tempaeArr11 = new String[3];
								String[] tempaeArr21 = new String[3];
								tempavArr = checkAvailableSquares(atkRow, atkColumn, 1, 0, false);
								if (tempavArr[0] != -1 && tempavArr[1] != -1) {
									tempaeArr11[0] = Integer.toString(tempavArr[0]);
									tempaeArr11[1] = Integer.toString(tempavArr[1]);
									tempaeArr11[2] = "D";
									AIenemySquares.add(tempaeArr11);
								}
								tempavArr = checkAvailableSquares(atkRow, atkColumn, -1, 0, false);
								if (tempavArr[0] != -1 && tempavArr[1] != -1) {
									tempaeArr21[0] = Integer.toString(tempavArr[0]);
									tempaeArr21[1] = Integer.toString(tempavArr[1]);
									tempaeArr21[2] = "U";
									AIenemySquares.add(tempaeArr21);
								}
								break;
						}
						
					}
					
				} else {
					if (!var.atkMode()) {
						
						var.setCurrentShip(myBoard[atkRow][atkColumn]);
						int hitShip = Integer.parseInt(myBoard[atkRow][atkColumn]);
						playerBattleShipHealths[hitShip]--;
						playerTotalHealth--;
						
						var.setHitsAndMisses(3);
						myBoard[atkRow][atkColumn] = "destroyed";
						myBoardGUI[atkRow][atkColumn].setText("X");
						myBoardGUI[atkRow][atkColumn].setForeground(Color.white);
						myBoardGUI[atkRow][atkColumn].setBackground(Color.red);
						
						System.out.println("Enemy Ship Health:" + playerBattleShipHealths[hitShip]);
						
						var.changeAtkMode(true);
						
						System.out.println("Attack mode ON");
						String[] tempaeArr1 = new String[3];
						String[] tempaeArr2 = new String[3];
						String[] tempaeArr3 = new String[3];
						String[] tempaeArr4 = new String[3];
						//check four adjacent square
						tempavArr = checkAvailableSquares(atkRow, atkColumn, 1, 0, false);
						if (tempavArr[0] != -1 && tempavArr[1] != -1) {
							tempaeArr1[0] = Integer.toString(tempavArr[0]);
							tempaeArr1[1] = Integer.toString(tempavArr[1]);
							tempaeArr1[2] = "D";
							AIenemySquares.add(tempaeArr1);
						}
						tempavArr = checkAvailableSquares(atkRow, atkColumn, -1, 0, false);
						if (tempavArr[0] != -1 && tempavArr[1] != -1) {
							tempaeArr2[0] = Integer.toString(tempavArr[0]);
							tempaeArr2[1] = Integer.toString(tempavArr[1]);
							tempaeArr2[2] = "U";
							AIenemySquares.add(tempaeArr2);
						}
						tempavArr = checkAvailableSquares(atkRow, atkColumn, 0, 1, false);
						if (tempavArr[0] != -1 && tempavArr[1] != -1) {
							tempaeArr3[0] = Integer.toString(tempavArr[0]);
							tempaeArr3[1] = Integer.toString(tempavArr[1]);
							tempaeArr3[2] = "R";
							AIenemySquares.add(tempaeArr3);
						}
						tempavArr = checkAvailableSquares(atkRow, atkColumn, 0, -1, false);
						if (tempavArr[0] != -1 && tempavArr[1] != -1) {
							tempaeArr4[0] = Integer.toString(tempavArr[0]);
							tempaeArr4[1] = Integer.toString(tempavArr[1]);
							tempaeArr4[2] = "L";
							AIenemySquares.add(tempaeArr4);
						}
						
					} else {
						
						var.setHitsAndMisses(4);
						
					}
				}	

			}
		}
	}
	
	public static void main(String[] args) throws IOException{
		
		System.out.println("\n __________________________________________________________________________________________________________________________________________________________________\n"
                + "> ______________          _____       ______________   ______________   _____           ___________       ________     ___       ___     _________     _________   <\n"
                + "> __  _______   |        /     \\     | _____  _____ | | _____  _____ |  |    /         |   ________|    /   _____/    |   |     |   |    \\__   __/    |   ____  \\  <\n"
                + "> |   [      ]  |       /   / \\ \\    |/    |   |   \\| |/    |   |   \\|  |   |          |   [           |  |           |   |     |   |       | |       |  /    \\  | <\n"
                + "> |   [______]  /      /   /___\\ \\         |   |            |   |       |   |          |   [______     \\  \\______     |   |_____|   |       | |       |  [____]  | <\n"
                + "> |   _______  <      /    ______ \\        |   |            |   |       |   |          |   _______|     \\  ____  \\    |    _____    |       | |       |  _______/  <\n"
                + "> |   [      ]  \\    /   /       \\ \\       |   |            |   |       |   |          |   [                   \\  \\   |   |     |   |       | |       |  |         <\n"
                + ">_|   [______]  |   /   /         \\ \\      |   |            |   |       |   |_______   |   [_______     _______/  /   |   |     |   |     __| |__     |  |         <\n"
                + ">|______________|  /___/           \\_\\     |___|            |___|       |___________|  |___________|   /_________/    |___|     |___|    /_______\\    |__|         <\n"
                + ">                                                    _____      ____      _____     _____          __________                                                      <\n"
                + ">                                                    \\     \\   |    |   /     /    /     \\       |    _____  \\                                                     <\n"
                + ">             \\==============================\\        \\     \\  |    |  /     /    /   / \\ \\      |   [_____]  |         /==============================/           <\n"
                + ">              \\______________________________\\        \\     \\ |    | /     /    /   /___\\ \\     |    __   ___/        /______________________________/            <\n"
                + ">                      \\_______________________\\        \\     \\|    |/     /    /    ______ \\    |   |  \\  \\          /_______________________/                    <\n"
                + ">                               \\               \\        \\                /    /   /       \\ \\   |   |   \\  \\_       /               /                             <\n"
                + ">                                \\_______________\\        \\______/\\______/    /___/         \\_\\  |___|    \\____\\    /_______________/                              <\n"
                + " __________________________________________________________________________________________________________________________________________________________________");
		System.out.print("\nEnter 'r' to read the rules; Enter 's' to start the game: ");
		Scanner sc = new Scanner(System.in);
		String input = "";
		while (!input.equals("s")) {
			input = sc.nextLine();
			if (input.equals("r")) {
				//print out the rules
				System.out.println("Player and computer hides ships on a 10x10 grid containing vertical and horizontal space coordinates. \n"
						+ "Each take turns attacking row and column coordinates on the opponent's grid in an attempt to identify a square that contains a ship.\n"
						+ "once all the ship of one side are all been attacked, meaning he've lost all the health, which loses the game, and relatively, the ones who lasts longer would be the winner.\n"
						+ "\nIn the GUI, please click on the ship you want to place and then choose the spot on GUI by clicking the buttons, use 'horizontal' and 'vertical' button to change directions\n"
						+ "after you've decide your palcement, please click 'confirm this location' to comfirm this placement"
						+ "\nIf you want to remove a placed ship, please click the 'remove' button and follow the instruction on the console to remove the ship.\n"
						+ "\nEnter 's' to start the game");
			}else if (input.equals("s")) {
				//start the game
				System.out.print("Please enter your name: ");
				String name = sc.nextLine();
				System.out.print("Hi " + name + ", please select the difficulty of the AI. Enter 1 for simple AI; Enter 2 for advanced AI: ");
				String input2 = "0";
				while (!input2.equals("1")&&!input2.equals("2")) {
					input2 = sc.nextLine();
					if (input2.equals("1")) {
						System.out.println("You have selected simple AI");
						mode = "simple";
						break;
						
					}else if (input2.equals("2")) {
						System.out.println("You have selected advanced AI");
						mode = "advanced";
						break;
					}else {
						System.out.print("Invalid input, please re-Enter: ");
						 
					}
				}
				
			}else {
					System.out.print("Invalid input, please Re-Enter: ");
				
			}
			
		}
		System.out.println("Proceeding...\n" + "__________________________________________________________________________________________________________________________________________________________________");
		
		@SuppressWarnings("unused")
		battleshipProject cl = new battleshipProject(sc);
	
		
	}
	
}