package database;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;

public class CommandLine {

    // 'sqlMngr' is the object which interacts directly with MySQL
	private SQLController sqlMngr = null;
    // 'sc' is needed in order to scan the inputs provided by the user
	private Scanner sc = null;
	public static String curr_userid = null;
	public static int curr_listid = 0;
	
	//Public functions - CommandLine State Functions
	
    /* Function used for initializing an istance of current
     * class
     */
	public int startSession() {
		boolean success = true;
		int checkup = 0;
		if (sc == null) {
			sc = new Scanner(System.in);
		}
		if (sqlMngr == null) {
			sqlMngr = new SQLController();
		}
		try {
		    success = sqlMngr.connect(this.getCredentials());
			checkup = sqlMngr.checkUser(userCheck());
		} catch (ClassNotFoundException e) {
			success = false;
			System.err.println("Establishing connection triggered an exception!");
			e.printStackTrace();
			sc = null;
			sqlMngr = null;
		}
		if (checkup != 0) {
			return checkup;
		} else if (checkup == 3) {
			return 0;
		}
		else {
			System.out.println("ID and Password does not match!");
			return 0;
		}
	}
	
    /* Function that acts as destructor of an instance of this class.
     * Performs some housekeeping setting instance's private field
     * to null
     */
	public void endSession() {
		if (sqlMngr != null)
			sqlMngr.disconnect();
		if (sc != null) {
			sc.close();
		}
		sqlMngr = null;
		sc = null;
	}

    /* Function that executes an infinite loop and activates the respective 
     * functionality according to user's choice. At each time it also outputs
     * the menu of core functionalities supported from our application.
     */
	public boolean execute(int user) {
		if (sc != null && sqlMngr != null) {
			System.out.println("");
			System.out.println("***************************");
			System.out.println("******ACCESS GRANTED*******");
			System.out.println("***************************");
			System.out.println("");
			
			String input = "";
			int choice = -1;
			if (user == 1) {
				do {
					menu_user(); //Print Menu
					input = sc.nextLine();
					try {
						choice = Integer.parseInt(input);
						switch (choice) { //Activate the desired functionality
						case 1:
							this.createPost();
							break;
						case 2:
							this.searchList();
							break;
						case 3:
							this.editPost();
							break;
						case 4:
							this.viewRent();
							break;
						case 6:
							this.showTable();
							break;
						default:
							break;
						}
					} catch (NumberFormatException e) {
						input = "-1";
					}
				} while (input.compareTo("0") != 0);
					return true;
			} else if (user == 2){
				do {
					menu_sa(); //Print Menu
					input = sc.nextLine();
					try {
						choice = Integer.parseInt(input);
						switch (choice) { //Activate the desired functionality
						case 1:
							this.insertOperator();
							break;
						case 2:
							this.selectOperator();
							break;
						case 3:
							this.printSchema();
							break;
						case 4:
							this.printColSchema();
							break;
						case 5:
							this.createTable();
							break;
						case 6:
							this.showTable();
							break;
						case 7:
							this.deleteTable();
							break;
						default:
							break;
						}
					} catch (NumberFormatException e) {
						input = "-1";
					}
				} while (input.compareTo("0") != 0);
					return true;
			}
		} else {
			System.out.println("");
			System.out.println("Connection could not been established! Bye!");
			System.out.println("");
			return false;
		}
		return false;
	}
	
	//Private functions
	public void user_id(String id) {
		curr_userid = id;
	}
	//Print menu options
	private static void menu_user() {
		System.out.println("=========MENU=========");
		System.out.println("WelCome " + curr_userid);
		System.out.println("0. Exit.");
		System.out.println("1. Create New Post.");
		System.out.println("2. Start Booking with us.");
		System.out.println("3. Edit My Posting.");
		System.out.println("4. View or cancel current booking.");
		System.out.print("Choose one of the previous options [0-4]: ");
	}
	
	private static void menu_sa() {
		System.out.println("=========MENU=========");
		System.out.println("WelCome " + curr_userid);
		System.out.println("0. Exit.");
		System.out.println("1. Insert.");
		System.out.println("2. Select.");
		System.out.println("3. Print Schema.");
		System.out.println("4. Print Column Schema.");
		System.out.println("5. Create Table.");
		System.out.println("6. Checkout the Table.");
		System.out.println("7. Delete Existing Table.");
		System.out.print("Choose one of the previous options [0-7]: ");
	}
	
