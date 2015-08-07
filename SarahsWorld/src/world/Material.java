package world;

import render.Texture;
import util.Color;
import util.math.Vec;

public enum Material {
	GRASS(Color.GREEN, 400f, new Texture("res/materials/Grass.png", 0, 0), 2, null, 0),
	EARTH(Color.BROWN, 300f, new Texture("res/materials/Earth.png", 0, 0), 2, null, 0),
	STONE(Color.GRAY, 300f, new Texture("res/materials/Stone.png", 0, 0), 2, null, 0),
	SANDSTONE(Color.YELLOW, 300f, new Texture("res/materials/Sandstone.png", 0, 0), 2, null, 0),
	SAND(Color.YELLOW, 300f, new Texture("res/materials/Sand.png", 0, 0), 2, null, 0),
	WATER(Color.INVISIBLE, 200f, Texture.empty, 1, new Vec(0, 1500), 0.5),
	CANDY(Color.BLUE, 400f, new Texture("res/materials/Candy.png", 0, 0), 2, null, 0.2),
	SNOW(new Color(0.8f, 0.8f, 0.8f), 400f, Texture.empty, 2, null, 0.2),
	WOOD(Color.BROWN, 300f, Texture.empty, 1, null, 0.5),
	AIR(new Color(0.8f, 0.8f, 1, 0), 100f, Texture.empty, 0, null, 1);
	
	public Color color = new Color();
	public Texture tex;
	public float deceleration;
	public int solidity;
	public Vec bouyancy;
	public double tranparency;
	
	Material(Color color, float deceleration, Texture tex, int solid, Vec bouyancy, double transparency){
		this.color.set(color);
		this.deceleration = deceleration;
		this.tex = tex;
		this.solidity = solid;
		this.bouyancy = bouyancy;
		this.tranparency = transparency;
	}
	
}
