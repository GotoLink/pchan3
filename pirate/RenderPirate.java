package pchan3.pirate;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLiving;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class RenderPirate extends RenderLiving
{

    public RenderPirate(ModelBase modelbase, ModelBase modelbase1, float f)
    {
        super(modelbase, f);
        this.setRenderPassModel(modelbase1);
    }

    protected int renderPirate(EntityPirate entitysheep, int i, float f)
    {
        if(i == 0)
        {
            loadTexture("/pchan3/mob/balloon.png");              
            return 2;
        } 
        else
        {
            return -1;
        }
    }
    @Override
    protected int shouldRenderPass(EntityLiving entityliving, int i, float f)
    {
        return this.renderPirate((EntityPirate)entityliving, i, f);
    }
}