	private static void type(){
		System.out.println("=========Type==========");
		System.out.println("1. Entire Home");
		System.out.println("2. Private Room");
		System.out.println("3. Share Room");
		System.out.println("Choose from the previous options [1-3]: ");
	}
	
	private static void amenities(){
		System.out.println("======Amenities=======");
		System.out.println("0. Essentials (Towels, bed sheet, soap and toilet paper)");
		System.out.println("1. Free Parking");
		System.out.println("2. High Speed Internet");
		System.out.println("3. TV");
		System.out.println("4. Heat.");
		System.out.println("5. Air Conditioning.");
		System.out.println("6. Desk / Workspace.");
		System.out.println("Choose from the previous options [0-6] (seperate them with comma for two or more choices): ");
	}
	
	private static void search(){
		System.out.println("=======Search========");
		System.out.println("Search by City, or address will sort by price ONLY");
		System.out.println("1. By Name of the City");
		System.out.println("2. By Address");
		System.out.println("3. By Geographical Coordinates");
		System.out.println("Choose from the previous options [1-3]: ");
	}
	
    // Called during the initialization of an instance of the current class
    // in order to retrieve from the user the credentials with which our program
    // is going to establish a connection with MySQL
	private String[] getCredentials() {
		String[] cred = new String[3];
		System.out.print("Username: ");
		cred[0] = sc.nextLine();
		System.out.print("Password: ");
		cred[1] = sc.nextLine();
		System.out.print("Database: ");
		cred[2] = sc.nextLine();
		return cred;
	}
	
	// Very beginning of the System.
	// If user has account, check authority.
	// If not allow user to create account.
	private String[] userCheck() {
		String current_user = "No";
		String cc_info = "Skip";
		int checkup;
		int valid = 1998;
		String [] new_info = new String [8];
		String [] curr_info = new String[2];
		String [] setup = new String[2];
		setup[0] = "serviceadmin";
		setup[1] = "sa";
		System.out.println("=========================================");
		System.out.println("Welcome to airbnb!");
		System.out.println("Are you a current user?(Y/N): ");
		current_user = sc.nextLine();
		if (current_user.startsWith("n") || current_user.startsWith("N")) {
			System.out.println("=========================================");
			System.out.println("Creating new user profile.");
			System.out.print("Please enter your ID: ");
			new_info[0] = sc.nextLine();
			System.out.print("Please enter your password: ");
			new_info[1] = sc.nextLine();
			System.out.print("Please enter your name: ");
			new_info[2] = sc.nextLine();
			System.out.print("Please enter your home address: ");
			new_info[3] = sc.nextLine();
			System.out.print("Please enter your date of birth(MM/DD/YEAR): ");
			new_info[4] = sc.nextLine();
			checkup = Integer.valueOf(new_info[4].substring(6, 10));
			if (checkup > valid) {
				System.out.println("You are not eligible to register. Must be 18+ years old.");
				new_info[0] = "invalid";
			}
			System.out.print("Please enter your occupation: ");
			new_info[5] = sc.nextLine();
			System.out.print("Please enter your SIN number: ");
			new_info[6] = sc.nextLine();
			System.out.print("Please enter your credit card number\n");
			System.out.print("If you wish to skip type skip: ");
			cc_info = sc.nextLine();
			if (cc_info.startsWith("s") || cc_info.startsWith("S")) {
				new_info[7] = "Not Assigned Yet";
				return new_info;
			} else {
				new_info[7] = cc_info;
				return new_info;
			}
		} else if (current_user.equals("serviceadmin")) {
			return setup;
		} else {
			System.out.print("Please enter your ID: ");
			curr_info[0] = sc.nextLine();
			System.out.print("Please enter your Password: ");
			curr_info[1] = sc.nextLine();
			return curr_info;			
		}
	}
	
    // Function that handles the feature: "1. Insert a record."
	private void insertOperator() {
		int rowsAff = 0;
		int counter = 0;
		String query = "";
		System.out.print("Table: ");
		String table = sc.nextLine();
		System.out.print("Comma Separated Columns: ");
		String cols = sc.nextLine();
		System.out.print("Comma Separated Values: ");
		String[] vals = sc.nextLine().split(",");
        //transform the user input into a valid SQL insert statement
		query = "INSERT INTO " + table + " (" + cols + ") VALUES("; 
		for (counter = 0; counter < vals.length - 1; counter++) {
			query = query.concat("'" + vals[counter] + "',");
		}
		query = query.concat("'" + vals[counter] + "');");
		System.out.println(query);
		rowsAff = sqlMngr.insertOp(query);
		System.out.println("");
		System.out.println("Rows affected: " + rowsAff);
		System.out.println("");
	}

