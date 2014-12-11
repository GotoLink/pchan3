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
		field_1194_b = aPositionTextureVertex.length;
	}

	public CustomTexturedQuad(PositionTextureVertex aPositionTextureVertex[], int i, int j, int k, int l) {
		this(aPositionTextureVertex);
		float f = 0.0015625F;
		float f1 = 0.003125F;
		aPositionTextureVertex[0] = aPositionTextureVertex[0].setTexturePosition(k / 64F - f, j / 32F + f1);
		aPositionTextureVertex[1] = aPositionTextureVertex[1].setTexturePosition(i / 64F + f, j / 32F + f1);
		aPositionTextureVertex[2] = aPositionTextureVertex[2].setTexturePosition(i / 64F + f, l / 32F - f1);
		aPositionTextureVertex[3] = aPositionTextureVertex[3].setTexturePosition(k / 64F - f, l / 32F - f1);
	}

	public CustomTexturedQuad(PositionTextureVertex aPositionTextureVertex[], int i, int j, int k, int l, int texWidth, int texHeight) {
		this(aPositionTextureVertex);
		float f = 0.0015625F;
		float f1 = 0.003125F;
		float w = texWidth;
		float h = texHeight;
		aPositionTextureVertex[0] = aPositionTextureVertex[0].setTexturePosition(k / w - f, j / h + f1);
		aPositionTextureVertex[1] = aPositionTextureVertex[1].setTexturePosition(i / w + f, j / h + f1);
		aPositionTextureVertex[2] = aPositionTextureVertex[2].setTexturePosition(i / w + f, l / h - f1);
		aPositionTextureVertex[3] = aPositionTextureVertex[3].setTexturePosition(k / w - f, l / h - f1);
	}

	public void func_809_a() {
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
			PositionTextureVertex PositionTextureVertex = field_1195_a[i];
			tessellator.addVertexWithUV((float) PositionTextureVertex.vector3D.xCoord * f, (float) PositionTextureVertex.vector3D.yCoord * f, (float) PositionTextureVertex.vector3D.zCoord * f,
					PositionTextureVertex.texturePositionX, PositionTextureVertex.texturePositionY);
		}
		tessellator.draw();
	}

	public PositionTextureVertex field_1195_a[];
	public int field_1194_b;
}
