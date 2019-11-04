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

import blue.BlueData;
import blue.score.TimeState;
import blue.score.layers.LayerGroup;
import blue.score.layers.audio.core.AudioLayerGroup;
import blue.ui.core.score.layers.LayerGroupUIProvider;
import javax.swing.JComponent;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author stevenyi
 */
public class AudioLayerGroupUIProvider implements LayerGroupUIProvider {

    AudioLayerGroupPropertiesPanel propsPanel = null;

    @Override
    public JComponent getLayerGroupPanel(LayerGroup<?> layerGroup,
            TimeState timeState, BlueData data, InstanceContent ic) {

        if (layerGroup instanceof AudioLayerGroup) {
            return new AudioLayersPanel((AudioLayerGroup) layerGroup,
                    timeState, ic);
        }
        return null;
    }

    @Override
    public JComponent getLayerGroupHeaderPanel(LayerGroup<?> layerGroup,
            TimeState timeState, BlueData data, InstanceContent ic) {
        if (layerGroup instanceof AudioLayerGroup) {
            return new AudioHeaderListPanel((AudioLayerGroup) layerGroup,
                    data.getMixer());
        }
        return null;
    }

    @Override
    public JComponent getLayerGroupPropertiesPanel(LayerGroup layerGroup) {
        if (!(layerGroup instanceof AudioLayerGroup)) {
            return null;
        }
        if (propsPanel == null) {
            propsPanel = new AudioLayerGroupPropertiesPanel();
        }
        propsPanel.setAudioLayerGroup((AudioLayerGroup) layerGroup);
        return propsPanel;
    }
}
