package assets.pchan3;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

@SideOnly(Side.CLIENT)
public final class CustomTexturedQuad {
	public CustomTexturedQuad(PositionTextureVertex aPositionTextureVertex[]) {
		field_1195_a = aPositionTextureVertex;
	}

	public CustomTexturedQuad(PositionTextureVertex aPositionTextureVertex[], int i, int j, int k, int l) {
		this(aPositionTextureVertex, i, j, k, l, 64F, 32F);
	}

	public CustomTexturedQuad(PositionTextureVertex aPositionTextureVertex[], int i, int j, int k, int l, float w, float h) {
		this(aPositionTextureVertex);
		float f = 0.0015625F;
		float f1 = 0.003125F;
		aPositionTextureVertex[0] = aPositionTextureVertex[0].setTexturePosition(k / w - f, j / h + f1);
		aPositionTextureVertex[1] = aPositionTextureVertex[1].setTexturePosition(i / w + f, j / h + f1);
		aPositionTextureVertex[2] = aPositionTextureVertex[2].setTexturePosition(i / w + f, l / h - f1);
		aPositionTextureVertex[3] = aPositionTextureVertex[3].setTexturePosition(k / w - f, l / h - f1);
	}

	public void reverse() {
		PositionTextureVertex aPositionTextureVertex[] = new PositionTextureVertex[field_1195_a.length];
		for (int i = 0; i < field_1195_a.length; i++) {
			aPositionTextureVertex[i] = field_1195_a[field_1195_a.length - i - 1];
		}
		field_1195_a = aPositionTextureVertex;
	}

	public void render(Tessellator tessellator, float f) {
		Vec3 vec3d = field_1195_a[1].vector3D.subtract(field_1195_a[0].vector3D);
		Vec3 vec3d1 = field_1195_a[1].vector3D.subtract(field_1195_a[2].vector3D);
		Vec3 vec3d2 = vec3d1.crossProduct(vec3d).normalize();
		tessellator.startDrawingQuads();
		tessellator.setNormal((float) vec3d2.xCoord, (float) vec3d2.yCoord, (float) vec3d2.zCoord);
		for (int i = 0; i < 4; i++) {
			PositionTextureVertex positionTextureVertex = field_1195_a[i];
			tessellator.addVertexWithUV((float) positionTextureVertex.vector3D.xCoord * f, (float) positionTextureVertex.vector3D.yCoord * f, (float) positionTextureVertex.vector3D.zCoord * f,
					positionTextureVertex.texturePositionX, positionTextureVertex.texturePositionY);
		}
		tessellator.draw();
	}

	public PositionTextureVertex field_1195_a[];
}
