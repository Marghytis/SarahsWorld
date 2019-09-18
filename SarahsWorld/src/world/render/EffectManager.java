package world.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import basis.effects.Effect;

public class EffectManager {
	
	private List<Effect> effects = new ArrayList<>();
	private List<Effect> toAdd = new ArrayList<>();
	private List<Effect> toRemove = new ArrayList<>();

	public boolean update(double delta) {
		effects.addAll(toAdd);
		toAdd.clear();
		effects.removeAll(toRemove);
		toRemove.clear();

		for(int i = 0; i < effects.size(); i++){
			effects.get(i).update(delta);
			if(!effects.get(i).living()){
				effects.remove(i);
				i--;
			}
		}
		return true;
	}
	
	public void forEach(Consumer<Effect> cons) {
		effects.forEach(cons);
	}

	public List<Effect> getEffects(){
		return effects;
	}
	
	public void addEffect(Effect effect){
		toAdd.add(effect);
	}
	
	public void removeEffect(Effect effect) {
		toRemove.add(effect);
	}
	
}
