package quest.script;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import basis.entities.Entity;
import basis.entities.Species;
import extra.items.ItemType;
import extra.things.Thing;
import extra.things.traits.Sticky.StickPlugin;
import quest.Action;
import quest.ActiveQuest;
import quest.Condition;
import quest.Event;
import quest.QuestSpawner;
import quest.script.Block.CommandBlock;
import quest.script.Block.ListBlock;
import quest.script.Block.SettingsBlock;
import quest.script.Block.SettingsBlock.Value;
import quest.script.ScriptError.NameError;
import quest.script.ScriptError.StructureError;
import quest.script.ScriptError.SyntaxError;
import world.World;
import world.data.WorldData;
import world.render.WorldPainter;

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
				if(!line.matches("\\s+//.*$"))//would be a comment
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
		return new CommandBlock(parseEvent(name, content));
	}
	
	public static Event parseEvent(String name, String content) throws ScriptError {
		Event out = new Event(name);
		String[] paragraphs = content.split("~");
		for(int i2 = 0; i2 < paragraphs.length; i2++)
		switch(paragraphs[i2].charAt(0)){
		case 'I'://IF
			out.condition = parseCondition(paragraphs[i2].substring(2));
			break;
		case 'D'://DO
			out.action = parseAction(paragraphs[i2].substring(2));
			break;
		case 'N'://NEXT
			out.nextTemp2 = paragraphs[i2].substring(4).split(";");
			if(out.nextTemp2 == null)
				throw new RuntimeException("nextTemp2 == null");
			break;
		case 'E'://EMPTY
			break;
		}
		if(out.condition == null) out.condition = (q, w) -> true;
		if(out.action == null) out.action = (q, w) -> {};
//		if(out.nextTemp == null) out.nextTemp = new int[0];
		if(out.nextTemp2 == null) out.nextTemp2 = new String[0];
		
		return out;
	}
	
	public static Condition parseCondition(String condition) throws ScriptError {
		String[] conditions = condition.split("&&|\\|\\|");
		boolean[] operators = new boolean[conditions.length];
		for(int i = 1, i1 = conditions[0].length(); i < conditions.length; i1 += 2 + conditions[i].length(), i++){
			operators[i] = condition.substring(i1, i1 + 2) == "&&";
		}
		Condition[] list = new Condition[conditions.length];
		for(int i = 0; i < conditions.length; i++){
			String[] data = conditions[i].split("\\)");
			String[] method = data[0].split("\\(");
			String[] args = method.length > 1 ? method[1].split(",") : null;
			Objector leftSide = null;
			switch(method[0]){
			case "true": leftSide = (q, w) -> 1; break;
			case "daytime": leftSide = (q, w) -> WorldPainter.getDayTime();break;
			case "random": leftSide = (q, w) -> World.rand.nextInt(Integer.parseInt(args[0]));break;
			case "distance": leftSide = (q, w) -> {
				return q.characters.get(args[0]).pos.minus(q.characters.get(args[1]).pos).length();
			}; break;
			case "timeDelta": leftSide = (q, w) -> System.currentTimeMillis()/1000.0 - q.lastEventTime;break;
			case "has": leftSide = (q, w) -> {
				boolean has = q.characters.get(args[0]).invPlug.containsItems(ItemType.valueOf(args[1]), Integer.parseInt(args[2]));
				return has ? "true" : "false";
			};break;
			default: throw new NameError("Script file has an unknown " + method[0] + " function in " + "condition.");
			}
			list[i] = getCondition(leftSide, data[1].substring(0, 2), data[1].substring(2));
		}
		if(conditions.length > 0){
			return (q, w) -> {//connect all the conditions with the operators to one single condition
				boolean out = list[0].isMet(q, w);
				for(int i = 1; i < list.length; i++){
					boolean op = operators[i];
					boolean cur = list[i].isMet(q, w);
					if(out && !op){
						return true;
					} else if(!out && op){
						out = false;
					} else if(cur){
						if(out) out = cur && op;
						else out = cur && !op;
					}
				}
				return out;
			};
		} else {
			return null;
		}
	}
	
	public static Condition getCondition(Objector left, String operator, String rightSide) throws SyntaxError {
		switch(operator){
		case "==": return (q, w) -> {
			if(left instanceof Number){
				System.out.println(((Number)left.object(q, w)).intValue());
			}
			return left.object(q, w).equals(rightSide) || (left instanceof Number && ((Number)left.object(q, w)).intValue() <= Integer.parseInt(rightSide));};
		case "<=": return (q, w) -> ((Number)left.object(q, w)).intValue() <= Integer.parseInt(rightSide);
		case ">=": return (q, w) -> ((Number)left.object(q, w)).intValue() >= Integer.parseInt(rightSide);
		case "<<": return (q, w) -> ((Number)left.object(q, w)).intValue() < Integer.parseInt(rightSide);
		case ">>": return (q, w) -> ((Number)left.object(q, w)).intValue() > Integer.parseInt(rightSide);
		default: throw new SyntaxError("operator " + operator + " not known.");
		}
	}
	
	public interface Objector {
		public Object object(ActiveQuest quest, WorldData world);
	}
	
	public static Action parseAction(String action) throws ScriptError {
		String[] actions = action.split(";");
		Action[] realActions = new Action[actions.length];
		for(int i = 0; i < actions.length; i++){
			String[] method = actions[i].substring(0, actions[i].length()-1).split("\\(");
			String[] args = method[1].split(",");
			switch(method[0]){
			case "bindAvatar": realActions[i] = (q,w)->{
				q.characters.put(args[0], World.world.avatar);
			}; break;
			case "spawn":
				realActions[i] = (q, w) -> {
					
					w.requestSpawn(new QuestSpawner(Species.valueOf(q.getQuest().characters().get(args[0])), q, args[0], args.length > 1 ? args[1] : -1));
				q.eventFinished = false;
			}; break;
			case "instantSpawn":
				String name = args[0];
				String whereName = args[1];

				String[] extraData = new String[args.length-2];
				System.arraycopy(args, 2, extraData, 0, extraData.length);
				Object[] extraValues = new Value[extraData.length];
				for(int i2 = 0; i2 < extraData.length; i2++) {
					extraValues[i2] = new Value(extraData[i2]);
				}
				
				realActions[i] = (q, w) -> {
					Entity where = q.characters.get(whereName);
					Thing t = Species.valueOf(q.getQuest().characters().get(name)).defaultSpawner.spawn(where.newLink, where.pos.copy(), extraValues);
					q.characters.put(name, t);
				};
				break;
			case "stick":
				realActions[i] = (q, w) -> {
					Thing toStick = q.characters.get(args[0]);
					Thing toStickOn = q.characters.get(args[1]);
					((StickPlugin)toStick.sticky).stickTo(toStickOn);
				}; break;
			case "say":
				realActions[i] = (q, w) -> {
					Thing charac = q.characters.get(args[1]);
					if(charac == null)
						throw new RuntimeException("Character name " + args[1] + " not recognized."); 
					charac.speakPlug.say(Boolean.parseBoolean(args[0]), q, args[2], args.length == 4 ? args[3].split("\\|") : new String[0]);
				};break;
			//say(boolean thoughtBubble, villager, question, answers)break;
			case "give": realActions[i] = (q, w) -> {
				Thing charac = q.characters.get(args[0]);
				if(charac == null)
					throw new RuntimeException("Character name " + args[1] + " not recognized."); 
				charac.invPlug.addItem( ItemType.valueOf(args[1]), Integer.parseInt(args[2]));
			}; break;
			case "print": realActions[i] = (q, w) -> System.out.println(args[0]); break;
			default: throw new NameError("Script file has an unknown " + method[0] + " function in action.");
			}
		}
		return (q, w) -> {
			q.lastEventTime = System.currentTimeMillis()/1000.0;
			for(int i = 0; i < realActions.length; i++){
				realActions[i].run(q, w);
			}
		};
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
		if(content == "")
			return new ListBlock(name, new String[0]);
		else
			return new ListBlock(name, content.split(";"));
	}
}
