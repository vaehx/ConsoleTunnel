/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.consoletunnel;

import com.prosicraft.consoletunnel.mighty.util.MLog;
import com.prosicraft.consoletunnel.mighty.util.MResult;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author passi
 */
public class C2CHandler extends ConsoleHandler
{

	private TunnelManager tm = null;

	public C2CHandler( TunnelManager _tm )
	{
		super();
		this.tm = _tm;
		this.setLevel( Level.ALL );
	}

	@Override
	public void publish( LogRecord lr )
	{
		if( tm != null && !lr.getMessage().equalsIgnoreCase( "" ) )
		{
			// get the tunnels, where target is console
			List<Tunnel> mytunnels = tm.getTunnelsByTarget( "console" );

			if( mytunnels != null && !mytunnels.isEmpty() )
			{
				MResult res;
				for( Tunnel t : mytunnels )
				{
					// Now try to send
					if( ( res = t.sendNotification( lr.getMessage() ) ) != MResult.RES_SUCCESS )
					{
						if( res != MResult.RES_NOTINIT )
						{
							try
							{
								MLog.d( "Got error in C2CStream.publish.t.send: " + String.valueOf( res ) );
							}
							catch( Exception ex )
							{ // can't send error, because whatever :D
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void close() throws SecurityException
	{
	}

	@Override
	public void flush()
	{
	}
}
