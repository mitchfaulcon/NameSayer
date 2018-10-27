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

import javax.sound.sampled.*;

import javafx.concurrent.Task;
import name_sayer.Controller.PracticeController;


/*
 * Singleton class that handles playing classes
 */
public class PlayCreation {

	private static final PlayCreation instance = new PlayCreation();

	private static HashMap<String,String> _attemptNames;

	private static ArrayList<String> lastPlayed = new ArrayList<>();

	private AudioRating audioRating = AudioRating.getInstance();
	private Settings settings = Settings.getInstance();
	private FilenameMaps filenameMaps = FilenameMaps.getInstance();


	private PlayCreation(){
		_attemptNames = filenameMaps.getAttemptMap();
	}

	public static PlayCreation getInstance(){
		return instance;
	}

	//template method for playing names in the database
	public void playName(String item) {
		lastPlayed.clear();
		if (isSingleName(item)) {
			playSingleName(item);
		} else {
			ArrayList<String> nameList = listNames(item);
			playFullName(nameList);
		}
	}

	//play user recording attempt file
	public void playRecordingAttempt(String item) {
		String fileName = _attemptNames.get(item);
		String filePath = "/"+System.getProperty("user.dir") + "/Attempts/" + fileName;
		playSingleFile(filePath);
	}


	//plays the audio clip of the recording
	private void playSingleFile(String nameLocation){
		try {
			File nameFile = new File(nameLocation);
			RemoveWhiteNoise(nameFile);
			normalizeAudio(nameFile);
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(nameFile);
			AudioFormat format = inputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);

			clip.open(inputStream);

			float playbackVolume = settings.getPlaybackVolume();

			//Mute clip if slider value is set to minimum, otherwise change volume of playback
			if (playbackVolume==-46.0206f){
				BooleanControl mute = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
				mute.setValue(true);
			} else {
				FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				volume.setValue(playbackVolume);
			}
			clip.start();


		} catch (FileNotFoundException e){
		} catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
		}
	}

	//plays a single name by finding the best rated version of the name
	private void playSingleName(String name){

		ArrayList<String> bestRated = audioRating.getBestRated(name);

		//Only play name if it exists
		if (bestRated.size()>0) {
			//get random index of recording to play
			int index = new Random().nextInt(bestRated.size());
			String fileName = bestRated.get(index);

			//Play correct file and add it to the 'last played' list
			String nameLocation = settings.getDirectory() + "/" + fileName;
			lastPlayed.add(fileName);
			playSingleFile(nameLocation);

			//add filename to FilesPlayed.txt
			AddToFilesPlayedTxt(name, fileName);
		} 
	}

	private void playFullName(ArrayList<String> filePaths) {

		Task task = new Task() {
			@Override
			protected Object call() throws Exception {
				for (String fileName: filePaths) {
					//if (_creationNames.containsKey())
					if (checkExists(fileName)) {
						playSingleName(fileName);
						try {
							Thread.sleep(2000);
						}
						catch (InterruptedException e) {

						}
					}
				}
				return null;
			}
		};
		new Thread(task).start();
	}


	/*
	 * This method detects if the item is a single name or if it's a complex name. returns true if item is a single name
	 * that is, contains no space or hyphen.
	 */
	private boolean isSingleName(String item) {
		if (!item.contains(" ") && !item.contains("-")) {
			return true;
		} else {
			return false;
		}
	}



	public static ArrayList<String> getLastPlayed(){
		return lastPlayed;

	}

	private ArrayList<String> listNames(String item) {
		String[] splitNames = item.split("[\\s+-]");
		ArrayList<String> namesList = new ArrayList<String>();
		for (String name: splitNames) {
			namesList.add(name);
		}
		return namesList;
	}

	//this method plays the database recording of a name, and then the user recording 
	//repeatedly 'numOfLoops' times
	public void playLoop(String creation, String attempt, int numOfLoops){

		Task task = new Task() {
			@Override
			protected Object call() throws Exception {
				for (int i=0;i<numOfLoops;i++){
					playName(creation);
					try {
						Thread.sleep(2000);
					}
					catch (InterruptedException e){

					}
					playRecordingAttempt(attempt);
					try {
						Thread.sleep(2000);
					}
					catch (InterruptedException e){

					}
				}
				return null;
			}
		};

		new Thread(task).start();
	}

	
	/*
	 *
	 * 1. every time user plays a name; check if file names exist in 'FilesPlayed'
	 * e.g. if user plays Li3, check for Li1 and Li2 file names
	 * 2. delete that line, and append played version to the file
	 */
	private void AddToFilesPlayedTxt(String name, String fileName) {


		//check if file name exists in FilesPlayed.txt
		File file = new File(".FilesPlayed.txt");
		String lineToDelete = null;
		try {

			Scanner sc = new Scanner(file);

			//search for file name of the recording in FilesPlayed.txt
			while (sc.hasNextLine()) {

				String nextLine = sc.nextLine();
				if (nextLine.toLowerCase().contains("_"+name.toLowerCase()+".wav")) {
					lineToDelete = nextLine;

					break;
				}
			}
			sc.close();

			if (lineToDelete != null) {
				//remove lineToDelete (recording filename) from .FilesPlayed.txt
				removeLine(".FilesPlayed.txt", lineToDelete);
			}
		}
		catch (IOException e) {
		}

		//append currently played version to .FilesPlayed.txt
		try(FileWriter fw = new FileWriter(".FilesPlayed.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw))
		{
			out.println(fileName);
		} catch (IOException e) {
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

	//remove the white noise of an audio file- method is executed before a database name is played
	private void RemoveWhiteNoise(File audioFile) {
		ProcessBuilder whiteNoiseBuilder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -hide_banner -i "
				+ audioFile.getPath() + " -af " + "silenceremove=1:0:-35dB:1:5:-35dB:0 " + audioFile.getPath());

		try {
			Process whiteNoiseProcess = whiteNoiseBuilder.start();
			whiteNoiseProcess.waitFor();
		} catch (InterruptedException | IOException e) {
			
		}
	}
	
	private void normalizeAudio(File audioFile) {
		ProcessBuilder normalizeAudioBuilder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -i " +
				audioFile.getPath() + " -af dynaudnorm " + audioFile.getPath());
		Process normalizeAudioProcess;
		try {
			normalizeAudioProcess = normalizeAudioBuilder.start();
			normalizeAudioProcess.waitFor();
		} catch (IOException | InterruptedException e) {
			
		}
	}
	
	

	/*
	 * check if name exists in database
	 */
	private boolean checkExists(String name) {

		for (String databaseName: PracticeController.getStaticCreationList()) {
			if (name.toLowerCase().equals(databaseName.toLowerCase())) {
				return true;
			}
		}

		return false;
	}
}
