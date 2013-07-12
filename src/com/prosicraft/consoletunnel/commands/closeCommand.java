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
import java.util.List;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class closeCommand extends extendedCommand {        
    
    public closeCommand (ConsoleTunnel handle, String[] args) {
        super (handle, args);
    }
   
    public commandResult run(Player p, MConfiguration config, TunnelManager tm) {
        
        try {
            
            // Todo: implement Permissions!
            
            // /tunnel close <TARGET>
            if ( this.hasArgs(2) && this.getArg(0).equalsIgnoreCase("close") || this.getArg(0).equalsIgnoreCase("cl") ) {
                
                this.ev ( p );
                
                List<Tunnel> lt = tm.getTunnelsBySender(p);
                int n = 0;                
                int closed = 0;
                
                for (Tunnel t : lt) {                 
                    MLog.d("Checking target name: " + t.getTargetName() + " :: " + getArg(1));
                    if ( t.getTargetName().replace("(offline)", "").equalsIgnoreCase(getArg(1)) ) {
                        MResult res;
                        if ( (res = t.close(p)) == MResult.RES_SUCCESS)
                            n++;                                                                                    
                        else if ( res == MResult.RES_ALREADY ) 
                            closed++;
                        else MLog.d("Got result in closeCommand: " + String.valueOf(res));
                    }
                }
                
                if (n == 0 && closed > 0) {
                    return err (p, "You already closed the " + closed + " tunnel(s) with " + getArg(1));
                } else if (n == 0)
                    return err (p, "There are " + lt.size() + " tunnel(s) you can't close (No Access or damaged)");
                
                MLog.d("Returning: suc (" + String.valueOf(p) + ", 'Closed " + n + " tunnel(s) with " + getArg(1) + " successfully.'");
                return suc (p, "Closed " + n + " tunnel(s) with " + getArg(1) + " successfully.");                    
                
            } else return skip ();
            
        } catch (Exception ex) {            
            MLog.e("(ClearCmd) " + ex.getMessage());
            ex.printStackTrace();
            return err(p, "Failed to execute command.");
        }
        
    }        
}
