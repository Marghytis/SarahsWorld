package quest.script;

import java.util.Hashtable;
import java.util.Set;

import quest.Event;
import quest.Quest;
import quest.script.Block.CommandBlock;
import quest.script.Block.ListBlock;
import quest.script.Block.SettingsBlock;
import quest.script.ScriptError.StructureError;
import world.generation.Zone.Attribute;

public class QuestScript extends Script {
	
	public static final String CHARACTERS_blockName = "CHARACTERS", START_blockName = "START", ATTRIBUTES_blockName = "ATTRIBUTES";

	protected int[] startAttributes;
	protected Hashtable<String, String> characters;
	protected Event start;
	Hashtable<String, Event> events;
	
	public QuestScript(String name, Hashtable<String, Block> blocks) throws StructureError {
		super(name);
		
		//START block
		if(!blocks.containsKey(START_blockName))
			throw new StructureError("A quest script needs a start EventBlock.");
		
		this.start = ((CommandBlock) blocks.get(START_blockName)).event;

		//CHARACTERS block
		this.characters = new Hashtable<>();
		if(blocks.containsKey(CHARACTERS_blockName)){
			SettingsBlock charBlock = (SettingsBlock) blocks.get(CHARACTERS_blockName);
			for(String charName : charBlock.settings.keySet()) {
				characters.put(charName, charBlock.settings.get(charName).asString());
			}
		}
		
		//ATTRIBUTES block
		if(blocks.containsKey(ATTRIBUTES_blockName)) {
			ListBlock attributesBlock = (ListBlock) blocks.get(ATTRIBUTES_blockName);
			this.startAttributes = new int[attributesBlock.elements.length];
			
			for(int i = 0; i < attributesBlock.elements.length; i++) {
				System.out.println(attributesBlock.elements[i]);
				startAttributes[i] = Attribute.valueOf(attributesBlock.elements[i]).ordinal();
			}
		}
		
		//COMMAND blocks
		events = new Hashtable<>();
		Set<String> blockNames = blocks.keySet();
		for(String blockName : blockNames) {
			Block block = blocks.get(blockName); 
			if(block instanceof CommandBlock) {
				events.put(blockName, ((CommandBlock) block).event);
			}
		}
		
		Quest.linkEvents2(events);
	}
	
	public Event getStart() {
		return start;
	}
	
	public int[] getStartAttributes() {
		return startAttributes;
	}
}
