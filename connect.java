/* Nathan Hicks
 * P3 Making Connections
 * COP 3503
 * Uses disjoint sets to connect a bunch of computers together and calulate their connectivity
 */

//Import
import java.util.*;

public class connect {
	
	public static class djset {
		
		//Array that holds which computers are head of their tree
		public int[] parent;

		// Creates a disjoint set of size n (0, 1, ..., n-1)
		public djset(int n) {
			parent = new int[n];
			for (int i = 0; i < n; i++)
				parent[i] = i;
		}

		//Finds root of tree containing v
		public int find(int v) {

			// I am the club president!!! (root of the tree)
			if (parent[v] == v)
				return v;

			// Find my parent's root.
			int res = find(parent[v]);

			// Attach me directly to the root of my tree.
			parent[v] = res;
			return res;
		}

		//Connects the trees of v1 and v2
		public boolean union(int v1, int v2) {

			// Find respective roots.
			int rootv1 = find(v1);
			int rootv2 = find(v2);

			// No union done, v1, v2 already together.
			if (rootv1 == rootv2)
				return false;

			// Attach tree of v2 to tree of v1.
			parent[rootv2] = rootv1;
			return true;
		}
	}

	//Main
	public static void main(String[] args) {

		//Scans in number of computers n and makes disjoint set of that size
		Scanner in = new Scanner(System.in);

		int n = in.nextInt();
		djset set = new djset(n);

		//Scan in number of commands and loop that many times
		int m = in.nextInt();
		for (int i = 0; i < m; i++) {

			//If command is 1 create new connections
			if (in.nextInt() == 1) {

				// Make v1 and v2 zero-based
				int v1 = in.nextInt() - 1;
				int v2 = in.nextInt() - 1;

				//Check if computers are in different trees or not, if not union them
				boolean res = set.union(v1, v2);

				// If union was successful then decrease number of sets by 1
				if (res) n--;

			//If command is 2 calculate connectivity
			} else {
				
				//Calculate sum of squares and gcd
				long sum = djCounter(set);
				long gcd = gcd(n, sum);

				//Print connectivity divided by gcd
				System.out.println(sum / gcd + "/" + n / gcd);
			}
		}

		//Close scanner and exit program
		in.close();
	}

	//Calculates the gcd of two numbers
	private static long gcd(long n, long sum) {
		
		//If sum=0, n is the GCD
		if (sum == 0)
			return n;
		
		//Recursively find gcd using Euclid's Algorithm
		else
			return gcd(sum, n % sum);
	}

	//Calculates the sum of squares
	public static long djCounter(djset set) {
		
		//Create hash map holding how many nodes are in each set
		HashMap<Integer, Integer> map = new HashMap<>();

		//Loop through dj set
		for (int i = 0; i < set.parent.length; i++) {

			//Run find opperation to make sure every path is compressed
			int parentID = set.find(set.parent[i]);
			
			//Adds the number of children each root node has
			if (map.containsKey(parentID)) {
				map.put(parentID, map.get(parentID) + 1);
			} else {
					map.put(parentID, 1);
			}
		}

		long total = 0;

		//Sums the squares of each key
		for (int key : map.keySet()) {
			total += map.get(key) * map.get(key);
		}

		return total;
	}
}