    // Function that handles the feature: "2. Select a record."
	private void selectOperator() {
		String query = "";
		System.out.print("Issue the Select Query: ");
		query = sc.nextLine();
		query.trim();
		if (query.substring(0, 6).compareToIgnoreCase("select") == 0) {
			sqlMngr.selectOp(query);
		}
		else
			System.err.println("No select statement provided!");
	}
	
    // Function that handles the feature: "3. Print schema."
	private void printSchema() {
		ArrayList<String> schema = sqlMngr.getSchema();
		
		System.out.println("");
		System.out.println("------------");
		System.out.println("Total number of tables: " + schema.size());
		for (int i = 0; i < schema.size(); i++) {
			System.out.println("Table: " + schema.get(i));
		}
		System.out.println("------------");
		System.out.println("");
	}
	
    // Function that handles the feature: "4. Print table schema."
	private void printColSchema() {
		System.out.print("Table Name: ");
		String tableName = sc.nextLine();
		ArrayList<String> result = sqlMngr.colSchema(tableName);
		System.out.println("");
		System.out.println("------------");
		System.out.println("Total number of fields: " + result.size()/2);
		for (int i = 0; i < result.size(); i+=2) {
			System.out.println("-");
			System.out.println("Field Name: " + result.get(i));
			System.out.println("Field Type: " + result.get(i+1));
		}
		System.out.println("------------");
		System.out.println("");
	}
	
	 // Function that handles the feature: "5. Create Table."
	private void createTable() {
		int col_num = 0;
		int i;
		String uid = "n";
		System.out.print("Table name?: ");
		String t_name = sc.nextLine();
		String sql = "CREATE TABLE " + t_name + " ";
		System.out.print("How many columns do you need?: ");
		col_num = Integer.parseInt(sc.nextLine());
		System.out.print("Column " + "1" + " Name(Label Type): ");
		sql += "(" + sc.nextLine() + ", ";
		for (i=2; i < col_num; i++) {
			System.out.print("Column " + String.valueOf(i) + " Name(Label Type): ");
			sql += sc.nextLine() + ", ";
		}
		if (i == 2 || i == col_num) {
			System.out.print("Column " + String.valueOf(i) + " Name(Label Type): ");
			sql += sc.nextLine();
			System.out.print("Do you need Primary key?(y/n): ");
			uid = sc.nextLine();
			if (uid.startsWith("y") || uid.startsWith("Y")) {
				System.out.print("Please provide name of the key.: ");
				sql += ", " + "PRIMARY KEY(" + sc.nextLine() + "))";
			} else {
				sql += ")";
			}
		}
		sqlMngr.createT(sql);
	}
	
	 // Function that handles the feature: "6. Show Table."
	private void showTable() {
		System.out.print("Table Name: ");
		String tableName = sc.nextLine();
		if (tableName.equals("accounts")) {
			sqlMngr.checkAccounts(tableName);
		}
		else if (tableName.equals("listing")) {
			sqlMngr.checkListing(tableName);
		}
		else if (tableName.equals("amenities")) {
			sqlMngr.checkAmenities(tableName);
		}
		else if (tableName.equals("calendar")) {
			sqlMngr.checkCalendar(tableName);
		}
	}
    // Function that handles the feature: "7. Print table schema."	
	private void deleteTable() {
		String query = "DROP TABLE ";
		System.out.print("Table Name: ");
		String tableName = sc.nextLine();
		query += tableName + ";";
		sqlMngr.deleteTableop(query);
	}
	
