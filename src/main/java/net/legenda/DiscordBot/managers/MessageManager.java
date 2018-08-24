package net.legenda.DiscordBot.managers;

import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.legenda.DiscordBot.exceptions.IllegalCommandAccessException;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;
import net.legenda.DiscordBot.exceptions.InvalidCommandException;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

public class MessageManager extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            CommandManager.execute(event);
        } catch (IllegalCommandAccessException | InvalidCommandArgumentException | InvalidCommandException | InvalidCommandStateException e) {
            Command.sendErrorMessage(e.getLocalizedMessage(), event.getTextChannel(), false);
        } catch (Exception e) {
            new Thread(() -> Main.LOGGER.warn("UNHANDLED EXCEPTION :" + e.getMessage())).start();
            System.out.print("-----------------------------------------------------\n");
            e.printStackTrace();
        }
    }
}
