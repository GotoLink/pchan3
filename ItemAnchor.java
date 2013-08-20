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

public class ItemAnchor extends Item{

	public ItemAnchor(int par1) 
	{
		super(par1);
		this.setCreativeTab(CreativeTabs.tabTools);
	}
	
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        int i1 = par3World.getBlockId(par4, par5, par6);

        if (Block.blocksList[i1] != null && Block.blocksList[i1] instanceof BlockFence)
        {
            if (par3World.isRemote)
            {
                return true;
            }
            else
            {
                attach(par2EntityPlayer, par3World, par4, par5, par6);
                return true;
            }
        }
        else
        {
            return false;
        }
    }
	
	public static boolean attach(EntityPlayer par0EntityPlayer, World par1World, int par2, int par3, int par4)
    {
        EntityLeashKnot entityleashknot = EntityLeashKnot.func_110130_b(par1World, par2, par3, par4);
        double d0 = 7.0D;
        List list = par1World.getEntitiesWithinAABB(EntityAirship.class, AxisAlignedBB.getAABBPool().getAABB((double)par2 - d0, (double)par3 - d0, (double)par4 - d0, (double)par2 + d0, (double)par3 + d0, (double)par4 + d0));

        if (list != null)
        {
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
            	EntityAirship airship = (EntityAirship)iterator.next();

                if (airship.isAnchor && airship.thrower == par0EntityPlayer)
                {
                    if (entityleashknot == null)
                    {
                        entityleashknot = EntityLeashKnot.func_110129_a(par1World, par2, par3, par4);
                    }

                    airship.setAnchor(entityleashknot, true);
                    return true;
                }
            }
        }

        return false;
    }
}
