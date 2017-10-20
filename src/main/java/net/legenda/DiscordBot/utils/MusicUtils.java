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
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

import java.awt.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                TrackManager manager = players.get(guild).getValue();
                manager.queue(audioTrack, author, channel);
                sendMessage(audioTrack, msg, channel);

            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                TrackManager manager = players.get(guild).getValue();
                List<AudioTrack> playlist = audioPlaylist.getTracks();
                AudioTrack audioTrack = playlist.get(0);
                if(playlist.isEmpty())
                    throw new InvalidCommandStateException("Search query returned nothing");
                if(identifier.startsWith("ytsearch:")){
                    manager.queue(audioTrack, author, channel);
                    sendMessage(audioTrack, msg, channel);
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

    public void skipTrack(Guild guild) {
        getAudioPlayer(guild).stopTrack();
    }

    public void seek(Guild guild, Long pos, TextChannel channel){
        AudioTrack track = getAudioPlayer(guild).getPlayingTrack();
        if(pos > track.getInfo().length)
            throw new InvalidCommandStateException("Cannot seek longer than the current song!");
        track.setPosition(pos);

    }

    public void pause(Guild guild){
        getAudioPlayer(guild).setPaused(false);
    }

    public void unPause(Guild guild){
        getAudioPlayer(guild).setPaused(false);
    }

    private void sendMessage(AudioTrack audioTrack, Message msg, TextChannel channel){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Added:", audioTrack.getInfo().uri, msg.getAuthor().getAvatarUrl());
        builder.setColor(Color.getHSBColor(0f, 1f, 1f));
        String title = audioTrack.getInfo().title;
        String author = audioTrack.getInfo().author;
        String requested = players.get(msg.getGuild()).getValue().getQueue().stream().filter(info -> info.getTrack().equals(audioTrack)).findFirst().orElse(null).getAuthor().getEffectiveName();
        double lengthLong = audioTrack.getInfo().length;
        double minutes = (lengthLong / 60000L);
        double seconds = (minutes - Math.floor(minutes)) * 60;
        int i = 1;
        for(AudioTrackInfo track : players.get(msg.getGuild()).getValue().getQueue()){
            if(track.getTrack().equals(audioTrack)){
                break;
            }
            i++;
        }
        String position = i + "";
        String length = (int) minutes + ":" + (int) seconds;
        builder.addField("Title:", title, false);
        builder.addField("Author:", author, true);
        builder.addField("Position:", position, true);
        builder.addField("Length:", length, true);
        builder.addField("Requested:", requested, true );
        builder.setFooter("Created by " + MessageUtils.Author, MessageUtils.Author_Image);
        channel.sendMessage(builder.build()).queue();
    }

}
