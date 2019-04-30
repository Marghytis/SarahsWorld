package item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import effects.particleEffects.BerryEat;
import effects.particleEffects.FireEffect;
import extra.things.Thing;
import extra.things.ThingType;
import main.Main;
import main.Res;
import render.Animator;
import render.Render;
import render.TexFile;
import render.Texture;
import util.Color;
import util.math.Vec;
import world.World;
import world.data.ColumnListElement;

public class ItemType {

	private static List<ItemType> tempList = new ArrayList<>();
	private static int index;
	private static ItemTypeBuilder builder = new ItemTypeBuilder("res/ItemTypes.txt");

	public static final ItemType SWORD			= new ItemType(builder.readItemType("SWORD"));
	public static final ItemType AXE			= new ItemType(builder.readItemType("AXE"));
	public static final ItemType STICK			= new ItemType(builder.readItemType("STICK"));
	public static final ItemType CANDY_CANE		= new ItemType(builder.readItemType("CANDY_CANE"));
	public static final ItemType SHOVEL			= new ItemType(builder.readItemType("SHOVEL"));
	public static final ItemType UNICORN_HORN	= new ItemType(builder.readItemType("UNICORN_HORN")) {
		@Override
		public boolean useAt(Thing src, Vec pos) {
			ColumnListElement c = Main.world.landscapeWindow.at(pos.x);
			if(c != null) {
				Vec loc = new Vec();
				ThingType.EFFECT.defaultSpawner.spawn(c.column().getRandomTopLocation(World.rand, loc), loc, new FireEffect(loc));
				return true;
			} else {
				return false;
			}
		}
	};
	
//	horn = new MagicWeapon	(Res.items_world.tex(4, 0),	Res.items_hand.tex(5, 0),		Res.getAtlas("items_inv").tex(5, 0), new Rect(-25, -2, 50, 50), new Rect(-55, -19, 80, 40), 180,					"Horn",			1000,			100, 		WeaponType.SPELL,	ItemUsageType.FIST, BodyPos.HAND, 3,	4,	0.3,false);TODO Add particle effects
	public static final ItemType SNAILS_EYE 	= new ItemType(builder.readItemType("SNAILS_EYE"));
	public static final ItemType SNAIL_SHELL 	= new ItemType(builder.readItemType("SNAIL_SHELL"));
	public static final ItemType RABBITS_FOOT 	= new ItemType(builder.readItemType("RABBITS_FOOT"));
	public static final ItemType TREX_TOOTH 	= new ItemType(builder.readItemType("TREX_TOOTH"));
	public static final ItemType COW_LEG 		= new ItemType(builder.readItemType("COW_LEG"));
	public static final ItemType COWHIDE 		= new ItemType(builder.readItemType("COWHIDE"));
	public static final ItemType WITCH_HAT 		= new ItemType(builder.readItemType("WITCH_HAT"));
	public static final ItemType SCORPION_STING = new ItemType(builder.readItemType("SCORPION_STING"));
	public static final ItemType SCORPION_CLAW 	= new ItemType(builder.readItemType("SCORPION_CLAW"));
	public static final ItemType RAINBOW_HAIR 	= new ItemType(builder.readItemType("RAINBOW_HAIR"));
	public static final ItemType ZOMBIE_EYE 	= new ItemType(builder.readItemType("ZOMBIE_EYE"));
	public static final ItemType ZOMBIE_BRAIN 	= new ItemType(builder.readItemType("ZOMBIE_BRAIN"));
	public static final ItemType ZOMBIE_FLESH 	= new ItemType(builder.readItemType("ZOMBIE_FLESH"));
	public static final ItemType BIRTHDAY_CAKE 	= new ItemType(builder.readItemType("BIRTHDAY_CAKE")) {
		public boolean useAt(Thing src, Vec pos){
			pos = pos.copy();
			src.newLink.getRandomTopLocation(World.rand, pos);
			ThingType.CAKE.defaultSpawner.spawn(src.newLink, pos);
			Main.world.window.addEffect(new BerryEat(new Vec(Main.world.avatar.pos.x + (Main.world.avatar.aniPlug.getAnimator().tex.w/2), Main.world.avatar.pos.y + (Main.world.avatar.aniPlug.getAnimator().tex.h/2))));
			return true;
		}
	};
	public static final ItemType BERRY 			= new ItemType(builder.readItemType("BERRY")) {
		public boolean useAt(Thing src, Vec pos){
			Main.world.window.addEffect(new BerryEat(new Vec(Main.world.avatar.pos.x + (Main.world.avatar.aniPlug.getAnimator().tex.w/2), Main.world.avatar.pos.y + (Main.world.avatar.aniPlug.getAnimator().tex.h/2))));
			if(src.type.magic != null) {
				src.magic.mana += 2;
				if(src.magic.mana > src.type.magic.maxMana){
					src.magic.mana = src.type.magic.maxMana;
				}
				return true;
			}
			return true;
		}
	};
	
	//Item types below this line won't appear in traders inventories
	public static final ItemType MOUTH 			= new ItemType(builder.readItemType("MOUTH"));
	public static final ItemType NOTHING 		= new ItemType(builder.readItemType("NOTHING"));
	public static final ItemType COIN = new ItemType(builder.readItemType("COIN"));

	public static ItemType[] values = tempList.toArray(new ItemType[tempList.size()]);
	public static TexFile handheldTex = Res.getAtlas("items_weapons").file;
	
	public final int ordinal;
	public String name;
	
