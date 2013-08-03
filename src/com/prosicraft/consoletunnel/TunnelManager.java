/*
 * The Tunnel Manager
 */
package com.prosicraft.consoletunnel;

import com.prosicraft.consoletunnel.mighty.util.MConfiguration;
import com.prosicraft.consoletunnel.mighty.util.MLog;
import com.prosicraft.consoletunnel.mighty.util.MResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class TunnelManager extends ArrayList<Tunnel>
{

	public int uIDCounter = 0;
	public ConsoleTunnel pluginHandle = null;

	public TunnelManager( ConsoleTunnel plugin )
	{
		super();
		pluginHandle = plugin;
	}

	public MResult addTunnel( String executorName, String targetName, boolean runas )
	{
		// Get the executor. Should be online
		CommandSender executor;

		if( executorName.equalsIgnoreCase( "console" ) )
			executor = pluginHandle.getServer().getConsoleSender();
		else
			executor = pluginHandle.getServer().getPlayer( executorName );

		// Get the target.
		// If not an online user and not console, only set name
		CommandSender target;

		if( targetName.equalsIgnoreCase( "console" ) )
			target = pluginHandle.getServer().getConsoleSender();
		else
			target = pluginHandle.getServer().getPlayer( targetName );

		// Only open tunnel if executor and target is set
		boolean open = ( executor != null && target != null );

		Tunnel t = new Tunnel( pluginHandle, uIDCounter, open, runas );
		t.create( executor, executorName, target, targetName );

		this.add( t );
		uIDCounter++;

		if( pluginHandle != null )
		{
			pluginHandle.getMConfiguration().set( "global.uIDCounter", uIDCounter);
			pluginHandle.getMConfiguration().save();
		}
		else
			MLog.e( "Cannot save new tunnel to conf: Plugin Handle not initialized" );

		return MResult.RES_SUCCESS;
	}

	public Tunnel getTunnelByTarget( CommandSender cs )
	{
		for( Tunnel t1 : this )
		{
			if( t1.isTarget( cs ) )
				return t1;
		}
		return null;
	}

	public boolean exists( String id )
	{
		for( Tunnel t1 : this )
		{
			if( t1.matchID( id ) )
				return true;
		}
		return false;
	}

	public Tunnel getTunnelByMembers( CommandSender cs1, CommandSender cs2 )
	{
		for( Tunnel t : this )
		{
			if( t.isExecutor( cs1 ) && t.isTarget( cs2 ) )
				return t;
		}
		return null;
	}

	public Tunnel getTunnelByMembers( CommandSender cs1, String s1 )
	{
		for( Tunnel t : this )
		{
			if( t.isExecutor( cs1 ) )
				return t;
		}
		return null;
	}

	public Tunnel getTunnelById( String id )
	{
		for( Tunnel t1 : this )
			if( t1.matchID( id ) )
				return t1;
		return null;
	}

	public List<Tunnel> getTunnelsByTarget( CommandSender cs )
	{
		List<Tunnel> res = new ArrayList<>();
		for( Tunnel t1 : this )
			if( t1.isTarget( cs ) )
				res.add( t1 );
		return res;
	}

	/**
	 * Get those tunnels, where the username is the target
	 * @param username The target user
	 * @return list of tunnels
	 */
	public List<Tunnel> getTunnelsByTarget( String username )
	{
		List<Tunnel> res = new ArrayList<>();
		for( Tunnel t1 : this )
		{
			if( t1.isTargetName( username ) )
				res.add( t1 );
		}
		return res;
	}

	public List<Tunnel> getTunnelsBySender( CommandSender cs )
	{
		List<Tunnel> res = new ArrayList<>();
		for( Tunnel t1 : this )
		{
			if( t1.isExecutor( cs ) )
				res.add( t1 );
		}
		return res;
	}

	public List<Tunnel> getTunnelsBySender( String username )
	{
		List<Tunnel> res = new ArrayList<>();
		for( Tunnel t1 : this )
		{
			if( t1.isExecutor( username ) )
				res.add( t1 );
		}
		return res;
	}

	public int numOpenTunnels( List<Tunnel> stack )
	{
		int cnt = 0;
		for( Tunnel t : stack )
		{
			if( t.isOpen() )
				cnt++;
		}
		return cnt;
	}

	public void handlePlayerJoin( Player p )
	{
		// go through all tunnels and add commandSenders
		int nUpdatedTunnels = 0;
		for( Tunnel t : this )
		{
			MLog.d( "Checking tunnel [executor=" + t.executorName + ", target=" + t.targetName + "]" );
			if( t.executorName.equalsIgnoreCase( p.getName() ) )
			{
				nUpdatedTunnels++;
				t.executor = p;
			}
			else if( t.targetName.equalsIgnoreCase( p.getName() ) )
			{
				nUpdatedTunnels++;
				t.target = p;
			}
		}
		MLog.d( nUpdatedTunnels + " tunnels updated" );
	}

	public int loadTunnels( ConsoleTunnel plugin )
	{
		MLog.d( "Loading tunnels..." );

		MConfiguration config = plugin.getMConfiguration();

		if( config != null )
		{
			Set<String> keys = config.getKeys( "" );
			int count = 0;
			for( String s1 : keys )
			{

				if( s1.equals( "global" ) ) continue;

				MLog.d( "Loading tunnel " + s1 );

				// Get the id
				int id = -1;
				try
				{
					id = Integer.parseInt( s1 );
				}
				catch( NumberFormatException ex )
				{
					MLog.e( "Cannot load tunnel " + s1 + ". This is not a valid id. Skipping." );
					continue;
				}

				// Get the target
				String targetName = config.getString( s1 + ".target", "" );

				CommandSender cs2;
				if( ( cs2 = plugin.getServer().getPlayer( targetName ) ) == null )
				{
					if( ( cs2 = plugin.getServer().getOfflinePlayer( targetName ).getPlayer() ) == null )
					{
						if( targetName.equalsIgnoreCase( "console" ) )
						{
							cs2 = plugin.getServer().getConsoleSender();
						}
						else
						{
							MLog.w( "Can't load tunnel '" + s1 + "': target not set or not found. Will be set when target joins" );
						}
					}
				}

				// Load the executor
				String execName = config.getString( s1 + ".executor", "" );

				CommandSender cs1;
				if( ( cs1 = plugin.getServer().getPlayer( execName ) ) == null )
				{
					if( ( cs1 = plugin.getServer().getOfflinePlayer( execName ).getPlayer() ) == null )
					{
						if( execName.equalsIgnoreCase( "console" ) )
						{
							cs1 = plugin.getServer().getConsoleSender();
						}
						else
						{
							MLog.w( "Can't load tunnel '" + s1 + "': executor '" + execName + "' not set or not found. Will be set when player joins." );
						}
					}
				}

				// Load states
				boolean open = config.getBoolean( s1 + ".open", false );
				boolean useRunas = config.getBoolean( s1 + ".flags.runas", false );

				// ============= NOW INITIALIZE THE TUNNEL ============
				Tunnel t = new Tunnel( plugin, id, open, useRunas );
				t.executor = cs1;
				t.executorName = execName;
				t.target = cs2;
				t.targetName = targetName;

				this.add( t );
				count++;

			}
			return count;
		}
		else
			return -1;
	}
}
