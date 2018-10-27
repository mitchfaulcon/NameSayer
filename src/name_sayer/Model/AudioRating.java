package name_sayer.Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import name_sayer.Controller.PracticeController;

/*
 * Singleton class that contains a hashmap linking the name of the recording to its rating
 */
public class AudioRating {

	// hashmap containing rating for filename <filename, rating>
	private static HashMap<String, Rating> ratingList = new HashMap<String, Rating>();
	private static final AudioRating instance = new AudioRating();
	private FilenameMaps filenameMaps = FilenameMaps.getInstance();
	private String itemToRate;
	
	
	private final String BAD_CREATIONS_TEXT_FILE = "BADCreations.txt";
	private final String GOOD_CREATIONS_TEXT_FILE = "GOODCreations.txt";
	
	private enum Rating {
		UNDEFINED, BAD, GOOD;
	}

	private AudioRating(){
		
		try	{
			FileWriter Badfw = new FileWriter(BAD_CREATIONS_TEXT_FILE, true);
			FileWriter Goodfw = new FileWriter(GOOD_CREATIONS_TEXT_FILE, true);

			Badfw.close();
			Goodfw.close();
		} catch (IOException e) {
		}
	}

	public static AudioRating getInstance(){
		return instance;
	}


	/*
	 * 1. read filesPlayed.txt, check if user has played the name before, and get the filename of the version played
	 * 2. rate that file name as 'good'- print to file and change rating hashmap
	 * 3. if it doesn't exist: prompt user to play name
	 */
	public boolean RateQualityGood(String name) {
		String item = ReadFilesPlayed(name);
		if (item == null) {
			return false;
		}

		//never rated before
		if (ratingList.get(item) == Rating.UNDEFINED) {
			ratingList.put(item, Rating.GOOD);
			PrintRatingToFile(item, Rating.GOOD);

		} else if (ratingList.get(item) == Rating.BAD) {
			//delete name from BADCreations.txt
			removeLine(BAD_CREATIONS_TEXT_FILE, item);

			//give good rating
			ratingList.put(item, Rating.GOOD);
			PrintRatingToFile(item, Rating.GOOD);

		} else if (ratingList.get(item) == Rating.GOOD) {
			//do nothing
		}
		
		return true;
	}
	
	
	/*
	 * 1. read FilesPlayed.txt, check if user has played the name before, and get the filename of the version played
	 * 2. rate that file name as 'bad'- print to file and change rating hashmap
	 * 3. if it doesn't exist: prompt user to play name
	 */
	public boolean RateQualityBad(String name) {
		//read file name from FilesPlayed.txt
		String item = ReadFilesPlayed(name);
		if (item == null) {
			return false;
		}

		//never rated before
		if (ratingList.get(item) == Rating.UNDEFINED) {
			//give bad rating
			ratingList.put(item, Rating.BAD);
			PrintRatingToFile(item, Rating.BAD);
		} else if (ratingList.get(item) == Rating.BAD) {
			//do nothing
		} else if (ratingList.get(item) == Rating.GOOD) {
			//delete name from GOODCreations.txt
			removeLine(GOOD_CREATIONS_TEXT_FILE, item);

			//Give bad rating
			ratingList.put(item, Rating.BAD);
			PrintRatingToFile(item, Rating.BAD);
		}
		
		return true;
	}



	//prints FILE name of recording to text file.
	private void PrintRatingToFile(String creationName, Rating rating) {
		try(FileWriter fw = new FileWriter(rating.toString()+"Creations.txt", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw))
		{
			out.println(creationName);
			//fw.close();
		} catch (IOException e) {

		}
	}


	//Read the names of creations that have already been rated. Called upon initialisation to replace default value in hashmap
	public void ReadCreations() {
		Rating[] ratings = {Rating.BAD, Rating.GOOD};
		for (Rating rating: ratings) {
			try (BufferedReader br = new BufferedReader(new FileReader(rating.toString() + "Creations.txt"))) {
				String line;
				while ((line = br.readLine()) != null) {
					if (rating.equals(Rating.BAD)) {
						//if name of recording is in BADCreations.txt, give recording a bad rating
						ratingList.put(line, Rating.BAD);
					} else {
						//if name of recording is in GOODCreations.txt, give recording a bad rating
						ratingList.put(line, Rating.GOOD);
					}
				}
			} catch (FileNotFoundException e) {

			} catch (IOException e) {

			}
		}
	}


