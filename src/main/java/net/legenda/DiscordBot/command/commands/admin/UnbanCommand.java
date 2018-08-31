package net.legenda.DiscordBot.command.commands.admin;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command.cmdInfo(name = "Unban", description = "Unbans a user from the server", type = Command.Type.Admin, permission = Permission.BAN_MEMBERS)
public class UnbanCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length == 0)
            throw new InvalidCommandArgumentException("Usage: .Unban <UserID || USER#0000>");

        GuildController guildController = new GuildController(event.getGuild());
        String input = args[0];
        String regex = ".*#[0-9]{4}";
        AtomicReference<User> toUnban = new AtomicReference<>(null);
        if (input.matches(regex) || (input.matches("[0-9]+") && input.length() >= 17)) {
            String name = input.matches(regex) ? input.split("#")[0] : "";
            String descriminator = input.matches(regex) ? input.split("#")[1] : "";
            event.getGuild().getBanList().queue(bans -> {
                if (input.matches(regex))
                    bans.stream().filter(ban -> ban.getUser().getName().equalsIgnoreCase(name) && ban.getUser().getDiscriminator().equalsIgnoreCase(descriminator)).findFirst().ifPresent(banned -> toUnban.set(banned.getUser()));
                else
                    bans.stream().filter(ban -> ban.getUser().getId().equals(input)).findFirst().ifPresent(banned -> toUnban.set(banned.getUser()));

                if (toUnban.get() == null)
                    sendErrorMessage("Could not find user: " + input, event.getTextChannel(), false);
                guildController.unban(toUnban.get()).queue();
                sendEmbedMessage("Unbanned User: " + toUnban.get().getAsMention(), event.getTextChannel(), false);
            });
        } else
            throw new InvalidCommandArgumentException("Enter a valid UserID or enter USER#0000");
    }
}
