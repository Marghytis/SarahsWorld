package main;

import render.TexFile;
import render.TexFileInfo;

public class Res {
	
	//Static things
	public static final TexFile cloud = new TexFile("res/objects/Cloud.png", 1, 1, -0.5f, -0.5f);
	public static final TexFile tree = new TexFile("res/objects/Tree.png", 1, 3, -0.5f, -0.3f);
	public static final TexFile tree_fir = new TexFile("res/objects/Fir.png", 1, 3, -0.5, -0.1);
	public static final TexFile tree_firSnow = new TexFile("res/objects/Fir_Snow.png", 1, 3, -0.5, -0.2);
	public static final TexFile tree_grave = new TexFile("res/objects/GraveTree.png", 1, 2, -0.5f, -0.2f);
	public static final TexFile tree_jungle	= new TexFile("res/objects/JungleTree.png", 1, 4, -0.5f, -0.2f);
	public static final TexFile plant_jungle = new TexFile("res/objects/Fern.png", 1, 5, -0.5f, -0.2f);
	public static final TexFile flower_jungle = new TexFile("res/objects/JungleFlower.png", 1, 5, -0.5f, -0.05f);
	public static final TexFile bush_jungle = new TexFile("res/objects/JungleBush.png", 1, 1, -0.5f, -0.2f);
	public static final TexFile plant_giant = new TexFile("res/objects/GiantPlant.png", 1, 4, -0.5f, -0.2f);
	public static final TexFile grass_giant = new TexFile("res/objects/GiantGras.png", 1, 3, -0.5f, -0.2f);
	public static final TexFile tree_palm = new TexFile("res/objects/PalmTree.png", 1, 3, -0.5f, -0.03f);
	public static final TexFile tree_candy = new TexFile("res/objects/CandyTree.png", 1, 1, -0.5f, -0.2f);
	public static final TexFile bamboo = new TexFile("res/objects/Bamboo.png", 1, 4, -0.5f, -0.02f);
	public static final TexFile bush_normal = new TexFile("res/objects/Bush.png", 1, 2, -0.5f, -0.02f);
	public static final TexFile bush_candy = new TexFile("res/objects/CandyBush.png", 1, 2, -0.5f, -0.2f);
	public static final TexFile cactus = new TexFile("res/objects/Cactus.png", 1, 3, -0.5f, -0.05f);
	public static final TexFile grasstuft = new TexFile("res/objects/Grass_tuft.png", 4, 1, -0.5f, -0.2f);
	public static final TexFile flower_normal = new TexFile("res/objects/Flower.png", 1, 3, -0.5f, 0f);
	public static final TexFile pyramide = new TexFile("res/objects/Pyramide.png", 1, 4, -0.5f, -0.1f);
	public static final TexFile house = new TexFile("res/objects/House.png", 1, 6, -0.5f, -0.05f);
	public static final TexFile townobject = new TexFile("res/objects/TownObject.png", 1, 5, -0.5f, -0.02f);
	public static final TexFile flower_candy = new TexFile("res/objects/Candy.png", 1, 6, -0.5f, 0f);
	public static final TexFile crack = new TexFile("res/objects/Crack.png", 1, 4, -0.5f, -0.5f);
	public static final TexFile fossil = new TexFile("res/objects/Fossil.png", 1, 3, -0.5f, -0.5f);
	public static final TexFile grave = new TexFile("res/objects/Grave.png", 1, 7, -0.5f, -0.05f);
	public static final TexFile rainbow = new TexFile("res/objects/Rainbow.png", 1, 1, -0.5f, 0f);
	public static final TexFile flower_light = new TexFile("res/Light_dimmed.png");
	
