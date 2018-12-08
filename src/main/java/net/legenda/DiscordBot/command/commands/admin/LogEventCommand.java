package net.legenda.DiscordBot.command.commands.admin;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.command.CommandInfo;
import net.legenda.DiscordBot.command.CommandType;
import net.legenda.DiscordBot.config.Preset;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;

@CommandInfo(name = "Logbans", description = "Logs bans to a specified channel", type = CommandType.Admin, permission = Permission.ADMINISTRATOR)
public class LogEventCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length < 1)
            throw new InvalidCommandArgumentException("Usage: `.Logbans <#Channel/NONE>`");

        String preset = "NONE";
        if (!args[0].equalsIgnoreCase("none"))
            if (event.getMessage().getMentionedChannels().isEmpty()) {
                throw new InvalidCommandArgumentException("Usage: `.Logbans <#Channel/NONE>`");
            } else {
                TextChannel channel = event.getMessage().getMentionedChannels().get(0);
                preset = channel.getId();
                sendEmbedMessage("Logging Bans In " + channel.getAsMention(), event.getTextChannel(), false);
            }
        else
            sendEmbedMessage("No Longer Logging Bans", event.getTextChannel(), false);

        Main.INSTANCE.configManager.appendPreset(event.getGuild(), new Preset("LogChannel", preset));
    }
}
