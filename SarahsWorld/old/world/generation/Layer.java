package world.generation;

import main.Savable;
import world.WorldContainer.WorldPoint;
import world.generation.BiomeOld.AimLayer;

public class Layer implements Savable{

	public AimLayer aim;
	public double thickness;
	public WorldPoint lastPDown, lastPUp;
	
	public boolean reachedAim;
	public boolean disappeared;
	
	public Layer(AimLayer aim){
		this.aim= aim;
	}
	
	public void reachAim(){
		if(!reachedAim){
			if(thickness + aim.sizingSpeed < aim.thickness){
				thickness += aim.sizingSpeed;
			} else {
				thickness = aim.thickness;
				reachedAim = true;
			}
		}
	}
	
	public void disappear(){
		if(!disappeared){
			if(thickness - aim.sizingSpeed > 0){
				thickness -= aim.sizingSpeed;
			} else {
				thickness = 0;
				disappeared = true;
			}
		}
	}
	
	public String toString(){
		return "Layer: " + aim.mat + " thickness: " + thickness;
	}

	public String save() {
		return    aim.mat.name() + layer
				+ aim.thickness + layer
				+ aim.sizingSpeed + layer
				+ aim.priority + layer
				+ thickness + layer
				+ lastPDown.i + layer
				+ lastPUp.i + layer
				+ reachedAim + layer
				+ disappeared + layer;
	}

	public void load(String save) {
		
	}
}
