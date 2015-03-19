package assets.pchan3;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public final class EntitySteamExplode extends EntityFX {
	public EntitySteamExplode(World world, double d, double d1, double d2) {
		super(world, d, d1, d2, 0, 0, 0);
		motionX = (float) (Math.random() * 2D - 1.0D) * 0.05F;
		motionZ = (float) (Math.random() * 2D - 1.0D) * 0.05F;
		particleRed = 230;
		particleGreen = 230;
		particleBlue = 230;
		particleScale = rand.nextFloat() * rand.nextFloat() * 6F + 1.0F;
		particleMaxAge = (int) (16D / (rand.nextFloat() * 0.8D + 0.2D));
		particleGravity = -2;
		noClip = true;
	}

	@Override
	public void onUpdate() {
		if (this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)).getMaterial() == Material.air)
		{
			this.motionY = 0.0D;
			super.onUpdate();
		}else if (this.particleAge++ >= this.particleMaxAge)
		{
			this.setDead();
		}
		setParticleTextureIndex(7 - (particleAge * 8) / particleMaxAge);
	}

	@Override
	protected void updateFallState(double distance, boolean ground){

	}

	@Override
	public void applyEntityCollision(Entity collider){

	}
}
