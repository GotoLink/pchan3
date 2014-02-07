package assets.pchan3.steamship;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import assets.pchan3.PChan3Mods;
import assets.pchan3.PacketHandler;
import org.lwjgl.input.Keyboard;

public class AirshipKeyHandler {
	public static final String chestKeyDesc = "openchest";
	public static final String upKeyDesc = "up";
	public static final String downKeyDesc = "down";
	public static final String fireKeyDesc = "fire";
    public static KeyBinding chest, up, down, fire;
	public Minecraft client = Minecraft.getMinecraft();

	public AirshipKeyHandler(int CHEST_KEY, int UP_KEY, int DOWN_KEY, int FIRE_KEY) {
		chest = new KeyBinding("key."+chestKeyDesc, CHEST_KEY, "key.categories.airship");
        up = new KeyBinding("key."+upKeyDesc, UP_KEY, "key.categories.airship");
        down = new KeyBinding("key."+downKeyDesc, DOWN_KEY, "key.categories.airship");
        fire = new KeyBinding("key."+fireKeyDesc, FIRE_KEY, "key.categories.airship");
        ClientRegistry.registerKeyBinding(chest);
        ClientRegistry.registerKeyBinding(up);
        ClientRegistry.registerKeyBinding(down);
        ClientRegistry.registerKeyBinding(fire);
	}

	@SubscribeEvent
	public void keyDown(InputEvent.KeyInputEvent event) {
		if (client != null && client.thePlayer != null) {
			Entity ent = client.thePlayer.ridingEntity;
			if (ent != null && ent instanceof EntityAirship) {
				if (Keyboard.getEventKey() == chest.func_151463_i()){
                    if(client.currentScreen == null) {
					    client.thePlayer.openGui(PChan3Mods.instance, PChan3Mods.GUI_ID, client.theWorld, 0, 0, 0);
					    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(Side.SERVER, ent.func_145782_y(), 0));
                    }
				} else if (Keyboard.getEventKey() == up.func_151463_i() && ((EntityAirship) ent).getFuelTime() != 0) {
                    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(Side.SERVER, ent.func_145782_y(), 1));
				} else if (Keyboard.getEventKey() == down.func_151463_i()) {
                    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(Side.SERVER, ent.func_145782_y(), 2));
				} else if (Keyboard.getEventKey() == fire.func_151463_i() && ((EntityAirship) ent).getFireCountDown() == 0) {
                    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(Side.SERVER, ent.func_145782_y(), 3));
				}
                if(!up.func_151470_d()){
                    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(Side.SERVER, ent.func_145782_y(), 4));
                }
                if(!down.func_151470_d()){
                    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(Side.SERVER, ent.func_145782_y(), 5));
                }
                if(!fire.func_151470_d()){
                    PChan3Mods.channel.sendToServer(PacketHandler.getPacket(Side.SERVER, ent.func_145782_y(), 6));
                }
			}
		}
	}
}
