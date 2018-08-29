package net.legenda.DiscordBot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.audioCore.AudioTrackInfo;
import net.legenda.DiscordBot.audioCore.TrackManager;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;
import net.legenda.DiscordBot.utils.MessageUtils;

import java.awt.*;
import java.util.Objects;

@Command.cmdInfo(name = "NowPlaying", description = "Sends Info On the Current Track", type = Command.Type.Music, alias = {"np"})
public class NowPlayingCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        TrackManager trackManager = Main.INSTANCE.musicUtils.getTrackManager(guild);
        AudioTrack track = Main.INSTANCE.musicUtils.getAudioPlayer(guild).getPlayingTrack();
        if (track != null) {
            AudioTrackInfo info = Objects.requireNonNull(trackManager.getQueue().stream().findFirst().orElse(null));
            EmbedBuilder builder = new EmbedBuilder();
            AudioTrackInfo upNext = Main.INSTANCE.musicUtils.getTrackManager(guild).getQueue().stream().filter(audio -> !audio.getTrack().equals(track)).findFirst().orElse(null);
            builder.setAuthor("Now Playing:", info.getTrack().getInfo().uri, info.getAuthor().getUser().getAvatarUrl());
            builder.setDescription("`" + info.getTrack().getInfo().title + "`\n\n`" + createSlider(track) + "`\n");
            builder.setColor(Color.red);
            builder.addField("Requested:", info.getAuthor().getEffectiveName(), true);
            builder.addField("UpNext:", upNext != null ? upNext.getTrack().getInfo().title : "Nothing", true);
            builder.setFooter("Created By " + MessageUtils.Author, MessageUtils.Author_Image);
            event.getTextChannel().sendMessage(builder.build()).queue();
            return;
        }
        throw new InvalidCommandStateException("There Is No Track Currently Playing");
    }

    private String createSlider(AudioTrack track) {
        long position = track.getPosition();
        long length = track.getInfo().length;
        StringBuilder slider = new StringBuilder();
        slider.append(Main.INSTANCE.musicUtils.getFormattedTime(position)).append(" ");
        for (int i = 0; i < 30; i++) {
            if (i != (int) (30 * ((double) position / (double) length)))
                slider.append("▬");
            else
                slider.append("\uD83D\uDD18");
        }
        slider.append(" ").append(Main.INSTANCE.musicUtils.getFormattedTime(length));
        return slider.toString();
    }
}
