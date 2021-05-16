package net.minecraft.util.text;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.ICharacterConsumer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

@OnlyIn(Dist.CLIENT)
public class CharacterManager {
   private final CharacterManager.ICharWidthProvider widthProvider;

   public CharacterManager(CharacterManager.ICharWidthProvider p_i232243_1_) {
      this.widthProvider = p_i232243_1_;
   }

   public float stringWidth(@Nullable String p_238350_1_) {
      if (p_238350_1_ == null) {
         return 0.0F;
      } else {
         MutableFloat mutablefloat = new MutableFloat();
         TextProcessing.iterateFormatted(p_238350_1_, Style.EMPTY, (p_238363_2_, p_238363_3_, p_238363_4_) -> {
            mutablefloat.add(this.widthProvider.getWidth(p_238363_4_, p_238363_3_));
            return true;
         });
         return mutablefloat.floatValue();
      }
   }

   public float stringWidth(ITextProperties p_238356_1_) {
      MutableFloat mutablefloat = new MutableFloat();
      TextProcessing.iterateFormatted(p_238356_1_, Style.EMPTY, (p_238359_2_, p_238359_3_, p_238359_4_) -> {
         mutablefloat.add(this.widthProvider.getWidth(p_238359_4_, p_238359_3_));
         return true;
      });
      return mutablefloat.floatValue();
   }

   public float stringWidth(IReorderingProcessor p_243238_1_) {
      MutableFloat mutablefloat = new MutableFloat();
      p_243238_1_.accept((p_243243_2_, p_243243_3_, p_243243_4_) -> {
         mutablefloat.add(this.widthProvider.getWidth(p_243243_4_, p_243243_3_));
         return true;
      });
      return mutablefloat.floatValue();
   }

   public int plainIndexAtWidth(String p_238352_1_, int p_238352_2_, Style p_238352_3_) {
      CharacterManager.StringWidthProcessor charactermanager$stringwidthprocessor = new CharacterManager.StringWidthProcessor((float)p_238352_2_);
      TextProcessing.iterate(p_238352_1_, p_238352_3_, charactermanager$stringwidthprocessor);
      return charactermanager$stringwidthprocessor.getPosition();
   }

   public String plainHeadByWidth(String p_238361_1_, int p_238361_2_, Style p_238361_3_) {
      return p_238361_1_.substring(0, this.plainIndexAtWidth(p_238361_1_, p_238361_2_, p_238361_3_));
   }

   public String plainTailByWidth(String p_238364_1_, int p_238364_2_, Style p_238364_3_) {
      MutableFloat mutablefloat = new MutableFloat();
      MutableInt mutableint = new MutableInt(p_238364_1_.length());
      TextProcessing.iterateBackwards(p_238364_1_, p_238364_3_, (p_238360_4_, p_238360_5_, p_238360_6_) -> {
         float f = mutablefloat.addAndGet(this.widthProvider.getWidth(p_238360_6_, p_238360_5_));
         if (f > (float)p_238364_2_) {
            return false;
         } else {
            mutableint.setValue(p_238360_4_);
            return true;
         }
      });
      return p_238364_1_.substring(mutableint.intValue());
   }

   @Nullable
   public Style componentStyleAtWidth(ITextProperties p_238357_1_, int p_238357_2_) {
      CharacterManager.StringWidthProcessor charactermanager$stringwidthprocessor = new CharacterManager.StringWidthProcessor((float)p_238357_2_);
      return p_238357_1_.visit((p_238348_1_, p_238348_2_) -> {
         return TextProcessing.iterateFormatted(p_238348_2_, p_238348_1_, charactermanager$stringwidthprocessor) ? Optional.empty() : Optional.of(p_238348_1_);
      }, Style.EMPTY).orElse((Style)null);
   }

   @Nullable
   public Style componentStyleAtWidth(IReorderingProcessor p_243239_1_, int p_243239_2_) {
      CharacterManager.StringWidthProcessor charactermanager$stringwidthprocessor = new CharacterManager.StringWidthProcessor((float)p_243239_2_);
      MutableObject<Style> mutableobject = new MutableObject<>();
      p_243239_1_.accept((p_243240_2_, p_243240_3_, p_243240_4_) -> {
         if (!charactermanager$stringwidthprocessor.accept(p_243240_2_, p_243240_3_, p_243240_4_)) {
            mutableobject.setValue(p_243240_3_);
            return false;
         } else {
            return true;
         }
      });
      return mutableobject.getValue();
   }

