/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.consoletunnel.commands;

import com.prosicraft.consoletunnel.bemyadvice.extendedCommand.commandResult;
import com.prosicraft.consoletunnel.bemyadvice.extendedCommand.extendedCommand;
import com.prosicraft.consoletunnel.ConsoleTunnel;
import com.prosicraft.consoletunnel.Tunnel;
import com.prosicraft.consoletunnel.TunnelManager;
import com.prosicraft.consoletunnel.mighty.util.MConfiguration;
import com.prosicraft.consoletunnel.mighty.util.MLog;
import com.prosicraft.consoletunnel.mighty.util.MResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class listCommand extends extendedCommand {        
    
    public listCommand (ConsoleTunnel handle, String[] args) {
        super (handle, args);
    }
   
    public commandResult run(Player p, MConfiguration config, TunnelManager tm) {
        
        try {
            
            // Todo: implement Permissions!
            
            // /tunnel list
            if ( this.hasArgs(1) && this.getArg(0).equalsIgnoreCase("list") ) {
                
                this.ev ( p );
                
                if ( tm == null || tm.isEmpty() )
                    return err (p, "There are no tunnels to list.");
                
                List<String> tunnels = new ArrayList<String>();                
                
                for ( Tunnel t : tm ) {
                    if ( t.func_4u7h3n7(p.getName()) && t.isDestination(p) )
                        tunnels.add(ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + ((t.isOpen()) ? "(o)" : "(closed)") + ChatColor.DARK_GRAY + " with " + ChatColor.AQUA + t.getTargetName() + ChatColor.DARK_GRAY + " and " + ChatColor.GRAY + "#" + ChatColor.GOLD + tm.indexOf(t) + ChatColor.DARK_GRAY + ", flags:" + 
                                ChatColor.DARK_AQUA + ((t.flagRunas()) ? " runas" : "") + ((t.flagChattunnel()) ? " chattunnel" : ""));
                }
                
                norm (p, ChatColor.GREEN + "There are " + tunnels.size() + " tunnel(s) you have created:");                
                for (String s : tunnels) {
                    norm (p, s);
                }
                
                return commandResult.RES_SUCCESS;                    
                
            } else return skip ();
            
        } catch (Exception ex) {            
            MLog.e("(ClearCmd) " + ex.getMessage());
            ex.printStackTrace();
            return err(p, "Failed to execute command.");
        }
        
    }        
}
