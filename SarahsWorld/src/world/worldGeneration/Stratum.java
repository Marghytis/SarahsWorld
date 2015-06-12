package world.worldGeneration;

import world.Material;

public class Stratum {

	public Material material;
	public double thickness;
	public double thicknessVariance;//(multiplier)
	public double transitionHeight;//to the Stratum below
	public int transitionWidth;//to the neighboring vertices
	public int wait;
	public double sizingSpeed;
	
	public Stratum(Material material, double defaultThickness, double thicknessVariance, double thicknessSizingSpeed, double transitionHeight, int transitionWidth, int wait){
		this.material = material;
		this.thickness = defaultThickness;
		this.thicknessVariance = thicknessVariance;
		this.sizingSpeed = thicknessSizingSpeed;
		this.transitionHeight = transitionHeight;
		this.transitionWidth = transitionWidth;
		this.wait = wait;
	}
}
