package com.cognixia.corejava.miniprojects.SeatReservation;

import java.util.Scanner;
import java.io.*;
import java.util.regex.*;

public class SeatReservation {
	private static int maxRow = 5, maxCol = 5;
	private static String[][] reservations;
	private static final String FILE_PATH = "resources/savefile.txt";
	private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]+(-|\s)?[a-zA-Z]*?$");

	public static void main(String[] args) {
		int optionSelect = -1;
		int row, col, row1, col1;
		int[] coord;
		String name;
		String menu = "\n      MENU      \n"
			    + "=================\n"
			    + "1: Reserve a seat\n"
			    + "2: Move your current seat\n"
			    + "3: Delete your reservation\n"
			    + "4: Print current reservations\n"
			    + "0: Exit Program\n"
			    + "Input: ";
		Scanner input = new Scanner(System.in);
		
		initSystem(input);
		
		while (optionSelect != 0) {
			System.out.print(menu);
			optionSelect = input.nextInt();
			
			switch(optionSelect) {
			
			case 1:
				System.out.println(printMap());
				System.out.println("Which seat would you like to reserve?");
				coord = getSeatInfo(input);
				System.out.print("Reservation name: ");
				input.nextLine(); //Negate the \n character from reading seat information.
				name = input.nextLine();
				row = coord[0];
				col = coord[1];
				
				if (!validateSeat(row, col)) {
					System.out.println("That seat does not exist.");
				} else if (!validateName(name)) {
					System.out.println("Name is invalid.");
				} else if (reservations[row - 1][col - 1] != null) {
					System.out.println("Seat is already reserved.");
				} else {				
					reservations[row - 1][col - 1] = name;
					System.out.println("Seat reservation successful!");
				}
				break;
				
			case 2:
				System.out.println("Where are you currently seated?");
				coord = getSeatInfo(input);
				row = coord[0];
				col = coord[1];
				
				if (!validateSeat(row, col)) {
					System.out.println("That seat does not exist.");
				} else if (reservations[row - 1][col - 1] == null) {
					System.out.println("No reservation found at that seat.");
				} else {
					System.out.println("Where do you want to move your seat to?");
					coord = getSeatInfo(input);
					row1 = coord[0];
					col1 = coord[1];
					
					if (!validateSeat(row1, col1)) {
						System.out.println("That seat does not exist.");
					}else if (reservations[row1 - 1][col1 - 1] != null) {
						System.out.println("Seat is already reserved.");
					} else {
						reservations[row1 - 1][col1 - 1] = reservations[row - 1][col - 1];
						reservations[row - 1][col - 1] = null;
						System.out.println("Your seat has been moved!");
					}
				}
				break;
				
			case 3:
				System.out.println("Where are you currently seated?");
				coord = getSeatInfo(input);
				row = coord[0];
				col = coord[1];
				
				if (!validateSeat(row, col)) {
					System.out.println("That seat does not exist.");
				}else if (reservations[row - 1][col - 1] == null) {
					System.out.println("No reservation found at that seat.");
				} else {
					reservations[row - 1][col - 1] = null;
					System.out.println("Your reservation has now been cancelled.");
				}
				break;
				
			case 4:
				System.out.println(printMap());
				System.out.println(printReservations());
				break;
				
			case 0:
				saveFile();
				break;
			}
		}
		
		input.close();
	}
	
	//Uses the given scanner to read user input to get seat information.
	private static int[] getSeatInfo(Scanner in) {
		int[] coord = new int[2];
		System.out.print("Row: ");
		coord[0] = in.nextInt();
		System.out.print("Column: ");
		coord[1] = in.nextInt();
		return coord;
	}
	
	//Compiles the seat information for unoccupied and occupied seats and returns that compiled information.
	private static String printMap() {
		String map = "\n         Map\n===================\n   ";
		
		for (int i = 0; i < maxCol; i++) {
			map += " " + (i + 1) + " ";
		}
		
		map += "\n   ";
		
		for (int i = 0; i < maxCol; i++) {
			map += "---";
		}
		
		map += "\n";
		
		for (int i = 0; i < maxRow; i++) {
			String row = String.valueOf(i + 1) + " |";
			for (int j = 0; j < maxCol; j++) {
				if (reservations[i][j] == null) {
					row += " o ";
				} else {
					row += " x ";
				}
			}
			map += row + "\n";
		}
		
		return map;
	}
	
	//Compiles the reservation information in the format of "[seat row, col]: reservation name" and returns the
	//compiled information.
	private static String printReservations() {
		String output = "\n  Reservations   \n"
					  + "==================\n";
		for (int i = 0; i < maxRow; i++) {
			for (int j = 0; j < maxCol; j++) {
				if (reservations[i][j] != null) {
					output += "[" + (i + 1) + ", " + (j + 1) + "]: " + reservations[i][j] + "\n";
				}
			}
		}
		
		return output;
	}
	
	//Checks to see if the seat information is within the bounds of the grid.
	private static boolean validateSeat(int row, int col) {
		return row > 0 && row <= maxRow && col > 0 && col <= maxCol;
	}
	
	//Checks to see if the given name matches the approved pattern.
	private static boolean validateName(String name) {
		return NAME_PATTERN.matcher(name).matches();
	}
	
	//Saves the current grid format and stores the reservation data into a grouped row format.
	private static void saveFile() {
		try {
			StringBuilder sb = new StringBuilder();
			BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH));
			sb.append(maxRow + "\n");
			sb.append(maxCol + "\n");
			
			for (int i = 0; i < maxRow; i++) {
				String row = "";
				
				for (int j = 0; j < maxCol; j++) {
					
					if (reservations[i][j] == null) {
						row += "-";
					} else {
						row += reservations[i][j];
					}
					
					if (j != maxCol - 1) {
						row += ",";
					}
				}	
				sb.append(row + "\n");
			}
			
			bw.write(sb.toString());
			bw.close();
			System.out.println("File saved.");
		} catch (IOException e) {
			System.out.println("File failed to save.");
		}
		
	}
	
	//Checks to see if a save file exists and loads the file if user permits, otherwise initializes data to default.
	private static void initSystem(Scanner in) {
		File save = new File(FILE_PATH);
		if (save.exists()) {
			String select = "";
			while (!select.equals("y") && !select.equals("Y") && !select.equals("n") && !select.equals("N")) {
				System.out.print("Save file found. Load (y/n): ");
				select = in.next();
			}
			
			if (select.equals("Y") || select.equals("y")) {
				
				try {
					Scanner scn = new Scanner(save);
					maxRow = scn.nextInt();
					maxCol = scn.nextInt();
					reservations = new String[maxRow][maxCol];
					scn.nextLine();
					for (int i = 0; i < maxRow; i++) {
						String[] row = scn.nextLine().split(",");
						int col = 0;
						for (String colEntry: row) {
							if (colEntry.equals("-")) {
								reservations[i][col] = null;
							} else {
								reservations[i][col] = colEntry;
							}
							col++;
						}
					}		
					scn.close();
				} catch (FileNotFoundException e) {
					reservations = new String[maxRow][maxCol];
				}
				
			} else {
				reservations = new String[maxRow][maxCol];
			}	
		} else {
			reservations = new String[maxRow][maxCol];
		}
		
	}

}
