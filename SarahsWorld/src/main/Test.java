package main;

import render.TexAtlas;

public class Test {

	public static void main(String[] args)
	{

		Main.prepareTest("Sarahs World");
		
		TexAtlas cloud = Res.getAtlas("cloud");
		System.out.println(cloud);
		
	}
	
}
