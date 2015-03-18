package world;

import render.TexFile;
import util.Color;

public enum Material {
	GRASS(Color.GREEN, 400f, new TexFile("res/materials/Grass.png")),
	EARTH(Color.BROWN, 300f, new TexFile("res/materials/Earth.png")),
	STONE(Color.GRAY, 300f, new TexFile("res/materials/Stone.png")),
	SANDSTONE(Color.YELLOW, 300f, new TexFile("res/materials/Sandstone.png")),
	SAND(Color.YELLOW, 300f, new TexFile("res/materials/Sand.png")),
	WATER(Color.BLUE, 300f, new TexFile("res/materials/Water.png")),
	CANDY(Color.BLUE, 400f, new TexFile("res/materials/Candy.png")),
	AIR(new Color(1, 1, 1, 0), 150f, null),
	SNOW(new Color(0.8f, 0.8f, 0.8f), 400f, null),
	WOOD(Color.BROWN, 300f, null);
	
	public Color color = new Color();
	public TexFile tex;
	public float deceleration;
	
	Material(Color color, float deceleration, TexFile tex){
		this.color.set(color);
		this.deceleration = deceleration;
		this.tex = tex;
	}
	
}
