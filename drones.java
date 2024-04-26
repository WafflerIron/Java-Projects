/* Nathan Hicks
 * P4 Drones Deliver to Senior Design Groups
 * COP 3503
 * Uses breadth first search to solve board of drones delivering food to groups
 */

//Imports
import java.util.*;
import java.util.regex.Pattern;

public class drones {

	// Directions of movement for swaps.
	final public static int[] DX = { -1, 0, 0, 1 };
	final public static int[] DY = { 0, -1, 1, 0 };

	// Global variables
	public static int numDrones;
	public static int startHash;
	public static int winHash;
	public static ArrayList<Drone> drones;
	public static ArrayList<Block> blocks;

	public static void main(String[] args) {

		// Scan in the number of drones
		Scanner in = new Scanner(System.in);
		numDrones = in.nextInt();

		// Create hashmap to hold board positions, arraylists to hold drones and blocks
		HashMap<Integer, Integer> grid = new HashMap<Integer, Integer>();
		drones = new ArrayList<>();
		blocks = new ArrayList<>();

		// Add blank drones to arraylist
		for (int i = 0; i < numDrones; i++)
			drones.add(new Drone(0, 0));

		// Scan in grid input checking in pairs
		String check = "";
		for (int i = 0; i < 64; i++) {
			check = in.next(Pattern.compile(".."));

			// If drone, update it's position
			if (check.startsWith("D")) {
				drones.get(Integer.parseInt(check.substring(1)) - 1).setPos(i % 8, i / 8);
			}
			// If group update target drone and also add to blockers for other drones
			if (check.startsWith("G")) {
				drones.get(Integer.parseInt(check.substring(1)) - 1).setGoal(i % 8, i / 8);
				blocks.add(new Block(i % 8, i / 8));
			}
			// If blocker add to blockers
			if (check.startsWith("X")) {
				blocks.add(new Block(i % 8, i / 8));
			}
		}

		in.close(); // Close input

		// Adds starting hash value to map and finds winning hash
		startHash = hashPos(drones, false);
		grid.put(startHash, 0);
		winHash = hashPos(drones, true);

		// Solve the board
		solve(grid);

		// If no solution found print -1
		if (grid.get(winHash) == null)
			System.out.println(-1);

		// Print number of moves to get to winning hash
		else
			System.out.println(grid.get(winHash));
	}

	// Solves the given board
	private static void solve(HashMap<Integer, Integer> grid) {

		// Queue for BFS and add starting hash
		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.offer(startHash);

		// Loop until queue empty
		while (queue.size() != 0) {

			// Get the next board.
			int val = queue.poll();
			int curMoves = grid.get(val);

			// Loop through adjacent boards, adding into the queue if they weren't
			// previously there.
			ArrayList<Integer> next = getNext(val);
			for (int i = 0; i < next.size(); i++) {

				if (!grid.containsKey(next.get(i))) {
					grid.put(next.get(i), curMoves + 1);

					// Return early if winning board found
					if (grid.containsKey(winHash))
						return;

					queue.offer(next.get(i));
				}
			}
		}
	}

	// Finds next possible board positions
	private static ArrayList<Integer> getNext(int val) {

		// Answer array to be returned
		ArrayList<Integer> ans = new ArrayList<Integer>();

		// Make copy of board
		ArrayList<Drone> copy = makeGrid(val);

		// Loops through each direction
		for (int i = 0; i < DX.length; i++) {

			// Hash result
			int res = 0;

			// Loops through each drones
			for (int j = 0; j < numDrones; j++) {

				// Get drone's next position
				int swapX = copy.get(j).x + DX[i];
				int swapY = copy.get(j).y + DY[i];

				// Checks if drone is going to its group, hits its group, or is out of bounds
				if (swapX == drones.get(j).goalX && swapY == drones.get(j).goalY) {

					// Drone should move if it hits its own group

				} else if (!inbounds(swapX, swapY)) {

					// Drone doesn't move if out of bounds
					swapX = copy.get(j).x;
					swapY = copy.get(j).y;

				} else if (copy.get(j).x == drones.get(j).goalX && copy.get(j).y == drones.get(j).goalY) {

					// Drone doesn't move if it's on it's group
					swapX = copy.get(j).x;
					swapY = copy.get(j).y;

				}
				// Builds hash based on its new location
				int xShift = j * 6;
				int yShift = xShift + 3;
				res += (swapX << xShift) + (swapY << yShift);

			}
			// Add hash value to array
			ans.add(res);
		}
		// Return our list.
		return ans;
	}

	// Makes drone list from hashed value
	private static ArrayList<drones.Drone> makeGrid(int val) {

		// Result
		ArrayList<Drone> res = new ArrayList<>();

		// Loop through drones and s position using 111 mask
		for (int i = 0; i < numDrones; i++) {
			int xShift = i * 6;
			int yShift = xShift + 3;

			int xPos = (7 << xShift) & val;
			int yPos = (7 << yShift) & val;

			res.add(new Drone(xPos >> xShift, yPos >> yShift));
		}

		// Return array
		return res;
	}

	// FInds if position is in bounds
	private static boolean inbounds(int swapX, int swapY) {

		// Check if position is within grid
		if (swapX < 0 || swapX > 7)
			return false;
		if (swapY < 0 || swapY > 7)
			return false;

		// Check if position is in block
		for (Block b : blocks)
			if (b.x == swapX && b.y == swapY)
				return false;

		// Position is in bounds
		return true;
	}

	// Calculates hash value
	private static int hashPos(ArrayList<drones.Drone> drones, boolean goalHash) {

		// result to be returned
		int res = 0;

		// Check if we are calculating hash off of goal positions or current positions
		if (!goalHash) {
			// Converts drones position into binary
			for (int i = 0; i < numDrones; i++) {
				int xShift = i * 6;
				int yShift = xShift + 3;
				res += (drones.get(i).x << xShift) + (drones.get(i).y << yShift);
			}
			// Return hash
			return res;
		}

		// Converts drones' goal position into binary
		for (int i = 0; i < numDrones; i++) {
			int xShift = i * 6;
			int yShift = xShift + 3;
			res += (drones.get(i).goalX << xShift) + (drones.get(i).goalY << yShift);
		}

		// Return hash
		return res;
	}

	// Drone class for storing position and goal position
	static class Drone {
		int x, y; // position
		int goalX, goalY; // goal position

		// Constructor
		public Drone(int x, int y) {
			this.x = x;
			this.y = y;
		}

		// Sets position
		public void setPos(int x, int y) {
			this.x = x;
			this.y = y;
		}

		// Sets goal position
		public void setGoal(int x, int y) {
			goalX = x;
			goalY = y;
		}
	}

	// Block class for storing position of blocks
	static class Block {
		int x, y; // position

		// Constructor
		public Block(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
