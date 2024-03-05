package se.enji.lep;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Home extends JavaPlugin {
	FileConfiguration config;
	
	public void onEnable() {
		config=getConfig();
		config.options().copyDefaults(true);
		saveConfig();
	}
	
	private String cstr(String key) {
		return config.getString(key);
	}
	
	private void setHome(Player p) {
		Location l = p.getLocation();
		config.set("users."+p.getName()+".home", l.getX()+","+ l.getY()+","+l.getZ()+","+l.getYaw()+","+l.getPitch()+","+l.getWorld().getName());
		saveConfig();
		p.sendMessage(cstr("msg.set-home.default"));
	}
	
	private void toHome(Player p) {
		String loc = config.getString("users."+p.getName()+".home");
		if (loc == null) {
			p.sendMessage(cstr("msg.to-home.no-home"));
			p.sendMessage(cstr("msg.to-home.how-to"));
			return;
		}
		String[] l = loc.split(",");
		p.teleport(new Location(getServer().getWorld(l[5]), Double.parseDouble(l[0]), Double.parseDouble(l[1]), Double.parseDouble(l[2]), Float.parseFloat(l[3]), Float.parseFloat(l[4])));
	}
	
	private void setNamedHome(String name, Player p) {
		Location l = p.getLocation();
		if (config.getString("users." + p.getName() + ".homes." + name) == null) {
			config.set("users." + p.getName() + ".homes." + name, l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch() + "," + l.getWorld().getName());
			saveConfig();
			String homeMsg = cstr("msg.set-home.named");
			homeMsg = homeMsg.replaceAll("%h",name);
			p.sendMessage(homeMsg);
		}
		else {
			String homeMsg = cstr("msg.set-home.error");
			homeMsg = homeMsg.replaceAll("%h",name);
			p.sendMessage(homeMsg);
		}
	}
	
	private void delNamedHome(String name, Player p) {
		config.set("users." + p.getName() + ".homes." + name, null);
		saveConfig();
		String homeMsg = cstr("msg.delete-home");
		homeMsg = homeMsg.replaceAll("%h",name);
		p.sendMessage(homeMsg);
	}
	
	private void toNamedHome(String name, Player p) {
		final String loc = this.config.getString("users." + p.getName() + ".homes." + name);
		if (loc == null) {
			String homeMsg = cstr("msg.to-home.no-named-home");
			homeMsg = homeMsg.replaceAll("%h",name);
			p.sendMessage(homeMsg);
			return;
		}
		String[] l = loc.split(",");
		p.teleport(new Location(getServer().getWorld(l[5]), Double.parseDouble(l[0]), Double.parseDouble(l[1]), Double.parseDouble(l[2]), Float.parseFloat(l[3]), Float.parseFloat(l[4])));
	}
	
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (cmd.equalsIgnoreCase("sethome")) {
				if (args.length == 1) {
					setNamedHome(args[0], p);
					return true;
				}
				if (args.length == 0) {
					setHome(p);
					return true;
				}
			}
			if (cmd.equalsIgnoreCase("delhome")) {
				if (args.length == 1) {
					delNamedHome(args[0], p);
					return true;
				}
			}
			if (cmd.equalsIgnoreCase("home")) {
				if (args.length == 1) {
					toNamedHome(args[0], p);
					return true;
				}
				toHome(p);
				return true;
			}
			if (cmd.equalsIgnoreCase("homes") || cmd.equalsIgnoreCase("listhomes")) {
				String div = cstr("msg.list-homes.divider");
				StringBuilder homes=new StringBuilder();
				for (String home:config.getConfigurationSection("users."+p.getName()+".homes").getKeys(false)) {
					if (homes.length()>0) homes.append(div);
					homes.append(home);
				}
				String ha = homes.toString();
				if (!ha.isEmpty()) {
					p.sendMessage(cstr("msg.list-homes.prefix") + ha);
				} else p.sendMessage(cstr("msg.list-homes.error"));
				return true;
			}
			return false;
		}
		return false;
	}
}
