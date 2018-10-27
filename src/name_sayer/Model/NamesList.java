package name_sayer.Model;

import name_sayer.Controller.PracticeController;

import java.util.ArrayList;

/*
 * Singleton class that contains array lists:
 * -all names passed in
 * -names that exist in the database
 * -names that don't exist in the database
 */
public class NamesList {


	private ArrayList<String> allItems = new ArrayList<>();
	private ArrayList<String> goodItems = new ArrayList<>();
	private ArrayList<String> errorItems = new ArrayList<>();



	private static final NamesList instance = new NamesList();

	private NamesList() {

	}

	public static NamesList getInstance() {
		return instance;
	}

	/*
	 * Fix items in input ArrayList and add items to 'goodItems' ArrayList containing only the names that exist in the database
	 */
	public void checkItems(ArrayList<String> inputArrayList) {
		String currentItem;
		
		//add all items passed in to 'allitems' arraylist
		for (int i=0; i<inputArrayList.size(); i++) {
			allItems.add(inputArrayList.get(i));
		}

		for (String item: allItems) {
			
			//break down item into strings, separated by '-' and '[space]'
			currentItem = item;
			String[] splitNames = item.split("[\\s+-]");

			for (String name: splitNames) {
				//check if name exists in database
				if (!checkExists(name)) {
					//if name doesn't exist in database add to 'errorItems' arraylist
					if (!(name.length()==3&&name.startsWith("(")&&name.endsWith(")"))) {
						errorItems.add(name);
					}
					//if name doesn't exist in database
					//remove it from currentItem
					currentItem = currentItem.replace(name+" ", "");
					currentItem = currentItem.replace(" "+name, "");
					currentItem = currentItem.replace(name+"-", "");
					currentItem = currentItem.replace("-"+name, "");
					currentItem = currentItem.replace(name,"");

				}
			}
			
			//add non-empty string to goodItems arraylist
			//this string should contain names only in the database
			if (!currentItem.equals("")) {
				goodItems.add(currentItem);
			}		
		}
	}
	
	/*
	 * return arraylist containing names existing in the database
	 * repeated names are included once
	 */
	public ArrayList<String> checkItemForRating(String item) {
	
		ArrayList<String> existingNames = new ArrayList<String>();
		
		//split item into names that exist in the database
		String[] splitNames = item.split("[\\s+-]");

		for (String name: splitNames) {
			//check if name exists in database and if it hasn't already been added to 'existinNames' before
			if (checkExists(name) && !existingNames.contains(name)) {
				existingNames.add(name);
			}
		}
		
		return existingNames;
	}
	
	
	/*
	 * return arraylist of items existing in database
	 */
	public ArrayList<String> getGoodItems(){
		return goodItems;
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
	
	//return a string of concatenated names in the errorItems arraylist
	public String getErrorItems() {
		StringBuilder allItems = new StringBuilder("\n");
		for (String item:errorItems){
			allItems.append("\n").append(item);
		}
		return allItems.toString();
	}
	
	//clear all arraylists
	public void clearLists(){
	    allItems.clear();
	    goodItems.clear();
	    errorItems.clear();
    }
	
	
	
	
}
