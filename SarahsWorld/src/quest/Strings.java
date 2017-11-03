package quest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;

public class Strings {

	static Hashtable<String, String[]> strings = new Hashtable<>();
	static {
		try {
			FileReader fileReader = new FileReader("res/quest/Strings.txt");
			BufferedReader reader = new BufferedReader(fileReader);

			String line = "";
			
			while((line = reader.readLine()) != null){
				String[] data = line.replace("\"", "").replace("; ", ";").split("=");
				strings.put(data[0], data[1].split(";"));
			}
			
			fileReader.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String get(String key, Random random){
		String[] selection = strings.get(key);
		return selection[random.nextInt(selection.length)];
	}
	
}
