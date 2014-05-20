package assets.pchan3.steamship;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import assets.pchan3.PChan3Mods;
import assets.pchan3.PacketHandler;

public class AirshipKeyHandler {
	public static final String chestKeyDesc = "openchest";
	public static final String upKeyDesc = "fly.up";
	public static final String downKeyDesc = "fly.down";
	public static final String fireKeyDesc = "fire";
    private static final String CAT = "key.categories.airship";
    public static KeyBinding chest, up, down, fire;
	public final Minecraft client = Minecraft.getMinecraft();

	public AirshipKeyHandler(int CHEST_KEY, int UP_KEY, int DOWN_KEY, int FIRE_KEY) {
        for(KeyBinding key:client.gameSettings.keyBindings){
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
		if (event.phase == TickEvent.Phase.START && client != null && client.thePlayer != null) {
			Entity ent = client.thePlayer.ridingEntity;
			if (ent != null && ent instanceof EntityAirship) {
				if (chest.getIsKeyPressed()){
                    if(client.currentScreen == null) {
					    client.thePlayer.openGui(PChan3Mods.instance, PChan3Mods.GUI_ID, client.theWorld, 0, 0, 0);
					    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(Side.SERVER, ent.getEntityId(), 0));
                    }
				} else if (up.getIsKeyPressed()) {
                    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(Side.SERVER, ent.getEntityId(), 1));
				} else if (down.getIsKeyPressed()) {
                    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(Side.SERVER, ent.getEntityId(), 2));
				} else if (fire.getIsKeyPressed()) {
                    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(Side.SERVER, ent.getEntityId(), 3));
				} else{
                    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(Side.SERVER, ent.getEntityId(), 4));
                }
			}
		}
	}
}
