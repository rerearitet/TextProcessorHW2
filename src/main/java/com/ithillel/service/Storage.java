package com.ithillel.service;

import java.util.function.BiFunction;

public interface Storage {
    void put(String key, String value);
    String get(String key);
    String compute(String key, BiFunction<String, String, String> remappingFunction);
}
