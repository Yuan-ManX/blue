/*
 * blue - object composition environment for csound
 * Copyright (c) 2000-2006 Steven Yi (stevenyi@gmail.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by  the Free Software Foundation; either version 2 of the License or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; see the file COPYING.LIB.  If not, write to
 * the Free Software Foundation Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307 USA
 */
package blue.ui.core.score.soundLayer;

import blue.SoundLayer;
import blue.automation.AutomationManager;
import blue.automation.Parameter;
import blue.automation.ParameterIdList;
import blue.noteProcessor.NoteProcessorChain;
import blue.noteProcessor.NoteProcessorChainMap;
import blue.ui.components.IconFactory;
import blue.ui.core.score.NoteProcessorDialog;
import blue.ui.core.score.ScoreController;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author steven
 */
public class SoundLayerPanel extends javax.swing.JPanel implements
        ListSelectionListener, ListDataListener, PropertyChangeListener {

    private static final Border NORMAL_BORDER = BorderFactory
            .createBevelBorder(BevelBorder.RAISED);

    private static final Border SELECTED_BORDER = BorderFactory.createBevelBorder(
            BevelBorder.RAISED, Color.GREEN, Color.GREEN.darker());

    private static SoundLayerPanelMenu OTHER_MENU = null;

    private final SoundLayer sLayer;

    private boolean automatable = true;

    private final ParameterIdList paramIdList;

    boolean updating = false;

    private final NoteProcessorChainMap npcMap;

    /**
     * Creates new form SoundLayerPanel
     */
    public SoundLayerPanel(SoundLayer soundLayer, NoteProcessorChainMap npcMap) {
        initComponents();
        setSelected(false);

        boolean automatable = ScoreController.getInstance().getScorePath().getLastLayerGroup() == null;

        muteToggleButton.putClientProperty("BlueToggleButton.selectColorOverride", Color.ORANGE.darker());
        soloToggleButton.putClientProperty("BlueToggleButton.selectColorOverride", Color.GREEN.darker());

        automationButton.setVisible(automatable);
        paramSelectPanel.setVisible(automatable);
        this.automatable = automatable;

        this.npcMap = npcMap;

        this.sLayer = soundLayer;

        muteToggleButton.setSelected(sLayer.isMuted());
        soloToggleButton.setSelected(sLayer.isSolo());
        nameLabel.setText(sLayer.getName());

        int size = sLayer.getNoteProcessorChain().size();
        noteProcessorButton.setBackground(size == 0 ? null : Color.GREEN);

        paramIdList = sLayer.getAutomationParameters();

        NoteProcessorChain npc = sLayer.getNoteProcessorChain();

        if (npc.size() > 0) {
            noteProcessorButton.setBackground(Color.RED.darker());
        } else {
            noteProcessorButton.setBackground(null);
        }

        updateParameterPanel();
    }

    public void setSelected(boolean val) {
        setBorder(val ? SELECTED_BORDER : NORMAL_BORDER);
    }

    public void editName() {
        if (sLayer == null) {
            return;
        }

        nameText.setText(sLayer.getName());
        ((CardLayout) jPanel1.getLayout()).show(jPanel1, "textField");
        nameText.requestFocusInWindow();
    }

    @Override
    public void removeNotify() {
        if (this.paramIdList != null) {
            paramIdList.removeListSelectionListener(this);
            paramIdList.removeListDataListener(this);
        }

        if (this.sLayer != null) {
            this.sLayer.removePropertyChangeListener(this);
        }
        super.removeNotify();
    }

    @Override
    public void addNotify() {
        if (this.paramIdList != null) {
            paramIdList.addListSelectionListener(this);
            paramIdList.addListDataListener(this);
        }

        if (this.sLayer != null) {
            this.sLayer.addPropertyChangeListener(this);
        }
        super.addNotify();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code
    // <editor-fold defaultstate="collapsed" desc=" Generated Code
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameText = new javax.swing.JTextField();
        muteToggleButton = new javax.swing.JToggleButton();
        soloToggleButton = new javax.swing.JToggleButton();
        noteProcessorButton = new javax.swing.JButton();
        automationButton = new javax.swing.JButton();
        otherMenuButton = new javax.swing.JButton();
        paramSelectPanel = new javax.swing.JPanel();
        paramColorSelect = new blue.components.ColorSelectionPanel();
        paramNameLabel = new javax.swing.JLabel();
        paramPreviousButton = new javax.swing.JButton();
        paramNextButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 3, 0, 3));
        jPanel1.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(17, 17));
        jPanel1.setLayout(new java.awt.CardLayout());

        nameLabel.setText("SoundObject Name");
        nameLabel.setMinimumSize(new java.awt.Dimension(0, 15));
        jPanel1.add(nameLabel, "label");

        nameText.setText("SoundObject Name");
        nameText.setMinimumSize(new java.awt.Dimension(0, 15));
        nameText.setPreferredSize(new java.awt.Dimension(115, 17));
        nameText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                nameTextFocusLost(evt);
            }
        });
        nameText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameTextActionPerformed(evt);
            }
        });
        nameText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nameTextKeyPressed(evt);
            }
        });
        jPanel1.add(nameText, "textField");

        jPanel2.add(jPanel1);

        muteToggleButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        muteToggleButton.setText("M");
        muteToggleButton.setFocusPainted(false);
        muteToggleButton.setFocusable(false);
        muteToggleButton.setMargin(new java.awt.Insets(0, 3, 0, 3));
        muteToggleButton.setMaximumSize(new java.awt.Dimension(19, 19));
        muteToggleButton.setPreferredSize(new java.awt.Dimension(19, 18));
        muteToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muteToggleButtonActionPerformed(evt);
            }
        });
        jPanel2.add(muteToggleButton);

        soloToggleButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        soloToggleButton.setText("S");
        soloToggleButton.setFocusPainted(false);
        soloToggleButton.setFocusable(false);
        soloToggleButton.setMargin(new java.awt.Insets(0, 3, 0, 3));
        soloToggleButton.setMaximumSize(new java.awt.Dimension(19, 19));
        soloToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                soloToggleButtonActionPerformed(evt);
            }
        });
        jPanel2.add(soloToggleButton);

        noteProcessorButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        noteProcessorButton.setText("N");
        noteProcessorButton.setFocusPainted(false);
        noteProcessorButton.setFocusable(false);
        noteProcessorButton.setMargin(new java.awt.Insets(0, 3, 0, 3));
        noteProcessorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteProcessorButtonActionPerformed(evt);
            }
        });
        jPanel2.add(noteProcessorButton);

        automationButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        automationButton.setText("A");
        automationButton.setToolTipText("Automation Settings");
        automationButton.setFocusPainted(false);
        automationButton.setFocusable(false);
        automationButton.setMargin(new java.awt.Insets(0, 3, 0, 3));
        automationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                automationButtonActionPerformed(evt);
            }
        });
        jPanel2.add(automationButton);

        otherMenuButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        otherMenuButton.setIcon(IconFactory.getDownArrowIcon());
        otherMenuButton.setToolTipText("Automation Settings");
        otherMenuButton.setFocusPainted(false);
        otherMenuButton.setFocusable(false);
        otherMenuButton.setMargin(new java.awt.Insets(5, 0, 4, 0));
        otherMenuButton.setMaximumSize(new java.awt.Dimension(19, 19));
        otherMenuButton.setPreferredSize(new java.awt.Dimension(16, 17));
        otherMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                otherMenuButtonActionPerformed(evt);
            }
        });
        jPanel2.add(otherMenuButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(jPanel2, gridBagConstraints);

        paramSelectPanel.setFocusable(false);
        paramSelectPanel.setPreferredSize(new java.awt.Dimension(100, 19));
        paramSelectPanel.setLayout(new javax.swing.BoxLayout(paramSelectPanel, javax.swing.BoxLayout.LINE_AXIS));

        paramColorSelect.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        paramColorSelect.setToolTipText("Parameter Line Color");
        paramColorSelect.setMaximumSize(new java.awt.Dimension(15, 15));
        paramColorSelect.setPreferredSize(new java.awt.Dimension(15, 15));
        paramColorSelect.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                paramColorSelectPropertyChange(evt);
            }
        });
        paramSelectPanel.add(paramColorSelect);

        paramNameLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        paramNameLabel.setText("jLabel1");
        paramNameLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 3, 0, 0));
        paramNameLabel.setFocusable(false);
        paramNameLabel.setMaximumSize(new java.awt.Dimension(32768, 15));
        paramNameLabel.setPreferredSize(new java.awt.Dimension(100, 15));
        paramSelectPanel.add(paramNameLabel);

        paramPreviousButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        paramPreviousButton.setIcon(IconFactory.getLeftArrowIcon());
        paramPreviousButton.setToolTipText("Automation Settings");
        paramPreviousButton.setFocusPainted(false);
        paramPreviousButton.setFocusable(false);
        paramPreviousButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        paramPreviousButton.setMaximumSize(new java.awt.Dimension(15, 15));
        paramPreviousButton.setPreferredSize(new java.awt.Dimension(18, 17));
        paramPreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paramPreviousButtonActionPerformed(evt);
            }
        });
        paramSelectPanel.add(paramPreviousButton);

        paramNextButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        paramNextButton.setIcon(IconFactory.getRightArrowIcon());
        paramNextButton.setToolTipText("Automation Settings");
        paramNextButton.setFocusPainted(false);
        paramNextButton.setFocusable(false);
        paramNextButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        paramNextButton.setMaximumSize(new java.awt.Dimension(15, 15));
        paramNextButton.setPreferredSize(new java.awt.Dimension(17, 17));
        paramNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paramNextButtonActionPerformed(evt);
            }
        });
        paramSelectPanel.add(paramNextButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(paramSelectPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weighty = 1.0;
        add(jPanel4, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void otherMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_otherMenuButtonActionPerformed
        if (OTHER_MENU == null) {
            OTHER_MENU = new SoundLayerPanelMenu();
        }
        OTHER_MENU.setSoundLayer(this.sLayer);
        OTHER_MENU.show(otherMenuButton, 0, otherMenuButton.getHeight());

    }// GEN-LAST:event_otherMenuButtonActionPerformed

    private void paramColorSelectPropertyChange(
            java.beans.PropertyChangeEvent evt) {// GEN-FIRST:event_paramColorSelectPropertyChange
        if (sLayer == null || paramIdList == null || updating || !"colorSelectionValue".equals(evt.getPropertyName())) {
            return;
        }

        int index = paramIdList.getSelectedIndex();

        if (index < 0) {
            return;
        }

        String id = paramIdList.getParameterId(index);

        Parameter param = AutomationManager.getInstance().getParameter(id);

        if (param != null) {
            param.getLine().setColor(paramColorSelect.getColor());
        }
    }// GEN-LAST:event_paramColorSelectPropertyChange

    private void formComponentResized(java.awt.event.ComponentEvent evt) {// GEN-FIRST:event_formComponentResized
        if (sLayer == null) {
            return;
        }

        if (automatable && sLayer.getAutomationParameters().size() > 0) {
            paramSelectPanel.setVisible(getHeight() > 22);
        } else {
            paramSelectPanel.setVisible(false);
        }
    }// GEN-LAST:event_formComponentResized

    private void paramNextButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_paramNextButtonActionPerformed
        if (sLayer == null || paramIdList == null || paramIdList.size() < 2) {
            return;
        }

        int index = paramIdList.getSelectedIndex() + 1;
        if (index >= paramIdList.size()) {
            index = 0;
        }
        paramIdList.setSelectedIndex(index);

    }// GEN-LAST:event_paramNextButtonActionPerformed

    private void paramPreviousButtonActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_paramPreviousButtonActionPerformed
        if (sLayer == null || paramIdList == null || paramIdList.size() < 2) {
            return;
        }

        int index = paramIdList.getSelectedIndex() - 1;
        if (index < 0) {
            index = paramIdList.size() - 1;
        }
        paramIdList.setSelectedIndex(index);
    }// GEN-LAST:event_paramPreviousButtonActionPerformed

    private void automationButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_automationButtonActionPerformed
        JPopupMenu menu = AutomationManager.getInstance().getAutomationMenu(
                this.sLayer.getAutomationParameters());

        menu.show(automationButton, 0, automationButton.getHeight());

    }// GEN-LAST:event_automationButtonActionPerformed

    private void nameTextKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_nameTextKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            nameText.setText(sLayer.getName());
            ((CardLayout) jPanel1.getLayout()).show(jPanel1, "label");
        }
    }// GEN-LAST:event_nameTextKeyPressed

    private void nameTextFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_nameTextFocusLost
        if (sLayer == null) {
            return;
        }

        final var newName = nameText.getText();
        if (!newName.equals(sLayer.getName())) {
            sLayer.setName(nameText.getText());
            nameLabel.setText(sLayer.getName());
        }

        ((CardLayout) jPanel1.getLayout()).show(jPanel1, "label");
    }// GEN-LAST:event_nameTextFocusLost

    private void nameTextActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_nameTextActionPerformed
        if (sLayer == null) {
            return;
        }

        sLayer.setName(nameText.getText());
        nameLabel.setText(sLayer.getName());

        ((CardLayout) jPanel1.getLayout()).show(jPanel1, "label");
    }// GEN-LAST:event_nameTextActionPerformed

    private void noteProcessorButtonActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_noteProcessorButtonActionPerformed
        NoteProcessorDialog dialog = NoteProcessorDialog.getInstance();

        NoteProcessorChain npc = sLayer.getNoteProcessorChain();

        dialog.setNoteProcessorChain(npc);
        dialog.setNoteProcessorChainMap(npcMap);
        dialog.ask();

        if (npc.size() > 0) {
            noteProcessorButton.setBackground(Color.RED.darker());
        } else {
            noteProcessorButton.setBackground(null);
        }
    }// GEN-LAST:event_noteProcessorButtonActionPerformed

    private void soloToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_soloToggleButtonActionPerformed
        if (sLayer == null) {
            return;
        }

        sLayer.setSolo(soloToggleButton.isSelected());
    }// GEN-LAST:event_soloToggleButtonActionPerformed

    private void muteToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_muteToggleButtonActionPerformed
        if (sLayer == null) {
            return;
        }

        sLayer.setMuted(muteToggleButton.isSelected());
    }// GEN-LAST:event_muteToggleButtonActionPerformed

    private void updateParameterPanel() {
        if (!automatable) {
            paramSelectPanel.setVisible(false);
            return;
        }

        int index = paramIdList.getSelectedIndex();

        if (paramIdList.size() <= 0 || index < 0) {

            updating = true;
            paramColorSelect.setEnabled(false);
            paramColorSelect.setColor(Color.BLACK);
            updating = false;

            paramNameLabel.setText("No Parameters Available");
            paramNameLabel.setEnabled(false);
            paramNextButton.setEnabled(false);
            paramPreviousButton.setEnabled(false);

            paramSelectPanel.setVisible(false);

            return;
        }

        String id = paramIdList.getParameterId(index);
        Parameter param = AutomationManager.getInstance().getParameter(id);

        if (param == null) {
            updating = true;
            paramColorSelect.setEnabled(false);
            paramColorSelect.setColor(Color.BLACK);
            updating = false;

            paramNameLabel.setText("No Parameters Available");
            paramNameLabel.setEnabled(false);
            paramNextButton.setEnabled(false);
            paramPreviousButton.setEnabled(false);

            paramSelectPanel.setVisible(false);

            return;
        }

        if (getHeight() > 22) {
            paramSelectPanel.setVisible(true);
        }

        updating = true;

        paramColorSelect.setEnabled(true);
        paramColorSelect.setColor(param.getLine().getColor());

        paramNameLabel.setText(param.getName());
        paramNameLabel.setEnabled(true);

        int size = paramIdList.size();

        paramNextButton.setEnabled(true);
        paramPreviousButton.setEnabled(true);

        updating = false;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        updateParameterPanel();
    }

    // ListDataListener
    @Override
    public void intervalAdded(ListDataEvent e) {
        updateParameterPanel();
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        updateParameterPanel();
    }

    @Override
    public void contentsChanged(ListDataEvent lde) {
        updateParameterPanel();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == this.sLayer) {
            String propName = evt.getPropertyName();
            switch (propName) {
                case "heightIndex":
                    revalidate();
                    break;
                case "name":
                    nameText.setText(sLayer.getName());
                    nameLabel.setText(sLayer.getName());
                    break;
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton automationButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JToggleButton muteToggleButton;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameText;
    private javax.swing.JButton noteProcessorButton;
    private javax.swing.JButton otherMenuButton;
    private blue.components.ColorSelectionPanel paramColorSelect;
    private javax.swing.JLabel paramNameLabel;
    private javax.swing.JButton paramNextButton;
    private javax.swing.JButton paramPreviousButton;
    private javax.swing.JPanel paramSelectPanel;
    private javax.swing.JToggleButton soloToggleButton;
    // End of variables declaration//GEN-END:variables
    static class SoundLayerPanelMenu extends JPopupMenu {

        SoundLayer soundLayer = null;

        JMenuItem[] heightItems = new JMenuItem[9];

        public SoundLayerPanelMenu() {
            super();

            JMenu layerHeightMenu = new JMenu("Layer Height");

            ActionListener al = (ActionEvent ae) -> {
                if (soundLayer == null) {
                    return;
                }

                int heightIndex = Integer.parseInt(ae.getActionCommand()) - 1;

                soundLayer.setHeightIndex(heightIndex);
            };

            for (int i = 0; i < heightItems.length; i++) {
                heightItems[i] = new JMenuItem(Integer.toString(i + 1));
                heightItems[i].addActionListener(al);

                layerHeightMenu.add(heightItems[i]);
            }
            this.add(layerHeightMenu);
        }

        public void setSoundLayer(SoundLayer sLayer) {
            this.soundLayer = sLayer;
            setupHeightMenu();
        }

        private void setupHeightMenu() {
            if (soundLayer == null) {
                return;
            }

            int index = soundLayer.getHeightIndex();

            for (int i = 0; i < heightItems.length; i++) {
                heightItems[i].setEnabled(i != index);
            }
        }
    }
}
