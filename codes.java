// Nathan Hicks
// COP 3503
// P5 Short Codes
// Uses Ford Fulkerson to bipartite match drug names to drug codes and find a possible combination

///////////////////////  ONLY SOLVES TASK 1 & 2   \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

//Import
import java.util.*;

public class codes {

	public static void main(String[] args) {
		// Scanner
		Scanner in = new Scanner(System.in);

		// Scan in number of drug names and drug codes
		int drugs = in.nextInt(), codes = in.nextInt();

		// Creates arraylist to store drug names and drug code names
		ArrayList<String> drugNames = new ArrayList<String>();
		ArrayList<String> codeNames = new ArrayList<String>();

		int size = drugs + codes; // Number of vertices

		FordFulkerson flow = new FordFulkerson(size); // Creates an unconnected network flow graph

		in.nextLine(); // Still on first line so skip

		// Read in drug names to array list and connect source to all of them
		for (int i = 0; i < drugs; i++) {
			drugNames.add(in.nextLine());
			flow.add(size, i, 1);
		}

		// Read in drug codes to array list and connect them all to sink
		for (int i = 0; i < codes; i++) {
			codeNames.add(in.nextLine());
			flow.add(i + drugs, size + 1, 1);
		}

		in.close(); // Close scanner

		// Connects drug names to drug codes if the name contains the code
		for (int i = 0; i < drugs; i++) {
			for (int j = 0; j < codes; j++) {
				if (drugNames.get(i).contains(codeNames.get(j)))
					flow.add(i, j + drugs, 1);
			}
		}

		int maxFlow = flow.ff(); // Calculate max flow

		// If max flow is equal to the number of drugs we have, then every drug has a
		// matching code!
		if (maxFlow == drugs) {
			System.out.println("yes");

			// Loop through edge matrix and look for backwards edges from drug codes to drug
			// names and print the drug code
			DrugLoop: for (int i = 0; i < drugs; i++)
				for (int j = 0; j < codes; j++) {
					if (flow.cap[j + drugs][i] == 1) {
						System.out.println(codeNames.get(j));
						continue DrugLoop;
					}
				}
		}
		// Not every drug could have a code
		else
			System.out.println("no");
	}
}

class FordFulkerson {

	// Stores graph.
	public int[][] cap;
	public int n;
	public int source;
	public int sink;

	// "Infinite" flow.
	final public static int oo = (int) (1E9);

	// Set up default flow network with size+2 vertices, size is source, size+1 is
	// sink.
	public FordFulkerson(int size) {
		n = size + 2;
		source = n - 2;
		sink = n - 1;
		cap = new int[n][n];
	}

	// Adds an edge from v1 -> v2 with capacity c.
	public void add(int v1, int v2, int c) {
		cap[v1][v2] = c;
	}

	// Wrapper function for Ford-Fulkerson Algorithm
	public int ff() {

		// Set visited array and flow.
		boolean[] visited = new boolean[n];
		int flow = 0;

		// Loop until no augmenting paths found.
		while (true) {

			// Run one DFS.
			Arrays.fill(visited, false);
			int res = dfs(source, visited, oo);

			// Nothing found, get out.
			if (res == 0)
				break;

			// Add this flow.
			// System.out.println(res);
			flow += res;
		}

		// Return it.
		return flow;
	}

	// DFS to find augmenting math from v with maxflow at most min.
	public int dfs(int v, boolean[] visited, int min) {

		// got to the sink, this is our flow.
		if (v == sink)
			return min;

		// We've been here before - no flow.
		if (visited[v])
			return 0;

		// Mark this node and recurse.
		visited[v] = true;
		int flow = 0;

		// Just loop through all possible next nodes.
		for (int i = 0; i < n; i++) {

			// We can augment in this direction.
			if (cap[v][i] > 0)
				flow = dfs(i, visited, Math.min(cap[v][i], min));

			// We got positive flow on this recursive route, return it.
			if (flow > 0) {

				// Subtract it going forward.
				cap[v][i] -= flow;

				// Add it going backwards, so that later, we can flow back through this edge as
				// a backedge.
				cap[i][v] += flow;

				// Return this flow.
				return flow;
			}
		}

		// If we get here there was no flow.
		return 0;
	}
}