package assets.pchan3;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class EntitySteamExplode extends EntityFX
{
    public EntitySteamExplode(World world, double d, double d1, double d2)
    {
        super(world, d, d1, d2, 0, 0, 0);
        motionX = (double)((float)(Math.random() * 2D - 1.0D) * 0.05F);
        motionY = (double)((float)(Math.random() * 2D - 1.0D) * 0.05F);
        motionZ = (double)((float)(Math.random() * 2D - 1.0D) * 0.05F);
        particleRed = 230;
        particleGreen = 230;
        particleBlue = 230;
        particleScale = rand.nextFloat() * rand.nextFloat() * 6F + 1.0F;
        particleMaxAge = (int)(16D / ((double)rand.nextFloat() * 0.8D + 0.2D)) + 2;
        particleGravity = -2;
    }

    @Override
    public void onUpdate()
    {
    	this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }

        setParticleTextureIndex(7 - (particleAge * 8) / particleMaxAge);
        this.motionY -= 0.04D * (double)this.particleGravity;
        //this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }
}
