package net.cubespace.lib.Command;

import net.cubespace.lib.CubespacePlugin;
import net.cubespace.lib.Module.Module;
import net.cubespace.lib.Util.StringUtils;
import net.md_5.bungee.api.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 *
 * The CommandExecutor holds all Commands which can be executed. You can add/remove Commands during the Runtime without
 * any Sideeffect.
 */
public class CommandExecutor {
    private HashMap<String, CommandStruct> commandMap = new HashMap<String, CommandStruct>();
    private CubespacePlugin plugin;

    public CommandExecutor(CubespacePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a new CLICommand for the given Module
     *
     * @param module The module for which this Command should be added
     * @param cliCommand The command class which contains @Command annotations
     */
    public void add(Module module, CLICommand cliCommand) {
        plugin.getPluginLogger().debug("Registered new Command " + cliCommand.toString());

        for (Method method : cliCommand.getClass().getDeclaredMethods()) {
            //Check if Command Annotation is present
            if (method.isAnnotationPresent(Command.class)) {
                Annotation[] annotations = method.getAnnotations();

                //Get the Command Annotation
                for(Annotation annotation : annotations) {
                    if(annotation instanceof Command) {
                        Command aCmd = (Command)annotation;

                        //Save the command
                        CommandStruct command = new CommandStruct();
                        command.setAnnotation(aCmd);
                        command.setCommand(method);
                        command.setInstance(cliCommand);
                        command.setModule(module);

                        commandMap.put(aCmd.command(), command);
                        plugin.getPluginLogger().debug("Added command " + aCmd.command() + " for the method " + method.getName());

                        break;
                    }
                }
            }
        }
    }

    /**
     * When you unload a Module be sure to remove all Commands loaded by it. Otherwise it can happen that the CommandExecutor
     * tries to call Commands from unloaded Modules
     *
     * @param module The module which has been unloaded
     */
    public void remove(Module module) {
        for(Map.Entry<String, CommandStruct> commands : new HashMap<>(commandMap).entrySet()) {
            if(commands.getValue().getModule().equals(module)) {
                commandMap.remove(commands.getKey());
            }
        }
    }

    /**
     * Executes a Command. It searches for a given Command in reverse order of the Arguments. For example if a Player executes
     * "money pay Skycrapper 150" the CommandExecutor first searches for a Command "money pay Skycrapper 150", then for "money
     * pay Skycrapper" with argument "150", then "money pay" with arguments "Skycrapper", "150" and then for "money" with the three
     * Arguments.
     *
     * @param commandSender The CommandSender which executed this Command
     * @param command The command name which has been executed
     * @param args All arguments which has been given
     * @return True when the Command has been executed, false if there was an Error
     */
    public boolean onCommand(CommandSender commandSender, String command, String[] args) {
        plugin.getPluginLogger().info(commandSender.getName() + " emitted command: " + command + " with args " + StringUtils.join(args, " "));

        for (int argsIncluded = args.length; argsIncluded >= -1; argsIncluded--) {
            StringBuilder identifierBuilder = new StringBuilder(command);
            for(int i = 0; i < argsIncluded; i++) {
                identifierBuilder.append(' ').append(args[i]);
            }

            String identifier = identifierBuilder.toString();

            if (commandMap.containsKey(identifier)) {
                String[] realArgs = Arrays.copyOfRange(args, argsIncluded, args.length);
                CommandStruct command1 = commandMap.get(identifier);

                if(realArgs.length < command1.getAnnotation().arguments()) {
                    plugin.getPluginLogger().debug("Command has not enough Arguments to be handled");
                    onNotEnoughArguments(commandSender, command1);
                    return false;
                }

                try {
                    plugin.getPluginLogger().debug("Invoking command with arguments " + StringUtils.join(realArgs, " "));
                    command1.getCommand().invoke(command1.getInstance(), commandSender, realArgs);
                    return true;
                } catch (Exception e) {
                    plugin.getPluginLogger().error("Exception thrown while executing a Command", e);
                    return false;
                }
            }
        }

        if(commandMap.containsKey(command)) {
            CommandStruct commandStruct = commandMap.get(command);

            try {
                plugin.getPluginLogger().debug("Invoking command with arguments " + StringUtils.join(args, " "));
                commandStruct.getCommand().invoke(commandStruct.getInstance(), commandSender, args);
                return true;
            } catch (Exception e) {
                plugin.getPluginLogger().error("Exception thrown while executing a Command", e);
                return false;
            }
        }

        plugin.getPluginLogger().debug("Executed a unknown Command");
        onUnknownCommand(commandSender, command);
        return false;
    }

    /**
     * On not enough arguments.
     *
     * @param sender the sender
     * @param command the command
     */
    public void onNotEnoughArguments(CommandSender sender, CommandStruct command) {
        sender.sendMessage("You have not given enough Arguments. You need at least " + command.getAnnotation().arguments() + " arguments");
    }

    /**
     * On unknown command.
     *
     * @param sender the sender
     * @param command the command
     */
    public void onUnknownCommand(CommandSender sender, String command) {

    }
}
