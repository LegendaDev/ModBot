package net.legenda.DiscordBot.command.commands.admin;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.command.CommandInfo;
import net.legenda.DiscordBot.command.CommandType;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;

import java.util.Arrays;
import java.util.List;

@CommandInfo(name = "Kick", description = "Kicks a user from the server", type = CommandType.Admin, permission = Permission.KICK_MEMBERS)
public class KickCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        String[] arguments = event.getMessage().getContentRaw().replaceAll("<@.*?>", "").split(" ");
        if (args.length == 0)
            throw new InvalidCommandArgumentException("Usage: `.Kick <@User (As many as you want)> <Reason>*`");

        String kickReason = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
        GuildController guildController = new GuildController(event.getGuild());
        List<User> toKick = event.getMessage().getMentionedUsers();
        if (!toKick.isEmpty()) {
            StringBuilder kicked = new StringBuilder();
            StringBuilder notKicked = new StringBuilder();
            toKick.forEach(user -> {
                try {
                    guildController.kick(event.getGuild().getMember(user), kickReason).queue();
                    kicked.append(" ").append(user.getAsMention());
                } catch (Exception e) {
                    notKicked.append(" ").append(user.getAsMention());
                }
            });
            if (notKicked.length() > 0)
                sendErrorMessage("Unable To Kick: " + notKicked.toString(), event.getTextChannel(), false);
            if (kicked.length() > 0)
                sendEmbedMessage("Kicked:" + kicked.toString(), event.getTextChannel(), false);
        } else {
            throw new InvalidCommandArgumentException("Usage: `.Kick <@User (As many as you want)> <Reason>*`");
        }
    }
}
