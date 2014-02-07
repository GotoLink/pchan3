package assets.pchan3;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import assets.pchan3.steamship.EntityAirship;

public class PacketHandler {
	public static String CHANNEL = "Steamship";

	@SubscribeEvent
	public void onPacketData(FMLNetworkEvent.ServerCustomPacketEvent event) {
		if (event.packet.channel().equals(CHANNEL))
            if(event.packet.getTarget().isServer()) {
			    event.reply = this.handle(event.packet, ((NetHandlerPlayServer)event.handler).field_147369_b);
		    }else{
                this.handle(event.packet, getPlayer());
            }
	}

    @SideOnly(Side.CLIENT)
    private EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    private FMLProxyPacket handle(FMLProxyPacket packet, EntityPlayer player) {
		ByteBuf buf = packet.payload();
		int id = buf.readInt();
		short data = buf.readShort();
		Entity ent = player.worldObj.getEntityByID(id);
		if (ent != null && ent instanceof EntityAirship && ent.riddenByEntity instanceof EntityPlayer) {
			switch (data) {
			case 0:
				((EntityPlayer) ent.riddenByEntity).openGui(PChan3Mods.instance, PChan3Mods.GUI_ID, ent.worldObj, 0, 0, 0);
				break;
			case 1:
                ((EntityAirship) ent).isGoingDown = false;
				((EntityAirship) ent).isGoingUp = true;
				break;
			case 2:
                ((EntityAirship) ent).isGoingUp = false;
				((EntityAirship) ent).isGoingDown = true;
				break;
			case 3:
				((EntityAirship) ent).isFiring = true;
				break;
			case 4:
				((EntityAirship) ent).isGoingUp = false;
				break;
			case 5:
				((EntityAirship) ent).isGoingDown = false;
				break;
			case 6:
				((EntityAirship) ent).isFiring = false;
				break;
			}
			if (!player.worldObj.isRemote && data != 0) {
                packet.setTarget(Side.CLIENT);
                return packet;
			}
		}
        return null;
	}

	public static FMLProxyPacket getPacket(Side side,int id, int key) {
		ByteBuf payload = Unpooled.buffer();
        payload.writeInt(id);
        payload.writeShort(key);
		FMLProxyPacket packet = new FMLProxyPacket(payload, CHANNEL);
		packet.setTarget(side);
		return packet;
	}
}
