/*
 * Nathan Hicks
 * COP 3503
 * RP1 Dyslectionary
 * Sorts words from back to front and right adjusts them
 */

//Imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class dyslectionary {

	public static void main(String[] args) {
		
		Scanner input = new Scanner(System.in); //Input scanner
		
		String curr; //Current word being scanned in
		
		ArrayList<DysWord> mainList = new ArrayList<DysWord>(); //ArrayList for holding custom class Dysword
		
		//Main loop that goes until no more input
		while (input.hasNext()) {
			
			int longestWord = 0; //Longest word ecountered in current group
					
			//Loops until an exit condition within loop has been met
			while (true) {
				
				//Scans in next line, exits loop if empty
				curr = input.nextLine();
				
				if(curr.isEmpty()) break;
				
				//Add new word to array list and break loop if next line in blank
				mainList.add(new DysWord(curr));
				
				if (curr.length() > longestWord) longestWord = curr.length();
				
				if (!input.hasNext()) break;

			}
			
			//Sort the array list
			Collections.sort(mainList);
			
			//Print list, right justifying with spaces based off longest word
			for (DysWord d : mainList) {
				for (int i = 0; i < (longestWord - d.word.length()); i++)
		            System.out.print(" ");
				
				System.out.println(d.word);
			}
			
			//Wont print extra line if this is the last word group
			if (input.hasNext())
				System.out.println();
			
			mainList.clear(); //Clear array list
		}
		
		//Close input scanner and exit program
		input.close();

	}

}

//Custom class that uses comparable to be able to sort from back to front
class DysWord implements Comparable<DysWord> {

	String word; //The actual word
	
	//Simple Constructor
	public DysWord(String word) {
		super();
		this.word = word;
	}

	// Comparable Method: reverses string to sort from back to front easier
	public int compareTo(DysWord o) {
		
		StringBuilder word1 = new StringBuilder();
		StringBuilder word2 = new StringBuilder();

        word1.append(this.word);
        word2.append(o.word);
 
        word1.reverse();
        word2.reverse();
        
        return word1.compareTo(word2);
	}
	
}
