package world;

import main.Res;
import render.Texture;
import things.aiPlugins.Physics;
import util.Color;

public enum Material {
	GRASS(Color.GREEN, Physics.grassFriction, Res.grass, 2, 0, 0),
	EARTH(Color.BROWN, Physics.grassFriction, Res.earth, 2, 0, 0),
	CLAY(Color.BROWN, Physics.grassFriction, Res.clay, 2, 0, 0),
	STONE(Color.GRAY, Physics.grassFriction, Res.stone, 2, 0, 0),
	STONE2(Color.GRAY, Physics.grassFriction, Res.stone2, 2, 0, 0),
	SANDSTONE(Color.YELLOW, Physics.grassFriction, Res.sandstone3, 2, 0, 0),
	SANDSTONE2(Color.YELLOW, Physics.grassFriction, Res.sandstone2, 2, 0, 0),
	SAND(Color.YELLOW, Physics.grassFriction, Res.sand, 2, 0, 0),
	WATER(Color.BLUE, 4f, Res.water, 1, 1600, 0.5),
	CANDY(Color.BLUE, 400f, Res.candy, 2, 0, 0.2),
	SNOW(new Color(0.8f, 0.8f, 0.8f), 400f, Texture.emptyTexture, 2, 0, 0.2),
	SOIL(Color.BROWN, Physics.grassFriction, Res.soil, 2, 0, 0),
	AIR(new Color(0.8f, 0.8f, 1, 0), 0.001f, Texture.emptyTexture, 0, 0, 1);
	
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
