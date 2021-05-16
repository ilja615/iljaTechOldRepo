package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class CommandStorage {
   private final Map<String, CommandStorage.Container> namespaces = Maps.newHashMap();
   private final DimensionSavedDataManager storage;

   public CommandStorage(DimensionSavedDataManager p_i225883_1_) {
      this.storage = p_i225883_1_;
   }

   private CommandStorage.Container newStorage(String p_227486_1_, String p_227486_2_) {
      CommandStorage.Container commandstorage$container = new CommandStorage.Container(p_227486_2_);
      this.namespaces.put(p_227486_1_, commandstorage$container);
      return commandstorage$container;
   }

   public CompoundNBT get(ResourceLocation p_227488_1_) {
      String s = p_227488_1_.getNamespace();
      String s1 = createId(s);
      CommandStorage.Container commandstorage$container = this.storage.get(() -> {
         return this.newStorage(s, s1);
      }, s1);
      return commandstorage$container != null ? commandstorage$container.get(p_227488_1_.getPath()) : new CompoundNBT();
   }

   public void set(ResourceLocation p_227489_1_, CompoundNBT p_227489_2_) {
      String s = p_227489_1_.getNamespace();
      String s1 = createId(s);
      this.storage.computeIfAbsent(() -> {
         return this.newStorage(s, s1);
      }, s1).put(p_227489_1_.getPath(), p_227489_2_);
   }

   public Stream<ResourceLocation> keys() {
      return this.namespaces.entrySet().stream().flatMap((p_227487_0_) -> {
         return p_227487_0_.getValue().getKeys(p_227487_0_.getKey());
      });
   }

   private static String createId(String p_227485_0_) {
      return "command_storage_" + p_227485_0_;
   }

   static class Container extends WorldSavedData {
      private final Map<String, CompoundNBT> storage = Maps.newHashMap();

      public Container(String p_i225884_1_) {
         super(p_i225884_1_);
      }

      public void load(CompoundNBT p_76184_1_) {
         CompoundNBT compoundnbt = p_76184_1_.getCompound("contents");

         for(String s : compoundnbt.getAllKeys()) {
            this.storage.put(s, compoundnbt.getCompound(s));
         }

      }

      public CompoundNBT save(CompoundNBT p_189551_1_) {
         CompoundNBT compoundnbt = new CompoundNBT();
         this.storage.forEach((p_227496_1_, p_227496_2_) -> {
            compoundnbt.put(p_227496_1_, p_227496_2_.copy());
         });
         p_189551_1_.put("contents", compoundnbt);
         return p_189551_1_;
      }

      public CompoundNBT get(String p_227493_1_) {
         CompoundNBT compoundnbt = this.storage.get(p_227493_1_);
         return compoundnbt != null ? compoundnbt : new CompoundNBT();
      }

      public void put(String p_227495_1_, CompoundNBT p_227495_2_) {
         if (p_227495_2_.isEmpty()) {
            this.storage.remove(p_227495_1_);
         } else {
            this.storage.put(p_227495_1_, p_227495_2_);
         }

         this.setDirty();
      }

      public Stream<ResourceLocation> getKeys(String p_227497_1_) {
         return this.storage.keySet().stream().map((p_227494_1_) -> {
            return new ResourceLocation(p_227497_1_, p_227494_1_);
         });
      }
   }
}
