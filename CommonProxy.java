package pchan3;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import pchan3.steamship.ContainerAirship;
import pchan3.steamship.EntityAirship;
import pchan3.steamship.GuiAirship;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy implements IGuiHandler{

	public void registerRenderInformation() {}
	public void preloadTextures() {}
	public void displayExplodeFX(Entity entity){}
	public void displayShipExplodeFX(DamageSource source, EntityAirship entity){}
	public void displaySmoke(Entity entity){}
	public void displaySplashEffect(Entity entity,double par1){}
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return new ContainerAirship(player.inventory, (EntityAirship) player.ridingEntity);      
	}
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return new GuiAirship(player.inventory, (EntityAirship) player.ridingEntity);     
	}
	public void sendPacket(int i,Entity playerEntity) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(2);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeShort(i);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "Steamship";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		Side side = FMLCommonHandler.instance().getEffectiveSide();
        if (side == Side.SERVER) {
                EntityPlayerMP player = (EntityPlayerMP) playerEntity;
        } else if (side == Side.CLIENT) {
                EntityClientPlayerMP player = (EntityClientPlayerMP) playerEntity;
                player.sendQueue.addToSendQueue(packet);
        		}
	}
}
