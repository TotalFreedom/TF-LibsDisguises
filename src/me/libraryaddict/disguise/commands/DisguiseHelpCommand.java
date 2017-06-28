package me.libraryaddict.disguise.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import me.libraryaddict.disguise.DisallowedDisguises;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.LivingWatcher;
import me.libraryaddict.disguise.utilities.DisguiseParser;
import me.libraryaddict.disguise.utilities.DisguiseParser.DisguisePerm;
import me.libraryaddict.disguise.utilities.ReflectionFlagWatchers;
import me.libraryaddict.disguise.utilities.ReflectionFlagWatchers.ParamInfo;

public class DisguiseHelpCommand extends DisguiseBaseCommand implements TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        for (String node : new String[]{
            "disguise", "disguiseradius", "disguiseentity"
        }) {
            HashMap<DisguisePerm, HashMap<ArrayList<String>, Boolean>> permMap = DisguiseParser.getPermissions(sender,
                    "libsdisguises." + node + ".");

            if (!permMap.isEmpty()) {
                if (args.length == 0) {
                    sendCommandUsage(sender, null);
                    return true;
                } else {
                    ParamInfo help = null;

                    for (ParamInfo s : ReflectionFlagWatchers.getParamInfos()) {
                        String name = s.getName().replaceAll(" ", "");

                        if (args[0].equalsIgnoreCase(name) || args[0].equalsIgnoreCase(name + "s")) {
                            help = s;
                            break;
                        }
                    }

                    if (help != null) {
                        sender.sendMessage(ChatColor.RED + help.getName() + ": " + ChatColor.GREEN
                                + StringUtils.join(help.getEnums(""), ChatColor.RED + ", " + ChatColor.GREEN));
                        return true;
                    }

                    DisguisePerm type = DisguiseParser.getDisguisePerm(args[0]);

                    if (type == null) {
                        sender.sendMessage(ChatColor.RED + "Cannot find the disguise " + args[0]);
                        return true;
                    }

                    if (!permMap.containsKey(type)) {
                        sender.sendMessage(ChatColor.RED + "You do not have permission for that disguise!");
                        return true;
                    }
                    
                    if (!DisallowedDisguises.isAllowed(type.getType())) {
                        sender.sendMessage(ChatColor.RED + "That disguise is forbidden.");
                        return true;
                    }

                    ArrayList<String> methods = new ArrayList<>();
                    HashMap<String, ChatColor> map = new HashMap<>();
                    Class watcher = type.getWatcherClass();
                    int ignored = 0;

                    try {
                        for (Method method : ReflectionFlagWatchers.getDisguiseWatcherMethods(watcher)) {
                            if (args.length < 2 || !args[1].equalsIgnoreCase("show")) {
                                boolean allowed = false;

                                for (ArrayList<String> key : permMap.get(type).keySet()) {
                                    if (permMap.get(type).get(key)) {
                                        if (key.contains("*") || key.contains(method.getName().toLowerCase())) {
                                            allowed = true;
                                            break;
                                        }
                                    } else if (!key.contains(method.getName().toLowerCase())) {
                                        allowed = true;
                                        break;
                                    }
                                }

                                if (!allowed) {
                                    ignored++;
                                    continue;
                                }
                            }

                            Class c = method.getParameterTypes()[0];
                            ParamInfo info = ReflectionFlagWatchers.getParamInfo(c);

                            if (info == null) {
                                continue;
                            }

                            ChatColor methodColor = ChatColor.YELLOW;

                            Class<?> declaring = method.getDeclaringClass();

                            if (declaring == LivingWatcher.class) {
                                methodColor = ChatColor.AQUA;
                            } else if (!(FlagWatcher.class.isAssignableFrom(declaring)) || declaring == FlagWatcher.class) {
                                methodColor = ChatColor.GRAY;
                            }

                            String str = method.getName() + ChatColor.DARK_RED + "(" + ChatColor.GREEN + info.getName()
                                    + ChatColor.DARK_RED + ")";

                            map.put(str, methodColor);
                            methods.add(str);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    Collections.sort(methods, String.CASE_INSENSITIVE_ORDER);

                    for (int i = 0; i < methods.size(); i++) {
                        methods.set(i, map.get(methods.get(i)) + methods.get(i));
                    }

                    if (methods.isEmpty()) {
                        methods.add(ChatColor.RED + "No options with permission to use");
                    }

                    sender.sendMessage(ChatColor.DARK_RED + type.toReadable() + " options: "
                            + StringUtils.join(methods, ChatColor.DARK_RED + ", "));

                    if (ignored > 0) {
                        sender.sendMessage(ChatColor.RED + "Ignored " + ignored
                                + " options you do not have permission to use. Add 'show' to view unusable options.");
                    }

                    return true;
                }
            }
        }

        sender.sendMessage(ChatColor.RED + "You are forbidden to use this command.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] origArgs) {
        ArrayList<String> tabs = new ArrayList<String>();
        String[] args = getArgs(origArgs);

        for (String node : new String[]{
            "disguise", "disguiseradius", "disguiseentity", "disguiseplayer"
        }) {
            HashMap<DisguisePerm, HashMap<ArrayList<String>, Boolean>> perms = DisguiseParser.getPermissions(sender,
                    "libsdisguises." + node + ".");

            if (args.length == 0) {
                for (DisguisePerm type : perms.keySet()) {
                    if (type.isUnknown()) {
                        continue;
                    }

                    if (DisallowedDisguises.isAllowed(type.getType()))
                    {
                        tabs.add(type.toReadable().replaceAll(" ", "_"));
                    }
                }

                for (ParamInfo s : ReflectionFlagWatchers.getParamInfos()) {
                    tabs.add(s.getName().replaceAll(" ", ""));
                }
            } else if (DisguiseParser.getDisguisePerm(args[0]) == null) {
                tabs.add("Show");
            }
        }

        return filterTabs(tabs, origArgs);
    }

    /**
     * Send the player the information
     */
    @Override
    protected void sendCommandUsage(CommandSender sender, HashMap<DisguisePerm, HashMap<ArrayList<String>, Boolean>> map) {
        sender.sendMessage(ChatColor.RED + "/disguisehelp <DisguiseType> " + ChatColor.GREEN
                + "- View the options you can set on a disguise. Add 'show' to reveal the options you don't have permission to use");

        for (ParamInfo s : ReflectionFlagWatchers.getParamInfos()) {
            sender.sendMessage(ChatColor.RED + "/disguisehelp " + s.getName().replaceAll(" ", "") + ChatColor.GREEN + " - "
                    + s.getDescription());
        }
    }
}
