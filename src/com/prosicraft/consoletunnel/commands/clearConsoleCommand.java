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
import java.util.logging.Logger;
import org.bukkit.command.ConsoleCommandSender;

/**
 *
 * @author passi
 */
public class clearConsoleCommand extends extendedCommand {        
    
    public clearConsoleCommand (ConsoleTunnel handle, String[] args) {
        super (handle, args);
    }
   
    public commandResult run(ConsoleCommandSender console, MConfiguration config, TunnelManager tm) {
        
        try {
            
            // Todo: implement Permissions!
            
            // /tunnel clear
            if ( this.hasArgs(1) && this.getArg(0).equalsIgnoreCase("clear") ) {                                                                
                
                Logger.getLogger("Minecraft").addHandler(null);
                
                if ( tm == null || tm.isEmpty() ) { MLog.w("There are no tunnels that could be cleared. Clearing config.yml"); }
                
                int n = 0;                
                
                for ( Tunnel t : tm ) { t.clear(); n++; }
                
                config.clear();
                config.save();                
                tm.clear();                                                                
                config.load();                
                
                console.sendMessage("Cleared all tunnels successfully.");                
                return commandResult.RES_SUCCESS;
                
            } else return skip ();
            
        } catch (Exception ex) {            
            MLog.e("(ClearCmd) " + ex.getMessage());
            ex.printStackTrace();            
            return commandResult.RES_ERROR;
        }
        
    }        
}
