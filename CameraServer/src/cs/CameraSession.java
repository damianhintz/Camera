/*
 * CameraSession.java
 *
 * Created on 3 maj 2007, 16:35
 */

package cs;

import javax.media.*;
import javax.media.protocol.*;
import javax.media.format.*;
import javax.media.rtp.*;
import javax.media.control.*;
import javax.media.rtp.event.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 *
 * @author Damian Hintz
 */
public class CameraSession 
{
    
    private ClientSession clientSession;
    private Camera camera;
    
    private int cameraIndex;
    public int getIndex()
    {
        return cameraIndex;
    }
    private boolean enabled;
    public boolean getEnabled()
    {
        return this.enabled;
    }
    
    private RTPManager rtpManager;
    private SendStream sendStream;
    private SessionAddress serverSessionAddress;
    private SessionAddress clientSessionAddress;
    private int serverSessionPort = 1234;
    private int clientSessionPort = 3456;
    
    /** Creates a new instance of CameraSession */
    public CameraSession(ClientSession clientSession, int cameraIndex) 
    {
        this.enabled = false;
        try
        {
            this.clientSession = clientSession;
            this.cameraIndex = cameraIndex;
            this.camera = clientSession.getServer().getCameras().get(cameraIndex);
            
            InetAddress serverIP = InetAddress.getByName(clientSession.getServer().getAddress());
            this.serverSessionAddress = new SessionAddress(serverIP, serverSessionPort + 2*clientSession.getId());
            //System.out.println("Server session address.");
            
            InetAddress clientIP = clientSession.getSocket().getInetAddress();
            this.clientSessionAddress = new SessionAddress(clientIP, clientSessionPort + 2*clientSession.getId());
            //System.out.println("Client session address.");
            
            this.rtpManager = RTPManager.newInstance();
            this.rtpManager.initialize(serverSessionAddress);
            this.rtpManager.addTarget(clientSessionAddress);
            
            this.sendStream = rtpManager.createSendStream(camera.getClonedSource(), 0);
            this.sendStream.start();
            camera.start();
            this.enabled = true;
        }catch(Exception e)
        {
            System.out.println("CameraSession start error:");
            System.out.println(e.toString());
        }
    }
    
    public void closeSession()
    {
        if(sendStream != null)
        {
                sendStream.close();
                sendStream = null;
        }
        if(rtpManager != null)
        {
                rtpManager.dispose();
                rtpManager = null;
        }
    }
}
