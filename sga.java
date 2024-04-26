/*
 * Name:			Nathan Hicks
 * Professor:		Arup Guha
 * Assignment:		Homework #1: SGA President
 * Purpose:
 * Given the names of each UCF student, calculates the number of possible President/Vice-President
 * pairs who have a potential to win the SGA election
 */

// Imports
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class sga {

	public static void main(String[] args) {
		
		// Input Reader
		Scanner inputRead = new Scanner(System.in);
		
		int studentNum = inputRead.nextInt(); // Number of students being entered
		
		
		// Creates two hash maps, one for holding student names and one for holding first inititals
		Map<String, Integer> studentMap = new HashMap<>();
		Map<Character, Integer> firstLetters = new HashMap<>(35);
		
		
		// Loops for every student entered
		for (int i = 0; i < studentNum; i++) {
			
			// Name being entered
			String temp = new String(inputRead.next());
			
			// Adds first occurance of intital to map and counts number of times initial appears
			if (firstLetters.containsKey(temp.charAt(0))) {
				firstLetters.put(temp.charAt(0), firstLetters.get(temp.charAt(0)) + 1);
			} else {
				firstLetters.put(temp.charAt(0), 1);
			}
			
			// Adds first occurance of name to map and counts number of times the name appears
			if (studentMap.containsKey(temp)) {
				studentMap.put(temp, studentMap.get(temp) + 1);				
			} else {
				studentMap.put(temp, 1);
			}
		}
		
		inputRead.close(); // Close the input reader
		
		long total = 0; // number of prez/vice-prez combos
		
		// Calculates total by looping through student map
		for (String key : studentMap.keySet()) {
            total += studentMap.get(key) * ( firstLetters.get(key.charAt(0)) -  studentMap.get(key));
        }
		
		// Prints the total
		System.out.println(total);
		
	}

}
