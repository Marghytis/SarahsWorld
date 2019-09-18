package menu;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F10;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F11;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F12;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_J;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ADD;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_MINUS;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.glfwGetKeyName;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;

import extra.Main;
import util.math.Vec;

public class Settings {

	static Hashtable<String, Boolean> booleans = new Hashtable<>();
	static Hashtable<String, Integer> ints = new Hashtable<>();
	static Hashtable<String, Double> doubles = new Hashtable<>();
	static Hashtable<String, Vec> vecs = new Hashtable<>();
	static Hashtable<String, String> strings = new Hashtable<>();
	
	public static Set<String> getBooleanKeys(){
		return booleans.keySet();
	}
	
	public static boolean getBoolean(String setting) {
		Boolean out = booleans.get(setting);
		if(out == null) {
			throw new RuntimeException("Setting not found: " + setting + "!");
		}
		return out;
	}
	public static int getInt(String setting) {
		Integer out = ints.get(setting);
		if(out == null) {
			throw new RuntimeException("Setting not found: " + setting + "!");
		}
		return out;
	}
	public static double getDouble(String setting) {
		Double out = doubles.get(setting);
		if(out == null) {
			throw new RuntimeException("Setting not found: " + setting + "!");
		}
		return out;
	}
	public static Vec getVec(String setting) {
		Vec out = vecs.get(setting);
		if(out == null) {
			throw new RuntimeException("Setting not found: " + setting + "!");
		}
		return out;
	}
	public static String getString(String setting) {
		String out = strings.get(setting);
		if(out == null) {
			throw new RuntimeException("Setting not found: " + setting + "!");
		}
		return out;
	}
	
	public static void set(String setting, boolean value) {
		booleans.put(setting, value);
	}
	public static void set(String setting, int value) {
		ints.put(setting, value);
	}
	public static void set(String setting, double value) {
		doubles.put(setting, value);
	}
	public static void set(String setting, Vec value) {
		vecs.put(setting, value);
	}
	public static void set(String setting, String value) {
		strings.put(setting, value);
	}
	
