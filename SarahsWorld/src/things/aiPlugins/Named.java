package things.aiPlugins;

import things.AiPlugin2;
import things.Entity;
import things.ThingPlugin;



public class Named extends AiPlugin2 {
	
	public String defaultName;

	public Named() {
		this("");
	}
	public Named(String defaultName) {
		this.defaultName = defaultName;
	}

	@Override
	public NamePlugin createAttribute(Entity thing) {
		return new NamePlugin(thing);
	}
	
	public class NamePlugin extends ThingPlugin {
		
		private String name;

		public NamePlugin(Entity thing) {
			super(thing);
			
			this.name = defaultName;
		}
		
		public void setName(String newName) {
			this.name = newName;
		}
		
		public String get() {
			return name;
		}
		
	}
}
