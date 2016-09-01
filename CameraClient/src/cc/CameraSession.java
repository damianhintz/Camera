/*
 * CameraSession.java
 *
 * Created on 4 maj 2007, 15:15
 */

package cc;

import javax.media.*;
import javax.media.rtp.*;
import javax.media.rtp.event.*;
import javax.media.protocol.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

/**
 *
 * @author Damian Hintz
 */
public class CameraSession implements ReceiveStreamListener 
{
    
    private CameraClient cameraClient;
    
    private int cameraIndex;
    public int getIndex()
    {
        return this.cameraIndex;
    }
    
    private Player player;
    private RTPManager rtpManager;
    private SessionAddress clientSessionAddress;
    private SessionAddress serverSessionAddress;
    private int serverSessionPort = 1234;
    private int clientSessionPort = 3456;
    
    /** Creates a new instance of CameraSession */
    public CameraSession(CameraClient cameraClient) 
    {
        try
        {
            this.cameraClient = cameraClient;
            int idSession = cameraClient.getId();
            
            InetAddress serverIP = InetAddress.getByName(cameraClient.getAddress());
            serverSessionAddress = new SessionAddress(serverIP, serverSessionPort + 2*idSession);
            InetAddress clientIP = InetAddress.getLocalHost();
            clientSessionAddress = new SessionAddress(clientIP, clientSessionPort + 2*idSession);

            rtpManager = RTPManager.newInstance();
            rtpManager.addReceiveStreamListener(this);
            rtpManager.initialize(clientSessionAddress);
            rtpManager.addTarget(serverSessionAddress);
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }
    
    public void stopSession()
    {
        if(rtpManager != null)
        {
            rtpManager.dispose();
            rtpManager = null;
        }
        if(player != null)
        {
            player.close();
            player = null;
        }
    }

    private Player createPlayer(DataSource ds)
    {
        player = null;
        try
        {
            player = Manager.createRealizedPlayer(ds);
        }
        catch(Exception e)
        {
            System.out.println("createPlayer error:");
            System.out.println(e.toString());
        }
        return player;
    }
    
    public void update(ReceiveStreamEvent e)
    {
        if(e instanceof ByeEvent)
        {
            System.out.println("ByeEvent.");
            cameraClient.getFrame().removePlayer();
            this.stopSession();
        }else
        if(e instanceof NewReceiveStreamEvent)
        {
            System.out.println("NewReceiveStreamEvent.");
            ReceiveStream rs = e.getReceiveStream();
            DataSource ds = rs.getDataSource();
            this.player = createPlayer(ds);
            if(this.player != null)
            {
                cameraClient.getFrame().addPlayer(this.player);
            }
        }
    }
}
