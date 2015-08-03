package main;

import java.awt.Font;

import render.TexFile;
import render.TexFileInfo;
import util.Color;
import util.Sound;
import util.TrueTypeFont;

public class Res {
	
	//Static things
	public static final TexFile cloud = new TexFile("SarahsWorld/res/objects/Cloud.png", 1, 1, -0.5f, -0.5f);
	public static final TexFile tree = new TexFile("SarahsWorld/res/objects/Tree.png", 1, 3, -0.5f, -0.3f);
	public static final TexFile tree_fir = new TexFile("SarahsWorld/res/objects/Fir.png", 1, 3, -0.5, -0.1);
	public static final TexFile tree_firSnow = new TexFile("SarahsWorld/res/objects/Fir_Snow.png", 1, 3, -0.5, -0.2);
	public static final TexFile tree_grave = new TexFile("SarahsWorld/res/objects/GraveTree.png", 1, 2, -0.5f, -0.2f);
	public static final TexFile tree_jungle	= new TexFile("SarahsWorld/res/objects/JungleTree.png", 1, 4, -0.5f, -0.2f);
	public static final TexFile plant_jungle = new TexFile("SarahsWorld/res/objects/Fern.png", 1, 5, -0.5f, -0.2f);
	public static final TexFile flower_jungle = new TexFile("SarahsWorld/res/objects/JungleFlower.png", 1, 5, -0.5f, -0.05f);
	public static final TexFile bush_jungle = new TexFile("SarahsWorld/res/objects/JungleBush.png", 1, 1, -0.5f, -0.2f);
	public static final TexFile plant_giant = new TexFile("SarahsWorld/res/objects/GiantPlant.png", 1, 4, -0.5f, -0.2f);
	public static final TexFile grass_giant = new TexFile("SarahsWorld/res/objects/GiantGras.png", 1, 3, -0.5f, -0.2f);
	public static final TexFile tree_palm = new TexFile("SarahsWorld/res/objects/PalmTree.png", 1, 3, -0.5f, -0.03f);
	public static final TexFile tree_candy = new TexFile("SarahsWorld/res/objects/CandyTree.png", 1, 1, -0.5f, -0.2f);
	public static final TexFile bamboo = new TexFile("SarahsWorld/res/objects/Bamboo.png", 1, 4, -0.5f, -0.02f);
	public static final TexFile bush_normal = new TexFile("SarahsWorld/res/objects/Bush.png", 1, 2, -0.5f, -0.02f);
	public static final TexFile bush_candy = new TexFile("SarahsWorld/res/objects/CandyBush.png", 1, 2, -0.5f, -0.2f);
	public static final TexFile cactus = new TexFile("SarahsWorld/res/objects/Cactus.png", 1, 3, -0.5f, -0.05f);
	public static final TexFile grasstuft = new TexFile("SarahsWorld/res/objects/Grass_tuft.png", 4, 1, -0.5f, -0.2f);
	public static final TexFile flower_normal = new TexFile("SarahsWorld/res/objects/Flower.png", 1, 3, -0.5f, 0f);
	public static final TexFile pyramide = new TexFile("SarahsWorld/res/objects/Pyramide.png", 1, 4, -0.5f, -0.1f);
	public static final TexFile house = new TexFile("SarahsWorld/res/objects/House.png", 1, 6, -0.5f, -0.05f);
	public static final TexFile townobject = new TexFile("SarahsWorld/res/objects/TownObject.png", 1, 5, -0.5f, -0.02f);
	public static final TexFile flower_candy = new TexFile("SarahsWorld/res/objects/Candy.png", 1, 6, -0.5f, 0f);
	public static final TexFile crack = new TexFile("SarahsWorld/res/objects/Crack.png", 1, 4, -0.5f, -0.5f);
	public static final TexFile fossil = new TexFile("SarahsWorld/res/objects/Fossil.png", 1, 3, -0.5f, -0.5f);
	public static final TexFile grave = new TexFile("SarahsWorld/res/objects/Grave.png", 1, 7, -0.5f, -0.05f);
	public static final TexFile rainbow = new TexFile("SarahsWorld/res/objects/Rainbow.png", 1, 1, -0.5f, 0f);
//	public static final TexFile flower_light = new TexFile("SarahsWorld/res/Light_dimmed.png");
	
