package com.stardust.util;

import androidx.annotation.NonNull;

import java.util.Map;
import java.util.Set;

public interface BiMap<K, V> extends Map<K, V> {

    K getKey(V value);

    @NonNull
    Set<V> valueSet();

    V getOr(K key, V def);

    K getKeyOr(V value, K def);

}
