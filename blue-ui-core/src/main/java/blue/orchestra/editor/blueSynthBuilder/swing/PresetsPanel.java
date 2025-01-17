/*
 * blue - object composition environment for csound Copyright (c) 2000-2009
 * Steven Yi (stevenyi@gmail.com)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; see the file COPYING.LIB. If not, write to the Free
 * Software Foundation Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307
 * USA
 */

/*
 * PresetsPanel.java
 *
 * Created on Jan 12, 2010, 8:25:35 PM
 */
package blue.orchestra.editor.blueSynthBuilder.swing;

import blue.orchestra.blueSynthBuilder.BSBGraphicInterface;
import blue.orchestra.blueSynthBuilder.Preset;
import blue.orchestra.blueSynthBuilder.PresetGroup;
import blue.orchestra.editor.blueSynthBuilder.PresetsManagerDialog;
import blue.orchestra.editor.blueSynthBuilder.PresetsUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.openide.windows.WindowManager;

/**
 *
 * @author syi
 */
public class PresetsPanel extends javax.swing.JPanel {

    private JPopupMenu rootMenu = new JPopupMenu();

    PresetsManagerDialog presetsManager = null;

    ActionListener addPresetListener;

    ActionListener addFolderListener;

    ArrayList<PresetListener> listeners = new ArrayList<>();

    private PresetGroup presetGroup;

    private BSBGraphicInterface gInterface;

