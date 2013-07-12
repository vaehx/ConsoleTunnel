/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.consoletunnel;

import com.prosicraft.consoletunnel.mighty.util.MConfiguration;
import com.prosicraft.consoletunnel.mighty.util.MLog;
import com.prosicraft.consoletunnel.mighty.util.MResult;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author prosicraft
 */
public class CTPlayerListener implements Listener {
    
    TunnelManager tm = null;
    private MConfiguration config = null;
    private JavaPlugin handle = null;
    
    public CTPlayerListener (TunnelManager tm1, MConfiguration config, JavaPlugin _handle) {        
        tm = tm1;
        this.config = config;
        this.handle = _handle;
    }   

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        
        MLog.d("Inside onPlayerCommandPreprocess");
        
        if (tm == null) return;
        
        List<Tunnel> mytunnels = tm.getTunnelsByTarget(event.getPlayer());
        if ( !mytunnels.isEmpty() )        
            for ( Tunnel t : mytunnels )
                t.send(event.getMessage());
        
        
        mytunnels = tm.getTunnelsBySender(event.getPlayer());
        if ( mytunnels.isEmpty() ) {
            MLog.d("Got no tunnels onPlayerCommandPreprocess");
            return;
        }
        
        if ( event.getMessage().startsWith("/tunnel") ) {
            MLog.d("Skipping tunnel command from " + event.getPlayer().getName());
            return;
        }            
        
        int cnt = 0;
                
        MResult res = null;
        for ( Tunnel t : mytunnels ) {            
            if ( (res =t.dispatch(event.getMessage())) == MResult.RES_SUCCESS )
                cnt++;
            else
                MLog.d("Got error at onPlayerCommandPreprocess.dispatch: " + String.valueOf(res));
        }
        
        if ( cnt > 0)
            event.setCancelled(true);
        
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        MLog.d("Counting tunnels on player-login [Player=" + event.getPlayer().getName() + "]");
        MLog.d("Reload remaining Tunnels...");
        
        tm.loadTunnels(config, handle);
        
        List<Tunnel> mytunnels = tm.getTunnelsBySender(event.getPlayer());        
        if (mytunnels != null && !mytunnels.isEmpty())
            event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "[" + handle.getDescription().getName() + "] " +
                    ChatColor.GRAY + "There are " + ChatColor.DARK_AQUA + mytunnels.size() + ChatColor.GRAY +
                    " tunnel(s). (open: " + ChatColor.GOLD + tm.numOpenTunnels(mytunnels) + ChatColor.GRAY + ")");        
    }        
}
