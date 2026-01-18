package com.fourshil.musicya;

import com.fourshil.musicya.data.SettingsPreferences;
import com.fourshil.musicya.player.PlayerController;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<SettingsPreferences> settingsPreferencesProvider;

  private final Provider<PlayerController> playerControllerProvider;

  public MainActivity_MembersInjector(Provider<SettingsPreferences> settingsPreferencesProvider,
      Provider<PlayerController> playerControllerProvider) {
    this.settingsPreferencesProvider = settingsPreferencesProvider;
    this.playerControllerProvider = playerControllerProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<SettingsPreferences> settingsPreferencesProvider,
      Provider<PlayerController> playerControllerProvider) {
    return new MainActivity_MembersInjector(settingsPreferencesProvider, playerControllerProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectSettingsPreferences(instance, settingsPreferencesProvider.get());
    injectPlayerController(instance, playerControllerProvider.get());
  }

  @InjectedFieldSignature("com.fourshil.musicya.MainActivity.settingsPreferences")
  public static void injectSettingsPreferences(MainActivity instance,
      SettingsPreferences settingsPreferences) {
    instance.settingsPreferences = settingsPreferences;
  }

  @InjectedFieldSignature("com.fourshil.musicya.MainActivity.playerController")
  public static void injectPlayerController(MainActivity instance,
      PlayerController playerController) {
    instance.playerController = playerController;
  }
}
