package world;

import render.Texture;
import things.aiPlugins.Physics;
import util.Color;

public enum Material {
	GRASS(Color.GREEN, Physics.grassFriction, new Texture("res/materials/Grass.png", 0, 0), 2, 0, 0),
	EARTH(Color.BROWN, Physics.grassFriction, new Texture("res/materials/Earth.png", 0, 0), 2, 0, 0),
	STONE(Color.GRAY, Physics.grassFriction, new Texture("res/materials/Stone.png", 0, 0), 2, 0, 0),
	SANDSTONE(Color.YELLOW, Physics.grassFriction, new Texture("res/materials/Sandstone3.png", 0, 0), 2, 0, 0),
	SANDSTONE2(Color.YELLOW, Physics.grassFriction, new Texture("res/materials/Sandstone2.png", 0, 0), 2, 0, 0),
	SAND(Color.YELLOW, Physics.grassFriction, new Texture("res/materials/Sand.png", 0, 0), 2, 0, 0),
	WATER(Color.INVISIBLE, 4f, Texture.empty, 1, 1200, 0.5),
	CANDY(Color.BLUE, 400f, new Texture("res/materials/Candy.png", 0, 0), 2, 0, 0.2),
	SNOW(new Color(0.8f, 0.8f, 0.8f), 400f, Texture.empty, 2, 0, 0.2),
	SOIL(Color.BROWN, Physics.grassFriction, new Texture("res/materials/Soil.png", 0, 0), 2, 0, 0),
	AIR(new Color(0.8f, 0.8f, 1, 0), 0.001f, Texture.empty, 0, 0, 1);
	
	public Color color = new Color();
	public Texture tex;
	public float deceleration;
	/**
	 * <1: gas; 1: fluid; 2: solid
	 */
	public int solidity;
	public double bouyancy;
	public double tranparency;
	
	Material(Color color, float deceleration, Texture tex, int solid, double bouyancy, double transparency){
		this.color.set(color);
		this.deceleration = deceleration;
		this.tex = tex;
		this.solidity = solid;
		this.bouyancy = bouyancy;
		this.tranparency = transparency;
	}
	
}
