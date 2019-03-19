package net.legenda.DiscordBot;

import net.dv8tion.jda.core.entities.Game;
import net.legenda.DiscordBot.listeners.AntiSpam;
import net.legenda.DiscordBot.listeners.ModEventsListener;
import net.legenda.DiscordBot.managers.CommandManager;
import net.legenda.DiscordBot.managers.ConfigManager;
import net.legenda.DiscordBot.managers.MessageManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.legenda.DiscordBot.managers.ScheduleManager;
import net.legenda.DiscordBot.utils.MessageUtils;
import net.legenda.DiscordBot.utils.MusicUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public enum Main {
    INSTANCE;

    public JDA jdaBot;
    public final String cmdPrefix = ".";
    public final MessageUtils msgUtil = new MessageUtils();
    public final MusicUtils musicUtils = new MusicUtils();
    public final CommandManager cmdManager = new CommandManager();
    public final ConfigManager configManager = new ConfigManager();
    public final ScheduleManager scheduleManager = new ScheduleManager();

    public final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws LoginException, InterruptedException {
        String token = INSTANCE.configManager.getToken(args[0]);
        JDABuilder builder = new JDABuilder(AccountType.BOT)
                .setToken(token)
                .setStatus(OnlineStatus.ONLINE)
                .setAutoReconnect(true)
                .setAudioEnabled(true)
                .setGame(Game.of(Game.GameType.STREAMING, "Porn", "https://www.twitch.tv/ninja"))
                .addEventListener(new MessageManager(), new AntiSpam(), new ModEventsListener());

        INSTANCE.jdaBot = builder.build().awaitReady();
        INSTANCE.cmdManager.initCommands();
        INSTANCE.musicUtils.setup();
        INSTANCE.configManager.readData(args[1]);
        INSTANCE.LOGGER.info("Bot Initialised");

        Runtime.getRuntime().addShutdownHook(new Thread(INSTANCE::onShutdown));
    }

    public void onShutdown() {
        configManager.saveData();
        INSTANCE.LOGGER.info("Shutting Down...");
    }

}