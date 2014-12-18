package assets.pchan3.steamship;

import assets.pchan3.PChan3Mods;
import assets.pchan3.PacketHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;

public final class AirshipKeyHandler {
	public static final String chestKeyDesc = "openchest";
	public static final String upKeyDesc = "fly.up";
	public static final String downKeyDesc = "fly.down";
	public static final String fireKeyDesc = "fire";
    private static final String CAT = "key.categories.airship";
    public KeyBinding chest, up, down, fire;

	public AirshipKeyHandler(int CHEST_KEY, int UP_KEY, int DOWN_KEY, int FIRE_KEY) {
        for(KeyBinding key:Minecraft.getMinecraft().gameSettings.keyBindings){
            if(up==null && key.getKeyDescription().contains(upKeyDesc)){
                up = key;
            }
            if(down==null && key.getKeyDescription().contains(downKeyDesc)){
                down = key;
            }
        }
        if(up==null){
            up = new KeyBinding("key."+upKeyDesc, UP_KEY, "key.categories.movement");
            ClientRegistry.registerKeyBinding(up);
        }
        if(down==null){
            down = new KeyBinding("key."+downKeyDesc, DOWN_KEY, "key.categories.movement");
            ClientRegistry.registerKeyBinding(down);
        }
        chest = new KeyBinding("key."+chestKeyDesc, CHEST_KEY, CAT);
        fire = new KeyBinding("key."+fireKeyDesc, FIRE_KEY, CAT);
        ClientRegistry.registerKeyBinding(chest);
        ClientRegistry.registerKeyBinding(fire);
	}

	@SubscribeEvent
	public void keyDown(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START && Minecraft.getMinecraft().thePlayer != null) {
			Entity ent = Minecraft.getMinecraft().thePlayer.ridingEntity;
			if (ent instanceof EntityAirship) {
				if (chest.getIsKeyPressed()){
                    if(Minecraft.getMinecraft().currentScreen == null) {
                        Minecraft.getMinecraft().thePlayer.openGui(PChan3Mods.instance, PChan3Mods.GUI_ID, ent.worldObj, 0, 0, 0);
					    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(ent.getEntityId(), 0));
                    }
				} else if (up.getIsKeyPressed()) {
                    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(ent.getEntityId(), 1));
				} else if (down.getIsKeyPressed()) {
                    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(ent.getEntityId(), 2));
				} else if (fire.getIsKeyPressed()) {
                    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(ent.getEntityId(), 3));
				} else{
                    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(ent.getEntityId(), 4));
                }
			}
		}
	}
}
