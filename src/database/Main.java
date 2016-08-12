package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		CommandLine commandLine = new CommandLine();
		int start = commandLine.startSession();
		if (start != 0 && commandLine.execute(start)) {
			commandLine.endSession();
		}
	}
}
