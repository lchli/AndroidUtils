package com.lch.util.http;

import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;
public class LengthOutputStream extends OutputStream {

    private final AtomicLong mLength = new AtomicLong(0L);

    public LengthOutputStream() {
    }

    public long getLength() {
        return mLength.get();
    }

    public void write(long multiByte) {
        mLength.addAndGet(multiByte);
    }

    @Override
    public void write(int oneByte) {
        mLength.addAndGet(1);
    }

    @Override
    public void write(byte[] buffer) {
        mLength.addAndGet(buffer.length);
    }

    @Override
    public void write(byte[] buffer, int offset, int count) {
        mLength.addAndGet(count);
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }
}