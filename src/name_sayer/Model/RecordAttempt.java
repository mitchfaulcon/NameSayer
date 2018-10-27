package name_sayer.Model;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/*
 * Singleton class that handles user recording functionality for user recording attempts
 */
public class RecordAttempt {
	
	private static final RecordAttempt instance = new RecordAttempt();
	Process recordProcess;
	
	private RecordAttempt() {
		
	}
	
	public static RecordAttempt getInstance() {
		return instance;
	}

	/*
	 * begin recording user's voice
	 */
	public void recordVoice(String name) {
		//start new background thread
		Service<Void> _bgThread = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						
						//Get current date & time to use in attempt naming convention
						SimpleDateFormat formatter = new SimpleDateFormat(" (dd-MM-yyyy_HH-mm-ss)");
						Date date = new Date();
						String dateAndTime = formatter.format(date);
						String _latestCreation = name + dateAndTime;
						

						//linux process for recording the user's voice. Makes a .wav file that lasts for 5 seconds
						ProcessBuilder recordProcessBuilder = new ProcessBuilder("bash", "-c", "ffmpeg -y -f alsa -ac 1 -ar 44100 -i default \"" + _latestCreation + ".wav\" &>/dev/null");
						
						//set directory of the processBuilder
						recordProcessBuilder.directory(new File(System.getProperty("user.dir") + "/Attempts"));

						try {
							recordProcess = recordProcessBuilder.start();
							recordProcess.waitFor();
						} catch (IOException e) {
							
						}

						return null;
					}
				};
			}
		};
		_bgThread.start();
	}

	/*
	 * stop recording user's voice
	 */
	public void stopRecording() {
		//kill all ffmpeg processes
		ProcessBuilder stopRecordingBuilder = new ProcessBuilder("bash", "-c", "killall ffmpeg");
		Process stopRecordingProcess;
		try {
			stopRecordingProcess = stopRecordingBuilder.start();
			stopRecordingProcess.waitFor();
		} catch (IOException | InterruptedException e) {
		}
	}

}
