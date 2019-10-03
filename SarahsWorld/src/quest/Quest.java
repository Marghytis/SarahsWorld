package quest;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import basis.entities.Species;
import extra.items.ItemType;
import quest.script.Block;
import quest.script.QuestScript;
import quest.script.ScriptError.StructureError;
import quest.script.ScriptParser;
import world.World;
import world.data.WorldData;
import world.generation.Zone.Attribute;
import world.render.WorldPainter;

public class Quest extends QuestScript {
	
	private static List<Quest> tempValues = new ArrayList<>();

//	public static final Quest FIREFIGHTER = new Quest("res/quest/Firefighter.txt");
//	public static final Quest TEST = new Quest("res/quest/Test.txt");
	public static final QuestScript EVELYN = (QuestScript) ScriptParser.parseScriptFile("res/quest/Evelyn.txt");
	
	public static Quest[] values;
	static {
		values = tempValues.toArray(new Quest[tempValues.size()]);
	}
	
	public Quest(String name, Hashtable<String, Block> blocks) throws StructureError {
		super(name, blocks);
	}
	
//	private Quest(String filePath){
//		try {
//			FileReader fileReader = new FileReader(filePath);
//			BufferedReader reader = new BufferedReader(fileReader);
//
//			String completeFile = "", line = "";
//			
//			while((line = reader.readLine()) != null){
//				completeFile += line;
//			}
//			
//			completeFile = completeFile.replaceAll("\\s+","");
//			
//			compile(completeFile);
//			
//			fileReader.close();
//			reader.close();
//			
//			tempValues.add(this);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (UnknownMethodException e) {
//			e.printStackTrace();
//		}
//	}
	
	public void compile(String file) throws UnknownMethodException {
		String[] blocks = file.split("\\}");
		
		//attributes
		String[] attribs = blocks[1].split("\\{")[1].split(";");
		startAttributes = new int[attribs.length];
		for(int i = 0; i < attribs.length; i++){
			startAttributes[i] = Attribute.valueOf(attribs[i]).ordinal();
		}
		
		//characters
		String[] chars = blocks[2].split("\\{")[1].split(";");
		for(int i = 0; i < chars.length; i++){
			String[] data = chars[i].split("=");
			characters.put(data[0], data[1]);
		}
		
		//events
		Hashtable<String, Event> events = new Hashtable<>();
		
		for(int i = 3; i < blocks.length; i++){
			String[] data = blocks[i].split("\\{");
			events.put(data[0], compileEvent(data[0], data[1]));
		}
		
		//link events and set start event
		linkEvents2(events);
		start = events.get(QuestScript.START_blockName);
	}
	
	public static void linkEvents2(Hashtable<String, Event> events) {
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
	
	public static void linkEvents(Event[] events) {
		for(int i = 0; i < events.length; i++){
			if(events[i].nextTemp != null) {
				events[i].next = new Event[events[i].nextTemp.length];
				events[i].answerCondition = new int[events[i].nextTemp.length];
				for(int i2 = 0; i2 < events[i].nextTemp.length; i2++){
					events[i].next[i2] = events[events[i].nextTemp[i2]%1000];
					events[i].answerCondition[i2] = events[i].nextTemp[i2]/1000;
				}
			} else {
				events[i].next = new Event[0];
				events[i].answerCondition = new int[0];
			}
		}
	}
	
	public static Event compileEvent(String name, String content) throws UnknownMethodException {
		Event out = new Event(name);
		String[] paragraphs = content.split("~");
		for(int i2 = 0; i2 < paragraphs.length; i2++)
		switch(paragraphs[i2].charAt(0)){
		case 'I'://IF
			out.condition = compileCondition(paragraphs[i2].substring(2));
			break;
		case 'D'://DO
			out.action = compileAction(paragraphs[i2].substring(2));
			break;
		case 'N'://NEXT
//			out.nextTemp = compileNext(names, paragraphs[i2].substring(4));
			out.nextTemp2 = paragraphs[i2].substring(4).split(";");
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
	
	public static Condition compileCondition(String condition) throws UnknownMethodException {
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
			default: throw(new UnknownMethodException("condition", method[0]));
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
	
	public static Condition getCondition(Objector left, String operator, String rightSide){
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
		default: throw new RuntimeException("operator not known.");
		}
	}
	
	public interface Objector {
		public Object object(ActiveQuest quest, WorldData world);
	}
	
	public static Action compileAction(String action) throws UnknownMethodException {
		String[] actions = action.split(";");
		Action[] realActions = new Action[actions.length];
		for(int i = 0; i < actions.length; i++){
			String[] method = actions[i].substring(0, actions[i].length()-1).split("\\(");
			String[] args = method[1].split(",");
			switch(method[0]){
			case "bindAvatar": realActions[i] = (q,w)->{q.characters.put(args[0], World.world.avatar);}; break;
			case "spawn": realActions[i] = (q, w) -> {w.requestSpawn(new QuestSpawner(Species.valueOf(q.getQuest().characters.get(args[0])), q, args[0], args.length > 1 ? args[1] : -1)); q.eventFinished = false;}; break;
			case "say": realActions[i] = (q, w) -> {q.characters.get(args[1]).speakPlug.say(Boolean.parseBoolean(args[0]), q, args[2], args.length == 4 ? args[3].split("\\|") : new String[0]);};break;
			//say(boolean thoughtBubble, villager, question, answers)break;
			case "give": realActions[i] = (q, w) -> q.characters.get(args[0]).invPlug.addItem( ItemType.valueOf(args[1]), Integer.parseInt(args[2])); break;
			case "print": realActions[i] = (q, w) -> System.out.println(args[0]); break;
			default: throw(new UnknownMethodException("action", method[0]));
			}
		}
		return (q, w) -> {
			q.lastEventTime = System.currentTimeMillis()/1000.0;
			for(int i = 0; i < realActions.length; i++){
				realActions[i].run(q, w);
			}
		};
	}
	
	public static int[] compileNext(String[] names, String next){
		String[] nexts = next.split(";");
		int[] out = new int[nexts.length];
		for(int i = 0; i < nexts.length; i++){
			String[] data = nexts[i].split(":");
			String name = data[0];
			int thousands = 0;
			if(data.length > 1){
				name = data[1];
				thousands = Integer.parseInt(data[0])*1000;
			}
			for(int i2 = 0; i2 < names.length; i2++){
				if(name.equals(names[i2])){
					out[i] = thousands + i2;
					break;
				}
			}
		}
		return out;
	}
	
	public static class UnknownMethodException extends Exception {
		private static final long serialVersionUID = 1L; 
		public UnknownMethodException(String type, String event){
			super("Script file has an unknown " + event + " function in " + type);
		}
	}
}
