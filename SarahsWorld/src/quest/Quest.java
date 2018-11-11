package quest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import item.ItemStack;
import item.ItemType;
import things.ThingType;
import world.World;
import world.data.WorldData;
import world.generation.Zone.Attribute;
import world.render.WorldPainter;

public enum Quest {
//	FIREFIGHTER("res/quest/Firefighter.txt"),
//	TEST("res/quest/Test.txt"),
	EVELYN("res/quest/Evelyn.txt")
	;

	public static Quest[] values;
	
	static {
		values = values();
	}
	public Hashtable<String, ThingType> characters = new Hashtable<>();
	public int[] startAttributes;
	public Event start;
	
	Quest(String filePath){
		try {
			FileReader fileReader = new FileReader(filePath);
			BufferedReader reader = new BufferedReader(fileReader);

			String completeFile = "", line = "";
			
			while((line = reader.readLine()) != null){
				completeFile += line;
			}
			
			completeFile = completeFile.replaceAll("\\s+","");
			
			compile(completeFile);
			
			fileReader.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnknownMethodException e) {
			e.printStackTrace();
		}
	}
	
	public void compile(String file) throws UnknownMethodException {
		String[] blocks = file.split("\\}");
		
		//attributes
		String[] attribs = blocks[0].split("\\{")[1].split(";");
		startAttributes = new int[attribs.length];
		for(int i = 0; i < attribs.length; i++){
			startAttributes[i] = Attribute.valueOf(attribs[i]).ordinal();
		}
		
		//characters
		String[] chars = blocks[1].split("\\{")[1].split(";");
		for(int i = 0; i < chars.length; i++){
			String[] data = chars[i].split("=");
			characters.put(data[0], ThingType.valueOf(data[1]));
		}
		
		//names
		String[] names = new String[blocks.length-2];//first two are not used
		String[] blocks2 = new String[blocks.length-2];
		for(int i = 2, i2 = 0; i < blocks.length; i++, i2++){
			String[] data = blocks[i].split("\\{");
			names[i2] = data[0];
			if(data.length > 1)
				blocks2[i2] = data[1];
			else
				blocks2[i2] = "";
		}
		
		//actual quest
		Event[] events = new Event[blocks2.length];
		for(int i = 0; i < blocks2.length; i++){
			events[i] = new Event(i, names[i]);
			String[] paragraphs = blocks2[i].split("~");
			for(int i2 = 0; i2 < paragraphs.length; i2++)
			switch(paragraphs[i2].charAt(0)){
			case 'I'://IF
				events[i].condition = compileCondition(paragraphs[i2].substring(2));
				break;
			case 'D'://DO
				events[i].action = compileAction(paragraphs[i2].substring(2));
				break;
			case 'N'://NEXT
				events[i].nextTemp = compileNext(names, paragraphs[i2].substring(4));
				break;
			case 'E'://EMPTY
				break;
			}
			if(events[i].condition == null) events[i].condition = (q, w) -> true;
			if(events[i].action == null) events[i].action = (q, w) -> {};
			if(events[i].nextTemp == null) events[i].nextTemp = new int[0];
		}
		for(int i = 0; i < events.length; i++){
			events[i].next = new Event[events[i].nextTemp.length];
			events[i].answerCondition = new int[events[i].nextTemp.length];
			for(int i2 = 0; i2 < events[i].nextTemp.length; i2++){
				events[i].next[i2] = events[events[i].nextTemp[i2]%1000];
				events[i].answerCondition[i2] = events[i].nextTemp[i2]/1000;
			}
		}
		start = events[0];
	}
	
	public Condition compileCondition(String condition) throws UnknownMethodException {
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
				ItemStack[] stacks = q.characters.get(args[0]).itemStacks;
				for(int i1 = 0; i1 < stacks.length; i1++){
					if(stacks[i1].item == ItemType.valueOf(args[1])){
						return (stacks[i1].count >= Integer.parseInt(args[2])) ? "true" : "false";
					}
				}
				return "false";
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
	
	public Condition getCondition(Objector left, String operator, String rightSide){
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
		default: return null;
		}
	}
	
	public interface Objector {
		public Object object(ActiveQuest quest, WorldData world);
	}
	
	public Action compileAction(String action) throws UnknownMethodException {
		String[] actions = action.split(";");
		Action[] realActions = new Action[actions.length];
		for(int i = 0; i < actions.length; i++){
			String[] method = actions[i].substring(0, actions[i].length()-1).split("\\(");
			String[] args = method[1].split(",");
			switch(method[0]){
			case "bindAvatar": realActions[i] = (q,w)->{q.characters.put(args[0], World.world.avatar);}; break;
			case "spawn": realActions[i] = (q, w) -> {w.requestSpawn(new QuestSpawner(characters.get(args[0]), q, args[0], args.length > 1 ? args[1] : -1)); q.eventFinished = false;}; break;
			case "say": realActions[i] = (q, w) -> {q.characters.get(args[1]).type.speak.say(q.characters.get(args[1]), Boolean.parseBoolean(args[0]), q, args[2], args.length == 4 ? args[3].split("\\|") : new String[0]);};break;
			//say(boolean thoughtBubble, villager, question, answers)break;
			case "give": realActions[i] = (q, w) -> q.characters.get(args[0]).type.inv.addItem(q.characters.get(args[0]), ItemType.valueOf(args[1]), Integer.parseInt(args[2])); break;
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
	
	public int[] compileNext(String[] names, String next){
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
	
	public class UnknownMethodException extends Exception {
		private static final long serialVersionUID = 1L; 
		public UnknownMethodException(String type, String event){
			super("Script file has an unknown " + event + " function in " + type);
		}
	}
}
