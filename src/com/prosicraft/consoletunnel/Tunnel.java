/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.consoletunnel;

import com.prosicraft.consoletunnel.mighty.util.MConfiguration;
import com.prosicraft.consoletunnel.mighty.util.MLog;
import com.prosicraft.consoletunnel.mighty.util.MResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class Tunnel
{

	public String executorName	= "";
	public String targetName	= "";
	public CommandSender executor	= null;		// Executor
	public CommandSender target	= null;		// Target
	public boolean open		= false;	// is this tunnel open?
	public boolean useRunas	= false;	// command flag
	public int id			= -1;		// tunnel id

	public ConsoleTunnel plugin	= null;		// plugin instance

	public Tunnel( ConsoleTunnel plugin, int id, boolean isOpen, boolean useRunas )
	{
		this.plugin = plugin;
		this.id = id;
		this.open = isOpen;
		this.useRunas = useRunas;
	}

	public boolean isExecutor( CommandSender cs )
	{
		return ( executor.getName().equals( cs.getName() ) );
	}

	public boolean isExecutor( String username )
	{
		return ( executor.getName().equals( username ) );
	}

	public boolean isTarget( CommandSender cs )
	{
		return target == cs;
	}

	public boolean isTarget( String username )
	{
		if( target == null ) return false;
		return target.getName().equalsIgnoreCase( username );
	}

	public MResult create( CommandSender dest, CommandSender target )
	{
		if( id == -1 )
			return MResult.RES_NOTGIVEN;

		if( open == true )
			return MResult.RES_ALREADY;

		// Add new tunnel to configuration
		if( plugin != null )
		{
			MConfiguration config = plugin.getMConfiguration();
			config.set( String.valueOf( id ) + ".open", true );
			config.set( String.valueOf( id ) + ".executor", dest.getName() );
			config.set( String.valueOf( id ) + ".target", target.getName() );
			config.set( String.valueOf( id ) + ".flags.runas", useRunas );
			config.save();
		}
		this.executor = dest;
		this.target = target;
		this.open = true;

		return MResult.RES_SUCCESS;
	}

	/**
	 * Open closed tunnel
	 * @return
	 */
	public MResult open()
	{
		if( id == -1 )
			return MResult.RES_NOTGIVEN;

		if( open )
			return MResult.RES_ALREADY;

		open = true;

		// Update configuration file
		if( plugin != null )
		{
			plugin.getMConfiguration().set( String.valueOf( id ) + ".open", true );
			plugin.getMConfiguration().save();
		}

		return MResult.RES_SUCCESS;
	}

	public MResult delete()
	{
		if( plugin != null )
		{
			plugin.getMConfiguration().set( String.valueOf( id ), null );
			return MResult.RES_SUCCESS;
		}
		else
			return MResult.RES_NOTINIT;
	}

	public MResult sendNotification( String s1 )
	{
		if( s1 == null || s1.equals( "" ) )
			return MResult.RES_NOTGIVEN;

		if( !open )
			return MResult.RES_NOTINIT;

		executor.sendMessage( ChatColor.DARK_GRAY + ( ( target.getName().equalsIgnoreCase( "console" ) ) ? "sys" : target.getName() ) + "# " + ChatColor.GRAY + s1 );
		return MResult.RES_SUCCESS;
	}

	public MResult dispatch( String cmd )
	{

		MLog.d( "Dispatching command: " + cmd );

		if( cmd.contains( "tunnel" ) )
		{
			return MResult.RES_UNKNOWN;
		}

		if( !useRunas )
		{
			return MResult.RES_NOACCESS;
		}

		if( cmd.equals( "" ) )
			return MResult.RES_NOTGIVEN;

		if( !open || !isTargetSet() )
			return MResult.RES_NOTINIT;

		MLog.d( "Passed checks on dispatching command: '" + cmd + "'" );

		try
		{
			executor.sendMessage( ChatColor.DARK_GRAY + "Runas " + ChatColor.GRAY + target.getName() );

			if( ( target instanceof Player ) && !( ( Player ) target ).performCommand( cmd.substring( 1 ) ) )
			{
				throw new CommandException( "Failed to execute command (unknown reason)" );
			}
			else if( ( target instanceof ConsoleCommandSender ) && !( ( ConsoleCommandSender ) target ).getServer().dispatchCommand( target, cmd ) )
			{
				throw new CommandException( "Failed to execute command (Can't pass command to server. Is Bukkit up to date?)" );
			}
			MLog.d( "Dispatched command." );
			return MResult.RES_SUCCESS;
		}
		catch( CommandException ex )
		{
			MLog.d( "Dispatched command, but can't find command." );
			return MResult.RES_ERROR;
		}
		catch( Exception ex )
		{
			MLog.e( "Can't dispatch command '" + cmd + "' to " + String.valueOf( target ) + ": " + ex.getMessage() );
			ex.printStackTrace( System.out );
			return MResult.RES_ERROR;
		}

	}

	public MResult close( CommandSender cs )
	{

		MLog.d( "Closing tunnel by " + cs.getName() + " with id: " + id );

		if( !open )
			return MResult.RES_ALREADY;

		if( plugin != null )
		{
			plugin.getMConfiguration().set( String.valueOf( id ) + ".open", false );
			plugin.getMConfiguration().save();
		}
		open = false;
		MLog.d( "Closed tunnel successfully." );
		return MResult.RES_SUCCESS;
	}

	public void setTarget( CommandSender target )
	{
		this.target = target;
		if( target != null )
		{
			setTargetName( target.getName() );
			if( plugin != null )
			{
				plugin.getMConfiguration().set( String.valueOf( id ) + ".target", target.getName() );
				plugin.getMConfiguration().save();
			}
		}
	}

	public String getTargetName()
	{
		return ( ( target != null ) ? target.getName() : targetName );
	}

	public void setTargetName( String name )
	{
		this.targetName = name;
	}

	public boolean isTargetSet()
	{
		return ( target != null );
	}

	public boolean isTargetName( String s1 )
	{
		if( target == null )
			return false;
		if( target.getName().equalsIgnoreCase( s1 ) )
			return true;
		return false;
	}

	public void setExecutorName( String name )
	{
		this.executorName = name;
	}

	public String getExecutorName()
	{
		return ( ( executor != null ) ? executor.getName() : executorName );
	}

	public boolean isExecutorSet()
	{
		return ( executor != null );
	}

	public boolean flagRunas()
	{
		return useRunas;
	}

	public boolean isOpen()
	{
		return open;
	}

	public boolean matchID( String id )
	{
		return id.equals( String.valueOf( this.id ) );
	}
}