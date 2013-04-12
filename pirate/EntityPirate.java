package mods.pchan3.pirate;

import java.util.Random;


import mods.pchan3.EntitySteamExplode;
import mods.pchan3.PChan3Mods;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityPirate extends EntityFlying implements IMob,IRangedAttackMob {
	public int courseChangeCooldown=0;
	public double waypointX,waypointY,waypointZ;
	private Entity targetedEntity=null;
	private int aggroCooldown=0;
	public int prevAttackCounter=0,attackCounter=0;
	public boolean playedWeigh = false,playedPrep = false;
	public EntityPirate(World world) {
		super(world);
		this.setSize(4F, 4F);
		this.isImmuneToFire = false;
		this.texture = "/mods/pchan3/textures/models/pirateairship.png";
		this.health = 60;
		this.experienceValue = 5;
		this.tasks.addTask(1, new EntityAIArrowAttack(this, 0.25F, 60, 10.0F));
		this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(2, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
	    this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 16.0F, 0, true));

	}
	@Override
	protected boolean isAIEnabled()
    {
        return true;
    }
	@Override
	public void initCreature()//added, not sure if held item will render or not
    {
		/*this.setCurrentItemOrArmor(0, new ItemStack(Item.bow));
		this.func_82162_bC();//add some enchantment*/
    }
	protected void updateEntityActionState() {
		if (!this.worldObj.isRemote && this.worldObj.difficultySetting == 0)
        {
            this.setDead();
        }
		this.despawnEntity();
		prevAttackCounter = attackCounter;
		double d = this.waypointX - this.posX;
		double d1 = this.waypointY - this.posY;
		double d2 = this.waypointZ - this.posZ;
		double d3 = MathHelper.sqrt_double(d * d + d1 * d1 + d2 * d2);
		if (d3 < 1.0D || d3 > 60D) {
			this.waypointX = this.posX+ (double) ((rand.nextFloat() * 2.0F - 1.0F) * 16F);
			this.waypointY = this.posY+ (double) ((rand.nextFloat() * 2.0F - 1.0F) * 16F);
			this.waypointZ = this.posZ+ (double) ((rand.nextFloat() * 2.0F - 1.0F) * 16F);
		}
		if (courseChangeCooldown-- <= 0) {
			courseChangeCooldown += rand.nextInt(5) + 2;
			if (isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, d3)) {
				this.motionX += (d / d3) * 0.04000000000000001D;
				this.motionY += (d1 / d3) * 0.04000000000000001D;
				this.motionZ += (d2 / d3) * 0.04000000000000001D;
			} else {
				this.waypointX = this.posX;
				this.waypointY = this.posY;
				this.waypointZ = this.posZ;
			}
		}
		if (targetedEntity != null && targetedEntity.isDead) {
			targetedEntity = null;
		}
		if (targetedEntity == null || aggroCooldown-- <= 0) {
			targetedEntity = this.worldObj.getClosestVulnerablePlayerToEntity(this, 100D);
			
			if (targetedEntity != null) {		
				aggroCooldown = 20;					
			}	
		}

		if (targetedEntity != null
				&& targetedEntity.getDistanceSqToEntity(this) < 64 * 64) {

			if (canEntityBeSeen(targetedEntity)) 
			{
				if(attackCounter == 0 && playedWeigh == false)
				{
					this.worldObj.playSoundAtEntity(this, "mob.pirate.weighanchor",
							getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
					playedWeigh = true;
				}
			
				if (attackCounter == 5 && playedPrep == false) 
				{
					this.worldObj.playSoundAtEntity(this, "mob.pirate.prepare",
							getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
					playedPrep = true;
				}
				
				attackCounter++;
				if (attackCounter == 60) 
				{
					double b = targetedEntity.posX - posX + 1.200D;
					double b1 = targetedEntity.posZ - posZ + 1.200D;
					
						EntityArrow entityarrow = new EntityArrow(this.worldObj,this,1.0f);
						entityarrow.posY += 1.3999999761581421D;
						double b2 = targetedEntity.posY - 0.20000000298023224D
								- entityarrow.posY;
						float f1 = MathHelper.sqrt_double(b * b + b1 * b1) * 0.3F;
						
						this.worldObj.playSoundAtEntity(this, "mob.pirate.fire", 10.0F,
								(this.rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
						//if (!this.worldObj.isRemote)
						this.worldObj.spawnEntityInWorld(entityarrow);
						
						entityarrow.setThrowableHeading(b, b2 + (double) f1, b1, 1.25F, 12F);
						attackCounter = -80;	
				}
			} 			
			else if (attackCounter > 0) 
			{
				attackCounter--;
			}
		
		}
		else
        {
            renderYawOffset = this.rotationYaw = (-(float)Math.atan2(this.motionX, this.motionZ) * 180F) / 3.141593F;
            if(attackCounter > 0)
            {
                attackCounter--;
            }
        }

	}
	private boolean isCourseTraversable(double par1, double par3, double par5, double par7)
    {
        double var9 = (this.waypointX - this.posX) / par7;
        double var11 = (this.waypointY - this.posY) / par7;
        double var13 = (this.waypointZ - this.posZ) / par7;
        AxisAlignedBB var15 = this.boundingBox.copy();

        for (int var16 = 1; (double)var16 < par7; ++var16)
        {
            var15.offset(var9, var11, var13);

            if (!this.worldObj.getCollidingBoundingBoxes(this, var15).isEmpty())
            {
                return false;
            }
        }

        return true;
    }
	@Override
	protected String getLivingSound() {
		return "";
	}
	@Override
	protected String getHurtSound() {
		return "mob.pirate.blowmedown";
	}
	@Override
	protected String getDeathSound() {
		return "mob.pirate.avastyescurvydog";
	}
	@Override
	protected void dropFewItems(boolean par1, int par2) {
		if (par1){
		Random rand = new Random();
		if (rand.nextInt(100) < 5+par2) {
			dropItem(PChan3Mods.instance.engine.itemID, 1);
		} else if (rand.nextBoolean() == true) {
			dropItem(Item.arrow.itemID, 4);
		} else {
			dropItem(Item.leather.itemID, 4);
		}
		}
	}
	@Override
	protected int getDropItemId() {
		return Item.arrow.itemID;
	}
	@Override
	protected float getSoundVolume() {
		return 10F;
	}
	@Override
	public void setDead() {
		if (!this.worldObj.isRemote ){
			PChan3Mods.instance.proxy.displayExplodeFX(this);
		}
		super.setDead();
	}
	@Override
	public boolean getCanSpawnHere()
    {
        return this.rand.nextInt(15) == 0 && super.getCanSpawnHere() && this.worldObj.difficultySetting > 0;
    }
	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}
	@Override
	public int getMaxHealth() {
		return 10;
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLiving par1EntityLiving,float f) {
		EntityArrow arrow = new EntityArrow(this.worldObj, this, par1EntityLiving, 1.6F, (float)(14 - this.worldObj.difficultySetting * 4));
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
        int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
        int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem());
        arrow.setDamage((double)(f * 2.0F) + this.rand.nextGaussian() * 0.25D + (double)((float)this.worldObj.difficultySetting * 0.11F));
        if (i > 0)      
        	arrow.setDamage(arrow.getDamage() + (double)i * 0.5D + 0.5D);      
        if (j > 0)
        	arrow.setKnockbackStrength(j);
        if (k > 0)
        	arrow.setFire(100);

        this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(arrow);
	}
}
