package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.resources.I18n;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditGamerulesScreen extends Screen {
   private final Consumer<Optional<GameRules>> exitCallback;
   private EditGamerulesScreen.GamerulesList rules;
   private final Set<EditGamerulesScreen.Gamerule> invalidEntries = Sets.newHashSet();
   private Button doneButton;
   @Nullable
   private List<IReorderingProcessor> tooltip;
   private final GameRules gameRules;

   public EditGamerulesScreen(GameRules p_i232310_1_, Consumer<Optional<GameRules>> p_i232310_2_) {
      super(new TranslationTextComponent("editGamerule.title"));
      this.gameRules = p_i232310_1_;
      this.exitCallback = p_i232310_2_;
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      super.init();
      this.rules = new EditGamerulesScreen.GamerulesList(this.gameRules);
      this.children.add(this.rules);
      this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, DialogTexts.GUI_CANCEL, (p_238976_1_) -> {
         this.exitCallback.accept(Optional.empty());
      }));
      this.doneButton = this.addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, DialogTexts.GUI_DONE, (p_238971_1_) -> {
         this.exitCallback.accept(Optional.of(this.gameRules));
      }));
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public void onClose() {
      this.exitCallback.accept(Optional.empty());
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.tooltip = null;
      this.rules.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 20, 16777215);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      if (this.tooltip != null) {
         this.renderTooltip(p_230430_1_, this.tooltip, p_230430_2_, p_230430_3_);
      }

   }

   private void setTooltip(@Nullable List<IReorderingProcessor> p_238980_1_) {
      this.tooltip = p_238980_1_;
   }

   private void updateDoneButton() {
      this.doneButton.active = this.invalidEntries.isEmpty();
   }

   private void markInvalid(EditGamerulesScreen.Gamerule p_238972_1_) {
      this.invalidEntries.add(p_238972_1_);
      this.updateDoneButton();
   }

   private void clearInvalid(EditGamerulesScreen.Gamerule p_238977_1_) {
      this.invalidEntries.remove(p_238977_1_);
      this.updateDoneButton();
   }

   @OnlyIn(Dist.CLIENT)
   public class BooleanEntry extends EditGamerulesScreen.ValueEntry {
      private final Button checkbox;

      public BooleanEntry(final ITextComponent p_i232311_2_, List<IReorderingProcessor> p_i232311_3_, final String p_i232311_4_, final GameRules.BooleanValue p_i232311_5_) {
         super(p_i232311_3_, p_i232311_2_);
         this.checkbox = new Button(10, 5, 44, 20, DialogTexts.optionStatus(p_i232311_5_.get()), (p_238988_1_) -> {
            boolean flag = !p_i232311_5_.get();
            p_i232311_5_.set(flag, (MinecraftServer)null);
            p_238988_1_.setMessage(DialogTexts.optionStatus(p_i232311_5_.get()));
         }) {
            protected IFormattableTextComponent createNarrationMessage() {
               return DialogTexts.optionStatus(p_i232311_2_, p_i232311_5_.get()).append("\n").append(p_i232311_4_);
            }
         };
         this.children.add(this.checkbox);
      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         this.renderLabel(p_230432_1_, p_230432_3_, p_230432_4_);
         this.checkbox.x = p_230432_4_ + p_230432_5_ - 45;
         this.checkbox.y = p_230432_3_;
         this.checkbox.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract class Gamerule extends AbstractOptionList.Entry<EditGamerulesScreen.Gamerule> {
      @Nullable
      private final List<IReorderingProcessor> tooltip;

      public Gamerule(@Nullable List<IReorderingProcessor> p_i232315_2_) {
         this.tooltip = p_i232315_2_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class GamerulesList extends AbstractOptionList<EditGamerulesScreen.Gamerule> {
      public GamerulesList(final GameRules p_i232316_2_) {
         super(EditGamerulesScreen.this.minecraft, EditGamerulesScreen.this.width, EditGamerulesScreen.this.height, 43, EditGamerulesScreen.this.height - 32, 24);
         final Map<GameRules.Category, Map<GameRules.RuleKey<?>, EditGamerulesScreen.Gamerule>> map = Maps.newHashMap();
         GameRules.visitGameRuleTypes(new GameRules.IRuleEntryVisitor() {
            public void visitBoolean(GameRules.RuleKey<GameRules.BooleanValue> p_230482_1_, GameRules.RuleType<GameRules.BooleanValue> p_230482_2_) {
               this.addEntry(p_230482_1_, (p_239012_1_, p_239012_2_, p_239012_3_, p_239012_4_) -> {
                  return EditGamerulesScreen.this.new BooleanEntry(p_239012_1_, p_239012_2_, p_239012_3_, p_239012_4_);
               });
            }

            public void visitInteger(GameRules.RuleKey<GameRules.IntegerValue> p_230483_1_, GameRules.RuleType<GameRules.IntegerValue> p_230483_2_) {
               this.addEntry(p_230483_1_, (p_239013_1_, p_239013_2_, p_239013_3_, p_239013_4_) -> {
                  return EditGamerulesScreen.this.new IntegerEntry(p_239013_1_, p_239013_2_, p_239013_3_, p_239013_4_);
               });
            }

            private <T extends GameRules.RuleValue<T>> void addEntry(GameRules.RuleKey<T> p_239011_1_, EditGamerulesScreen.IRuleEntry<T> p_239011_2_) {
               ITextComponent itextcomponent = new TranslationTextComponent(p_239011_1_.getDescriptionId());
               ITextComponent itextcomponent1 = (new StringTextComponent(p_239011_1_.getId())).withStyle(TextFormatting.YELLOW);
               T t = p_i232316_2_.getRule(p_239011_1_);
               String s = t.serialize();
               ITextComponent itextcomponent2 = (new TranslationTextComponent("editGamerule.default", new StringTextComponent(s))).withStyle(TextFormatting.GRAY);
               String s1 = p_239011_1_.getDescriptionId() + ".description";
               List<IReorderingProcessor> list;
               String s2;
               if (I18n.exists(s1)) {
                  Builder<IReorderingProcessor> builder = ImmutableList.<IReorderingProcessor>builder().add(itextcomponent1.getVisualOrderText());
                  ITextComponent itextcomponent3 = new TranslationTextComponent(s1);
                  EditGamerulesScreen.this.font.split(itextcomponent3, 150).forEach(builder::add);
                  list = builder.add(itextcomponent2.getVisualOrderText()).build();
                  s2 = itextcomponent3.getString() + "\n" + itextcomponent2.getString();
               } else {
                  list = ImmutableList.of(itextcomponent1.getVisualOrderText(), itextcomponent2.getVisualOrderText());
                  s2 = itextcomponent2.getString();
               }

               map.computeIfAbsent(p_239011_1_.getCategory(), (p_239010_0_) -> {
                  return Maps.newHashMap();
               }).put(p_239011_1_, p_239011_2_.create(itextcomponent, list, s2, t));
            }
         });
         map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((p_239004_1_) -> {
            this.addEntry(EditGamerulesScreen.this.new NameEntry((new TranslationTextComponent(p_239004_1_.getKey().getDescriptionId())).withStyle(new TextFormatting[]{TextFormatting.BOLD, TextFormatting.YELLOW})));
            p_239004_1_.getValue().entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.comparing(GameRules.RuleKey::getId))).forEach((p_239005_1_) -> {
               this.addEntry(p_239005_1_.getValue());
            });
         });
      }

      public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
         super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
         if (this.isMouseOver((double)p_230430_2_, (double)p_230430_3_)) {
            EditGamerulesScreen.Gamerule editgamerulesscreen$gamerule = this.getEntryAtPosition((double)p_230430_2_, (double)p_230430_3_);
            if (editgamerulesscreen$gamerule != null) {
               EditGamerulesScreen.this.setTooltip(editgamerulesscreen$gamerule.tooltip);
            }
         }

      }
   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   interface IRuleEntry<T extends GameRules.RuleValue<T>> {
      EditGamerulesScreen.Gamerule create(ITextComponent p_create_1_, List<IReorderingProcessor> p_create_2_, String p_create_3_, T p_create_4_);
   }

   @OnlyIn(Dist.CLIENT)
   public class IntegerEntry extends EditGamerulesScreen.ValueEntry {
      private final TextFieldWidget input;

      public IntegerEntry(ITextComponent p_i232314_2_, List<IReorderingProcessor> p_i232314_3_, String p_i232314_4_, GameRules.IntegerValue p_i232314_5_) {
         super(p_i232314_3_, p_i232314_2_);
         this.input = new TextFieldWidget(EditGamerulesScreen.this.minecraft.font, 10, 5, 42, 20, p_i232314_2_.copy().append("\n").append(p_i232314_4_).append("\n"));
         this.input.setValue(Integer.toString(p_i232314_5_.get()));
         this.input.setResponder((p_238999_2_) -> {
            if (p_i232314_5_.tryDeserialize(p_238999_2_)) {
               this.input.setTextColor(14737632);
               EditGamerulesScreen.this.clearInvalid(this);
            } else {
               this.input.setTextColor(16711680);
               EditGamerulesScreen.this.markInvalid(this);
            }

         });
         this.children.add(this.input);
      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         this.renderLabel(p_230432_1_, p_230432_3_, p_230432_4_);
         this.input.x = p_230432_4_ + p_230432_5_ - 44;
         this.input.y = p_230432_3_;
         this.input.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class NameEntry extends EditGamerulesScreen.Gamerule {
      private final ITextComponent label;

      public NameEntry(ITextComponent p_i232313_2_) {
         super((List<IReorderingProcessor>)null);
         this.label = p_i232313_2_;
      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         AbstractGui.drawCenteredString(p_230432_1_, EditGamerulesScreen.this.minecraft.font, this.label, p_230432_4_ + p_230432_5_ / 2, p_230432_3_ + 5, 16777215);
      }

      public List<? extends IGuiEventListener> children() {
         return ImmutableList.of();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public abstract class ValueEntry extends EditGamerulesScreen.Gamerule {
      private final List<IReorderingProcessor> label;
      protected final List<IGuiEventListener> children = Lists.newArrayList();

      public ValueEntry(@Nullable List<IReorderingProcessor> p_i241256_2_, ITextComponent p_i241256_3_) {
         super(p_i241256_2_);
         this.label = EditGamerulesScreen.this.minecraft.font.split(p_i241256_3_, 175);
      }

      public List<? extends IGuiEventListener> children() {
         return this.children;
      }

      protected void renderLabel(MatrixStack p_241649_1_, int p_241649_2_, int p_241649_3_) {
         if (this.label.size() == 1) {
            EditGamerulesScreen.this.minecraft.font.draw(p_241649_1_, this.label.get(0), (float)p_241649_3_, (float)(p_241649_2_ + 5), 16777215);
         } else if (this.label.size() >= 2) {
            EditGamerulesScreen.this.minecraft.font.draw(p_241649_1_, this.label.get(0), (float)p_241649_3_, (float)p_241649_2_, 16777215);
            EditGamerulesScreen.this.minecraft.font.draw(p_241649_1_, this.label.get(1), (float)p_241649_3_, (float)(p_241649_2_ + 10), 16777215);
         }

      }
   }
}
