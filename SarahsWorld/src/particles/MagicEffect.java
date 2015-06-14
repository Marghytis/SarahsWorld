package particles;

import util.math.Vec;
import world.things.Thing;

public class MagicEffect implements ParticleEffect{

	Vec pos;
	Vec dir;
	int live = 3000;
	Thing source;
	
	public MagicEffect(Vec pos, Vec dir, Thing source){
		this.pos = pos.copy();
		this.dir = dir.copy();
		this.source = source;
	}
	
	@Override
	public void tick(float delta) {
		
	}

	@Override
	public void render() {
		
	}

	@Override
	public boolean living() {
		return false;
	}
	
	@Override
	public void finalize(){
		
	}

}
