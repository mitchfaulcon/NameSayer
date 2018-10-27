package name_sayer.Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.fxml.FXML;

/*
 * Singleton class that contains :
 * a map linking the name of a recording to its file name
 * a map linking the name of attempts to its file name                                     
 */
public class FilenameMaps {
	
	private static FilenameMaps instance = new FilenameMaps();
	// < displayName, fileName >
	private static TreeMap<String,String> _creationNames = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private static HashMap<String,String> _attemptNames = new HashMap<>();
	
	private FilenameMaps() {
		
	}
	
	public static FilenameMaps getInstance() {
		return instance;
	}
	
	
	//list all current creations on the creationList
	@FXML
	public void listNames(String directory) {

		try {
			//linux process to display all .wav types without .wav extension
			ProcessBuilder listProcessBuilder = new ProcessBuilder("bash", "-c", "ls | grep .wav");
			listProcessBuilder.directory(new File(directory));
			Process listProcess = listProcessBuilder.start();

			InputStream stdout = listProcess.getInputStream();

			try {
				listProcess.waitFor();
			} catch (InterruptedException ex1) {
			}

			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));

			String line = null;

			if (directory.equals(System.getProperty("user.dir") + "/Attempts")){
				//Add items to past practice attempts list
				while ((line = stdoutBuffered.readLine()) != null) {
					addAttempt(line);
				}
			} else {
				//add items to creationList
				while ((line = stdoutBuffered.readLine()) != null) {
					addCreation(line);
				}
			}
		}
		catch (IOException ex2) {
		}
	}
	
	private void addCreation(String fileName){
		String displayName;

		//Get all the letters after the last underscore and before the .wav
		Pattern pattern = Pattern.compile("_([a-zA-Z]*)\\.wav");
		Matcher matcher = pattern.matcher(fileName);
		if (matcher.find()){
			displayName = matcher.group(1);
		} else {
			displayName = fileName;
		}

		String onlyName = displayName;

		//Change name to 'Name (version)' if duplicate
		int duplicate=0;
		while (_creationNames.containsKey(displayName)){
			duplicate++;
			displayName = onlyName + " (" + duplicate + ")";
		}

		//Add name to creationNames hashmap
		_creationNames.put(displayName,fileName);
	}

	public void clearCreationNames() {
		_creationNames.clear();
	}

	public Set getCreationNamesEntrySet() {
		return _creationNames.entrySet();
	}

	public void putInAttemptNames(String displayName, String fileName) {
		_attemptNames.put(displayName, fileName);
	}
	
//	<display name, file name>
	public TreeMap<String,String> getCreationMap(){
		return _creationNames;
	}

	public HashMap<String,String> getAttemptMap(){
		return _attemptNames;
	}
	
	private void addAttempt(String fileName){
		String displayName = null;
		//Get all characters before the .wav
		int pos = fileName.lastIndexOf(".");
		if (pos > 0) {
			displayName = fileName.substring(0, pos);
		}

		//Add attempt to attempt map
		putInAttemptNames(displayName, fileName);
	}

	public Set getAttemptsEntrySet() {
		return _attemptNames.entrySet();
	}

	public void clearAttemptNames(){
		_attemptNames.clear();
	}
}
