package com.fourshil.musicya.ui.editor;

import com.fourshil.musicya.data.repository.TagRepository;
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
public final class TagEditorViewModel_Factory implements Factory<TagEditorViewModel> {
  private final Provider<TagRepository> repositoryProvider;

  public TagEditorViewModel_Factory(Provider<TagRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public TagEditorViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static TagEditorViewModel_Factory create(Provider<TagRepository> repositoryProvider) {
    return new TagEditorViewModel_Factory(repositoryProvider);
  }

  public static TagEditorViewModel newInstance(TagRepository repository) {
    return new TagEditorViewModel(repository);
  }
}
