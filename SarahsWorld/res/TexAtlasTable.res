
//togethers
TexFile	NPC_plus_Handheld_Items	res/creatures/NPC_and_handheld_Items.png

//Static	things			(offsetY	is	location	of	point	on	floor)
TexAtlas	cloud			res/objects/Cloud.png			d	d	d	d	1	1	-0.5f	0
TexAtlas	tree			res/objects/Tree.png			d	d	d	d	1	3	-0.5f	-0.26
TexAtlas	tree_fir		res/objects/Fir.png				d	d	d	d	1	3	-0.5	-0.1
TexAtlas	tree_firSnow	res/objects/Fir_Snow.png		d	d	d	d	1	3	-0.5	-0.2
TexAtlas	tree_grave		res/objects/GraveTree.png		d	d	d	d	1	2	-0.5f	-0.2
TexAtlas	tree_jungle		res/objects/JungleTree.png		d	d	d	d	1	4	-0.5f	-0.12
TexAtlas	plant_jungle	res/objects/Fern.png			d	d	d	d	1	5	-0.5f	0
TexAtlas	flower_jungle	res/objects/JungleFlower.png	d	d	d	d	1	5	-0.5f	0
TexAtlas	bush_jungle		res/objects/JungleBush.png		d	d	d	d	1	1	-0.5f	0
TexAtlas	plant_giant		res/objects/GiantPlant.png		d	d	d	d	1	4	-0.5f	0
TexAtlas	grass_giant		res/objects/GiantGras.png		d	d	d	d	1	3	-0.5f	0
TexAtlas	tree_palm		res/objects/PalmTree.png		d	d	d	d	1	3	-0.5f	0
TexAtlas	tree_candy		res/objects/CandyTree.png		d	d	d	d	1	1	-0.5f	-0.12
TexAtlas	bamboo			res/objects/Bamboo.png			d	d	d	d	1	4	-0.5f	-0.07
TexAtlas	bush_normal		res/objects/Bush.png			d	d	d	d	1	2	-0.5f	0
TexAtlas	bush_candy		res/objects/CandyBush.png		d	d	d	d	1	2	-0.5f	0
TexAtlas	cactus			res/objects/Cactus.png			d	d	d	d	1	3	-0.5f	0
TexAtlas	grasstuft		res/objects/Grass_tuft.png		d	d	d	d	4	1	-0.5f	-0.05
TexAtlas	flower_normal	res/objects/Flower.png			d	d	d	d	1	3	-0.5f	0f
TexAtlas	pyramide		res/objects/Pyramide.png		d	d	d	d	1	4	-0.5f	0f
TexAtlas	house			res/objects/House.png			d	d	d	d	1	6	-0.5f	0f
TexAtlas	townobject		res/objects/TownObject.png		d	d	d	d	1	5	-0.5f	0
TexAtlas	flower_candy	res/objects/Candy.png			d	d	d	d	1	6	-0.5f	0
TexAtlas 	cake			res/objects/BirthdayCake.png	d	d	d	d	1	1	-0.5f	-0.2f
TexAtlas	crack			res/objects/Crack.png			d	d	d	d	1	4	-0.5f	0
TexAtlas	fossil			res/objects/Fossil.png			d	d	d	d	1	3	-0.5f	0
TexAtlas	grave			res/objects/Grave.png			d	d	d	d	1	7	-0.5f	-0.1
TexAtlas	rainbow			res/objects/Rainbow.png			d	d	d	d	1	1	-0.5f	0f
//	TexFile	flower_light	res/Light_dimmed.png
	
//Creatures
TexInfo	sloth_onTreePos		res/objects/Sloth_JungleTree.txt
TexInfo	sarah_HandPos		res/creatures/Sarah.txt
TexInfo	sarah_HandPos_onCow	res/creatures/Sarah_riding_cow.txt
TexInfo	sarah_HandPos_dive	res/creatures/Sarah_dive_hand.txt
TexInfo	sarah_HandPos_swim	res/creatures/Sarah_dive_hand.txt
TexInfo	sarah_HeadPos		res/creatures/Sarah_HORN.txt
TexInfo	sarah_HeadPos_onCow	res/creatures/Sarah_riding_cow_horn.txt
TexInfo	sarah_HeadPos_dive	res/creatures/Sarah_dive_hand.txt
TexInfo	sarah_HeadPos_swim	res/creatures/Sarah_dive_hand.txt
TexInfo	villager_HandPos	res/creatures/NPC.txt
TexInfo unicorn_mouth		res/creatures/UnicornMouth.txt

