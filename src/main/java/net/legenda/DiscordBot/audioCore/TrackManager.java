package net.legenda.DiscordBot.audioCore;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;
import net.legenda.DiscordBot.utils.MessageUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer player;
    private final Queue<AudioTrackInfo> queue;

    public TrackManager(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track, Member author, TextChannel channel) {
        AudioTrackInfo info = new AudioTrackInfo(track, author, channel);
        queue.add(info);

        if (player.getPlayingTrack() == null) {
            player.playTrack(track);
        }
    }

    public Set<AudioTrackInfo> getQueue() {
        return new LinkedHashSet<>(queue);
    }

    public AudioTrackInfo getAudioTrackInfo(com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo track) {
        return queue.stream().filter(info -> info.getTrack().equals(track)).findFirst().orElse(null);
    }

    public void clearQueue() {
        queue.clear();
    }

    public void shuffleQueue() {
        List<AudioTrackInfo> oldQueue = new ArrayList<>(getQueue());
        AudioTrackInfo currentTrack = oldQueue.get(0);
        oldQueue.remove(0);
        Collections.shuffle(oldQueue);
        oldQueue.add(0, currentTrack);
        clearQueue();
        queue.addAll(oldQueue);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        AudioTrackInfo info = queue.element();
        VoiceChannel channel = info.getAuthor().getVoiceState().getChannel();
        VoiceChannel connected = info.getAuthor().getGuild().getAudioManager().getConnectedChannel();
        if (connected != null || info.getAuthor().getGuild().getAudioManager().isAttemptingToConnect()){
            channel = connected;
        } else if(channel == null){
            throw new InvalidCommandStateException("You must be in a VoiceChannel to summon the bot");
        }
        info.getAuthor().getGuild().getAudioManager().openAudioConnection(channel);

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        queue.poll().getTrack();
        if (!queue.isEmpty()){
            player.playTrack(queue.element().getTrack());
            AudioTrackInfo info = queue.element();
            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor("Now Playing", info.getTrack().getInfo().uri, info.getAuthor().getUser().getAvatarUrl());
            builder.setColor(Color.getHSBColor(0f, 1f, 1f));
            String message = "`" + info.getTrack().getInfo().title + "` By `" + info.getTrack().getInfo().author + "`\n"
                    + "Requested By: " + info.getAuthor().getEffectiveName() + " Length: " + info.getTrack().getInfo().length;
            builder.setDescription(message);
            builder.setFooter("Created by " + MessageUtils.Author, MessageUtils.Author_Image);
            info.getChannel().sendMessage("").queue();
        }

    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        exception.printStackTrace();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        System.out.println("Stuck track");
    }


}
