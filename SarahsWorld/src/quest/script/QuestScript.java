package quest.script;

import java.util.Hashtable;
import java.util.Set;

import quest.Event;
import quest.script.Block.CommandBlock;
import quest.script.Block.ListBlock;
import quest.script.Block.SettingsBlock;
import quest.script.ScriptError.StructureError;
import world.generation.ZoneAttribute;

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
				startAttributes[i] = ZoneAttribute.valueOf(attributesBlock.elements[i]).ordinal();
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
		
		linkEvents2(events);
	}
	
	public void linkEvents2(Hashtable<String, Event> events) {
		Set<String> names = events.keySet();
		for(String name : names) {
			Event event = events.get(name);
			if(event.nextTemp2 != null) {
				event.next = new Event[event.nextTemp2.length];
				event.answerCondition = new int[event.nextTemp2.length];
				for(int i2 = 0; i2 < event.nextTemp2.length; i2++) {
					String[] nextStrings = event.nextTemp2[i2].split(":");
					if(nextStrings.length == 1) {
						event.next[i2] = events.get(nextStrings[0]);
					} else if(nextStrings.length == 2) {
						event.next[i2] = events.get(nextStrings[1]);
						event.answerCondition[i2] = Integer.parseInt(nextStrings[0]);
					} else {
						throw new RuntimeException("What?! Too many colons..");
					}
				}
			} else {
				event.next = new Event[0];
				event.answerCondition = new int[0];
			}
		}
	}
	
	public Event getStart() {
		return start;
	}
	
	public int[] getStartAttributes() {
		return startAttributes;
	}

	public Hashtable<String, String> characters() {
		return characters;
	}
}
