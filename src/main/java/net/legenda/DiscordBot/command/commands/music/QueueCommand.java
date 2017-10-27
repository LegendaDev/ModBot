package net.legenda.DiscordBot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.audioCore.AudioTrackInfo;
import net.legenda.DiscordBot.audioCore.TrackManager;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;
import net.legenda.DiscordBot.utils.MusicUtils;

import java.awt.*;
import java.util.stream.Collectors;

@Command.cmdInfo(name = "Queue", description = "Lists the current queue", type = Command.Type.Music)
public class QueueCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
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
        if(info == null)
            throw new InvalidCommandStateException("The Queue is empty");
        int i = 1;
        for (AudioTrackInfo track : manager.getQueue().stream().skip(1).collect(Collectors.toList())) {
            if (i < 5)
                queue.append(i).append(".`  ").append(track.getTrack().getInfo().title).append("` Requested By `").append(user.getEffectiveName()).append("`\n");
            else
                break;
            i++;
        }
        builder.setDescription("__Currently Playing:__\n`" + player.getPlayingTrack().getInfo().title + "` Requested By` " + user.getEffectiveName() + "`\n"
                + "\n__Coming up__:\n" + queue.toString() + "\n\n" + "**" + manager.getQueue().size() + " Songs**");
        event.getTextChannel().sendMessage(builder.build()).queue();
    }
}
