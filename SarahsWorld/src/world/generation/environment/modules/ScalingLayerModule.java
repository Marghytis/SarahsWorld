package world.generation.environment.modules;

import world.data.Vertex;
import world.generation.Material;

public class ScalingLayerModule extends Module {

	Module guide, follower;
	Material mat;
	double collisionY, thickness, sizeEnd, scaleRate;
	boolean invisible;
	
	public ScalingLayerModule(int index0, Module guide, Module follower, Material mat) {
		this(index0, guide, follower, mat, 0);
	}
	
	public ScalingLayerModule(int index0, Module guide, Module follower, Material mat, double sizeStart) {
		super(index0, 1, guide.collision);
		this.mat = mat;
		this.guide = guide;
		this.follower = follower;
		this.thickness = sizeStart;
		this.invisible = true;
	}
	
	public void step() {
		if(invisible && !finishedScaling()) {
			invisible = false;
		} else if(finishedScaling() && sizeEnd == 0) {
			invisible = true;
		} else {
			thickness += scaleRate;
			if(finishedScaling()) {
				thickness = sizeEnd;
			}
		}
	}
	
	public boolean visible() {
		return !invisible;
	}
	
	public void scale(double sizeEnd, double scaleRate) {
		this.sizeEnd = sizeEnd;
		this.scaleRate = scaleRate;
	}

	public Vertex createVertex(int index) {
		Material[] mats = new Material[Vertex.maxMatCount];
		double[] alphas = new double[Vertex.maxMatCount];
		alphas[0] = 1;
		mats[0] = Material.AIR;
		mats[1] = Material.AIR;
		mats[2] = Material.AIR;
		mats[3] = Material.AIR;
		if(!invisible)
			mats[0] = mat;
		collisionY = thickness+guide.getCollisionY();
		return new Vertex(index0, mats, alphas, 0, 0, Math.abs(thickness), collisionY);
	}

	public double getCollisionY() {
		return guide.getCollisionY() + thickness;
	}

	public boolean finishedScaling() {
		return scaleRate*thickness >= scaleRate*sizeEnd;//multiplied by scaleRate to account for positive and negative rates
	}
	
	public boolean done() {
		return false;
	}
	public int stepsBeforeEnd() {
		return 0;
	}
	public Module getFollower() {
		return follower;
	}

}
