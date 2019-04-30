package extra.things.traits;

import basis.entities.Trait;
import extra.things.ThingAttribute;
import basis.entities.Entity;



public class Named extends Trait {
	
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
	
	public class NamePlugin extends ThingAttribute {
		
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
