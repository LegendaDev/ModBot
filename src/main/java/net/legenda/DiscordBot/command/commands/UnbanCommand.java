package net.legenda.DiscordBot.command.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;

@Command.cmdInfo(name = "Unban", description = "Unbans a user from the server", type = Command.Type.Admin, permission = Permission.BAN_MEMBERS)
public class UnbanCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length <= 1) {
            throw new InvalidCommandArgumentException("Usage: .Unban <@User>");
        }
        GuildController guildController = new GuildController(event.getGuild());
        User toUnban = event.getMessage().getMentionedUsers().size() > 0 ? event.getMessage().getMentionedUsers().get(0) : null;
        if (toUnban != null) {
            guildController.unban(toUnban).queue();
            sendEmbedMessage("Unbanned user: " + toUnban.getAsMention(), event.getChannel(), false);
        } else
            throw new InvalidCommandArgumentException("Could not find User");

    }

}
