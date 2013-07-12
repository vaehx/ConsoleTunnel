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
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class openCommand extends extendedCommand {        
    
    public openCommand (ConsoleTunnel handle, String[] args) {
        super (handle, args);
    }
   
    public commandResult run(Player p, MConfiguration config, TunnelManager tm) {
        
        try {
            
            // Todo: implement Permissions!                                                                     
            
            // /tunnel open <TARGET> [flags...]
            if ( this.hasArgs(2) && this.getArg(0).equalsIgnoreCase("open") || this.getArg(0).equalsIgnoreCase("o") ) {                                                            
                
                this.ev ( p );                                
                
                Tunnel t = null;
                boolean isConsole = this.getArg(1).equalsIgnoreCase("console");
                
                if ( (t = tm.getTunnelByMembers(p, getArg(1))) != null ) {
                    if (t.isOpen()) return err (p, "This tunnel is already open.");
                    MResult resb = null;
                    if ( ( resb = t.open(p, getParent().getServer().getPlayer(this.getArg(1)), t.flagRunas(), t.flagChattunnel()) ) == MResult.RES_SUCCESS ) {
                        MLog.d("Tunnel already existed. Reopened it.");
                        return suc (p, "Successfully reopened tunnel with " + this.getArg(1));
                    } else {
                        MLog.d("Got error: " + String.valueOf(resb));
                        return err (p, "Failed to reopen tunnel with " + this.getArg(1));
                    }                    
                }                                  
                
                if ( this.getParent().getServer().getPlayer(this.getArg(1)) == null  && !isConsole  ) {                    
                    MLog.d ("Got target (" + getArg(1) + ") is not an online player.");
                    return err (p, "Target not found.");
                } else if ( !isConsole ) {
                    MLog.d ("Got target (" + getArg(1) + ") is not the console.");
                    return err (p, "Target not found.");
                }
                
                t = new Tunnel (config);
                CommandSender cs = ((isConsole) ? getParent().getServer().getConsoleSender() : getParent().getServer().getPlayer(this.getArg(1)));
                t.authenticate(p.getName());
                tm.add(t);                                
                MResult res = t.open(p, cs, false, false);
                if (res == MResult.RES_NOACCESS)
                    return err (p, "You don't have access to this tunnel.");
                if (res == MResult.RES_ALREADY)
                    return err (p, "This tunnel is already open.");                
                return suc (p, "Opened Tunnel with " + getArg(1) + " successfully.");                    
                
            } else if ( this.getArgs().length > 2 && this.getArg(0).equalsIgnoreCase("open") ) {
                
                this.ev ( p );        
                
                boolean b1 = false;
                boolean b2 = false;
                boolean isConsole = this.getArg(1).equalsIgnoreCase("console");
                
                List<String> flags = new ArrayList<String>();
                Collections.addAll(flags, getArgs());
                flags.remove("open");
                flags.remove(getArg(1)); 
                for ( String s1 : flags ) {
                    MLog.d("Scanning arg: " + s1 + "(" + flags.indexOf(s1) + ")");
                    if ( s1.contains("chat") ) b2 = true;
                    if ( s1.contains("runas") ) b1 = true;
                }     
                   
                Tunnel t = null;
                
                if ( (t = tm.getTunnelByMembers(p, getArg(1))) != null ) {
                    if (t.isOpen()) return err (p, "This tunnel is already open.");
                    MResult resb = null;
                    if ( ( resb = t.open(p, getParent().getServer().getPlayer(this.getArg(1)), t.flagRunas(), t.flagChattunnel()) ) == MResult.RES_SUCCESS ) {
                        MLog.d("Tunnel already existed. Reopened it.");
                        return suc (p, "Successfully reopened tunnel with " + this.getArg(1));
                    } else {
                        MLog.d("Got error: " + String.valueOf(resb));
                        return err (p, "Failed to reopen tunnel with " + this.getArg(1));
                    }                    
                }                   
                
                if ( this.getParent().getServer().getPlayer(this.getArg(1)) == null && !isConsole ) {                    
                    MLog.d ("Got target (" + getArg(1) + ") is not an online player.");
                    return err (p, "Target not found.");
                } else if ( !isConsole ) {
                    MLog.d ("Got target (" + getArg(1) + ") is not the console.");
                    return err (p, "Target not found.");
                }
                
                if ( this.getParent().getServer().getPlayer(this.getArg(1)) == null  && !isConsole  ) {                    
                    MLog.d ("Got target (" + getArg(1) + ") is not an online player.");
                    return err (p, "Target not found.");
                } else if ( !isConsole ) {
                    MLog.d ("Got target (" + getArg(1) + ") is not the console.");
                    return err (p, "Target not found.");
                }                                                                                            
                
                t = new Tunnel (config);                    
                CommandSender cs = ((isConsole) ? getParent().getServer().getConsoleSender() : getParent().getServer().getPlayer(this.getArg(1)));
                t.authenticate(p.getName());
                tm.add(t);                                
                MResult res = t.open(p, cs, b1, b2);
                if (res == MResult.RES_NOACCESS)
                    return err (p, "You don't have access to this tunnel.");
                if (res == MResult.RES_ALREADY)
                    return err (p, "This tunnel is already open.");                
                return suc (p, "Opened Tunnel with " + getArg(1) + " successfully.");                                    
               
            } else return skip ();
            
        } catch (Exception ex) {            
            MLog.e("(ClearCmd) " + ex.getMessage());
            ex.printStackTrace();
            return err(p, "Failed to execute command.");
        }
        
    }        
}
