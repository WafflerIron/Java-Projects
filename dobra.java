/* Nathan Hicks
 * COP 3503
 * RP2 Dobra
 * Read input of string with blanks, and print number of words that can be created
 * following certain rules
 */

//Imports
import java.util.ArrayList;
import java.util.Scanner;

public class dobra {
	
	//Global variables
	public static char[] arr; //Character array formed from input string
	public static int n; //Length of character array
	public static long res = 0; //Total number of pleasant words
	public static boolean usedL = false; //If L was found in initial string
	public static ArrayList<Integer> weights = new ArrayList<>(); //Weight array for how much each type of character contributes to total

	public static void main(String[] args) {
		
		//Scan in initial string and convert to character array
		Scanner in = new Scanner(System.in);
		
		arr = in.nextLine().toCharArray();
		n = arr.length;
		
		in.close();		
		
		//Check if input is valid and look for L, then find pleasant words
		if(valid()) go(0, usedL);
				
		//Print result
		System.out.println(res);
	}
	
	//Recursive permutation function for filling in blanks
	public static void go(int k, boolean putL) {
			
		boolean temp = putL; //True if L is currently contained in word
		
		//Reached end of word
		if (k == n) {
				//Check if final word is pleasant
				if (valid(k - 2) && (putL)) {

					//Calculate permutations based on weight and add to result
					long weight = 1;
					for (int j = 0; j < weights.size(); j++) weight *= weights.get(j);
					res += weight;
				}
			return;
		}
		
		//Only permute for blanks
		if (arr[k] != '_') {
			go(k+1, putL);
			return;
		}				
			//Try L
      		putL = true;			
        	arr[k] = 'L';   
        	
        	///////// BACKTRACKING \\\\\\\\\ Only try next blank if current word up to this point is pleasant
        	if (valid(k - 2)) 
        		go(k+1, putL);
        	
        	putL = temp;
        	
        	//Try a vowel and update weight to number of vowels
        	arr[k] = 'A';
        	weights.set(k, 5);
        	
        	///////// BACKTRACKING \\\\\\\\
        	if (valid(k - 2)) 
        		go(k+1, putL);
        	
        	//Try a consonant and update weight to number of consonants not counting L
        	arr[k] = 'B';
        	weights.set(k, 20);
        	
        	///////// BACKTRACKING \\\\\\\\
        	if (valid(k - 2)) 
        		go(k+1, putL);
        	        			
            //Reset to blank with weight of 1    
        	arr[k] = '_';
        	weights.set(k, 1);
        		        	
		//End function
        return;		
	}
	
	//Check if full string is valid and contains letter L
	public static boolean valid() {
		
		//Vowel and consonant counters
		int vowels = 0, cons = 0;
		
		//Loop through input string array
		for (int i = 0; i < arr.length; i++) {
			
			//Create weights of 1 for each character
			weights.add(1);
			char c = arr[i];
			
			//If vowel increment vowel counter and set consonant to 0
			if(c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U' ) {
				vowels++;
				cons = 0;
			//If L treat as consonant but set global usedL to true
			} else if (c == 'L') {
				cons++;
				vowels = 0;
				usedL = true;
			//If consonant increment cons counter and set vowel to 0
			} else if (c != '_') {
				cons++;
				vowels = 0;
			//if blank set both counters to 0
			} else {				
				vowels = 0;
				cons = 0;
			}
			//If 3 vowels or consonants in a row then not valid
			if (vowels == 3) return false;
			if (cons == 3) return false;	
		}
		//String is valid
		return true;
	}
	
	//Check if string is valid from starting point j
	public static boolean valid(int j) {
	
		//If starting point is less than 0 then set to 0
		if (j < 0) j = 0;

		//Same check as valid() but starting in array at point j and no looking for L
		int vowels = 0, cons = 0;
		
		for (int i = j; i < n; i++) {
				
			if(arr[i] == 'A' || arr[i] == 'E' || arr[i] == 'I' || arr[i] == 'O' || arr[i] == 'U' ) {
				vowels++;
				cons = 0;
			} else if (arr[i] != '_') {
				cons++;
				vowels = 0;
			} else {
				vowels = 0;
				cons = 0;
			}
			if (vowels == 3) return false;
			if (cons == 3) return false;	
		}
		return true;
	}
}
