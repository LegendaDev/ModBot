package net.legenda.DiscordBot.utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;

public class MessageUtils {

    private static String Author = "Legenda";
    private static String Author_Image = "https://cdn.discordapp.com/avatars/348464305164910605/11ec4dd6feaea03dc613a47efb1f6b27.jpg";

    private static float hue = 0f;

    public MessageEmbed wrapMessage(String msg){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.getHSBColor(hue, 1f, 1f));
        hue += 0.12;
        if(hue > 1)
            hue = 0;
        eb.setDescription(msg);
        eb.setFooter("Created by " + Author, Author_Image);
        return eb.build();
    }

    public  MessageEmbed wrapErrorMessage(String msg){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(":warning:Error:warning:");
        eb.setColor(Color.RED);
        eb.setDescription(msg);
        eb.setFooter("Created by " + Author, Author_Image);
        return eb.build();
    }

    public  Message getMessage(String content, MessageChannel channel) {
        for (Message msg : channel.getIterableHistory().limit(20)) {
            if (msg.getAuthor().isBot())
                return  msg.getEmbeds().stream().filter(embed -> embed.getDescription().equalsIgnoreCase(content)) != null ? msg : null;
            else
                return msg.getContent().equalsIgnoreCase(msg.getContent()) ? msg : null;
        }
        return null;
    }

}
