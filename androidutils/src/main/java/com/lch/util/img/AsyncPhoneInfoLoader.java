package com.lch.util.img;

public class AsyncPhoneInfoLoader extends AsyncDataLoader<String, String> {
    @Override
    protected String getCache(String param) {
        return null;
    }

    @Override
    protected void putCache(String param, String res) {

    }

    @Override
    protected String loadImpl(String param) {
        return null;//TODO query number info by number.

    }
}