	// Function that handles the feature(user): "1. Create New Post."
	private void createPost() {
		int i = 0;
		String query_listing = "";
		String query_amenities = "";
		String query_calendar = "";
		System.out.println("Please choose the type of your building.: ");
		type();
		String type = sc.nextLine();
		if (type.equals("1")) {
			type = "Full House";
		} else if (type.equals("2")) {
			type = "Private Room";
		} else if (type.equals("3")) {
			type = "Shared Room";
		}
		System.out.print("Please enter latitude of your place (Search google): ");
		String lat = sc.nextLine();
		System.out.print("Please enter longtitude of your place (Search google): ");
		String lon = sc.nextLine();
		System.out.print("Please enter your address(street# or suite#-street#): ");
		String street = sc.nextLine();
		System.out.print("Please enter your city: ");
		String city = sc.nextLine();
		System.out.print("Please enter your country: ");
		String country = sc.nextLine();
		System.out.println("Please choose amenities from below menu: ");
		amenities();
		String[] amen = (sc.nextLine().replaceAll("\\s+", "")).split(",");
		System.out.print("Please enter the available Month: ");
		String month = sc.nextLine();
		System.out.print("Please enter the available days(seperated by comma): ");
		String[] date = (sc.nextLine().replaceAll("\\s+", "")).split(",");
		System.out.print("Please enter the price per day: ");
		String price = sc.nextLine();
        //transform the user input into a valid SQL insert statement
		query_listing = "INSERT INTO listing (type,owner,lat,lon,street,city,country,price,status) VALUES("
				+ "'"+ type + "'," + "'" + curr_userid + "'," + "'" + lat 
				+ "'," + "'"+ lon + "'," + "'" + street + "',"
				+ "'" + city + "'," + "'" + country + "'," + "'" + price +"','Available');";
		sqlMngr.insertOp(query_listing);
		curr_listid = sqlMngr.get_id("SELECT id FROM listing ORDER BY id DESC LIMIT 1;");
		for (i = 0; i < amen.length; i++) {
			query_amenities = "INSERT INTO amenities (id,amen) VALUES ('" + curr_listid + "',"
					+ "'" + amen[i] + "');";
			sqlMngr.insertOp(query_amenities);
		}
		
		for (i = 0; i < date.length; i++) {
			query_calendar = "INSERT INTO calendar (id,month,date) VALUES ('" + curr_listid + "',"
					+ "'" + month + "'," + "'" + date[i] + "');";
			sqlMngr.insertOp(query_calendar);
		}
		System.out.println("Your post has been updated");
		System.out.println("");
	}
	
