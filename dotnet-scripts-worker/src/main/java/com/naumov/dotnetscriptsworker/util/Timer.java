package com.naumov.dotnetscriptsworker.util;

public final class Timer {
    private final long durationMs;
    private long endTimeMs;

    public Timer(long durationMs) {
        this.durationMs = durationMs;
    }

    public void start() {
        this.endTimeMs = System.currentTimeMillis() + durationMs;
    }

    public boolean isFinished() {
        return System.currentTimeMillis() >= endTimeMs;
    }
}
