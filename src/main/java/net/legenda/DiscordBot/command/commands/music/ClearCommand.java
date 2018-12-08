package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

@Command.cmdInfo(name = "Clear", description = "Clears the current queue", type = Command.Type.Music, role = "DJ")
public class ClearCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (Main.INSTANCE.musicUtils.getTrackManager(event.getGuild()).getQueue().isEmpty())
            throw new InvalidCommandStateException("The Queue is already empty");
        Main.INSTANCE.musicUtils.clear(event.getGuild());
        sendMessage(":arrows_counterclockwise: Cleared Queue:", event.getTextChannel());
    }
}
