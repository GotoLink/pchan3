package assets.pchan3.pirate;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderPirate extends RenderLiving {
	private static final ResourceLocation balloon = new ResourceLocation("pchan3", "textures/models/pirateballoon.png");
	private static final ResourceLocation body = new ResourceLocation("pchan3", "textures/models/pirateairship.png");

	public RenderPirate(ModelBase modelbase, ModelBase modelbase1, float f) {
		super(modelbase, f);
		this.setRenderPassModel(modelbase1);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return body;
	}

	protected int renderPirate(EntityPirate entitysheep, int i, float f) {
		if (i == 0) {
			this.bindTexture(balloon);
			return 2;
		} else
			return -1;
	}

	@Override
	protected int shouldRenderPass(EntityLivingBase entityliving, int i, float f) {
		return this.renderPirate((EntityPirate) entityliving, i, f);
	}
}
