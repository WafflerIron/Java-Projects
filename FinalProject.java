/*
- COP 3330 Final Project.
- Nathan Hicks
- In regards to java naming conventions, iD looked strange so i used id or ID, hope that's understandable.
*/

//Imports
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Random;

public class FinalProject {

	public static void main(String[] args) {		
		
		//Asks user to input file path
		Scanner scan = null;
		Boolean valid = true;
		
		String path = "";
		
		System.out.print("Enter the absolute path of the file: ");
		
		//Ensure entered path is valid and loops if not
		do {

			path = new Scanner(System.in).nextLine();
			valid = true;

			try {
				scan = new Scanner (new File(path));

			} catch(Exception e) {
				System.out.println("Sorry no such file.");
				System.out.print("Try again: ");
				valid = false;
			}

		} while (!valid);
		
		System.out.println("\nFile Found! Let's proceed...");
				
		//Creates an array list of all the lectures
		ArrayList<Lecture> courseList = new ArrayList<Lecture>();
		
		String [] currentLine;
		
		//Scans lectures from text file into array list
		while (scan.hasNextLine()) {
			currentLine = scan.nextLine().split(",");
			
			if (currentLine.length > 3) {
				courseList.add(new Lecture(currentLine));
	
			}	
			//if the lecture has labs adds the labs to it
			else {
				courseList.get(courseList.size()-1).getLabs().add(new Lab(currentLine));
			}
		}
		
		//Creates an array list containing all the faculty, TAs, and students
		ArrayList<Person> people = new ArrayList<Person>();
		
		int amountRemoved = 0;

		
		//User chooses option from menu
		int choice = -1;
		
		while (choice != 7) {
			
			System.out.println("\n----------------------------------------\n"
					+ "1- Add a new Faculty to the schedule\n"
					+ "2- Enroll a Student to a Lecture\n"
					+ "3- Print the schedule of a Faculty\n"
					+ "4- Print the schedule of an TA\n"
					+ "5- Print the schedule of a Student\n"
					+ "6- Delete a Lecture\n"
					+ "7- Exit");
			
			System.out.print("\nEnter your choice: ");

			//People array list is sorted by ID for ease of use
			Collections.sort(people);
			
			//Makes sure user inputs valid option from 1-7
			do {
				valid = true;

				try {
					choice = new Scanner(System.in).nextInt();
					
					if (!(choice < 8 && choice >0)) throw new Exception("Invalid Choice");
					
					valid = true;

				} catch(Exception e) {
					System.out.println("Sorry invalid choice, please enter a number 1 - 7");
					System.out.print("Try again: ");
					valid = false;
				}

			} while (!valid);
			

			//Runs different method based on user selection
			switch (choice) {
			case 1:
				newFaculty(people, courseList);
				break;
				
			case 2:
				enrollStudent(people, courseList);
				break;
			case 3:
				facultySchedule(people);
				break;
			case 4:
				taSchedule(people, courseList);
				break;
			case 5:
				studentSchedule(people);
				break;
			case 6:
				amountRemoved += remove(people, courseList);
				break;
			default:
				break;

			}

		}	

		//If at least one lecture was removed updates file if user chooses yes
		if (amountRemoved > 0) {
			
			System.out.print("\nYou have made a deletion of at least one lecture. Would you like to "
					+ "print the copy of lec.txt? Enter y/Y for Yes or n/N for No: ");
			
			String answer;

			do {
				valid = false;

				answer = new Scanner(System.in).nextLine();

				if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("n")) valid = true;

				else System.out.print("\nIs that a yes or no? Enter y/Y for Yes or n/N for No: ");


			} while (!valid);

			PrintWriter writer = null;

			if (answer.equalsIgnoreCase("y")) {

				try {
					writer = new PrintWriter(path);
				} catch (FileNotFoundException e) {
					System.out.println("File not found.");
				}

				for (Lecture l : courseList) {
					writer.println(String.join(",", l.getDesc()));

					for (Lab lab : l.getLabs()) {
						writer.println(String.join(",", lab.getDesc()));
					}
				}
			}
			writer.close();
		}
		
