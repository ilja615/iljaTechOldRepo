package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.HandSide;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemModelsProperties {
   private static final Map<ResourceLocation, IItemPropertyGetter> GENERIC_PROPERTIES = Maps.newHashMap();
   private static final ResourceLocation DAMAGED = new ResourceLocation("damaged");
   private static final ResourceLocation DAMAGE = new ResourceLocation("damage");
   private static final IItemPropertyGetter PROPERTY_DAMAGED = (p_239434_0_, p_239434_1_, p_239434_2_) -> {
      return p_239434_0_.isDamaged() ? 1.0F : 0.0F;
   };
   private static final IItemPropertyGetter PROPERTY_DAMAGE = (p_239433_0_, p_239433_1_, p_239433_2_) -> {
      return MathHelper.clamp((float)p_239433_0_.getDamageValue() / (float)p_239433_0_.getMaxDamage(), 0.0F, 1.0F);
   };
   private static final Map<Item, Map<ResourceLocation, IItemPropertyGetter>> PROPERTIES = Maps.newHashMap();

   private static IItemPropertyGetter registerGeneric(ResourceLocation p_239420_0_, IItemPropertyGetter p_239420_1_) {
      GENERIC_PROPERTIES.put(p_239420_0_, p_239420_1_);
      return p_239420_1_;
   }

   public static void register(Item p_239418_0_, ResourceLocation p_239418_1_, IItemPropertyGetter p_239418_2_) {
      PROPERTIES.computeIfAbsent(p_239418_0_, (p_239416_0_) -> {
         return Maps.newHashMap();
      }).put(p_239418_1_, p_239418_2_);
   }

   @Nullable
   public static IItemPropertyGetter getProperty(Item p_239417_0_, ResourceLocation p_239417_1_) {
      if (p_239417_0_.getMaxDamage() > 0) {
         if (DAMAGE.equals(p_239417_1_)) {
            return PROPERTY_DAMAGE;
         }

         if (DAMAGED.equals(p_239417_1_)) {
            return PROPERTY_DAMAGED;
         }
      }

      IItemPropertyGetter iitempropertygetter = GENERIC_PROPERTIES.get(p_239417_1_);
      if (iitempropertygetter != null) {
         return iitempropertygetter;
      } else {
         Map<ResourceLocation, IItemPropertyGetter> map = PROPERTIES.get(p_239417_0_);
         return map == null ? null : map.get(p_239417_1_);
      }
   }

   static {
      registerGeneric(new ResourceLocation("lefthanded"), (p_239432_0_, p_239432_1_, p_239432_2_) -> {
         return p_239432_2_ != null && p_239432_2_.getMainArm() != HandSide.RIGHT ? 1.0F : 0.0F;
      });
      registerGeneric(new ResourceLocation("cooldown"), (p_239431_0_, p_239431_1_, p_239431_2_) -> {
         return p_239431_2_ instanceof PlayerEntity ? ((PlayerEntity)p_239431_2_).getCooldowns().getCooldownPercent(p_239431_0_.getItem(), 0.0F) : 0.0F;
      });
      registerGeneric(new ResourceLocation("custom_model_data"), (p_239430_0_, p_239430_1_, p_239430_2_) -> {
         return p_239430_0_.hasTag() ? (float)p_239430_0_.getTag().getInt("CustomModelData") : 0.0F;
      });
      register(Items.BOW, new ResourceLocation("pull"), (p_239429_0_, p_239429_1_, p_239429_2_) -> {
         if (p_239429_2_ == null) {
            return 0.0F;
         } else {
            return p_239429_2_.getUseItem() != p_239429_0_ ? 0.0F : (float)(p_239429_0_.getUseDuration() - p_239429_2_.getUseItemRemainingTicks()) / 20.0F;
         }
      });
      register(Items.BOW, new ResourceLocation("pulling"), (p_239428_0_, p_239428_1_, p_239428_2_) -> {
         return p_239428_2_ != null && p_239428_2_.isUsingItem() && p_239428_2_.getUseItem() == p_239428_0_ ? 1.0F : 0.0F;
      });
      register(Items.CLOCK, new ResourceLocation("time"), new IItemPropertyGetter() {
         private double rotation;
         private double rota;
         private long lastUpdateTick;

         public float call(ItemStack p_call_1_, @Nullable ClientWorld p_call_2_, @Nullable LivingEntity p_call_3_) {
            Entity entity = (Entity)(p_call_3_ != null ? p_call_3_ : p_call_1_.getEntityRepresentation());
            if (entity == null) {
               return 0.0F;
            } else {
               if (p_call_2_ == null && entity.level instanceof ClientWorld) {
                  p_call_2_ = (ClientWorld)entity.level;
               }

               if (p_call_2_ == null) {
                  return 0.0F;
               } else {
                  double d0;
                  if (p_call_2_.dimensionType().natural()) {
                     d0 = (double)p_call_2_.getTimeOfDay(1.0F);
                  } else {
                     d0 = Math.random();
                  }

                  d0 = this.wobble(p_call_2_, d0);
                  return (float)d0;
               }
            }
         }

         private double wobble(World p_239438_1_, double p_239438_2_) {
            if (p_239438_1_.getGameTime() != this.lastUpdateTick) {
               this.lastUpdateTick = p_239438_1_.getGameTime();
               double d0 = p_239438_2_ - this.rotation;
               d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
               this.rota += d0 * 0.1D;
               this.rota *= 0.9D;
               this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
            }

            return this.rotation;
         }
      });
      register(Items.COMPASS, new ResourceLocation("angle"), new IItemPropertyGetter() {
         private final ItemModelsProperties.Angle wobble = new ItemModelsProperties.Angle();
         private final ItemModelsProperties.Angle wobbleRandom = new ItemModelsProperties.Angle();

         public float call(ItemStack p_call_1_, @Nullable ClientWorld p_call_2_, @Nullable LivingEntity p_call_3_) {
            Entity entity = (Entity)(p_call_3_ != null ? p_call_3_ : p_call_1_.getEntityRepresentation());
            if (entity == null) {
               return 0.0F;
            } else {
               if (p_call_2_ == null && entity.level instanceof ClientWorld) {
                  p_call_2_ = (ClientWorld)entity.level;
               }

               BlockPos blockpos = CompassItem.isLodestoneCompass(p_call_1_) ? this.getLodestonePosition(p_call_2_, p_call_1_.getOrCreateTag()) : this.getSpawnPosition(p_call_2_);
               long i = p_call_2_.getGameTime();
               if (blockpos != null && !(entity.position().distanceToSqr((double)blockpos.getX() + 0.5D, entity.position().y(), (double)blockpos.getZ() + 0.5D) < (double)1.0E-5F)) {
                  boolean flag = p_call_3_ instanceof PlayerEntity && ((PlayerEntity)p_call_3_).isLocalPlayer();
                  double d1 = 0.0D;
                  if (flag) {
                     d1 = (double)p_call_3_.yRot;
                  } else if (entity instanceof ItemFrameEntity) {
                     d1 = this.getFrameRotation((ItemFrameEntity)entity);
                  } else if (entity instanceof ItemEntity) {
                     d1 = (double)(180.0F - ((ItemEntity)entity).getSpin(0.5F) / ((float)Math.PI * 2F) * 360.0F);
                  } else if (p_call_3_ != null) {
                     d1 = (double)p_call_3_.yBodyRot;
                  }

                  d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
                  double d2 = this.getAngleTo(Vector3d.atCenterOf(blockpos), entity) / (double)((float)Math.PI * 2F);
                  double d3;
                  if (flag) {
                     if (this.wobble.shouldUpdate(i)) {
                        this.wobble.update(i, 0.5D - (d1 - 0.25D));
                     }

                     d3 = d2 + this.wobble.rotation;
                  } else {
                     d3 = 0.5D - (d1 - 0.25D - d2);
                  }

                  return MathHelper.positiveModulo((float)d3, 1.0F);
               } else {
                  if (this.wobbleRandom.shouldUpdate(i)) {
                     this.wobbleRandom.update(i, Math.random());
                  }

                  double d0 = this.wobbleRandom.rotation + (double)((float)p_call_1_.hashCode() / 2.14748365E9F);
                  return MathHelper.positiveModulo((float)d0, 1.0F);
               }
            }
         }

         @Nullable
         private BlockPos getSpawnPosition(ClientWorld p_239444_1_) {
            return p_239444_1_.dimensionType().natural() ? p_239444_1_.getSharedSpawnPos() : null;
         }

         @Nullable
         private BlockPos getLodestonePosition(World p_239442_1_, CompoundNBT p_239442_2_) {
            boolean flag = p_239442_2_.contains("LodestonePos");
            boolean flag1 = p_239442_2_.contains("LodestoneDimension");
            if (flag && flag1) {
               Optional<RegistryKey<World>> optional = CompassItem.getLodestoneDimension(p_239442_2_);
               if (optional.isPresent() && p_239442_1_.dimension() == optional.get()) {
                  return NBTUtil.readBlockPos(p_239442_2_.getCompound("LodestonePos"));
               }
            }

            return null;
         }

         private double getFrameRotation(ItemFrameEntity p_239441_1_) {
            Direction direction = p_239441_1_.getDirection();
            int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
            return (double)MathHelper.wrapDegrees(180 + direction.get2DDataValue() * 90 + p_239441_1_.getRotation() * 45 + i);
         }

         private double getAngleTo(Vector3d p_239443_1_, Entity p_239443_2_) {
            return Math.atan2(p_239443_1_.z() - p_239443_2_.getZ(), p_239443_1_.x() - p_239443_2_.getX());
         }
      });
      register(Items.CROSSBOW, new ResourceLocation("pull"), (p_239427_0_, p_239427_1_, p_239427_2_) -> {
         if (p_239427_2_ == null) {
            return 0.0F;
         } else {
            return CrossbowItem.isCharged(p_239427_0_) ? 0.0F : (float)(p_239427_0_.getUseDuration() - p_239427_2_.getUseItemRemainingTicks()) / (float)CrossbowItem.getChargeDuration(p_239427_0_);
         }
      });
      register(Items.CROSSBOW, new ResourceLocation("pulling"), (p_239426_0_, p_239426_1_, p_239426_2_) -> {
         return p_239426_2_ != null && p_239426_2_.isUsingItem() && p_239426_2_.getUseItem() == p_239426_0_ && !CrossbowItem.isCharged(p_239426_0_) ? 1.0F : 0.0F;
      });
      register(Items.CROSSBOW, new ResourceLocation("charged"), (p_239425_0_, p_239425_1_, p_239425_2_) -> {
         return p_239425_2_ != null && CrossbowItem.isCharged(p_239425_0_) ? 1.0F : 0.0F;
      });
      register(Items.CROSSBOW, new ResourceLocation("firework"), (p_239424_0_, p_239424_1_, p_239424_2_) -> {
         return p_239424_2_ != null && CrossbowItem.isCharged(p_239424_0_) && CrossbowItem.containsChargedProjectile(p_239424_0_, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
      });
      register(Items.ELYTRA, new ResourceLocation("broken"), (p_239423_0_, p_239423_1_, p_239423_2_) -> {
         return ElytraItem.isFlyEnabled(p_239423_0_) ? 0.0F : 1.0F;
      });
      register(Items.FISHING_ROD, new ResourceLocation("cast"), (p_239422_0_, p_239422_1_, p_239422_2_) -> {
         if (p_239422_2_ == null) {
            return 0.0F;
         } else {
            boolean flag = p_239422_2_.getMainHandItem() == p_239422_0_;
            boolean flag1 = p_239422_2_.getOffhandItem() == p_239422_0_;
            if (p_239422_2_.getMainHandItem().getItem() instanceof FishingRodItem) {
               flag1 = false;
            }

            return (flag || flag1) && p_239422_2_ instanceof PlayerEntity && ((PlayerEntity)p_239422_2_).fishing != null ? 1.0F : 0.0F;
         }
      });
      register(Items.SHIELD, new ResourceLocation("blocking"), (p_239421_0_, p_239421_1_, p_239421_2_) -> {
         return p_239421_2_ != null && p_239421_2_.isUsingItem() && p_239421_2_.getUseItem() == p_239421_0_ ? 1.0F : 0.0F;
      });
      register(Items.TRIDENT, new ResourceLocation("throwing"), (p_239419_0_, p_239419_1_, p_239419_2_) -> {
         return p_239419_2_ != null && p_239419_2_.isUsingItem() && p_239419_2_.getUseItem() == p_239419_0_ ? 1.0F : 0.0F;
      });
   }

   @OnlyIn(Dist.CLIENT)
   static class Angle {
      private double rotation;
      private double deltaRotation;
      private long lastUpdateTick;

      private Angle() {
      }

      private boolean shouldUpdate(long p_239448_1_) {
         return this.lastUpdateTick != p_239448_1_;
      }

      private void update(long p_239449_1_, double p_239449_3_) {
         this.lastUpdateTick = p_239449_1_;
         double d0 = p_239449_3_ - this.rotation;
         d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
         this.deltaRotation += d0 * 0.1D;
         this.deltaRotation *= 0.8D;
         this.rotation = MathHelper.positiveModulo(this.rotation + this.deltaRotation, 1.0D);
      }
   }
}