	//give a creating a rating in the rating list
	private void PutInRatingList(String creation, Rating rating) {
		ratingList.put(creation, rating);
	}
	
	//clear the contents of GOODCreations.txt and BadCreations.txt
	public void clearRatingFiles() {
		PrintWriter badPW;
		PrintWriter goodPW;
		try {
			badPW = new PrintWriter(BAD_CREATIONS_TEXT_FILE);
			goodPW = new PrintWriter(GOOD_CREATIONS_TEXT_FILE);
			badPW.close();
			goodPW.close();
		} catch (FileNotFoundException e) {
		}
	}

	//give all recordings an 'undefined' rating
	public void clearRatings() {
		for (String creation: PracticeController.getStaticCreationList()) {
			PutInRatingList(filenameMaps.getCreationMap().get(creation), Rating.UNDEFINED);
		}
	}



	/*
	 * removes line from a file (text file) by creating a temp.txt file, copying all the text and skipping when 'line' occurs.
	 * deletes old file, and rename temp.txt to old file name 
	 */
	private void removeLine(String file, String lineToRemove) {

		try {

			File inFile = new File(file);

			if (!inFile.isFile()) {

				return;
			}

			//Construct the new file that will later be renamed to the original filename.
			File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

			BufferedReader br = new BufferedReader(new FileReader(file));
			PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

			String line = null;

			//Read from the original file and write to the new
			//unless content matches data to be removed.
			while ((line = br.readLine()) != null) {

				if (!line.trim().equals(lineToRemove)) {

					pw.println(line);
					pw.flush();
				}
			}
			pw.close();
			br.close();

			//Delete the original file
			if (!inFile.delete()) {

				return;
			}

			//Rename the new file to the filename the original file had.
			if (!tempFile.renameTo(inFile)) {
				
			}


		}
		catch (FileNotFoundException ex) {
		}
		catch (IOException ex) {

		}
	}

	//return the best rated filenames of a given name
	//returns a list of all names that are rated either 'good' or 'unrated',
	//if no names are rated 'good'/'unrated', return a list of all names (rated bad)
	public ArrayList<String> getBestRated(String name){
		ArrayList<String> bestRated = new ArrayList<>();
		ArrayList<String> allName = new ArrayList<>();

		//return arraylist of all items containing input name that arent rated bad
		Set set = ratingList.entrySet();
		Iterator iterator = set.iterator();

		while(iterator.hasNext()) {

			Map.Entry mentry = (Map.Entry)iterator.next();
			String fileName = (String) mentry.getKey();
			if (fileName.toLowerCase().contains("_"+name.toLowerCase()+".")) {

				if (mentry.getValue()==Rating.BAD){
					allName.add(fileName);
				} else {
					bestRated.add(fileName);
				}
			}
		}

		if (bestRated.size()==0){
			return allName;
		} else {
			return bestRated;
		}
	}

	//setter for the name to be rated
	public void setItemToRate(String item) {
		itemToRate = item;
	}
	
	//get the name to be rated
	public String getItemToRate() {
		return itemToRate;
	}

	//returns the filename of the last played version of a name
	//returns null if the name has never been played
	private String ReadFilesPlayed(String name) {
		File file = new File(".FilesPlayed.txt");
		Scanner sc;
		try {
			sc = new Scanner(file);

			while (sc.hasNextLine()) {
				String nextLine = sc.nextLine();
				if (nextLine.toLowerCase().contains("_"+name.toLowerCase()+".wav")) {
					sc.close();
					return nextLine;
				}

			}
			sc.close();


		} catch (FileNotFoundException e) {
		}
		
		return null;
	}

}