    /** Creates new form PresetsPanel */
    public PresetsPanel() {
        initComponents();

        addPresetListener = (ActionEvent e) -> {
            Object obj = e.getSource();
            if (obj instanceof AddPresetMenuItem) {
                PresetGroup presetGroup1 = ((AddPresetMenuItem) obj).
                        getPresetGroup();
                addPreset(presetGroup1);
            }
        };

        addFolderListener = (ActionEvent e) -> {
            Object obj = e.getSource();
            if (obj instanceof AddFolderMenuItem) {
                PresetGroup presetGroup1 = ((AddFolderMenuItem) obj).
                        getPresetGroup();
                addFolder(presetGroup1);
            }
        };
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {
                java.awt.GridBagConstraints gridBagConstraints;

                presetsButton = new javax.swing.JButton();
                currentPresetTextField = new javax.swing.JTextField();
                updateButton = new javax.swing.JButton();

                setLayout(new java.awt.GridBagLayout());

                presetsButton.setText(org.openide.util.NbBundle.getMessage(PresetsPanel.class, "PresetsPanel.presetsButton.text")); // NOI18N
                presetsButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                presetsButtonActionPerformed(evt);
                        }
                });
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
                add(presetsButton, gridBagConstraints);

                currentPresetTextField.setEditable(false);
                currentPresetTextField.setText(org.openide.util.NbBundle.getMessage(PresetsPanel.class, "PresetsPanel.currentPresetTextField.text")); // NOI18N
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.weighty = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
                add(currentPresetTextField, gridBagConstraints);

                updateButton.setText(org.openide.util.NbBundle.getMessage(PresetsPanel.class, "PresetsPanel.updateButton.text")); // NOI18N
                updateButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                updateButtonActionPerformed(evt);
                        }
                });
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
                add(updateButton, gridBagConstraints);
        }// </editor-fold>//GEN-END:initComponents

    private void presetsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_presetsButtonActionPerformed
        if (rootMenu != null) {
            rootMenu.show(this, presetsButton.getX(), presetsButton.getHeight());
        }
    }//GEN-LAST:event_presetsButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        Preset preset = presetGroup.findPresetByUniqueId(presetGroup.getCurrentPresetUniqueId());

        if(preset != null){
            preset.updatePresets(gInterface);
            presetGroup.setCurrentPresetModified(false);
            updateCurrentPresetUI();
        }

    }//GEN-LAST:event_updateButtonActionPerformed

    private void setPresetsMenu(PresetGroup pGroup, JMenu menu) {

        for (Iterator<PresetGroup> iter = pGroup.getSubGroups().iterator(); iter.hasNext();) {
            PresetGroup subGroup = iter.next();
            JMenu subMenu = new JMenu(subGroup.getPresetGroupName());
            setPresetsMenu(subGroup, subMenu);

            if (menu == null) {
                rootMenu.add(subMenu);
            } else {
                menu.add(subMenu);
            }
        }

        for (Iterator<Preset> iter = pGroup.getPresets().iterator(); iter.hasNext();) {
            Preset preset = iter.next();
            SetPresetAction presetAction = new SetPresetAction(preset, this);

            if (menu == null) {
                rootMenu.add(presetAction);
            } else {
                menu.add(presetAction);
            }
        }

        JMenuItem addFolder = new AddFolderMenuItem(pGroup);
        JMenuItem addPreset = new AddPresetMenuItem(pGroup);
        Action syncPresetsAction = new AbstractAction("Synchronize Presets") {
	
            @Override
            public void actionPerformed(ActionEvent e) {
                PresetsUtilities.synchronizePresets(
                        PresetsPanel.this.presetGroup, gInterface);
            }
        };
	
	Action managePresetsAction = new AbstractAction("Manage Presets") {
        @Override
	    public void actionPerformed(ActionEvent e) {
		    if (presetsManager == null) {
           		presetsManager = new PresetsManagerDialog(
				    WindowManager.getDefault().getMainWindow());
		    }

		    PresetGroup retVal = presetsManager.editPresetGroup(presetGroup);

		    if (retVal != null) {
			    presetGroup.setPresets(retVal.getPresets());
			    presetGroup.setSubGroups(retVal.getSubGroups());
			    updatePresetMenu();

			    Preset preset = presetGroup.findPresetByUniqueId(presetGroup.getCurrentPresetUniqueId());

			    if (preset == null) {
				    presetGroup.setCurrentPresetUniqueId(null);
				    presetGroup.setCurrentPresetModified(false);
			    }

			    updateCurrentPresetUI();
		    }
	    }	
	};



        if (menu == null) {
            rootMenu.addSeparator();
            rootMenu.add(addFolder);
            rootMenu.add(addPreset);
	    rootMenu.addSeparator();
            rootMenu.add(syncPresetsAction);
	    rootMenu.add(managePresetsAction);
        } else {
            menu.addSeparator();
            menu.add(addFolder);
            menu.add(addPreset);
        }

        addPreset.addActionListener(addPresetListener);
        addFolder.addActionListener(addFolderListener);
    }

    /**
     * @param currentPresetGroup
     */
    private void addPreset(PresetGroup currentPresetGroup) {
        Object retVal = JOptionPane.showInputDialog(null, "Enter Preset Name");

        if (retVal == null) {
            return;
        }

        String presetName = retVal.toString();

        if (presetName.length() == 0) {
            return;
        }

        Preset preset = Preset.createPreset(gInterface);
        preset.setPresetName(presetName);

        currentPresetGroup.getPresets().add(preset);
        Collections.sort(currentPresetGroup.getPresets());

        presetGroup.setCurrentPresetUniqueId(preset.getUniqueId());
        presetGroup.setCurrentPresetModified(false);

        updatePresetMenu();
        updateCurrentPresetUI();
    }

    /**
     * @param currentPresetGroup
     */
    protected void addFolder(PresetGroup presetGroup) {
        Object retVal = JOptionPane.showInputDialog(null, "Enter Folder Name");

        if (retVal == null) {
            return;
        }

        String folderName = retVal.toString();

        if (folderName.length() == 0) {
            return;
        }

        PresetGroup newFolder = new PresetGroup();
        newFolder.setPresetGroupName(folderName);

        presetGroup.getSubGroups().add(newFolder);

        Collections.sort(presetGroup.getSubGroups());

        updatePresetMenu();
    }

    /**
     * @param gInterface
     * @param bsb
     */
    public void editPresetGroup(BSBGraphicInterface gInterface,
            PresetGroup presetGroup) {
        this.gInterface = gInterface;
        this.presetGroup = presetGroup;

        updatePresetMenu();
        updateCurrentPresetUI();
    }

    public void updatePresetMenu() {
        if (this.presetGroup != null) {
            rootMenu.removeAll();
            setPresetsMenu(presetGroup, null);
        }
    }

    protected void updateCurrentPresetUI() {
        if(this.presetGroup.getCurrentPresetUniqueId() == null) {
            currentPresetTextField.setText(" No Preset Selected");
            updateButton.setEnabled(false);
        } else {
            Preset preset = presetGroup.findPresetByUniqueId(presetGroup.getCurrentPresetUniqueId());
            String presetText = " Current Preset: ";


            if(preset != null) {
                String presetPath = presetGroup.getPresetFullPathName(presetGroup.getCurrentPresetUniqueId());
                presetText += presetPath;
            }

            currentPresetTextField.setText(presetText);
            updateButton.setEnabled(true);
        }
    }
    public void addPresetListener(PresetListener presetListener) {
        listeners.add(presetListener);
    }

    /**
     * @param preset
     */
    public void firePresetSelected(Preset preset) {
        this.presetGroup.setCurrentPresetUniqueId(preset.getUniqueId());
        this.presetGroup.setCurrentPresetModified(false);
        updateCurrentPresetUI();

        for (PresetListener listener : listeners) {
            listener.presetSelected(preset);
        }
    }

    private static class SetPresetAction extends AbstractAction {

        private Preset preset;

        private PresetsPanel presetsPanel;

        public SetPresetAction(Preset preset, PresetsPanel presetsPanel) {
            super(preset.getPresetName());
            this.preset = preset;
            this.presetsPanel = presetsPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.presetsPanel.firePresetSelected(preset);
        }
    }

    private static class AddFolderMenuItem extends JMenuItem {

        PresetGroup presetGroup = null;

        public AddFolderMenuItem(PresetGroup presetGroup) {
            super();
            this.setText("Add Folder");
            this.presetGroup = presetGroup;
        }

        public PresetGroup getPresetGroup() {
            return this.presetGroup;
        }
    }

    private static class AddPresetMenuItem extends JMenuItem {

        PresetGroup presetGroup = null;

        public AddPresetMenuItem(PresetGroup presetGroup) {
            super();
            this.setText("Add Preset");
            this.presetGroup = presetGroup;
        }

        public PresetGroup getPresetGroup() {
            return this.presetGroup;
        }
    }
        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JTextField currentPresetTextField;
        private javax.swing.JButton presetsButton;
        private javax.swing.JButton updateButton;
        // End of variables declaration//GEN-END:variables

}
