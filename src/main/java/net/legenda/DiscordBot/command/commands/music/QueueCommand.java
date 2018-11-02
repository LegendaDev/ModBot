package net.legenda.DiscordBot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.audio.AudioTrackInfo;
import net.legenda.DiscordBot.managers.TrackManager;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;
import net.legenda.DiscordBot.utils.MusicUtils;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@Command.cmdInfo(name = "Queue", description = "Lists the current queue", type = Command.Type.Music)
public class QueueCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        int page = 1;
        if (args.length > 0) {
            page = Integer.parseInt(args[0]);
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Queue:");
        builder.setColor(Color.red);
        String author = "Legenda";
        String author_Image = "https://cdn.discordapp.com/avatars/348464305164910605/11ec4dd6feaea03dc613a47efb1f6b27.jpg";
        builder.setFooter("Created By " + author, author_Image);
        AudioPlayer player = Main.INSTANCE.musicUtils.getAudioPlayer(event.getGuild());
        TrackManager manager = MusicUtils.players.get(event.getGuild()).getValue();
        AudioTrackInfo info = manager.getQueue().stream().findFirst().orElse(null);
        Member user = info == null ? null : info.getAuthor();
        StringBuilder queue = new StringBuilder();
        if (info == null)
            throw new InvalidCommandStateException("The Queue is empty");
        List<AudioTrackInfo> tracks = manager.getQueue().stream().skip(1).collect(Collectors.toList());
        double numTracks = tracks.size() + 1;
        int pages = numTracks / 5 > (int) numTracks / 5 ? (int) numTracks / 5 + 1 : (int) numTracks / 5;
        if (page > pages || page < 1) {
            throw new InvalidCommandStateException("Invalid Page Number");
        }
        if (numTracks != 1)
            for (int i = page * 5 - 4; i <= page * 5; i++) {
                if (i >= numTracks)
                    break;
                queue.append(i).append(".     `").append(tracks.get(i - 1).getTrack().getInfo().title).append("` Requested By `").append(user.getEffectiveName()).append("`\n");
            }
        StringBuilder description = new StringBuilder();
        if (page == 1)
            description.append("__Currently Playing:__\n`").append(player.getPlayingTrack().getInfo().title).append("` Requested By` ").append(user.getEffectiveName()).append("`\n");
        description.append("\n__Coming up__:\n").append(queue.toString()).append("\n\n").append("**")
                .append(tracks.size()).append(" Songs")
                .append(" : ").append(pages).append(" Pages**");

        builder.setDescription(description.toString());
        event.getTextChannel().sendMessage(builder.build()).queue();
    }
}
