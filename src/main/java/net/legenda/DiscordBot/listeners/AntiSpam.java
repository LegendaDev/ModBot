package net.legenda.DiscordBot.listeners;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;
import java.util.stream.Collectors;

public class AntiSpam extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean spam = false;
        List<Message> previousMessages = event.getChannel().getIterableHistory().stream().limit(20).collect(Collectors.toList());
        for(Message msg : previousMessages){
            if(msg.getAuthor().equals(event.getAuthor()))
                if(event.getMessage().getCreationTime().toEpochSecond() - msg.getCreationTime().toEpochSecond() < 300l){
                    spam = true;
                    break;
                }
        }
        if(spam && !event.getAuthor().isBot()){
            //event.getMessage().delete().queueAfter(3l, TimeUnit.SECONDS);
            //Command.sendEmbedMessage("Stop spamming :rage: " + event.getAuthor().getAsMention(), event.getChannel(), true);
        }

    }

}