   public ITextProperties headByWidth(ITextProperties p_238358_1_, int p_238358_2_, Style p_238358_3_) {
      final CharacterManager.StringWidthProcessor charactermanager$stringwidthprocessor = new CharacterManager.StringWidthProcessor((float)p_238358_2_);
      return p_238358_1_.visit(new ITextProperties.IStyledTextAcceptor<ITextProperties>() {
         private final TextPropertiesManager collector = new TextPropertiesManager();

         public Optional<ITextProperties> accept(Style p_accept_1_, String p_accept_2_) {
            charactermanager$stringwidthprocessor.resetPosition();
            if (!TextProcessing.iterateFormatted(p_accept_2_, p_accept_1_, charactermanager$stringwidthprocessor)) {
               String s = p_accept_2_.substring(0, charactermanager$stringwidthprocessor.getPosition());
               if (!s.isEmpty()) {
                  this.collector.append(ITextProperties.of(s, p_accept_1_));
               }

               return Optional.of(this.collector.getResultOrEmpty());
            } else {
               if (!p_accept_2_.isEmpty()) {
                  this.collector.append(ITextProperties.of(p_accept_2_, p_accept_1_));
               }

               return Optional.empty();
            }
         }
      }, p_238358_3_).orElse(p_238358_1_);
   }

   public static int getWordPosition(String p_238351_0_, int p_238351_1_, int p_238351_2_, boolean p_238351_3_) {
      int i = p_238351_2_;
      boolean flag = p_238351_1_ < 0;
      int j = Math.abs(p_238351_1_);

      for(int k = 0; k < j; ++k) {
         if (flag) {
            while(p_238351_3_ && i > 0 && (p_238351_0_.charAt(i - 1) == ' ' || p_238351_0_.charAt(i - 1) == '\n')) {
               --i;
            }

            while(i > 0 && p_238351_0_.charAt(i - 1) != ' ' && p_238351_0_.charAt(i - 1) != '\n') {
               --i;
            }
         } else {
            int l = p_238351_0_.length();
            int i1 = p_238351_0_.indexOf(32, i);
            int j1 = p_238351_0_.indexOf(10, i);
            if (i1 == -1 && j1 == -1) {
               i = -1;
            } else if (i1 != -1 && j1 != -1) {
               i = Math.min(i1, j1);
            } else if (i1 != -1) {
               i = i1;
            } else {
               i = j1;
            }

            if (i == -1) {
               i = l;
            } else {
               while(p_238351_3_ && i < l && (p_238351_0_.charAt(i) == ' ' || p_238351_0_.charAt(i) == '\n')) {
                  ++i;
               }
            }
         }
      }

      return i;
   }

   public void splitLines(String p_238353_1_, int p_238353_2_, Style p_238353_3_, boolean p_238353_4_, CharacterManager.ISliceAcceptor p_238353_5_) {
      int i = 0;
      int j = p_238353_1_.length();

      CharacterManager.MultilineProcessor charactermanager$multilineprocessor;
      for(Style style = p_238353_3_; i < j; style = charactermanager$multilineprocessor.getSplitStyle()) {
         charactermanager$multilineprocessor = new CharacterManager.MultilineProcessor((float)p_238353_2_);
         boolean flag = TextProcessing.iterateFormatted(p_238353_1_, i, style, p_238353_3_, charactermanager$multilineprocessor);
         if (flag) {
            p_238353_5_.accept(style, i, j);
            break;
         }

         int k = charactermanager$multilineprocessor.getSplitPosition();
         char c0 = p_238353_1_.charAt(k);
         int l = c0 != '\n' && c0 != ' ' ? k : k + 1;
         p_238353_5_.accept(style, i, p_238353_4_ ? l : k);
         i = l;
      }

   }

   public List<ITextProperties> splitLines(String p_238365_1_, int p_238365_2_, Style p_238365_3_) {
      List<ITextProperties> list = Lists.newArrayList();
      this.splitLines(p_238365_1_, p_238365_2_, p_238365_3_, false, (p_238354_2_, p_238354_3_, p_238354_4_) -> {
         list.add(ITextProperties.of(p_238365_1_.substring(p_238354_3_, p_238354_4_), p_238354_2_));
      });
      return list;
   }

