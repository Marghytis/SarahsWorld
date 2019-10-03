package quest.script;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import quest.Quest;
import quest.Quest.UnknownMethodException;
import quest.script.Block.CommandBlock;
import quest.script.Block.ListBlock;
import quest.script.Block.SettingsBlock;
import quest.script.Block.SettingsBlock.Value;
import quest.script.ScriptError.NameError;
import quest.script.ScriptError.StructureError;
import quest.script.ScriptError.SyntaxError;

public class ScriptParser {
	
	//script type names
	private static final String GAME_SETUP_scriptTypeName = "GAME_SETUP", QUEST_scriptTypeName = "QUEST";
	
	//special block names
	private static final String MAIN_blockName = "MAIN";
	
	//main settings names
	private static final String SCRIPT_NAME_settingName = "scriptName", SCRIPT_TYPE_settingName = "scriptType";

	public static Script parseScriptFile(String filePath) {

		try {
			FileReader fileReader = new FileReader(filePath);
			BufferedReader reader = new BufferedReader(fileReader);
	
			String completeFile = "", line = "";
			
			while((line = reader.readLine()) != null){
				if(!line.startsWith("\\s+//"))//would be a comment
					completeFile += line;
			}
			
			Script out = parseScriptText(completeFile);
			
			fileReader.close();
			reader.close();
			
			return out;
		} catch (IOException | ScriptError e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Splits up the script in blocks, parses each block and then combines them.
	 * @param script
	 * @return
	 * @throws ScriptError
	 */
	private static Script parseScriptText(String script) throws ScriptError {
		
		//remove all whitespaces
		script = script.replaceAll("\\s+","");

		//split script in blocks
		String[] blocks = script.split("\\}");
		
		//parse each block
		Hashtable<String, Block> blocksHashed = new Hashtable<>();
		
		for(int i = 0; i < blocks.length; i++) {
			String[] blockStrings = blocks[i].split("\\{");
			
			Block block = parseBlock(blockStrings[0], blockStrings.length > 1 ? blockStrings[1] : "");
			
			if(block != null)
				blocksHashed.put(blockStrings[0], block);
		}
		
		//combine blocks to a complete script and return it
		return parseScript(blocksHashed);
	}
	
	private static Block parseBlock(String name, String content) throws ScriptError {
		//special blocks have a special name, the others are event blocks
		switch(name) {
		case MAIN_blockName: return parseKeyValueBlock(name, content);
		case GameSetupScript.GAME_SETUP_blockName: return parseKeyValueBlock(name, content);
		case QuestScript.CHARACTERS_blockName: return parseKeyValueBlock(name, content);
		case QuestScript.ATTRIBUTES_blockName: return parseListBlock(name, content);
		default: return parseEventBlock(name, content);
		}
	}
	
	private static Script parseScript(Hashtable<String, Block> blocks) throws StructureError {
		
		//no Main block?
		if(!blocks.containsKey(MAIN_blockName))
			throw new StructureError("There needs to be at least one block called '" + MAIN_blockName + "' in a script.");
		
		SettingsBlock mainBlock = ((SettingsBlock)blocks.get(MAIN_blockName)); 
		String name = mainBlock.settings.get(SCRIPT_NAME_settingName).asString();
		String type = mainBlock.settings.get(SCRIPT_TYPE_settingName).asString();
		
		switch(type) {
		case GAME_SETUP_scriptTypeName: return new GameSetupScript(blocks);
		case QUEST_scriptTypeName: return new QuestScript(name, blocks);
		default: return null;
		}
	}
	
	//script types
	public static enum ScriptType {
		GAME_SETUP, QUEST;
	}
	
	private static Block parseEventBlock(String name, String content) throws ScriptError {
		try {
			return new CommandBlock(Quest.compileEvent(name, content));
		} catch (UnknownMethodException e) {
			throw new NameError("Function not recognized: " + e.getMessage());
		}
	}
	
	private static Block parseKeyValueBlock(String name, String content) throws ScriptError {
		String[] settingsStrings = content.split(";");
		
		Hashtable<String, Value> settings = new Hashtable<>();
		
		for(int i = 0; i < settingsStrings.length; i++) {
			String[] keyValue = settingsStrings[i].split("=");
			if(keyValue.length != 2)
				throw new SyntaxError(name +": In a settings block you must only put ';'-separated '='-statements.");
			
			settings.put( keyValue[0], new Value(keyValue[1]));
		}
		
		return new SettingsBlock(settings);
	}
	
	private static Block parseListBlock(String name, String content) throws ScriptError {
		return new ListBlock(name, content.split(";"));
	}
}
