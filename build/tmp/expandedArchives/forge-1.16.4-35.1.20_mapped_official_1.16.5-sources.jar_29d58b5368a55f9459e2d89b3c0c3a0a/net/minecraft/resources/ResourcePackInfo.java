package net.minecraft.resources;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackInfo implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final PackMetadataSection BROKEN_ASSETS_FALLBACK = new PackMetadataSection((new TranslationTextComponent("resourcePack.broken_assets")).withStyle(new TextFormatting[]{TextFormatting.RED, TextFormatting.ITALIC}), SharedConstants.getCurrentVersion().getPackVersion());
   private final String id;
   private final Supplier<IResourcePack> supplier;
   private final ITextComponent title;
   private final ITextComponent description;
   private final PackCompatibility compatibility;
   private final ResourcePackInfo.Priority defaultPosition;
   private final boolean required;
   private final boolean fixedPosition;
   private final boolean hidden; // Forge: Allow packs to be hidden from the UI entirely
   private final IPackNameDecorator packSource;

   @Nullable
   public static ResourcePackInfo create(String p_195793_0_, boolean p_195793_1_, Supplier<IResourcePack> p_195793_2_, ResourcePackInfo.IFactory p_195793_3_, ResourcePackInfo.Priority p_195793_4_, IPackNameDecorator p_195793_5_) {
      try (IResourcePack iresourcepack = p_195793_2_.get()) {
         PackMetadataSection packmetadatasection = iresourcepack.getMetadataSection(PackMetadataSection.SERIALIZER);
         if (p_195793_1_ && packmetadatasection == null) {
            LOGGER.error("Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!");
            packmetadatasection = BROKEN_ASSETS_FALLBACK;
         }

         if (packmetadatasection != null) {
            return p_195793_3_.create(p_195793_0_, p_195793_1_, p_195793_2_, iresourcepack, packmetadatasection, p_195793_4_, p_195793_5_);
         }

         LOGGER.warn("Couldn't find pack meta for pack {}", (Object)p_195793_0_);
      } catch (IOException ioexception) {
         LOGGER.warn("Couldn't get pack info for: {}", (Object)ioexception.toString());
      }

      return null;
   }

   @Deprecated
   public ResourcePackInfo(String p_i231422_1_, boolean p_i231422_2_, Supplier<IResourcePack> p_i231422_3_, ITextComponent p_i231422_4_, ITextComponent p_i231422_5_, PackCompatibility p_i231422_6_, ResourcePackInfo.Priority p_i231422_7_, boolean p_i231422_8_, IPackNameDecorator p_i231422_9_) {
       this(p_i231422_1_, p_i231422_2_, p_i231422_3_, p_i231422_4_, p_i231422_5_, p_i231422_6_, p_i231422_7_, p_i231422_8_, p_i231422_9_, false);
   }

   public ResourcePackInfo(String p_i231422_1_, boolean p_i231422_2_, Supplier<IResourcePack> p_i231422_3_, ITextComponent p_i231422_4_, ITextComponent p_i231422_5_, PackCompatibility p_i231422_6_, ResourcePackInfo.Priority p_i231422_7_, boolean p_i231422_8_, IPackNameDecorator p_i231422_9_, boolean hidden) {
      this.id = p_i231422_1_;
      this.supplier = p_i231422_3_;
      this.title = p_i231422_4_;
      this.description = p_i231422_5_;
      this.compatibility = p_i231422_6_;
      this.required = p_i231422_2_;
      this.defaultPosition = p_i231422_7_;
      this.fixedPosition = p_i231422_8_;
      this.packSource = p_i231422_9_;
      this.hidden = hidden;
   }

   @Deprecated
   public ResourcePackInfo(String p_i231421_1_, boolean p_i231421_2_, Supplier<IResourcePack> p_i231421_3_, IResourcePack p_i231421_4_, PackMetadataSection p_i231421_5_, ResourcePackInfo.Priority p_i231421_6_, IPackNameDecorator p_i231421_7_) {
       this(p_i231421_1_, p_i231421_2_, p_i231421_3_, p_i231421_4_, p_i231421_5_, p_i231421_6_, p_i231421_7_, false);
   }

   public ResourcePackInfo(String p_i231421_1_, boolean p_i231421_2_, Supplier<IResourcePack> p_i231421_3_, IResourcePack p_i231421_4_, PackMetadataSection p_i231421_5_, ResourcePackInfo.Priority p_i231421_6_, IPackNameDecorator p_i231421_7_, boolean hidden) {
      this(p_i231421_1_, p_i231421_2_, p_i231421_3_, new StringTextComponent(p_i231421_4_.getName()), p_i231421_5_.getDescription(), PackCompatibility.forFormat(p_i231421_5_.getPackFormat()), p_i231421_6_, false, p_i231421_7_, hidden);
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getTitle() {
      return this.title;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDescription() {
      return this.description;
   }

   public ITextComponent getChatLink(boolean p_195794_1_) {
      return TextComponentUtils.wrapInSquareBrackets(this.packSource.decorate(new StringTextComponent(this.id))).withStyle((p_211689_2_) -> {
         return p_211689_2_.withColor(p_195794_1_ ? TextFormatting.GREEN : TextFormatting.RED).withInsertion(StringArgumentType.escapeIfRequired(this.id)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new StringTextComponent("")).append(this.title).append("\n").append(this.description)));
      });
   }

   public PackCompatibility getCompatibility() {
      return this.compatibility;
   }

   public IResourcePack open() {
      return this.supplier.get();
   }

   public String getId() {
      return this.id;
   }

   public boolean isRequired() {
      return this.required;
   }

   public boolean isFixedPosition() {
      return this.fixedPosition;
   }

   public ResourcePackInfo.Priority getDefaultPosition() {
      return this.defaultPosition;
   }

   @OnlyIn(Dist.CLIENT)
   public IPackNameDecorator getPackSource() {
      return this.packSource;
   }
   
   public boolean isHidden() { return hidden; }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof ResourcePackInfo)) {
         return false;
      } else {
         ResourcePackInfo resourcepackinfo = (ResourcePackInfo)p_equals_1_;
         return this.id.equals(resourcepackinfo.id);
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public void close() {
   }

   @FunctionalInterface
   public interface IFactory {
      @Nullable
      ResourcePackInfo create(String p_create_1_, boolean p_create_2_, Supplier<IResourcePack> p_create_3_, IResourcePack p_create_4_, PackMetadataSection p_create_5_, ResourcePackInfo.Priority p_create_6_, IPackNameDecorator p_create_7_);
   }

   public static enum Priority {
      TOP,
      BOTTOM;

      public <T> int insert(List<T> p_198993_1_, T p_198993_2_, Function<T, ResourcePackInfo> p_198993_3_, boolean p_198993_4_) {
         ResourcePackInfo.Priority resourcepackinfo$priority = p_198993_4_ ? this.opposite() : this;
         if (resourcepackinfo$priority == BOTTOM) {
            int j;
            for(j = 0; j < p_198993_1_.size(); ++j) {
               ResourcePackInfo resourcepackinfo1 = p_198993_3_.apply(p_198993_1_.get(j));
               if (!resourcepackinfo1.isFixedPosition() || resourcepackinfo1.getDefaultPosition() != this) {
                  break;
               }
            }

            p_198993_1_.add(j, p_198993_2_);
            return j;
         } else {
            int i;
            for(i = p_198993_1_.size() - 1; i >= 0; --i) {
               ResourcePackInfo resourcepackinfo = p_198993_3_.apply(p_198993_1_.get(i));
               if (!resourcepackinfo.isFixedPosition() || resourcepackinfo.getDefaultPosition() != this) {
                  break;
               }
            }

            p_198993_1_.add(i + 1, p_198993_2_);
            return i + 1;
         }
      }

      public ResourcePackInfo.Priority opposite() {
         return this == TOP ? BOTTOM : TOP;
      }
   }
}
