package net.minecraft.client.audio;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class BackgroundMusicTracks {
   public static final BackgroundMusicSelector MENU = new BackgroundMusicSelector(SoundEvents.MUSIC_MENU, 20, 600, true);
   public static final BackgroundMusicSelector CREATIVE = new BackgroundMusicSelector(SoundEvents.MUSIC_CREATIVE, 12000, 24000, false);
   public static final BackgroundMusicSelector CREDITS = new BackgroundMusicSelector(SoundEvents.MUSIC_CREDITS, 0, 0, true);
   public static final BackgroundMusicSelector END_BOSS = new BackgroundMusicSelector(SoundEvents.MUSIC_DRAGON, 0, 0, true);
   public static final BackgroundMusicSelector END = new BackgroundMusicSelector(SoundEvents.MUSIC_END, 6000, 24000, true);
   public static final BackgroundMusicSelector UNDER_WATER = createGameMusic(SoundEvents.MUSIC_UNDER_WATER);
   public static final BackgroundMusicSelector GAME = createGameMusic(SoundEvents.MUSIC_GAME);

   public static BackgroundMusicSelector createGameMusic(SoundEvent p_232677_0_) {
      return new BackgroundMusicSelector(p_232677_0_, 12000, 24000, false);
   }
}
