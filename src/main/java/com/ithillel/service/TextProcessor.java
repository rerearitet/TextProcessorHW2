package com.ithillel.service;

public interface TextProcessor {
    void save(String key, String text);
    String getByKey(String key);
}
