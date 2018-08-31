package net.legenda.DiscordBot.command.commands.admin;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

@Command.cmdInfo(name = "Cooldown", description = "Adds user to cooldown role", type = Command.Type.Admin, permission = Permission.MESSAGE_MANAGE, alias = {"mute"})
public class CooldownCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length == 0)
            throw new InvalidCommandArgumentException("Usage: .Cooldown <@User>");
        User toMute = event.getMessage().getMentionedUsers().size() > 0 ? event.getMessage().getMentionedUsers().get(0) : null;
        if (toMute != null) {
            Guild guild = event.getGuild();
            GuildController guildController = guild.getController();
            Role muteRole = guild.getRoles().stream().filter(role -> role.getName().equalsIgnoreCase("Cooldown")).findFirst().orElse(null);
            if (muteRole == null)
                throw new InvalidCommandStateException("You need a role called Cooldown to use this");
            guildController.removeRolesFromMember(guild.getMember(toMute), guild.getMember(toMute).getRoles()).queue();
            guildController.addSingleRoleToMember(guild.getMember(toMute), muteRole).queue();
            sendEmbedMessage("Added " + toMute.getAsMention() + " To Cooldown", event.getTextChannel(), false);
        } else
            throw new InvalidCommandArgumentException("Could not find User");
    }
}
