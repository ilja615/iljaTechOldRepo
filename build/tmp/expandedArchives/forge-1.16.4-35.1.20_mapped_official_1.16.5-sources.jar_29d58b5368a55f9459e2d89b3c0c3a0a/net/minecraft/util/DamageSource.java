package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;

public class DamageSource {
   public static final DamageSource IN_FIRE = (new DamageSource("inFire")).bypassArmor().setIsFire();
   public static final DamageSource LIGHTNING_BOLT = new DamageSource("lightningBolt");
   public static final DamageSource ON_FIRE = (new DamageSource("onFire")).bypassArmor().setIsFire();
   public static final DamageSource LAVA = (new DamageSource("lava")).setIsFire();
   public static final DamageSource HOT_FLOOR = (new DamageSource("hotFloor")).setIsFire();
   public static final DamageSource IN_WALL = (new DamageSource("inWall")).bypassArmor();
   public static final DamageSource CRAMMING = (new DamageSource("cramming")).bypassArmor();
   public static final DamageSource DROWN = (new DamageSource("drown")).bypassArmor();
   public static final DamageSource STARVE = (new DamageSource("starve")).bypassArmor().bypassMagic();
   public static final DamageSource CACTUS = new DamageSource("cactus");
   public static final DamageSource FALL = (new DamageSource("fall")).bypassArmor();
   public static final DamageSource FLY_INTO_WALL = (new DamageSource("flyIntoWall")).bypassArmor();
   public static final DamageSource OUT_OF_WORLD = (new DamageSource("outOfWorld")).bypassArmor().bypassInvul();
   public static final DamageSource GENERIC = (new DamageSource("generic")).bypassArmor();
   public static final DamageSource MAGIC = (new DamageSource("magic")).bypassArmor().setMagic();
   public static final DamageSource WITHER = (new DamageSource("wither")).bypassArmor();
   public static final DamageSource ANVIL = new DamageSource("anvil");
   public static final DamageSource FALLING_BLOCK = new DamageSource("fallingBlock");
   public static final DamageSource DRAGON_BREATH = (new DamageSource("dragonBreath")).bypassArmor();
   public static final DamageSource DRY_OUT = new DamageSource("dryout");
   public static final DamageSource SWEET_BERRY_BUSH = new DamageSource("sweetBerryBush");
   private boolean bypassArmor;
   private boolean bypassInvul;
   private boolean bypassMagic;
   private float exhaustion = 0.1F;
   private boolean isFireSource;
   private boolean isProjectile;
   private boolean scalesWithDifficulty;
   private boolean isMagic;
   private boolean isExplosion;
   public final String msgId;

   public static DamageSource sting(LivingEntity p_226252_0_) {
      return new EntityDamageSource("sting", p_226252_0_);
   }

   public static DamageSource mobAttack(LivingEntity p_76358_0_) {
      return new EntityDamageSource("mob", p_76358_0_);
   }

   public static DamageSource indirectMobAttack(Entity p_188403_0_, LivingEntity p_188403_1_) {
      return new IndirectEntityDamageSource("mob", p_188403_0_, p_188403_1_);
   }

   public static DamageSource playerAttack(PlayerEntity p_76365_0_) {
      return new EntityDamageSource("player", p_76365_0_);
   }

   public static DamageSource arrow(AbstractArrowEntity p_76353_0_, @Nullable Entity p_76353_1_) {
      return (new IndirectEntityDamageSource("arrow", p_76353_0_, p_76353_1_)).setProjectile();
   }

   public static DamageSource trident(Entity p_203096_0_, @Nullable Entity p_203096_1_) {
      return (new IndirectEntityDamageSource("trident", p_203096_0_, p_203096_1_)).setProjectile();
   }

   public static DamageSource fireworks(FireworkRocketEntity p_233548_0_, @Nullable Entity p_233548_1_) {
      return (new IndirectEntityDamageSource("fireworks", p_233548_0_, p_233548_1_)).setExplosion();
   }

   public static DamageSource fireball(AbstractFireballEntity p_233547_0_, @Nullable Entity p_233547_1_) {
      return p_233547_1_ == null ? (new IndirectEntityDamageSource("onFire", p_233547_0_, p_233547_0_)).setIsFire().setProjectile() : (new IndirectEntityDamageSource("fireball", p_233547_0_, p_233547_1_)).setIsFire().setProjectile();
   }

