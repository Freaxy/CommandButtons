package io.github.freaxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandButtons extends JavaPlugin {
	Logger logger;
	PlayerListener listener = new PlayerListener(this);
	Server server;
	ConsoleCommandSender commandSender;
	File data;
	Map<Location, String> commandButtons = new HashMap<Location, String>();

	public void onEnable() {
		this.server = getServer();
		getConfig().options().copyDefaults(true);
		loadData();
		saveConfig();
		this.logger = getLogger();
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(this.listener, this);
		this.commandSender = this.server.getConsoleSender();
	}

	public void saveData() {
		Location[] locations = new Location[this.commandButtons.size()];
		this.commandButtons.keySet().toArray(locations);

		try {
			data = new File("plugins/CommandButtons/data.txt");
			data.delete();
			data.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(data, true));
			for (int i = 0; i < locations.length; i++) {
				writer.write(locations[i].getWorld().getName() + " - " + locations[i].getBlockX() + "," + locations[i].getBlockY() + "," + locations[i].getBlockZ() + " - " + ((String) this.commandButtons.get(locations[i])).toString());
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadData() {
		data = new File("plugins/CommandButtons/data.txt");
		if (!data.exists())
			try {
				data.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		else {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(data)));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] lineSegment = line.split(" - ");
					String[] cords = lineSegment[1].split(",");
					this.commandButtons.put(new Location(this.server.getWorld(lineSegment[0]), Double.parseDouble(cords[0]), Double.parseDouble(cords[1]), Double.parseDouble(cords[2])), lineSegment[2]);
				}
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void onDisable() {
		saveData();
		saveConfig();
		commandButtons.clear();
	}

	public boolean isButton(Block block) {
		if ((block.getType() == Material.STONE_BUTTON) || (block.getType() == Material.WOOD_BUTTON)) {
			return true;
		}
		return false;
	}

	public String joinArray(String[] stringArray, String seperator, int offsetBegining) {
		String string = "";
		for (int i = offsetBegining; i < stringArray.length; i++) {
			string = string + stringArray[i] + " ";
		}
		return string;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("cb") || label.equalsIgnoreCase("CommandButton") || label.equalsIgnoreCase("CommandButtons")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				Block targetBlock = player.getTargetBlock(null, 16);
				if (isButton(targetBlock)) {
					if (args.length > 0) {
						switch (args[0]) {
						case "set":
							if (player.hasPermission("edit")) {
								commandButtons.put(targetBlock.getLocation(), joinArray(args, " ", 1));
								player.sendMessage(ChatColor.YELLOW + "Command set to: " + ChatColor.WHITE + joinArray(args, " ", 1));
							} else
								player.sendMessage(ChatColor.RED + "You don't have the permission to set and edit button commands.");
							break;
						case "clear":
							if (player.hasPermission("edit")) {
								commandButtons.remove(targetBlock.getLocation());
								player.sendMessage(ChatColor.YELLOW + "Command cleared for this button");
							} else
								player.sendMessage(ChatColor.RED + "You don't have the permission to set and edit button commands.");
							break;
						case "show":
							if (player.hasPermission("view")) {
								if (commandButtons.containsKey(targetBlock.getLocation()))
									player.sendMessage(ChatColor.YELLOW + "Current command for this button: " + ChatColor.WHITE + commandButtons.get(targetBlock.getLocation()));
								else
									player.sendMessage(ChatColor.YELLOW + "No command currently set");
							} else
								player.sendMessage(ChatColor.RED + "You don't have the permission to view button commands");
							break;
						}
					} else
						return false;
				} else
					player.sendMessage(ChatColor.RED + "You must be looking at a button");

				return true;
			}
			sender.sendMessage("Must be player!");
		}

		return false;
	}
}