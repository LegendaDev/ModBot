package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;

@Command.cmdInfo(name = "Skip", description = "Skips the current playing song", type = Command.Type.Music, role = "DJ")
public class SkipCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Main.INSTANCE.musicUtils.skipTrack(event.getTextChannel(), 1);
    }
}
