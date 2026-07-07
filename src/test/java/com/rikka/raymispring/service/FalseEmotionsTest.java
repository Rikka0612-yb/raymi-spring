package com.rikka.raymispring.service;

import com.rikka.raymispring.RaymiSpringApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 晏波
 * 2026/7/08 02:35
 */
@Slf4j
@ActiveProfiles({"dev", "secret"})
@SpringBootTest(classes = RaymiSpringApplication.class)
public class FalseEmotionsTest {
    private final Lock window = new ReentrantLock();
    private final Lock flame = new ReentrantLock();
    @Test
    void loopTest() throws InterruptedException {
        Thread p2 = new Thread(() -> {
            synchronized (window) {
                log.info("You hold the window, see though...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("You reach for the flame...");
                synchronized (flame) {
                    log.info("Window and flame, together at last...");
                }
            }
        },"yb");
        Thread p1 = new Thread(() -> {
            synchronized (flame) {
                log.info("She guards the flame,its warmth is hers...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("She asks you stay behind the window...");
                synchronized (window) {
                    log.info("She confirms: it's just a window.");
                }
            }
        },"ymx");
        p2.start();
        p1.start();
        p2.join();
        p1.join();
    }
}
