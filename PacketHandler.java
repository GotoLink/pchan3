package mods.pchan3;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import mods.pchan3.steamship.EntityAirship;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

/**
 *
 * @author pchan3
 */
public class PacketHandler implements IPacketHandler
{
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload payload, Player player)
    {
    	if (payload.channel.equals("Steamship")) {
            this.handle(payload,player);
    }
    }

	private void handle(Packet250CustomPayload payload, Player player) {
		DataInputStream inStream = new DataInputStream(new ByteArrayInputStream(payload.data));
		short data;
		try {
           data = inStream.readShort();
		} catch (IOException e) {
            e.printStackTrace();
            return;
		}
		Entity ent = ((EntityPlayer)player).ridingEntity;
		if (ent!=null && ent instanceof EntityAirship){
				((EntityAirship)ent).isGoingUp=false;
				((EntityAirship)ent).isGoingDown=false;
				((EntityAirship)ent).isFiring=false;
			if (data==2)
				((EntityAirship)ent).isGoingUp=true;
			else if (data==1)
				((EntityAirship)ent).isGoingDown=true;	
			else if (data==3)
				((EntityAirship)ent).isFiring=true;
		}
	}
}
