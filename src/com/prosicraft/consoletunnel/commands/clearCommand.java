/*
 * Clear Tunnels Command
 */
package com.prosicraft.consoletunnel.commands;

import com.prosicraft.consoletunnel.bemyadvice.extendedCommand.commandResult;
import com.prosicraft.consoletunnel.bemyadvice.extendedCommand.extendedCommand;
import com.prosicraft.consoletunnel.ConsoleTunnel;
import com.prosicraft.consoletunnel.Tunnel;
import com.prosicraft.consoletunnel.TunnelManager;
import com.prosicraft.consoletunnel.mighty.util.MConfiguration;
import com.prosicraft.consoletunnel.mighty.util.MLog;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class clearCommand extends extendedCommand
{

	public clearCommand( ConsoleTunnel handle, String[] args )
	{
		super( handle, args );
	}

	public commandResult run( Player p, MConfiguration config, TunnelManager tm )
	{

		try
		{

			// Todo: implement Permissions!

			// /tunnel clear
			if( this.hasArgs( 1 ) && this.getArg( 0 ).equalsIgnoreCase( "clear" ) )
			{

				this.ev( p );

				if( tm == null || tm.isEmpty() )
					return err( p, "There are no tunnels yet." );

				int n = 0;

				for( Tunnel t : tm )
				{
					t.delete();
					n++;
				}

				config.save();
				tm.clear();
				config.load();

				return suc( p, "Cleared " + n + " tunnels successfully." );

			}
			else
				return skip();

		}
		catch( Exception ex )
		{
			MLog.e( "(ClearCmd) " + ex.getMessage() );
			ex.printStackTrace( System.out );
			return err( p, "Failed to execute command." );
		}

	}
}
