package assets.pchan3;

import assets.pchan3.pirate.EntityPirate;
import assets.pchan3.pirate.ModelPirate;
import assets.pchan3.pirate.RenderPirate;
import assets.pchan3.steamboat.EntitySteamBoat;
import assets.pchan3.steamboat.RenderSteamBoat;
import assets.pchan3.steamship.*;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.Random;

/**
 * @author pchan3
 */
public final class ClientProxy extends CommonProxy {
    public static int KEY_UP = Keyboard.KEY_NUMPAD8, KEY_DOWN = Keyboard.KEY_NUMPAD2;
    public static int KEY_CHEST = Keyboard.KEY_R, KEY_FIRE = Keyboard.KEY_NUMPAD5;
    Random rand = new Random();

	@Override
	public void displayExplodeFX(Entity entity) {
		for (int i = 1; i < 30; i++) {
			if (i % 2 == 0) {
				addEffect(new EntitySteamExplode(entity.worldObj, entity.posX + (rand.nextInt(i) / 8), entity.posY, entity.posZ - (rand.nextInt(i) / 8)));
				addEffect(new EntitySteamExplode(entity.worldObj, entity.posX + (rand.nextInt(i) / 8), entity.posY, entity.posZ + (rand.nextInt(i) / 8)));
			} else {
				addEffect(new EntitySteamExplode(entity.worldObj, entity.posX - (rand.nextInt(i) / 8), entity.posY, entity.posZ + (rand.nextInt(i) / 8)));
				addEffect(new EntitySteamExplode(entity.worldObj, entity.posX - (rand.nextInt(i) / 8), entity.posY, entity.posZ - (rand.nextInt(i) / 8)));
			}
		}
	}

	@Override
	public void displayShipExplodeFX(DamageSource source, EntityAirship entity) {
        if(source.getEntity()==null)
            return;
		double d1 = source.getEntity().posX - (source.getEntity().posX - entity.posX) / 2;
		double d2 = source.getEntity().posY - (source.getEntity().posY - entity.posY) / 2;
		double d3 = source.getEntity().posZ - (source.getEntity().posZ - entity.posZ) / 2;
		addEffect(new EntitySteamExplode(entity.worldObj, d1, d2, d3));
	}

	@Override
	public void displaySmoke(Entity entity) {
		if (rand.nextFloat() * 2.0f > 1.65f) {
			EntitySmokeFX fx = new EntitySmokeFX(entity.worldObj, entity.posX, entity.posY + 0.9D, entity.posZ, 0.0D, 0.0D, 0.0D);
			fx.setRBGColorF(230, 230, 230);
			addEffect(fx);
		}
	}

	@Override
	public void displaySplashEffect(Entity entity, double par1) {
		int i = 5;
		double d13 = Math.cos((entity.rotationYaw * Math.PI) / 180D);
		double d15 = Math.sin((entity.rotationYaw * Math.PI) / 180D);
		for (int i1 = 0; i1 < 1.0D + par1 * 60D; i1++) {
			double d18 = rand.nextFloat() * 2.0F - 1.0F;
			double d20 = (rand.nextInt(2) * 2 - 1) * 0.7D;
			double d4 = (entity.boundingBox.minY + ((entity.boundingBox.maxY - entity.boundingBox.minY) * (i1 + 0)) / i) - 0.125D;
			double d8 = (entity.boundingBox.minY + ((entity.boundingBox.maxY - entity.boundingBox.minY) * (i1 + 1)) / i) - 0.125D;
			AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(entity.boundingBox.minX, d4, entity.boundingBox.minZ, entity.boundingBox.maxX, d8, entity.boundingBox.maxZ);
			if (rand.nextBoolean()) {
				double d21 = entity.posX - d13 * d18 * 0.8D + d15 * d20;
				double d23 = entity.posZ - d15 * d18 * 0.8D - d13 * d20;
				if (entity.worldObj.isAABBInMaterial(axisalignedbb, Material.water)) {
					entity.worldObj.spawnParticle("splash", d21, entity.posY - 0.125D, d23, entity.motionX, entity.motionY, entity.motionZ);
				}
			} else {
				double d22 = entity.posX + d13 + d15 * d18 * 0.7D;
				double d24 = entity.posZ + d15 - d13 * d18 * 0.7D;
				if (entity.worldObj.isAABBInMaterial(axisalignedbb, Material.water)) {
					entity.worldObj.spawnParticle("splash", d22, entity.posY - 0.125D, d24, entity.motionX, entity.motionY, entity.motionZ);
				}
			}
		}
	}

	@Override
	public void registerRenderInformation() {
		RenderingRegistry.registerEntityRenderingHandler(EntityAirship.class, new RenderAirship(new ModelAirship(), new ModelBalloon(), 3.0f));
		RenderingRegistry.registerEntityRenderingHandler(EntityPirate.class, new RenderPirate(new ModelPirate(), new ModelBalloon(), 1.0f));
		RenderingRegistry.registerEntityRenderingHandler(EntitySteamBoat.class, new RenderSteamBoat());
        FMLCommonHandler.instance().bus().register(new AirshipKeyHandler(KEY_CHEST, KEY_UP, KEY_DOWN, KEY_FIRE));
	}

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == PChan3Mods.GUI_ID && player.ridingEntity instanceof EntityAirship)
            return new GuiAirship(player.inventory, (EntityAirship) player.ridingEntity);
        else
            return null;
    }

    @Override
    public EntityPlayer getPlayer(){
        return Minecraft.getMinecraft().thePlayer;
    }

    private void addEffect(EntityFX entityFX){
        Minecraft.getMinecraft().effectRenderer.addEffect(entityFX);
    }

    @Override
    public void tryCheckForUpdate() {
        try {
            Class.forName("mods.mud.ModUpdateDetector").getDeclaredMethod("registerMod", ModContainer.class, String.class, String.class).invoke(null,
                    FMLCommonHandler.instance().findContainerFor(PChan3Mods.instance),
                    "https://raw.github.com/GotoLink/pchan3/master/update.xml",
                    "https://raw.github.com/GotoLink/pchan3/master/changelog.md"
            );
        } catch (Throwable e) {
        }
    }
}
