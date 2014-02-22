package assets.pchan3;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import assets.pchan3.steamship.EntityAirship;
import assets.pchan3.steamship.EntityAnchor;

public class ItemAnchor extends Item {
	public ItemAnchor() {
		super();
		this.setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer player, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
		Block i1 = par3World.getBlock(par4, par5, par6);
		if (i1 instanceof BlockFence) {
			if (par3World.isRemote) {
				return true;
			} else {
				attach(player, par3World, par4, par5, par6);
				return true;
			}
		} else {
			return false;
		}
	}

	public static boolean attach(EntityPlayer player, World par1World, int par2, int par3, int par4) {
		EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForBlock(par1World, par2, par3, par4);
		double d0 = 7.0D;
		List<?> list = par1World.getEntitiesWithinAABB(EntityAirship.class, AxisAlignedBB.getAABBPool().getAABB(par2 - d0, par3 - d0, par4 - d0, par2 + d0, par3 + d0, par4 + d0));
		if (list != null) {
			Iterator<?> iterator = list.iterator();
			while (iterator.hasNext()) {
				EntityAirship airship = (EntityAirship) iterator.next();
				if (airship.isAnchor && airship.thrower.getEntityId() == player.getEntityId()) {
					if (entityleashknot == null) {
						entityleashknot = EntityAnchor.specialSpawn(par1World, par2, par3, par4);
					}
					airship.setAnchor(entityleashknot, true);
					return true;
				}
			}
		}
		return false;
	}
}
