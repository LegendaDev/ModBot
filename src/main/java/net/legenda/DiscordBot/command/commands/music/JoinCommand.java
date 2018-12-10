package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.command.CommandInfo;
import net.legenda.DiscordBot.command.CommandType;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

import java.util.concurrent.TimeUnit;

@CommandInfo(name = "Join", description = "Joins the user's voice channel", type = CommandType.Music, aliases = {"connect"})
public class JoinCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        VoiceChannel channel = event.getMember().getVoiceState().getChannel();
        if (channel == null)
            throw new InvalidCommandStateException("You must be in a VoiceChannel to summon the bot");
        Guild guild = event.getGuild();
        guild.getAudioManager().openAudioConnection(channel);
        Main.INSTANCE.scheduleManager.schedule(guild, () -> {
            Main.INSTANCE.musicUtils.closeAudio(guild);
            Main.INSTANCE.scheduleManager.remove(guild);
        }, 1, TimeUnit.MINUTES);
        sendMessage(":arrow_down: Joined:", event.getTextChannel());
    }
}
