/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.consoletunnel;

import com.prosicraft.consoletunnel.bemyadvice.extendedCommand.commandResult;
import com.prosicraft.consoletunnel.commands.clearCommand;
import com.prosicraft.consoletunnel.commands.clearConsoleCommand;
import com.prosicraft.consoletunnel.commands.closeCommand;
import com.prosicraft.consoletunnel.commands.listCommand;
import com.prosicraft.consoletunnel.commands.openCommand;
import com.prosicraft.consoletunnel.mighty.util.MConfiguration;
import com.prosicraft.consoletunnel.mighty.util.MLog;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author prosicraft
 */
public class ConsoleTunnel extends JavaPlugin {  
    
    private PluginDescriptionFile pdf = null;
    
    private CTPlayerListener pl = null;
    private CTServerListener sl = null;    
    //private Handler c2chandler = null;  // --> StreamHandler
    
    private MConfiguration config = null;
    
    private TunnelManager tm = null;
    
    public static void i (String txt) { MLog.i(txt); }
    public static void e (String txt) { MLog.e(txt); }
    public static void d (String txt) { MLog.d(txt); }
    public static void w (String txt) { MLog.w(txt); }
    
    @Override
    public void onDisable() {                
        
        config.save();
        
        i ("Disabled.");                     
        
    }

    public static String prependZeros(String number) { 
        String s= "000000000000"+number;
        return s.substring(s.length()-4);
    }

    @Override
    public void onEnable() {
        i ("Loading...");
        
        pdf = getDescription();     
        
        loadConfig ("config.yml");
        
        tm = new TunnelManager();
        pl = new CTPlayerListener (tm, config, this);
        sl = new CTServerListener (this);
        
        getServer().getPluginManager().registerEvents(pl, this);
        getServer().getPluginManager().registerEvents(sl, this);                
        
        int n = 0;
        if ( (n =tm.loadTunnels(config, this)) > -1 ) {
            i ("Loaded " + n + " tunnel(s) successfully.");
        } else {
            e ("Can't load any tunnel: Config file missing.");
        }
        
        i ("Opening C2CStream Handler... (may cause exception since this is a dev build!)");                
    
        
        Logger.getLogger("Minecraft").addHandler( new C2CHandler(tm) );                    
        
        i ("Enabled Version " + pdf.getVersion() + " by " + pdf.getAuthors().get(0) + "#b????");
    }
    
    public void loadConfig (String configfilename) {                    
        
        if ( !this.getDataFolder().exists() ) {
            this.getDataFolder().mkdirs();
        }
            
        File file = new File (this.getDataFolder(), configfilename);
        if ( !file.exists() ) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                e ("Can't load plugin configuration file at " + ((file != null) ? file.getAbsolutePath() : "/?/" + configfilename));
            }
        }        
        
        config = new MConfiguration ( YamlConfiguration.loadConfiguration(file), file );
        config.load();
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] argsb) {                       
        
        String[] args = argsb;
        
        if (sender == null || command == null || label.equals("") || args == null || args.length == 0)
            return false;
        
        if ( sender instanceof ConsoleCommandSender ) {
            MLog.d ("Running command from console.");
            
            if ( command.getLabel().equalsIgnoreCase("ctunnel") ) {
                
                if ( (new clearConsoleCommand(this, args)).run((ConsoleCommandSender)sender, config, tm) != commandResult.RES_SKIPPED )
                    return true;
                
                sender.sendMessage("This command does not work in console.");
                return true;
                
            }
            return false;
        }                  
        
        if ( command.getLabel().equalsIgnoreCase("rtunnel") && args.length == 1 ) {
            command.setLabel("ctunnel");
            String[] newargs = { "open", args[0], "runas" };                                    
            args = newargs;
            MLog.d("Command label = " + command.getLabel());
            MLog.d("Args = " + args.toString());                    
        }
        
        if ( command.getLabel().equalsIgnoreCase("ctunnel") ) {
            
            if ( (new openCommand(this, args)).run((Player) sender, config, tm) != commandResult.RES_SKIPPED )
                return true;
            
            if ( (new clearCommand(this, args)).run((Player) sender, config, tm) != commandResult.RES_SKIPPED )
                return true;
            
            if ( (new listCommand(this, args)).run((Player) sender, config, tm) != commandResult.RES_SKIPPED )
                return true;
            
            if ( (new closeCommand(this, args)).run((Player) sender, config, tm) != commandResult.RES_SKIPPED )
                return true;
            
            sender.sendMessage("This is the help - unbeatable.");
            return true;
            
        }
        
        return false;
    }   
    
    public boolean hasPerms (Player p, String path) {
        return p.hasPermission(path);
    }
    
    public MConfiguration getMConfiguration () {
        return config;
    }
    
    public TunnelManager getTunnelManager () {
        return tm;
    }
    
}