TexFile		sarahFile		res/creatures/Sarah.png
TexAtlas	sarah			@sarahFile							0	0	550	750	11	10	-0.5	-0.1	@sarah_HandPos	@sarah_HeadPos
TexAtlas	sarah_dive		@sarahFile							50	0	400	75	5	1	-0.5	-0.1	@sarah_HandPos_dive	@sarah_HeadPos_dive
TexAtlas	sarah_swim		@sarahFile							150	675	350	56	5	1	-0.5	-0.5	@sarah_HandPos_swim	@sarah_HeadPos_swim
TexAtlas	sarah_onCow		@sarahFile							0	751	700	210	7	2	-0.5	-0.1	@sarah_HandPos_onCow	@sarah_HeadPos_onCow
TexAtlas	sarah_death		res/creatures/Sarah_death.png		d	d	d	d	14	1	-0.5f	-0.5f
TexAtlas	snail			res/creatures/Snail.png				d	d	d	d	7	3	-0.5f	-0.1f
TexAtlas	butterfly		res/creatures/Butterfly.png			d	d	d	d	5	2	-0.5f	-0.5f
TexAtlas	midge			res/creatures/Midge.png				d	d	d	d	1	1	-0.5f	-0.5f
TexAtlas	heart			res/creatures/Heart.png				d	d	d	d	4	2	-0.5f	-0.2f
TexAtlas	rabbit			res/creatures/Rabbit.png			d	d	d	d	5	6	-0.5f	-0.2f
TexAtlas	bird			res/creatures/Bird.png				d	d	d	d	5	4	-0.5f	-0.2f
TexAtlas	panda			res/creatures/Panda.png				d	d	d	d	6	2	-0.5f	-0.1f
TexAtlas	scorpion		res/creatures/Scorpion.png			d	d	d	d	7	2	-0.5f	-0.1f
TexAtlas	cow				res/creatures/Cow.png				d	d	d	d	7	1	-0.5f	-0.1f
TexAtlas	unicorn			res/creatures/Unicorn.png			d	d	d	d	6	3	-0.5f	-0.1f	@unicorn_mouth
TexAtlas	unicorn_hair	res/creatures/Unicorn_hair.png		d	d	d	d	6	3	-0.5f	-0.1f
TexAtlas	trex			res/creatures/Trex.png				d	d	d	d	9	4	-0.5f	-0.05f
TexAtlas	cat_giant		res/creatures/GiantCat.png			d	d	d	d	5	2	-0.5f	-0.05f
TexAtlas	villager		@NPC_plus_Handheld_Items			0	0	45	340	1	4	-0.5f	-0.05f	@villager_HandPos
TexAtlas	zombie			res/creatures/Zombie.png			d	d	d	d	4	2	-0.5f	-0.05f
TexAtlas	sloth			res/creatures/Sloth.png				d	d	d	d	5	1	-0.5f	-0.05f
TexAtlas 	meteor   		res/creatures/Meteor.png 			d	d	d	d	4	1	-0.23f	-0.2f

TexInfo		items_handheld_gripPos		res/items/ItemsHandheld.txt
TexAtlas	coin						res/Items/Coin.png				d	d	d	d	1	1	-0.5f	-0.2f
TexAtlas	inventory					res/items/Inventory.png			d	d	d	d	1	2	0	0
TexAtlas	inventoryDifferentOffset	res/menu/Inventory.png			d	d	d	d	1	2	-0.5	-0.5
TexAtlas	items_world					res/items/ItemsWorld.png		d	d	d	d	5	1	-0.5f	-0.5f
TexAtlas	items_inv					res/items/ItemsInv.png			d	d	d	d	20	2	-0.5f	-0.5f
TexAtlas	items_weapons				res/items/ItemsHandheld.png		d	d	d	d	5	7	-0.5f	-0.5f	@items_handheld_gripPos
Texture		moneybag					res/items/Moneybag.png			0	0
Texture		answers						res/menu/Answers.png			-0.5	-0.5
Texture		speechBubbleConnector		res/menu/Connector.png			0	0
Texture		speechBubble				res/particles/Bubble.png		0	0
TexAtlas	thoughtBubble				res/particles/ThoughtBubble.png	d	d	d	d	1	3	-0.5	-0.5
TexAtlas	dialogBar2					res/menu/Bar2.png				d	d	d	d	3	3	0	0
TexAtlas	button						res/menu/Button.png				d	d	d	d	1	2	-0.5	-0.5

Texture	light			res/particles/Light.png				-0.5	-0.5
Texture	light1			res/particles/Light1.png			-0.5	-0.5
Texture	light2			res/particles/Light2.png			-0.5	-0.5
Texture	rainbowParticle	res/particles/RainbowParticle.png	-0.5	-0.5
Texture	sparkleParticle	res/particles/Sparkle.png			-0.5	-0.5
Texture	bloodParticle	res/particles/Blood_drop.png		-0.5	-0.5
Texture	smokeParticle	res/particles/Smoke.png				-0.5	-0.5
Texture	flameParticle	res/particles/Flame.png				-0.5	-0.5
Texture	sparkParticle	res/particles/Spark.png				-0.5	-0.5
Texture	fireParticle	res/particles/Fire.png				-0.5	-0.5
Texture	heartParticle	res/particles/Heart.png				-0.5	-0.5
Texture	rainParticle	res/particles/Raindrop.png			-0.5	-0.5
Texture	fogParticle		res/particles/Fog.png				-0.5	-0.5
Texture christmasBallPartile res/particles/ChristmasBall.png -0.5	-0.5
Texture snowFlakeParticle	 res/particles/SnowFlake.png	-0.5	-0.5
Texture fireBall		 res/particles/FireBallColor.png	-0.5	-0.5

Texture	grass		res/materials/Grass.png			0	0
Texture	earth		res/materials/Earth.png			0	0
Texture	clay		res/materials/Clay.png			0	0
Texture	stone		res/materials/Stone.png			0	0
Texture	stone2		res/materials/Stone2.png		0	0
Texture	sandstone3	res/materials/Sandstone3.png	0	0
Texture	sandstone2	res/materials/Sandstone2.png	0	0
Texture	sand		res/materials/Sand.png			0	0
Texture	candy		res/materials/Candy.png			0	0
Texture	soil		res/materials/Soil.png			0	0
Texture	water		res/materials/Water.png			0	0
Texture	snow		res/materials/Snow.png			0	0