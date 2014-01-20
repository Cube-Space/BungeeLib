package net.cubespace.lib.Chat.MessageBuilder;

import net.cubespace.lib.Chat.MessageBuilder.ClickEvent.IClickEvent;
import net.md_5.bungee.api.CommandSender;

import java.util.HashMap;
import java.util.Map;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 06.01.14 02:12
 */
public class LegacyMessageBuilder implements IMessageBuilder {
    private String message;
    private HashMap<String, String> variables = new HashMap<>();

    @Override
    public IMessageBuilder setText(String text) {
        this.message = text;

        return this;
    }

    @Override
    public IMessageBuilder addEvent(String ident, IClickEvent event) {

        return this;
    }

    @Override
    public void send(CommandSender sender) {
        sender.sendMessage(getString());
    }

    @Override
    public IMessageBuilder setVariable(String variable, String value) {
        variables.put(variable, value);

        return this;
    }

    public String getString() {
        for(Map.Entry<String, String> variable : variables.entrySet()) {
            message = message.replace("%" + variable.getKey() + "%", variable.getValue());
        }

        return message.replaceAll("\\{click:([^}]*)\\}", "");
    }
}
