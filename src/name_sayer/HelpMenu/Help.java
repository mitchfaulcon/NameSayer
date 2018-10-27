package name_sayer.HelpMenu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



public class Help {
	
	
	private static final Help instance = new Help();
	
	private Help() {
		
	}
	
	public static Help getInstance() {
		return instance;
	}
	
	
	
	public void createFileOnLaunch() {
		try	{
			FileWriter launchFW = new FileWriter(".ProgrammeLaunch.txt", true);

			launchFW.close();

		} catch (IOException e) {
			
		}
	}

	//Returns true if first time launching program
	public boolean checkFirstLaunch(){
		File launchFile = new File(".ProgrammeLaunch.txt");
		return !launchFile.exists();
	}

}
