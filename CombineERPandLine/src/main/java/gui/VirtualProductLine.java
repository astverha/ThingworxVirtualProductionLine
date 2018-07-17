package gui;

import configuration.AssetThing;
import configuration.Line;
import configuration.StatusEnum;
import configuration.ThingProperty;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import static java.awt.event.KeyEvent.VK_ENTER;
import java.awt.event.KeyListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class VirtualProductLine extends javax.swing.JFrame {

    private GUIAgent guiAgent;

    public VirtualProductLine(GUIAgent guiAgent) {
        initComponents();
        setLocationRelativeTo(null);
        this.guiAgent = guiAgent;
        this.PropertyPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        this.RestartButton.setEnabled(false);
        this.initDropDown();

        newProductionRateField.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == VK_ENTER) {
                    int prodRate = Integer.parseInt(newProductionRateField.getText().trim());
                    if (Integer.parseInt(guiAgent.getSelectedAsset().getPropertyByName("IdealRunRate").getValue()) < prodRate) {
                        JOptionPane.showMessageDialog(null, "Invalid! New production rate cannot be greater than ideal run rate.");
                    } else {

                        guiAgent.setProductionRate(prodRate);
                    }
                    newProductionRateField.setText("");
                }
            }

            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
            }

        });

        new Timer(1000, taskPerformer).start();
    }

    ActionListener taskPerformer = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            showPropsOfSelectedAsset();
        }
    };

    private void initDropDown() {
        //LINE DROP DOWN
        this.LineDropDown.removeAllItems();
        for (Line l : guiAgent.getLines()) {
            this.LineDropDown.addItem(l.getName());
        }
        this.LineDropDown.setSelectedIndex(0);
        this.guiAgent.setSelectedLine(guiAgent.getLines().get(0).getName());

        this.LineDropDown.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == 1) {
                    guiAgent.setSelectedLine(e.getItem().toString());
                    AssetDropDown.removeAllItems();
                    for (AssetThing at : guiAgent.getAssetThingsFromLine()) {
                       AssetDropDown.addItem(at.getName());
                    }
                }
            }

        });

        //ASSET DROP DOWN
        this.AssetDropDown.removeAllItems();
        for (AssetThing at : guiAgent.getAssetThingsFromLine()) {
            this.AssetDropDown.addItem(at.getName());
        }
        this.AssetDropDown.setSelectedIndex(0);
        this.guiAgent.setSelectedAsset(this.guiAgent.getAllThings().get(1));
        this.showPropsOfSelectedAsset();
        this.setButtonStatus(this.guiAgent.getSelectedAsset().getPropertyByName("pushedStatus").getValue());

        this.AssetDropDown.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                //1 means that an item was selected
                //2 means that an item was deselected
                if (e.getStateChange() == 1) {
                    AssetThing at = guiAgent.getAssetThingByName(e.getItem().toString());
                    if (at != null) {
                        guiAgent.setSelectedAsset(at);
                        showPropsOfSelectedAsset();
                        //Buttons enabled/disabled depending on status
                        setButtonStatus(guiAgent.getSelectedAsset().getPropertyByName("pushedStatus").getValue());
                    }

                }
            }

        });

    }

    private void showPropsOfSelectedAsset() {
        this.PropertyPanel.removeAll();

        GridLayout grid = new GridLayout(this.guiAgent.getSelectedAsset().getAssetProperties().size(), 2, 0, 5);
        this.PropertyPanel.setLayout(grid);

        for (ThingProperty pt : this.guiAgent.getSelectedAsset().getAssetProperties()) {
            JLabel label = new JLabel(pt.getName() + ":", SwingConstants.CENTER);
            JLabel value = new JLabel();
            if (pt.getName().equalsIgnoreCase("pushedStatus")) {
                value.setText(StatusEnum.convertIntToState(Integer.parseInt(pt.getValue())));
            } else {
                value.setText(pt.getValue());
            }

            this.PropertyPanel.add(value);

            this.PropertyPanel.add(label);
        }
        this.PropertyPanel.validate();
    }

    private void setButtonStatus(String value) {
        if (value.equals("2")) {
            this.BreakButton.setEnabled(true);
            this.MaintenanceButton.setEnabled(true);
            this.RestartButton.setEnabled(false);
        } else {
            this.BreakButton.setEnabled(false);
            this.MaintenanceButton.setEnabled(false);
            this.RestartButton.setEnabled(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        AssetDropDown = new javax.swing.JComboBox<>();
        PropertyPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        newProductionRateField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        MaintenanceButton = new javax.swing.JButton();
        BreakButton = new javax.swing.JButton();
        RestartButton = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        LineDropwDownLabel = new javax.swing.JLabel();
        LineDropDown = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Asset: ");

        AssetDropDown.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        AssetDropDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AssetDropDownActionPerformed(evt);
            }
        });

        PropertyPanel.setMinimumSize(new java.awt.Dimension(470, 300));
        PropertyPanel.setPreferredSize(new java.awt.Dimension(470, 300));

        javax.swing.GroupLayout PropertyPanelLayout = new javax.swing.GroupLayout(PropertyPanel);
        PropertyPanel.setLayout(PropertyPanelLayout);
        PropertyPanelLayout.setHorizontalGroup(
            PropertyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        PropertyPanelLayout.setVerticalGroup(
            PropertyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jLabel2.setText("New Production Rate:");

        MaintenanceButton.setText("Maintenance");
        MaintenanceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MaintenanceButtonActionPerformed(evt);
            }
        });

        BreakButton.setText("Break");
        BreakButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BreakButtonActionPerformed(evt);
            }
        });

        RestartButton.setText("Restart");
        RestartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RestartButtonActionPerformed(evt);
            }
        });

        jToggleButton1.setText("Pause");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addComponent(BreakButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(MaintenanceButton)
                        .addGap(18, 18, 18)
                        .addComponent(RestartButton))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jToggleButton1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BreakButton)
                    .addComponent(MaintenanceButton)
                    .addComponent(RestartButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jToggleButton1)
                .addContainerGap())
        );

        LineDropwDownLabel.setText("Line: ");

        LineDropDown.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(PropertyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(AssetDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(LineDropwDownLabel)
                        .addGap(18, 18, 18)
                        .addComponent(LineDropDown, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newProductionRateField, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LineDropwDownLabel)
                    .addComponent(LineDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AssetDropDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(newProductionRateField, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49)
                .addComponent(PropertyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BreakButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BreakButtonActionPerformed
        this.guiAgent.breakMachine();
        this.setButtonStatus(this.guiAgent.getSelectedAsset().getPropertyByName("pushedStatus").getValue());
    }//GEN-LAST:event_BreakButtonActionPerformed

    private void MaintenanceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MaintenanceButtonActionPerformed
        this.guiAgent.performMaintenance();
        this.setButtonStatus(this.guiAgent.getSelectedAsset().getPropertyByName("pushedStatus").getValue());
    }//GEN-LAST:event_MaintenanceButtonActionPerformed

    private void RestartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RestartButtonActionPerformed
        this.guiAgent.restartMachine();
        this.setButtonStatus(this.guiAgent.getSelectedAsset().getPropertyByName("pushedStatus").getValue());
    }//GEN-LAST:event_RestartButtonActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        this.guiAgent.togglePause();
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void AssetDropDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AssetDropDownActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AssetDropDownActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(VirtualProductLine.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(VirtualProductLine.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(VirtualProductLine.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(VirtualProductLine.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new VirtualProductLine(new GUIAgent(new ConfigurationAgent("configuration.xml"))).setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> AssetDropDown;
    private javax.swing.JButton BreakButton;
    private javax.swing.JComboBox<String> LineDropDown;
    private javax.swing.JLabel LineDropwDownLabel;
    private javax.swing.JButton MaintenanceButton;
    private javax.swing.JPanel PropertyPanel;
    private javax.swing.JButton RestartButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JTextField newProductionRateField;
    // End of variables declaration//GEN-END:variables
}
