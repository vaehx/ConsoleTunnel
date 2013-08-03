/*
 * Open Tunnel Command
 */
package com.prosicraft.consoletunnel.commands;

import com.prosicraft.consoletunnel.bemyadvice.extendedCommand.commandResult;
import com.prosicraft.consoletunnel.bemyadvice.extendedCommand.extendedCommand;
import com.prosicraft.consoletunnel.ConsoleTunnel;
import com.prosicraft.consoletunnel.Tunnel;
import com.prosicraft.consoletunnel.TunnelManager;
import com.prosicraft.consoletunnel.mighty.util.MConfiguration;
import com.prosicraft.consoletunnel.mighty.util.MLog;
import com.prosicraft.consoletunnel.mighty.util.MResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class openCommand extends extendedCommand
{

	public openCommand( ConsoleTunnel handle, String[] args )
	{
		super( handle, args );
	}

	public commandResult run( Player p, MConfiguration config, TunnelManager tm )
	{

		try
		{

			// Todo: implement Permissions!

			// /tunnel open <TARGET> [flags...]
			if( this.hasArgs( 2 ) && this.getArg( 0 ).equalsIgnoreCase( "open" ) || this.getArg( 0 ).equalsIgnoreCase( "o" ) )
			{

				this.ev( p );

				Tunnel t;
				boolean isConsole = this.getArg( 1 ).equalsIgnoreCase( "console" );

				// Check if this tunnel already exists... if yes, try to reopen
				if( ( t = tm.getTunnelByMembers( p, getArg( 1 ) ) ) != null )
				{
					if( t.isOpen() )
						return err( p, "A tunnel to this person/console is already open." );

					MResult resb;
					if( ( resb = t.open() ) == MResult.RES_SUCCESS )
					{
						MLog.d( "Tunnel already existed but was closed. Reopened it." );
						return suc( p, "Successfully reopened tunnel [executor=" + p.getName() + ", target=" + this.getArg( 1 ) + "]" );
					}
					else
					{
						MLog.d( "Got error: " + String.valueOf( resb ) );
						return err( p, "Failed to reopen tunnel with " + this.getArg( 1 ) );
					}
				}

				// Try to check if specified target actually exists
				if( this.getParent().getServer().getPlayer( this.getArg( 1 ) ) == null && !isConsole )
				{
					MLog.d( "Got target (" + getArg( 1 ) + ") is not an online player." );
					return err( p, "Target not found." );
				}
				else if( !isConsole )
				{
					MLog.d( "Got target (" + getArg( 1 ) + ") is not the console." );
					return err( p, "Target not found." );
				}

				tm.addTunnel( p.getName(), this.getArg( 1 ), false );
				return suc( p, "Opened Tunnel with " + getArg( 1 ) + " successfully." );

			}
			else if( this.getArgs().length > 2 && this.getArg( 0 ).equalsIgnoreCase( "open" ) )
			{

				this.ev( p );

				boolean useRunas = false;
				boolean isConsole = this.getArg( 1 ).equalsIgnoreCase( "console" );

				List<String> flags = new ArrayList<String>();
				Collections.addAll( flags, getArgs() );
				flags.remove( "open" );
				flags.remove( getArg( 1 ) );
				for( String s1 : flags )
				{
					MLog.d( "Scanning arg: " + s1 + "(" + flags.indexOf( s1 ) + ")" );
					if( s1.contains( "runas" ) )
						useRunas = true;
				}

				Tunnel t;

				if( ( t = tm.getTunnelByMembers( p, getArg( 1 ) ) ) != null )
				{
					if( t.isOpen() )
						return err( p, "This tunnel is already open." );

					MResult resb;
					t.useRunas = useRunas;
					if( ( resb = t.open() ) == MResult.RES_SUCCESS )
					{
						MLog.d( "Tunnel already existed but was closed. Reopened it." );
						return suc( p, "Successfully reopened tunnel [executor=" + p.getName() + ", target=" + this.getArg( 1 ) + "]" );
					}
					else
					{
						MLog.d( "Got error: " + String.valueOf( resb ) );
						return err( p, "Failed to reopen tunnel with " + this.getArg( 1 ) );
					}
				}

				if( this.getParent().getServer().getPlayer( this.getArg( 1 ) ) == null && !isConsole )
				{
					MLog.d( "Got target (" + getArg( 1 ) + ") is not an online player." );
					return err( p, "Target not found." );
				}
				else if( !isConsole )
				{
					MLog.d( "Got target (" + getArg( 1 ) + ") is not the console." );
					return err( p, "Target not found." );
				}

				tm.addTunnel( p.getName(), getArg( 1 ), useRunas );

				return suc( p, "Opened Tunnel with " + getArg( 1 ) + " successfully." );

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
