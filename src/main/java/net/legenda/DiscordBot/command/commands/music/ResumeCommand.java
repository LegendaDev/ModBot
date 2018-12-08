package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.command.CommandInfo;
import net.legenda.DiscordBot.command.CommandType;

@CommandInfo(name = "Resume", description = "Unpauses current song", type = CommandType.Music, alias = {"unpause"})
public class ResumeCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Main.INSTANCE.musicUtils.unPause(event.getGuild());
        sendMessage(":arrow_forward: Resumed:", event.getTextChannel());
    }
}
