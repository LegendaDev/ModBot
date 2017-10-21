package net.legenda.DiscordBot.command.commands.admin;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;

import java.util.Arrays;

@Command.cmdInfo(name = "Ban", description = "Bans a user from the server", type = Command.Type.Admin, permission = Permission.BAN_MEMBERS)
public class BanCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        String reason = "";
        String[] arguments = event.getMessage().getRawContent().replaceAll("<@.*?>", "").split(" ");

        if (arguments.length <= 1) {
            throw new InvalidCommandArgumentException("Usage: .Ban <@User> <Reason>*");
        }
        if (args.length > 1)
            reason = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));

        GuildController guildController = new GuildController(event.getGuild());
        User toBan = event.getMessage().getMentionedUsers().stream().findFirst().orElse(null);
        if (toBan != null) {
            if (!reason.isEmpty())
                guildController.ban(toBan, 7, reason).queue();
            else
                guildController.ban(toBan, 7).queue();
            sendEmbedMessage("Banned user: " + toBan.getAsMention(), event.getTextChannel(), false);

        } else
            throw new InvalidCommandArgumentException("Could not find User");

    }

}