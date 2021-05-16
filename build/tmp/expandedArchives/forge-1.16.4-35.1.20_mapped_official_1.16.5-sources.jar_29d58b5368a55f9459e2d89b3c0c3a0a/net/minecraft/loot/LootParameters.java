package net.minecraft.loot;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

public class LootParameters {
   public static final LootParameter<Entity> THIS_ENTITY = create("this_entity");
   public static final LootParameter<PlayerEntity> LAST_DAMAGE_PLAYER = create("last_damage_player");
   public static final LootParameter<DamageSource> DAMAGE_SOURCE = create("damage_source");
   public static final LootParameter<Entity> KILLER_ENTITY = create("killer_entity");
   public static final LootParameter<Entity> DIRECT_KILLER_ENTITY = create("direct_killer_entity");
   public static final LootParameter<Vector3d> ORIGIN = create("origin");
   public static final LootParameter<BlockState> BLOCK_STATE = create("block_state");
   public static final LootParameter<TileEntity> BLOCK_ENTITY = create("block_entity");
   public static final LootParameter<ItemStack> TOOL = create("tool");
   public static final LootParameter<Float> EXPLOSION_RADIUS = create("explosion_radius");

   private static <T> LootParameter<T> create(String p_216280_0_) {
      return new LootParameter<>(new ResourceLocation(p_216280_0_));
   }
}
