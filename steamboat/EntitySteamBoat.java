package mods.pchan3.steamboat;
 
import java.util.List;

import mods.pchan3.PChan3Mods;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntitySteamBoat extends EntityBoat
{
	private int boatPosRotationIncrements;
    private double boatX,boatY,boatZ;
    private double boatYaw;
    private double boatPitch;
    @SideOnly(Side.CLIENT)
    private double velocityX;
    @SideOnly(Side.CLIENT)
    private double velocityY;
    @SideOnly(Side.CLIENT)
    private double velocityZ;
    private double field_70276_b;
    private boolean field_70279_a;

    public EntitySteamBoat(World world)
    {
    	super(world);
    	this.field_70279_a = true;
        this.field_70276_b = 0.07D;
        this.preventEntitySpawning = true;
        this.setSize(1.5F, 0.6F);
        this.yOffset = this.height / 2.0F;
    }
    public EntitySteamBoat(World world, double par2, double par4, double par6)
    {
    	this(world);
    	this.setPosition(par2, par4 + (double)this.yOffset, par6);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = par2;
        this.prevPosY = par4;
        this.prevPosZ = par6;
    }
    @Override
    protected void entityInit()
    {     
    	super.entityInit();
        this.dataWatcher.addObject(25, new Integer(0)); 	
    }
    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
    	if (this.isEntityInvulnerable())
    {
        return false;
    }
    else if (!this.worldObj.isRemote && !this.isDead)
    {
        this.setForwardDirection(-this.getForwardDirection());
        this.setTimeSinceHit(10);
        this.setDamageTaken(this.getDamageTaken() + par2 * 10);
        this.setBeenAttacked();

        if (par1DamageSource.getEntity() instanceof EntityPlayer && ((EntityPlayer)par1DamageSource.getEntity()).capabilities.isCreativeMode)
        {
            this.setDamageTaken(100);
            if (this.riddenByEntity != null)
            {
                this.riddenByEntity.mountEntity(this);
            }
            
            this.dropItemWithOffset(PChan3Mods.instance.steamBoat.itemID, 1, 0.0f);
            this.setDead();
            return true;
        }

        if (this.getDamageTaken() > 80)
        {
            if (this.riddenByEntity != null)
            {
                this.riddenByEntity.mountEntity(this);
            }
            
            this.dropItemWithOffset(Block.planks.blockID, 5, 0.0F);
            this.dropItemWithOffset(Item.ingotIron.itemID, 1, 0.0f);
            this.setDead();
        }

        return true;
    }
    else
    {
        return true;
    }           
    }

    @SideOnly(Side.CLIENT)
    public void displayEffect(double par1)
    {
    	 double var6 = Math.cos((double)this.rotationYaw * Math.PI / 180.0D);
         double var8 = Math.sin((double)this.rotationYaw * Math.PI / 180.0D);

         for (int var10 = 0; (double)var10 < 1.0D + par1 * 60.0D; ++var10)
         {
             double var11 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
             double var13 = (double)(this.rand.nextInt(2) * 2 - 1) * 0.7D;
             double var15;
             double var17;

             if (this.rand.nextBoolean())
             {
                 var15 = this.posX - var6 * var11 * 0.8D + var8 * var13;
                 var17 = this.posZ - var8 * var11 * 0.8D - var6 * var13;
                 this.worldObj.spawnParticle("splash", var15, this.posY - 0.125D, var17, this.motionX, this.motionY, this.motionZ);
             }
             else
             {
                 var15 = this.posX + var6 + var8 * var11 * 0.7D;
                 var17 = this.posZ + var8 - var6 * var11 * 0.7D;
                 this.worldObj.spawnParticle("splash", var15, this.posY - 0.125D, var17, this.motionX, this.motionY, this.motionZ);
             }
         }	
    }
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
    {
        if (this.field_70279_a)
        {
            this.boatPosRotationIncrements = par9 + 5;
        }
        else
        {
            double d3 = par1 - this.posX;
            double d4 = par3 - this.posY;
            double d5 = par5 - this.posZ;
            double d6 = d3 * d3 + d4 * d4 + d5 * d5;

            if (d6 <= 1.0D)
            {
                return;
            }

            this.boatPosRotationIncrements = 3;
        }

        this.boatX = par1;
        this.boatY = par3;
        this.boatZ = par5;
        this.boatYaw = (double)par7;
        this.boatPitch = (double)par8;
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }

    @SideOnly(Side.CLIENT)
    public void setVelocity(double par1, double par3, double par5)
    {
        this.velocityX = this.motionX = par1;
        this.velocityY = this.motionY = par3;
        this.velocityZ = this.motionZ = par5;
    }
    @SideOnly(Side.CLIENT)
    public void displaySmoke()
    {
    	double smoke = this.rand.nextFloat() * 2.0f - 1.0f;
        if (smoke > 0.65f) {
           this.worldObj.spawnParticle("smoke", this.posX, this.posY+0.9D, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }
    @Override
    public void onUpdate()
    {
        super.onEntityUpdate();
        if (!this.worldObj.isRemote){
        if (this.getTimeSinceHit() > 0)
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        if (this.getDamageTaken() > 0)
            this.setDamageTaken(this.getDamageTaken() - 1);
        if (this.getFuelTime() > 0) 
    	    this.setFuelTime(this.getFuelTime()-1);
        
    	if (this.getFuelTime() == 0 && this.riddenByEntity != null) {
    		    if (((EntityPlayer) this.riddenByEntity).inventory.hasItem(263)) {
    		    	this.setFuelTime(1600);
    			((EntityPlayer) this.riddenByEntity).inventory.consumeInventoryItem(263);
    		    }		
    	  }
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        byte var1 = 5;
        double var2 = 0.0D;

        for (int var4 = 0; var4 < var1; ++var4)
        {
            double var5 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(var4 + 0) / (double)var1 - 0.125D;
            double var7 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(var4 + 1) / (double)var1 - 0.125D;
            AxisAlignedBB var9 = AxisAlignedBB.getAABBPool().getAABB(this.boundingBox.minX, var5, this.boundingBox.minZ, this.boundingBox.maxX, var7, this.boundingBox.maxZ);

            if (this.worldObj.isAABBInMaterial(var9, Material.water))
            {
                var2 += 1.0D / (double)var1;
            }
        }

        double var24 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        double var6;
        double var8;

        if (!this.worldObj.isRemote && var24 > 0.26249999999999996D)
        {
           PChan3Mods.instance.proxy.displaySplashEffect(this, var24);
        }
        if ( !this.worldObj.isRemote && this.getFuelTime()!=0) {
        	PChan3Mods.instance.proxy.displaySmoke(this);
        }
        double var12;
        double var26;

        if (this.worldObj.isRemote && this.field_70279_a)
        {
            if (this.boatPosRotationIncrements > 0)
            {
                var6 = this.posX + (this.boatX - this.posX) / (double)this.boatPosRotationIncrements;
                var8 = this.posY + (this.boatY - this.posY) / (double)this.boatPosRotationIncrements;
                var26 = this.posZ + (this.boatZ - this.posZ) / (double)this.boatPosRotationIncrements;
                var12 = MathHelper.wrapAngleTo180_double(this.boatYaw - (double)this.rotationYaw);
                this.rotationYaw = (float)((double)this.rotationYaw + var12 / (double)this.boatPosRotationIncrements);
                this.rotationPitch = (float)((double)this.rotationPitch + (this.boatPitch - (double)this.rotationPitch) / (double)this.boatPosRotationIncrements);
                --this.boatPosRotationIncrements;
                this.setPosition(var6, var8, var26);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
            else
            {
                var6 = this.posX + this.motionX;
                var8 = this.posY + this.motionY;
                var26 = this.posZ + this.motionZ;
                this.setPosition(var6, var8, var26);

                if (this.onGround||this.getFuelTime()==0)
                {
                    this.motionX *= 0.5D;
                    this.motionY *= 0.5D;
                    this.motionZ *= 0.5D;
                }

                this.motionX *= 0.9900000095367432D;
                this.motionY *= 0.949999988079071D;
                this.motionZ *= 0.9900000095367432D;
            }
        }
        else
        {
            if (var2 < 1.0D)
            {
                var6 = var2 * 2.0D - 1.0D;
                this.motionY += 0.03999999910593033D * var6;
            }
            else
            {
                if (this.motionY < 0.0D)
                {
                    this.motionY /= 2.0D;
                }

                this.motionY += 0.007000000216066837D;
            }

            if (this.riddenByEntity != null)
            {
                this.motionX += this.riddenByEntity.motionX * this.field_70276_b;
                this.motionZ += this.riddenByEntity.motionZ * this.field_70276_b;
            }

            var6 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (var6 > 0.35D)
            {
                var8 = 0.35D / var6;
                this.motionX *= var8;
                this.motionZ *= var8;
                var6 = 0.35D;
            }

            if (var6 > var24 && this.field_70276_b < 0.35D)
            {
                this.field_70276_b += (0.35D - this.field_70276_b) / 35.0D;

                if (this.field_70276_b > 0.35D)
                {
                    this.field_70276_b = 0.35D;
                }
            }
            else
            {
                this.field_70276_b -= (this.field_70276_b - 0.07D) / 35.0D;

                if (this.field_70276_b < 0.07D)
                {
                    this.field_70276_b = 0.07D;
                }
            }

            if (this.onGround||this.getFuelTime()==0)
            {
                this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);

            if (this.isCollidedHorizontally && var24 > 0.4D)
            {
                if (!this.worldObj.isRemote)
                {
                    this.setDead();
                    int var25;
                    for(int k = 0; k < 5; k++)
                    {
                    	this.dropItemWithOffset(Block.planks.blockID, 1, 0.0F);
                    }

                    for(int l = 0; l < 1; l++)
                    {
                    	this.dropItemWithOffset(Item.ingotIron.itemID, 1, 0.0F);
                    }
                }
            }
            else
            {
                this.motionX *= 0.9900000095367432D;
                this.motionY *= 0.949999988079071D;
                this.motionZ *= 0.9900000095367432D;
            }

            this.rotationPitch = 0.0F;
            var8 = (double)this.rotationYaw;
            var26 = this.prevPosX - this.posX;
            var12 = this.prevPosZ - this.posZ;

            if (var26 * var26 + var12 * var12 > 0.001D)
            {
                var8 = (double)((float)(Math.atan2(var12, var26) * 180.0D / Math.PI));
            }

            double var14 = MathHelper.wrapAngleTo180_double(var8 - (double)this.rotationYaw);

            if (var14 > 20.0D)
            {
                var14 = 20.0D;
            }

            if (var14 < -20.0D)
            {
                var14 = -20.0D;
            }

            this.rotationYaw = (float)((double)this.rotationYaw + var14);
            this.setRotation(this.rotationYaw, this.rotationPitch);

            if (!this.worldObj.isRemote)
            {
                List var16 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
                int var27;

                if (var16 != null && !var16.isEmpty())
                {
                    for (var27 = 0; var27 < var16.size(); ++var27)
                    {
                        Entity var18 = (Entity)var16.get(var27);

                        if (var18 != this.riddenByEntity && var18.canBePushed() && var18 instanceof EntitySteamBoat)
                        {
                            var18.applyEntityCollision(this);
                        }
                    }
                }

                for (var27 = 0; var27 < 4; ++var27)
                {
                    int var28 = MathHelper.floor_double(this.posX + ((double)(var27 % 2) - 0.5D) * 0.8D);
                    int var19 = MathHelper.floor_double(this.posZ + ((double)(var27 / 2) - 0.5D) * 0.8D);

                    for (int var20 = 0; var20 < 2; ++var20)
                    {
                        int var21 = MathHelper.floor_double(this.posY) + var20;
                        int var22 = this.worldObj.getBlockId(var28, var21, var19);
                        
                        if (var22 == Block.snow.blockID)
                        {
                            this.worldObj.setBlockToAir(var28, var21, var19);
                        }
                        else if (var22 == Block.waterlily.blockID)
                        {
                            this.worldObj.destroyBlock(var28, var21, var19, true);
                        }
                    }
                }

                if (this.riddenByEntity != null && this.riddenByEntity.isDead)
                {
                    this.riddenByEntity = null;
                }
            }
        }     
    }
	
    @Override
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != par1EntityPlayer)
        {
            return true;
        }
        else {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();

        if (var2 != null && var2.itemID == Item.coal.itemID)
        {
            if (--var2.stackSize == 0)
            {
                par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
            }
            if (!this.worldObj.isRemote)
            this.setFuelTime(1600);
        }
        else if (!this.worldObj.isRemote)   
                par1EntityPlayer.mountEntity(this);       
        }
        return true;
    }
    
    public int getFuelTime()
	{
	return this.dataWatcher.getWatchableObjectInt(25);	
	}
    
	public void setFuelTime(int par1)
	{
	this.dataWatcher.updateObject(25, Integer.valueOf(par1));
	}
	
	@SideOnly(Side.CLIENT)
    public void func_70270_d(boolean par1)
    {
        this.field_70279_a = par1;
    }
}
