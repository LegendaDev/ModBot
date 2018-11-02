package net.legenda.DiscordBot.listeners;

import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;

public class ModEventsListener extends ListenerAdapter {

    @Override
    public void onGuildBan(GuildBanEvent event) {
        Main.INSTANCE.configManager.presets.entrySet().stream().filter(set -> set.getKey().equals(event.getGuild())).findAny().ifPresent(entry -> entry.getValue().stream().filter(preset -> preset.getKey().equals("LogChannel")).findAny().ifPresent(preset -> event.getGuild().getBan(event.getUser()).queue(ban -> {
            String message = ban.getUser().getAsMention() + " Has Been Banned" + (ban.getReason() == null ? "" : ", Reason: ``" + ban.getReason() + "``");
            Command.sendEmbedMessage(message, event.getGuild().getTextChannelById(preset.getValue()), false);
        })));
    }
}
