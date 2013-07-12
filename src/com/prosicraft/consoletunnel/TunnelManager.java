/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.consoletunnel;

import com.prosicraft.consoletunnel.mighty.util.MConfiguration;
import com.prosicraft.consoletunnel.mighty.util.MLog;
import com.prosicraft.consoletunnel.mighty.util.MResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author passi
 */
public class TunnelManager extends ArrayList<Tunnel> {
    
    public TunnelManager () {
        super();
    }
    
    public Tunnel getTunnelByTarget (CommandSender cs) {
        for ( Tunnel t1 : this ) if (t1.isTarget(cs)) return t1;        
        return null;
    }
    
    public boolean exists (String id) {
        for (Tunnel t1 : this ) if (t1.matchID(id)) return true; return false;
    }
    
    public Tunnel getTunnelByMembers (CommandSender cs1, CommandSender cs2) {        
        for ( Tunnel t : this ) {
            if (t.isDestination(cs1) && t.isTarget(cs2))
                return t;
        } return null;
    }
    
    public Tunnel getTunnelByMembers (CommandSender cs1, String s1) {        
        for ( Tunnel t : this ) {
            if (t.isDestination(cs1) && t.getTargetName().replace("(off)", "").equalsIgnoreCase(s1))
                return t;
        } return null;
    }
    
    public Tunnel getTunnelById (String id) {
        for (Tunnel t1 : this ) if (t1.matchID(id)) return t1; return null;
    }
    
    public List<Tunnel> getTunnelsByTarget (CommandSender cs) {
        List<Tunnel> res = new ArrayList<Tunnel>();
        for ( Tunnel t1 : this ) if (t1.isTarget(cs)) res.add(t1);
        return res;        
    }
    
    public List<Tunnel> getTunnelsByTarget (String s1) {
        List<Tunnel> res = new ArrayList<Tunnel>();
        for ( Tunnel t1 : this ) if (t1.isTargetName(s1)) res.add(t1);
        return res;        
    }
    
    public List<Tunnel> getTunnelsBySender(CommandSender cs) {
        List<Tunnel> res = new ArrayList<Tunnel>();
        for ( Tunnel t1 : this ) if (t1.isDestination(cs)) res.add(t1);
        return res; 
    }
    
    public int numOpenTunnels (List<Tunnel> stack) {
        int cnt = 0;
        for ( Tunnel t : stack ) if (t.isOpen()) cnt++;
        return cnt;
    }
    
    public List<Tunnel> getTunnelsBySenderName(String s1) {
        List<Tunnel> res = new ArrayList<Tunnel>();
        for ( Tunnel t1 : this ) if (t1.isDestination(s1)) res.add(t1);
        return res; 
    }           
    
    public int loadTunnels (MConfiguration config, JavaPlugin plugin) {                
        
        MLog.d("Loading tunnels...");
        
        if (config != null) {
            Set<String> keys = config.getKeys("");
            int count = 0;
            for (String s1 : keys) {                
                                
                // ============= LOAD THE TARGET CommandReceiver ===========
                CommandSender cs2 = null;
                if ( (cs2 = plugin.getServer().getPlayer(config.getString(s1 + ".target", ""))) == null )
                    if ( (cs2 = plugin.getServer().getOfflinePlayer(config.getString(s1 + ".target", "")).getPlayer()) == null )
                        if ( config.getString(s1 + ".target", "").equalsIgnoreCase("console") )
                            cs2 = plugin.getServer().getConsoleSender();
                        else
                            MLog.w("Can't load tunnel '" + s1 + "': target not set or not found. Will be set when target joins");
                
                
                if (exists(s1)) { 
                    Tunnel spec;
                    if ( (spec = getTunnelById(s1)) != null )                        
                        if ( !spec.isTargetSet() && cs2 != null )
                        {
                            spec.setTarget(cs2);
                            MLog.d("Target joined after creating tunnel: Setting target.");
                        }                    
                    continue;
                }
                
                MLog.d("Loading tunnel " + s1);
                Tunnel t = new Tunnel (config);
                boolean closed = !config.getBoolean(s1 + ".open", false);
                
                t.setTargetName(((cs2 != null) ? "" : "(off)") + config.getString(s1 + ".target", "---"));
                
                // ============= LOAD THE DEST CommandSender ===========
                CommandSender cs1 = null;
                if ( (cs1 = plugin.getServer().getPlayer(config.getString(s1 + ".dest", ""))) == null &&
                        config.getString(s1 + ".dest", "").equalsIgnoreCase("console"))
                    cs1 = plugin.getServer().getConsoleSender();
                else if ( (cs1 = plugin.getServer().getOfflinePlayer(config.getString(s1 + ".dest", "")).getPlayer()) == null ) {
                    MLog.w("Can't load tunnel '" + s1 + "': destination not set or not found. Skipped.");
                    continue;
                }                                                                                                              
                
                // ============= LOAD THE FLAGS ==========
                boolean b1 = config.getBoolean(s1 + ".flags.runas", false); config.set(s1 + ".flags.runas", b1);                                
                boolean b2 = config.getBoolean(s1 + ".flags.chat", false); config.set(s1 + ".flags.chat", b2);
                
                // ============= NOW INITIALIZE THE TUNNEL ============
                if ( t.authenticate(cs1.getName(), Integer.parseInt(s1)).equals("") ) {           // authenticate..
                    MLog.e("Can't load tunnel " + s1 + ": authentication failure. Skipped"); continue;
                }
                
                MResult res = null;
                if ((res = t.open(cs1, cs2, b1, b2)) != MResult.RES_SUCCESS) {      // open & create                                
                    MLog.e("Can't load tunnel " + s1 + ": Initialize (open) Failed. Skipped");
                    MLog.d("--> Got error: " + String.valueOf(res));
                    continue;
                }                
                
                if (closed) { 
                    if (t.close(cs1) != MResult.RES_SUCCESS)                // close if it shouldn't be open (s1.open = false)
                        MLog.w("Tunnel " + s1 + " should be closed, but an error occured. It's now open.");
                }
                
                this.add(t);
                config.save();
                count++;
                                
            }
            return count;
        } else return -1;
    }
}
