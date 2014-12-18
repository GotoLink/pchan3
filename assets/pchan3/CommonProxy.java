package assets.pchan3;

import assets.pchan3.steamship.ContainerAirship;
import assets.pchan3.steamship.EntityAirship;
import assets.pchan3.steamship.GuiAirship;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler {
	public void registerRenderInformation() {
	}

	public void displayExplodeFX(Entity entity) {
	}

	public void displayShipExplodeFX(DamageSource source, EntityAirship entity) {
	}

	public void displaySmoke(Entity entity) {
	}

	public void displaySplashEffect(Entity entity, double par1) {
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == PChan3Mods.GUI_ID && player.ridingEntity instanceof EntityAirship)
			return new ContainerAirship(player.inventory, (EntityAirship) player.ridingEntity);
		else
			return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
	}

    public EntityPlayer getPlayer(){
        return null;
    }

    public void tryCheckForUpdate() {

    }
}
