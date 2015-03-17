package item;

import item.ItemType.ItemUsageType;
import render.TexFile;
import render.TexFileInfo;
import render.Texture;

public class ItemTexture extends Texture{

	public ItemUsageType[] usageType;
	public TexFileInfo[] extraInfo;
	
	public ItemTexture(TexFile file, TexFileInfo[] extraInfo, ItemUsageType[] usageType, int fps, int y, int... sequence){
		super(file, fps, y, sequence);
		this.usageType = usageType;
		this.extraInfo = extraInfo;
	}
	
	public ItemTexture(TexFile file, TexFileInfo[] extraInfo, ItemUsageType[] usageType){
		super(file);
		this.usageType = usageType;
		this.extraInfo = extraInfo;
	}
	
	public ItemTexture(TexFile file, TexFileInfo[] extraInfo, ItemUsageType[] usageType, int x, int y){
		super(file, x, y);
		this.usageType = usageType;
		this.extraInfo = extraInfo;
	}
}
