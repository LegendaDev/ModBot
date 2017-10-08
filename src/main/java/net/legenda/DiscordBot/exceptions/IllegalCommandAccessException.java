package net.legenda.DiscordBot.exceptions;

public class IllegalCommandAccessException extends RuntimeException {

    public IllegalCommandAccessException(String message){
        super(message);
    }
}
