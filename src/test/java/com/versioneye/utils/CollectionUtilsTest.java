package com.versioneye.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author glick
 */
public class CollectionUtilsTest
{
  @Test
  public void testNullList() {
    List<String> nullList = null;

    assertThat(CollectionUtils.collectionIsEmpty(nullList)).isTrue();
  }

  @Test
  public void testEmptyList() {
    List<String> emptyList = new ArrayList();

    assertThat(CollectionUtils.collectionIsEmpty(emptyList)).isTrue();
  }

  @Test
  public void demonstrateNonEmptyList() {
    List<String> hasContent = Arrays.asList("abcd", "efgh");

    assertThat(CollectionUtils.collectionIsEmpty(hasContent)).isFalse();
    assertThat(CollectionUtils.collectionNotEmpty(hasContent)).isTrue();
  }
}
