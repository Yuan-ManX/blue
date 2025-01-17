/*
 * blue - object composition environment for csound
 * Copyright (C) 2014
 * Steven Yi <stevenyi@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package blue.score.layers.audio.ui;

import blue.BlueSystem;
import blue.plugin.ScoreObjectEditorPlugin;
import blue.score.ScoreObject;
import blue.score.layers.audio.core.AudioClip;
import blue.score.layers.audio.core.FadeType;
import blue.soundObject.editor.ScoreObjectEditor;
import javafx.beans.value.ChangeListener;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Steven Yi
 */
@ScoreObjectEditorPlugin(scoreObjectType = AudioClip.class)
public class AudioClipEditor extends ScoreObjectEditor {

    AudioClip clip;

    volatile boolean isUpdating = false;

    private ChangeListener cl;

    /**
     * Creates new form AudioClipEditor
     */
    public AudioClipEditor() {
        initComponents();

        cl = (obs, old, newVal) -> {
            if (clip == null || isUpdating) {
                return;
            }

            if (obs == clip.startProperty()) {
                startTimeTextField.setText(newVal.toString());
            } else if (obs == clip.durationProperty()) {
                durationTextField.setText(newVal.toString());
            } else if (obs == clip.fileStartTimeProperty()) {
                fileStartTextField.setText(newVal.toString());
            } else if (obs == clip.fadeInProperty()) {
                fadeInTextField.setText(newVal.toString());
            } else if (obs == clip.fadeInTypeProperty()) {
                fadeInTypeComboBox.setSelectedItem(newVal);
            } else if (obs == clip.fadeOutProperty()) {
                fadeOutTextField.setText(newVal.toString());
            } else if (obs == clip.fadeOutTypeProperty()) {
                fadeOutTypeComboBox.setSelectedItem(newVal);
            }
        };
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        audioFileNameTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        fileStartTextField = new javax.swing.JTextField();
        fileDurationTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        fadeInTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        fadeOutTypeComboBox = new javax.swing.JComboBox<>();
        fadeOutTextField = new javax.swing.JTextField();
        fadeInTypeComboBox = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        loopingCheckbox = new javax.swing.JCheckBox();
        startTimeTextField = new javax.swing.JTextField();
        durationTextField = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.jLabel1.text")); // NOI18N

        audioFileNameTextField.setText(org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.audioFileNameTextField.text")); // NOI18N
        audioFileNameTextField.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.jLabel3.text")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.jPanel1.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.jLabel4.text")); // NOI18N

