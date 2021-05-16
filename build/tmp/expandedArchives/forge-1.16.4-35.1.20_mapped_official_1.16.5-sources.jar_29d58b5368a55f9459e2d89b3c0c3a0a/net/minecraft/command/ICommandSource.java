package net.minecraft.command;

import java.util.UUID;
import net.minecraft.util.text.ITextComponent;

public interface ICommandSource {
   ICommandSource NULL = new ICommandSource() {
      public void sendMessage(ITextComponent p_145747_1_, UUID p_145747_2_) {
      }

      public boolean acceptsSuccess() {
         return false;
      }

      public boolean acceptsFailure() {
         return false;
      }

      public boolean shouldInformAdmins() {
         return false;
      }
   };

   void sendMessage(ITextComponent p_145747_1_, UUID p_145747_2_);

   boolean acceptsSuccess();

   boolean acceptsFailure();

   boolean shouldInformAdmins();
}
