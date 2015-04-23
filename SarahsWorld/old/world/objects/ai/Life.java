package world.objects.ai;

import world.objects.Thing;


public class Life extends AiPlugin {
	
	static double coolDownStart = 0.5;
	
	public int coins; //:P
	public int health;
	public int armor;
	double cooldown;
	
	public Life(Thing t, int health, int coins){
		super(t);
		this.health = health;
		this.coins = coins;
	}

	public boolean action(double delta) {
		if(cooldown > 0) cooldown -= delta;
		return false;
	}
	
	public boolean getHit(Thing src, int damage){
		if(cooldown <= 0 && damage > 0){
			if(t.ground != null && t.ground.g){
				t.ground.leaveGround(t.pos.p.x > src.pos.p.x ? 200 : -200, 300);
			}
			health -= damage;
			cooldown = coolDownStart;
			return true;
		}
		return false;
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}

}