		//Program exits
		System.out.println("\nBye!");
	}
	
	//Static Main Methods
	
	//Checks if entered id is valid and returns id
	static int idInput() {
		
		int ID = -1;
		
		boolean valid;
		
		//Input validation loop for 7-digit integer
		do {
			valid = true;

			try {
				ID = new Scanner(System.in).nextInt();
				
				String strID = Integer.toString(ID);
				if (strID.length() != 7) throw new IdException(ID);
				
				valid = true;
			} catch (InputMismatchException e) {
				try {
					throw new IdException(ID);
				}
				catch (IdException i) {
					System.out.print( i.getMessage());
					valid = false;
				}
			} catch(IdException i) {
				System.out.print( i.getMessage());
				valid = false;
			}
		} while (!valid);
		
		//Returns now valid id
		return ID;
	}
	
	//Find location of id in array list of people, returns -1 if person does'nt exists
	static int idChecker(int ID, ArrayList<Person> people) {
		
		int i = 0;
		for (Person p : people) {
			if (p.getID() == ID) {
				return i;
			}
			i++;
		}
		return -1;
	
	}
	
	//Returns true if specified person is already involved with a specificed lecture, and false otherwise
	static boolean inClass(int index, ArrayList<Person> people, String crn) {
		for (Lecture l : people.get(index).getLectures()) {
			if (l.getDesc()[0].equalsIgnoreCase(crn)) return true;
		}
		return false;
	}
	
	//Assigns faculty to lectures, and creates new faculty if they don't already exist
	static void newFaculty(ArrayList<Person> people, ArrayList<Lecture> courses) {
		
		//Gets id of faculty
		System.out.print("\nEnter UCF id: ");
		int id = idInput();
		
		//If faculty doesn't exists creates new Faculty
		if (idChecker(id, people) == -1) {
			System.out.print("Enter name: ");
			String name = new Scanner(System.in).nextLine();
			
			System.out.print("Enter rank: ");
			String rank = new Scanner(System.in).nextLine();
			
			System.out.print("Enter office location: ");
			String officeLoc = new Scanner(System.in).nextLine();
			
			people.add(new Faculty(id, name, rank, officeLoc));
		}
		//Checks if id entered is a student since they can't be faculty
		if (!(people.get(idChecker(id, people)) instanceof Faculty)) {
			System.out.println("A Student cannot be Faculty");
			return;

		}
		
		//Input validation for an integer representing number of lectures assigning to faculty
		System.out.print("Enter how many lectures: ");
		
		boolean valid = true;
		int lectNum = 0;
		
		do {
			try {
				lectNum = new Scanner(System.in).nextInt();
				valid = true;
			} catch (Exception e) {
				System.out.print("Pleas enter a valid integer: ");
				valid = false;
			}
		} while (!valid);
		
		//User inputs crns of the lectures being assigned
		System.out.print("Enter the crns of the lectures seperated by a space: ");
		
		String crn = "";
		String [] line;
		
		line = new Scanner(System.in).nextLine().split(" ");
		
		//Adds specified lectures to faculty unless faculty already has them
		for (int i = 0; i < lectNum; i++) {
			crn = line[i];
			System.out.println(crn);

			for (Lecture l : courses) {
				if (l.getDesc()[0].equalsIgnoreCase(crn) && !inClass(idChecker(id, people), people, crn)) {
					people.get(idChecker(id, people)).getLectures().add(l);
				}
			}
		}
		
		//Prints information about lectures being added and updates user that the assignment was a success
		for(int i = people.get(idChecker(id, people)).getLectures().size() - lectNum;
				 i < people.get(idChecker(id, people)).getLectures().size(); i++) {
			
			Lecture l = people.get(idChecker(id, people)).getLectures().get(i);
			if (l.getLabs().isEmpty())
				System.out.println(l + " Added!");

			//Assigns TA's to labs if labs exist
			else {
				System.out.println("[" + l.getDesc()[0] + "/" + l.getDesc()[1] + "/" + l.getDesc()[2] + "]" + " has these labs: ");
				for (Lab lab : l.getLabs()) {
					System.out.println("\t" + lab.getDesc()[0] + "," + lab.getDesc()[1]);
				}
				for (Lab lab : l.getLabs()) {

					
					//User inputs id's for ta teaching each lab
					System.out.print("\nPlease enter the TA’s id for " + lab.getDesc()[0] + ": ");
					
					int ta;
					
					valid = false;
					
					//Checks to make id entered is qualified to be ta for lab
					do {
						ta = idInput();

						if (idChecker(ta, people) == -1) valid = true;

						//Checks if entered ta is a faculty
						else if (people.get(idChecker(ta, people)) instanceof Faculty) {
							System.out.println("Faculty cannont be TA's");
							System.out.print("Please try again: ");
							valid = false;
							
						//If the entered ta is currently a student, asks for extra info to promote them to ta
						} else if (!(people.get(idChecker(ta, people)) instanceof TeachAssist)) {
							Person p = people.get(idChecker(ta, people));
							
							System.out.print("TA’s supervisor’s name: ");
							String advisor = new Scanner(System.in).nextLine();

							System.out.print("Degree Seeking: ");
							String degree = new Scanner(System.in).nextLine();
							
							people.set(idChecker(ta, people), new TeachAssist(ta, p.getName(), advisor, degree));
							valid = true;
							
							
						//Checks if ta is already part of the lecture
						} else if (inClass(idChecker(ta, people), people, l.getDesc()[0])) {
							System.out.println("Student is already in " + l.getDesc()[0]);
							System.out.print("Please try again: ");
							valid = false;
							
						} else valid = true;

					} while(!valid);

					//If ta does not exist creates new ta
					if (idChecker(ta, people) == -1) {
						System.out.print("Name of TA: ");
						String name = new Scanner(System.in).nextLine();

						System.out.print("TA’s supervisor’s name: ");
						String advisor = new Scanner(System.in).nextLine();

						System.out.print("Degree Seeking: ");
						String degree = new Scanner(System.in).nextLine();

						people.add(new TeachAssist(ta, name, degree, advisor));
					}
					((TeachAssist)(people.get(idChecker(ta, people)))).getLabs().add(lab);
				}
				
				System.out.println("[" + l.getDesc()[0] + "/" + l.getDesc()[1] + "/" + l.getDesc()[2] + "]" + " Added!");

			}
		}
	}
	
	//Enrolls students to lectures and labs
	public static void enrollStudent(ArrayList<Person> people, ArrayList<Lecture> lectures) {
		
		//User inputs student id
		System.out.print("\nEnter UCF id: ");
		int id = idInput();
		
		//If student does not exist creates new student
		if (idChecker(id, people) == -1) {
			System.out.print("Enter name: ");
			String name = new Scanner(System.in).nextLine();
			
			System.out.print("Graduate or Undergraduate: ");
			String type = new Scanner(System.in).nextLine();
			
			people.add(new Student(id, name, type));
		}
		
		//Checks if entered student is faculty
		if (people.get(idChecker(id, people)) instanceof Faculty) {
			System.out.println("Cannot enroll Faculty to classes.");
			return;
		}
		
		//User inputs lecture to enroll student in
		System.out.print("Which lecture to enroll [" + people.get(idChecker(id, people)).getName() +  "] in? ");

		String crn = new Scanner(System.in).nextLine();

		//Checks if student is a ta for a lecture being enrolled in
		for (Lecture l : lectures) {
			if (!l.getLabs().isEmpty() && l.getDesc()[0].equalsIgnoreCase(crn) && people.get(idChecker(id, people)) instanceof TeachAssist)
				for (Lab lab : l.getLabs()) {
					for (Lab labIn : ((TeachAssist) people.get(idChecker(id, people))).getLabs() ) {


						if (lab.getDesc()[0] == labIn.getDesc()[0]) {
							System.out.println("This student is already a TA for " + crn);
							return;
						}
					}
				}
			
			//Checks if student is already enrolled in specified course
			if (inClass(idChecker(id, people), people, crn)) {
					System.out.println("This student is already in " + crn);
					return;
			}
			
			//Adds specified lecture to student's schedule
			if (l.getDesc()[0].equalsIgnoreCase(crn) && !inClass(idChecker(id, people), people, crn)) {
				people.get(idChecker(id, people)).getLectures().add(l);
				break;
			}
		}

		//Randomly assigns lab to student taking a lecture with labs
		Lecture l = people.get(idChecker(id, people)).getLectures().get(people.get(idChecker(id, people)).getLectures().size() - 1);
			if (!l.getLabs().isEmpty()) {
				System.out.println("[" + l.getDesc()[0] + "/" + l.getDesc()[1] + "/" + l.getDesc()[2] + "]" + " has these labs: ");
				for (Lab lab : l.getLabs()) {
					System.out.println("\t" + lab.getDesc()[0] + "," + lab.getDesc()[1]);
				}
				
				int labRand = new Random().nextInt(l.getLabs().size());
				
				System.out.println("\n" + people.get(idChecker(id, people)).getName() + " is added to lab : " 
									+ l.getLabs().get(labRand).getDesc()[0]);
				((Student)(people.get(idChecker(id, people)))).getLabsEnrolled().add(l.getLabs().get(labRand));
			}
		
		System.out.println("Student Enrolled!"); 
	}
	
	//Prints schedule of faculty
	public static void facultySchedule(ArrayList<Person> people) {
		//User inputs id of faculty
		System.out.print("\nEnter UCF id: ");
		int id = idInput();
		
		//Makes sure entered id exists and is a faculty
		if (idChecker(id, people) == -1) {
			System.out.println("No Faculty with this id.");
			return;
		} else if (!(people.get(idChecker(id, people)) instanceof Faculty)) {
			System.out.println("No Faculty with this id.");
			return;
		}
		
		//Prints lectures that faculty teaches and their associated labs
		System.out.println("\n" + people.get(idChecker(id, people)).getName() + " is teaching the following lectures: ");

			
		for (Lecture l : people.get(idChecker(id, people)).getLectures())
			System.out.println(l);

	}
	
	//Prints schedule of ta
	public static void taSchedule(ArrayList<Person> people, ArrayList<Lecture> lectures) {
		
		//First prints the ta's student schedule
		int id = studentSchedule(people);
		
		//Prints nothing more if student is actually not a ta
		if (idChecker(id, people) == -1 ) {
			return;
		} else if (!(people.get(idChecker(id, people)) instanceof TeachAssist))
				return;
		
		//Prints lectures and labs student is a ta for
		System.out.println("And is a TA for: ");
		
		for (Lecture l : lectures) 
			for (Lab lab : ((TeachAssist) people.get(idChecker(id, people))).getLabs()) 
				for (Lab labIn : l.getLabs()) 
					if (lab.getDesc()[0].equalsIgnoreCase(labIn.getDesc()[0])) 
						System.out.println("\t[" + l.getDesc()[0] + "/" + l.getDesc()[1] + "/" + l.getDesc()[2] + "]"
								+ "/[Lab: " + labIn.getDesc()[0] + "]");

	}
	
	//Prints schedule of a student, return id of student to be used in taSchedule
	public static int studentSchedule(ArrayList<Person> people) {
		
		//Checks if entered id is valid and a student
		System.out.print("\nEnter UCF id: ");
		int id = idInput();
		
		if (idChecker(id, people) == -1) {
			System.out.println("No Student with this id.");
			return -1;
		} else if (!(people.get(idChecker(id, people)) instanceof Student)) {
			System.out.println("No Student with this id.");
			return -1;
		}
			
		//Prints lectures and labs on student's schedule
		System.out.println("\n" + people.get(idChecker(id, people)).getName() + " is enrolled in the following lectures: ");

		for (Lecture l : people.get(idChecker(id, people)).getLectures()) {
			System.out.print("\t[" + l.getDesc()[0] + "/" + l.getDesc()[1] + "/" + l.getDesc()[2] + "]");
			for (Lab lab : l.getLabs()) {
				for (Lab labIn : ((Student) people.get(idChecker(id, people))).getLabsEnrolled() ) {
					if (lab.getDesc()[0].equalsIgnoreCase(labIn.getDesc()[0])) {
						System.out.print("/[Lab: " + labIn.getDesc()[0] + "]");
					}
				}
			}
			System.out.print("\n");

		}
		return id;
	}
	
	//Removes a lecture and its labs from all schedules, returns 1 if lecture removed and 0 if it doesn't exist
	public static int remove(ArrayList<Person> people, ArrayList<Lecture> lectures) {
		
		//User inputs lecture to be removed
		System.out.print("Enter the crn of the lecture to delete: ");

		String crn = new Scanner(System.in).nextLine();
		
		//Find lecture to be removed or returns if lecture does not exist
		Lecture toRemove = null;
		for (Lecture l : lectures) {
			if (crn.equalsIgnoreCase(l.getDesc()[0])) {
				toRemove = l;
				break;
			}
		}
		
		if (toRemove == null) {
			System.out.print("Lecture does not exist. ");
			return 0;

		}
		
		//Removes lecture and its labs from all schedules regardless of Person type
		Person empty = null;
		for (Person p : people) {
			people.remove(empty); //Post-order removes person from array list to prevent exception
			p.getLectures().remove(toRemove);
			
			if (p instanceof Student) {
				for (Lab lab : toRemove.getLabs())
					for(Lab labIn : ((Student)p).getLabsEnrolled()) {
						if (lab.getDesc()[0].equalsIgnoreCase(labIn.getDesc()[0])) {
							((Student)p).getLabsEnrolled().remove(labIn);
						}
					}
			}
			if (p instanceof TeachAssist) {
				for (Lab lab : toRemove.getLabs())
					for(Lab labIn : ((TeachAssist)p).getLabs()) {
						if (lab.getDesc()[0].equalsIgnoreCase(labIn.getDesc()[0])) {
							((TeachAssist)p).getLabs().remove(labIn);
						}
					}
				//If ta isn't helping in any labs reverts them to student
				if (((TeachAssist)p).getLabs().isEmpty()) {
					people.set(people.indexOf(p), new Student(p.getID(), p.getName(), ((Student)p).getType()));
				}
			}
			//Prepares to remove a person if they attend no lectures
			if (p.getLectures().isEmpty())
				empty = p;
		}
		//Removes any other necessary persons after iteration
		people.remove(empty);
		
		//Removes lecture from lecture array list, prints success of which lecture was removed, and returns 1
		lectures.remove(toRemove);
		System.out.print("[" + toRemove.getDesc()[0] + "/" + toRemove.getDesc()[1] + "/" + toRemove.getDesc()[2] + "] Deleted");
		return 1;
	}

}

