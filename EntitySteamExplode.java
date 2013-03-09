package pchan3;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class EntitySteamExplode extends EntityFX
{
    public EntitySteamExplode(World world, double d, double d1, double d2, 
            double d3, double d4, double d5)
    {
        super(world, d, d1, d2, d3, d4, d5);
        motionX = d3 + (double)((float)(Math.random() * 2D - 1.0D) * 0.05F);
        motionY = d4 + (double)((float)(Math.random() * 2D - 1.0D) * 0.05F);
        motionZ = d5 + (double)((float)(Math.random() * 2D - 1.0D) * 0.05F);
        particleRed = 230;
        particleGreen = 230;
        particleBlue = 230;
        particleScale = rand.nextFloat() * rand.nextFloat() * 6F + 1.0F;
        particleMaxAge = (int)(16D / ((double)rand.nextFloat() * 0.80000000000000004D + 0.20000000000000001D)) + 2;
    }

    @Override
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if(particleAge++ >= particleMaxAge)
        {
            setDead();
        }
        setParticleTextureIndex(7 - (particleAge * 8) / particleMaxAge);
        
        motionY += 0.0080000000000000001D;
	/*try{
        moveEntity(motionX, motionY, motionZ);
	} catch (Exception ex){
	    
	}*/
        motionX *= 0.89999997615814209D;
        motionY *= 0.89999997615814209D;
        motionZ *= 0.89999997615814209D;
        if(onGround)
        {
            motionX *= 0.69999998807907104D;
            motionZ *= 0.69999998807907104D;
        }
    }
}
