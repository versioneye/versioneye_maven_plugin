package com.versioneye.utils;

import java.util.Collection;

/**
 * @author glick
 */
@SuppressWarnings("WeakerAccess")
public class CollectionUtils
{
  private CollectionUtils() {

  }

  public static boolean collectionIsEmpty(Collection collection) {
    return collection == null || collection.isEmpty();
  }
  public static boolean collectionNotEmpty(Collection collection) {
    return !collectionIsEmpty(collection);
  }
}
