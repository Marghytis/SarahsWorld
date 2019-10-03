package quest.script;

import java.util.Hashtable;

import quest.script.Block.SettingsBlock;
import quest.script.ScriptError.StructureError;
import quest.script.ScriptParser.ScriptType;

public class GameSetupScript extends QuestScript {
	
	public static final String GAME_SETUP_blockName = "GAME_SETUP";
	
	SettingsBlock preInitBlock;
	
	public GameSetupScript(Hashtable<String, Block> blocks) throws StructureError {
		super(ScriptType.GAME_SETUP.name(), blocks);
		
		if(!blocks.containsKey(GAME_SETUP_blockName))
			throw new StructureError("A GAME_SETUP script needs a block called '" + GAME_SETUP_blockName + "'.");
		
		this.preInitBlock = (SettingsBlock) blocks.get(GAME_SETUP_blockName);
	}

	public void changeSettings() {
		preInitBlock.applySettings();
	}

	public void startGame() {
		
	}
	
}