	//Creatures
	public static final TexFileInfo sloth_onTreePos = new TexFileInfo("res/objects/Sloth_JungleTree.txt");
	public static final TexFileInfo sarah_HandPos = new TexFileInfo("res/creatures/Sarah.txt");
	public static final TexFileInfo sarah_HandPos_onCow = new TexFileInfo("res/creatures/Sarah_riding_cow.txt");
	public static final TexFileInfo sarah_HeadPos = new TexFileInfo("res/creatures/Sarah_HORN.txt");
	public static final TexFileInfo sarah_HeadPos_onCow = new TexFileInfo("res/creatures/Sarah_riding_cow_horn.txt");
	public static final TexFile sarah = new TexFile("res/creatures/Sarah.png", 11, 10, -0.5, -0.1);{sarah.addInfo(sarah_HandPos, sarah_HeadPos);}
	public static final TexFile sarah_onCow = new TexFile("res/creatures/Sarah_riding_cow.png", 7, 2, -0.5, -0.1);{sarah.addInfo(sarah_HandPos_onCow, sarah_HeadPos_onCow);}
	public static final TexFile sarah_death = new TexFile("res/creatures/Sarah_death.png", 14, 1, -0.5f, -0.5f);
	public static final TexFile snail  = new TexFile("res/creatures/Snail.png", 7, 3, -0.5f, -0.1f);
	public static final TexFile butterfly  = new TexFile("res/creatures/Butterfly.png", 5, 2, -0.5f, -0.5f);
	public static final TexFile heart = new TexFile("res/creatures/Heart.png", 4, 2, -0.5f, -0.2f);
	public static final TexFile rabbit  = new TexFile("res/creatures/Rabbit.png", 5, 3, -0.5f, -0.2f);
	public static final TexFile bird  = new TexFile("res/creatures/Bird.png", 5, 4, -0.5f, -0.2f);
	public static final TexFile panda  = new TexFile("res/creatures/Panda.png", 6, 2, -0.5f, -0.1f);
	public static final TexFile scorpion  = new TexFile("res/creatures/Scorpion.png", 7, 2, -0.5f, -0.1f);
	public static final TexFile cow  = new TexFile("res/creatures/Cow.png", 7, 1, -0.5f, -0.1f);
	public static final TexFile unicorn = new TexFile("res/creatures/Unicorn.png", 6, 3, -0.5f, -0.1f);
	public static final TexFile unicorn_hair  = new TexFile("res/creatures/Unicorn_hair.png", 6, 3, -0.5f, -0.1f);
	public static final TexFile trex  = new TexFile("res/creatures/Trex.png", 9, 4, -0.5f, -0.05f);
	public static final TexFile cat_giant  = new TexFile("res/creatures/GiantCat.png", 5, 2, -0.5f, -0.05f);
	public static final TexFile villager  = new TexFile("res/creatures/NPC.png", 1, 4, -0.5f, -0.05f);
	public static final TexFile zombie  = new TexFile("res/creatures/Zombie.png", 4, 2, -0.5f, -0.05f);
	public static final TexFile sloth  = new TexFile("res/creatures/Sloth.png", 5, 1, -0.5f, -0.05f);
	public static final TexFile coin  = new TexFile("res/Items/Coin.png", -0.5f, -0.2f);

	public static final TexFile menu_button = new TexFile("res/Button.png", 1, 2, -0.5f, -0.5f);
	public static final TexFile inventory = new TexFile("res/items/Inventory.png", 1, 2);
	public static final TexFile items_world = new TexFile("res/items/ItemsWorld.png", 5, 1, -0.5f, -0.5f);
	public static final TexFile items_hand = new TexFile("res/items/ItemsHand.png", 6, 1, -0.5f, -0.5f);
	public static final TexFile items_inv = new TexFile("res/items/ItemsInv.png", 7, 1, -0.5f, -0.5f);
	public static final TexFile items_weapons = new TexFile("res/items/Weapons.png", 1, 6, -0.5f, -0.5f);
	public static final TexFile moneybag = new TexFile("res/items/Moneybag.png", 1, 1, -0.5f, -0.5f);
}
