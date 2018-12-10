package net.legenda.DiscordBot.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.legenda.DiscordBot.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public abstract class Command {

    private final String name = getClass().getAnnotation(CommandInfo.class).name();
    private final String description = getClass().getAnnotation(CommandInfo.class).description();
    private final CommandType type = getClass().getAnnotation(CommandInfo.class).type();
    private final String role = getClass().getAnnotation(CommandInfo.class).role();
    private final Permission perm = getClass().getAnnotation(CommandInfo.class).permission();
    private final String[] aliases = getClass().getAnnotation(CommandInfo.class).aliases();
    private final String[] args = getClass().getAnnotation(CommandInfo.class).args();

    /**
     * @param args  = The given arguments of the command
     * @param event = The MessageEvent
     */
    public abstract void execute(String[] args, MessageReceivedEvent event);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getRole() {
        return role;
    }

    public Permission getPermission() {
        return this.perm;
    }

    public CommandType getType() {
        return this.type;
    }

    public String[] getAlias() {
        return this.aliases;
    }

    public String[] getArgs() {
        return this.args;
    }

    protected static void sendMessage(String msg, TextChannel channel) {
        channel.sendMessage(msg).queue();
    }

    public static void sendEmbedMessage(String msg, TextChannel channel, boolean delete) {
        channel.sendMessage(Main.INSTANCE.msgUtil.wrapMessage(msg)).queue(sent -> {
            if (delete)
                sent.delete().queueAfter(3L, TimeUnit.SECONDS);
        });
    }

    public static void sendErrorMessage(String msg, TextChannel channel, boolean delete) {
        channel.sendMessage(Main.INSTANCE.msgUtil.wrapErrorMessage(msg)).queue(sent -> {
            if (delete)
                sent.delete().queueAfter(3L, TimeUnit.SECONDS);
        });
    }


}
