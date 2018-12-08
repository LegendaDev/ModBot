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
import net.legenda.DiscordBot.audio.AudioTrackInfo;
import net.legenda.DiscordBot.audio.PlayerSendHandler;
import net.legenda.DiscordBot.managers.TrackManager;
import net.legenda.DiscordBot.command.commands.music.LoopCommand;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MusicUtils {

    private static final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    public final Map<Guild, Map.Entry<AudioPlayer, TrackManager>> players = new HashMap<>();

    public MusicUtils() {
        AudioSourceManagers.registerRemoteSources(manager);
    }

    public AudioPlayer getAudioPlayer(Guild guild) {
        if (players.containsKey(guild))
            return players.get(guild).getKey();

        return newPlayer(guild);
    }

    public TrackManager getTrackManager(Guild guild) {
        if (!players.containsKey(guild))
            newPlayer(guild);

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

    public void newTrack(String identifier, Member author, Message msg, boolean top) {
        Guild guild = author.getGuild();
        TextChannel channel = msg.getTextChannel();
        getAudioPlayer(guild);

        manager.setFrameBufferDuration(500);
        manager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                TrackManager manager = getTrackManager(guild);
                manager.queue(audioTrack, author, channel, top);
                if (getTrackManager(guild).getQueue().size() != 1) {
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
                    manager.queue(audioTrack, author, channel, top);
                    if (getTrackManager(guild).getQueue().size() != 1) {
                        sendMessage(audioTrack, msg, channel);
                    }
                } else {
                    for (AudioTrack track : playlist) {
                        manager.queue(track, author, channel, top);
                    }
                    if (getTrackManager(guild).getQueue().size() != 1) {
                        sendPlaylistMessage(audioPlaylist, msg, channel);
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
        channel.sendMessage(":track_next: Skipped" + (position != 1 ? " To: `" + skippedTrack + "`" : ": `" + skippedTrack + "`")).queue();
        LoopCommand.endRepeat(guild);
        getAudioPlayer(guild).stopTrack();

    }

    public void seek(Guild guild, Long pos) {
        AudioTrack track = getAudioPlayer(guild).getPlayingTrack();
        if (track == null)
            throw new InvalidCommandStateException("No song is playing");
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

    public String remove(Guild guild, int number) {
        return getTrackManager(guild).removeItem(number);
    }

    public void shuffle(Guild guild) {
        getTrackManager(guild).shuffleQueue();
    }

    public void closeAudio(Guild guild) {
        players.remove(guild);
        getTrackManager(guild).clearQueue(true);
        guild.getAudioManager().closeAudioConnection();

    }

    private void sendMessage(AudioTrack audioTrack, Message msg, TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Added:", audioTrack.getInfo().uri, msg.getAuthor().getAvatarUrl());
        builder.setColor(Color.getHSBColor(0f, 1f, 1f));
        String title = audioTrack.getInfo().title;
        AudioTrackInfo info = players.get(msg.getGuild()).getValue().getQueue().stream().filter(trackInfo -> trackInfo.getTrack().equals(audioTrack)).findFirst().orElse(null);
        String requested = info == null ? null : info.getAuthor().getEffectiveName();
        String position = getPositionInQueue(audioTrack, msg.getGuild());
        String length = getFormattedTime(audioTrack.getInfo().length);
        builder.addField("Title:", title, false);
        builder.addField("Position:", position, true);
        builder.addField("Length:", length, true);
        builder.addField("Requested:", requested, true);
        builder.setFooter("Created by " + MessageUtils.Author, MessageUtils.Author_Image);
        channel.sendMessage(builder.build()).queue();
    }

    private void sendPlaylistMessage(AudioPlaylist playlist, Message msg, TextChannel channel) {
        if (playlist.getTracks().size() > 1) {
            EmbedBuilder builder = new EmbedBuilder();
            AudioTrack audioTrack = playlist.getTracks().get(1);
            builder.setAuthor("Added Playlist:", audioTrack.getInfo().uri, msg.getAuthor().getAvatarUrl());
            builder.setColor(Color.getHSBColor(0f, 1f, 1f));
            builder.setDescription("**Enqueued:**` " + playlist.getTracks().size() + "` \nUse .Queue to see all enqueued songs");
            channel.sendMessage(builder.build()).queue();
        }
    }

    public String getFormattedTime(Long length) {
        double lengthSeconds = (double) length / 1000;
        double hours = lengthSeconds / 3600;
        double minutes = (hours - Math.floor(hours)) * 60;
        double seconds = (minutes - Math.floor(minutes)) * 60;
        String secondStr = (int) seconds + "";
        if (secondStr.toCharArray().length < 2) {
            secondStr = "0" + secondStr;
        }
        if (hours > 1) {
            String minutesStr = (int) minutes + "";
            if (minutesStr.toCharArray().length < 2) {
                minutesStr = "0" + minutesStr;
            }
            return (int) hours + ":" + minutesStr + ":" + secondStr;
        } else {
            return (int) minutes + ":" + secondStr;
        }
    }

    private String getPositionInQueue(AudioTrack audioTrack, Guild guild) {
        int i = 0;
        for (AudioTrackInfo track : getTrackManager(guild).getQueue()) {
            if (track.getTrack().equals(audioTrack)) {
                break;
            }
            i++;
        }
        return i + "";
    }

}
