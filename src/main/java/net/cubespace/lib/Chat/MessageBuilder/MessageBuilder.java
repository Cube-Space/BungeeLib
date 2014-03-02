package net.cubespace.lib.Chat.MessageBuilder;

import net.cubespace.lib.Chat.MessageBuilder.ClickEvent.IClickEvent;
import net.md_5.bungee.api.CommandSender;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class MessageBuilder implements IMessageBuilder {
    private IMessageBuilder messageBuilder;
    private static boolean useComponentBuilder = false;

    static {
        try {
            Class.forName("net.md_5.bungee.api.chat.ComponentBuilder");
            useComponentBuilder = true;
        } catch (ClassNotFoundException e) {
            useComponentBuilder = false;
        }
    }

    public MessageBuilder() {
        if(useComponentBuilder) {
            messageBuilder = new ComponentBuilderBuilder();
        } else {
            messageBuilder = new LegacyMessageBuilder();
        }
    }

    @Override
    public IMessageBuilder setText(String text) {
        messageBuilder.setText(text);

        return this;
    }

    @Override
    public IMessageBuilder addEvent(String ident, IClickEvent event) {
        messageBuilder.addEvent(ident, event);

        return this;
    }

    @Override
    public void send(CommandSender sender) {
        messageBuilder.send(sender);
    }

    @Override
    public IMessageBuilder setVariable(String variable, String value) {
        messageBuilder.setVariable(variable, value);

        return this;
    }
}
