package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;

@Command.cmdInfo(name = "Resume", description = "Unpauses current song", type = Command.Type.Music)
public class ResumeCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Main.INSTANCE.musicUtils.unPause(event.getGuild());
        sendEmbedMessage(":arrow_forward: Unpaused:", event.getTextChannel(), false);
    }
}
