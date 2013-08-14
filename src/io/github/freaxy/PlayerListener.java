package io.github.freaxy;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.world.WorldSaveEvent;

public class PlayerListener
  implements Listener
{
  public CommandButtons plugin;

  public PlayerListener(CommandButtons instance)
  {
    this.plugin = instance;
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    Block block = event.getBlock();
    if (this.plugin.commandButtons.containsKey(block.getLocation()))
      this.plugin.commandButtons.remove(block.getLocation());
  }
  
  @EventHandler
  public void onWorldSave(WorldSaveEvent event) {
	  plugin.saveData();
  }

  @EventHandler
  public void onServerCommand(ServerCommandEvent event) {
	 if (event.getCommand().equalsIgnoreCase("save-all")) {
		 plugin.saveData();
	 }
  }
  
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Block clickedBlock = event.getClickedBlock();
    Player player = event.getPlayer();
    if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && 
      (this.plugin.isButton(clickedBlock)) && 
      (this.plugin.commandButtons.containsKey(clickedBlock.getLocation()))) {
      String[] commands = ((String)this.plugin.commandButtons.get(clickedBlock.getLocation())).replace("@p", player.getName()).split(" & ");
      for (int i = 0; i < commands.length; i++) {
        this.plugin.server.dispatchCommand(this.plugin.commandSender, commands[i]);
      }
      CraftPlayer cPlayer = (CraftPlayer)player;
      cPlayer.getHandle().updateInventory(cPlayer.getHandle().activeContainer);
    }
  }
}