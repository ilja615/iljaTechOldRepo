package net.minecraft.entity.merchant;

public interface IReputationType {
   IReputationType ZOMBIE_VILLAGER_CURED = register("zombie_villager_cured");
   IReputationType GOLEM_KILLED = register("golem_killed");
   IReputationType VILLAGER_HURT = register("villager_hurt");
   IReputationType VILLAGER_KILLED = register("villager_killed");
   IReputationType TRADE = register("trade");

   static IReputationType register(final String p_221028_0_) {
      return new IReputationType() {
         public String toString() {
            return p_221028_0_;
         }
      };
   }
}
