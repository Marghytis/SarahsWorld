package extra.things.traits;

import basis.entities.Trait;
import extra.things.ThingAttribute;
import basis.entities.Entity;



public class Magic extends Trait {
	
	public int startMana;
	public int maxMana;

	public Magic(int maxMana, int startMana) {
		this.startMana = startMana;
		this.maxMana = maxMana;
	}

	@Override
	public MagicPlugin createAttribute(Entity thing) {
		return new MagicPlugin(thing);
	}
	
	public class MagicPlugin extends ThingAttribute {
		
		public int mana;

		public MagicPlugin(Entity thing) {
			super(thing);
			
			this.mana = startMana;
		}
		
		public boolean drainMana(int manaUse) {
			if(mana >= manaUse) {
				mana -= manaUse;
				return true;
			} else {
				return false;
			}
		}
		
	}
}
