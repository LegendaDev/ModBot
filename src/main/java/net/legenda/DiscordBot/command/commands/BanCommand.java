package net.legenda.DiscordBot.command.commands;

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
        int days = 0;
        String[] arguments = event.getMessage().getRawContent().replaceAll("<@.*?>", "").split(" ");

        if (arguments.length <= 1) {
            throw new InvalidCommandArgumentException("Usage: .Ban <@User> <Del Msgs (Days)> <Reason>*");
        }
        if (args.length > 1)
            days = Integer.parseInt(arguments[1]);
        if (args.length > 2)
            reason = String.join(" ", Arrays.copyOfRange(arguments, 2, arguments.length));

        GuildController guildController = new GuildController(event.getGuild());
        User toBan = event.getMessage().getMentionedUsers().size() > 0 ? event.getMessage().getMentionedUsers().get(0) : null;
        if (toBan != null) {
            if (!reason.isEmpty())
                guildController.ban(toBan, days, reason).queue();
            else
                guildController.ban(toBan, days).queue();
            sendEmbedMessage("Banned user: " + toBan.getAsMention(), event.getChannel(), false);

        } else
            throw new InvalidCommandArgumentException("Could not find User");

    }

}