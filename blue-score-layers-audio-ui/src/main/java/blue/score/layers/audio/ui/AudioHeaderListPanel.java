/*
 * blue - object composition environment for csound
 * Copyright (C) 2013
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

import blue.event.SelectionEvent;
import blue.event.SelectionListener;
import blue.mixer.Channel;
import blue.mixer.Mixer;
import blue.score.layers.LayerGroupDataEvent;
import blue.score.layers.LayerGroupListener;
import blue.score.layers.audio.core.AudioLayer;
import blue.score.layers.audio.core.AudioLayerGroup;
import blue.ui.utilities.SelectionModel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import skt.swing.SwingUtil;

/**
 *
 * @author stevenyi
 */
public class AudioHeaderListPanel extends JPanel implements
        LayerGroupListener, SelectionListener {
    
    private final AudioLayerGroup layerGroup;
    
    private SelectionModel selection = new SelectionModel();
    
    JPopupMenu menu;
    
    AudioHeaderListLayout layout = new AudioHeaderListLayout();
    private final Mixer mixer;
    
    public AudioHeaderListPanel(AudioLayerGroup audioLayerGroup, Mixer mixer) {
        this.layerGroup = audioLayerGroup;
        this.mixer = mixer;
        this.setLayout(layout);
        layout.setAudioLayerGroup(audioLayerGroup);
        
        for (AudioLayer layer : layerGroup) {
            Channel c = mixer.findChannelById(layer.getUniqueId());
            this.add(new AudioHeaderLayerPanel(layer, c));
        }
        
        selection.addChangeListener((ChangeEvent e) -> {
            updateSelection();
        });
        
        final LayerAddAction layerAddAction = new LayerAddAction();
        final LayerRemoveAction layerRemoveAction = new LayerRemoveAction();
        final PushUpAction pushUpAction = new PushUpAction();
        final PushDownAction pushDownAction = new PushDownAction();
        
        menu = new JPopupMenu("Layer Operations") {
            
            @Override
            public void show(Component invoker, int x, int y) {
                if (layerGroup == null) {
                    return;
                }
                
                layerRemoveAction.setEnabled(layerGroup.size() >= 2);
                pushUpAction.setEnabled(selection.getStartIndex() >= 1);
                pushDownAction.setEnabled(
                        selection.getEndIndex() < layerGroup.size() - 1);
                
                super.show(invoker, x, y);
            }
            
        };
        
        menu.add(layerAddAction);
        menu.add(layerRemoveAction);
        menu.add(pushUpAction);
        menu.add(pushDownAction);
        
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                AudioHeaderListPanel.this.requestFocus();
                
                if (me.isPopupTrigger()) {
                    Component c = getComponentAt(me.getPoint());
                    int index = getIndexOfComponent(c);
                    
                    if (index > selection.getEndIndex()
                            || index < selection.getStartIndex()) {
                        selection.setAnchor(index);
                    }
                    
                    menu.show(me.getComponent(), me.getX(), me.getY());
                } else if (SwingUtilities.isLeftMouseButton(me)) {
                    
                    if (me.getClickCount() == 1) {
                        Component c = getComponentAt(me.getPoint());
                        int index = getIndexOfComponent(c);
                        
                        if (index < 0) {
                            return;
                        }
                        
                        if (me.isShiftDown()) {
                            selection.setEnd(index);
//                            SoundObjectSelectionBus.getInstance().selectionPerformed(
//                                    new SelectionEvent(null,
//                                    SelectionEvent.SELECTION_CLEAR));
                        } else {
                            selection.setAnchor(index);
//                            ((AudioHeaderLayerPanel) c).setSelected(true);
                        }
                        
                    } else if (me.getClickCount() == 2) {
                        Component c = SwingUtilities.getDeepestComponentAt(
                                AudioHeaderListPanel.this, me.getX(), me.getY());
                        
                        if (c != null && c instanceof JLabel) {
                            Component panel = getComponentAt(me.getPoint());
                            
                            ((AudioHeaderLayerPanel) panel).editName();
                            
                        }
                        
                    }
                }
            }
        });
        
        selection.addChangeListener((ChangeEvent e) -> {
            updateSelection();
        });
        
        initActions();
    }
    
    private void initActions() {
        SwingUtil.installActions(this, new Action[]{new ShiftUpAction(),
            new UpAction(), new ShiftDownAction(), new DownAction()},
                WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
    
    private int getIndexOfComponent(Component c) {
        Component[] comps = getComponents();
        
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] == c) {
                return i;
            }
        }
        return -1;
    }
    
    private void updateSelection() {
//        int start = selection.getStartIndex();
//        int end = selection.getEndIndex();
//        
//        Component[] comps = getComponents();
//        
//        for (int i = 0; i < comps.length; i++) {
//            AudioHeaderLayerPanel panel = (AudioHeaderLayerPanel) comps[i];
//            panel.setSelected(i >= start && i <= end);
//        }
    }
    
    public void checkSize() {
        if (layerGroup == null || getParent() == null) {
            setSize(0, 0);
            return;
        }
        
        int w = getParent().getWidth();
        
        int h = layerGroup.getTotalHeight();
        
        this.setSize(w, h);
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        checkSize();
        if (this.layerGroup != null) {
            this.layerGroup.addLayerGroupListener(this);
        }
    }
    
    @Override
    public void removeNotify() {
        if (this.layerGroup != null) {
            this.layerGroup.removeLayerGroupListener(this);
        }
        super.removeNotify();
    }

    /* LAYER GROUP LISTENER */
    @Override
    public void layerGroupChanged(LayerGroupDataEvent event) {
        switch (event.getType()) {
            case LayerGroupDataEvent.DATA_ADDED:
                layersAdded(event);
                break;
            case LayerGroupDataEvent.DATA_REMOVED:
                layersRemoved(event);
                break;
            case LayerGroupDataEvent.DATA_CHANGED:
                contentsChanged(event);
                break;
        }
    }
    
    public void layersAdded(LayerGroupDataEvent e) {
        final int index = e.getStartIndex();
        final AudioLayer sLayer = layerGroup.get(index);
        
        SwingUtilities.invokeLater(() -> {
            Channel c = mixer.findChannelById(sLayer.getUniqueId());
            AudioHeaderLayerPanel panel = new AudioHeaderLayerPanel(sLayer, c);
            
            add(panel, index);
            checkSize();
        });
    }
    
    public void layersRemoved(LayerGroupDataEvent e) {
        final int start = e.getStartIndex();
        final int end = e.getEndIndex();
        
        SwingUtilities.invokeLater(() -> {
            for (int i = end; i >= start; i--) {
                remove(i);
            }
            
            checkSize();
            
            selection.setAnchor(-1);
        });
    }
    
    public void contentsChanged(LayerGroupDataEvent e) {
        final int start = e.getStartIndex();
        final int end = e.getEndIndex();

        // This is a hack to determine what direction the layers were
        // pushed
        final boolean isUp = ((start >= 0) && (end >= 0));
        
        SwingUtilities.invokeLater(() -> {
            if (isUp) {
                Component c = getComponent(start);
                
                AudioHeaderLayerPanel panel = (AudioHeaderLayerPanel) c;
                remove(start);
                add(c, end);
                
                int i1 = selection.getStartIndex() - 1;
                int i2 = selection.getEndIndex() - 1;
                
                selection.setAnchor(i1);
                selection.setEnd(i2);
                
            } else {
                // have to flip because listDataEvent stores as min and max
                Component c = getComponent(-start);
                
                AudioHeaderLayerPanel panel = (AudioHeaderLayerPanel) c;
                remove(-start);
                add(c, -end);
                
                int i1 = selection.getStartIndex() + 1;
                int i2 = selection.getEndIndex() + 1;
                
                selection.setAnchor(i1);
                selection.setEnd(i2);
            }
            
            revalidate();
        });
        
    }
    
    @Override
    public void selectionPerformed(SelectionEvent e) {
//        SoundObject sObj = (SoundObject) e.getSelectedItem();
//        if(sObj == null) {
//            return;
//        }
//        
//        boolean found = false;
//        for(int i = 0; i < layerGroup.getSize(); i++) {
//            AudioLayer pLayer = (AudioLayer) layerGroup.getLayerAt(i);
//            if(pLayer.getSoundObject() == sObj) {
//                found = true;
//                break;
//            }
//        }

//        if(!found) {
//            selection.clear();
//        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        return layout.preferredLayoutSize(getParent());
    }

    /* Keyboard Actions */
    class UpAction extends AbstractAction {
        
        public UpAction() {
            super("up");
            putValue(Action.SHORT_DESCRIPTION, "Up Action");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_UP, 0));
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            int index = selection.getLastIndexSet() - 1;
            index = index < 0 ? 0 : index;

