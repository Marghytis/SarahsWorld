package main;

import render.TexAtlas;

public class Test {

	public static void main(String[] args)
	{
		Main.initializeGame("Sarahs World");
		
		Main.resetCoreLists();
		
		
		TexAtlas cloud = Res.getAtlas("cloud");
		System.out.println(cloud);
	}
	
}
