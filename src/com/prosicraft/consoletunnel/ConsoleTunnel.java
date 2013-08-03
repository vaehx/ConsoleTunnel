/**
 *
 * CONSOLE TUNNEL PLUGIN by prosicraft
 *
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;

/**
 * Console Tunnel Plugin Main Class
 *
 * @author prosicraft
 */
public class ConsoleTunnel extends JavaPlugin
{
	private PluginDescriptionFile pdf	= null; // Plugin description File
	private CTPlayerListener pl		= null; // Player Listener
	private CTServerListener sl		= null; // Server Listener
	private C2CHandler c2chandler		= null; // Console Stream handler
	private MConfiguration config		= null; // Plugin configuration
	private TunnelManager tm		= null; // Manager class of tunnels

	/************************************************************************************************/
	/**
	 * Add prepending zeros
	 * @param number number of max digits
	 * @return String with prepended zeroes
	 */
	public static String prependZeros( String number )
	{
		String s = "000000000000" + number;
		return s.substring( s.length() - 4 );
	}

	/************************************************************************************************/
	/**
	 * Enable this plugin
	 */
	@Override
	public void onEnable()
	{
		i( "Loading..." );

		pdf = getDescription();

		tm = new TunnelManager( this );
		pl = new CTPlayerListener( tm, this );
		sl = new CTServerListener( this );

		loadConfig( "config.yml" );

		getServer().getPluginManager().registerEvents( pl, this );
		getServer().getPluginManager().registerEvents( sl, this );

		int n = 0;
		if( ( n = tm.loadTunnels( this ) ) > -1 )
		{
			i( "Loaded " + n + " tunnel(s) successfully." );
		}
		else
		{
			e( "Can't load any tunnel: Config file missing." );
		}

		i( "Opening C2CStream Handler... (may cause exception since this is a dev build!)" );

		c2chandler = new C2CHandler( tm );
		( ( CraftServer ) getServer() ).getLogger().addHandler( c2chandler );

		i( "Enabled Version " + pdf.getVersion() + " by " + pdf.getAuthors().get( 0 ) + "#b????" );
	}

	/************************************************************************************************/
	/**
	 * Disable this plugin
	 */
	@Override
	public void onDisable()
	{
		// Disable Handler
		d( "Remove handler..." );
		( ( CraftServer ) getServer() ).getLogger().removeHandler( c2chandler );

		i( "Disabled." );
	}

	/************************************************************************************************/
	/**
	 * Load the plugin configuration
	 * @param configfilename Configuration Filename
	 */
	public void loadConfig( String configfilename )
	{
		if( !this.getDataFolder().exists() )
		{
			this.getDataFolder().mkdirs();
		}

		File file = new File( this.getDataFolder(), configfilename );
		if( !file.exists() )
		{
			try
			{
				file.createNewFile();
			}
			catch( IOException ex )
			{
				e( "Can't load plugin configuration file at " + ( ( file != null ) ? file.getAbsolutePath() : "/?/" + configfilename ) );
			}
		}

		config = new MConfiguration( YamlConfiguration.loadConfiguration( file ), file );
		config.load();

		// Load UID Counter
		config.set( "global.uIDCounter", ( tm.uIDCounter = config.getInt( "global.uIDCounter", tm.uIDCounter ) ) );
	}

	/************************************************************************************************/
	/**
	 * Command handler of this plugin
	 * @param sender Sender of command
	 * @param command The actual command
	 * @param label Command label
	 * @param argsb command args
	 * @return true if any command handled
	 */
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] argsb )
	{
		String[] args = argsb;

		if( sender == null || command == null || label.equals( "" ) || args == null || args.length == 0 )
			return false;

		if( sender instanceof ConsoleCommandSender )
		{
			MLog.d( "Running command from console." );

			if( command.getLabel().equalsIgnoreCase( "ctunnel" ) )
			{

				if( ( new clearConsoleCommand( this, args ) ).run( ( ConsoleCommandSender ) sender, config, tm ) != commandResult.RES_SKIPPED )
					return true;

				sender.sendMessage( "This command does not work in console." );
				return true;

			}
			return false;
		}

		if( command.getLabel().equalsIgnoreCase( "rtunnel" ) && args.length == 1 )
		{
			command.setLabel( "ctunnel" );
			String[] newargs =
			{
				"open", args[0], "runas"
			};
			args = newargs;
			MLog.d( "Command label = " + command.getLabel() );
			MLog.d( "Args = " + args.toString() );
		}

		if( command.getLabel().equalsIgnoreCase( "ctunnel" ) )
		{

			if( ( new openCommand( this, args ) ).run( ( Player ) sender, config, tm ) != commandResult.RES_SKIPPED )
				return true;

			if( ( new clearCommand( this, args ) ).run( ( Player ) sender, config, tm ) != commandResult.RES_SKIPPED )
				return true;

			if( ( new listCommand( this, args ) ).run( ( Player ) sender, config, tm ) != commandResult.RES_SKIPPED )
				return true;

			if( ( new closeCommand( this, args ) ).run( ( Player ) sender, config, tm ) != commandResult.RES_SKIPPED )
				return true;

			sender.sendMessage( "This is the help - unbeatable." );
			return true;

		}

		return false;
	}

	/************************************************************************************************/
	/**
	 * Check if player has permissions
	 * @param p The Player
	 * @param path permission path
	 * @return true if player has permission, false if not
	 */
	public boolean hasPerms( Player p, String path )
	{
		return p.hasPermission( path );
	}

	/************************************************************************************************/
	/**
	 * Gets the plugin configuration
	 * @return MConfiguration
	 */
	public MConfiguration getMConfiguration()
	{
		return config;
	}

	/************************************************************************************************/
	/**
	 * Get the Tunnel Manager
	 * @return TunnelManager
	 */
	public TunnelManager getTunnelManager()
	{
		return tm;
	}

	/************************************************************************************************/
	/**
	 * Print info to log
	 * @param txt The Message
	 */
	public static void i( String txt )
	{
		MLog.i( txt );
	}

	/************************************************************************************************/
	/**
	 * Print error to log
	 * @param txt The Message
	 */
	public static void e( String txt )
	{
		MLog.e( txt );
	}

	/************************************************************************************************/
	/**
	 * Print Debug info to log
	 * @param txt The Message
	 */
	public static void d( String txt )
	{
		MLog.d( txt );
	}

	/************************************************************************************************/
	/**
	 * Print Warning to log
	 * @param txt the message
	 */
	public static void w( String txt )
	{
		MLog.w( txt );
	}
}
