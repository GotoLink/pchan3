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
			switch(data){
			case 1: ((EntityAirship)ent).isGoingUp=true;break;
			case 2: ((EntityAirship)ent).isGoingDown=true;break;			
			case 3: ((EntityAirship)ent).isFiring=true;break;	
			case 4: ((EntityAirship)ent).isGoingUp=false;break;
			case 5: ((EntityAirship)ent).isGoingDown=false;break;
			case 6: ((EntityAirship)ent).isFiring=false;break;
			}		
		}
	}
}