	//rendering
	public Texture texWorld;//Texture shown if the item lies around
	public Texture texHand;//Texture shown, if the item is held
	public Texture texInv;//Texture of the item in the inventory
	public BodyPos bodyPos;//A TexFileInfo giving the location of the item in each frame, if its held
	public int[] boxWorld;//The box the item has lying around
	public int defaultRotationHand;//The rotation the item already has in the texture in degrees. 0 would be horizontal to the right
	
	//usable
	public boolean oneWay;
	public boolean needsTarget;//Whether the item needs a thing target to use it or not
	public int coolDownTime, coolDownTimeUsage;//Length of the cool down after usage
	public ItemUsageType useType;//determines the animation played on usage (right click)
	
	//weapon
	public WeaponType weaponType;//determines the animation that is used to attack with this item (left click)
	public int attackStrength;//The default attack strength of the item
	public double crit;//The attack strength when landing a critical hit
	public double critProb;//The default probability of landing a critical hit
	
	//misc
	public int value;//Value when trading
	public String nameInv;//The name the item shows in the inventory
	
	
	ItemType(ItemTypeBuilder builder){
		this.texWorld = builder.texWorld;
		this.texHand = builder.texHand;
		this.texInv = builder.texInventory;
		this.boxWorld = builder.boxWorld;
		if(texHand.info != null){
			this.texHand.pixelCoords[0] = -builder.texHand.info[0][0];
			this.texHand.pixelCoords[1] = -builder.texHand.info[0][1];
			this.texHand.pixelCoords[2] = this.texHand.pixelCoords[0] + this.texHand.w;
			this.texHand.pixelCoords[3] = this.texHand.pixelCoords[1] + this.texHand.h;
			this.defaultRotationHand = builder.texHand.info[0][2]+180;
		}
		this.name = builder.name;
		this.nameInv = builder.nameInv;
		this.value = builder.coinValue;
		this.coolDownTime = builder.coolDownTime;
		this.coolDownTimeUsage = builder.coolDownTimeUsage;
		this.weaponType = builder.weaponType;
		this.useType = builder.useType;
		this.bodyPos = builder.bodyPos;
		this.attackStrength = builder.attackStrength;
		this.crit = builder.crit;
		this.critProb = builder.critProb;
		this.needsTarget = builder.needsTarget;
		this.oneWay = builder.oneWay;
		
		this.ordinal = index++;
		tempList.add(this);
	}
	
	public boolean use(Thing src, Vec pos, Thing[] dest){
		if(needsTarget){
			//prefer to pick up items
			for(Thing t : dest){
				if(t.type == ThingType.ITEM){
					if(useOn(src, pos, t)){
						return true;
					}
				}
			}
			//and only then use on other things
			for(Thing t : dest){
				if(useOn(src, pos, t)){
					return true;
				}
			}
		} else {
			return useAt(src, pos);
		}
		return false;
	}

//	public boolean useOn(Thing src, Vec pos, Thing dest){return false;}
	public boolean useAt(Thing src, Vec pos){return false;}
	public boolean useOn(Thing src, Vec pos, Thing dest){
		boolean success = false;
		if(dest.itemPlug != null && dest.itemPlug.containsAnyItems() && src.invPlug != null && src.pos.minus(dest.pos).lengthSquare() < 100000){
			ItemType i = dest.itemPlug.removeRandomFruit(World.rand);
			if(i != null) src.invPlug.addItem( i, 1);
			success = true;
		} else {
			if(dest.ridePlug != null && dest.ridePlug.canRide(dest.type)){
				src.ridePlug.mount(dest);
				success = true;
			} else if(dest.interaction != null){
				dest.interaction.onInteraction(src, pos);
			}
		}
		return success;
	}
	/**
	 * Translates and rotates the model matrix. Matrix origin should be at bottom left corner of the things texture.
	 * Then the hand texture is rendered via the provided animator.
	 * @param t
	 * @param ani The animator which is used to render the item. Must have the correct texture set already.
	 */
	public void renderHand(Thing t, Animator ani){
		
		if(texHand == Texture.emptyTexture) return;
		Texture thingTex = t.aniPlug.getAnimator().tex;
		int[] info = thingTex.info[bodyPos.ordinal()];
		
		int handX = 0;
		int handY = 0;
		int handAngle = 0;
		
		if(info != null){
			if(info[0] == -200) return;
			handX = info[0];
			handY = info[1];
			handAngle = defaultRotationHand + info[2] + 90;
		}
		
		if(t.aniPlug.getOrientation()){
			handX = thingTex.w - handX;
			handAngle = -handAngle;
		}
		ani.quad.update(new Vec(handX + t.pos.x + thingTex.pixelCoords[0] + Render.offsetX, handY + t.pos.y + thingTex.pixelCoords[1] + Render.offsetY), (0 + handAngle)/360.0, 1, t.aniPlug.getOrientation());
	
		ani.quad.render(new Vec(), -1, new Vec(Render.scaleX, Render.scaleY), Color.WHITE);
	}
	
	public static ItemType getRandomItem(Random random){
		return values[random.nextInt(values.length-1)];
	}
	
	public static ItemType valueOf(String name){
		for(ItemType type : values){
			if(type.name.equals(name)){
				return type;
			}
		}
		return null;
	}

	public static enum ItemUsageType {FIST, EAT}
	public static enum WeaponType {
		PUNCH, KICK, STRIKE, SPELL, BITE;
	}
	public enum BodyPos {HAND, HEAD}
}