//Classes
	
//Labs are assigned to lectures and hold only basic information
class Lab {
	private String [] desc; //A lab's description
	
	//Constructor
	public Lab (String [] desc) {
		this.desc = desc;
	}
	
	//Getter
	public String[] getDesc () {
		return desc;
	}
}

//Lectures have the similar descriptions as labs but also contain labs
class Lecture extends Lab {
	private ArrayList<Lab> labs; //Array list of labs associated with lecture
	
	//Constructor
	public Lecture (String [] desc) {
		super(desc);
		labs = new ArrayList<Lab>();
	}
	
	//Getter
	public ArrayList<Lab> getLabs () {
		return labs;
	}
	
	@Override
	public String toString () {
		String line = "\t[" + super.getDesc()[0] + "/" + super.getDesc()[1] + "/" + super.getDesc()[2] + "]";

		if (super.getDesc()[4].equalsIgnoreCase("online"))
			line += "[Online]";
		
		if(labs.size() != 0) {
			line += " with Labs:";
			for (Lab a : labs) {
				line += "\n\t\t" + a.getDesc()[0] + "," + a.getDesc()[1];
			}
		}
		
		return line;
	}
}

//Abstract Person, holds all the information any person at UCF has
abstract class Person implements Comparable<Person>{
	private int ID; //ID of Person
	private String name; //Person's name
	private ArrayList<Lecture> lectures; //Array list of lectures taught or enrolled in
	
