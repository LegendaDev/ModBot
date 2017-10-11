package net.legenda.DiscordBot.command.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.managers.CommandManager;

@Command.cmdInfo(name = "Help", description = "A help command")
public class HelpCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        StringBuilder msg = new StringBuilder();
        msg.append("*Admin Commands:* \n ");
        CommandManager.commands.values().stream().filter(cmd -> cmd.getType().equals(Type.Admin)).forEach(cmd -> msg.append(cmd.getName() + ": " + cmd.getDescription() + "\n "));

        msg.append("\n*Fun Commands:*\n ");
        CommandManager.commands.values().stream().filter(cmd -> cmd.getType().equals(Type.Fun)).forEach(cmd -> msg.append(cmd.getName() + ": " + cmd.getDescription() + "\n "));

        msg.append("\n*Other Commands:*\n ");
        CommandManager.commands.values().stream().filter(cmd -> cmd.getType().equals(Type.Misc)).forEach(cmd -> msg.append(cmd.getName() + ": " + cmd.getDescription() + "\n "));

        String message = new String(msg);
        sendEmbedMessage(message, event.getChannel(), false);

    }
}
