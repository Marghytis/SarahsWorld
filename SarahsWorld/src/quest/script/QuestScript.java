package quest.script;

import java.util.Hashtable;

import basis.entities.Species;
import quest.script.Block.CommandBlock;
import quest.script.Block.ListBlock;
import quest.script.Block.SettingsBlock;
import quest.script.ScriptError.StructureError;
import quest.script.ScriptParser.ScriptType;
import world.generation.Zone.Attribute;

public class QuestScript extends Script {
	
	public static final String CHARACTERS_blockName = "CHARACTERS", START_blockName = "START", ATTRIBUTES_blockName = "ATTRIBUTES";

	Attribute[] attributes;
	Hashtable<String, Species> characters;
	CommandBlock start;
	Hashtable<String, CommandBlock> events;
	
	public QuestScript(String name, Hashtable<String, Block> blocks) throws StructureError {
		super(ScriptType.QUEST, name);
		
		//START block
		if(!blocks.containsKey(START_blockName))
			throw new StructureError("A quest script needs a start EventBlock.");
		
		this.start = (CommandBlock) blocks.get(START_blockName);

		//CHARACTERS block
		this.characters = new Hashtable<>();
		if(blocks.containsKey(CHARACTERS_blockName)){
			SettingsBlock charBlock = (SettingsBlock) blocks.get(CHARACTERS_blockName);
			for(String charName : charBlock.settings.keySet()) {
				characters.put(charName, charBlock.settings.get(charName).asSpecies());
			}
		}
		
		//ATTRIBUTES block
		if(blocks.containsKey(ATTRIBUTES_blockName)) {
			ListBlock attributesBlock = (ListBlock) blocks.get(ATTRIBUTES_blockName);
			this.attributes = new Attribute[attributesBlock.elements.length];
			
			for(int i = 0; i < attributesBlock.elements.length; i++) {
				attributes[i] = Attribute.valueOf(attributesBlock.elements[i]);
			}
		}
		
		//COMMAND blocks
		
	}
}
