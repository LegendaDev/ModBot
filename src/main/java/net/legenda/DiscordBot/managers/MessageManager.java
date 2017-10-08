package net.legenda.DiscordBot.managers;

import net.dv8tion.jda.core.entities.Message;
import net.legenda.DiscordBot.command.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.legenda.DiscordBot.exceptions.IllegalCommandAccessException;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;
import net.legenda.DiscordBot.exceptions.InvalidCommandException;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

import java.util.HashMap;

public class MessageManager extends ListenerAdapter {

    private static HashMap<String, Message> sent = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            CommandManager.execute(event);
            sent.put(event.getMessage().getContent(), event.getMessage());
        } catch (InvalidCommandArgumentException |InvalidCommandStateException|IllegalCommandAccessException|InvalidCommandException e) {
            Command.sendErrorMessage(e.getLocalizedMessage(), event.getTextChannel());
        }
    }
}
