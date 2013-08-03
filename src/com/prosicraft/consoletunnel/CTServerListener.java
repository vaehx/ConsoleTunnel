/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.consoletunnel;

import com.prosicraft.consoletunnel.mighty.util.MLog;
import com.prosicraft.consoletunnel.mighty.util.MResult;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.StreamHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

/**
 *
 * @author passi
 */
public class CTServerListener implements Listener
{

	private ConsoleTunnel handle;
	private Handler sout = null;

	public CTServerListener( ConsoleTunnel _handle )
	{
		handle = _handle;
		sout = new StreamHandler();
	}

	@EventHandler( priority = EventPriority.NORMAL )
	public void onServerCommand( ServerCommandEvent event )
	{

		if( handle == null )
			return;

		TunnelManager tm;
		if( ( tm = handle.getTunnelManager() ) == null )
			return;

		List<Tunnel> mytunnels = tm.getTunnelsByTarget( "console" );
		for( Tunnel t : mytunnels )
		{
			if( t.sendNotification( event.getCommand() ) != MResult.RES_SUCCESS )
			{
				MLog.d( "Error sending command to tunnel to '" + t.getExecutorName() + "'" );
			}
		}

	}
}