	// Function that handles the feature(user): "2. Start Booking with us."	
	private void searchList() {
		String order_opt = "";
		String dist_opt = "";
		String sql_dest = "";
		String select1 = "";
		String select2 = "";
		String select3 = "";
		String select4 = "";
		String select5 = "";
		String select6 = "";
		String last_query = "";
		String check_query = "";
		String confirm = "";
		String delete_query = "";
		String update_query = "";
		String insert_query = "";
		String org_target[] = new String[2];
		String insert_query2 = "";
		String priceordist = "p";
		String suffix = "'";
		float diff;
		boolean available = true;
		int checkin, checkout, month, nights, i;
		search();
		dist_opt= sc.nextLine();
		if (dist_opt.equals("1")) {
			System.out.print("Sort by (ascending / descending) price order (a/d): ");
			order_opt = sc.nextLine();
			System.out.print("Please provide name of the city: ");
			sql_dest = sc.nextLine();
		} else if (dist_opt.equals("3")) {
			System.out.print("Sort by (ascending / descending) distance order (a/d): ");
			order_opt = sc.nextLine();
		    System.out.print("Please provide latitude: ");
		    org_target[0] = sc.nextLine();
		    System.out.print("Please provide longitude: ");
		    org_target[1] = sc.nextLine();
		    priceordist = "d";
		} else {
			System.out.print("Sort by (ascending / descending) price order (a/d): ");
			order_opt = sc.nextLine();
			System.out.print("Please provide address of your search destination: ");
			sql_dest = sc.nextLine();
		}
		System.out.print("Please enter the check-in Month(MM): ");
		month = Integer.valueOf(sc.nextLine());
		System.out.print("Please enter the check-in date(DD): ");
		checkin = Integer.valueOf(sc.nextLine());
		System.out.print("Please enter the check-out date(DD): ");
		checkout = Integer.valueOf(sc.nextLine());
		nights = checkout - checkin;
		int[] staying = new int[nights+1];
		staying[0] = checkin;
		for (i=1; i < nights+1; i++) {
			staying[i] = staying[i-1] + 1;
		}
		select1 = "SELECT id FROM calendar WHERE month = " + String.valueOf(month) + " AND date > "
				+ String.valueOf(checkin-1) + " AND date < " + String.valueOf(checkout+1) + ";";
		String[] ids1 = sqlMngr.searchOp(select1);
		List<Integer> selected = new ArrayList<Integer>();
		for (i=0; i < ids1.length; i++) {
			if (i+nights < ids1.length) {
				if (ids1[i].equals(ids1[i+nights])) {
					selected.add(Integer.valueOf(ids1[i]));
				}
			}
		}

		if (dist_opt.equals("1")) {
			select2 = "SELECT id, owner, city, status FROM listing;";
			ids1 = sqlMngr.searchOp3(select2);
			for (i=1; i < ids1.length+1; i+=4) {
				if (ids1[i].equals(sql_dest) != true || ids1[i+1].equals("Available") != true ||
						ids1[i+2].equals(curr_userid)) {
					selected.remove(Integer.valueOf(ids1[i-1]));
				}
			}
		}
		
		if (dist_opt.equals("2")) {
			select3 = "SELECT id, owner, street, status FROM listing;";
			ids1 = sqlMngr.searchOp2(select3);
			for (i=1; i < ids1.length+1; i+=4) {
				if (ids1[i].equals(sql_dest) != true || ids1[i+1].equals("Available") != true ||
						ids1[i+2].equals(curr_userid)) {
					selected.remove(Integer.valueOf(ids1[i-1]));
				}
			}			
		}
		
		if (dist_opt.equals("3")) {
			select4 = "SELECT id, lat, lon FROM listing;";
			select5 = "SELECT id FROM distance ORDER BY dist DESC;";
			select6 = "SELECT id FROM distance ORDER BY dist;";
			ids1 =  sqlMngr.searchOp7(select4);
			for (i=1; i < ids1.length; i+=3) {
				float new_lat = Math.abs(Float.valueOf(ids1[i]) - Float.valueOf(org_target[0]));
				float new_lon = Math.abs(Float.valueOf(ids1[i+1]) - Float.valueOf(org_target[1]));
				if (new_lat < 1 && new_lon < 1) {
					diff = (new_lat + new_lon) / 2;
					insert_query2 = "INSERT INTO distance (id,dist) VALUES ('";
					insert_query2 += ids1[i-1] + "','" + String.valueOf(diff) + "');";
					sqlMngr.insertOp(insert_query2);
				}
			}
			if (order_opt.startsWith("a") || order_opt.startsWith("A")) {
				String[] ids2 = sqlMngr.searchOp(select5);
				for (i=0; i < ids2.length; i++) {
					selected.add(Integer.valueOf(ids2[i]));
				}
			}
			else if (order_opt.startsWith("d") || order_opt.startsWith("D")) {
				String[] ids2 = sqlMngr.searchOp(select6);
				for (i=0; i < ids2.length; i++) {
					selected.add(Integer.valueOf(ids2[i]));
				}
			}
		}

		if (selected.size() != 0) {
			last_query = "SELECT * FROM listing WHERE id = " + String.valueOf(selected.get(0));
			for (i=0; i < selected.size(); i++) {
				last_query += " OR id = " + String.valueOf(selected.get(i));
			}
			if (priceordist.startsWith("p") || priceordist.startsWith("P")) {
				if (order_opt.startsWith("a") || order_opt.startsWith("A")) {
					last_query += " ORDER BY price;";
				}
				else if (order_opt.startsWith("d") || order_opt.startsWith("D")) {
					last_query += " ORDER BY price DESC;";
				}
				sqlMngr.selectOp(last_query);
			}
			if (priceordist.startsWith("d") || priceordist.startsWith("D")) {
				last_query = "SELECT * FROM listing WHERE id in (";
				for (i=0; i < selected.size()-1; i++) {
					last_query += String.valueOf(selected.get(i)) + ",";
					suffix += String.valueOf(selected.get(i)) + ",";
				}
				last_query += String.valueOf(selected.get(i)) + ") ORDER BY FIND_IN_SET (id,";
				suffix += String.valueOf(selected.get(i)) + "');";
				last_query += suffix;
				sqlMngr.selectOp(last_query);
			}
			System.out.print("Please select id you wish to book or type cancel: ");
			confirm = sc.nextLine();
			if (confirm.equals("cancel") != true) {
				for (i = checkin; i <= checkout; i++) {
					delete_query = "DELETE FROM calendar WHERE id = " + String.valueOf(confirm) +
							" AND date = " + String.valueOf(i) + ";";
					sqlMngr.insertOp(delete_query);
					insert_query = "INSERT INTO booking (id,month,date) Values ('" + 
					String.valueOf(confirm) + "','" + String.valueOf(month) + "','" + String.valueOf(i) + "');";
					sqlMngr.insertOp(insert_query);
				}
				check_query = "SELECT id FROM calendar WHERE id = " + String.valueOf(confirm) + ";";
				available = sqlMngr.checkAvailable(check_query);
				if (available == false) {
					update_query = "UPDATE listing SET status='Rented to " +
				curr_userid + "' WHERE id = "
				+ String.valueOf(confirm) + ";";
				sqlMngr.insertOp(update_query);
				}
			}
			sqlMngr.insertOp("TRUNCATE distance;");
		}
		
		if (selected.size() == 0) {
			System.out.print("");
			System.out.println("There are no available hosts. Try again later.");			
		}		
	}
	
