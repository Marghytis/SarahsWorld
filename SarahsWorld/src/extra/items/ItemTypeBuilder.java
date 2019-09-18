package extra.items;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import extra.Res;
import extra.items.ItemType.BodyPos;
import extra.items.ItemType.ItemUsageType;
import extra.items.ItemType.WeaponType;
import render.TexAtlas;
import render.Texture;
import util.math.Rect;

public class ItemTypeBuilder {
	
	static int[] standardBoxWorld = {-25, -2, 50, 50};
	static TexAtlas itemsInv = Res.getAtlas("items_inv");
	static TexAtlas itemsWeapons = Res.getAtlas("items_weapons");
	
	
	public String name;
	
	//obsolete (not changed)
	public ItemUsageType useType = ItemUsageType.FIST;
	
	//rendering:
	boolean texturesBuilt;
	public Texture texWorld;
	public Texture texHand;
	public int defaultRotationHand;
	public Texture texInventory;
	public int[] boxWorld;
	public BodyPos bodyPos;
	public Rect boxHand;
	
	//usable
	boolean usableBuilt;
	public boolean oneWay;
	public int coolDownTime, coolDownTimeUsage;
	public boolean needsTarget;
	
	//weapon
	boolean weaponBuilt;
	public WeaponType weaponType;
	public int attackStrength;
	public double crit;
	public double critProb;
	
	//misc
	boolean miscBuilt;
	public int coinValue;
	public String nameInv;
	
	BufferedReader reader;
	
	public ItemTypeBuilder(String filePath) {
		try {
			reader = new BufferedReader(new FileReader(filePath));
			reader.mark(1000);
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public ItemTypeBuilder newItemType(String name) {
		this.name = name;
		
		resetRenderable();
		resetUsable();
		resetWeapon();
		resetMisc();
		
		return this;
	}
	
	public ItemTypeBuilder readItemType(String name) {
		newItemType(name);
		try {
			reader.reset();
			String line = reader.readLine();

			while (line != null) {
				
				if(line.startsWith(name)) {
					processLine(line);
					break;
				}
				
				line = reader.readLine();
			}
			
			return this;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void processLine(String line) {
		String[] words = line.split("\\s+");
		int i = 1;//word 0 is the name

		setMisc(
				words[i++].replace('_', ' '),//nameInv
				Integer.parseInt(words[i++])//coinValue
				);
		setRenderable(
				words[i++].equals("e") ? Texture.emptyTexture : (words[i-1].equals("d") ? itemsInv : 	Res.getAtlas(words[i-1])).tex(Integer.parseInt(words[i++]), Integer.parseInt(words[i++])),//texWorld
				words[i++].equals("e") ? Texture.emptyTexture : (words[i-1].equals("d") ? itemsWeapons :Res.getAtlas(words[i-1])).tex(Integer.parseInt(words[i++]), Integer.parseInt(words[i++])),//texHand
				words[i++].equals("e") ? Texture.emptyTexture : (words[i-1].equals("d") ? itemsInv : 	Res.getAtlas(words[i-1])).tex(Integer.parseInt(words[i++]), Integer.parseInt(words[i++])),//texInventory
				standardBoxWorld,			//boxWorld
				BodyPos.valueOf(words[i++])	//bodyPos
				);
		setUsable(
				Boolean.parseBoolean(words[i++]),//oneWay
				Boolean.parseBoolean(words[i++]),//needsTarget
				Integer.parseInt(words[i++]),	 //coolDownTime
				Integer.parseInt(words[i++])	 //coolDownTimeUsage
				);
		setWeapon(
				WeaponType.valueOf(words[i++]),//weaponType
				Integer.parseInt(words[i++]),  //strength
				Double.parseDouble(words[i++]),//crit
				Double.parseDouble(words[i++]) //critProb
				);
		if(i < words.length) {
			this.boxWorld = Res.getAtlas(words[i++]).pixelCoords; 
		}
	}
	
	public ItemTypeBuilder setUsable(boolean oneWay, boolean needsTarget, int coolDownTime, int coolDownTimeUsage) {
		this.oneWay = oneWay;
		this.needsTarget = needsTarget;
		this.coolDownTime = coolDownTime;
		this.coolDownTimeUsage = coolDownTimeUsage;
		
		usableBuilt = true;
		return this;
	}
	
	public ItemType build() {
		return new ItemType(this);
	}

	private void resetRenderable() {
		this.texturesBuilt = false;
		this.texWorld = null;
		this.texHand = Texture.emptyTexture;
		this.texInventory = Texture.emptyTexture;
		this.boxWorld = standardBoxWorld;
		this.defaultRotationHand = 0;
		this.bodyPos = BodyPos.HAND;
		
		texturesBuilt = false;
	}
	
	public ItemTypeBuilder setRenderable(Texture texWorld, Texture texHand, Texture texInventory, int[] boxWorld, BodyPos bodyPos) {
		this.texWorld = texWorld;
		this.texHand = texHand;
		this.texInventory = texInventory;
		this.boxWorld = boxWorld;
		this.bodyPos = bodyPos;
		if(texHand.info != null){
			this.texHand.pixelCoords[0] = -texHand.info[0][0];
			this.texHand.pixelCoords[1] = -texHand.info[0][1];
			this.texHand.pixelCoords[2] = this.texHand.pixelCoords[0] + this.texHand.w;
			this.texHand.pixelCoords[3] = this.texHand.pixelCoords[1] + this.texHand.h;
			this.defaultRotationHand = texHand.info[0][2]+180;
		}
		texturesBuilt = true;
		return this;
	}
	
	public ItemTypeBuilder setRenderable(Texture texWorld, Texture texHand, Texture texInventory) {
		return setRenderable(texWorld, texHand, texInventory, standardBoxWorld, BodyPos.HAND);
	}
	
	public void resetUsable() {
		this.oneWay = false;
		this.needsTarget = false;
		this.coolDownTime = 0;
		
		usableBuilt = false;
	}
	
	public void resetWeapon() {
		this.weaponType = WeaponType.PUNCH;
		this.attackStrength = 0;
		this.crit = 0;
		this.critProb = 0;
		
		weaponBuilt = false;
	}

	public ItemTypeBuilder setWeapon(WeaponType weaponType, int attackStrength, double crit, double critProb) {
		this.weaponType = weaponType;
		this.attackStrength = attackStrength;
		this.crit = crit;
		this.critProb = critProb;
		
		weaponBuilt = true;
		return this;
	}
	
	public ItemTypeBuilder setMisc(String nameInv, int coinValue) {
		this.nameInv = nameInv;
		this.coinValue = coinValue;
		
		miscBuilt = true;
		return this;
	}
	
	void resetMisc() {
		this.nameInv = "";
		this.coinValue = 0;
		
		miscBuilt = false;
	}
	
	public void finalize() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
