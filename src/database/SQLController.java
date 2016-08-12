package database;

import java.security.interfaces.RSAKey;
import java.sql.*;
import java.util.ArrayList;

/*
 * This class acts as the medium between our CommandLine interface
 * and the SQL Backend. It is a controller class.
 */
public class SQLController {
	
	private static final String dbClassName = "com.mysql.jdbc.Driver";
	private static final String CONNECTION = "jdbc:mysql://127.0.0.1/";
    //Object that establishes and keeps the state of our application's
    //connection with the MySQL backend.
	private Connection conn = null;
    //Object which communicates with the SQL backend delivering to it the
    //desired query from our application and returning the results of this
    //execution the same way that are received from the SQL backend.
	private Statement st = null;
	private CommandLine cmd_line = new CommandLine();
	
    // Initialize current instance of this class.
	public boolean connect(String[] cred) throws ClassNotFoundException {
		Class.forName(dbClassName);
		boolean success = true;
		String user = cred[0];
		String pass = cred[1];
		String connection = CONNECTION + cred[2];
		try {
			conn = DriverManager.getConnection(CONNECTION, user, pass);
			st = conn.createStatement();
			st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + cred[2] + ";");
			conn = DriverManager.getConnection(connection, user, pass);
			st = conn.createStatement();
			//conn = DriverManager.getConnection(connection, user, pass);
		} catch (SQLException e) {
			success = false;
			System.err.println("Connection could not be established!");
			e.printStackTrace();
		}
		return success;
	}

    // Destroy the private objects/fields of current instance of this class.
    // Acts like a destructor.
	public void disconnect() {
		try {
			st.close();
			conn.close();
		} catch (SQLException e) {
			System.err.println("Exception occured while disconnecting!");
			e.printStackTrace();
		} finally {
			st = null;
			conn = null;
		}
	}
	
	// To check if user_id exists on the database along with correct password.
	public int checkUser(String[] query) {
		int i;
		String sql1 = "INSERT INTO accounts (id,password,name,addr,dob,occup,sin,cc) VALUES (";
		String sql2 = "";
		String fin_sql = "";
		if (query.length == 2) {
			try {
				if (query[0].equals("serviceadmin")) {
					cmd_line.user_id(query[0]);
					return 2;
				}
				String cmd = "SELECT id, password FROM accounts";
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(cmd);
				while (rs.next()) {
					String id = rs.getString("id");
					String password = rs.getString("password");
					if (id.matches(query[0]) && password.matches(query[1])) {
						cmd_line.user_id(query[0]);
						return 1;
					}
					
				}
			} catch (SQLException e) {
				System.err.print("Error occurred during checking credential.");
				e.printStackTrace();
				return 0;
			}
		} else {
			if (query[0].equals("invalid")) {
				return 3;
			}
			cmd_line.user_id(query[0]);
			for (i = 0; i < query.length - 1; i++) {
				sql2 = sql2.concat("'" + query[i] + "',");
			}
			sql2 = sql2.concat("'" + query[i] + "');");
			fin_sql = sql1 + sql2;
			this.insertOp(fin_sql);
			return 1;
		}
		return 0;
	}
	
	// Helper function to maintain current user_id
	public int get_id(String query) {
		int id = 0;
		try {
		    Statement st = conn.createStatement();
		    // execute the query, and get a java resultset
		    ResultSet rs = st.executeQuery(query);
		    while (rs.next()){
		        id = rs.getInt(1);
		    }
		    rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered during Select execution!");
			e.printStackTrace();
		}
		System.out.println();
		return id;
	} 	

    //Controls the execution of an insert query.
    //Functionality: "1. Insert a record."
	public int insertOp(String query) {
		int rows = 0; 
		try {
			rows = st.executeUpdate(query);
		} catch (SQLException e) {
			System.err.println("Exception triggered during Insert execution!");
			e.printStackTrace();
		}
		return rows;
	}
	
    //Controls the execution of a select query.
    //Functionality: "2. Select a record."
	public void selectOp(String query) {
		try {
			ResultSet rs = st.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int colNum = rsmd.getColumnCount();
			System.out.println("");
			for (int i = 0; i < colNum; i++) {
				System.out.print(rsmd.getColumnLabel(i+1) + "\t");
			}
			System.out.println("");
			while(rs.next()) {
				for (int i = 0; i < colNum; i++) {
					System.out.print(rs.getString(i+1) + "\t");
				}
				System.out.println("");
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered during Select execution!");
			e.printStackTrace();
		}
		System.out.println();
	}
	
    // Controls the execution of functionality: "3. Print schema."
	public ArrayList<String> getSchema() {
		ArrayList<String> output = new ArrayList<String>();
		try {
			DatabaseMetaData meta = conn.getMetaData();
			ResultSet schemas = meta.getTables(null,null,"%",null);
			//ResultSet catalogs = meta.getCatalogs();
			while (schemas.next()) {
				output.add(schemas.getString("TABLE_NAME"));
			}
			schemas.close();
		} catch (SQLException e) {
			System.err.println("Retrieval of Schema Info failed!");
			e.printStackTrace();
			output.clear();
		}
		return output;
	}
	
    // Controls the execution of functionality: "4. Print table schema."
	public ArrayList<String> colSchema(String tableName) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			DatabaseMetaData meta = conn.getMetaData();
			ResultSet rs = meta.getColumns(null, null, tableName, null);
			while(rs.next()) {
				result.add(rs.getString(4));
				result.add(rs.getString(6));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Retrieval of Table Info failed!");
			e.printStackTrace();
			result.clear();
		}
		return result;
	} 

	// Controls the execution of functionality: "5. Create Table."
	public void createT(String query) {
		try {
			st.executeUpdate(query);
		    System.out.println("Created table in given database...");
		} catch (SQLException e) {
			System.err.println("Exception triggered during Creating Table!");
			e.printStackTrace();
		}
	}
	
	// Controls the execution of functionality: "6. Show Table."
	public void checkAccounts(String query) {
		try {
			String cmd = "SELECT * FROM " + query;
		    Statement st = conn.createStatement();
		    // execute the query, and get a java resultset
		    ResultSet rs = st.executeQuery(cmd);
		    System.out.println("User_ID\t||\tUser_password\t||\tName\t||\tAddress\t"
		    		+ "||\tDate of Birth\t||\tOccupation\t||\tSIN#\t||\tCredit Card Information");
		    while (rs.next()){
		        String id = rs.getString("id");
		        String password = rs.getString("password");
		        String name = rs.getString("name");
		        String addr = rs.getString("addr");
		        String dob = rs.getString("dob");
		        String occup = rs.getString("occup");
		        String sin = rs.getString("sin");
		        String cc = rs.getString("cc");
		        
		        // print the results
		        System.out.format("%s\t||\t%s\t||\t%s\t||\t%s\t||\t%s\t||"
		        		+ "\t%s\t||\t%s\t||\t%s\n", id, password, name, addr, dob, occup, sin, cc);
		    }
		    rs.close();
		} catch (Exception e) {
			System.err.println("Got an exception! ");
		    System.err.println(e.getMessage());
		}
	}
	
	// Controls the execution of functionality: "7. Delete Table."
	public void deleteTableop(String query) {
		try {
			Statement st = conn.createStatement();
			st.executeUpdate(query);
		} catch (SQLException e) {
			System.err.println("Exception occured during deletion");
			System.err.println(e.getMessage());
		}
	}
	
	// Helper function for serviceadmin to maintain all the listing
	public void checkListing(String query) {
		try {
			String cmd = "SELECT * FROM " + query;
		    Statement st = conn.createStatement();
		    // execute the query, and get a java resultset
		    ResultSet rs = st.executeQuery(cmd);
		    while (rs.next()){
		        String id = String.valueOf(rs.getInt("id"));
		        String type = rs.getString("type");
		        String owner = rs.getString("owner");
		        String lat = rs.getString("lat");
		        String lon = rs.getString("lon");
		        String street = rs.getString("street");
		        String city = rs.getString("city");
		        String country = rs.getString("country");
		        String price = rs.getString("price");
		        String status = rs.getString("status");
		        // print the results
		        System.out.format("%s\t||\t%s\t||\t%s\t||\t%s\t||\t%s\t||"
		        		+ "\t%s\t||\t%s\t||\t%s\t||\t%s\t||\t%s\n", id, 
		        		type, owner, lat, lon, street, city, country, price, status);
		    }
		    rs.close();
		} catch (Exception e) {
			System.err.println("Got an exception! ");
		    System.err.println(e.getMessage());
		}
	}
	
	// Controls amenities table.
	public void checkAmenities(String query) {
		try {
			String cmd = "SELECT * FROM " + query;
		    Statement st = conn.createStatement();
		    // execute the query, and get a java resultset
		    ResultSet rs = st.executeQuery(cmd);
		    while (rs.next()){
		        String id = String.valueOf(rs.getInt("id"));
		        String amen = rs.getString("amen");
		        System.out.format("%s\t||\t%s\n", id, amen);
		    }
		    rs.close();
		} catch (Exception e) {
			System.err.println("Got an exception! ");
		    System.err.println(e.getMessage());
		}
	}
	
	// Helper function to use date.
	public void checkCalendar(String query) {
		try {
			String cmd = "SELECT * FROM " + query;
		    Statement st = conn.createStatement();
		    // execute the query, and get a java resultset
		    ResultSet rs = st.executeQuery(cmd);
		    while (rs.next()){
		        String id = String.valueOf(rs.getInt("id"));
		        String month = rs.getString("month");
		        String date = rs.getString("date");
		        // print the results
		        System.out.format("%s\t||\t%s\t||\t%s\n", id, month, date);
		    }
		    rs.close();
		} catch (Exception e) {
			System.err.println("Got an exception! ");
		    System.err.println(e.getMessage());
		}
	}
	
	// Helper function to find id of available list"
	public String[] searchOp(String query) {
		try {
			String ids = "";
		    Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				ids += String.valueOf(rs.getString("id")) + ",";
			}
			String[] selected = ids.split(",");
			rs.close();
			return selected;
		} catch (SQLException e) {
			System.err.println("Exception triggered during Select execution!");
			e.printStackTrace();
		}
		System.out.println();
		return null;
	}
	
	// Controls the execution of functionality: "Search by address."
	public String[] searchOp2(String query) {
		try {
			String ids = "";
		    Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				ids += String.valueOf(rs.getString("id")) + ",";
				ids += String.valueOf(rs.getString("street")) + ",";
				ids += String.valueOf(rs.getString("status")) + ",";
				ids += String.valueOf(rs.getString("owner")) + ",";
			}
			String[] selected = ids.split(",");
			rs.close();
			return selected;
		} catch (SQLException e) {
			System.err.println("Exception triggered during Select execution!");
			e.printStackTrace();
		}
		System.out.println();
		return null;
	}
	
	// Controls the execution of functionality: "Search by city."
	public String[] searchOp3(String query) {
		try {
			String ids = "";
		    Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				ids += String.valueOf(rs.getString("id")) + ",";
				ids += String.valueOf(rs.getString("city")) + ",";
				ids += String.valueOf(rs.getString("status")) + ",";
				ids += String.valueOf(rs.getString("owner")) + ",";
			}
			String[] selected = ids.split(",");
			rs.close();
			return selected;
		} catch (SQLException e) {
			System.err.println("Exception triggered during Select execution!");
			e.printStackTrace();
		}
		System.out.println();
		return null;
	}
	
	// Check if the queried post has room for booking."	
	public boolean checkAvailable(String query) {
		try {
			boolean status = true;
			String id = "";
		    Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				id = String.valueOf(rs.getString("id"));
			}
			if (id.equals("")) {
				status = false;
			}
			rs.close();
			return status;
		} catch (SQLException e) {
			System.err.println("Exception triggered during Select execution!");
			e.printStackTrace();
		}
		System.out.println();
		return true;
	}
	
	// Controls the execution of functionality: "edit price"
	public String[] searchOp4(String query) {
		try {
			String ids = "";
		    Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				ids += String.valueOf(rs.getString("id")) + ",";
				ids += String.valueOf(rs.getString("owner")) + ",";
				ids += String.valueOf(rs.getString("status")) + ",";
			}
			String[] selected = ids.split(",");
			rs.close();
			return selected;
		} catch (SQLException e) {
			System.err.println("Exception triggered during Select execution!");
			e.printStackTrace();
		}
		System.out.println();
		return null;
	}
	
	// Controls the execution of functionality: "cancel booking"
	public String[] searchOp5(String query) {
		try {
			String ids = "";
		    Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				ids += String.valueOf(rs.getString("id")) + ",";
				ids += String.valueOf(rs.getString("status")) + ",";
			}
			String[] selected = ids.split(",");
			rs.close();
			return selected;
		} catch (SQLException e) {
			System.err.println("Exception triggered during Select execution!");
			e.printStackTrace();
		}
		System.out.println();
		return null;
	}
	
	public String[] searchOp6(String query) {
		try {
			String ids = "";
		    Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				ids += rs.getString("month") + ",";
				ids += rs.getString("date") + ",";
			}
			String[] selected = ids.split(",");
			rs.close();
			return selected;
		} catch (SQLException e) {
			System.err.println("Exception triggered during Select execution!");
			e.printStackTrace();
		}
		System.out.println();
		return null;
	}
	
	public String[] searchOp7(String query) {
		try {
			String ids = "";
		    Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				ids += rs.getString("id") + ",";
				ids += rs.getString("lat") + ",";
				ids += rs.getString("lon") + ",";
			}
			String[] selected = ids.split(",");
			rs.close();
			return selected;
		} catch (SQLException e) {
			System.err.println("Exception triggered during Select execution!");
			e.printStackTrace();
		}
		System.out.println();
		return null;
	}
	
	// Check if the queried post has room for booking."	
	public boolean checkNoAvailable(String query) {
		try {
			boolean status = true;
			String id = "";
		    Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				id = String.valueOf(rs.getString("id"));
			}
			if (id.equals("")) {
				status = false;
			}
			rs.close();
			return status;
		} catch (SQLException e) {
			System.err.println("Exception triggered during Select execution!");
			e.printStackTrace();
		}
		System.out.println();
		return true;
	}
}