package assets.pchan3.steamship;

import java.util.Iterator;
import java.util.List;

import assets.pchan3.ItemAnchor;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityAnchor extends EntityLeashKnot {
	public EntityAnchor(World par1World) {
		super(par1World);
	}

	public EntityAnchor(World par1World, int x, int y, int z) {
		super(par1World, x, y, z);
	}

	@Override
	public boolean interactFirst(EntityPlayer par1EntityPlayer) {
		ItemStack itemstack = par1EntityPlayer.getHeldItem();
		boolean flag = false;
		double d0;
		List list;
		Iterator iterator;
		EntityAirship entity;
		if (itemstack != null && itemstack.getItem() instanceof ItemAnchor && !this.worldObj.isRemote) {
			d0 = 7.0D;
			list = this.worldObj.getEntitiesWithinAABB(EntityAirship.class,
					AxisAlignedBB.getAABBPool().getAABB(this.posX - d0, this.posY - d0, this.posZ - d0, this.posX + d0, this.posY + d0, this.posZ + d0));
			if (list != null) {
				iterator = list.iterator();
				while (iterator.hasNext()) {
					entity = (EntityAirship) iterator.next();
					if (entity.isAnchor && entity.thrower == par1EntityPlayer) {
						entity.setAnchor(this, true);
						flag = true;
					}
				}
			}
		}
		if (!this.worldObj.isRemote && !flag) {
			this.setDead();
			if (par1EntityPlayer.capabilities.isCreativeMode) {
				d0 = 7.0D;
				list = this.worldObj.getEntitiesWithinAABB(EntityAirship.class,
						AxisAlignedBB.getAABBPool().getAABB(this.posX - d0, this.posY - d0, this.posZ - d0, this.posX + d0, this.posY + d0, this.posZ + d0));
				if (list != null) {
					iterator = list.iterator();
					while (iterator.hasNext()) {
						entity = (EntityAirship) iterator.next();
						if (entity.isAnchor && entity.thrower == this) {
							entity.unsetAnchor(true, false);
						}
					}
				}
			}
		}
		return true;
	}

	public static EntityLeashKnot specialSpawn(World par0World, int par1, int par2, int par3) {
		EntityAnchor entityleashknot = new EntityAnchor(par0World, par1, par2, par3);
		entityleashknot.forceSpawn = true;
		par0World.spawnEntityInWorld(entityleashknot);
		return entityleashknot;
	}
}
