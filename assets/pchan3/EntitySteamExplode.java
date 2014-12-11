package assets.pchan3;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntitySteamExplode extends EntityFX {
	public EntitySteamExplode(World world, double d, double d1, double d2) {
		super(world, d, d1, d2, 0, 0, 0);
		motionX = (float) (Math.random() * 2D - 1.0D) * 0.05F;
		motionY = (float) (Math.random() * 2D - 1.0D) * 0.05F;
		motionZ = (float) (Math.random() * 2D - 1.0D) * 0.05F;
		particleRed = 230;
		particleGreen = 230;
		particleBlue = 230;
		particleScale = rand.nextFloat() * rand.nextFloat() * 6F + 1.0F;
		particleMaxAge = (int) (16D / (rand.nextFloat() * 0.8D + 0.2D)) + 2;
		particleGravity = -2;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		setParticleTextureIndex(7 - (particleAge * 8) / particleMaxAge);
	}
}
