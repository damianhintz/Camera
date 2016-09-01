/*
 * JFrameCS.java
 *
 * Created on 1 maj 2007, 09:33
 */

package cs;

import java.net.*;
import java.lang.Object;
import javax.swing.*;
import javax.media.*;
import javax.media.protocol.*;
import javax.media.format.*;
import javax.media.rtp.*;
import javax.media.control.*;
import javax.media.rtp.event.*;

/**
 *
 * @author  Damian Hintz
 */
public class JFrameCS extends javax.swing.JFrame {
    
    private CameraServer cs;
    private String address = "localhost";
    private int port = 1234;
    private JFrame cf = null;
    private Player player;
    private DataSource ds;
    /**
     * Creates new form JFrameCS
     */
    
    public JFrameCS() 
    {
        setTitle("cs - camera server");
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jToolBar1 = new javax.swing.JToolBar();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldHost = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldPort = new javax.swing.JTextField();
        jButtonSS = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jLabelStatus = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jCheckBox1 = new javax.swing.JCheckBox();
        jSpinner1 = new javax.swing.JSpinner();
        jLabelKamera = new javax.swing.JLabel();
        jLabelKlient = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListClient = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        jToolBar1.setFloatable(false);
        jLabel2.setText("Address: ");
        jToolBar1.add(jLabel2);

        jTextFieldHost.setText("192.168.1.101");
        jToolBar1.add(jTextFieldHost);

        jLabel1.setText(" Port: ");
        jToolBar1.add(jLabel1);

        jTextFieldPort.setText("4321");
        jTextFieldPort.setPreferredSize(new java.awt.Dimension(10, 20));
        jTextFieldPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPortActionPerformed(evt);
            }
        });

        jToolBar1.add(jTextFieldPort);

        jButtonSS.setText("Start");
        jButtonSS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSSActionPerformed(evt);
            }
        });

        jToolBar1.add(jButtonSS);

        jToolBar2.setFloatable(false);
        jLabelStatus.setText("Status: idle ");
        jToolBar2.add(jLabelStatus);

        jToolBar2.add(jSeparator1);

        jCheckBox1.setText("Show camera");
        jCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBox1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox1StateChanged(evt);
            }
        });
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jToolBar2.add(jCheckBox1);

        jToolBar2.add(jSpinner1);

        jLabelKamera.setText(" Kamery: 0 ");
        jToolBar2.add(jLabelKamera);

        jLabelKlient.setText(" Klienci: 0 ");
        jToolBar2.add(jLabelKlient);

        jListClient.setToolTipText("Clients");
        jScrollPane1.setViewportView(jListClient);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
// TODO add your handling code here:
        if(cs == null)
            return;
        if(jCheckBox1.isSelected())
        {
            if(cf != null)
            {
                cf.dispose();
                cf = null;
            }
            cf = new JFrame();
            String index = jSpinner1.getValue().toString();
            cf.setTitle("Camera " + index);
            
            int i = Integer.parseInt(index);
            
            int j = cs.getCameras().size();
            if(j < 1 || i < 0 || i >= j)
                return;
            Camera cam = cs.getCameras().elementAt(Integer.parseInt(index));
            ds = cam.getDataSource();
            player = null;
            try
            {
                player = Manager.createRealizedPlayer(ds);
            }
            catch(Exception e)
            {
                System.out.println(e.toString());
                return;
            }
            cf.add("Center", player.getVisualComponent());
            cf.add("South", player.getControlPanelComponent());
            cf.pack();
            cf.setVisible((true));
        }else
        {
            cleanFrame();
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed
    
    private void cleanFrame()
    {
        jCheckBox1.setSelected(false);
        if(player != null)
        {
            player.stop();
            player = null;
        }
        if(ds != null)
        {
            try
            {
                ds.stop();
                ds = null;
            }catch(Exception e)
            {
                System.out.println(e.toString());
            }
        }
        if(cf != null)
        {
            cf.dispose();
            cf = null;
        }
    }
    
    private void jCheckBox1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox1StateChanged
// TODO add your handling code here:
        
    }//GEN-LAST:event_jCheckBox1StateChanged

    private void jTextFieldPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPortActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPortActionPerformed

    public void refreshFrame()
    {
        DefaultListModel model = new DefaultListModel();
        for(ClientSession c : cs.getClients())
        {
            model.addElement(c.toString());
        }
        
        jListClient.setModel(model);
        jLabelKamera.setText(" Kamery: " + cs.getCameras().size());
        jLabelKlient.setText(" Klienci: " + model.getSize());    
        
        this.invalidate();
    }
    
    private boolean startServer(String address, int port)
    {
        jLabelStatus.setText("Status: starting...");
        cs = new CameraServer(address, port, this);
        if(cs != null && cs.getEnabled() == true)
        {
            jLabelKamera.setText(" Kamery: " + cs.getCameras().size());
            jLabelStatus.setText("Status: enabled");
            jButtonSS.setText("Stop");
        }else
        {
            jLabelStatus.setText("Status: error");
        }
        return true;
    }
    
    private void stopServer()
    {
        cs.stopServer();
        jListClient.setModel(new DefaultListModel());
     
        jLabelKamera.setText(" Kamery: 0 ");
        jLabelKlient.setText(" Klienci: 0 ");
        cs = null;
        jLabelStatus.setText("Status: idle");        
    }
    
    private void jButtonSSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSSActionPerformed
        
        if(cs == null || cs.getEnabled() == false)
        {
            address = jTextFieldHost.getText();
            try
            {
                port = Integer.parseInt(jTextFieldPort.getText());
            }catch(NumberFormatException e)
            {
                port = 0;
            }
        
            if(address != "" && port > 1024)
            {
                
                startServer(address, port);
            }else
            {
                setTitle("CS binding error");
            }
        }else
        {
            cleanFrame();
            stopServer();
            jButtonSS.setText("Start");
        }
    }//GEN-LAST:event_jButtonSSActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrameCS().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonSS;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelKamera;
    private javax.swing.JLabel jLabelKlient;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JList jListClient;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextField jTextFieldHost;
    private javax.swing.JTextField jTextFieldPort;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    // End of variables declaration//GEN-END:variables
    
}