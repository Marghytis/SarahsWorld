package things.aiPlugins;

import things.AiPlugin2;
import things.Entity;
import things.ThingPlugin;



public class Magic extends AiPlugin2 {
	
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
	
	public class MagicPlugin extends ThingPlugin {
		
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
