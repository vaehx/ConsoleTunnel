/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.consoletunnel;

import com.prosicraft.consoletunnel.mighty.util.MConfiguration;
import com.prosicraft.consoletunnel.mighty.util.MCrypt;
import com.prosicraft.consoletunnel.mighty.util.MLog;
import com.prosicraft.consoletunnel.mighty.util.MResult;
import java.security.SecureRandom;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class Tunnel {
    
    private CommandSender cs1       = null;
    private CommandSender cs2       = null; 
    private String        cs2name   = "NoName";
    private boolean       open      = false;
    private boolean       b1        = false;    // command flag
    private boolean       b2        = false;    // chat(send) flag
    private MConfiguration mc1      = null;
    private String        authsalt   = "einbisschenBEEschadetnie98765redfghjui8u7z654ewedfghzjui8u7z6t5r4ewsdfghzjuiu87z6t5redscfvgbhj234567890ploijuztredsxcvbgh";
    private String        auth      = "einbisschenBEEschadetnie";
    private int           id        = -1;
    
    public Tunnel (MConfiguration config) {
        mc1 = config;                            
    }        
    
    public String authenticate (String username) {
        if ( id != -1) return "";
        SecureRandom sr = new SecureRandom();
        while (id == -1)
            id = Math.abs(sr.nextInt());        
        return authenticate (username, id);
    }
    
    public String authenticate (String username, int id) {                       
        this.id = id;
        return (auth = MCrypt.getHash(1000, username, authsalt));
    }
    
    public boolean func_4u7h3n7 (String s1) {
        return auth.equals(MCrypt.getHash(1000, s1, authsalt));
    }
    
    public boolean isDestination (CommandSender cs) {
        return (cs1.getName().equals(cs.getName()));
    }
    
    public boolean isDestination (String s1) {
        return (cs1.getName().equals(s1));
    }
    
    public boolean isTarget (CommandSender cs) {        
        try {
            return (cs2.getName().equals(cs.getName()));
        } catch (NullPointerException nex) {
            return false;
        }
    }        
    
    public MResult open (CommandSender dest, CommandSender target, boolean targetRunas, boolean targetChat) {
        if ( !func_4u7h3n7(dest.getName()) )
            return MResult.RES_NOACCESS;
        
        if ( id == -1 )
            return MResult.RES_NOACCESS;
        
        if ( open == true )
            return MResult.RES_ALREADY;
        
        if (mc1 != null) {                            
            mc1.set(String.valueOf(id) + ".open", true);            
            mc1.set(String.valueOf(id) + ".dest", dest.getName());
            
            if (target != null)
                mc1.set(String.valueOf(id) + ".target", target.getName());
            
            // Set the flags
            mc1.set(String.valueOf(id) + ".flags.runas", targetRunas);
            mc1.set(String.valueOf(id) + ".flags.chat", targetChat);
            mc1.save();
        }        
        this.cs1 = dest;
        this.cs2 = target;        
        this.open = true;        
        this.b1 = targetRunas;
        this.b2 = targetChat;        
        return MResult.RES_SUCCESS;
    } 
    
    public MResult clear () {                       
        if ( mc1 != null ) {
            mc1.set(String.valueOf(id), null);
            return MResult.RES_SUCCESS;
        } else return MResult.RES_NOTINIT;     
    }
    
    public MResult send (String s1) {                
        if ( s1 == null || s1.equals("") )            
            return MResult.RES_NOTGIVEN;
        
        if ( !open ) return MResult.RES_NOTINIT;
        
        cs1.sendMessage(ChatColor.DARK_GRAY + ((cs2.getName().equalsIgnoreCase("console")) ? "sys" : cs2.getName()) + "# " + ChatColor.GRAY + s1 );
        return MResult.RES_SUCCESS;
    }        
    
    public MResult dispatch (String cmd) {
                        
        MLog.d("Dispatching command: " + cmd);        
        
        if ( cmd.contains("tunnel") ) return MResult.RES_UNKNOWN;
        
        if ( !b1 ) return MResult.RES_NOACCESS;
        
        if ( cmd == null || cmd.equals("") )
            return MResult.RES_NOTGIVEN;        
        
        if ( !open || !isTargetSet() ) return MResult.RES_NOTINIT;
        
        MLog.d("Passed checks on dispatching command: '" + cmd + "'");
        
        try {
            cs1.sendMessage(ChatColor.DARK_GRAY + "Runas " + ChatColor.GRAY + cs2.getName());                                    
            if ( (cs2 instanceof Player) && !((Player)cs2).performCommand(cmd.substring(1)) )
                throw new CommandException ("Failed to execute command (unknown reason)");
            else if ( (cs2 instanceof ConsoleCommandSender) && !((ConsoleCommandSender)cs2).getServer().dispatchCommand(cs2, cmd) )
                throw new CommandException ("Failed to execute command (Can't force command to server. Is Bukkit up to date?)");
            MLog.d("Dispatched command.");
            return MResult.RES_SUCCESS;
        } catch (CommandException ex) {            
            MLog.d("Dispatched command, but can't find command.");            
            return MResult.RES_ERROR;
        } catch (Exception ex) {
            MLog.e("Can't dispatch command '" + cmd + "' to " + String.valueOf(cs2) + ": " + ex.getMessage());
            ex.printStackTrace();
            return MResult.RES_ERROR;
        }              
        
    }
    
    public MResult close (CommandSender cs) {
        
        MLog.d ("Closing tunnel by " + cs.getName() + " with id: " + id);
        if ( !func_4u7h3n7(cs.getName()) || id == -1 )
            return MResult.RES_NOACCESS;
        
        if ( !open )
            return MResult.RES_ALREADY;
                
        if (mc1 != null) {                            
            mc1.set(String.valueOf(id) + ".open", false);            
        }
        open = false;
        MLog.d ("Closed tunnel successfully.");
        return MResult.RES_SUCCESS;        
    }          
    
    public String getTargetName () {        
        return ((cs2 != null) ? cs2.getName() : cs2name);
    }
    
    public String getDestinationName () {
        return ((cs1 != null) ? cs1.getName() : "fail");
    }
    
    public void setTarget (CommandSender target) {
        this.cs2 = target;
        if ( target != null ) setTargetName (target.getName());
        if ( mc1 != null )
            mc1.set(String.valueOf(id) + ".target", target.getName());        
    }
    
    public void setTargetName (String name) {
        this.cs2name = name;
    }
    
    public boolean isDestinationSet () {
        return (cs1 != null);
    }
    
    public boolean isTargetSet () {
        return (cs2 != null);
    }
    
    public boolean isTargetName (String s1) {        
        if (cs2 == null) return false;
        if (cs2.getName().equalsIgnoreCase(s1)) return true;
        return false;
    }
    
    public boolean flagRunas () {
        return b1;
    }
    
    public boolean flagChattunnel () {
        return b2;
    }
    
    public boolean isOpen () {
        return open;
    }
    
    public boolean matchID (String id) {
        return id.equals(String.valueOf(this.id));
    }
}