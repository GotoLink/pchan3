package assets.pchan3.steamship;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCoal;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet39AttachEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import assets.pchan3.ItemAnchor;
import assets.pchan3.PChan3Mods;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityAirship extends Entity implements IInventory {
	private ItemStack cargoItems[];
	private int airshipPosRotationIncrements;
	private double airShipX, airShipY, airShipZ;
	private double airshipYaw, airshipPitch;
	public boolean isGoingUp, isGoingDown, isFiring;
	private boolean field_70279_a;
	@SideOnly(Side.CLIENT)
	private double velocityX;
	@SideOnly(Side.CLIENT)
	private double velocityY;
	@SideOnly(Side.CLIENT)
	private double velocityZ;
	public boolean isAnchor;
	public Entity thrower;
	private NBTTagCompound leash;

	public EntityAirship(World world) {
		super(world);
		this.field_70279_a = true;
		this.preventEntitySpawning = true;
		this.setSize(1.5F, 1.7F);
		this.yOffset = this.height / 2.0F;
		this.cargoItems = new ItemStack[this.getSizeInventory()];
	}

	public EntityAirship(World world, double d, double d1, double d2) {
		this(world);
		this.setPosition(d, d1 + yOffset, d2);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = d;
		this.prevPosY = d1;
		this.prevPosZ = d2;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float i) {
		if (this.isEntityInvulnerable()) {
			return false;
		} else if (!this.worldObj.isRemote && !this.isDead) {
			this.setForwardDirection(-this.getForwardDirection());
			this.setTimeSinceHit(2);
			this.setDamageTaken(this.getDamageTaken() + i * 10);
			this.setBeenAttacked();
			if (source.getEntity() instanceof EntityPlayer && ((EntityPlayer) source.getEntity()).capabilities.isCreativeMode) {
				this.setDamageTaken(200);
			}
			if (this.getDamageTaken() > 100) {
				if (this.riddenByEntity != null) {
					this.riddenByEntity.mountEntity(this);
				}
				this.dropItemWithOffset(PChan3Mods.airShip.itemID, 1, 0.0F);
				this.setDead();
			}
			return true;
		}
		if (this.worldObj.isRemote && this.isDead) {
			PChan3Mods.proxy.displayShipExplodeFX(source, this);
		}
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public void closeChest() {
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		ItemStack stack = this.getStackInSlot(i);
		if (stack != null) {
			if (stack.stackSize <= j) {
				this.setInventorySlotContents(i, (ItemStack) null);
				this.onInventoryChanged();
			} else {
				stack = stack.splitStack(j);
				if (stack.stackSize == 0) {
					this.setInventorySlotContents(i, (ItemStack) null);
					this.onInventoryChanged();
				}
			}
		}
		return stack;
	}

	public void fireArrow(EntityPlayer entityplayer) {
		boolean playerHasArrows = entityplayer.inventory.hasItem(Item.arrow.itemID) && PChan3Mods.usePlayerArrow;
		boolean shipHasArrows = this.getStackInSlot(1) != null && this.getStackInSlot(1).itemID == Item.arrow.itemID;
		if ((playerHasArrows || shipHasArrows || entityplayer.capabilities.isCreativeMode) && this.getFireCountDown() == 0) {
			Vec3 vec = entityplayer.getLookVec();
			double d8 = 4D;
			double d1 = this.posX + vec.xCoord * d8;
			double d2 = this.posY + height / 4.0F;
			double d3 = this.posZ + vec.zCoord * d8;
			EntityArrow arrow = new EntityArrow(this.worldObj, entityplayer, 1.0F);
			this.worldObj.playSoundAtEntity(entityplayer, "random.bow", 1.0F, 1.0F / (new Random().nextFloat() * 0.4F + 0.8F));
			arrow.setLocationAndAngles(d1, d2, d3, 2.6F, 6F);
			arrow.setDamage(1.0D);
			if (!this.worldObj.isRemote) {
				this.worldObj.spawnEntityInWorld(arrow);
				this.setFireCountDown(20);
			}
			if (!entityplayer.capabilities.isCreativeMode) {
				if (shipHasArrows) {
					if (--this.cargoItems[1].stackSize <= 0)
						this.setInventorySlotContents(1, null);
				} else if (playerHasArrows)
					entityplayer.inventory.consumeInventoryItem(Item.arrow.itemID);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void func_70270_d(boolean par1) {
		this.field_70279_a = par1;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity par1Entity) {
		return par1Entity.boundingBox;
	}

	public float getDamageTaken() {
		return this.dataWatcher.getWatchableObjectFloat(19);
	}

	public int getFireCountDown() {
		return this.dataWatcher.getWatchableObjectInt(31);
	}

	public int getForwardDirection() {
		return this.dataWatcher.getWatchableObjectInt(18);
	}

	public int getFuelScaled(int i) {
		return (this.getFuelTime() * i) / 600;
	}

	public int getFuelTime() {
		return this.dataWatcher.getWatchableObjectInt(30);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public String getInvName() {
		return "Airship";
	}

	@Override
	public double getMountedYOffset() {
		return this.height * 0.0D - 0.30000001192092896D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getShadowSize() {
		return 0.0F;
	}

	@Override
	public int getSizeInventory() {
		return 14;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return this.cargoItems[i];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	public int getTimeSinceHit() {
		return this.dataWatcher.getWatchableObjectInt(17);
	}

	@Override
	public boolean interactFirst(EntityPlayer entityplayer) {
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != entityplayer) {
			return true;
		} else if (!this.worldObj.isRemote) {
			ItemStack itemstack = entityplayer.inventory.getCurrentItem();
			if (itemstack != null) {
				if (itemstack.getItem() instanceof ItemCoal) {
					if (--itemstack.stackSize == 0) {
						entityplayer.destroyCurrentEquippedItem();
					}
					if (this.getFuelTime() == 0)
						this.setFuelTime(1600);
					else if (this.getStackInSlot(0) == null)
						this.setInventorySlotContents(0, new ItemStack(itemstack.getItem()));
					else if (this.getStackInSlot(0).itemID == Item.coal.itemID) {
						this.cargoItems[0].stackSize++;
						this.onInventoryChanged();
					}
					return false;
				} else if (itemstack.getItem() instanceof ItemAnchor) {
					if (!this.isAnchor) {
						this.setAnchor(entityplayer, true);
						if (!entityplayer.capabilities.isCreativeMode && --itemstack.stackSize == 0) {
							entityplayer.destroyCurrentEquippedItem();
						}
					} else if (this.thrower.entityId == entityplayer.entityId) {
						this.unsetAnchor(true, !entityplayer.capabilities.isCreativeMode);
					}
					return true;
				}
			}
			entityplayer.mountEntity(this);
			return true;
		}
		return false;
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return i > 2 || (itemstack.getItem() instanceof ItemCoal && i == 0) || (itemstack.itemID == Item.arrow.itemID && i == 1);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
		return this.isDead ? false : entityPlayer.getDistanceSqToEntity(this) <= 64.0D;
	}

	@Override
	public void onInventoryChanged() {
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.getFuelTime() > 0)
			this.setFuelTime(this.getFuelTime() - 1);
		if (this.getFireCountDown() > 0)
			this.setFireCountDown(this.getFireCountDown() - 1);
		if (this.getTimeSinceHit() > 0)
			this.setTimeSinceHit(this.getTimeSinceHit() - 1);
		if (this.getDamageTaken() > 0)
			this.setDamageTaken(this.getDamageTaken() - 1);
		if (this.getFuelTime() == 0 && this.riddenByEntity != null) {
			if (this.getStackInSlot(0) != null && this.getStackInSlot(0).itemID == Item.coal.itemID) {
				this.setFuelTime(1600);
				if (--this.cargoItems[0].stackSize <= 0)
					this.setInventorySlotContents(0, (ItemStack) null);
			} else if (PChan3Mods.usePlayerCoal && ((EntityPlayer) this.riddenByEntity).inventory.hasItem(Item.coal.itemID)) {
				this.setFuelTime(1600);
				((EntityPlayer) this.riddenByEntity).inventory.consumeInventoryItem(Item.coal.itemID);
			}
		}
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		int i = 5;
		for (int j = 0; j < i; j++) {
			double d5 = (this.boundingBox.minY + ((this.boundingBox.maxY - this.boundingBox.minY) * (j + 0)) / i) - 0.125D;
			double d9 = (this.boundingBox.minY + ((this.boundingBox.maxY - this.boundingBox.minY) * (j + 1)) / i) - 0.125D;
			AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(this.boundingBox.minX, d5, this.boundingBox.minZ, this.boundingBox.maxX, d9, this.boundingBox.maxZ);
			if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.water)) {
			}
		}
		double d3 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		double d1;
		if (this.worldObj.isRemote && this.field_70279_a) {
			if (this.airshipPosRotationIncrements > 0) {
				d1 = this.posX + (this.airShipX - this.posX) / this.airshipPosRotationIncrements;
				double d5 = this.posY + (this.airShipY - this.posY) / this.airshipPosRotationIncrements;
				double d9 = this.posZ + (this.airShipZ - this.posZ) / this.airshipPosRotationIncrements;
				double d12 = MathHelper.wrapAngleTo180_double(this.airshipYaw - this.rotationYaw);
				this.rotationYaw = (float) (this.rotationYaw + d12 / this.airshipPosRotationIncrements);
				this.rotationPitch = (float) (this.rotationPitch + (airshipPitch - rotationPitch) / this.airshipPosRotationIncrements);
				--this.airshipPosRotationIncrements;
				this.setPosition(d1, d5, d9);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			} else {
				d1 = this.posX + this.motionX;
				double d6 = this.posY + this.motionY;
				double d10 = this.posZ + this.motionZ;
				this.setPosition(d1, d6, d10);
				if (this.getFuelTime() == 0) {
					this.motionX *= 0.5D;
					this.motionY *= 0.5D;
					this.motionZ *= 0.5D;
				} else if (PChan3Mods.SHOW_BOILER) {
					PChan3Mods.proxy.displaySmoke(this);
				}
				if (d3 > 0.15D) {
					PChan3Mods.proxy.displaySplashEffect(this, d3);
				}
				this.motionX *= 0.99D;
				this.motionY *= 0.95D;
				this.motionZ *= 0.99D;
			}
		} else {
			double d4;
			double d5;
			if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase) {
				d4 = ((EntityLivingBase) this.riddenByEntity).moveForward;//>0 for forward key, <0 for backward
				d5 = ((EntityLivingBase) this.riddenByEntity).moveStrafing;//>0 for left key, <0 for right key
				double d0 = -Math.sin(this.riddenByEntity.rotationYaw * (float) Math.PI / 180.0F);
				double d11 = Math.cos(this.riddenByEntity.rotationYaw * (float) Math.PI / 180.0F);
				this.motionX += (d5 * d11 + d0 * d4) * PChan3Mods.airSpeed;
				this.motionZ += (d4 * d11 - d0 * d5) * PChan3Mods.airSpeed;
			}
			d4 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			if (d4 > 0.35D) {
				d5 = 0.35D / d4;
				this.motionX *= d5;
				this.motionZ *= d5;
			}
			if (this.isGoingUp) {
				this.motionY += PChan3Mods.airUpSpeed;
			} else if (this.isGoingDown) {
				for (int j = 0; j < i; j++) {
					double d41 = (this.boundingBox.minY + ((this.boundingBox.maxY - this.boundingBox.minY) * (j - 2)) / i) - 0.125D;
					double d8 = (this.boundingBox.minY + ((this.boundingBox.maxY - this.boundingBox.minY) * (j - 4)) / i) - 0.125D;
					AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(this.boundingBox.minX, d41, this.boundingBox.minZ, this.boundingBox.maxX, d8, this.boundingBox.maxZ);
					if (!this.worldObj.isAABBInMaterial(axisalignedbb, Material.water)) {
						this.motionY -= PChan3Mods.airDownSpeed;
					} else {
						this.posY += 5D;
						this.motionY = 0D;
					}
				}
			}
			if (this.getFuelTime() == 0 && !this.onGround) {
				this.motionY -= 0.01D * 10 / 15; // Gravity :P
			}
			double d7 = 1D;
			if (this.motionX < -d7)
				this.motionX = -d7;
			if (this.motionX > d7)
				this.motionX = d7;
			if (this.motionZ < -d7)
				this.motionZ = -d7;
			if (this.motionZ > d7)
				this.motionZ = d7;
			if (this.getFuelTime() == 0) {
				this.motionX *= 0.5D;
				this.motionY *= 0.5D;
				this.motionZ *= 0.5D;
			}
			double d11 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			if (!(isCollidedHorizontally && d11 > 0.15D)) {
				this.motionX *= 0.99D;
				this.motionY *= 0.95D;
				this.motionZ *= 0.99D;
			}
			moveEntity(this.motionX, this.motionY, this.motionZ);
			this.rotationPitch = 0.0F;
			double d14 = this.rotationYaw;
			double d16 = this.prevPosX - this.posX;
			double d17 = this.prevPosZ - this.posZ;
			if (d16 * d16 + d17 * d17 > 0.001D) {
				d14 = ((float) (Math.atan2(d17, d16) * 180D / Math.PI));
			}
			double d19 = MathHelper.wrapAngleTo180_double(d14 - this.rotationYaw);
			if (d19 > 20D) {
				d19 = 20D;
			}
			if (d19 < -20D) {
				d19 = -20D;
			}
			this.rotationYaw = (float) (this.rotationYaw + d19);
			setRotation(this.rotationYaw, this.rotationPitch);
			if (!this.worldObj.isRemote) {
				List<?> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.2D, 0.0D, 0.2D));
				if (list != null && list.size() > 0) {
					for (int j1 = 0; j1 < list.size(); j1++) {
						Entity entity = (Entity) list.get(j1);
						if (entity != this.riddenByEntity && entity.canBePushed() && (entity instanceof EntityAirship)) {
							entity.applyEntityCollision(this);
						}
					}
				}
				if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
					this.riddenByEntity = null;
				}
			}
			if (this.isFiring && this.getFireCountDown() == 0) {
				this.fireArrow((EntityPlayer) this.riddenByEntity);
			}
		}
		if (!this.worldObj.isRemote) {
			this.updateAnchor();
		}
	}

	@Override
	public void openChest() {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void performHurtAnimation() {
		this.setForwardDirection(-this.getForwardDirection());
		this.setTimeSinceHit(10);
		this.setDamageTaken(this.getDamageTaken() * 11.0F);
	}

	public void setAnchor(Entity par1Entity, boolean forcePacket) {
		this.isAnchor = true;
		this.thrower = par1Entity;
		if (!this.worldObj.isRemote && forcePacket && this.worldObj instanceof WorldServer) {
			((WorldServer) this.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(this, new Packet39AttachEntity(1, this, this.thrower));
		}
	}

	public void setDamageTaken(float par1) {
		this.dataWatcher.updateObject(19, Float.valueOf(par1));
	}

	@Override
	public void setDead() {
		for (int var1 = 0; var1 < this.getSizeInventory(); ++var1) {
			ItemStack var2 = this.getStackInSlot(var1);
			if (var2 != null) {
				float var3 = this.rand.nextFloat() * 0.8F + 0.1F;
				float var4 = this.rand.nextFloat() * 0.8F + 0.1F;
				float var5 = this.rand.nextFloat() * 0.8F + 0.1F;
				while (var2.stackSize > 0) {
					int var6 = this.rand.nextInt(21) + 10;
					if (var6 > var2.stackSize) {
						var6 = var2.stackSize;
					}
					var2.stackSize -= var6;
					EntityItem var7 = new EntityItem(this.worldObj, this.posX + var3, this.posY + var4, this.posZ + var5, new ItemStack(var2.itemID, var6, var2.getItemDamage()));
					if (var2.hasTagCompound()) {
						var7.getEntityItem().setTagCompound((NBTTagCompound) var2.getTagCompound().copy());
					}
					float var8 = 0.05F;
					var7.motionX = (float) this.rand.nextGaussian() * var8;
					var7.motionY = (float) this.rand.nextGaussian() * var8 + 0.2F;
					var7.motionZ = (float) this.rand.nextGaussian() * var8;
					if (!this.worldObj.isRemote)
						this.worldObj.spawnEntityInWorld(var7);
				}
			}
		}
		if (this.worldObj.isRemote)
			PChan3Mods.proxy.displayExplodeFX(this);
		super.setDead();
	}

	public void setFireCountDown(int par1) {
		this.dataWatcher.updateObject(31, Integer.valueOf(par1));
	}

	public void setForwardDirection(int par1) {
		this.dataWatcher.updateObject(18, Integer.valueOf(par1));
	}

	public void setFuelTime(int par1) {
		this.dataWatcher.updateObject(30, Integer.valueOf(par1));
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.cargoItems[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
			itemstack.stackSize = this.getInventoryStackLimit();
		this.onInventoryChanged();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float f, float f1, int i) {
		if (this.field_70279_a) {
			this.airshipPosRotationIncrements = i + 5;
		} else {
			double d3 = x - this.posX;
			double d4 = y - this.posY;
			double d5 = z - this.posZ;
			double d6 = d3 * d3 + d4 * d4 + d5 * d5;
			if (d6 <= 1.0D) {
				return;
			}
			this.airshipPosRotationIncrements = 3;
		}
		this.airShipX = x;
		this.airShipY = y;
		this.airShipZ = z;
		this.airshipYaw = f;
		this.airshipPitch = f1;
		this.motionX = this.velocityX;
		this.motionY = this.velocityY;
		this.motionZ = this.velocityZ;
	}

	public void setTimeSinceHit(int par1) {
		this.dataWatcher.updateObject(17, Integer.valueOf(par1));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setVelocity(double par1, double par3, double par5) {
		this.velocityX = this.motionX = par1;
		this.velocityY = this.motionY = par3;
		this.velocityZ = this.motionZ = par5;
	}

	public void unsetAnchor(boolean forcePacket, boolean forceDrop) {
		if (this.isAnchor) {
			this.isAnchor = false;
			this.thrower = null;
			if (!this.worldObj.isRemote && forceDrop) {
				this.dropItem(PChan3Mods.anchor.itemID, 1);
			}
			if (!this.worldObj.isRemote && forcePacket && this.worldObj instanceof WorldServer) {
				((WorldServer) this.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(this, new Packet39AttachEntity(1, this, (Entity) null));
			}
		}
	}

	@Override
	public void updateRiderPosition() {
		if (this.riddenByEntity != null) {
			double d0 = Math.cos(this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			double d1 = Math.sin(this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			this.riddenByEntity.setPosition(this.posX + d0, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + d1);
		}
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(17, new Integer(0));
		this.dataWatcher.addObject(18, new Integer(1));
		this.dataWatcher.addObject(19, new Float(0.0F));
		this.dataWatcher.addObject(30, new Integer(0));
		this.dataWatcher.addObject(31, new Integer(0));
	}

	@Override
	protected void fall(float par1) {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		NBTTagList itemTags = par1NBTTagCompound.getTagList("Items");
		this.cargoItems = new ItemStack[this.getSizeInventory()];
		for (int id = 0; id < itemTags.tagCount(); ++id) {
			NBTTagCompound var4 = (NBTTagCompound) itemTags.tagAt(id);
			int var5 = var4.getByte("Slot") & 255;
			if (var5 >= 0 && var5 < this.cargoItems.length) {
				this.cargoItems[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}
		this.isAnchor = par1NBTTagCompound.getBoolean("Leashed");
		if (this.isAnchor && par1NBTTagCompound.hasKey("Leash")) {
			this.leash = par1NBTTagCompound.getCompoundTag("Leash");
		}
	}

	protected void updateAnchor() {
		if (this.leash != null) {
			this.recreateAnchor();
		}
		if (this.isAnchor) {
			if (this.thrower == null || this.thrower.isDead) {
				this.unsetAnchor(true, true);
			}
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		NBTTagList tags = new NBTTagList();
		for (int id = 0; id < this.cargoItems.length; ++id) {
			if (this.cargoItems[id] != null) {
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) id);
				this.cargoItems[id].writeToNBT(var4);
				tags.appendTag(var4);
			}
		}
		par1NBTTagCompound.setTag("Items", tags);
		par1NBTTagCompound.setBoolean("Leashed", this.isAnchor);
		if (this.thrower != null) {
			par1NBTTagCompound = new NBTTagCompound("Leash");
			if (this.thrower instanceof EntityLivingBase) {
				par1NBTTagCompound.setLong("UUIDMost", this.thrower.getUniqueID().getMostSignificantBits());
				par1NBTTagCompound.setLong("UUIDLeast", this.thrower.getUniqueID().getLeastSignificantBits());
			} else if (this.thrower instanceof EntityHanging) {
				EntityHanging entityhanging = (EntityHanging) this.thrower;
				par1NBTTagCompound.setInteger("X", entityhanging.xPosition);
				par1NBTTagCompound.setInteger("Y", entityhanging.yPosition);
				par1NBTTagCompound.setInteger("Z", entityhanging.zPosition);
			}
			par1NBTTagCompound.setTag("Leash", par1NBTTagCompound);
		}
	}

	private void recreateAnchor() {
		if (this.isAnchor && this.leash != null) {
			if (this.leash.hasKey("UUIDMost") && this.leash.hasKey("UUIDLeast")) {
				UUID uuid = new UUID(this.leash.getLong("UUIDMost"), this.leash.getLong("UUIDLeast"));
				List list = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(10.0D, 10.0D, 10.0D));
				Iterator iterator = list.iterator();
				while (iterator.hasNext()) {
					EntityLivingBase entitylivingbase = (EntityLivingBase) iterator.next();
					if (entitylivingbase.getUniqueID().equals(uuid)) {
						this.thrower = entitylivingbase;
						break;
					}
				}
			} else if (this.leash.hasKey("X") && this.leash.hasKey("Y") && this.leash.hasKey("Z")) {
				int i = this.leash.getInteger("X");
				int j = this.leash.getInteger("Y");
				int k = this.leash.getInteger("Z");
				EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForBlock(this.worldObj, i, j, k);
				if (entityleashknot == null) {
					entityleashknot = EntityAnchor.specialSpawn(this.worldObj, i, j, k);
				}
				this.thrower = entityleashknot;
			} else {
				this.unsetAnchor(false, true);
			}
		}
		this.leash = null;
	}
}
