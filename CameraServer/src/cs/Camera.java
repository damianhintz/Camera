/*
 * Camera.java
 *
 * Created on 1 maj 2007, 12:01
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
public class Camera 
{

    private MediaLocator cameraLocator;
    private DataSource dataSource;
    private ContentDescriptor contentDescriptor;
    private Format format;
    private ProcessorModel processorModel;
    private Processor processor;
    private DataSource dataOutput;
    private DataSource cloneableSource;
    private DataSource clonedSource;
    
    private boolean enabled = false;
    public boolean getEnabled()
    {
        return this.enabled;
    }
    
    /** Creates a new instance of Camera */
    public Camera(MediaLocator cameraLocator) 
    {
        this.cameraLocator = cameraLocator;
        this.enabled = init();
    }
    
    public String toString()
    {
        return cameraLocator.toString();
    }
    
    private boolean init()
    {
        try
        {
            dataSource = Manager.createDataSource(cameraLocator);
            format = new VideoFormat(VideoFormat.H263_RTP);
            contentDescriptor = new ContentDescriptor(ContentDescriptor.RAW_RTP);
            processorModel = new ProcessorModel(dataSource, new Format[]{format}, contentDescriptor);
            processor = Manager.createRealizedProcessor(processorModel);

            dataOutput = processor.getDataOutput();
            cloneableSource = Manager.createCloneableDataSource(dataOutput);
            
            return true;
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            return false;
        }
    }

    public void dispose()
    {
        try
        {
            dataOutput.stop();
            processor.stop();
            processor.deallocate();
            dataSource.disconnect();
        }catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }

    public void start()
    {
        processor.start();
    }

    public DataSource getDataSource()
    {
        return dataSource;
    }
    
    public DataSource getClonedSource()
    {
        //return ((SourceCloneable)cloneableSource).createClone();
        return dataOutput;
    }

}
