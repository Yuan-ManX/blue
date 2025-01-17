/*
 * blue - object composition environment for csound Copyright (c) 2000-2008
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

package blue.soundObject.editor;

import blue.BlueSystem;
import blue.CompileData;
import blue.gui.ExceptionDialog;
import blue.gui.InfoDialog;
import blue.plugin.ScoreObjectEditorPlugin;
import blue.score.ScoreObject;
import blue.soundObject.NoteList;
import blue.soundObject.PythonObject;
import blue.soundObject.SoundObject;
import blue.soundObject.SoundObjectException;
import blue.ui.nbutilities.MimeTypeEditorComponent;
import blue.ui.utilities.SimpleDocumentListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.undo.UndoManager;
import org.openide.awt.UndoRedo;

/**
 *
 * @author steven
 */
@ScoreObjectEditorPlugin(scoreObjectType = PythonObject.class)
public class PythonEditor extends ScoreObjectEditor {

    PythonObject pObj = null;
    UndoManager undo = new UndoRedo.Manager();
    
    MimeTypeEditorComponent codeEditor = new MimeTypeEditorComponent("text/x-python");
    /** Creates new form PythonEditor */
    public PythonEditor() {
        initComponents();

        initActions();
        
        this.add(codeEditor, BorderLayout.CENTER);

        codeEditor.getDocument().addDocumentListener(new SimpleDocumentListener() {

            @Override
            public void documentChanged(DocumentEvent e) {
                if (pObj != null) {
                    pObj.setText(codeEditor.getText());
                }
            }
        });

        codeEditor.setUndoManager(undo);
        codeEditor.getDocument().addUndoableEditListener(undo);

        undo.setLimit(1000);
    }

    private void initActions() {
        InputMap inputMap = codeEditor.getJEditorPane().getInputMap();
        ActionMap actions = codeEditor.getJEditorPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, BlueSystem.
                getMenuShortcutKey()), "testSoundObject");

        actions.put("testSoundObject", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                testSoundObject();
            }
        });

    }

    @Override
    public final void editScoreObject(ScoreObject sObj) {
        this.pObj = null;
        
        if (sObj == null) {
            codeEditor.setText("null soundObject");
            codeEditor.getJEditorPane().setEnabled(false);
            processOnLoadCheckBox.setEnabled(false);
            return;
        }

        if (!(sObj instanceof PythonObject)) {            
            codeEditor.setText(
                    "[ERROR] GenericEditor::editSoundObject - not instance " +
                    "of GenericEditable");
            codeEditor.getJEditorPane().setEnabled(false);
            processOnLoadCheckBox.setEnabled(false);
            return;
        }

        PythonObject tempPObj = (PythonObject) sObj;

        codeEditor.setText(tempPObj.getText());
        codeEditor.getJEditorPane().setEnabled(true);
        codeEditor.getJEditorPane().setCaretPosition(0);
        codeEditor.resetUndoManager();

        processOnLoadCheckBox.setSelected(tempPObj.isOnLoadProcessable());
        processOnLoadCheckBox.setEnabled(true);

        undo.discardAllEdits();

        this.pObj = tempPObj;
    }

    public final void testSoundObject() {
        if (this.pObj == null) {
            return;
        }

        NoteList notes = null;

        try {
            notes = ((SoundObject) this.pObj).generateForCSD(CompileData.createEmptyCompileData(),
                    0.0f, -1.0f);
        } catch (SoundObjectException e) {
            ExceptionDialog.showExceptionDialog(SwingUtilities.getRoot(this), e);
        }

        if (notes != null) {
            InfoDialog.showInformationDialog(SwingUtilities.getRoot(this),
                    notes.toString(), BlueSystem.getString(
                    "soundObject.generatedScore"));
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        processOnLoadCheckBox = new javax.swing.JCheckBox();
        testButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(596, 222));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("PythonObject");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 3, 0);
        jPanel1.add(jLabel1, gridBagConstraints);

        processOnLoadCheckBox.setText("Process On Load");
        processOnLoadCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processOnLoadCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 0);
        jPanel1.add(processOnLoadCheckBox, gridBagConstraints);

        testButton.setText(BlueSystem.getString("common.test"));
        testButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(testButton, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    private void processOnLoadCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processOnLoadCheckBoxActionPerformed
        if (this.pObj != null) {
            this.pObj.setOnLoadProcessable(processOnLoadCheckBox.isSelected());
        }
    }//GEN-LAST:event_processOnLoadCheckBoxActionPerformed

    private void testButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testButtonActionPerformed
        testSoundObject();
    }//GEN-LAST:event_testButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JCheckBox processOnLoadCheckBox;
    private javax.swing.JButton testButton;
    // End of variables declaration//GEN-END:variables
}
