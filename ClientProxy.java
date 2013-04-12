package mods.pchan3;

import java.util.Random;

import mods.pchan3.pirate.EntityPirate;
import mods.pchan3.pirate.ModelPirate;
import mods.pchan3.pirate.RenderPirate;
import mods.pchan3.steamboat.EntitySteamBoat;
import mods.pchan3.steamboat.RenderSteamBoat;
import mods.pchan3.steamship.AirshipKeyHandler;
import mods.pchan3.steamship.EntityAirship;
import mods.pchan3.steamship.EntitySteamFX;
import mods.pchan3.steamship.ModelAirship;
import mods.pchan3.steamship.ModelBalloon;
import mods.pchan3.steamship.RenderAirship;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
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
	KeyBindingRegistry.registerKeyBinding(new AirshipKeyHandler(PChan3Mods.instance.KEY_CHEST,PChan3Mods.instance.KEY_UP,PChan3Mods.instance.KEY_DOWN,PChan3Mods.instance.KEY_FIRE));
  }  
     @Override
    public void displayExplodeFX(Entity entity)
     {
    	for (int i = 1; i < 30; i++) {
 			Random rand=new Random();
 		    if (i % 2 == 0) {
 		    	FMLClientHandler.instance().getClient().effectRenderer.addEffect(new EntitySteamExplode(entity.worldObj,
 		    			entity.posX + (rand.nextInt(i) / 8), entity.posY, entity.posZ
 				- (rand.nextInt(i) / 8), 0D, 0D, 0D));
 		    	FMLClientHandler.instance().getClient().effectRenderer.addEffect(new EntitySteamExplode(entity.worldObj,
 		    			entity.posX + (rand.nextInt(i) / 8), entity.posY, entity.posZ
 				+ (rand.nextInt(i) / 8), 0D, 0D, 0D));
 		    } else {
 		    	FMLClientHandler.instance().getClient().effectRenderer.addEffect(new EntitySteamExplode(entity.worldObj,
 		    			entity.posX - (rand.nextInt(i) / 8), entity.posY, entity.posZ
 				+ (rand.nextInt(i) / 8), 0D, 0D, 0D));
 		    	FMLClientHandler.instance().getClient().effectRenderer.addEffect(new EntitySteamExplode(entity.worldObj,
 		    			entity.posX - (rand.nextInt(i) / 8), entity.posY, entity.posZ
 				- (rand.nextInt(i) / 8), 0D, 0D, 0D));
 		    }
 		}
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
    	 Random rand=new Random();
    	 double smoke = rand.nextFloat() * 2.0f - 1.0f;
         if (smoke > 0.65f) {
         	FMLClientHandler.instance().getClient().effectRenderer.addEffect(new EntitySteamFX(entity.worldObj, entity.posX,
         			entity.posY + 0.9D, entity.posZ, 0.0D, 0.0D, 0.0D));
     }
     }
     @Override
 	public void displaySplashEffect(Entity entity,double par1)
     {
	 	Random rand=new Random();
	 	int i=5;
 		double d13 = Math.cos(((double) entity.rotationYaw * Math.PI) / 180D);
 		double d15 = Math.sin(((double) entity.rotationYaw * Math.PI) / 180D);
 	    for (int i1 = 0; (double) i1 < 1.0D + par1 * 60D; i1++) {
 		double d18 = rand.nextFloat() * 2.0F - 1.0F;
 		double d20 = (double) (rand.nextInt(2) * 2 - 1) * 0.7D;

 		double d4 = (entity.boundingBox.minY + ((entity.boundingBox.maxY - entity.boundingBox.minY) * (double) (i1 + 0))
 			/ (double) i) - 0.125D;
 		double d8 = (entity.boundingBox.minY + ((entity.boundingBox.maxY - entity.boundingBox.minY) * (double) (i1 + 1))
 			/ (double) i) - 0.125D;
 		AxisAlignedBB axisalignedbb = AxisAlignedBB
 			.getBoundingBox(entity.boundingBox.minX, d4, entity.boundingBox.minZ, entity.boundingBox.maxX, d8,
 					entity.boundingBox.maxZ);

 		if (rand.nextBoolean()) {
 		    double d21 = (entity.posX - d13 * d18 * 0.8D)+ d15 * d20;
 		    double d23 = entity.posZ - d15 * d18 * 0.8D - d13 * d20;

 		    if (entity.worldObj.isAABBInMaterial(axisalignedbb, Material.water)) {
 		    	entity.worldObj.spawnParticle("splash", d21, entity.posY - 0.125D, d23, entity.motionX, entity.motionY, entity.motionZ);
 		    }
 		} else {
 		    double d22 = entity.posX + d13 + d15 * d18 * 0.69999999999999996D;
 		    double d24 = (entity.posZ + d15) - d13 * d18 * 0.69999999999999996D;
 		    if (entity.worldObj.isAABBInMaterial(axisalignedbb, Material.water)) {
 		    	entity.worldObj.spawnParticle("splash", d22, entity.posY - 0.125D, d24, entity.motionX, entity.motionY, entity.motionZ);
 		    }
 		}
 	    } 
     }
}
