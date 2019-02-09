package things.aiPlugins;

import things.AiPlugin2;
import things.Thing;
import things.ThingPlugin;



public class Magic extends AiPlugin2 {
	
	public int startMana;
	public int maxMana;

	public Magic(int maxMana, int startMana) {
		this.startMana = startMana;
		this.maxMana = maxMana;
	}

	@Override
	public ThingPlugin plugIntoThing(Thing t) {
		MagicPlugin magic = new MagicPlugin(t);
		t.setMagicPlugin(magic);
		return magic;
	}
	
	public class MagicPlugin extends ThingPlugin {
		
		public int mana;

		public MagicPlugin(Thing thing) {
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
