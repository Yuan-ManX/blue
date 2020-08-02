/*
 * GenericEditor2.java
 *
 * Created on November 16, 2006, 2:41 PM
 */
package blue.ui.core.orchestra.editor;

import blue.BlueSystem;
import blue.gui.InfoDialog;
import blue.orchestra.Instrument;
import blue.orchestra.JavaScriptInstrument;
import blue.orchestra.editor.InstrumentEditor;
import blue.plugin.InstrumentEditorPlugin;
import blue.ui.core.udo.EmbeddedOpcodeListPanel;
import blue.ui.nbutilities.MimeTypeEditorComponent;
import blue.ui.utilities.SimpleDocumentListener;
import blue.undo.TabWatchingUndoableEditGenerator;
import java.util.HashMap;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.undo.UndoManager;
import org.openide.awt.UndoRedo;

/**
 *
 * @author steven
 */

@InstrumentEditorPlugin(instrumentType = JavaScriptInstrument.class)
public class JavaScriptInstrumentEditor extends InstrumentEditor {

    private static final HashMap tokenMarkerTypes = new HashMap();
    
    protected MimeTypeEditorComponent codeEditor =
            new MimeTypeEditorComponent("text/javascript");
    
    protected MimeTypeEditorComponent globalOrcEditor =
            new MimeTypeEditorComponent("text/x-csound-orc");
    
    protected MimeTypeEditorComponent globalScoEditor =
            new MimeTypeEditorComponent("text/x-csound-sco");

    JavaScriptInstrument instr;
    EmbeddedOpcodeListPanel udoPanel = new EmbeddedOpcodeListPanel();
    UndoManager undo = new UndoRedo.Manager();

    /**
     * Creates new form GenericEditor2
     */
    public JavaScriptInstrumentEditor() {
        initComponents();

        tabs.add("Instrument Text", codeEditor);

        tabs.add(BlueSystem.getString("instrument.udo"), udoPanel);
        tabs.add("Global Orc", globalOrcEditor);
        tabs.add("Global Sco", globalScoEditor);

        tabs.setSelectedIndex(0);

        globalOrcEditor.getDocument().addDocumentListener(
                new SimpleDocumentListener() {
                    @Override
                    public void documentChanged(DocumentEvent e) {
                        if (instr != null) {
                            instr.setGlobalOrc(globalOrcEditor.getText());
                        }
                    }
                });

        globalScoEditor.getDocument().addDocumentListener(
                new SimpleDocumentListener() {
                    @Override
                    public void documentChanged(DocumentEvent e) {
                        if (instr != null) {
                            instr.setGlobalSco(globalScoEditor.getText());
                        }
                    }
                });

        codeEditor.getDocument().addDocumentListener(
                new SimpleDocumentListener() {
                    @Override
                    public void documentChanged(DocumentEvent e) {
                        if (instr != null) {
                            instr.setText(codeEditor.getText());
                        }
                    }
                });

        new TabWatchingUndoableEditGenerator(tabs, undo);

        codeEditor.getDocument().addUndoableEditListener(undo);
        globalOrcEditor.getDocument().addUndoableEditListener(undo);
        globalScoEditor.getDocument().addUndoableEditListener(undo);

        codeEditor.setUndoManager(undo);
        globalOrcEditor.setUndoManager(undo);
        globalScoEditor.setUndoManager(undo);

        undo.setLimit(1000);

    }

    @Override
    public final void editInstrument(Instrument instr) {
        if (instr == null) {
            this.instr = null;
            editorLabel.setText(BlueSystem
                    .getString("instrument.noEditorAvailable"));
            codeEditor.setText("Null Instrument");
            codeEditor.getJEditorPane().setEnabled(false);
            return;
        }

        if (!(instr instanceof JavaScriptInstrument)) {
            this.instr = null;
            editorLabel.setText(BlueSystem
                    .getString("instrument.noEditorAvailable"));
            codeEditor
                    .setText(
                    "[ERROR] GenericEditor::editInstrument - not instance of GenericEditable");
            codeEditor.getJEditorPane().setEnabled(false);
            return;
        }

        this.instr = (JavaScriptInstrument) instr;

        codeEditor.setText(this.instr.getText());
        codeEditor.getJEditorPane().setEnabled(true);
        codeEditor.getJEditorPane().setCaretPosition(0);
        codeEditor.resetUndoManager();
        
        globalOrcEditor.setText(this.instr.getGlobalOrc());
        globalOrcEditor.getJEditorPane().setEnabled(true);
        globalOrcEditor.getJEditorPane().setCaretPosition(0);
        globalOrcEditor.resetUndoManager();

        globalScoEditor.setText(this.instr.getGlobalSco());
        globalScoEditor.getJEditorPane().setEnabled(true);
        globalScoEditor.getJEditorPane().setCaretPosition(0);
        globalScoEditor.resetUndoManager();

        udoPanel.editOpcodeList(this.instr.getOpcodeList());

        undo.discardAllEdits();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editorLabel = new javax.swing.JLabel();
        testButton = new javax.swing.JButton();
        tabs = new javax.swing.JTabbedPane();

        editorLabel.setText("JavaScript Instrument");

        testButton.setText(BlueSystem.getString("common.test"));
        testButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tabs, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(editorLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 225, Short.MAX_VALUE)
                        .addComponent(testButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testButton)
                    .addComponent(editorLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void testButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_testButtonActionPerformed
        if (this.instr == null) {
            return;
        }
        String instrumentText = ((Instrument) this.instr).generateInstrument();
        InfoDialog.showInformationDialog(SwingUtilities.getRoot(this),
                instrumentText, BlueSystem
                .getString("instrument.generatedInstrument"));
    }// GEN-LAST:event_testButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel editorLabel;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JButton testButton;
    // End of variables declaration//GEN-END:variables

}
