package main;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import render.Shader;
import render.TexAtlas;
import render.TexFile;
import render.TexInfo;
import render.Texture;
import util.Color;
import util.TrueTypeFont;

public class Res {
	
	public static TrueTypeFont menuFont = new TrueTypeFont(new Font("Times New Roman", 0, 35), true);
	public static Color menuFontColor = new Color(0.9f, 0.8f, 0.1f);

//	public static final Sound music = new Sound("res/sound/Sarahs Welt Loop.wav", null);
//	public static final Sound coinSound = new Sound("res/sound/coins_quick_movement_in_hand.wav", null);TODO add these sounds back in

	public static Shader landscapeShader = Shader.create("res/shader/material.vert", "res/shader/material.frag", "in_Position", "in_TextureCoords", "in_Alphas", "in_TransAlpha");
	public static Shader thingShader = Shader.withGeometry("res/shader/thing.vert", "res/shader/thing.geom", "res/shader/thing.frag", "in_position", "in_rotation", "in_texCoords", "in_mirror", "in_color", "in_z", "in_size", "in_box", "in_texWH");
	public static Shader thingOutlineShader = Shader.withGeometry("res/shader/thing.vert", "res/shader/thing.geom", "res/shader/thingOutline.frag", "in_position", "in_rotation", "in_texCoords", "in_mirror", "in_color", "in_z", "in_size", "in_box", "in_texWH");
	public static Shader darknessShader = Shader.create("res/shader/color.vert", "res/shader/color.frag", "in_Position", "in_Color");
	public static Shader backgroundShader = Shader.create("res/shader/colorBackground.vert", "res/shader/colorBackground.frag", "in_Position");
	public static Shader usualShader = Shader.create("res/shader/usual.vert", "res/shader/usual.frag", "in_position", "in_texCoords");

	public static TexAtlas getAtlas(String key) {
		TexAtlas out = texAtlases.get(key);
		if(out == null) {
			new Exception("TexAtlas not found!").printStackTrace();
		}
		return out;
	}
	public static Texture getTex(String key) {
		Texture out = textures.get(key);
		if(out == null) {
			new Exception("Texture not found!").printStackTrace();
		}
		return out;
	}
	public static TexInfo getInfo(String key) {
		TexInfo out = texInfos.get(key);
		if(out == null) {
			new Exception("TexInfo not found!").printStackTrace();
		}
		return out;
	}
	public static TexFile getFile(String key) {
		TexFile out = texFiles.get(key);
		if(out == null) {
			new Exception("TexFile not found!").printStackTrace();
		}
		return out;
	}

	static Hashtable<String, TexFile> texFiles = new Hashtable<>();
	static Hashtable<String, TexInfo> texInfos = new Hashtable<>();
	static Hashtable<String, Texture> textures = new Hashtable<>();
	static Hashtable<String, TexAtlas> texAtlases = new Hashtable<>();
	
