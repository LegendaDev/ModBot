package net.legenda.DiscordBot.command.commands.admin;

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
        String[] arguments = event.getMessage().getContentRaw().replaceAll("<@.*?>", "").split(" ");
        if (arguments.length <= 1)
            throw new InvalidCommandArgumentException("Usage: .Kick <@User> <Reason>*");

        String reason = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
        GuildController guildController = new GuildController(event.getGuild());
        User user = event.getMessage().getMentionedUsers().stream().findFirst().orElse(null);
        Member toKick = event.getGuild().getMember(user);
        if (toKick != null) {
            guildController.kick(toKick, reason).queue();
            sendEmbedMessage("Kicked user: " + toKick.getAsMention(), event.getTextChannel(), false);
        } else
            throw new InvalidCommandArgumentException("Could not find User");

    }
}
