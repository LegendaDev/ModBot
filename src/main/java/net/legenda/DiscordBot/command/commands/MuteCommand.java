package net.legenda.DiscordBot.command.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;

@Command.cmdInfo(name = "Mute", description = "Mute a user on the server", type = Command.Type.Admin, permission = Permission.MESSAGE_MANAGE)
public class MuteCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if(args.length <= 1)
            throw new InvalidCommandArgumentException("Usage: .Mute <@User>");
        User toMute = event.getMessage().getMentionedUsers().size() > 0 ? event.getMessage().getMentionedUsers().get(0) : null;
        if (toMute != null) {
            sendEmbedMessage("Muted user " + toMute.getAsMention(), event.getChannel(), false);
        } else
            throw new InvalidCommandArgumentException("Could not find User");
    }
}
