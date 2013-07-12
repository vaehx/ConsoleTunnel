/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.consoletunnel;

import com.prosicraft.consoletunnel.mighty.util.MLog;
import com.prosicraft.consoletunnel.mighty.util.MResult;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 * @author passi
 */
public class C2CHandler extends Handler {    
    
    private TunnelManager tm = null;
    
    public C2CHandler (TunnelManager _tm) {
        this.tm = _tm;        
    }       

    @Override
    public void publish(LogRecord lr) {
        //MLog.d("Inside C2CHandler.publish (LogRecord:" + String.valueOf(lr) + ")");
        
        if ( tm != null && !lr.getMessage().equalsIgnoreCase("") ) {
            
            // get the tunnels
            List<Tunnel> mytunnels = tm.getTunnelsByTarget("console");
                        
            if ( mytunnels != null && !mytunnels.isEmpty() ) {
            
                MResult res = null;
                
                for ( Tunnel t : mytunnels ) {
                                        
                    // Now try to send
                    if ( ( res = t.send( lr.getMessage() )) != MResult.RES_SUCCESS ) {
                        if ( res != MResult.RES_NOTINIT) {
                            try {
                                MLog.d("Got error in C2CStream.publish.t.send: " + String.valueOf(res));
                            } catch (Exception ex) { // can't send error, because whatever :D                            
                            }                            
                        }
                    }
                    
                }
            }
            
        }
    }                           

    @Override
    public void close() throws SecurityException {        
    }

    @Override
    public void flush() {        
    }        
}        
