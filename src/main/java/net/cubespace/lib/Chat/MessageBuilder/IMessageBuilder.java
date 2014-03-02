package net.cubespace.lib.Chat.MessageBuilder;

import net.cubespace.lib.Chat.MessageBuilder.ClickEvent.IClickEvent;
import net.md_5.bungee.api.CommandSender;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 *
 * An example Message for the MessageBuilder can look like this:
 *
 * Message String: "Mute %nick%{click:playerMenu}"
 * Usage:
 *
 * ClickEvent clickEvent = new ClickEvent();
 * clickEvent.setAction(ClickAction.RUN_COMMAND);
 * clickEvent.setValue("/mute geNAZt");
 *
 * MessageBuilder messageBuilder = new MessageBuilder();
 * messageBuilder.addEvent("playerMenu", clickEvent);
 * messageBuilder.setText(Message String);
 * messageBuilder.setVariable("nick", "geNAZt");
 * messageBuilder.send(player);
 *
 * In 1.7: This would send a "Nick geNAZt" to the Player where the geNAZt is clickable. When the User click on geNAZt the Command
 * "/mute geNAZt" will be executed.
 *
 * In 1.6: This would send the same Text but without any effect on the Click Event.
 */
public interface IMessageBuilder {
    /**
     * Set the Text this MessageBuilder should use for output to the Sender
     *
     * @param text Any Text which contains Colors, Events and such like. This Text can also contain Variables which
     *             get implemented in the Text. A ClickEvent can be embedded like this "{click:executorIdent}", a
     *             Variable must be given like this "%variable%"
     * @return The Builder itself
     */
    public IMessageBuilder setText(String text);

    /**
     * Add a ClickEvent which should be used to fill the ident given. A "{click:executorIdent}" in the Text would search
     * for a ident "executorIdent" and use it as ClickEvent.
     *
     * @param ident The ident String this event should get
     * @param event The Event which should be executed when this Event has been triggered
     * @return The Builder itself
     */
    public IMessageBuilder addEvent(String ident, IClickEvent event);

    /**
     * Convert the String and send it to the sender. Please care that the Convertation of the String gets called in here
     * so if you want to change the MessageBuilder over and over again you can do that without any performance issues.
     * But sending a MessageBuilder 1000 times can cause a little Lag.
     *
     * @param sender The CommandSender to send this MessageBuilder to.
     */
    public void send(CommandSender sender);

    /**
     * Set the Content of a Variable
     *
     * @param variable The variable which should be filled
     * @param value The value which should replace the variable
     * @return The MessageBuilder itself
     */
    public IMessageBuilder setVariable(String variable, String value);
}