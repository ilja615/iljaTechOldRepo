package net.minecraft.util.text;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TranslationTextComponent extends TextComponent implements ITargetedTextComponent {
   private static final Object[] NO_ARGS = new Object[0];
   private static final ITextProperties TEXT_PERCENT = ITextProperties.of("%");
   private static final ITextProperties TEXT_NULL = ITextProperties.of("null");
   private final String key;
   private final Object[] args;
   @Nullable
   private LanguageMap decomposedWith;
   private final List<ITextProperties> decomposedParts = Lists.newArrayList();
   private static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

   public TranslationTextComponent(String p_i232574_1_) {
      this.key = p_i232574_1_;
      this.args = NO_ARGS;
   }

   public TranslationTextComponent(String p_i45160_1_, Object... p_i45160_2_) {
      this.key = p_i45160_1_;
      this.args = p_i45160_2_;
   }

   private void decompose() {
      LanguageMap languagemap = LanguageMap.getInstance();
      if (languagemap != this.decomposedWith) {
         this.decomposedWith = languagemap;
         this.decomposedParts.clear();
         String s = languagemap.getOrDefault(this.key);

         try {
            this.decomposeTemplate(s);
         } catch (TranslationTextComponentFormatException translationtextcomponentformatexception) {
            this.decomposedParts.clear();
            this.decomposedParts.add(ITextProperties.of(s));
         }

      }
   }

   private void decomposeTemplate(String p_240758_1_) {
      Matcher matcher = FORMAT_PATTERN.matcher(p_240758_1_);

      try {
         int i = 0;

         int j;
         int l;
         for(j = 0; matcher.find(j); j = l) {
            int k = matcher.start();
            l = matcher.end();
            if (k > j) {
               String s = p_240758_1_.substring(j, k);
               if (s.indexOf(37) != -1) {
                  throw new IllegalArgumentException();
               }

               this.decomposedParts.add(ITextProperties.of(s));
            }

            String s4 = matcher.group(2);
            String s1 = p_240758_1_.substring(k, l);
            if ("%".equals(s4) && "%%".equals(s1)) {
               this.decomposedParts.add(TEXT_PERCENT);
            } else {
               if (!"s".equals(s4)) {
                  throw new TranslationTextComponentFormatException(this, "Unsupported format: '" + s1 + "'");
               }

               String s2 = matcher.group(1);
               int i1 = s2 != null ? Integer.parseInt(s2) - 1 : i++;
               if (i1 < this.args.length) {
                  this.decomposedParts.add(this.getArgument(i1));
               }
            }
         }

         if (j == 0) {
            // if we failed to match above, lets try the messageformat handler instead.
            j = net.minecraftforge.fml.TextComponentMessageFormatHandler.handle(this, this.decomposedParts, this.args, p_240758_1_);
         }
         if (j < p_240758_1_.length()) {
            String s3 = p_240758_1_.substring(j);
            if (s3.indexOf(37) != -1) {
               throw new IllegalArgumentException();
            }

            this.decomposedParts.add(ITextProperties.of(s3));
         }

      } catch (IllegalArgumentException illegalargumentexception) {
         throw new TranslationTextComponentFormatException(this, illegalargumentexception);
      }
   }

   private ITextProperties getArgument(int p_240757_1_) {
      if (p_240757_1_ >= this.args.length) {
         throw new TranslationTextComponentFormatException(this, p_240757_1_);
      } else {
         Object object = this.args[p_240757_1_];
         if (object instanceof ITextComponent) {
            return (ITextComponent)object;
         } else {
            return object == null ? TEXT_NULL : ITextProperties.of(object.toString());
         }
      }
   }

   public TranslationTextComponent plainCopy() {
      return new TranslationTextComponent(this.key, this.args);
   }

   @OnlyIn(Dist.CLIENT)
   public <T> Optional<T> visitSelf(ITextProperties.IStyledTextAcceptor<T> p_230534_1_, Style p_230534_2_) {
      this.decompose();

      for(ITextProperties itextproperties : this.decomposedParts) {
         Optional<T> optional = itextproperties.visit(p_230534_1_, p_230534_2_);
         if (optional.isPresent()) {
            return optional;
         }
      }

      return Optional.empty();
   }

   public <T> Optional<T> visitSelf(ITextProperties.ITextAcceptor<T> p_230533_1_) {
      this.decompose();

      for(ITextProperties itextproperties : this.decomposedParts) {
         Optional<T> optional = itextproperties.visit(p_230533_1_);
         if (optional.isPresent()) {
            return optional;
         }
      }

      return Optional.empty();
   }

   public IFormattableTextComponent resolve(@Nullable CommandSource p_230535_1_, @Nullable Entity p_230535_2_, int p_230535_3_) throws CommandSyntaxException {
      Object[] aobject = new Object[this.args.length];

      for(int i = 0; i < aobject.length; ++i) {
         Object object = this.args[i];
         if (object instanceof ITextComponent) {
            aobject[i] = TextComponentUtils.updateForEntity(p_230535_1_, (ITextComponent)object, p_230535_2_, p_230535_3_);
         } else {
            aobject[i] = object;
         }
      }

      return new TranslationTextComponent(this.key, aobject);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TranslationTextComponent)) {
         return false;
      } else {
         TranslationTextComponent translationtextcomponent = (TranslationTextComponent)p_equals_1_;
         return Arrays.equals(this.args, translationtextcomponent.args) && this.key.equals(translationtextcomponent.key) && super.equals(p_equals_1_);
      }
   }

   public int hashCode() {
      int i = super.hashCode();
      i = 31 * i + this.key.hashCode();
      return 31 * i + Arrays.hashCode(this.args);
   }

   public String toString() {
      return "TranslatableComponent{key='" + this.key + '\'' + ", args=" + Arrays.toString(this.args) + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   public String getKey() {
      return this.key;
   }

   public Object[] getArgs() {
      return this.args;
   }
}