        fileStartTextField.setText(org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.fileStartTextField.text")); // NOI18N
        fileStartTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileStartTextFieldActionPerformed(evt);
            }
        });

        fileDurationTextField.setText(org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.fileDurationTextField.text")); // NOI18N
        fileDurationTextField.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.jLabel5.text")); // NOI18N

        fadeInTextField.setText(org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.fadeInTextField.text")); // NOI18N
        fadeInTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fadeInTextFieldActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.jLabel6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.jLabel7.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.jLabel8.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.jLabel9.text")); // NOI18N

        fadeOutTypeComboBox.setModel(new DefaultComboBoxModel(FadeType.values()));
        fadeOutTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fadeOutTypeComboBoxActionPerformed(evt);
            }
        });

        fadeOutTextField.setText(org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.fadeOutTextField.text")); // NOI18N
        fadeOutTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fadeOutTextFieldActionPerformed(evt);
            }
        });

        fadeInTypeComboBox.setModel(new DefaultComboBoxModel(FadeType.values()));
        fadeInTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fadeInTypeComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.jLabel10.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(loopingCheckbox, org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.loopingCheckbox.text")); // NOI18N
        loopingCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loopingCheckboxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))
                        .addGap(44, 44, 44)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fileDurationTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                            .addComponent(fileStartTextField)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(loopingCheckbox)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(fadeOutTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fadeOutTextField)
                            .addComponent(fadeInTextField)
                            .addComponent(fadeInTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(fileStartTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(fileDurationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(fadeInTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(fadeInTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(fadeOutTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(fadeOutTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(loopingCheckbox))
                .addContainerGap(46, Short.MAX_VALUE))
        );

        startTimeTextField.setText(org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.startTimeTextField.text")); // NOI18N
        startTimeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startTimeTextFieldActionPerformed(evt);
            }
        });

        durationTextField.setText(org.openide.util.NbBundle.getMessage(AudioClipEditor.class, "AudioClipEditor.durationTextField.text")); // NOI18N
        durationTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                durationTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(19, 19, 19)
                        .addComponent(durationTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(audioFileNameTextField)
                            .addComponent(startTimeTextField))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(audioFileNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(startTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(durationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane1.setViewportView(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void durationTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_durationTextFieldActionPerformed
        if (this.clip == null || isUpdating) {
            return;
        }

        try {
            isUpdating = true;
            var val = Double.parseDouble(durationTextField.getText());
            val = Math.min(0.0001, Math.max(val, clip.getAudioDuration()));
            this.clip.setDuration(val);
        } catch (NumberFormatException nfe) {
            durationTextField.setText(Double.toString(this.clip.getDuration()));
        } finally {
            isUpdating = false;
        }
    }//GEN-LAST:event_durationTextFieldActionPerformed

    private void startTimeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startTimeTextFieldActionPerformed
        if (this.clip == null || isUpdating) {
            return;
        }

        try {
            isUpdating = true;
            var val = Double.parseDouble(startTimeTextField.getText());
            val = Math.max(0, val);
            this.clip.setStart(val);
        } catch (NumberFormatException nfe) {
            startTimeTextField.setText(Double.toString(this.clip.getStart()));
        } finally {
            isUpdating = false;
        }
    }//GEN-LAST:event_startTimeTextFieldActionPerformed

    private void fadeOutTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fadeOutTextFieldActionPerformed
        if (this.clip == null || isUpdating) {
            return;
        }

        try {
            isUpdating = true;
            var val = Double.parseDouble(fadeOutTextField.getText());
            val = Math.max(0, Math.min(val, clip.getDuration() - clip.getFadeIn()));
            this.clip.setFadeOut(val);
        } catch (NumberFormatException nfe) {
            fadeOutTextField.setText(Double.toString(this.clip.getFadeOut()));
        } finally {
            isUpdating = false;
        }
    }//GEN-LAST:event_fadeOutTextFieldActionPerformed

    private void fadeInTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fadeInTextFieldActionPerformed
        if (this.clip == null || isUpdating) {
            return;
        }

        try {
            isUpdating = true;
            var val = Double.parseDouble(fadeInTextField.getText());
            val = Math.max(0, Math.min(val, clip.getDuration() - clip.getFadeOut()));
            this.clip.setFadeIn(val);
        } catch (NumberFormatException nfe) {
            fadeInTextField.setText(Double.toString(this.clip.getFadeIn()));
        } finally {
            isUpdating = false;
        }
    }//GEN-LAST:event_fadeInTextFieldActionPerformed

    private void fileStartTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileStartTextFieldActionPerformed
        if (this.clip == null || isUpdating) {
            return;
        }

        try {
            isUpdating = true;
            var val = Double.parseDouble(fileStartTextField.getText());
            val = Math.min(0.0, Math.max(val, clip.getAudioDuration()));
            this.clip.setFileStartTime(val);
        } catch (NumberFormatException nfe) {
            fileStartTextField.setText(Double.toString(this.clip.getFileStartTime()));
        } finally {
            isUpdating = false;
        }
    }//GEN-LAST:event_fileStartTextFieldActionPerformed

    private void fadeInTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fadeInTypeComboBoxActionPerformed
        if (this.clip == null || isUpdating) {
            return;
        }

        isUpdating = true;
        this.clip.setFadeInType((FadeType) fadeInTypeComboBox.getSelectedItem());
        isUpdating = false;
    }//GEN-LAST:event_fadeInTypeComboBoxActionPerformed

    private void fadeOutTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fadeOutTypeComboBoxActionPerformed
        if (this.clip == null || isUpdating) {
            return;
        }

        isUpdating = true;
        this.clip.setFadeOutType((FadeType) fadeOutTypeComboBox.getSelectedItem());
        isUpdating = false;
    }//GEN-LAST:event_fadeOutTypeComboBoxActionPerformed

    private void loopingCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loopingCheckboxActionPerformed
        if (this.clip == null || isUpdating) {
            return;
        }

        isUpdating = true;
        this.clip.setLooping(loopingCheckbox.isSelected());
        isUpdating = false;
    }//GEN-LAST:event_loopingCheckboxActionPerformed

    @Override
    public void editScoreObject(ScoreObject sObj) {
        final AudioClip newClip = (AudioClip) sObj;

        if (this.clip != null) {
            this.clip.startProperty().removeListener(cl);
            this.clip.durationProperty().removeListener(cl);
            this.clip.fileStartTimeProperty().removeListener(cl);
            this.clip.fadeInProperty().removeListener(cl);
            this.clip.fadeInTypeProperty().removeListener(cl);
            this.clip.fadeOutProperty().removeListener(cl);
            this.clip.fadeOutTypeProperty().removeListener(cl);
            this.clip.loopingProperty().removeListener(cl);
        }

        this.clip = newClip;

        isUpdating = true;

        String path = BlueSystem.getRelativePath(
                clip.getAudioFile().getAbsolutePath());
        audioFileNameTextField.setText(path);

        startTimeTextField.setText(Double.toString(clip.getStartTime()));
        durationTextField.setText(Double.toString(clip.getSubjectiveDuration()));

        fileStartTextField.setText(Double.toString(clip.getFileStartTime()));
        fileDurationTextField.setText(Double.toString(clip.getDuration()));
        fadeInTextField.setText(Double.toString(clip.getFadeIn()));
        fadeOutTextField.setText(Double.toString(clip.getFadeOut()));

        fadeOutTypeComboBox.setSelectedItem(clip.getFadeInType());
        fadeInTypeComboBox.setSelectedItem(clip.getFadeOutType());
        
        loopingCheckbox.setSelected(clip.isLooping());

        isUpdating = false;

        this.clip.startProperty().addListener(cl);
        this.clip.durationProperty().addListener(cl);
        this.clip.fileStartTimeProperty().addListener(cl);
        this.clip.fadeInProperty().addListener(cl);
        this.clip.fadeInTypeProperty().addListener(cl);
        this.clip.fadeOutProperty().addListener(cl);
        this.clip.fadeOutTypeProperty().addListener(cl);
        this.clip.loopingProperty().addListener(cl);

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField audioFileNameTextField;
    private javax.swing.JTextField durationTextField;
    private javax.swing.JTextField fadeInTextField;
    private javax.swing.JComboBox<FadeType> fadeInTypeComboBox;
    private javax.swing.JTextField fadeOutTextField;
    private javax.swing.JComboBox<FadeType> fadeOutTypeComboBox;
    private javax.swing.JTextField fileDurationTextField;
    private javax.swing.JTextField fileStartTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JCheckBox loopingCheckbox;
    private javax.swing.JTextField startTimeTextField;
    // End of variables declaration//GEN-END:variables
}
