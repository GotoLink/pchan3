package assets.pchan3;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import assets.pchan3.steamship.EntityAirship;

public class PacketHandler {
	public final static String CHANNEL = "Steamship";

	@SubscribeEvent
	public void onPacketData(FMLNetworkEvent.ServerCustomPacketEvent event) {
		if (event.packet.channel().equals(CHANNEL))
            if(event.packet.getTarget().isServer()) {
			    event.reply = this.handle(event.packet, ((NetHandlerPlayServer)event.handler).playerEntity);
		    }
	}

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event){
        if (event.packet.channel().equals(CHANNEL))
            this.handle(event.packet, PChan3Mods.proxy.getPlayer());
    }

    private FMLProxyPacket handle(FMLProxyPacket packet, EntityPlayer player) {
		ByteBuf buf = packet.payload();
		int id = buf.readInt();
		short data = buf.readShort();
		Entity ent = player.worldObj.getEntityByID(id);
		if (ent != null && ent instanceof EntityAirship && ent.riddenByEntity instanceof EntityPlayer) {
            if(data==0){
                ((EntityPlayer) ent.riddenByEntity).openGui(PChan3Mods.instance, PChan3Mods.GUI_ID, ent.worldObj, 0, 0, 0);
                return null;
            }else{
                ((EntityAirship) ent).isGoingUp = false;
                ((EntityAirship) ent).isGoingDown = false;
                ((EntityAirship) ent).isFiring = false;
                switch (data) {
                    case 1:
                        if(((EntityAirship) ent).getFuelTime() > 0){
                            ((EntityAirship) ent).isGoingUp = true;
                        }
                        break;
                    case 2:
                        ((EntityAirship) ent).isGoingDown = true;
                        break;
                    case 3:
                        ((EntityAirship) ent).isFiring = ((EntityAirship) ent).getFireCountDown() == 0;
                        break;
                }
                if (!player.worldObj.isRemote) {
                    packet.setTarget(Side.CLIENT);
                    return packet;
                }
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
