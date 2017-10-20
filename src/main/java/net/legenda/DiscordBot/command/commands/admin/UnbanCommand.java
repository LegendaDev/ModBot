package net.legenda.DiscordBot.command.commands.admin;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;

@Command.cmdInfo(name = "Unban", description = "Unbans a user from the server", type = Command.Type.Admin, permission = Permission.BAN_MEMBERS)
public class UnbanCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length == 0) {
            throw new InvalidCommandArgumentException("Usage: .Unban <UserID>");
        }
        GuildController guildController = new GuildController(event.getGuild());
        User toUnban = null;
        try{
            toUnban = Main.jdaBot.getUserById(args[0]);
        }catch(Exception e){

        }
        if (toUnban != null) {
            guildController.unban(toUnban).queue();
            sendEmbedMessage("Unbanned user: " + toUnban.getAsMention(), event.getTextChannel(), false);
        } else
            throw new InvalidCommandArgumentException("Could not find User");

    }

}
