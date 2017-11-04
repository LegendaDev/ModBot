package net.legenda.DiscordBot.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.audioCore.AudioTrackInfo;
import net.legenda.DiscordBot.audioCore.PlayerSendHandler;
import net.legenda.DiscordBot.audioCore.TrackManager;
import net.legenda.DiscordBot.command.commands.music.LoopCommand;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MusicUtils {

    private static final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    public static final Map<Guild, Map.Entry<AudioPlayer, TrackManager>> players = new HashMap<>();

    public MusicUtils() {
        AudioSourceManagers.registerRemoteSources(manager);
    }

    public AudioPlayer getAudioPlayer(Guild guild) {
        if (players.containsKey(guild)) {
            return players.get(guild).getKey();
        }
        return newPlayer(guild);
    }

    public TrackManager getTrackManager(Guild guild) {
        if (!players.containsKey(guild)) {
            newPlayer(guild);
        }
        return players.get(guild).getValue();
    }

    private AudioPlayer newPlayer(Guild guild) {
        AudioPlayer player = manager.createPlayer();
        TrackManager trackManager = new TrackManager(player);
        player.addListener(trackManager);

        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(player));

        players.put(guild, new AbstractMap.SimpleEntry<>(player, trackManager));

        return player;

    }

    public void newTrack(String identifier, Member author, Message msg) {
        Guild guild = author.getGuild();
        TextChannel channel = msg.getTextChannel();
        getAudioPlayer(guild);

        manager.setFrameBufferDuration(500);
        manager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                TrackManager manager = getTrackManager(guild);
                manager.queue(audioTrack, author, channel);
                if (getTrackManager(guild).getQueue().size() != 1){
                    sendMessage(audioTrack, msg, channel);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                TrackManager manager = getTrackManager(guild);
                List<AudioTrack> playlist = audioPlaylist.getTracks();
                if (playlist.isEmpty())
                    throw new InvalidCommandStateException("Search query returned nothing");
                AudioTrack audioTrack = playlist.get(0);
                if (identifier.startsWith("ytsearch:")) {
                    manager.queue(audioTrack, author, channel);
                    if (getTrackManager(guild).getQueue().size() != 1){
                        sendMessage(audioTrack, msg, channel);
                    }
                }
            }

            @Override
            public void noMatches() {
                Main.INSTANCE.msgUtil.wrapMessage("No matches for " + identifier);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                if (e.severity.equals(FriendlyException.Severity.COMMON))
                    Main.INSTANCE.msgUtil.wrapErrorMessage(e.getMessage());
                else
                    e.printStackTrace();
            }
        });
    }

    public void skipTrack(TextChannel channel, int position) {
        Guild guild = channel.getGuild();
        List<AudioTrackInfo> Queue = new ArrayList<>(getTrackManager(guild).getQueue());
        List<AudioTrackInfo> toRemove = Queue.stream().filter(info -> Queue.indexOf(info) != 0).limit(position - 1).collect(Collectors.toList());
        getTrackManager(guild).removeCollectionFromQueue(toRemove);
        AudioTrackInfo current = Queue.stream().findFirst().orElse(null);
        AudioTrackInfo next = Queue.isEmpty() ? null : Queue.get(position - 1);
        String skippedTrack = position == 1 ? (current != null ? current.getTrack().getInfo().title : "") : next != null ? next.getTrack().getInfo().title : "NONE";
        channel.sendMessage(":track_next: Skipped" + (position != 1 ? " To: `" + skippedTrack  + "`" : ": `" + skippedTrack + "`")).queue();
        LoopCommand.endRepeat();
        getAudioPlayer(guild).stopTrack();

    }

    public void seek(Guild guild, Long pos) {
        AudioTrack track = getAudioPlayer(guild).getPlayingTrack();
        if (pos > track.getInfo().length)
            throw new InvalidCommandStateException("Cannot seek longer than the current song!");
        track.setPosition(pos);

    }

    public void pause(Guild guild) {
        getAudioPlayer(guild).setPaused(true);
    }

    public void unPause(Guild guild) {
        getAudioPlayer(guild).setPaused(false);
    }

    public void clear(Guild guild) {
        getTrackManager(guild).clearQueue(false);
    }

    public void shuffle(Guild guild) {
        getTrackManager(guild).shuffleQueue();
    }

    private void sendMessage(AudioTrack audioTrack, Message msg, TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Added:", audioTrack.getInfo().uri, msg.getAuthor().getAvatarUrl());
        builder.setColor(Color.getHSBColor(0f, 1f, 1f));
        String title = audioTrack.getInfo().title;
        AudioTrackInfo info = players.get(msg.getGuild()).getValue().getQueue().stream().filter(trackInfo -> trackInfo.getTrack().equals(audioTrack)).findFirst().orElse(null);
        String requested = info == null ? null : info.getAuthor().getEffectiveName();
        String position = getPositionInQueue(audioTrack, msg.getGuild());
        String length = getFormattedLength(audioTrack.getInfo().length);
        builder.addField("Title:", title, false);
        builder.addField("Position:", position, true);
        builder.addField("Length:", length, true);
        builder.addField("Requested:", requested, true);
        builder.setFooter("Created by " + MessageUtils.Author, MessageUtils.Author_Image);
        channel.sendMessage(builder.build()).queue();
    }

    public String getFormattedLength(Long length) {
        double lengthDubl = length;
        double minutes = (lengthDubl / 60000L);
        double seconds = (minutes - Math.floor(minutes)) * 60;
        String secondStr = (int) seconds + "";
        if (secondStr.toCharArray().length < 2) {
            secondStr = "0" + secondStr;
        }
        return (int) minutes + ":" + secondStr;
    }

    private String getPositionInQueue(AudioTrack audioTrack, Guild guild) {
        int i = 1;
        for (AudioTrackInfo track : players.get(guild).getValue().getQueue()) {
            if (track.getTrack().equals(audioTrack)) {
                break;
            }
            i++;
        }
        i -= 1;
        return i + "";
    }

}