	//Constructor
	public Person(int ID, String name) {
		this.ID = ID;
		this.name = name;
		lectures = new ArrayList<Lecture>();
	}
	
	//Compares persons by ID
	public int compareTo (Person p) {
		if (this.ID == p.getID())
			return 0;
		if (this.ID > p.getID())
			return 1;
		return -1;	
	}
	
	//Getters and Setters
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Lecture> getLectures() {
		return lectures;
	}
	public void setLectures(ArrayList<Lecture> lectures) {
		this.lectures = lectures;
	}
	
	//Correct capitalization issues
	public String fixCase(String str) { 
        str = str.substring(0, 0).toUpperCase() + str.substring(1).toLowerCase();
        return str;
	}
	
	
}

//Student is a type of person enrolled in classes
class Student extends Person {
	private String type; //Either graduate or undergraduate
	private ArrayList<Lab> labsEnrolled; //List of labs enrolled in
	
	//Constructor
	public Student (int ID, String name, String type) {
		super(ID, name);
		this.type = fixCase(type);
		labsEnrolled = new ArrayList<Lab>();
	}
	
	//Getters and Setters
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<Lab> getLabsEnrolled() {
		return labsEnrolled;
	}
}

//A type of student that also assists in labs
class TeachAssist extends Student {
	private ArrayList<Lab> labs; //Labs assisting in
	private String advisor; //TA advisor
	
