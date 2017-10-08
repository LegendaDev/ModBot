package net.legenda.DiscordBot.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.legenda.DiscordBot.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

public abstract class Command {

    private String name = getClass().getAnnotation(CmdInfo.class).name();
    private String description = getClass().getAnnotation(CmdInfo.class).description();
    private Type type = getClass().getAnnotation(CmdInfo.class).type();
    private String role = getClass().getAnnotation(CmdInfo.class).role();
    private Permission perm = getClass().getAnnotation(CmdInfo.class).permission();

    /**
     * @param args  = The given arguments of the command
     * @param event = The MessageEvent
     */
    public abstract void execute(String[] args, MessageReceivedEvent event);

    public enum Type {
        Admin, Fun, Misc
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface CmdInfo {
        String name();

        String description();

        Type type() default Type.Misc;

        String role() default "@everyone";

        Permission permission() default Permission.UNKNOWN;

    }

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

    public Type getType() {
        return this.type;
    }

    protected static void sendEmbedMessage(String msg, MessageChannel channel, boolean delete) {
        channel.sendMessage(Main.INSTANCE.msgUtil.wrapMessage(msg)).queue();
        Message message = Main.INSTANCE.msgUtil.getMessage(msg, channel);
        if (delete && message != null) {
            channel.deleteMessageById(message.getId()).queueAfter(3l, TimeUnit.SECONDS);
        }
    }

    public static void sendErrorMessage(String msg, MessageChannel channel) {
        channel.sendMessage(Main.INSTANCE.msgUtil.wrapErrorMessage(msg)).queue();
    }

}