	// function to track uses' posting along with editing price
	private void editPost() {
		String query = "";
		String last_query = "";
		int i;
		String confirm = "";
		String update_query = "";
		String price;
		query = "SELECT * FROM listing;";
		String[] ids1 = sqlMngr.searchOp4(query);
		List<Integer> selected = new ArrayList<Integer>();
		for (i=1; i < ids1.length; i+=3) {
			if (ids1[i].equals(curr_userid) && ids1[i+1].equals("Available")) {
				selected.add(Integer.valueOf(ids1[i-1]));
			}
		}
		last_query = "SELECT * FROM listing WHERE id = " + String.valueOf(selected.get(0));
		for (i=1; i < selected.size(); i++) {
			last_query += " OR id = " + String.valueOf(selected.get(i));
		}
		sqlMngr.selectOp(last_query);
		System.out.print("Please select id you wish to change price or type cancel: ");
		confirm = sc.nextLine();
		if (confirm.equals("cancel") != true) {
			System.out.print("Please enter the new price: ");
			price = sc.nextLine();
			update_query = "UPDATE listing SET price='" +
			price + "' WHERE id = "
			+ String.valueOf(confirm) + ";";
			sqlMngr.insertOp(update_query);
			System.out.print("");
			System.out.println("Price has been updated");
		}
	}
	
	// function to track uses' booking along with editing cancel booking
	private void viewRent() {
		String query = "";
		String last_query = "";
		int i;
		String confirm = "";
		String update_query = "";
		String delete_query = "";
		String insert_query = "";
		String month = "";
		String check_query = "";
		String duration = "";
		boolean available = false;
		query = "SELECT * FROM listing;";
		duration = "SELECT * FROM booking;";
		String[] ids = sqlMngr.searchOp5(query);
		String[] ids1 = sqlMngr.searchOp6(duration);
		List<Integer> selected = new ArrayList<Integer>();
		month = ids1[0];
		for (i=1; i < ids.length; i+=2) {
			if (ids[i].equals("Rented to " + curr_userid)){
				selected.add(Integer.valueOf(ids[i-1]));
			}
		}
		if (selected.size() != 0) {
			last_query = "SELECT * FROM listing WHERE id = " + String.valueOf(selected.get(0));
			for (i=1; i < selected.size(); i++) {
				last_query += " OR id = " + String.valueOf(selected.get(i));
			}
			sqlMngr.selectOp(last_query);
			System.out.print("Please select id you wish to cancel booking or type cancel: ");
			confirm = sc.nextLine();
			if (confirm.equals("cancel") != true) {
				for (i = 1; i <= ids1.length; i+=2) {
					delete_query = "DELETE FROM booking WHERE id = " + String.valueOf(confirm) +
							" AND date = " + String.valueOf(ids1[i]) + ";";
					sqlMngr.insertOp(delete_query);
					insert_query = "INSERT INTO calendar (id,month,date) Values ('" + 
					String.valueOf(confirm) + "','" + String.valueOf(month) + "','" 
							+ String.valueOf(ids1[i]) + "');";
					sqlMngr.insertOp(insert_query);
				}
				check_query = "SELECT id FROM calendar WHERE id = " + String.valueOf(confirm) + ";";
				available = sqlMngr.checkNoAvailable(check_query);
				if (available == true) {
					update_query = "UPDATE listing SET status='Available' WHERE id = "
				+ String.valueOf(confirm) + ";";
				sqlMngr.insertOp(update_query);
				System.out.println("");
				System.out.println("You have cancelled booking successfully");
				System.out.println("");
				}
			}
		} else {
			System.out.println("");
			System.out.println("You do not have booked items.");
			System.out.println("");
		}
	}
}