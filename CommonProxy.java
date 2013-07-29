package assets.pchan3;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import assets.pchan3.steamship.ContainerAirship;
import assets.pchan3.steamship.EntityAirship;
import assets.pchan3.steamship.GuiAirship;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler{

	public void registerRenderInformation() {}
	public void displayExplodeFX(Entity entity){}
	public void displayShipExplodeFX(DamageSource source, EntityAirship entity){}
	public void displaySmoke(Entity entity){}
	public void displaySplashEffect(Entity entity,double par1){}
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if(ID==PChan3Mods.instance.GUI_ID)
			return new ContainerAirship(player.inventory, (EntityAirship) player.ridingEntity);
		else 
			return null;
	}
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if(ID==PChan3Mods.instance.GUI_ID)
			return new GuiAirship(player.inventory, (EntityAirship) player.ridingEntity);
		else 
			return null;
	}
}
