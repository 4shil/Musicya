package com.fourshil.musicya.ui.nowplaying;

import com.fourshil.musicya.data.db.MusicDao;
import com.fourshil.musicya.player.PlayerController;
import com.fourshil.musicya.util.LyricsManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class NowPlayingViewModel_Factory implements Factory<NowPlayingViewModel> {
  private final Provider<PlayerController> playerControllerProvider;

  private final Provider<MusicDao> musicDaoProvider;

  private final Provider<LyricsManager> lyricsManagerProvider;

  public NowPlayingViewModel_Factory(Provider<PlayerController> playerControllerProvider,
      Provider<MusicDao> musicDaoProvider, Provider<LyricsManager> lyricsManagerProvider) {
    this.playerControllerProvider = playerControllerProvider;
    this.musicDaoProvider = musicDaoProvider;
    this.lyricsManagerProvider = lyricsManagerProvider;
  }

  @Override
  public NowPlayingViewModel get() {
    return newInstance(playerControllerProvider.get(), musicDaoProvider.get(), lyricsManagerProvider.get());
  }

  public static NowPlayingViewModel_Factory create(
      Provider<PlayerController> playerControllerProvider, Provider<MusicDao> musicDaoProvider,
      Provider<LyricsManager> lyricsManagerProvider) {
    return new NowPlayingViewModel_Factory(playerControllerProvider, musicDaoProvider, lyricsManagerProvider);
  }

  public static NowPlayingViewModel newInstance(PlayerController playerController,
      MusicDao musicDao, LyricsManager lyricsManager) {
    return new NowPlayingViewModel(playerController, musicDao, lyricsManager);
  }
}
