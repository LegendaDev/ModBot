package net.legenda.DiscordBot.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.legenda.DiscordBot.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

public abstract class Command {

    private String name = getClass().getAnnotation(cmdInfo.class).name();
    private String description = getClass().getAnnotation(cmdInfo.class).description();
    private Type type = getClass().getAnnotation(cmdInfo.class).type();
    private String role = getClass().getAnnotation(cmdInfo.class).role();
    private Permission perm = getClass().getAnnotation(cmdInfo.class).permission();


    /**
     * @param args  = The given arguments of the command
     * @param event = The MessageEvent
     */
    public abstract void execute(String[] args, MessageReceivedEvent event);

    public enum Type {
        Admin, Music, Fun, Misc
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface cmdInfo {
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

    protected static void sendMessage(String msg, TextChannel channel){
        channel.sendMessage(msg).queue();
    }

    protected static void sendEmbedMessage(String msg, TextChannel channel, boolean delete) {
        channel.sendMessage(Main.INSTANCE.msgUtil.wrapMessage(msg)).queue(sent -> {
            if (delete)
                sent.delete().queueAfter(3l, TimeUnit.SECONDS);
        });
    }

    public static void sendErrorMessage(String msg, TextChannel channel, boolean delete) {
        channel.sendMessage(Main.INSTANCE.msgUtil.wrapErrorMessage(msg)).queue(sent -> {
            if (delete)
                sent.delete().queueAfter(3l, TimeUnit.SECONDS);
        });
    }


}
