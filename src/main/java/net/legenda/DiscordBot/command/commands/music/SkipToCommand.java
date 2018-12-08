package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.command.CommandInfo;
import net.legenda.DiscordBot.command.CommandType;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;

@CommandInfo(name = "SkipTo", description = "Skips to a position within the queue", type = CommandType.Music, role = "DJ")
public class SkipToCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length < 1)
            throw new InvalidCommandArgumentException("Usage: `.SkipTo (Position)`");
        int position = Integer.parseInt(args[0]);
        Main.INSTANCE.musicUtils.skipTrack(event.getTextChannel(), position);
    }

}
