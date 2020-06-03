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
package blue.score.layers.patterns.core;

import blue.score.layers.Layer;
import blue.score.layers.LayerGroup;
import blue.score.layers.LayerGroupProvider;
import java.util.Map;

/**
 *
 * @author stevenyi
 */
public class PatternsLayerGroupProvider implements LayerGroupProvider {

    @Override
    public String getLayerGroupName() {
        return "Patterns";
    }

    @Override
    public LayerGroup<? extends Layer> createLayerGroup() {
        PatternsLayerGroup layerGroup =new PatternsLayerGroup();
        layerGroup.newLayerAt(0);
        return layerGroup;
    }

    @Override
    public LayerGroup<? extends Layer> loadFromXML(electric.xml.Element element, Map<String, Object> objRefMap) {
        if ("patternsLayerGroup".equals(element.getName())) {
            try {
                return PatternsLayerGroup.loadFromXML(element);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }
    
}
