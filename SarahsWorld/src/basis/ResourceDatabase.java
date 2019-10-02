package basis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import quest.script.Script;
import quest.script.ScriptError;
import quest.script.ScriptParser;
import render.TexAtlas;
import render.TexFile;
import render.TexInfo;
import render.Texture;

public class ResourceDatabase {
	
	static Hashtable<String, TexFile> texFiles = new Hashtable<>();
	static Hashtable<String, TexInfo> texInfos = new Hashtable<>();
	static Hashtable<String, Texture> textures = new Hashtable<>();
	static Hashtable<String, TexAtlas> texAtlases = new Hashtable<>();
	
	static Hashtable<String, Script> scripts = new Hashtable<>();
	
	public static TexAtlas getAtlas(String key) {
		TexAtlas out = texAtlases.get(key);
		if(out == null) {
			new Exception("TexAtlas not found! " + key).printStackTrace();
		}
		return out;
	}
	public static Texture getTex(String key) {
		Texture out = textures.get(key);
		if(out == null) {
			new Exception("Texture not found! " + key).printStackTrace();
		}
		return out;
	}
	public static TexInfo getInfo(String key) {
		TexInfo out = texInfos.get(key);
		if(out == null) {
			new Exception("TexInfo not found! " + key).printStackTrace();
		}
		return out;
	}
	public static TexFile getFile(String key) {
		TexFile out = texFiles.get(key);
		if(out == null) {
			new Exception("TexFile not found! " + key).printStackTrace();
		}
		return out;
	}
	
	public static void readTexTable(String tablePath)
	{
		TexFile file;
		int[] imageCoords = {0,0,1,1};
		int partsX, partsY;
		Double offsetX, offsetY;
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(tablePath));
			int lineIndex = 0;
			String line = reader.readLine();
			while (line != null) {

				String[] words = line.split("\\s+");
				
				if(words.length != 0 && words.length != 1 && !words[0].startsWith("//")) {
				
					int i = 0;
					String type = words[i++];
					String name = words[i++];
					
					switch(type) {
					
					case "TexFile" :	texFiles.put(name, new TexFile(words[i++]));
									break;
									
					case "TexInfo" :	texInfos.put(name, new TexInfo(words[i++]));
									break;
									
					case "Texture" :	textures.put(name, new Texture(words[i++], Double.parseDouble(words[i++]), Double.parseDouble(words[i++])));
									break;
						
					case "TexAtlas" :	
										if(words.length < 11) {
											System.out.println("ERROR: Not enough arguments in " + tablePath + ", line " + lineIndex);
											break;
										}
										if(words[i].charAt(0) == '@') {
											file = texFiles.get(words[i].substring(1,words[i].length()));
										} else {
											file = new TexFile(words[i]);
										}
										i++;
										imageCoords[0] = 0;
										imageCoords[1] = 0;
										imageCoords[2] = file.width;
										imageCoords[3] = file.height;
										
										for(int j = 0; j < 4; j++, i++) {
											if(!words[i].equals("d")) //default
												imageCoords[j] = Integer.parseInt(words[i]);
										}
										
										partsX = Integer.parseInt(words[i++]);
										partsY = Integer.parseInt(words[i++]);
										offsetX = Double.parseDouble(words[i++]);
										offsetY = Double.parseDouble(words[i++]);

										
										TexAtlas atlas = new TexAtlas(file, imageCoords[0], imageCoords[1], imageCoords[2], imageCoords[3], partsX, partsY, offsetX, offsetY);
										if(words.length - i > 0) {
											TexInfo[] infos = new TexInfo[words.length - i];
											for(int j = 0; i < words.length; j++, i++) {
												infos[j] = texInfos.get(words[i].substring(1, words[i].length()));//pars start with '@' to make clear they're references 
											}
											atlas.addInfo(infos);
										}
										
										texAtlases.put(name, atlas);
										break;
					}
				}
				
				// read next line
				line = reader.readLine();
				lineIndex++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Script getScript(String name) {
		return scripts.get(name);
	}
	
	public static void readScriptTable(String tablePath) {

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(tablePath));
			String line = reader.readLine();
			while (line != null) {

				String[] words = line.split("\\s+");
				
				if(words.length != 0 && !words[0].startsWith("//")) {
					try {
						Script script = ScriptParser.parseScriptFile(words[0]);
						scripts.put(script.getName(), script);
					} catch (ScriptError e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				// read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
