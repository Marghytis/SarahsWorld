package main;

import java.awt.Font;

import render.TexAtlas;
import render.TexFile;
import render.TexInfo;
import render.Texture;
import util.Color;
import util.Sound;
import util.TrueTypeFont;

public class Res {
	
	//togethers
	public static final TexFile NPC_plus_Handheld_Items = new TexFile("res/creatures/NPC_and_handheld_Items.png");
	
	//Static things
	public static final TexAtlas cloud = new TexAtlas("res/objects/Cloud.png", 1, 1, -0.5f, -0.5f);
	public static final TexAtlas tree = new TexAtlas("res/objects/Tree.png", 1, 3, -0.5f, -0.3f);
	public static final TexAtlas tree_fir = new TexAtlas("res/objects/Fir.png", 1, 3, -0.5, -0.1);
	public static final TexAtlas tree_firSnow = new TexAtlas("res/objects/Fir_Snow.png", 1, 3, -0.5, -0.2);
	public static final TexAtlas tree_grave = new TexAtlas("res/objects/GraveTree.png", 1, 2, -0.5f, -0.2f);
	public static final TexAtlas tree_jungle	= new TexAtlas("res/objects/JungleTree.png", 1, 4, -0.5f, -0.2f);
	public static final TexAtlas plant_jungle = new TexAtlas("res/objects/Fern.png", 1, 5, -0.5f, -0.02f);
	public static final TexAtlas flower_jungle = new TexAtlas("res/objects/JungleFlower.png", 1, 5, -0.5f, -0.05f);
	public static final TexAtlas bush_jungle = new TexAtlas("res/objects/JungleBush.png", 1, 1, -0.5f, -0.2f);
	public static final TexAtlas plant_giant = new TexAtlas("res/objects/GiantPlant.png", 1, 4, -0.5f, -0.2f);
	public static final TexAtlas grass_giant = new TexAtlas("res/objects/GiantGras.png", 1, 3, -0.5f, -0.2f);
	public static final TexAtlas tree_palm = new TexAtlas("res/objects/PalmTree.png", 1, 3, -0.5f, -0.03f);
	public static final TexAtlas tree_candy = new TexAtlas("res/objects/CandyTree.png", 1, 1, -0.5f, -0.2f);
	public static final TexAtlas bamboo = new TexAtlas("res/objects/Bamboo.png", 1, 4, -0.5f, -0.02f);
	public static final TexAtlas bush_normal = new TexAtlas("res/objects/Bush.png", 1, 2, -0.5f, -0.1f);
	public static final TexAtlas bush_candy = new TexAtlas("res/objects/CandyBush.png", 1, 2, -0.5f, -0.2f);
	public static final TexAtlas cactus = new TexAtlas("res/objects/Cactus.png", 1, 3, -0.5f, -0.05f);
	public static final TexAtlas grasstuft = new TexAtlas("res/objects/Grass_tuft.png", 4, 1, -0.5f, -0.2f);
	public static final TexAtlas flower_normal = new TexAtlas("res/objects/Flower.png", 1, 3, -0.5f, 0f);
	public static final TexAtlas pyramide = new TexAtlas("res/objects/Pyramide.png", 1, 4, -0.5f, -0.1f);
	public static final TexAtlas house = new TexAtlas("res/objects/House.png", 1, 6, -0.5f, -0.05f);
	public static final TexAtlas townobject = new TexAtlas("res/objects/TownObject.png", 1, 5, -0.5f, -0.02f);
	public static final TexAtlas flower_candy = new TexAtlas("res/objects/Candy.png", 1, 6, -0.5f, 0f);
	public static final TexAtlas crack = new TexAtlas("res/objects/Crack.png", 1, 4, -0.5f, -0.5f);
	public static final TexAtlas fossil = new TexAtlas("res/objects/Fossil.png", 1, 3, -0.5f, -0.5f);
	public static final TexAtlas grave = new TexAtlas("res/objects/Grave.png", 1, 7, -0.5f, -0.05f);
	public static final TexAtlas rainbow = new TexAtlas("res/objects/Rainbow.png", 1, 1, -0.5f, 0f);
//	public static final TexFile flower_light = new TexFile("res/Light_dimmed.png");
	