   public static DamageSource witherSkull(WitherSkullEntity p_233549_0_, Entity p_233549_1_) {
      return (new IndirectEntityDamageSource("witherSkull", p_233549_0_, p_233549_1_)).setProjectile();
   }

   public static DamageSource thrown(Entity p_76356_0_, @Nullable Entity p_76356_1_) {
      return (new IndirectEntityDamageSource("thrown", p_76356_0_, p_76356_1_)).setProjectile();
   }

   public static DamageSource indirectMagic(Entity p_76354_0_, @Nullable Entity p_76354_1_) {
      return (new IndirectEntityDamageSource("indirectMagic", p_76354_0_, p_76354_1_)).bypassArmor().setMagic();
   }

   public static DamageSource thorns(Entity p_92087_0_) {
      return (new EntityDamageSource("thorns", p_92087_0_)).setThorns().setMagic();
   }

   public static DamageSource explosion(@Nullable Explosion p_94539_0_) {
      return explosion(p_94539_0_ != null ? p_94539_0_.getSourceMob() : null);
   }

   public static DamageSource explosion(@Nullable LivingEntity p_188405_0_) {
      return p_188405_0_ != null ? (new EntityDamageSource("explosion.player", p_188405_0_)).setScalesWithDifficulty().setExplosion() : (new DamageSource("explosion")).setScalesWithDifficulty().setExplosion();
   }

   public static DamageSource badRespawnPointExplosion() {
      return new BedExplosionDamageSource();
   }

   public String toString() {
      return "DamageSource (" + this.msgId + ")";
   }

   public boolean isProjectile() {
      return this.isProjectile;
   }

   public DamageSource setProjectile() {
      this.isProjectile = true;
      return this;
   }

   public boolean isExplosion() {
      return this.isExplosion;
   }

   public DamageSource setExplosion() {
      this.isExplosion = true;
      return this;
   }

   public boolean isBypassArmor() {
      return this.bypassArmor;
   }

   public float getFoodExhaustion() {
      return this.exhaustion;
   }

   public boolean isBypassInvul() {
      return this.bypassInvul;
   }

   public boolean isBypassMagic() {
      return this.bypassMagic;
   }

   public DamageSource(String p_i1566_1_) {
      this.msgId = p_i1566_1_;
   }

   @Nullable
   public Entity getDirectEntity() {
      return this.getEntity();
   }

   @Nullable
   public Entity getEntity() {
      return null;
   }

   public DamageSource bypassArmor() {
      this.bypassArmor = true;
      this.exhaustion = 0.0F;
      return this;
   }

   public DamageSource bypassInvul() {
      this.bypassInvul = true;
      return this;
   }

   public DamageSource bypassMagic() {
      this.bypassMagic = true;
      this.exhaustion = 0.0F;
      return this;
   }

   public DamageSource setIsFire() {
      this.isFireSource = true;
      return this;
   }

   public ITextComponent getLocalizedDeathMessage(LivingEntity p_151519_1_) {
      LivingEntity livingentity = p_151519_1_.getKillCredit();
      String s = "death.attack." + this.msgId;
      String s1 = s + ".player";
      return livingentity != null ? new TranslationTextComponent(s1, p_151519_1_.getDisplayName(), livingentity.getDisplayName()) : new TranslationTextComponent(s, p_151519_1_.getDisplayName());
   }

   public boolean isFire() {
      return this.isFireSource;
   }

   public String getMsgId() {
      return this.msgId;
   }

   public DamageSource setScalesWithDifficulty() {
      this.scalesWithDifficulty = true;
      return this;
   }

   public boolean scalesWithDifficulty() {
      return this.scalesWithDifficulty;
   }

   public boolean isMagic() {
      return this.isMagic;
   }

   public DamageSource setMagic() {
      this.isMagic = true;
      return this;
   }

   public boolean isCreativePlayer() {
      Entity entity = this.getEntity();
      return entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.instabuild;
   }

   @Nullable
   public Vector3d getSourcePosition() {
      return null;
   }
}
