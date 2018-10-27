package name_sayer.Model;

/*
 * Singleton class that handles user settings
 */
public class Settings {
    private static Settings instance = new Settings();

    //Setting screen options initially set to default values
    private double volume = 100.0;
    private String directory = System.getProperty("user.dir") + "/Names";
    private int timesToLoop = 2;

    public static Settings getInstance() {
        return instance;
    }

    private Settings() {
    }


    public void changeDirectory(String newDirectory){
        directory = newDirectory;
    }
    
    public String getDirectory(){
        return directory;
    }


    public void changeVolume(double newVolume){
        volume = newVolume;
    }
    
    public double getVolume(){
        return volume;
    }
    
    public float getPlaybackVolume(){
        //Convert volume from a scale of 0 - 100 to a scale of 0 - 46.0206
        double playbackVolume = volume * (40 + 6.0206)/100;

        //Return a gain between -46.0206db and 0db
        return (float) (playbackVolume - 46.0206);
    }

    /*
     * timesToLoop represents the number of times to loop when the 'compare' function is used in the practice module
     */
    public void changeTimesToLoop(int newTimesToLoop){
        timesToLoop = newTimesToLoop;
    }
    public int getTimesToLoop(){
        return timesToLoop;
    }

}