	//Creatures
	public static final TexInfo sloth_onTreePos = new TexInfo("res/objects/Sloth_JungleTree.txt");
	public static final TexInfo sarah_HandPos = new TexInfo("res/creatures/Sarah.txt");
	public static final TexInfo sarah_HandPos_onCow = new TexInfo("res/creatures/Sarah_riding_cow.txt");
	public static final TexInfo sarah_HandPos_dive = new TexInfo("res/creatures/Sarah_dive_hand.txt");
	public static final TexInfo sarah_HandPos_swim = new TexInfo("res/creatures/Sarah_dive_hand.txt");
	public static final TexInfo sarah_HeadPos = new TexInfo("res/creatures/Sarah_HORN.txt");
	public static final TexInfo sarah_HeadPos_onCow = new TexInfo("res/creatures/Sarah_riding_cow_horn.txt");
	public static final TexInfo sarah_HeadPos_dive = new TexInfo("res/creatures/Sarah_dive_hand.txt");
	public static final TexInfo sarah_HeadPos_swim = new TexInfo("res/creatures/Sarah_dive_hand.txt");
	public static final TexInfo villager_HandPos = new TexInfo("res/creatures/NPC.txt");
	public static final TexAtlas sarah = new TexAtlas("res/creatures/Sarah.png", 11, 10, -0.5, -0.1);static {sarah.addInfo(sarah_HandPos, sarah_HeadPos);}
	public static final TexAtlas sarah_dive = new TexAtlas(sarah.file, 50, 0, 400, 75, 5, 1, -0.5, -0.1);static {sarah_dive.addInfo(sarah_HandPos_dive, sarah_HeadPos_dive);}
	public static final TexAtlas sarah_swim = new TexAtlas(sarah.file, 150, 675, 350, 75, 5, 1, -0.5, -0.5);static {sarah_swim.addInfo(sarah_HandPos_swim, sarah_HeadPos_swim);}
	public static final TexAtlas sarah_onCow = new TexAtlas("res/creatures/Sarah_riding_cow.png", 7, 2, -0.5, -0.1);static {sarah_onCow.addInfo(sarah_HandPos_onCow, sarah_HeadPos_onCow);}
	public static final TexAtlas sarah_death = new TexAtlas("res/creatures/Sarah_death.png", 14, 1, -0.5f, -0.5f);
	public static final TexAtlas snail  = new TexAtlas("res/creatures/Snail.png", 7, 3, -0.5f, -0.1f);
	public static final TexAtlas butterfly  = new TexAtlas("res/creatures/Butterfly.png", 5, 2, -0.5f, -0.5f);
	public static final TexAtlas heart = new TexAtlas("res/creatures/Heart.png", 4, 2, -0.5f, -0.2f);
	public static final TexAtlas rabbit  = new TexAtlas("res/creatures/Rabbit.png", 5, 3, -0.5f, -0.2f);
	public static final TexAtlas bird  = new TexAtlas("res/creatures/Bird.png", 5, 4, -0.5f, -0.2f);
	public static final TexAtlas panda  = new TexAtlas("res/creatures/Panda.png", 6, 2, -0.5f, -0.1f);
	public static final TexAtlas scorpion  = new TexAtlas("res/creatures/Scorpion.png", 7, 2, -0.5f, -0.1f);
	public static final TexAtlas cow  = new TexAtlas("res/creatures/Cow.png", 7, 1, -0.5f, -0.1f);
	public static final TexAtlas unicorn = new TexAtlas("res/creatures/Unicorn.png", 6, 3, -0.5f, -0.1f);
	public static final TexAtlas unicorn_hair  = new TexAtlas("res/creatures/Unicorn_hair.png", 6, 3, -0.5f, -0.1f);
	public static final TexAtlas trex  = new TexAtlas("res/creatures/Trex.png", 9, 4, -0.5f, -0.05f);
	public static final TexAtlas cat_giant  = new TexAtlas("res/creatures/GiantCat.png", 5, 2, -0.5f, -0.05f);
	public static final TexAtlas villager  = new TexAtlas(NPC_plus_Handheld_Items, 0, 0, 45, 340, 1, 4, -0.5f, -0.05f);static {villager.addInfo(villager_HandPos);}
	public static final TexAtlas zombie  = new TexAtlas("res/creatures/Zombie.png", 4, 2, -0.5f, -0.05f);
	public static final TexAtlas sloth  = new TexAtlas("res/creatures/Sloth.png", 5, 1, -0.5f, -0.05f);
	
	public static final TexInfo items_handheld_gripPos = new TexInfo("res/items/ItemsHandheld.txt");
	public static final TexAtlas coin  = new TexAtlas("res/Items/Coin.png", 1, 1, -0.5f, -0.2f);
	public static final TexAtlas inventory = new TexAtlas("res/items/Inventory.png", 1, 2, 0, 0);
	public static final TexAtlas items_world = new TexAtlas("res/items/ItemsWorld.png", 5, 1, -0.5f, -0.5f);
	public static final TexAtlas items_inv = new TexAtlas("res/items/ItemsInv.png", 20, 2, -0.5f, -0.5f);
	public static final TexAtlas items_weapons = new TexAtlas(NPC_plus_Handheld_Items, 45, 0, 175, 280, 5, 4, -0.5f, -0.5f);static {items_weapons.addInfo(items_handheld_gripPos);}
	public static final Texture moneybag = new Texture("res/items/Moneybag.png", -0.5f, -0.5f);
	
	public static final Texture light = new Texture("res/particles/Light.png", -0.5, -0.5);
	public static final Texture light1 = new Texture("res/particles/Light1.png", -0.5, -0.5);
	public static final Texture light2 = new Texture("res/particles/Light2.png", -0.5, -0.5);
	
	public static TrueTypeFont menuFont = new TrueTypeFont(new Font("Times New Roman", 0, 35), true);
	public static Color menuFontColor = new Color(0.9f, 0.8f, 0.1f);

//	public static final Sound music = new Sound("res/sound/Sarahs Welt Loop.wav", null);
	public static final Sound coinSound = new Sound("res/sound/coins_quick_movement_in_hand.wav", null);
}
