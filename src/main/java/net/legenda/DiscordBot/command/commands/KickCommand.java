package net.legenda.DiscordBot.command.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;

import java.util.Arrays;

@Command.cmdInfo(name = "Kick", description = "Kicks a user from the server", type = Command.Type.Admin, permission = Permission.KICK_MEMBERS)
public class KickCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        String reason;
        String[] arguments = event.getMessage().getRawContent().replaceAll("<@.*?>", "").split(" ");

        if (arguments.length <= 1) {
            throw new InvalidCommandArgumentException("Usage: .Kick <@User> <Reason>*");
        }
        reason = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));

        GuildController guildController = new GuildController(event.getGuild());
        User user = event.getMessage().getMentionedUsers().size() > 0 ? event.getMessage().getMentionedUsers().get(0) : null;
        Member toKick = event.getGuild().getMember(user);
        if (toKick != null) {
            if (reason != null)
                guildController.kick(toKick, reason).queue();
            else
                guildController.kick(toKick).queue();
            sendEmbedMessage("Kicked user: " + toKick.getAsMention(), event.getChannel(), false);

        } else {
            throw new InvalidCommandArgumentException("Could not find User");
        }
    }
}
