/*
 * CameraClient.java
 *
 * Created on 3 maj 2007, 20:36
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
public class CameraClient {
    
    private int sessionId;
    public int getId()
    {
        return this.sessionId;
    
    }
    private CameraSession cameraSession;
    
    private String address = "";
    public String getAddress()
    {
        return this.address;
    }
    
    private int port = 4321;
    
    private JFrameCC clientFrame;
    public JFrameCC getFrame()
    {
        return this.clientFrame;
    }
    
    private Socket socket = null;
    private BufferedReader serverStream = null;
    private PrintStream clientStream = null;

    private boolean enabled = false;
    public boolean getEnabled()
    {
        return this.enabled;
    }
    
    /** Creates a new instance of CameraClient */
    public CameraClient(String address, int port, JFrameCC clientFrame)
    {
        this.address = address;
        this.port = port;
        this.clientFrame = clientFrame;
        
        this.enabled = this.connectToServer();
    }
    
    private String sendLine(String clientLine)
    {
        String serverLine;

        try
        {
                clientStream.println(clientLine);
                clientStream.flush();

                serverLine = serverStream.readLine();
                return serverLine;
        }			
        catch(Exception e)
        {
            System.out.println("sendLine error:");
            System.out.println(e.toString());
        }
        return "";
    }

    private boolean connectToServer()
    {
        socket = null;
        serverStream = null;
        clientStream = null;
        String serverLine = null;

        try
        {
                socket = new Socket(address, port);
                serverStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                clientStream = new PrintStream(socket.getOutputStream());
                serverLine = sendLine("connect server");
                sessionId = Integer.parseInt(serverLine);
                
                return true;
        }catch(Exception e)
        {
            System.out.println("Server connection error:");
            System.out.println(e.toString());
            return false;
        }
    }

    public void disconnectFromServer()
    {
        disconnectFromCamera();
        
        sendLine("disconnect server");
        try
        {
            if(serverStream != null) serverStream.close();
            if(clientStream != null) clientStream.close();
            if(socket != null) socket.close();
        }catch(Exception e)
        {
            System.out.println("Disconnection error:");
            System.out.println(e.toString());
        }finally
        {
            serverStream = null;
            clientStream = null;
            socket = null;
        }
    }
    
    private CameraSession createCameraSession(int sessionId)
    {
        CameraSession cameraSession = new CameraSession(this);
        return cameraSession;
    }
    
    public void connectToCamera(int cameraIndex)
    {
        String line = sendLine("connect camera " + cameraIndex);
        if(line.startsWith("not"))
        {
            this.cameraSession = null;
        }else
        {
            this.cameraSession = createCameraSession(sessionId);
        }
    }

    public void disconnectFromCamera()
    {
        if(cameraSession != null)
        {
            sendLine("disconnect camera " + cameraSession.getIndex());
            cameraSession.stopSession();
            cameraSession = null;
        }
    }

    public int countCameras()
    {
        String serverLine = sendLine("count cameras");
        if(serverLine != "")
        {
                return Integer.parseInt(serverLine);
        }else
        {
                return 0;
        }
    }
}

