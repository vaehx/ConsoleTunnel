/**
 * Player Listener
 */
package com.prosicraft.consoletunnel;

import com.prosicraft.consoletunnel.mighty.util.MLog;
import com.prosicraft.consoletunnel.mighty.util.MResult;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author prosicraft
 */
public class CTPlayerListener implements Listener
{
	public TunnelManager tm	= null; // the tunnel manager
	public ConsoleTunnel ct	= null; // The plugin instance

	public CTPlayerListener( TunnelManager tm1, ConsoleTunnel ctplugin )
	{
		this.tm = tm1;
		this.ct = ctplugin;
	}

	/**
	 * Handle inputs by player
	 * @param event
	 */
	@EventHandler( priority = EventPriority.LOW )
	public void onPlayerCommandPreprocess( PlayerCommandPreprocessEvent event )
	{
		if( tm == null )
			return;

		// Send this message to all tunnels this player is connected to
		// (Only the Notification)
		List<Tunnel> mytunnels = tm.getTunnelsByTarget( event.getPlayer() );
		if( !mytunnels.isEmpty() )
		{
			for( Tunnel t : mytunnels )
			{
				t.sendNotification( event.getMessage() );
			}
		}
		else
		{
			MLog.d( "Got no tunnels onPlayerCommandPreprocess" );
			return;
		}

		// Do not dispatch ConsoleTunnel-commands
		if( event.getMessage().startsWith( "/tunnel" ) )
		{
			MLog.d( "Skipping tunnel command from '" + event.getPlayer().getName() + "'" );
			return;
		}

		// The RunAs-Functionality
		int cnt = 0;

		MResult res = null;
		mytunnels = tm.getTunnelsBySender( event.getPlayer() );
		for( Tunnel t : mytunnels )
		{
			if( ( res = t.dispatch( event.getMessage() ) ) == MResult.RES_SUCCESS )
				cnt++;
			else
				MLog.d( "Got error at onPlayerCommandPreprocess.dispatch: " + String.valueOf( res ) );
		}

		if( cnt > 0 )
			event.setCancelled( true );

	}

	@EventHandler( priority = EventPriority.LOW )
	public void onPlayerJoin( PlayerJoinEvent event )
	{
		MLog.d( "Counting tunnels on player-login [Player=" + event.getPlayer().getName() + "]" );

		tm.handlePlayerJoin( event.getPlayer() );

		List<Tunnel> mytunnels = tm.getTunnelsBySender( event.getPlayer() );
		if( mytunnels != null && !mytunnels.isEmpty() )
		{
			event.getPlayer().sendMessage( ChatColor.DARK_GRAY + "[" + ct.getDescription().getName() + "] "
				+ ChatColor.GRAY + "There are " + ChatColor.DARK_AQUA + mytunnels.size() + ChatColor.GRAY
				+ " tunnel(s). (open: " + ChatColor.GOLD + tm.numOpenTunnels( mytunnels ) + ChatColor.GRAY + ")" );
		}
	}
}
