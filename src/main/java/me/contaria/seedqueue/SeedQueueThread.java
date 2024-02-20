package me.contaria.seedqueue;

import me.voidxwalker.autoreset.AtumCreateWorldScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.util.Optional;

public class SeedQueueThread extends Thread {

    private final Object lock = new Object();
    private volatile boolean running;

    public SeedQueueThread() {
        super("SeedQueue Thread");
    }

    @Override
    public void run() {
        this.running = true;
        while (this.running) {
            try {
                synchronized (this.lock) {
                    if (!SeedQueue.shouldGenerate()) {
                        this.lock.wait();
                        continue;
                    }
                }

                Optional<SeedQueueEntry> entryToUnpause = SeedQueue.getEntryMatching(entry -> entry.isPaused() && !entry.shouldPause());
                if (entryToUnpause.isPresent()) {
                    entryToUnpause.get().unpause();
                    continue;
                }

                Screen atumCreateWorldScreen = new AtumCreateWorldScreen(null);
                atumCreateWorldScreen.init(MinecraftClient.getInstance(), 0, 0);
            } catch (Exception e) {
                SeedQueue.LOGGER.error("Shutting down SeedQueue Thread...", e);
                this.stopQueue();
            }
        }
    }

    public void ping() {
        synchronized (this.lock) {
            this.lock.notify();
        }
    }

    public void stopQueue() {
        this.running = false;
    }
}
