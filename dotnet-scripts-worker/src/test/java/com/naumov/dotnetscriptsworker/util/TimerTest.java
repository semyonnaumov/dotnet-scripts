package com.naumov.dotnetscriptsworker.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimerTest {

    @Test
    void testNegative() {
        Timer timer = new Timer(-500L);
        timer.start();
        assertTrue(timer.isFinished());
    }

    @Test
    void testPositive() throws InterruptedException {
        Timer timer = new Timer(1000L);
        timer.start();
        assertFalse(timer.isFinished());
        Thread.sleep(2000L);
        assertTrue(timer.isFinished());
        timer.start();
        assertFalse(timer.isFinished());
    }
}