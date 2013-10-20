package assets.pchan3.pirate;

import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import assets.pchan3.PChan3Mods;

public class EntityPirate extends EntityFlying implements IMob, IRangedAttackMob {
	public int courseChangeCooldown = 0;
	public double waypointX, waypointY, waypointZ;
	private EntityLivingBase targetedEntity = null;
	private int aggroCooldown = 0;
	public int prevAttackCounter = 0, attackCounter = 0;
	public boolean playedWeigh = false, playedPrep = false;

	public EntityPirate(World world) {
		super(world);
		this.setSize(4F, 4F);
		this.isImmuneToFire = false;
		this.experienceValue = 5;
	}

	@Override
	protected void updateEntityActionState() {
		if (!this.worldObj.isRemote && this.worldObj.difficultySetting == 0) {
			this.setDead();
		}
		this.despawnEntity();
		prevAttackCounter = attackCounter;
		double d = this.waypointX - this.posX;
		double d1 = this.waypointY - this.posY;
		double d2 = this.waypointZ - this.posZ;
		double d3 = MathHelper.sqrt_double(d * d + d1 * d1 + d2 * d2);
		if (d3 < 1.0D || d3 > 60D) {
			this.waypointX = this.posX + (rand.nextFloat() * 2.0F - 1.0F) * 16F;
			this.waypointY = this.posY + (rand.nextFloat() * 2.0F - 1.0F) * 16F;
			this.waypointZ = this.posZ + (rand.nextFloat() * 2.0F - 1.0F) * 16F;
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
		if (targetedEntity != null && targetedEntity.getDistanceSqToEntity(this) < 64 * 64) {
			if (canEntityBeSeen(targetedEntity)) {
				if (attackCounter == 0 && playedWeigh == false) {
					this.worldObj.playSoundAtEntity(this, "mob.pirate.weighanchor", getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
					playedWeigh = true;
				}
				if (attackCounter == 5 && playedPrep == false) {
					this.worldObj.playSoundAtEntity(this, "mob.pirate.prepare", getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
					playedPrep = true;
				}
				attackCounter++;
				if (attackCounter >= 60) {
					/*
					 * double b = targetedEntity.posX - posX + 1.200D; double b1
					 * = targetedEntity.posZ - posZ + 1.200D; EntityArrow
					 * entityarrow = new EntityArrow(this.worldObj,this,1.0f);
					 * entityarrow.posY += 1.4D; double b2 = targetedEntity.posY
					 * - 0.2D - entityarrow.posY; float f1 =
					 * MathHelper.sqrt_double(b * b + b1 * b1) * 0.3F;
					 * this.worldObj.playSoundAtEntity(this, "mob.pirate.fire",
					 * 10.0F, (this.rand.nextFloat() - rand.nextFloat()) * 0.2F
					 * + 1.0F); //if (!this.worldObj.isRemote)
					 * this.worldObj.spawnEntityInWorld(entityarrow);
					 * entityarrow.setThrowableHeading(b, b2 + (double) f1, b1,
					 * 1.25F, 12F);
					 */
					float charge = this.rand.nextFloat();
					if (charge > 0.1F) {
						this.attackEntityWithRangedAttack(targetedEntity, charge);
						attackCounter = -80;
					}
				}
			} else if (attackCounter > 0) {
				attackCounter--;
			}
		} else {
			renderYawOffset = this.rotationYaw = (-(float) Math.atan2(this.motionX, this.motionZ) * 180F) / 3.141593F;
			if (attackCounter > 0) {
				attackCounter--;
			}
		}
	}

	private boolean isCourseTraversable(double par1, double par3, double par5, double par7) {
		double var9 = (this.waypointX - this.posX) / par7;
		double var11 = (this.waypointY - this.posY) / par7;
		double var13 = (this.waypointZ - this.posZ) / par7;
		AxisAlignedBB var15 = this.boundingBox.copy();
		for (int var16 = 1; var16 < par7; ++var16) {
			var15.offset(var9, var11, var13);
			if (!this.worldObj.getCollidingBoundingBoxes(this, var15).isEmpty()) {
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
	public boolean getCanSpawnHere() {
		return this.rand.nextInt(15) == 0 && super.getCanSpawnHere() && this.worldObj.difficultySetting > 0;
	}

	@Override
	protected void dropFewItems(boolean par1, int par2) {
		if (par1) {
			Random rand = new Random();
			if (rand.nextInt(100) < 5 + par2) {
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
		if (this.worldObj.isRemote) {
			PChan3Mods.instance.proxy.displayExplodeFX(this);
		}
		super.setDead();
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLiving, float f) {
		EntityArrow arrow = new EntityArrow(this.worldObj, this, par1EntityLiving, 1.6F, 14 - this.worldObj.difficultySetting * 4);
		int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
		int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
		int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem());
		arrow.setDamage(f * 2.0F + this.rand.nextGaussian() * 0.25D + this.worldObj.difficultySetting * 0.11F);
		if (i > 0)
			arrow.setDamage(arrow.getDamage() + i * 0.5D + 0.5D);
		if (j > 0)
			arrow.setKnockbackStrength(j);
		if (k > 0)
			arrow.setFire(100);
		this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
		this.worldObj.spawnEntityInWorld(arrow);
	}
}
