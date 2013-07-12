/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.consoletunnel.mighty.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author passi
 */
public class MConfiguration {
    private FileConfiguration   fc1 = null;
    private File                f1  = null;
    
    public MConfiguration (FileConfiguration fc, File f) {
        this.fc1 = fc;
        this.f1 = f;
    }

    public File getFile () {
        return f1;
    }        
    
    public FileConfiguration getConfig () {
        return fc1;
    }
    
    public void setFile (File f) {
        f1 = f;
    }
    
    public void setConfig (FileConfiguration fc) {
        fc1 = fc;
    }
    
    public void setProperty (String path, Object value) {
        fc1.set(path, value);
    }   
    
    public void set (String path, Object value) {
        fc1.set(path, value);
    }
    
    public boolean getBoolean (String path, boolean def) {
        return fc1.getBoolean(path, def);
    }
    
    public String getString (String path, String ref) {
        return fc1.getString(path, ref);
    }
    
    public Set<String> getKeys (String path, boolean deep) {
        try {
            return fc1.getConfigurationSection(path).getKeys(deep);
        } catch (NullPointerException nex) {
            return new HashSet<String>();
        }
    }
    
    public Set<String> getKeys (String path) {
        return getKeys (path, false);
    }
    
    public void save () {
        try {
            fc1.save(f1);
        } catch (IOException iex) {            
            MLog.e("Can't save configuration at " + ((f1 != null) ? f1.getAbsolutePath() : "not given configuration file!"));
        }
    }
    
    public void load () {
        try {
            fc1.load(f1);
        } catch (IOException iex) {
            MLog.e("Can't load configuration at " + ((f1 != null) ? f1.getAbsolutePath() : "not given configuration file!"));
        } catch (InvalidConfigurationException icex) {
            MLog.e("Loaded invalid configuration at " + ((f1 != null) ? f1.getAbsolutePath() : "not given configuration file!"));
        }
    }
    
    public void clear () {        
        for ( String s1 : fc1.getKeys(false) )
            fc1.set(s1, null);        
    }
} 
