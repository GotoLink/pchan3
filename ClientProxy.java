package pchan3;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.client.MinecraftForgeClient;
import pchan3.pirate.EntityPirate;
import pchan3.pirate.ModelPirate;
import pchan3.pirate.RenderPirate;
import pchan3.steamboat.EntitySteamBoat;
import pchan3.steamboat.RenderSteamBoat;
import pchan3.steamship.AirshipKeyHandler;
import pchan3.steamship.EntityAirship;
import pchan3.steamship.ModelAirship;
import pchan3.steamship.ModelBalloon;
import pchan3.steamship.RenderAirship;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

/**
 *
 * @author pchan3
 */
public class ClientProxy extends CommonProxy{
     @Override
    public void registerRenderInformation() 
  {   	
	RenderingRegistry.registerEntityRenderingHandler(EntityAirship.class, new RenderAirship(new ModelAirship(),new ModelBalloon(), 3.0f));
	RenderingRegistry.registerEntityRenderingHandler(EntityPirate.class, new RenderPirate(new ModelPirate(),new ModelBalloon(), 1.0f));
	RenderingRegistry.registerEntityRenderingHandler(EntitySteamBoat.class, new RenderSteamBoat());
	KeyBindingRegistry.registerKeyBinding(new AirshipKeyHandler(PChan3mods.instance.KEY_CHEST,PChan3mods.instance.KEY_UP,PChan3mods.instance.KEY_DOWN,PChan3mods.instance.KEY_FIRE));
  }  
     @Override
    public void preloadTextures()
    {
    	MinecraftForgeClient.preloadTexture("/pchan3/gui/items.png");
    }
     @Override
    public void displayExplodeFX(Entity entity)
     {
    	 if (entity instanceof EntityPirate)
    		 ((EntityPirate) entity).displayEffect();
    	 else if (entity instanceof EntityAirship)
    		 ((EntityAirship) entity).displayDeadEffect();
     }
     @Override
    public void displayShipExplodeFX(DamageSource source, EntityAirship entity)
     {
    	double d1 = source.getEntity().posX - (source.getEntity().posX - entity.posX) / 2;
 		double d2 = source.getEntity().posY - (source.getEntity().posY - entity.posY) / 2;
 		double d3 = source.getEntity().posZ - (source.getEntity().posZ - entity.posZ) / 2;
 		FMLClientHandler.instance().getClient().effectRenderer.addEffect(new EntitySteamExplode(entity.worldObj, d1, d2, d3, 0.0D, 0.0D, 0.0D));
 		
     }
     @Override
 	public void displaySmoke(Entity entity)
     {
    	 if (entity instanceof EntityAirship )
    	 {
    		((EntityAirship) entity).displaySmoke(); 
    	 }
    	 else if (entity instanceof EntitySteamBoat)
    	 {
    		 ((EntitySteamBoat) entity).displaySmoke();
    	 }
     }
     @Override
 	public void displaySplashEffect(Entity entity,double par1)
     {
    	 if (entity instanceof EntityAirship )
    	 {
    		((EntityAirship) entity).displayEffect(par1); 
    	 }
    	 else if (entity instanceof EntitySteamBoat)
    	 {
    		 ((EntitySteamBoat) entity).displayEffect(par1);
    	 } 
     }
}
