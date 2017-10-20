package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;

@Command.cmdInfo(name = "Stop", description = "Pauses current song", type = Command.Type.Music, alias = {"pause"})
public class StopCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Main.INSTANCE.musicUtils.pause(event.getGuild());
        sendMessage(":pause_button: Paused:", event.getTextChannel());
    }
}