//            if (index != selection.getStartIndex()) {
            selection.setAnchor(index);
//                ((AudioHeaderLayerPanel)getComponent(index)).editSoundObject();
//            }

        }
        
    }
    
    class ShiftUpAction extends AbstractAction {
        
        public ShiftUpAction() {
            super("shift-up");
            putValue(Action.SHORT_DESCRIPTION, "Shift-Up Action");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_UP, InputEvent.SHIFT_DOWN_MASK));
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            int index = selection.getLastIndexSet() - 1;
            index = index < 0 ? 0 : index;
            
            selection.setEnd(index);

//            SoundObjectSelectionBus.getInstance().selectionPerformed(
//                                    new SelectionEvent(null,
//                                    SelectionEvent.SELECTION_CLEAR));
        }
        
    }
    
    class DownAction extends AbstractAction {
        
        public DownAction() {
            super("down");
            putValue(Action.SHORT_DESCRIPTION, "Down Action");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_DOWN, 0));
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            int index = selection.getLastIndexSet() + 1;
            int length = getComponents().length;
            index = index >= length ? length - 1 : index;

//            if (index != selection.getEndIndex()) {
            selection.setAnchor(index);
//                ((AudioHeaderLayerPanel)getComponent(index)).editSoundObject();
//            }

        }
        
    }
    
    class ShiftDownAction extends AbstractAction {
        
        public ShiftDownAction() {
            super("shift-Down");
            putValue(Action.SHORT_DESCRIPTION, "Shift-Down Action");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_DOWN, InputEvent.SHIFT_DOWN_MASK));
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            int index = selection.getLastIndexSet() + 1;
            int length = getComponents().length;
            index = index >= length ? length - 1 : index;
            
            selection.setEnd(index);

