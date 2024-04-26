// Nathan Hicks
// COP 3503
// P2 Hexagram
// Permutes all possible locations that input of 12 numbers can be placed so that each side of hexagram adds up to same sum

//Import
import java.util.Scanner;

public class hexagram {
	
	//Global variables
	public static int res; //Result
	public static int magicSum; //Number each side of hexagram sums to
    public static int[] puzzle = new int[12]; //Holds the input
    public static boolean[] used = new boolean[12]; //Determines if number has been used yet
    public static int[] star = new int[24]; //Array that forms the star itself

	public static void main(String[] args) {
		
		//Reads in 12 numbers as input
		Scanner stdin = new Scanner(System.in);
		
		for (int i=0; i<12; i++) puzzle[i] = stdin.nextInt();
		
		
		//Loops until 12 numbers are zero
		while (!zero(puzzle)) {
			
			//Calculates magic sum and resets result to 0
			magicSum = magicCalc(puzzle);
			res = 0;
			
			//If there is no magic sum print 0 and dont attempt to solve
			if (magicSum == -1) {
				System.out.println(0);
				
			//Else solve the hexagram and print result divided by 12 to account for rotations and reflections
			} else {
				solve(0);
				System.out.println(res / 12);
			}
			
			
			//Reads in next set of input
			for (int i=0; i<12; i++) puzzle[i] = stdin.nextInt();
		}
		
		//Close the reader
		stdin.close();

	}

	//Methods
	
	//Recursviely solves the hexagram
	public static void solve(int k) {
	
        // We've reached the end of array, done
        if (k == 24) return;  

        // Fixed point, go to next.
        if (star[k] != 0) {
        	solve(k+1);
        	return;
        }

        // Try each number in this spot.
        for (int i=0; i<12; i++) {
        	//Only try if haven't used yet
        	if (!used[i]) {
        		used[i] = true;
        		
        		//Places number in correct points around hexagram
        		kChecker(k, puzzle[i]);
        		
        		//Checks if the current hexagram is even valid
        		if (isConsistent()) 
        			solve(k+1);
                
        		//Sets spot back to zero
        		kChecker(k, 0);
        		used[i] = false;
        	}
        }

        //No solution found so return
        return;
    }
    
	//Places number pairs in correct location around hexagram
	private static void kChecker(int k, int i) {
		switch (k) {
		
		case 0: case 12: 
			star[k] = i;
			star[k+11] = i;
			break;
		case 11: case 23: 
			star[k] = i;
			star[k-11] = i;
			break;
		case 3: case 7:  case 15: case 19:
			star[k] = i;
			star[k+1] = i;
			break;
		case 4: case 8:  case 16: case 20:
			star[k] = i;
			star[k-1] = i;
			break;
		case 1: case 14:
			star[1] = i;
			star[14] = i;
			break;
		case 2: case 17:
			star[2] = i;
			star[17] = i;
			break;
		case 5: case 18:
			star[5] = i;
			star[18] = i;
			break;
		case 6: case 21:
			star[6] = i;
			star[21] = i;
			break;
		case 9: case 22:
			star[9] = i;
			star[22] = i;
			break;
		case 10: case 13:
			star[10] = i;
			star[13] = i;
			break;
		
		default:
			star[k] = i;
			break;
		}
		
	}

	//Checks if the current hexagram is valid
	private static boolean isConsistent() {
		
		for (int i = 0; i < 24; i += 4) {
			//If current leg is unfinished return true to continue the leg
			if (star[i] == 0 || star[i + 1] == 0 || star[i + 2] == 0 || star[i + 3] == 0) 
				return true;
				
			//If current leg does not equal magic sum, the return false to back out and try different number
			else if (star[i] + star[i + 1] + star[i + 2] + star[i + 3] != magicSum) 
				return false;	
		}
		
		//Reaching this point means all 6 legs summed to correct value, so increase result
		res++;
		return true;
	}

	//Checks if a zero is contained in the input
	private static boolean zero(int[] puzzle) {
		
		for(int i : puzzle) 
			if (i == 0) return true;
		
		return false;
	}
	
	//Calculates magic sum
	private static int magicCalc(int[] puzzle) {
		
		int sum = 0;
		
		//Sums are numbers in input
		for(int i : puzzle) 
			sum += i;
		
		//If its divisible by 3, return sum divided by 3, else return -1 to mark an unsolvable hexagram
		if (sum % 3 == 0) return sum / 3;
		else return -1;
	}

}
