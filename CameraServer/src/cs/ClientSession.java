/*
 * ClientSession.java
 *
 * Created on 1 maj 2007, 11:56
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
public class ClientSession implements Runnable 
{
    
    private CameraSession cameraSession;
    
    private CameraServer cameraServer;
    public CameraServer getServer()
    {
        return this.cameraServer;
    }
    
    private Thread thread;
    
    private Socket clientSocket;
    public Socket getSocket()
    {
        return this.clientSocket;
    }
    
    private BufferedReader clientStream = null;
    private PrintStream serverStream = null;

    private int sessionId;
    public int getId()
    {
        return sessionId;
    }
    
    private String address = "127.0.0.1";
    private int port = 0;
    
    private boolean enabled = false;
    public boolean getEnabled()
    {
        return this.enabled;
    }
    
    /** Creates a new instance of ClientSession */
    public ClientSession(Socket clientSocket, CameraServer cameraServer, int sessionId)
    {
        this.enabled = false;
        
        this.address = clientSocket.getInetAddress().getHostAddress();
        this.port = clientSocket.getPort();
        
        this.cameraServer = cameraServer;
        this.clientSocket = clientSocket;
        this.sessionId = sessionId;
        
        startSession();
        
        this.enabled = true;
    }
    
    public String toString()
    {
        String state = " State: connected ";
        if(getEnabled() == false)
            state = " State: disconnected ";
        String camera = " Camera: not connected ";
        if(cameraSession != null)
        {
            camera = " Camera: " + cameraSession.getIndex();
        }
        return address + ":" + port + state + camera;
    }
    
    private void startSession()
    {
        thread = new Thread(this);
        thread.start();
    }

    public void run()
    {
        String clientLine, serverLine;

        try
        {
            clientStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            serverStream = new PrintStream(new BufferedOutputStream(clientSocket.getOutputStream()), false);

            while((clientLine = clientStream.readLine()) != null)
            {
                clientLine = clientLine.trim().toLowerCase();
                if(clientLine.startsWith("connect server"))
                {
                    connectToServer();
                }else
                if(clientLine.startsWith("count cameras"))
                {
                    countCameras();
                }else
                if(clientLine.startsWith("connect camera"))
                {
                    int cameraIndex = Integer.parseInt(clientLine.replace("connect camera ", "").trim());
                    connectToCamera(cameraIndex);
                    this.cameraServer.getFrame().refreshFrame();
                }else
                if(clientLine.startsWith("disconnect camera"))
                {
                    int indexOld;
                    //indexOld = Integer.parseInt(clientLine.replace("disconnect camera", "").trim());
                    disconnectFromCamera();
                    this.cameraServer.getFrame().refreshFrame();
                }else
                if(clientLine.startsWith("disconnect server"))
                {
                    break;
                }else
                {
                    serverStream.println("wrong command");
                    serverStream.flush();
                }
            }
            disconnectFromServer();
            if(serverStream != null) serverStream.close();
            if(clientStream != null) clientStream.close();
            if(clientSocket != null) clientSocket.close();
        }/*
        catch(InvalidSessionAddressException e)
        {
                System.err.println(e.toString());
        }*/
        /*catch(UnsupportedFormatException e)
        {
            
        }*/
        catch(SocketException e)
        {
            //session closed
        }catch(Exception e)
        {
            System.out.println("ClientSession error:");
            System.out.println(e.toString());
        }finally
        {    
            enabled = false;
            serverStream = null;
            clientStream = null;
            clientSocket = null;
        }
        this.cameraServer.getFrame().refreshFrame();
    }
    
    private void connectToServer()
    {
        serverStream.println(String.valueOf(sessionId));
        serverStream.flush();
    }
    
    private void disconnectFromServer()
    {
        this.enabled = false;
        //serverStream.println("disconnected from server");
        //serverStream.flush();
        this.closeSession();
    }
    
    private void countCameras()
    {
        serverStream.println(cameraServer.getCameras().size());
        serverStream.flush();
    }
    
    private void connectToCamera(int cameraIndex)
    {
        this.cameraSession = createCameraSession(cameraIndex);
        if(cameraSession != null)
        {
            serverStream.println("connected to camera");
            serverStream.flush();
        }else
        {
            serverStream.println("not connected to camera");
            serverStream.flush();
        }
    }
    
    private CameraSession createCameraSession(int cameraIndex)
    {
        cameraSession = new CameraSession(this, cameraIndex);
        if(cameraSession.getEnabled() == false)
        {
            cameraSession.closeSession();
            return null;
        }else
            return cameraSession;
    }
    
    private void destroyCameraSession()
    {
        if(cameraSession != null)
        {
            this.cameraSession.closeSession();
            this.cameraSession = null;
        }
    }
    
    private void disconnectFromCamera()
    {
        destroyCameraSession();
        serverStream.println("disconnected from camera");
        serverStream.flush();
    }
        
    public void closeSession()
    {
        try
        {
            if(cameraSession != null) cameraSession.closeSession();
            if(serverStream != null) serverStream.close();
            if(clientStream != null) clientStream.close();
            if(clientSocket != null) clientSocket.close();
        }catch(Exception e)
        {
            System.out.println("Session close error:");
            System.out.println(e.toString());
        }finally
        {
            cameraSession = null;
            serverStream = null;
            clientStream = null;
            clientSocket = null;
            enabled = false;
        }
    }
}
