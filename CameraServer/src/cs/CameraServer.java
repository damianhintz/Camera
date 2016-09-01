/*
 * CameraServer.java
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
public class CameraServer implements Runnable 
{
     
    private Vector<Camera> cameras;
    public Vector<Camera> getCameras()
    {
        return this.cameras;
    }
    private Vector<ClientSession> clientSessions;
    public Vector<ClientSession> getClients()
    {
        return this.clientSessions;
    }
    
    private ServerSocket serverSocket;
    
    private SessionAddress serverSessionAddress;
    public SessionAddress getSessionAddress()
    {
        return this.serverSessionAddress;
    }
    
    private int serverSessionPort = 1234;
    
    private int port = 4321;
    private String address = "192.168.3.1";
    public String getAddress()
    {
        return this.address;
    }
    
    private Thread thread = null;
    
    private boolean enabled = false;
    public boolean getEnabled()
    {
        return this.enabled;
    }
    
    private JFrameCS serverFrame;
    public JFrameCS getFrame()
    {
        return this.serverFrame;
    }
    
    /** Creates a new instance of CameraServer */
    public CameraServer(String address, int port, JFrameCS serverFrame) {
        this.address = address;
        this.port = port;
        
        this.serverFrame = serverFrame;
        
        thread = new Thread(this);
        
        this.enabled = this.startServer();
        
    }
    
    /** Start server */
    private boolean startServer()
    {
        try
        {
            System.out.println("Initializing server...");
            InetAddress serverIP = InetAddress.getByName(address);
            serverSessionAddress = new SessionAddress(serverIP, serverSessionPort);
        
            serverSocket = new ServerSocket(port);
            
            clientSessions = new Vector<ClientSession>();

            System.out.println("Connecting cameras to server...");
            VideoFormat format = new RGBFormat();
            Vector<CaptureDeviceInfo> devices = CaptureDeviceManager.getDeviceList(format);
            
            System.out.println("" + devices.size() + " camera(s) found.");
            
            cameras = new Vector<Camera>();
            for(CaptureDeviceInfo deviceInfo : devices)
            {
            
                MediaLocator cameraLocator = deviceInfo.getLocator();
            
                Camera camera = new Camera(cameraLocator);
                if(camera.getEnabled())
                {
                    System.out.println("Camera enabled: " + cameraLocator.toString());
                    this.cameras.add(camera);
                }else
                {
                    System.out.println("Camera not enabled: " + cameraLocator.toExternalForm());
                }
            }
            System.out.println(cameras.size() + " camera(s) connected.");
            System.out.println("Starting server thread...");
            thread.start();
            System.out.println("Server started.");
        }
        catch(Exception e)
        {
            System.out.println("Exception in startServer:");
            System.out.println(e.toString());
            return false;
        }
        return true;
    }
    
    /** Server thread started */
    public void run()
    {
        Socket clientSocket = null;
        SocketAddress clientSocketAddress;
        SessionAddress clientSessionAddress;
        ClientSession clientSession = null;

        while(true)
        {
            if(serverSocket == null)
                break;
            try
            {
                System.out.println("Waiting for a client...");
                clientSocket = serverSocket.accept();
                
                System.out.println("Creating client session ... " + clientSocket.toString());
                clientSession = createClientSession(clientSocket);
                clientSessions.add(clientSession);
                System.out.println("Client session created.");
                
                for(ClientSession c : clientSessions)
                {
                    if(c.getEnabled() == false)
                    {
                        clientSessions.remove(c);
                    }
                }
                
                serverFrame.refreshFrame();
            }
            catch(SocketException e)
            {
                //Server stoped
                break;
            }
            catch(Exception e)
            {
                System.out.println("Exception in run:");
                System.out.println(e.toString());
            }
        }
    }
    
    private int createSessionId()
    {
        return this.getClients().size();
    }
    
    private ClientSession createClientSession(Socket clientSocket) throws InvalidSessionAddressException, UnknownHostException, IOException
    {
        int sessionId = createSessionId();
        ClientSession clientSession = null;
        clientSession = new ClientSession(clientSocket, this, sessionId);
        
        return clientSession;
    }

    public void stopServer()
    {
        System.out.println("Stopping server...");
        try
        {
            System.out.println("Closing socket...");
            serverSocket.close();
            
            System.out.println("Closing sessions...");
            for(ClientSession session: clientSessions)
            {
                    session.closeSession();
            }
            
            System.out.println("Disposing cameras...");
            for(Camera camera : cameras)
            {
                    camera.dispose();
            }
        }catch(Exception e)
        {
            System.out.println("Exception in serverStop:");
            System.out.println(e.toString());
        }
        finally
        {
            serverSocket = null;
            cameras = new Vector<Camera>();
            clientSessions = new Vector<ClientSession>();
        }
        System.out.println("Server stoped.");
    }
}
