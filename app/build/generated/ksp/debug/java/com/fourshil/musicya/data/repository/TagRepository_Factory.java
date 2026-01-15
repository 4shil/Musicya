package com.fourshil.musicya.data.repository;

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
public final class TagRepository_Factory implements Factory<TagRepository> {
  private final Provider<Context> contextProvider;

  public TagRepository_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public TagRepository get() {
    return newInstance(contextProvider.get());
  }

  public static TagRepository_Factory create(Provider<Context> contextProvider) {
    return new TagRepository_Factory(contextProvider);
  }

  public static TagRepository newInstance(Context context) {
    return new TagRepository(context);
  }
}
