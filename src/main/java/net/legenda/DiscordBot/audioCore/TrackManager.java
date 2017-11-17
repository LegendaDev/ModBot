package net.legenda.DiscordBot.audioCore;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.commands.music.LoopCommand;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;
import net.legenda.DiscordBot.utils.MessageUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer player;
    private final Queue<AudioTrackInfo> queue;

    public TrackManager(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track, Member author, TextChannel channel, boolean top) {
        AudioTrackInfo info = new AudioTrackInfo(track, author, channel);
        Set<AudioTrackInfo> currentQueue = new LinkedHashSet<>();
        if(top){
            currentQueue = getQueue().stream().skip(1).collect(Collectors.toSet());
            clearQueue(false);
        }
        queue.add(info);
        queue.addAll(currentQueue);

        if (player.getPlayingTrack() == null) {
            player.playTrack(track);
        }
    }

    public Set<AudioTrackInfo> getQueue() {
        return new LinkedHashSet<>(queue);
    }

    public void clearQueue(boolean current) {
        List<AudioTrackInfo> oldQueue = new ArrayList<>(getQueue());
        if (oldQueue.isEmpty())
            throw new InvalidCommandStateException("The Queue is already empty");
        queue.clear();
        if (!current)
            queue.add(oldQueue.get(0));
    }

    public void shuffleQueue() {
        List<AudioTrackInfo> oldQueue = new ArrayList<>(getQueue());
        AudioTrackInfo currentTrack = oldQueue.get(0);
        oldQueue.remove(0);
        Collections.shuffle(oldQueue);
        oldQueue.add(0, currentTrack);
        clearQueue(true);
        queue.addAll(oldQueue);
    }

    private void addTop(AudioTrackInfo info) {
        List<AudioTrackInfo> oldQueue = new ArrayList<>(getQueue());
        if (!oldQueue.isEmpty())
            oldQueue.remove(0);
        oldQueue.add(0, info);
        clearQueue(true);
        queue.addAll(oldQueue);
    }

    public void removeCollectionFromQueue(Collection<AudioTrackInfo> c) {
        queue.removeAll(c);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        AudioTrackInfo info = queue.element();
        VoiceChannel channel = info.getAuthor().getVoiceState().getChannel();
        VoiceChannel connected = info.getAuthor().getGuild().getAudioManager().getConnectedChannel();
        Guild guild = info.getAuthor().getGuild();
        if (connected != null) {
            channel = connected;
        } else if (channel == null) {
            throw new InvalidCommandStateException("You must be in a VoiceChannel to summon the bot");
        }
        sendNextTrackMessage(info, guild, track);
        guild.getAudioManager().openAudioConnection(channel);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        AudioTrackInfo audioTrackInfo = getQueue().stream().findFirst().orElse(null);
        if (audioTrackInfo == null)
            return;
        Guild guild = audioTrackInfo.getAuthor().getGuild();
        if (!LoopCommand.getRepeat(guild)) {
            queue.poll();
            if (!queue.isEmpty()) {
                player.playTrack(queue.element().getTrack());
            }
        } else {
            addTop(queue.element());
            player.playTrack(track.makeClone());
        }
    }

    private void sendNextTrackMessage(AudioTrackInfo info, Guild guild, AudioTrack track) {
        EmbedBuilder builder = new EmbedBuilder();
        AudioTrackInfo upNext = Main.INSTANCE.musicUtils.getTrackManager(guild).getQueue().stream().filter(audio -> !audio.getTrack().equals(track)).findFirst().orElse(null);
        builder.setAuthor("Now Playing:", info.getTrack().getInfo().uri, info.getAuthor().getUser().getAvatarUrl());
        builder.setDescription("`" + info.getTrack().getInfo().title + "`");
        builder.setColor(Color.red);
        builder.addField("Requested:", info.getAuthor().getEffectiveName(), true);
        builder.addField("Length: ", Main.INSTANCE.musicUtils.getFormattedLength(track.getInfo().length), true);
        builder.addField("UpNext:", upNext != null ? upNext.getTrack().getInfo().title : "Nothing", true);
        builder.setFooter("Created By " + MessageUtils.Author, MessageUtils.Author_Image);
        info.getChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        exception.printStackTrace();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        player.playTrack(track);
    }

}