	//Constructor
	public TeachAssist (int ID, String name, String type, String advisor) {
		super(ID, name, type);
		this.advisor = advisor;
		labs = new ArrayList<Lab>();
	}
	
	//Getters and Setters
	public ArrayList<Lab> getLabs() {
		return labs;
	}
	public void setLabs(ArrayList<Lab> labs) {
		this.labs = labs;
	}
	public String getAdvisor() {
		return advisor;
	}
	public void setAdvisor(String advisor) {
		this.advisor = advisor;
	}
}

//A type of Person that teaches lectures
class Faculty extends Person {
	private String rank; //professor
	private String officeLoc; //location of office
	
	//Constructor
	public Faculty (int ID, String name, String rank, String officeLoc) {
		super(ID, name);
		this.rank = fixCase(rank);
		this.officeLoc = officeLoc;
	}
	
	//Getters and Setters
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getOffice() {
		return officeLoc;
	}
	public void setOffic(String office) {
		officeLoc = office;
	}
}

//Custom exception
class IdException extends Exception {
	private int id; //id being entered
	
	//Constructor
	public IdException(int id) {
		this.id = id;
	}
	
	@Override
	public String getMessage() {
		return ">>>>>>>>>>>Sorry incorrect format. (Ids are 7 digits) \nPlease try again: ";
	}
}