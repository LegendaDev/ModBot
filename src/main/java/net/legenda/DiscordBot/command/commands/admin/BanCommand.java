package net.legenda.DiscordBot.command.commands.admin;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;

import java.util.Arrays;
import java.util.List;

@Command.cmdInfo(name = "Ban", description = "Bans a user from the server", type = Command.Type.Admin, permission = Permission.BAN_MEMBERS)
public class BanCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        String banReason = "";
        String[] arguments = event.getMessage().getContentRaw().replaceAll("<@.*?>", "").split(" ");

        if (args.length == 0) {
            throw new InvalidCommandArgumentException("Usage: .Ban <@User (As many as you want)> <Reason>*");
        }
        if (args.length > 1)
            banReason = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));

        GuildController guildController = new GuildController(event.getGuild());
        List<User> toBan = event.getMessage().getMentionedUsers();
        if (!toBan.isEmpty()) {
            if (!banReason.isEmpty()){
                String reason = banReason;
                toBan.forEach(user -> guildController.ban(user, 7, reason).queue());
            } else {
                toBan.forEach(user -> guildController.ban(user, 7).queue());
            }
            StringBuilder banned = new StringBuilder();
            toBan.forEach(user -> banned.append(" ").append(user.getAsMention()));
            sendEmbedMessage("Banned:" + banned.toString(), event.getTextChannel(), false);

        } else
            throw new InvalidCommandArgumentException("Could not find user");
    }

}