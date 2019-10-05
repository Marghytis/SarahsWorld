package quest.script;

import java.util.Hashtable;

import menu.Settings;
import quest.Event;
import quest.script.ScriptError.SyntaxError;
import util.math.Vec;

public class Block {
	
	public static class CommandBlock extends Block {
		public Event event;
		
		public CommandBlock(Event event) {
			this.event = event;
		}
	}
	
	public static class ListBlock extends Block {
		public String[] elements;
		public String name;
		
		public ListBlock(String name, String... elements) {
			this.name = name;
			this.elements = elements;
		}
	}
	
	public static class SettingsBlock extends Block {
		public Hashtable<String, Value> settings;
		
		public SettingsBlock(Hashtable<String, Value> settings) {
			this.settings = settings;
		}
		
		public void applySettings() {
			settings.forEach((key, value) -> {
				value.type.apply(key, value.values);
			});
		}
		
		public static class Value {
			public ValueType type;
			public String[] values;
			
			public Value(String value) throws SyntaxError {
				if(value.contains(":")) {//means it's a tuple
					value = value.replaceAll("\\(", "");
					value = value.replaceAll("\\)", "");
					String[] parts = value.split(":");
					this.type = ValueType.valueOf(parts[0]);
					
					if(parts.length - 1 != type.nValues)
						throw new SyntaxError("Number of values in tuple doesn't fit to value type.");
					
					this.values = new String[parts.length - 1];
					System.arraycopy(parts, 1, values, 0, values.length);
				} else {
					type = ValueType.STRING;
					values = new String[] {value};
				}
				
			}

			public String asString() {
				if(values.length == 1)
					return values[0];
				else
					throw new RuntimeException("Value can't be a String.");
			}

			public Vec asVec() {
				if(values.length == 2)
					return new Vec(Double.parseDouble(values[0]), Double.parseDouble(values[1]));
				else
					throw new RuntimeException("Value can't be a Vec.");
			}

			public double asDouble() {
				if(values.length == 1)
					return Double.parseDouble(values[0]);
				else
					throw new RuntimeException("Value can't be a Double.");
			}
		}
		
		public static enum ValueType {
			VEC(2){

				@Override
				public void apply(String name, String[] value) {
					Settings.set(name, new Vec(Double.parseDouble(value[0]), Double.parseDouble(value[1])));
				}
				
			}, STRING(1){

				@Override
				public void apply(String name, String[] value) {
					Settings.set(name, value[0]);
				}
				
			}, ENUM(1){

				@Override
				public void apply(String name, String[] value) {
					Settings.set(name, value[0]);
				}
				
			}, INT(1){

				@Override
				public void apply(String name, String[] value) {
					Settings.set(name, Integer.parseInt(value[0]));
				}
				
			}, DOUBLE(1){

				@Override
				public void apply(String name, String[] value) {
					Settings.set(name, Double.parseDouble(value[0]));
				}
				
			};
			
			public int nValues;
			
			ValueType(int nValues){
				this.nValues = nValues;
			}
			
			/**
			 * 
			 * @param value still has the type as its first component
			 */
			public abstract void apply(String name, String[] value);
		}
	}

	public static enum BlockType {
		MAIN, PLOT, CHARACTERS, ATTRIBUTES
	}
	
}
