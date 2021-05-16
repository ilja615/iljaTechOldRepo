package net.minecraft.client.settings;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyBinding implements Comparable<KeyBinding>, net.minecraftforge.client.extensions.IForgeKeybinding {
   private static final Map<String, KeyBinding> ALL = Maps.newHashMap();
   private static final net.minecraftforge.client.settings.KeyBindingMap MAP = new net.minecraftforge.client.settings.KeyBindingMap();
   private static final Set<String> CATEGORIES = Sets.newHashSet();
   private static final Map<String, Integer> CATEGORY_SORT_ORDER = Util.make(Maps.newHashMap(), (p_205215_0_) -> {
      p_205215_0_.put("key.categories.movement", 1);
      p_205215_0_.put("key.categories.gameplay", 2);
      p_205215_0_.put("key.categories.inventory", 3);
      p_205215_0_.put("key.categories.creative", 4);
      p_205215_0_.put("key.categories.multiplayer", 5);
      p_205215_0_.put("key.categories.ui", 6);
      p_205215_0_.put("key.categories.misc", 7);
   });
   private final String name;
   private final InputMappings.Input defaultKey;
   private final String category;
   private InputMappings.Input key;
   boolean isDown;
   private int clickCount;

   public static void click(InputMappings.Input p_197981_0_) {
      KeyBinding keybinding = MAP.lookupActive(p_197981_0_);
      if (keybinding != null) {
         ++keybinding.clickCount;
      }

   }

   public static void set(InputMappings.Input p_197980_0_, boolean p_197980_1_) {
      for (KeyBinding keybinding : MAP.lookupAll(p_197980_0_))
      if (keybinding != null) {
         keybinding.setDown(p_197980_1_);
      }

   }

   public static void setAll() {
      for(KeyBinding keybinding : ALL.values()) {
         if (keybinding.key.getType() == InputMappings.Type.KEYSYM && keybinding.key.getValue() != InputMappings.UNKNOWN.getValue()) {
            keybinding.setDown(InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keybinding.key.getValue()));
         }
      }

   }

   public static void releaseAll() {
      for(KeyBinding keybinding : ALL.values()) {
         keybinding.release();
      }

   }

   public static void resetMapping() {
      MAP.clearMap();

      for(KeyBinding keybinding : ALL.values()) {
         MAP.addKey(keybinding.key, keybinding);
      }

   }

   public KeyBinding(String p_i45001_1_, int p_i45001_2_, String p_i45001_3_) {
      this(p_i45001_1_, InputMappings.Type.KEYSYM, p_i45001_2_, p_i45001_3_);
   }

   public KeyBinding(String p_i47675_1_, InputMappings.Type p_i47675_2_, int p_i47675_3_, String p_i47675_4_) {
      this.name = p_i47675_1_;
      this.key = p_i47675_2_.getOrCreate(p_i47675_3_);
      this.defaultKey = this.key;
      this.category = p_i47675_4_;
      ALL.put(p_i47675_1_, this);
      MAP.addKey(this.key, this);
      CATEGORIES.add(p_i47675_4_);
   }

   public boolean isDown() {
      return this.isDown && isConflictContextAndModifierActive();
   }

   public String getCategory() {
      return this.category;
   }

   public boolean consumeClick() {
      if (this.clickCount == 0) {
         return false;
      } else {
         --this.clickCount;
         return true;
      }
   }

   private void release() {
      this.clickCount = 0;
      this.setDown(false);
   }

   public String getName() {
      return this.name;
   }

   public InputMappings.Input getDefaultKey() {
      return this.defaultKey;
   }

   public void setKey(InputMappings.Input p_197979_1_) {
      this.key = p_197979_1_;
   }

   public int compareTo(KeyBinding p_compareTo_1_) {
      if (this.category.equals(p_compareTo_1_.category)) return I18n.get(this.name).compareTo(I18n.get(p_compareTo_1_.name));
      Integer tCat = CATEGORY_SORT_ORDER.get(this.category);
      Integer oCat = CATEGORY_SORT_ORDER.get(p_compareTo_1_.category);
      if (tCat == null && oCat != null) return 1;
      if (tCat != null && oCat == null) return -1;
      if (tCat == null && oCat == null) return I18n.get(this.category).compareTo(I18n.get(p_compareTo_1_.category));
      return  tCat.compareTo(oCat);
   }

   public static Supplier<ITextComponent> createNameSupplier(String p_193626_0_) {
      KeyBinding keybinding = ALL.get(p_193626_0_);
      return keybinding == null ? () -> {
         return new TranslationTextComponent(p_193626_0_);
      } : keybinding::getTranslatedKeyMessage;
   }

   public boolean same(KeyBinding p_197983_1_) {
      if (getKeyConflictContext().conflicts(p_197983_1_.getKeyConflictContext()) || p_197983_1_.getKeyConflictContext().conflicts(getKeyConflictContext())) {
         net.minecraftforge.client.settings.KeyModifier keyModifier = getKeyModifier();
         net.minecraftforge.client.settings.KeyModifier otherKeyModifier = p_197983_1_.getKeyModifier();
         if (keyModifier.matches(p_197983_1_.getKey()) || otherKeyModifier.matches(getKey())) {
            return true;
         } else if (getKey().equals(p_197983_1_.getKey())) {
            // IN_GAME key contexts have a conflict when at least one modifier is NONE.
            // For example: If you hold shift to crouch, you can still press E to open your inventory. This means that a Shift+E hotkey is in conflict with E.
            // GUI and other key contexts do not have this limitation.
            return keyModifier == otherKeyModifier ||
               (getKeyConflictContext().conflicts(net.minecraftforge.client.settings.KeyConflictContext.IN_GAME) &&
               (keyModifier == net.minecraftforge.client.settings.KeyModifier.NONE || otherKeyModifier == net.minecraftforge.client.settings.KeyModifier.NONE));
         }
      }
      return this.key.equals(p_197983_1_.key);
   }

   public boolean isUnbound() {
      return this.key.equals(InputMappings.UNKNOWN);
   }

   public boolean matches(int p_197976_1_, int p_197976_2_) {
      if (p_197976_1_ == InputMappings.UNKNOWN.getValue()) {
         return this.key.getType() == InputMappings.Type.SCANCODE && this.key.getValue() == p_197976_2_;
      } else {
         return this.key.getType() == InputMappings.Type.KEYSYM && this.key.getValue() == p_197976_1_;
      }
   }

   public boolean matchesMouse(int p_197984_1_) {
      return this.key.getType() == InputMappings.Type.MOUSE && this.key.getValue() == p_197984_1_;
   }

   public ITextComponent getTranslatedKeyMessage() {
      return getKeyModifier().getCombinedName(key, () -> {
      return this.key.getDisplayName();
      });
   }

   public boolean isDefault() {
      return this.key.equals(this.defaultKey) && getKeyModifier() == getKeyModifierDefault();
   }

   public String saveString() {
      return this.key.getName();
   }

   public void setDown(boolean p_225593_1_) {
      this.isDown = p_225593_1_;
   }

   /****************** Forge Start *****************************/
   private net.minecraftforge.client.settings.KeyModifier keyModifierDefault = net.minecraftforge.client.settings.KeyModifier.NONE;
   private net.minecraftforge.client.settings.KeyModifier keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
   private net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext = net.minecraftforge.client.settings.KeyConflictContext.UNIVERSAL;

   /**
    * Convenience constructor for creating KeyBindings with keyConflictContext set.
    */
   public KeyBinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, final InputMappings.Type inputType, final int keyCode, String category) {
       this(description, keyConflictContext, inputType.getOrCreate(keyCode), category);
   }

   /**
    * Convenience constructor for creating KeyBindings with keyConflictContext set.
    */
   public KeyBinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, InputMappings.Input keyCode, String category) {
       this(description, keyConflictContext, net.minecraftforge.client.settings.KeyModifier.NONE, keyCode, category);
   }

   /**
    * Convenience constructor for creating KeyBindings with keyConflictContext and keyModifier set.
    */
   public KeyBinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, net.minecraftforge.client.settings.KeyModifier keyModifier, final InputMappings.Type inputType, final int keyCode, String category) {
       this(description, keyConflictContext, keyModifier, inputType.getOrCreate(keyCode), category);
   }

   /**
    * Convenience constructor for creating KeyBindings with keyConflictContext and keyModifier set.
    */
   public KeyBinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, net.minecraftforge.client.settings.KeyModifier keyModifier, InputMappings.Input keyCode, String category) {
      this.name = description;
      this.key = keyCode;
      this.defaultKey = keyCode;
      this.category = category;
      this.keyConflictContext = keyConflictContext;
      this.keyModifier = keyModifier;
      this.keyModifierDefault = keyModifier;
      if (this.keyModifier.matches(keyCode))
         this.keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
      ALL.put(description, this);
      MAP.addKey(keyCode, this);
      CATEGORIES.add(category);
   }

   @Override
   public InputMappings.Input getKey() {
       return this.key;
   }

   @Override
   public void setKeyConflictContext(net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext) {
       this.keyConflictContext = keyConflictContext;
   }

   @Override
   public net.minecraftforge.client.settings.IKeyConflictContext getKeyConflictContext() {
       return keyConflictContext;
   }

   @Override
   public net.minecraftforge.client.settings.KeyModifier getKeyModifierDefault() {
       return keyModifierDefault;
   }

   @Override
   public net.minecraftforge.client.settings.KeyModifier getKeyModifier() {
       return keyModifier;
   }

   @Override
   public void setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier keyModifier, InputMappings.Input keyCode) {
       this.key = keyCode;
       if (keyModifier.matches(keyCode))
           keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
       MAP.removeKey(this);
       this.keyModifier = keyModifier;
       MAP.addKey(keyCode, this);
   }
   /****************** Forge End *****************************/
}
