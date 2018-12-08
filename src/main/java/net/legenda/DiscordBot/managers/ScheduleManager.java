package net.legenda.DiscordBot.managers;

import net.dv8tion.jda.core.entities.Guild;
import net.legenda.DiscordBot.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ScheduleManager {

    private final ScheduledExecutorService scheduler;
    private final Map<Guild, Future> schedules = new HashMap<>();

    public ScheduleManager() {
        scheduler = Executors.newScheduledThreadPool(1);
    }

    public void schedule(Guild guild, Runnable runnable, long delay, TimeUnit unit) {
        remove(guild);
        schedules.put(guild, scheduler.schedule(runnable, delay, unit));
    }

    public void scheduleAudioClose() {
        scheduler.scheduleAtFixedRate(() -> Main.INSTANCE.jdaBot.getAudioManagers().forEach(audioManager -> {
            if (audioManager.getConnectedChannel().getMembers().size() <= 1) {
                Guild guild = audioManager.getGuild();
                schedule(guild, () -> {
                    Main.INSTANCE.musicUtils.closeAudio(guild);
                    remove(guild);
                }, 1, TimeUnit.MINUTES);
            }
        }), 1, 1, TimeUnit.MINUTES);
    }

    public void remove(Guild guild) {
        Future currentSchedule = schedules.get(guild);
        if (currentSchedule != null) {
            if (!currentSchedule.isCancelled())
                currentSchedule.cancel(true);
            schedules.remove(guild);
        }
    }
}