	static {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(Main.SETTINGS_PATH));
			String line = reader.readLine();
			while (line != null) {

				String[] words = line.split("\\s+");
				
				if(words.length != 0 && words.length != 1 && !words[0].startsWith("//")) {
				
					int i = 0;
					String type = words[i++];
					String name = words[i++];
					
					switch(type) {
					
					case "boolean" :booleans.put(name, Boolean.parseBoolean(words[i++]));
									break;
									
					case "int" :	ints.put(name, Integer.parseInt(words[i++]));
									break;

					case "double" :	doubles.put(name, Double.parseDouble(words[i++]));
									break;
									
					case "Vec" :	vecs.put(name, new Vec(Double.parseDouble(words[i++]), Double.parseDouble(words[i++])));
									break;
									
					case "String" :	strings.put(name, words[i++]);
									break;
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

	public static int firstDebugKey;
	public static enum Key {
		MAIN_MENU(GLFW_KEY_ESCAPE, "toggle menu"),
		RIGHT(GLFW_KEY_D, "go right"),
		LEFT(GLFW_KEY_A, "go left"),
		JUMP(GLFW_KEY_SPACE, "jump!"),
		INVENTORY(GLFW_KEY_TAB, "open inventory"),
		SPRINT(GLFW_KEY_LEFT_SHIFT, "sprint"),
		CROUCH(GLFW_KEY_S, "crouch"),
		ZOOM_IN(GLFW_KEY_I, "zoom in"),
		ZOOM_OUT(GLFW_KEY_O, "zoom out"),
		THROW_ITEM(GLFW_KEY_Q, "throw item"),
		//debugging
		FASTER(GLFW_KEY_KP_ADD, "faster", true),
		SLOWER(GLFW_KEY_MINUS, "slower"),
		LAYERCOUNT_UP(GLFW_KEY_L, "draw more layers"),
		LAYERCOUNT_DOWN(GLFW_KEY_K, "draw less layers"),
		TOSS_COIN(GLFW_KEY_C, "toss a coin"),
		FREEZE(GLFW_KEY_V, "freeze Sarah"),
		JUMPDOWN(GLFW_KEY_J, "jump down"),
		MEGASPRINT(GLFW_KEY_RIGHT, "sprint very fast"),
		ANTIGRAVITY(GLFW_KEY_T, "antigravity"),
		SUPERGRAVITY(GLFW_KEY_G, "supergravity"),
		FLY_RIGHT(GLFW_KEY_H, "fly right"),
		FLY_LEFT(GLFW_KEY_F, "fly left"),
		DISMOUNT(GLFW_KEY_E, "dismount a cow"),
		SUPERSPRINT(GLFW_KEY_W, "sprint faster"),
		STOP_GRAPH(GLFW_KEY_R, "stop debug graph"),
		DEBUG(GLFW_KEY_F1, "open debug screen"),
		REFILL_BUFFERS(GLFW_KEY_F2, "refill buffers"),
		RELOAD_TERRAIN(GLFW_KEY_F3, "refill buffers"),
		NONE(0, "");
		
		public int key;
		String name;

		Key(int key, String name){
			this(key, name, false);
		}
		Key(int key, String name, boolean isFirstDebugKey){
			this.key = key;
			this.name = name;
			if(isFirstDebugKey) {
				firstDebugKey = ordinal();
			}
		}
		
		public String getName(){
			String out = glfwGetKeyName(key, 0);
			if(out == null || key == GLFW_KEY_MINUS) {
				out = keys.get(key);
			}
			return out;
		}
		
		public static Key getBinding(int key){
			for(Key bind : values()){
				if(bind.key == key){
					return bind;
				}
			}
			return NONE;
		}
		
		public String toString() {
			return name;
		}
	}

	public boolean keyPressed(int key) {
		switch(key){
		case GLFW_KEY_0: set("FRICTION", !getBoolean("FRICTION")); return true;
		case GLFW_KEY_1: set("AIR_FRICTION", !getBoolean("AIR_FRICTION")); return true;
		}
		Key bind = Key.getBinding(key);
		
		switch(bind){
		case STOP_GRAPH:
			if(Settings.getBoolean("DEBUGGING"))
				Settings.set("STOP_GRAPH",!Settings.getBoolean("STOP_GRAPH"));
			return true;
			default: return false;
		}
	}
	
	public static Hashtable<Integer, String> keys = new Hashtable<>();
	static {
		keys.put(GLFW_KEY_LEFT_SHIFT, "LSHIFT");
		keys.put(GLFW_KEY_SPACE, "SPACE");
		keys.put(GLFW_KEY_LEFT, "LEFT");
		keys.put(GLFW_KEY_RIGHT, "RIGHT");
		keys.put(GLFW_KEY_UP, "UP");
		keys.put(GLFW_KEY_DOWN, "DOWN");
		keys.put(GLFW_KEY_ESCAPE, "ESCAPE");
		keys.put(GLFW_KEY_ENTER, "ENTER");
		keys.put(GLFW_KEY_RIGHT_SHIFT, "RSHIFT");
		keys.put(GLFW_KEY_TAB, "TAB");
		keys.put(GLFW_KEY_MINUS, "-");
		keys.put(GLFW_KEY_F1, "F1");
		keys.put(GLFW_KEY_F2, "F2");
		keys.put(GLFW_KEY_F3, "F3");
		keys.put(GLFW_KEY_F4, "F4");
		keys.put(GLFW_KEY_F5, "F5");
		keys.put(GLFW_KEY_F6, "F6");
		keys.put(GLFW_KEY_F7, "F7");
		keys.put(GLFW_KEY_F8, "F8");
		keys.put(GLFW_KEY_F9, "F9");
		keys.put(GLFW_KEY_F10, "F10");
		keys.put(GLFW_KEY_F11, "F11");
		keys.put(GLFW_KEY_F12, "F12");
		
	}
	
}
