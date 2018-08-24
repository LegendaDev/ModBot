package net.legenda.DiscordBot.command.commands.admin;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command.cmdInfo(name = "Unban", description = "Unbans a user from the server", type = Command.Type.Admin, permission = Permission.BAN_MEMBERS)
public class UnbanCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length == 0) {
            throw new InvalidCommandArgumentException("Usage: .Unban <UserID || USER#0000>");
        }
        GuildController guildController = new GuildController(event.getGuild());
        String input = args[0];
        String regex = ".*#[0-9]{4}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        if (m.find()) {
            String name = input.split("#")[0];
            String descriminator = input.split("#")[1];
            event.getGuild().getBanList().queue(bans -> {
                User toUnbanDescrim = Objects.requireNonNull(bans.stream().filter(ban -> ban.getUser().getName().equalsIgnoreCase(name) && ban.getUser().getDiscriminator().equalsIgnoreCase(descriminator)).findFirst().orElse(null)).getUser();
                guildController.unban(toUnbanDescrim).queue();
                sendEmbedMessage("Unbanned User: " + toUnbanDescrim.getAsMention(), event.getTextChannel(), false);
            });
        } else {
            User toUnbanID = Main.jdaBot.getUserById(input);
            if (toUnbanID != null) {
                guildController.unban(toUnbanID).queue();
                sendEmbedMessage("Unbanned User: " + toUnbanID.getAsMention(), event.getTextChannel(), false);
            } else {
                throw new InvalidCommandArgumentException("User " + input + " is not banned");
            }
        }
    }
}
