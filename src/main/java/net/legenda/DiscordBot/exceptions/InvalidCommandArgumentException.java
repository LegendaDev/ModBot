package net.legenda.DiscordBot.exceptions;

public class InvalidCommandArgumentException extends RuntimeException{

    public InvalidCommandArgumentException(String message){
        super(message);
    }
}
