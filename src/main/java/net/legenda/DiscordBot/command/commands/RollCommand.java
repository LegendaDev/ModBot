package net.legenda.DiscordBot.command.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

@Command.cmdInfo(name = "Roll", description = "Rolls a dice", type = Command.Type.Fun)
public class RollCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        int sides = 6;
        if (args.length >= 1)
            try {
                sides = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                throw new InvalidCommandStateException("Number of sides of the dice must be a number");
            }
        int output = (int) Math.round(Math.random() * (sides - 1) + 1);
        sendEmbedMessage("Rolled a " + output + " (" + sides + ")", event.getTextChannel(), false);
    }

}