   public List<ITextProperties> splitLines(ITextProperties p_238362_1_, int p_238362_2_, Style p_238362_3_) {
      List<ITextProperties> list = Lists.newArrayList();
      this.splitLines(p_238362_1_, p_238362_2_, p_238362_3_, (p_243241_1_, p_243241_2_) -> {
         list.add(p_243241_1_);
      });
      return list;
   }

   public void splitLines(ITextProperties p_243242_1_, int p_243242_2_, Style p_243242_3_, BiConsumer<ITextProperties, Boolean> p_243242_4_) {
      List<CharacterManager.StyleOverridingTextComponent> list = Lists.newArrayList();
      p_243242_1_.visit((p_238355_1_, p_238355_2_) -> {
         if (!p_238355_2_.isEmpty()) {
            list.add(new CharacterManager.StyleOverridingTextComponent(p_238355_2_, p_238355_1_));
         }

         return Optional.empty();
      }, p_243242_3_);
      CharacterManager.SubstyledText charactermanager$substyledtext = new CharacterManager.SubstyledText(list);
      boolean flag = true;
      boolean flag1 = false;
      boolean flag2 = false;

      while(flag) {
         flag = false;
         CharacterManager.MultilineProcessor charactermanager$multilineprocessor = new CharacterManager.MultilineProcessor((float)p_243242_2_);

         for(CharacterManager.StyleOverridingTextComponent charactermanager$styleoverridingtextcomponent : charactermanager$substyledtext.parts) {
            boolean flag3 = TextProcessing.iterateFormatted(charactermanager$styleoverridingtextcomponent.contents, 0, charactermanager$styleoverridingtextcomponent.style, p_243242_3_, charactermanager$multilineprocessor);
            if (!flag3) {
               int i = charactermanager$multilineprocessor.getSplitPosition();
               Style style = charactermanager$multilineprocessor.getSplitStyle();
               char c0 = charactermanager$substyledtext.charAt(i);
               boolean flag4 = c0 == '\n';
               boolean flag5 = flag4 || c0 == ' ';
               flag1 = flag4;
               ITextProperties itextproperties = charactermanager$substyledtext.splitAt(i, flag5 ? 1 : 0, style);
               p_243242_4_.accept(itextproperties, flag2);
               flag2 = !flag4;
               flag = true;
               break;
            }

            charactermanager$multilineprocessor.addToOffset(charactermanager$styleoverridingtextcomponent.contents.length());
         }
      }

      ITextProperties itextproperties1 = charactermanager$substyledtext.getRemainder();
      if (itextproperties1 != null) {
         p_243242_4_.accept(itextproperties1, flag2);
      } else if (flag1) {
         p_243242_4_.accept(ITextProperties.EMPTY, false);
      }

   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   public interface ICharWidthProvider {
      float getWidth(int p_getWidth_1_, Style p_getWidth_2_);
   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   public interface ISliceAcceptor {
      void accept(Style p_accept_1_, int p_accept_2_, int p_accept_3_);
   }

   @OnlyIn(Dist.CLIENT)
   class MultilineProcessor implements ICharacterConsumer {
      private final float maxWidth;
      private int lineBreak = -1;
      private Style lineBreakStyle = Style.EMPTY;
      private boolean hadNonZeroWidthChar;
      private float width;
      private int lastSpace = -1;
      private Style lastSpaceStyle = Style.EMPTY;
      private int nextChar;
      private int offset;

      public MultilineProcessor(float p_i232246_2_) {
         this.maxWidth = Math.max(p_i232246_2_, 1.0F);
      }

      public boolean accept(int p_accept_1_, Style p_accept_2_, int p_accept_3_) {
         int i = p_accept_1_ + this.offset;
         switch(p_accept_3_) {
         case 10:
            return this.finishIteration(i, p_accept_2_);
         case 32:
            this.lastSpace = i;
            this.lastSpaceStyle = p_accept_2_;
         default:
            float f = CharacterManager.this.widthProvider.getWidth(p_accept_3_, p_accept_2_);
            this.width += f;
            if (this.hadNonZeroWidthChar && this.width > this.maxWidth) {
               return this.lastSpace != -1 ? this.finishIteration(this.lastSpace, this.lastSpaceStyle) : this.finishIteration(i, p_accept_2_);
            } else {
               this.hadNonZeroWidthChar |= f != 0.0F;
               this.nextChar = i + Character.charCount(p_accept_3_);
               return true;
            }
         }
      }

      private boolean finishIteration(int p_238388_1_, Style p_238388_2_) {
         this.lineBreak = p_238388_1_;
         this.lineBreakStyle = p_238388_2_;
         return false;
      }

      private boolean lineBreakFound() {
         return this.lineBreak != -1;
      }

      public int getSplitPosition() {
         return this.lineBreakFound() ? this.lineBreak : this.nextChar;
      }

      public Style getSplitStyle() {
         return this.lineBreakStyle;
      }

      public void addToOffset(int p_238387_1_) {
         this.offset += p_238387_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class StringWidthProcessor implements ICharacterConsumer {
      private float maxWidth;
      private int position;

      public StringWidthProcessor(float p_i232248_2_) {
         this.maxWidth = p_i232248_2_;
      }

      public boolean accept(int p_accept_1_, Style p_accept_2_, int p_accept_3_) {
         this.maxWidth -= CharacterManager.this.widthProvider.getWidth(p_accept_3_, p_accept_2_);
         if (this.maxWidth >= 0.0F) {
            this.position = p_accept_1_ + Character.charCount(p_accept_3_);
            return true;
         } else {
            return false;
         }
      }

      public int getPosition() {
         return this.position;
      }

      public void resetPosition() {
         this.position = 0;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class StyleOverridingTextComponent implements ITextProperties {
      private final String contents;
      private final Style style;

      public StyleOverridingTextComponent(String p_i232247_1_, Style p_i232247_2_) {
         this.contents = p_i232247_1_;
         this.style = p_i232247_2_;
      }

      public <T> Optional<T> visit(ITextProperties.ITextAcceptor<T> p_230438_1_) {
         return p_230438_1_.accept(this.contents);
      }

      public <T> Optional<T> visit(ITextProperties.IStyledTextAcceptor<T> p_230439_1_, Style p_230439_2_) {
         return p_230439_1_.accept(this.style.applyTo(p_230439_2_), this.contents);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class SubstyledText {
      private final List<CharacterManager.StyleOverridingTextComponent> parts;
      private String flatParts;

      public SubstyledText(List<CharacterManager.StyleOverridingTextComponent> p_i232245_1_) {
         this.parts = p_i232245_1_;
         this.flatParts = p_i232245_1_.stream().map((p_238375_0_) -> {
            return p_238375_0_.contents;
         }).collect(Collectors.joining());
      }

      public char charAt(int p_238372_1_) {
         return this.flatParts.charAt(p_238372_1_);
      }

      public ITextProperties splitAt(int p_238373_1_, int p_238373_2_, Style p_238373_3_) {
         TextPropertiesManager textpropertiesmanager = new TextPropertiesManager();
         ListIterator<CharacterManager.StyleOverridingTextComponent> listiterator = this.parts.listIterator();
         int i = p_238373_1_;
         boolean flag = false;

         while(listiterator.hasNext()) {
            CharacterManager.StyleOverridingTextComponent charactermanager$styleoverridingtextcomponent = listiterator.next();
            String s = charactermanager$styleoverridingtextcomponent.contents;
            int j = s.length();
            if (!flag) {
               if (i > j) {
                  textpropertiesmanager.append(charactermanager$styleoverridingtextcomponent);
                  listiterator.remove();
                  i -= j;
               } else {
                  String s1 = s.substring(0, i);
                  if (!s1.isEmpty()) {
                     textpropertiesmanager.append(ITextProperties.of(s1, charactermanager$styleoverridingtextcomponent.style));
                  }

                  i += p_238373_2_;
                  flag = true;
               }
            }

            if (flag) {
               if (i <= j) {
                  String s2 = s.substring(i);
                  if (s2.isEmpty()) {
                     listiterator.remove();
                  } else {
                     listiterator.set(new CharacterManager.StyleOverridingTextComponent(s2, p_238373_3_));
                  }
                  break;
               }

               listiterator.remove();
               i -= j;
            }
         }

         this.flatParts = this.flatParts.substring(p_238373_1_ + p_238373_2_);
         return textpropertiesmanager.getResultOrEmpty();
      }

      @Nullable
      public ITextProperties getRemainder() {
         TextPropertiesManager textpropertiesmanager = new TextPropertiesManager();
         this.parts.forEach(textpropertiesmanager::append);
         this.parts.clear();
         return textpropertiesmanager.getResult();
      }
   }
}
