package assets.pchan3.steamship;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAirship extends GuiContainer {
	private EntityAirship airship;
	public static final ResourceLocation gui = new ResourceLocation("pchan3", "textures/gui/airshipgui.png");

	public GuiAirship(InventoryPlayer inventoryplayer, EntityAirship air) {
		super(new ContainerAirship(inventoryplayer, air));
		airship = air;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRenderer.drawString("Airship Inventory", 8, 4, 0x404040);
		fontRenderer.drawString("Arrows:", 89, 55, 0x404040);
		fontRenderer.drawString("Fuel:", 105, 20, 0x404040);
		fontRenderer.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int b, int r) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(gui);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		int x = airship.getFuelScaled(10);//32 = Empty, 0 = Full, Work Out Fuel level.
		this.drawTexturedModalRect(j + 156, k + 15, 176, 32 - x, 12, 32);
		//drawTexturedModalRect(j + 79, k + 34, 176, 14, i1 + 1, 16);
	}
}
