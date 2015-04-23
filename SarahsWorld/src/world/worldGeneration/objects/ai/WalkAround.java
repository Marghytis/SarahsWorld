package world.worldGeneration.objects.ai;



public class WalkAround extends AiPlugin{

	double acc;
	double xDestMin, xDestMax;
	double waitTime;
	
	public WalkAround(Thing thing, double acc) {
		super(thing);
		this.acc = acc;
		xDestMin = t.pos.p.x + t.rand.nextInt(500) - 250;
		xDestMax = xDestMin + 20;
	}

	public boolean action(double delta) {
		if(t.ground.g) {
			double acc = 0;
			if(waitTime > 0){
				waitTime -= delta;
			} else if(t.pos.p.x < xDestMin){
				t.ground.acc += acc;
			} else if(t.pos.p.x > xDestMax){
				t.ground.acc -= acc;
			} else {
				if(t.rand.nextBoolean()){
					waitTime = t.rand.nextInt(10);
				} else {
					xDestMin = t.pos.p.x + t.rand.nextInt(500) - 250;
					xDestMax = xDestMin + 20;
				}
			}
			t.ground.setAni(acc);
			t.ground.acc += acc;
		}
		return false;
	}

	public String save() {
		return "";
	}

	public void load(String save) {
	}

}
