/*
 * blue - object composition environment for csound
 * Copyright (C) 2012
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
package blue.ui.nbutilities;

import java.awt.BorderLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;
import org.netbeans.editor.BaseDocument;

/**
 *
 * @author stevenyi
 */
public class MimeTypeEditorComponent extends JPanel {
    JEditorPane editor = new JEditorPane();
    
    public MimeTypeEditorComponent(){
        this("text/plain");
    }
    
    public MimeTypeEditorComponent(String mimeType) {
        this.setLayout(new BorderLayout());
        
        editor.putClientProperty("usedByCloneableEditor", true);
    
        setMimeType(mimeType);
    }

    public void setMimeType(String mimeType) {
        this.removeAll();
        this.add(BlueNbUtilities.convertEditorForMimeType(editor, mimeType));
    }
    
    public void setUndoManager(UndoManager undo) {
        editor.getDocument().putProperty(BaseDocument.UNDO_MANAGER_PROP, undo);
    }
    
    public JEditorPane getJEditorPane() {
        return editor;
    }

    public Document getDocument() {
        return editor.getDocument();
    }

    public String getText() {
        return editor.getText();
    }
    
    public void setText(String text) {
        editor.setText(text);
    }
}
