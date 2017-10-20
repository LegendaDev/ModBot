package net.legenda.DiscordBot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.audioCore.AudioTrackInfo;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.utils.MessageUtils;
import net.legenda.DiscordBot.utils.MusicUtils;

import java.awt.*;

@Command.cmdInfo(name = "NowPlaying", description = "Sends Info On the Current Track", type = Command.Type.Music, alias = {"np"})
public class NowPlayingCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        AudioTrackInfo info = MusicUtils.players.get(guild).getValue().getQueue().stream().findFirst().orElse(null);
        AudioTrack track = Main.INSTANCE.musicUtils.getAudioPlayer(guild).getPlayingTrack();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Now Playing", info.getTrack().getInfo().uri, Main.jdaBot.getSelfUser().getAvatarUrl());
        builder.setColor(Color.red);
        builder.setDescription("`" + info.getTrack().getInfo().title + "` By `" + info.getTrack().getInfo().author + "`");
        builder.addField("Length: ", Main.INSTANCE.musicUtils.getFormattedLength(track.getInfo().length), true);
        builder.addField("Requested By:", info.getAuthor().getEffectiveName(), true);
        builder.setFooter("Created by " + MessageUtils.Author, MessageUtils.Author_Image);

        event.getTextChannel().sendMessage(builder.build()).queue();

    }
}
