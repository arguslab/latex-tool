/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package latexGlobalGraphics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import latex.Svn;

/**
 *
 * @author Dwight
 */
public class Collis extends javax.swing.JDialog {

    /**
     * Creates new form Collis
     */
    
    private Process _process;
    private String _posOut;
    private Svn _svnCode;
    private java.awt.Frame _parent;
    
    public Collis(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        _parent = parent;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttPost = new javax.swing.JButton();
        buttPush = new javax.swing.JButton();
        buttPop = new javax.swing.JButton();
        buttDisplay = new javax.swing.JButton();
        labelName = new javax.swing.JLabel();
        buttResolve = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        buttPost.setText("POST - PONE");
        buttPost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttPostActionPerformed(evt);
            }
        });

        buttPush.setText("MINE VERSION");
        buttPush.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttPushActionPerformed(evt);
            }
        });

        buttPop.setText("SVN VERSION");
        buttPop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttPopActionPerformed(evt);
            }
        });

        buttDisplay.setText("DISPLAY CHANGES");
        buttDisplay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttDisplayActionPerformed(evt);
            }
        });

        labelName.setText("File Name");

        buttResolve.setText("RESOLVE/EDIT");
        buttResolve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttResolveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buttDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttPost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttPush, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttPop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttResolve, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addComponent(buttPost)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttResolve)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttPush)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttPop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttDisplay)
                .addGap(18, 18, 18))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttDisplayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttDisplayActionPerformed
        sendOptionToTerminal("dc");
        _svnCode.setContFlag(true);
        Output tempO = new Output();
        if(_posOut!=null && !_posOut.equals("")){
            tempO.AddText(_posOut);
            tempO.setLocation(this.getLocation());
            tempO.setVisible(true);
            tempO.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        }
        
        //read them
        this.dispose();
    }//GEN-LAST:event_buttDisplayActionPerformed

    private void buttPostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttPostActionPerformed
    sendOptionToTerminal("p");
    _svnCode.setContFlag(false);
        this.dispose();
    }//GEN-LAST:event_buttPostActionPerformed

    private void buttPushActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttPushActionPerformed
    sendOptionToTerminal("mc");
    _svnCode.setContFlag(false);
        _svnCode.CreateOrUpdateBibs();
        this.dispose();
    }//GEN-LAST:event_buttPushActionPerformed

    private void buttPopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttPopActionPerformed
    sendOptionToTerminal("tc");
    _svnCode.setContFlag(false);
       _svnCode.CreateOrUpdateBibs();
        this.dispose();
        
        
    }//GEN-LAST:event_buttPopActionPerformed

    private void buttResolveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttResolveActionPerformed
     ResolveCollis rs = new ResolveCollis(_parent, true);
     rs.setSVN(_svnCode);
     rs.setAlwaysOnTop(true);
     rs.setVisible(true);
     sendOptionToTerminal("dc");
     _svnCode.setContFlag(true);
     this.dispose();
    }//GEN-LAST:event_buttResolveActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Collis.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Collis.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Collis.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Collis.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Collis dialog = new Collis(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    
    
     public void handleThisCollision(Process p,Svn svn,String paper,String bib) {
        _svnCode = svn;
        labelName.setText("there was a collision "+paper+"/"+bib);
        _process = p;
    }
    
    private void sendOptionToTerminal(String s){
        try {
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(_process.getOutputStream()));
            w.write(s+"\n"); 
            w.flush();
            w.close();
            if(s.equals("dc")){
                      _posOut = "";
                      BufferedReader buf = new BufferedReader(new InputStreamReader(_process.getInputStream()));
                      String st;
                      while((st=buf.readLine())!=null)
                              _posOut+=st+"\n";
                      buf.close();
                      }
        } catch (IOException ex) {ex.printStackTrace();}
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttDisplay;
    private javax.swing.JButton buttPop;
    private javax.swing.JButton buttPost;
    private javax.swing.JButton buttPush;
    private javax.swing.JButton buttResolve;
    private javax.swing.JLabel labelName;
    // End of variables declaration//GEN-END:variables
}
