package pchan3.steamship;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import pchan3.PChan3mods;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class AirshipKeyHandler extends KeyHandler {
	
	static GuiAirship gui;
    
    public AirshipKeyHandler(int CHEST_KEY, int UP_KEY, int DOWN_KEY, int FIRE_KEY) 
    {                
    	super(new KeyBinding[]{new KeyBinding("OpenAirshipChest",CHEST_KEY),new KeyBinding("AirshipUp",UP_KEY),new KeyBinding("AirshipDown",DOWN_KEY),new KeyBinding("AirshipFire",FIRE_KEY)}, new boolean[]{false,false,false,false});       
    }
    
    @Override
    public void keyDown(EnumSet<TickType> es, KeyBinding kb, boolean bln, boolean bln1) {
    	Minecraft client = Minecraft.getMinecraft();
		if (client != null && client.thePlayer != null)
		{
    	Entity ent=client.thePlayer.ridingEntity;
    	if (ent!=null && ent instanceof EntityAirship){
    		PChan3mods.proxy.sendPacket(0,ent.riddenByEntity);
		if (kb.keyDescription=="OpenAirshipChest" && gui==null )
		{
	    ((EntityPlayer) ent.riddenByEntity).openGui(PChan3mods.instance, PChan3mods.instance.GUI_ID, ent.worldObj, ent.serverPosX, ent.serverPosY, ent.serverPosZ);
		}
		else if (kb.keyDescription=="AirshipUp" && ((EntityAirship) ent).getFuelTime()!=0)
		{	
			PChan3mods.proxy.sendPacket(2,ent.riddenByEntity);	
		}
		else if (kb.keyDescription=="AirshipDown")
		{ 
			PChan3mods.proxy.sendPacket(1,ent.riddenByEntity);
		}
		else if (kb.keyDescription=="AirshipFire" && ((EntityAirship) ent).getFireCountDown()==0)
		{			
			PChan3mods.proxy.sendPacket(3,ent.riddenByEntity);
		}	
		}	
		}
    }
    @Override
    public void keyUp(EnumSet<TickType> es, KeyBinding kb, boolean bln) {
	
    }

    @Override
    public EnumSet<TickType> ticks() {
	return EnumSet.of(TickType.CLIENT);
    }

    @Override
    public String getLabel() {
	return "Airship KeyHandler";
    }
    
}
