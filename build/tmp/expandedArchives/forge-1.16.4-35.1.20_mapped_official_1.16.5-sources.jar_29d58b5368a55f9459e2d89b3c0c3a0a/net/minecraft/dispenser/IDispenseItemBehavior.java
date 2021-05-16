package net.minecraft.dispenser;

import java.util.List;
import java.util.Random;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.block.WitherSkeletonSkullBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEquipable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public interface IDispenseItemBehavior {
   IDispenseItemBehavior NOOP = (p_210297_0_, p_210297_1_) -> {
      return p_210297_1_;
   };

   ItemStack dispense(IBlockSource p_dispense_1_, ItemStack p_dispense_2_);

   static void bootStrap() {
      DispenserBlock.registerBehavior(Items.ARROW, new ProjectileDispenseBehavior() {
         protected ProjectileEntity getProjectile(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
            ArrowEntity arrowentity = new ArrowEntity(p_82499_1_, p_82499_2_.x(), p_82499_2_.y(), p_82499_2_.z());
            arrowentity.pickup = AbstractArrowEntity.PickupStatus.ALLOWED;
            return arrowentity;
         }
      });
      DispenserBlock.registerBehavior(Items.TIPPED_ARROW, new ProjectileDispenseBehavior() {
         protected ProjectileEntity getProjectile(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
            ArrowEntity arrowentity = new ArrowEntity(p_82499_1_, p_82499_2_.x(), p_82499_2_.y(), p_82499_2_.z());
            arrowentity.setEffectsFromItem(p_82499_3_);
            arrowentity.pickup = AbstractArrowEntity.PickupStatus.ALLOWED;
            return arrowentity;
         }
      });
      DispenserBlock.registerBehavior(Items.SPECTRAL_ARROW, new ProjectileDispenseBehavior() {
         protected ProjectileEntity getProjectile(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
            AbstractArrowEntity abstractarrowentity = new SpectralArrowEntity(p_82499_1_, p_82499_2_.x(), p_82499_2_.y(), p_82499_2_.z());
            abstractarrowentity.pickup = AbstractArrowEntity.PickupStatus.ALLOWED;
            return abstractarrowentity;
         }
      });
      DispenserBlock.registerBehavior(Items.EGG, new ProjectileDispenseBehavior() {
         protected ProjectileEntity getProjectile(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
            return Util.make(new EggEntity(p_82499_1_, p_82499_2_.x(), p_82499_2_.y(), p_82499_2_.z()), (p_218408_1_) -> {
               p_218408_1_.setItem(p_82499_3_);
            });
         }
      });
      DispenserBlock.registerBehavior(Items.SNOWBALL, new ProjectileDispenseBehavior() {
         protected ProjectileEntity getProjectile(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
            return Util.make(new SnowballEntity(p_82499_1_, p_82499_2_.x(), p_82499_2_.y(), p_82499_2_.z()), (p_218409_1_) -> {
               p_218409_1_.setItem(p_82499_3_);
            });
         }
      });
      DispenserBlock.registerBehavior(Items.EXPERIENCE_BOTTLE, new ProjectileDispenseBehavior() {
         protected ProjectileEntity getProjectile(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
            return Util.make(new ExperienceBottleEntity(p_82499_1_, p_82499_2_.x(), p_82499_2_.y(), p_82499_2_.z()), (p_218410_1_) -> {
               p_218410_1_.setItem(p_82499_3_);
            });
         }

         protected float getUncertainty() {
            return super.getUncertainty() * 0.5F;
         }

         protected float getPower() {
            return super.getPower() * 1.25F;
         }
      });
      DispenserBlock.registerBehavior(Items.SPLASH_POTION, new IDispenseItemBehavior() {
         public ItemStack dispense(IBlockSource p_dispense_1_, ItemStack p_dispense_2_) {
            return (new ProjectileDispenseBehavior() {
               protected ProjectileEntity getProjectile(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
                  return Util.make(new PotionEntity(p_82499_1_, p_82499_2_.x(), p_82499_2_.y(), p_82499_2_.z()), (p_218411_1_) -> {
                     p_218411_1_.setItem(p_82499_3_);
                  });
               }

               protected float getUncertainty() {
                  return super.getUncertainty() * 0.5F;
               }

               protected float getPower() {
                  return super.getPower() * 1.25F;
               }
            }).dispense(p_dispense_1_, p_dispense_2_);
         }
      });
      DispenserBlock.registerBehavior(Items.LINGERING_POTION, new IDispenseItemBehavior() {
         public ItemStack dispense(IBlockSource p_dispense_1_, ItemStack p_dispense_2_) {
            return (new ProjectileDispenseBehavior() {
               protected ProjectileEntity getProjectile(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_) {
                  return Util.make(new PotionEntity(p_82499_1_, p_82499_2_.x(), p_82499_2_.y(), p_82499_2_.z()), (p_218413_1_) -> {
                     p_218413_1_.setItem(p_82499_3_);
                  });
               }

               protected float getUncertainty() {
                  return super.getUncertainty() * 0.5F;
               }

               protected float getPower() {
                  return super.getPower() * 1.25F;
               }
            }).dispense(p_dispense_1_, p_dispense_2_);
         }
      });
      DefaultDispenseItemBehavior defaultdispenseitembehavior = new DefaultDispenseItemBehavior() {
         public ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            Direction direction = p_82487_1_.getBlockState().getValue(DispenserBlock.FACING);
            EntityType<?> entitytype = ((SpawnEggItem)p_82487_2_.getItem()).getType(p_82487_2_.getTag());
            entitytype.spawn(p_82487_1_.getLevel(), p_82487_2_, (PlayerEntity)null, p_82487_1_.getPos().relative(direction), SpawnReason.DISPENSER, direction != Direction.UP, false);
            p_82487_2_.shrink(1);
            return p_82487_2_;
         }
      };

      for(SpawnEggItem spawneggitem : SpawnEggItem.eggs()) {
         DispenserBlock.registerBehavior(spawneggitem, defaultdispenseitembehavior);
      }

      DispenserBlock.registerBehavior(Items.ARMOR_STAND, new DefaultDispenseItemBehavior() {
         public ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            Direction direction = p_82487_1_.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos blockpos = p_82487_1_.getPos().relative(direction);
            World world = p_82487_1_.getLevel();
            ArmorStandEntity armorstandentity = new ArmorStandEntity(world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D);
            EntityType.updateCustomEntityTag(world, (PlayerEntity)null, armorstandentity, p_82487_2_.getTag());
            armorstandentity.yRot = direction.toYRot();
            world.addFreshEntity(armorstandentity);
            p_82487_2_.shrink(1);
            return p_82487_2_;
         }
      });
      DispenserBlock.registerBehavior(Items.SADDLE, new OptionalDispenseBehavior() {
         public ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            BlockPos blockpos = p_82487_1_.getPos().relative(p_82487_1_.getBlockState().getValue(DispenserBlock.FACING));
            List<LivingEntity> list = p_82487_1_.getLevel().getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(blockpos), (p_239789_0_) -> {
               if (!(p_239789_0_ instanceof IEquipable)) {
                  return false;
               } else {
                  IEquipable iequipable = (IEquipable)p_239789_0_;
                  return !iequipable.isSaddled() && iequipable.isSaddleable();
               }
            });
            if (!list.isEmpty()) {
               ((IEquipable)list.get(0)).equipSaddle(SoundCategory.BLOCKS);
               p_82487_2_.shrink(1);
               this.setSuccess(true);
               return p_82487_2_;
            } else {
               return super.execute(p_82487_1_, p_82487_2_);
            }
         }
      });
      DefaultDispenseItemBehavior defaultdispenseitembehavior1 = new OptionalDispenseBehavior() {
         protected ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            BlockPos blockpos = p_82487_1_.getPos().relative(p_82487_1_.getBlockState().getValue(DispenserBlock.FACING));

            for(AbstractHorseEntity abstracthorseentity : p_82487_1_.getLevel().getEntitiesOfClass(AbstractHorseEntity.class, new AxisAlignedBB(blockpos), (p_239790_0_) -> {
               return p_239790_0_.isAlive() && p_239790_0_.canWearArmor();
            })) {
               if (abstracthorseentity.isArmor(p_82487_2_) && !abstracthorseentity.isWearingArmor() && abstracthorseentity.isTamed()) {
                  abstracthorseentity.setSlot(401, p_82487_2_.split(1));
                  this.setSuccess(true);
                  return p_82487_2_;
               }
            }

            return super.execute(p_82487_1_, p_82487_2_);
         }
      };
      DispenserBlock.registerBehavior(Items.LEATHER_HORSE_ARMOR, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.IRON_HORSE_ARMOR, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.GOLDEN_HORSE_ARMOR, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.DIAMOND_HORSE_ARMOR, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.WHITE_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.ORANGE_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.CYAN_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.BLUE_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.BROWN_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.BLACK_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.GRAY_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.GREEN_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.LIGHT_BLUE_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.LIGHT_GRAY_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.LIME_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.MAGENTA_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.PINK_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.PURPLE_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.RED_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.YELLOW_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.CHEST, new OptionalDispenseBehavior() {
         public ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            BlockPos blockpos = p_82487_1_.getPos().relative(p_82487_1_.getBlockState().getValue(DispenserBlock.FACING));

            for(AbstractChestedHorseEntity abstractchestedhorseentity : p_82487_1_.getLevel().getEntitiesOfClass(AbstractChestedHorseEntity.class, new AxisAlignedBB(blockpos), (p_239791_0_) -> {
               return p_239791_0_.isAlive() && !p_239791_0_.hasChest();
            })) {
               if (abstractchestedhorseentity.isTamed() && abstractchestedhorseentity.setSlot(499, p_82487_2_)) {
                  p_82487_2_.shrink(1);
                  this.setSuccess(true);
                  return p_82487_2_;
               }
            }

            return super.execute(p_82487_1_, p_82487_2_);
         }
      });
      DispenserBlock.registerBehavior(Items.FIREWORK_ROCKET, new DefaultDispenseItemBehavior() {
         public ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            Direction direction = p_82487_1_.getBlockState().getValue(DispenserBlock.FACING);
            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(p_82487_1_.getLevel(), p_82487_2_, p_82487_1_.x(), p_82487_1_.y(), p_82487_1_.x(), true);
            IDispenseItemBehavior.setEntityPokingOutOfBlock(p_82487_1_, fireworkrocketentity, direction);
            fireworkrocketentity.shoot((double)direction.getStepX(), (double)direction.getStepY(), (double)direction.getStepZ(), 0.5F, 1.0F);
            p_82487_1_.getLevel().addFreshEntity(fireworkrocketentity);
            p_82487_2_.shrink(1);
            return p_82487_2_;
         }

         protected void playSound(IBlockSource p_82485_1_) {
            p_82485_1_.getLevel().levelEvent(1004, p_82485_1_.getPos(), 0);
         }
      });
      DispenserBlock.registerBehavior(Items.FIRE_CHARGE, new DefaultDispenseItemBehavior() {
         public ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            Direction direction = p_82487_1_.getBlockState().getValue(DispenserBlock.FACING);
            IPosition iposition = DispenserBlock.getDispensePosition(p_82487_1_);
            double d0 = iposition.x() + (double)((float)direction.getStepX() * 0.3F);
            double d1 = iposition.y() + (double)((float)direction.getStepY() * 0.3F);
            double d2 = iposition.z() + (double)((float)direction.getStepZ() * 0.3F);
            World world = p_82487_1_.getLevel();
            Random random = world.random;
            double d3 = random.nextGaussian() * 0.05D + (double)direction.getStepX();
            double d4 = random.nextGaussian() * 0.05D + (double)direction.getStepY();
            double d5 = random.nextGaussian() * 0.05D + (double)direction.getStepZ();
            world.addFreshEntity(Util.make(new SmallFireballEntity(world, d0, d1, d2, d3, d4, d5), (p_229425_1_) -> {
               p_229425_1_.setItem(p_82487_2_);
            }));
            p_82487_2_.shrink(1);
            return p_82487_2_;
         }

         protected void playSound(IBlockSource p_82485_1_) {
            p_82485_1_.getLevel().levelEvent(1018, p_82485_1_.getPos(), 0);
         }
      });
      DispenserBlock.registerBehavior(Items.OAK_BOAT, new DispenseBoatBehavior(BoatEntity.Type.OAK));
      DispenserBlock.registerBehavior(Items.SPRUCE_BOAT, new DispenseBoatBehavior(BoatEntity.Type.SPRUCE));
      DispenserBlock.registerBehavior(Items.BIRCH_BOAT, new DispenseBoatBehavior(BoatEntity.Type.BIRCH));
      DispenserBlock.registerBehavior(Items.JUNGLE_BOAT, new DispenseBoatBehavior(BoatEntity.Type.JUNGLE));
      DispenserBlock.registerBehavior(Items.DARK_OAK_BOAT, new DispenseBoatBehavior(BoatEntity.Type.DARK_OAK));
      DispenserBlock.registerBehavior(Items.ACACIA_BOAT, new DispenseBoatBehavior(BoatEntity.Type.ACACIA));
      IDispenseItemBehavior idispenseitembehavior1 = new DefaultDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

         public ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            BucketItem bucketitem = (BucketItem)p_82487_2_.getItem();
            BlockPos blockpos = p_82487_1_.getPos().relative(p_82487_1_.getBlockState().getValue(DispenserBlock.FACING));
            World world = p_82487_1_.getLevel();
            if (bucketitem.emptyBucket((PlayerEntity)null, world, blockpos, (BlockRayTraceResult)null)) {
               bucketitem.checkExtraContent(world, p_82487_2_, blockpos);
               return new ItemStack(Items.BUCKET);
            } else {
               return this.defaultDispenseItemBehavior.dispense(p_82487_1_, p_82487_2_);
            }
         }
      };
      DispenserBlock.registerBehavior(Items.LAVA_BUCKET, idispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.WATER_BUCKET, idispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.SALMON_BUCKET, idispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.COD_BUCKET, idispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.PUFFERFISH_BUCKET, idispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.TROPICAL_FISH_BUCKET, idispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.BUCKET, new DefaultDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

         public ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            IWorld iworld = p_82487_1_.getLevel();
            BlockPos blockpos = p_82487_1_.getPos().relative(p_82487_1_.getBlockState().getValue(DispenserBlock.FACING));
            BlockState blockstate = iworld.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (block instanceof IBucketPickupHandler) {
               Fluid fluid = ((IBucketPickupHandler)block).takeLiquid(iworld, blockpos, blockstate);
               if (!(fluid instanceof FlowingFluid)) {
                  return super.execute(p_82487_1_, p_82487_2_);
               } else {
                  Item item = fluid.getBucket();
                  p_82487_2_.shrink(1);
                  if (p_82487_2_.isEmpty()) {
                     return new ItemStack(item);
                  } else {
                     if (p_82487_1_.<DispenserTileEntity>getEntity().addItem(new ItemStack(item)) < 0) {
                        this.defaultDispenseItemBehavior.dispense(p_82487_1_, new ItemStack(item));
                     }

                     return p_82487_2_;
                  }
               }
            } else {
               return super.execute(p_82487_1_, p_82487_2_);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new OptionalDispenseBehavior() {
         protected ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            World world = p_82487_1_.getLevel();
            this.setSuccess(true);
            Direction direction = p_82487_1_.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos blockpos = p_82487_1_.getPos().relative(direction);
            BlockState blockstate = world.getBlockState(blockpos);
            if (AbstractFireBlock.canBePlacedAt(world, blockpos, direction)) {
               world.setBlockAndUpdate(blockpos, AbstractFireBlock.getState(world, blockpos));
            } else if (CampfireBlock.canLight(blockstate)) {
               world.setBlockAndUpdate(blockpos, blockstate.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)));
            } else if (blockstate.isFlammable(world, blockpos, p_82487_1_.getBlockState().getValue(DispenserBlock.FACING).getOpposite())) {
               blockstate.catchFire(world, blockpos, p_82487_1_.getBlockState().getValue(DispenserBlock.FACING).getOpposite(), null);
               if (blockstate.getBlock() instanceof TNTBlock)
               world.removeBlock(blockpos, false);
            } else {
               this.setSuccess(false);
            }

            if (this.isSuccess() && p_82487_2_.hurt(1, world.random, (ServerPlayerEntity)null)) {
               p_82487_2_.setCount(0);
            }

            return p_82487_2_;
         }
      });
      DispenserBlock.registerBehavior(Items.BONE_MEAL, new OptionalDispenseBehavior() {
         protected ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            this.setSuccess(true);
            World world = p_82487_1_.getLevel();
            BlockPos blockpos = p_82487_1_.getPos().relative(p_82487_1_.getBlockState().getValue(DispenserBlock.FACING));
            if (!BoneMealItem.growCrop(p_82487_2_, world, blockpos) && !BoneMealItem.growWaterPlant(p_82487_2_, world, blockpos, (Direction)null)) {
               this.setSuccess(false);
            } else if (!world.isClientSide) {
               world.levelEvent(2005, blockpos, 0);
            }

            return p_82487_2_;
         }
      });
      DispenserBlock.registerBehavior(Blocks.TNT, new DefaultDispenseItemBehavior() {
         protected ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            World world = p_82487_1_.getLevel();
            BlockPos blockpos = p_82487_1_.getPos().relative(p_82487_1_.getBlockState().getValue(DispenserBlock.FACING));
            TNTEntity tntentity = new TNTEntity(world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D, (LivingEntity)null);
            world.addFreshEntity(tntentity);
            world.playSound((PlayerEntity)null, tntentity.getX(), tntentity.getY(), tntentity.getZ(), SoundEvents.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            p_82487_2_.shrink(1);
            return p_82487_2_;
         }
      });
      IDispenseItemBehavior idispenseitembehavior = new OptionalDispenseBehavior() {
         protected ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            this.setSuccess(ArmorItem.dispenseArmor(p_82487_1_, p_82487_2_));
            return p_82487_2_;
         }
      };
      DispenserBlock.registerBehavior(Items.CREEPER_HEAD, idispenseitembehavior);
      DispenserBlock.registerBehavior(Items.ZOMBIE_HEAD, idispenseitembehavior);
      DispenserBlock.registerBehavior(Items.DRAGON_HEAD, idispenseitembehavior);
      DispenserBlock.registerBehavior(Items.SKELETON_SKULL, idispenseitembehavior);
      DispenserBlock.registerBehavior(Items.PLAYER_HEAD, idispenseitembehavior);
      DispenserBlock.registerBehavior(Items.WITHER_SKELETON_SKULL, new OptionalDispenseBehavior() {
         protected ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            World world = p_82487_1_.getLevel();
            Direction direction = p_82487_1_.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos blockpos = p_82487_1_.getPos().relative(direction);
            if (world.isEmptyBlock(blockpos) && WitherSkeletonSkullBlock.canSpawnMob(world, blockpos, p_82487_2_)) {
               world.setBlock(blockpos, Blocks.WITHER_SKELETON_SKULL.defaultBlockState().setValue(SkullBlock.ROTATION, Integer.valueOf(direction.getAxis() == Direction.Axis.Y ? 0 : direction.getOpposite().get2DDataValue() * 4)), 3);
               TileEntity tileentity = world.getBlockEntity(blockpos);
               if (tileentity instanceof SkullTileEntity) {
                  WitherSkeletonSkullBlock.checkSpawn(world, blockpos, (SkullTileEntity)tileentity);
               }

               p_82487_2_.shrink(1);
               this.setSuccess(true);
            } else {
               this.setSuccess(ArmorItem.dispenseArmor(p_82487_1_, p_82487_2_));
            }

            return p_82487_2_;
         }
      });
      DispenserBlock.registerBehavior(Blocks.CARVED_PUMPKIN, new OptionalDispenseBehavior() {
         protected ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            World world = p_82487_1_.getLevel();
            BlockPos blockpos = p_82487_1_.getPos().relative(p_82487_1_.getBlockState().getValue(DispenserBlock.FACING));
            CarvedPumpkinBlock carvedpumpkinblock = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
            if (world.isEmptyBlock(blockpos) && carvedpumpkinblock.canSpawnGolem(world, blockpos)) {
               if (!world.isClientSide) {
                  world.setBlock(blockpos, carvedpumpkinblock.defaultBlockState(), 3);
               }

               p_82487_2_.shrink(1);
               this.setSuccess(true);
            } else {
               this.setSuccess(ArmorItem.dispenseArmor(p_82487_1_, p_82487_2_));
            }

            return p_82487_2_;
         }
      });
      DispenserBlock.registerBehavior(Blocks.SHULKER_BOX.asItem(), new ShulkerBoxDispenseBehavior());

      for(DyeColor dyecolor : DyeColor.values()) {
         DispenserBlock.registerBehavior(ShulkerBoxBlock.getBlockByColor(dyecolor).asItem(), new ShulkerBoxDispenseBehavior());
      }

      DispenserBlock.registerBehavior(Items.GLASS_BOTTLE.asItem(), new OptionalDispenseBehavior() {
         private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

         private ItemStack takeLiquid(IBlockSource p_229424_1_, ItemStack p_229424_2_, ItemStack p_229424_3_) {
            p_229424_2_.shrink(1);
            if (p_229424_2_.isEmpty()) {
               return p_229424_3_.copy();
            } else {
               if (p_229424_1_.<DispenserTileEntity>getEntity().addItem(p_229424_3_.copy()) < 0) {
                  this.defaultDispenseItemBehavior.dispense(p_229424_1_, p_229424_3_.copy());
               }

               return p_229424_2_;
            }
         }

         public ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            this.setSuccess(false);
            ServerWorld serverworld = p_82487_1_.getLevel();
            BlockPos blockpos = p_82487_1_.getPos().relative(p_82487_1_.getBlockState().getValue(DispenserBlock.FACING));
            BlockState blockstate = serverworld.getBlockState(blockpos);
            if (blockstate.is(BlockTags.BEEHIVES, (p_239787_0_) -> {
               return p_239787_0_.hasProperty(BeehiveBlock.HONEY_LEVEL);
            }) && blockstate.getValue(BeehiveBlock.HONEY_LEVEL) >= 5) {
               ((BeehiveBlock)blockstate.getBlock()).releaseBeesAndResetHoneyLevel(serverworld, blockstate, blockpos, (PlayerEntity)null, BeehiveTileEntity.State.BEE_RELEASED);
               this.setSuccess(true);
               return this.takeLiquid(p_82487_1_, p_82487_2_, new ItemStack(Items.HONEY_BOTTLE));
            } else if (serverworld.getFluidState(blockpos).is(FluidTags.WATER)) {
               this.setSuccess(true);
               return this.takeLiquid(p_82487_1_, p_82487_2_, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
            } else {
               return super.execute(p_82487_1_, p_82487_2_);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.GLOWSTONE, new OptionalDispenseBehavior() {
         public ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
            Direction direction = p_82487_1_.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos blockpos = p_82487_1_.getPos().relative(direction);
            World world = p_82487_1_.getLevel();
            BlockState blockstate = world.getBlockState(blockpos);
            this.setSuccess(true);
            if (blockstate.is(Blocks.RESPAWN_ANCHOR)) {
               if (blockstate.getValue(RespawnAnchorBlock.CHARGE) != 4) {
                  RespawnAnchorBlock.charge(world, blockpos, blockstate);
                  p_82487_2_.shrink(1);
               } else {
                  this.setSuccess(false);
               }

               return p_82487_2_;
            } else {
               return super.execute(p_82487_1_, p_82487_2_);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new BeehiveDispenseBehavior());
   }

   static void setEntityPokingOutOfBlock(IBlockSource p_239785_0_, Entity p_239785_1_, Direction p_239785_2_) {
      p_239785_1_.setPos(p_239785_0_.x() + (double)p_239785_2_.getStepX() * (0.5000099999997474D - (double)p_239785_1_.getBbWidth() / 2.0D), p_239785_0_.y() + (double)p_239785_2_.getStepY() * (0.5000099999997474D - (double)p_239785_1_.getBbHeight() / 2.0D) - (double)p_239785_1_.getBbHeight() / 2.0D, p_239785_0_.z() + (double)p_239785_2_.getStepZ() * (0.5000099999997474D - (double)p_239785_1_.getBbWidth() / 2.0D));
   }
}
