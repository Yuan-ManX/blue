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
package blue.ui.utilities;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 *
 * @author stevenyi
 */
public class LinearLayout implements LayoutManager {

    int spacer = 0;
    
    /** Creates a new instance of SoundLayerLayout */
    public LinearLayout() {
    }
    
    public LinearLayout(int spacer) {
        this.spacer = spacer;
    }
    
    public void setSpacer(int spacer) {
        this.spacer = spacer;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return minimumLayoutSize(parent);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        int count = parent.getComponentCount();

        if (count == 0) {
            return new Dimension(0, 0);
        }

        if (parent.getParent() == null) {
            return new Dimension(0, 0);
        }

        int w = parent.getWidth();

        int h = 0; 
        
        for(Component c : parent.getComponents()) {
            if(h != 0) {
                h += spacer;
            }
            h += c.getHeight();
        }

        return new Dimension(w, h);
    }

    @Override
    public void layoutContainer(Container parent) {

        int count = parent.getComponentCount();
        if (count == 0) {
            return;
        }

        if (parent.getParent() == null) {
            return;
        }

        int w = parent.getWidth();

        int runningY = 0;

        for (int i = 0; i < count; i++) {
            Component temp = parent.getComponent(i);

            int h = temp.getHeight();

            temp.setLocation(0, runningY);
            temp.setSize(w, h);

            runningY += h + spacer;
        }
    }

}