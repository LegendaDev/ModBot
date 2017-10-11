package net.legenda.DiscordBot.listeners;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.legenda.DiscordBot.command.Command;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AntiSpam extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        List<Message> history = event.getTextChannel().getIterableHistory().complete().stream().limit(3).filter(msg -> !msg.equals(event.getMessage())).collect(Collectors.toList());
        int spam = history.stream().filter(message -> message.getAuthor().equals(event.getAuthor()) && !message.getAuthor().isBot()).filter(msg -> (event.getMessage().getCreationTime().toEpochSecond() - msg.getCreationTime().toEpochSecond()) < 2).collect(Collectors.toList()).size();
        if(spam == 1){
            Command.sendErrorMessage("Please leave 2 seconds between messages, " + event.getAuthor().getAsMention(), event.getChannel());
            event.getMessage().delete().queue();

        } else if (spam > 5) {
        }
    }

    public static boolean isSpamming(Guild guild){
        return false;
    }
}
