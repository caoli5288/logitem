package com.i5mc.logitem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Created by on 12月3日.
 */
@AllArgsConstructor
@Data
@RequiredArgsConstructor
public class Pair<K, V> {

    private final K key;
    private V value;
}
