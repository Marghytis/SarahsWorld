package world.generation.environment.modules;

import world.data.Vertex;
import world.generation.Material;

public class FluidFilling extends Module {

	private double y, thickness;
	private Material fluid;
	private boolean visible, done, stopAtIsland, active, copyYnext;
	private Module guide;

	public FluidFilling(int index0, Module guide, Material fluid) {
		this(index0, guide, fluid, 0, true);
	}
	public FluidFilling(int index0, Module guide, Material fluid, double y, boolean stopAtIsland) {
		super(index0, 1, false);
		
		this.guide = guide;
		this.fluid = fluid;
		this.y = y;
		this.stopAtIsland = stopAtIsland;
		this.active = !stopAtIsland;
	}

	public void step() {
		if(guide.done()) {
			done = true;
		}
		if(active) {
			if(copyYnext) {
				y = guide.getCollisionY();
				copyYnext = false;
			}
			thickness = guide.getCollisionY() - y;
			if(guide.getCollisionY() <= y) {
				visible = true;
			} else {
				if(visible && stopAtIsland) {//if it was visible before this column, we have reached an island
					active = false;
				}
				visible = false;
				thickness = 0;
			}
		}
	}
	
	public boolean visible() {
		return visible;
	}
	
	public void startAt(double y) {
		this.y = y;
		this.active = true;
	}
	
	public void start() {
		copyYnext = true;
		this.active = true;
	}
	
	public void stop() {
		this.active = false;
	}

	public Vertex createVertex(int index) {
		Material[] mats = new Material[Vertex.maxMatCount];
		double[] alphas = new double[Vertex.maxMatCount];
		alphas[0] = 1;
		mats[0] = Material.AIR;
		mats[1] = Material.AIR;
		mats[2] = Material.AIR;
		mats[3] = Material.AIR;
		if(active)
			mats[0] = fluid;
		return new Vertex(index0, mats, alphas, 0, 0, Math.abs(thickness), y);
	}

	public double getCollisionY() {
		return y;
	}

	public int stepsBeforeEnd() {
		return 0;
	}
	
	@Override
	public boolean done() {
		return done;
	}

}
