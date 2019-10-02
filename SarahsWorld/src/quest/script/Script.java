package quest.script;

import quest.script.ScriptParser.ScriptType;

public class Script {
	
	private String name;
	private ScriptType type;
	
	public Script(ScriptType type, String name) {
		this.type = type;
		this.name = name;
	}

	public ScriptType getType() {
		return type;
	}
	
	public String getName() {
		return name;	}
}
