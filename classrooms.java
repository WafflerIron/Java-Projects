/* Nathan Hicks
 * COP 3503
 * RP3 Classrooms
 * Given a list of events with start an end times and the number of classrooms
 * Uses a greedy algorithm to calculate max amount of events that could be scheduled
 */

//Imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.TreeSet;

public class classrooms {

	public static void main(String[] args) {
		
		//Scan in the number of activities and classrooms
		Scanner in = new Scanner(System.in);
		
		int activities = in.nextInt();
		int rooms = in.nextInt();
		int count = 0; //Max number of activities to be scheduled
		
		//Tree set to track the rooms and arraylist to track the activities
		TreeSet<Room> roomTracker = new TreeSet<>();
		ArrayList<Activity> activitiesTracker = new ArrayList<>();
		
		//Scans in all the start and end times for each activity
		for (int i = 0; i < activities; i++)
			activitiesTracker.add(new Activity(in.nextInt(), in.nextInt()));
		
		//Sorts activities from lowest end time to highest
		Collections.sort(activitiesTracker);

		//Loop through all activities
		for (Activity a : activitiesTracker) {

			
			Room lowest = null; //Lowest end time in the rooms
			int size = roomTracker.size(); //Number of rooms in use
			
			//Tries to find a room with an end time closest but lower than the activities start time
			try { 
	            lowest = roomTracker.floor(new Room(a.s, rooms)); 
	        } 
			//Sets to null if fails
	        catch (Exception e) { 
	             lowest = null;
	        } 

			//If could not find an end time lower than start time, add to empty room if one is available
			if (lowest == null) {
				if (size < rooms) {
					roomTracker.add(new Room(a.e +1, size)); // +1 is to make floor function work properly
					count++;
				}
				
			//If lowest was found, add activity to that room
			} else {				
				roomTracker.remove(lowest);
				roomTracker.add(new Room(a.e +1, lowest.id));
				count++;
			}
		}
		
		//Print result and closed scanner
		System.out.println(count);
		in.close();
	}
	
	//Activity object that contains the start and end time
	static class Activity implements Comparable<Activity>{
		
		int s; //Start time
		int e; //End time
		
		//Constructor
		public Activity (int start, int end) {
			s = start;
			e = end;
		}

		//Comparable method to sort from lowest to highest end time then lowest to highest start time
		public int compareTo(Activity o) {
			int res = e - o.e;
			if (res == 0) return s - o.s;
			return res;
		}
	}
	
	//Room object with end time, and id to make TreeSet work for duplicates
	static class Room implements Comparable<Room>{
		
		int e; //End time of most recent activity
		int id; //Id for room
		
		//Constructor
		public Room (int end, int id) {
			e = end;
			this.id = id;
		}

		//Comparable method that sorts by lowest to highest end time, then by lowest to highest id
		//Almost forgot to sort by lowest to highest id which would've completely undermined the point of having no dupes
		public int compareTo(Room o) {
			// TODO Auto-generated method stub
			int res = e - o.e;
			if (res == 0) return id - o.id;
			return res;
		}
	}
}