	public static void readTexTable(String tablePath)
	{
		TexFile file;
		int[] imageCoords = {0,0,1,1};
		int partsX, partsY;
		Double offsetX, offsetY;
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(Main.TEX_ATLAS_TABLE_PATH));
			int lineIndex = 0;
			String line = reader.readLine();
			while (line != null) {

				String[] words = line.split("\\s+");
				
				if(words.length != 0 && words.length != 1 && !words[0].startsWith("//")) {
				
					int i = 0;
					String type = words[i++];
					String name = words[i++];
					
					switch(type) {
					
					case "TexFile" :	texFiles.put(name, new TexFile(words[i++]));
									break;
									
					case "TexInfo" :	texInfos.put(name, new TexInfo(words[i++]));
									break;
									
					case "Texture" :	textures.put(name, new Texture(words[i++], Double.parseDouble(words[i++]), Double.parseDouble(words[i++])));
									break;
						
					case "TexAtlas" :	
										if(words.length < 11) {
											System.out.println("ERROR: Not enough arguments in " + Main.TEX_ATLAS_TABLE_PATH + ", line " + lineIndex);
											break;
										}
										if(words[i].charAt(0) == '@') {
											file = texFiles.get(words[i].substring(1,words[i].length()));
										} else {
											file = new TexFile(words[i]);
										}
										i++;
										imageCoords[0] = 0;
										imageCoords[1] = 0;
										imageCoords[2] = file.width;
										imageCoords[3] = file.height;
										
										for(int j = 0; j < 4; j++, i++) {
											if(!words[i].equals("d")) //default
												imageCoords[j] = Integer.parseInt(words[i]);
										}
										
										partsX = Integer.parseInt(words[i++]);
										partsY = Integer.parseInt(words[i++]);
										offsetX = Double.parseDouble(words[i++]);
										offsetY = Double.parseDouble(words[i++]);

										
										TexAtlas atlas = new TexAtlas(file, imageCoords[0], imageCoords[1], imageCoords[2], imageCoords[3], partsX, partsY, offsetX, offsetY);
										if(words.length - i > 0) {
											TexInfo[] infos = new TexInfo[words.length - i];
											for(int j = 0; i < words.length; j++, i++) {
												infos[j] = texInfos.get(words[i].substring(1, words[i].length()));//pars start with '@' to make clear they're references 
											}
											atlas.addInfo(infos);
										}
										
										texAtlases.put(name, atlas);
										break;
					}
				}
				
				// read next line
				line = reader.readLine();
				lineIndex++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static {readTexTable(Main.TEX_ATLAS_TABLE_PATH);}
	
	//togethers
//	public static final TexFile NPC_plus_Handheld_Items = new TexFile("res/creatures/NPC_and_handheld_Items.png");
	
	//Static things (offsetY is location of point on floor)
//	public static final TexAtlas cloud = new TexAtlas("res/objects/Cloud.png", 1, 1, -0.5f, 0);//-0.5
//	public static final TexAtlas tree = new TexAtlas("res/objects/Tree.png", 1, 3, -0.5f, 0);//-0.3
//	public static final TexAtlas tree_fir = new TexAtlas("res/objects/Fir.png", 1, 3, -0.5, -0.1);//-0.1
//	public static final TexAtlas tree_firSnow = new TexAtlas("res/objects/Fir_Snow.png", 1, 3, -0.5, -0.2);//-0.2
//	public static final TexAtlas tree_grave = new TexAtlas("res/objects/GraveTree.png", 1, 2, -0.5f, -0.2);//-0.2
//	public static final TexAtlas tree_jungle	= new TexAtlas("res/objects/JungleTree.png", 1, 4, -0.5f, -0.12);//-0.1
//	public static final TexAtlas plant_jungle = new TexAtlas("res/objects/Fern.png", 1, 5, -0.5f, 0);//-0.1
//	public static final TexAtlas flower_jungle = new TexAtlas("res/objects/JungleFlower.png", 1, 5, -0.5f, 0);//-0.05
//	public static final TexAtlas bush_jungle = new TexAtlas("res/objects/JungleBush.png", 1, 1, -0.5f, 0);//-0.2
//	public static final TexAtlas plant_giant = new TexAtlas("res/objects/GiantPlant.png", 1, 4, -0.5f, 0);//-0.2
//	public static final TexAtlas grass_giant = new TexAtlas("res/objects/GiantGras.png", 1, 3, -0.5f, 0);//-0.1
//	public static final TexAtlas tree_palm = new TexAtlas("res/objects/PalmTree.png", 1, 3, -0.5f, 0);//-0.03
//	public static final TexAtlas tree_candy = new TexAtlas("res/objects/CandyTree.png", 1, 1, -0.5f, -0.17);//-0.2
//	public static final TexAtlas bamboo = new TexAtlas("res/objects/Bamboo.png", 1, 4, -0.5f, 0);//-0.02
//	public static final TexAtlas bush_normal = new TexAtlas("res/objects/Bush.png", 1, 2, -0.5f, 0);//-0.1
//	public static final TexAtlas bush_candy = new TexAtlas("res/objects/CandyBush.png", 1, 2, -0.5f, 0);//-0.2
//	public static final TexAtlas cactus = new TexAtlas("res/objects/Cactus.png", 1, 3, -0.5f, 0);//-0.05
//	public static final TexAtlas grasstuft = new TexAtlas("res/objects/Grass_tuft.png", 4, 1, -0.5f, -0.05);//-0.2
//	public static final TexAtlas flower_normal = new TexAtlas("res/objects/Flower.png", 1, 3, -0.5f, 0f);//0
//	public static final TexAtlas pyramide = new TexAtlas("res/objects/Pyramide.png", 1, 4, -0.5f, 0f);//-0.1
//	public static final TexAtlas house = new TexAtlas("res/objects/House.png", 1, 6, -0.5f, 0f);//-0.05
//	public static final TexAtlas cake = new TexAtlas("res/objects/BirthdayCake.png", 1, 1, -0.5f, -0.2f);//-0.05
//	public static final TexAtlas townobject = new TexAtlas("res/objects/TownObject.png", 1, 5, -0.5f, 0);//-0.02
//	public static final TexAtlas flower_candy = new TexAtlas("res/objects/Candy.png", 1, 6, -0.5f, 0);//0
//	public static final TexAtlas crack = new TexAtlas("res/objects/Crack.png", 1, 4, -0.5f, 0);//-0.5
//	public static final TexAtlas fossil = new TexAtlas("res/objects/Fossil.png", 1, 3, -0.5f, 0);//-0.5
//	public static final TexAtlas grave = new TexAtlas("res/objects/Grave.png", 1, 7, -0.5f, 0);//-0.05
//	public static final TexAtlas rainbow = new TexAtlas("res/objects/Rainbow.png", 1, 1, -0.5f, 0f);//0
//	public static final TexFile flower_light = new TexFile("res/Light_dimmed.png");
	
	//Creatures
//	public static final TexInfo sloth_onTreePos = new TexInfo("res/objects/Sloth_JungleTree.txt");
//	public static final TexInfo sarah_HandPos = new TexInfo("res/creatures/Sarah.txt");
//	public static final TexInfo sarah_HandPos_onCow = new TexInfo("res/creatures/Sarah_riding_cow.txt");
//	public static final TexInfo sarah_HandPos_dive = new TexInfo("res/creatures/Sarah_dive_hand.txt");
//	public static final TexInfo sarah_HandPos_swim = new TexInfo("res/creatures/Sarah_dive_hand.txt");
//	public static final TexInfo sarah_HeadPos = new TexInfo("res/creatures/Sarah_HORN.txt");
//	public static final TexInfo sarah_HeadPos_onCow = new TexInfo("res/creatures/Sarah_riding_cow_horn.txt");
//	public static final TexInfo sarah_HeadPos_dive = new TexInfo("res/creatures/Sarah_dive_hand.txt");
//	public static final TexInfo sarah_HeadPos_swim = new TexInfo("res/creatures/Sarah_dive_hand.txt");
//	public static final TexInfo villager_HandPos = new TexInfo("res/creatures/NPC.txt");
//	public static final TexInfo unicorn_MouthPos = new TexInfo("res/creatures/UnicornMouth.txt");
//	public static final TexAtlas sarah = new TexAtlas("res/creatures/Sarah.png", 11, 10, -0.5, -0.1);static {sarah.addInfo(sarah_HandPos, sarah_HeadPos);}
//	public static final TexAtlas sarah_dive = new TexAtlas(sarah.file, 50, 0, 400, 75, 5, 1, -0.5, -0.1);static {sarah_dive.addInfo(sarah_HandPos_dive, sarah_HeadPos_dive);}
//	public static final TexAtlas sarah_swim = new TexAtlas(sarah.file, 150, 675, 350, 56, 5, 1, -0.5, -0.5);static {sarah_swim.addInfo(sarah_HandPos_swim, sarah_HeadPos_swim);}
//	public static final TexAtlas sarah_onCow = new TexAtlas("res/creatures/Sarah_riding_cow.png", 7, 2, -0.5, -0.1);static {sarah_onCow.addInfo(sarah_HandPos_onCow, sarah_HeadPos_onCow);}
//	public static final TexAtlas sarah_death = new TexAtlas("res/creatures/Sarah_death.png", 14, 1, -0.5f, -0.5f);
//	public static final TexAtlas snail  = new TexAtlas("res/creatures/Snail.png", 7, 3, -0.5f, -0.1f);
//	public static final TexAtlas butterfly  = new TexAtlas("res/creatures/Butterfly.png", 5, 2, -0.5f, -0.5f);
//	public static final TexAtlas midge  = new TexAtlas("res/creatures/Midge.png", 1, 1, -0.5f, -0.5f);
//	public static final TexAtlas heart = new TexAtlas("res/creatures/Heart.png", 4, 2, -0.5f, -0.2f);
//	public static final TexAtlas rabbit  = new TexAtlas("res/creatures/Rabbit.png", 5, 6, -0.5f, -0.2f);
//	public static final TexAtlas bird  = new TexAtlas("res/creatures/Bird.png", 5, 4, -0.5f, -0.2f);
//	public static final TexAtlas panda  = new TexAtlas("res/creatures/Panda.png", 6, 2, -0.5f, -0.1f);
//	public static final TexAtlas scorpion  = new TexAtlas("res/creatures/Scorpion.png", 7, 2, -0.5f, -0.1f);
//	public static final TexAtlas cow  = new TexAtlas("res/creatures/Cow.png", 7, 1, -0.5f, -0.1f);
//	public static final TexAtlas unicorn = new TexAtlas("res/creatures/Unicorn.png", 6, 3, -0.5f, -0.1f);static {unicorn.addInfo(unicorn_MouthPos);}
//	public static final TexAtlas unicorn_hair  = new TexAtlas("res/creatures/Unicorn_hair.png", 6, 3, -0.5f, -0.1f);
//	public static final TexAtlas trex  = new TexAtlas("res/creatures/Trex.png", 9, 4, -0.5f, -0.05f);
//	public static final TexAtlas cat_giant  = new TexAtlas("res/creatures/GiantCat.png", 5, 2, -0.5f, -0.05f);
//	public static final TexAtlas villager  = new TexAtlas(NPC_plus_Handheld_Items, 0, 0, 45, 340, 1, 4, -0.5f, -0.05f);static {villager.addInfo(villager_HandPos);}
//	public static final TexAtlas zombie  = new TexAtlas("res/creatures/Zombie.png", 4, 2, -0.5f, -0.05f);
//	public static final TexAtlas sloth  = new TexAtlas("res/creatures/Sloth.png", 5, 1, -0.5f, -0.05f);
	
//	public static final TexInfo items_handheld_gripPos = new TexInfo("res/items/ItemsHandheld.txt");
//	public static final TexAtlas coin  = new TexAtlas("res/Items/Coin.png", 1, 1, -0.5f, -0.2f);
//	public static final TexAtlas inventory = new TexAtlas("res/items/Inventory.png", 1, 2, 0, 0);
//	public static final TexAtlas inventoryDifferentOffset = new TexAtlas("res/menu/Inventory.png", 1, 2, -0.5, -0.5);
//	public static final TexAtlas items_world = new TexAtlas("res/items/ItemsWorld.png", 5, 1, -0.5f, -0.5f);
//	public static final TexAtlas items_inv = new TexAtlas("res/items/ItemsInv.png", 20, 2, -0.5f, -0.5f);
//	public static final TexAtlas items_weapons = new TexAtlas("res/items/ItemsHandheld.png", 5, 7, -0.5f, -0.5f);static {items_weapons.addInfo(items_handheld_gripPos);}
//	public static final Texture moneybag = new Texture("res/items/Moneybag.png", 0, 0);
//	public static final Texture answers = new Texture("res/menu/Answers.png", -0.5, -0.5);
//	public static final Texture speechBubbleConnector = new Texture("res/menu/Connector.png", 0, 0);
//	public static final Texture speechBubble = new Texture("res/particles/Bubble.png", 0, 0);
//	public static final TexAtlas thoughtBubble = new TexAtlas("res/particles/ThoughtBubble.png", 1, 3, -0.5, -0.5);
//	public static final TexAtlas dialogBar2 = new TexAtlas("res/menu/Bar2.png", 3, 3, 0, 0);
//	public static final TexAtlas button = new TexAtlas("res/menu/Button.png", 1, 2, -0.5, -0.5);
	
//	public static final Texture light = new Texture("res/particles/Light.png", -0.5, -0.5);
//	public static final Texture light1 = new Texture("res/particles/Light1.png", -0.5, -0.5);
//	public static final Texture light2 = new Texture("res/particles/Light2.png", -0.5, -0.5);
//	public static final Texture rainbowParticle = new Texture("res/particles/RainbowParticle.png", -0.5, -0.5);
//	public static final Texture sparkleParticle = new Texture("res/particles/Sparkle.png", -0.5, -0.5);
//	public static final Texture bloodParticle = new Texture("res/particles/Blood_drop.png", -0.5, -0.5);
//	public static final Texture smokeParticle = new Texture("res/particles/Smoke.png", -0.5, -0.5);
//	public static final Texture flameParticle = new Texture("res/particles/Flame.png", -0.5, -0.5);
//	public static final Texture sparkParticle = new Texture("res/particles/Spark.png", -0.5, -0.5);
//	public static final Texture fireParticle = new Texture("res/particles/Fire.png", -0.5, -0.5);
//	public static final Texture heartParticle = new Texture("res/particles/Heart.png", -0.5, -0.5);
//	public static final Texture rainParticle = new Texture("res/particles/Raindrop.png", -0.5, -0.5);
//	public static final Texture fogParticle = new Texture("res/particles/Fog.png", -0.5, -0.5);
//	public static final Texture christmasBallPartile = new Texture("res/particles/ChristmasBall.png",-0.5,-0.5);
//	public static final Texture snowFlakeParticle = new Texture("res/particles/SnowFlake.png",-0.5,-0.5);

//	public static final Texture grass = new Texture("res/materials/Grass.png", 0, 0);
//	public static final Texture earth = new Texture("res/materials/Earth.png", 0, 0);
//	public static final Texture clay = new Texture("res/materials/Clay.png", 0, 0);
//	public static final Texture stone = new Texture("res/materials/Stone.png", 0, 0);
//	public static final Texture stone2 = new Texture("res/materials/Stone2.png", 0, 0);
//	public static final Texture sandstone3 = new Texture("res/materials/Sandstone3.png", 0, 0);
//	public static final Texture sandstone2 = new Texture("res/materials/Sandstone2.png", 0, 0);
//	public static final Texture sand = new Texture("res/materials/Sand.png", 0, 0);
//	public static final Texture candy = new Texture("res/materials/Candy.png", 0, 0);
//	public static final Texture soil = new Texture("res/materials/Soil.png", 0, 0);
//	public static final Texture water = new Texture("res/materials/Water.png", 0, 0);
//	public static final Texture ice = new Texture("res/materials/Ice.png", 0, 0);
//	public static final Texture snow = new Texture("res/materials/Snow.png", 0, 0);
}
