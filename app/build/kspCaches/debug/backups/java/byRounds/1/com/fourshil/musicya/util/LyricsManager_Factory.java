package com.fourshil.musicya.util;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class LyricsManager_Factory implements Factory<LyricsManager> {
  private final Provider<Context> contextProvider;

  public LyricsManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public LyricsManager get() {
    return newInstance(contextProvider.get());
  }

  public static LyricsManager_Factory create(Provider<Context> contextProvider) {
    return new LyricsManager_Factory(contextProvider);
  }

  public static LyricsManager newInstance(Context context) {
    return new LyricsManager(context);
  }
}
