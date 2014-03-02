package net.cubespace.lib.Chat.MessageBuilder.ClickEvent;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public enum ClickAction {
    /**
     * If a Player clicks on this the Player gets asked to open the Value in a Browser
     */
    OPEN_URL,
    /**
     * If a Player clicks on this it gets asked to open the value as File on his Computer
     */
    OPEN_FILE,
    /**
     * If a Player click on this it executes the value as Command
     */
    RUN_COMMAND,
    /**
     * If a Player click on this the Chat bar gets filled with the value
     */
    SUGGEST_COMMAND
}
