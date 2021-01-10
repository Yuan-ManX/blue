/*
 * blue - object composition environment for csound
 * Copyright (c) 2000-2004 Steven Yi (stevenyi@gmail.com)
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
package blue.orchestra.editor.blueSynthBuilder.swing.editors;

import blue.orchestra.blueSynthBuilder.BSBDropdown;
import blue.orchestra.blueSynthBuilder.BSBDropdownItemList;
import blue.orchestra.editor.blueSynthBuilder.swing.BSBDropdownView;
import blue.orchestra.editor.blueSynthBuilder.swing.DropdownItemEditorDialog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author steven
 */
public class DropdownItemsPropertyEditor implements PropertyEditor {

    private static final DropdownItemEditorDialog dialog = new DropdownItemEditorDialog();

    PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    BSBDropdownItemList items = null;

    BSBDropdownView view = null;

    Component editor;

    JButton button;

    public DropdownItemsPropertyEditor() {
        editor = new JPanel(new BorderLayout(0, 0));

        ((JPanel) editor).add("East", button = new JButton("..."));
        button.setMargin(new Insets(0, 0, 0, 0));

        button.addActionListener((ActionEvent e) -> {
            // selectColor();
            // JOptionPane.showMessageDialog(null, "test");
            editItems();
        });

        ((JPanel) editor).setOpaque(false);
    }

    /**
     * This method is a hack to get the view updated
     */
    protected void editItems() {
        BSBDropdownItemList old = items;
        BSBDropdownItemList newList = new BSBDropdownItemList(old);
        dialog.show(newList);

        listeners.firePropertyChange("value", old, newList);

        old.clear();
        old.addAll(newList);

//        view.refresh();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyEditor#getCustomEditor()
     */
    @Override
    public Component getCustomEditor() {

        return editor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyEditor#supportsCustomEditor()
     */
    @Override
    public boolean supportsCustomEditor() {
        return false;
    }

    @Override
    public Object getValue() {
        return view;
    }

    @Override
    public void setValue(Object value) {
        this.view = (BSBDropdownView) value;
        this.items = ((BSBDropdown) view.getBSBObject()).getBSBDropdownItemList();
    }

    @Override
    public String getAsText() {
        return null;
    }

    @Override
    public String getJavaInitializationString() {
        return null;
    }

    @Override
    public String[] getTags() {
        return null;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyEditor#isPaintable()
     */
    @Override
    public boolean isPaintable() {
        return false;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(Object oldValue, Object newValue) {
        listeners.firePropertyChange("value", oldValue, newValue);
    }

}