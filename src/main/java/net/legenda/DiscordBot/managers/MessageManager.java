package net.legenda.DiscordBot.managers;

import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageManager extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            CommandManager.execute(event);
        } catch(Exception e){
            Main.LOGGER.warn(e.getLocalizedMessage());
            Command.sendErrorMessage(e.getLocalizedMessage(), event.getTextChannel(),false);
        }
    }
}
