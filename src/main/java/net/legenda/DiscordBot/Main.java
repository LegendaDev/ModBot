package net.legenda.DiscordBot;

import net.dv8tion.jda.core.utils.SimpleLog;
import net.legenda.DiscordBot.listeners.AntiSpam;
import net.legenda.DiscordBot.managers.CommandManager;
import net.legenda.DiscordBot.managers.MessageManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.legenda.DiscordBot.utils.MessageUtils;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Main {

    public static final Main INSTANCE = new Main();

    private static JDA jdaBot;
    public String cmdPrefix = ".";
    public MessageUtils msgUtil = new MessageUtils();
    public CommandManager cmdManager = new CommandManager();

    public static HashMap<String, Role> roles;
    private static final SimpleLog LOGGER = SimpleLog.getLog(Main.class);

    public static void main(String[] args) throws LoginException, InterruptedException, RateLimitedException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken("MzQ4NDY0MzA1MTY0OTEwNjA1.DHnYmg.j3p3NPbxKTTcFOAF_gmyo01t_NI");
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setAutoReconnect(true);
        builder.setAudioEnabled(true);
        builder.setGame(Game.of(".help"));

        jdaBot = builder.buildBlocking();
        jdaBot.addEventListener(new MessageManager());
        jdaBot.addEventListener(new AntiSpam());

        getRoles();

        LOGGER.info("Bot Initialised");

    }

    private static void getRoles(){
        roles = new LinkedHashMap<>();
        List<Guild> guilds = jdaBot.getGuilds();
        for(Guild guild : guilds){
            guild.getRoles().forEach(role -> roles.put(role.getId(), role));
        }
    }

}