	//Creatures
	public static final TexFileInfo sloth_onTreePos = new TexFileInfo("SarahsWorld/res/objects/Sloth_JungleTree.txt");
	public static final TexFileInfo sarah_HandPos = new TexFileInfo("SarahsWorld/res/creatures/Sarah.txt");
	public static final TexFileInfo sarah_HandPos_onCow = new TexFileInfo("SarahsWorld/res/creatures/Sarah_riding_cow.txt");
	public static final TexFileInfo sarah_HeadPos = new TexFileInfo("SarahsWorld/res/creatures/Sarah_HORN.txt");
	public static final TexFileInfo sarah_HeadPos_onCow = new TexFileInfo("SarahsWorld/res/creatures/Sarah_riding_cow_horn.txt");
	public static final TexFileInfo villager_HandPos = new TexFileInfo("SarahsWorld/res/creatures/NPC.txt");
	public static final TexFile sarah = new TexFile("SarahsWorld/res/creatures/Sarah.png", 11, 10, -0.5, -0.1);static {sarah.addInfo(sarah_HandPos, sarah_HeadPos);}
	public static final TexFile sarah_onCow = new TexFile("SarahsWorld/res/creatures/Sarah_riding_cow.png", 7, 2, -0.5, -0.1);static {sarah_onCow.addInfo(sarah_HandPos_onCow, sarah_HeadPos_onCow);}
	public static final TexFile sarah_death = new TexFile("SarahsWorld/res/creatures/Sarah_death.png", 14, 1, -0.5f, -0.5f);
	public static final TexFile snail  = new TexFile("SarahsWorld/res/creatures/Snail.png", 7, 3, -0.5f, -0.1f);
	public static final TexFile butterfly  = new TexFile("SarahsWorld/res/creatures/Butterfly.png", 5, 2, -0.5f, -0.5f);
	public static final TexFile heart = new TexFile("SarahsWorld/res/creatures/Heart.png", 4, 2, -0.5f, -0.2f);
	public static final TexFile rabbit  = new TexFile("SarahsWorld/res/creatures/Rabbit.png", 5, 3, -0.5f, -0.2f);
	public static final TexFile bird  = new TexFile("SarahsWorld/res/creatures/Bird.png", 5, 4, -0.5f, -0.2f);
	public static final TexFile panda  = new TexFile("SarahsWorld/res/creatures/Panda.png", 6, 2, -0.5f, -0.1f);
	public static final TexFile scorpion  = new TexFile("SarahsWorld/res/creatures/Scorpion.png", 7, 2, -0.5f, -0.1f);
	public static final TexFile cow  = new TexFile("SarahsWorld/res/creatures/Cow.png", 7, 1, -0.5f, -0.1f);
	public static final TexFile unicorn = new TexFile("SarahsWorld/res/creatures/Unicorn.png", 6, 3, -0.5f, -0.1f);
	public static final TexFile unicorn_hair  = new TexFile("SarahsWorld/res/creatures/Unicorn_hair.png", 6, 3, -0.5f, -0.1f);
	public static final TexFile trex  = new TexFile("SarahsWorld/res/creatures/Trex.png", 9, 4, -0.5f, -0.05f);
	public static final TexFile cat_giant  = new TexFile("SarahsWorld/res/creatures/GiantCat.png", 5, 2, -0.5f, -0.05f);
	public static final TexFile villager  = new TexFile("SarahsWorld/res/creatures/NPC.png", 1, 4, -0.5f, -0.05f);static {villager.addInfo(villager_HandPos);}
	public static final TexFile zombie  = new TexFile("SarahsWorld/res/creatures/Zombie.png", 4, 2, -0.5f, -0.05f);
	public static final TexFile sloth  = new TexFile("SarahsWorld/res/creatures/Sloth.png", 5, 1, -0.5f, -0.05f);
	public static final TexFile coin  = new TexFile("SarahsWorld/res/Items/Coin.png", -0.5f, -0.2f);

	public static final TexFile inventory = new TexFile("SarahsWorld/res/items/Inventory.png", 1, 2);
	public static final TexFile items_world = new TexFile("SarahsWorld/res/items/ItemsWorld.png", 5, 1, -0.5f, -0.5f);
	public static final TexFile items_hand = new TexFile("SarahsWorld/res/items/ItemsHand.png", 6, 1, -0.5f, -0.5f);
	public static final TexFile items_inv = new TexFile("SarahsWorld/res/items/ItemsInv.png", 20, 1, -0.5f, -0.5f);
	public static final TexFile items_weapons = new TexFile("SarahsWorld/res/items/Weapons.png", 1, 6, -0.5f, -0.5f);
	public static final TexFile moneybag = new TexFile("SarahsWorld/res/items/Moneybag.png", 1, 1, -0.5f, -0.5f);
	
	public static final TexFile answer = new TexFile("SarahsWorld/res/menu/Answer.png", 1, 2, -0.5, -0.5);

	public static final TexFile light = new TexFile("SarahsWorld/res/particles/Light.png", -0.5, -0.5);
	public static final TexFile light1 = new TexFile("SarahsWorld/res/particles/Light1.png", -0.5, -0.5);
	public static final TexFile light2 = new TexFile("SarahsWorld/res/particles/Light2.png", -0.5, -0.5);
	
	public static TrueTypeFont menuFont = new TrueTypeFont(new Font("Times New Roman", 0, 40), true);
	public static Color menuFontColor = new Color(0.9f, 0.8f, 0.1f);

	public static final Sound music = new Sound("SarahsWorld/res/sound/Sarahs Welt Loop.wav", null);
	public static final Sound coinSound = new Sound("SarahsWorld/res/sound/coins_quick_movement_in_hand.wav", null);
}
