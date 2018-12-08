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

@CommandInfo(name = "Ban", description = "Bans a user from the server", type = CommandType.Admin, permission = Permission.BAN_MEMBERS)
public class BanCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        String[] arguments = event.getMessage().getContentRaw().replaceAll("<@.*?>", "").split(" ");
        if (args.length == 0)
            throw new InvalidCommandArgumentException("Usage: `.Ban <@User (As many as you want)> <Reason>*`");

        String banReason = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
        GuildController guildController = new GuildController(event.getGuild());
        List<User> toBan = event.getMessage().getMentionedUsers();
        if (!toBan.isEmpty()) {
            StringBuilder banned = new StringBuilder();
            StringBuilder notBanned = new StringBuilder();
            toBan.forEach(user -> {
                try {
                    guildController.ban(user, 7, banReason).queue();
                    banned.append(" ").append(user.getAsMention());
                } catch (Exception e) {
                    notBanned.append(" ").append(user.getAsMention());
                }
            });
            if (notBanned.length() > 0)
                sendErrorMessage("Unable To Ban: " + notBanned.toString(), event.getTextChannel(), false);
            if (banned.length() > 0)
                sendEmbedMessage("Banned:" + banned.toString(), event.getTextChannel(), false);
        } else {
            throw new InvalidCommandArgumentException("Usage: `.Ban <@User (As many as you want)> <Reason>*`");
        }
    }
}