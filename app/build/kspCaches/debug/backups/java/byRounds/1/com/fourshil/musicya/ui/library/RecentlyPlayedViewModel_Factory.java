package com.fourshil.musicya.ui.library;

import com.fourshil.musicya.data.db.MusicDao;
import com.fourshil.musicya.data.repository.MusicRepository;
import com.fourshil.musicya.player.PlayerController;
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
public final class RecentlyPlayedViewModel_Factory implements Factory<RecentlyPlayedViewModel> {
  private final Provider<MusicDao> musicDaoProvider;

  private final Provider<MusicRepository> musicRepositoryProvider;

  private final Provider<PlayerController> playerControllerProvider;

  public RecentlyPlayedViewModel_Factory(Provider<MusicDao> musicDaoProvider,
      Provider<MusicRepository> musicRepositoryProvider,
      Provider<PlayerController> playerControllerProvider) {
    this.musicDaoProvider = musicDaoProvider;
    this.musicRepositoryProvider = musicRepositoryProvider;
    this.playerControllerProvider = playerControllerProvider;
  }

  @Override
  public RecentlyPlayedViewModel get() {
    return newInstance(musicDaoProvider.get(), musicRepositoryProvider.get(), playerControllerProvider.get());
  }

  public static RecentlyPlayedViewModel_Factory create(Provider<MusicDao> musicDaoProvider,
      Provider<MusicRepository> musicRepositoryProvider,
      Provider<PlayerController> playerControllerProvider) {
    return new RecentlyPlayedViewModel_Factory(musicDaoProvider, musicRepositoryProvider, playerControllerProvider);
  }

  public static RecentlyPlayedViewModel newInstance(MusicDao musicDao,
      MusicRepository musicRepository, PlayerController playerController) {
    return new RecentlyPlayedViewModel(musicDao, musicRepository, playerController);
  }
}