//            SoundObjectSelectionBus.getInstance().selectionPerformed(
//                                    new SelectionEvent(null,
//                                    SelectionEvent.SELECTION_CLEAR));
        }
        
    }
    
    class PushUpAction extends AbstractAction {
        
        public PushUpAction() {
            super("Push Up Layers");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            int start = selection.getStartIndex();
            int end = selection.getEndIndex();
            
            if (end < 0 || start == 0) {
                return;
            }
            
            layerGroup.pushUpLayers(start, end);
            
            selection.setAnchor(start - 1);
        }
        
    }
    
    class PushDownAction extends AbstractAction {
        
        public PushDownAction() {
            super("Push Down Layers");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            int start = selection.getStartIndex();
            int end = selection.getEndIndex();
            
            if (end < 0 || end >= layerGroup.size() - 1) {
                return;
            }
            
            layerGroup.pushDownLayers(start, end);
            
            selection.setAnchor(start + 1);
        }
        
    }
    
    class LayerAddAction extends AbstractAction {
        
        public LayerAddAction() {
            super("Add Layer");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            int end = selection.getEndIndex();
            
            if (end < 0) {
                return;
            }
            end++;
            
            layerGroup.newLayerAt(end);
            
        }
        
    }
    
    class LayerRemoveAction extends AbstractAction {
        
        public LayerRemoveAction() {
            super("Remove Layers");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            int start = selection.getStartIndex();
            int end = selection.getEndIndex();
            
            if (end < 0 || layerGroup.size() < 2) {
                return;
            }
            
            int len = (end - start) + 1;
            
            String message = "Please confirm deleting these "
                    + len
                    + " layers.";
            if (JOptionPane.showConfirmDialog(null, message) == JOptionPane.OK_OPTION) {
                layerGroup.removeLayers(start, end);
                selection.setAnchor(-1);
            }
        }
        
    }
}
