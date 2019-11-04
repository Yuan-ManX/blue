/*
 * blue - object composition environment for csound
 *  Copyright (c) 2000-2009 Steven Yi (stevenyi@gmail.com)
 * 
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published
 *  by  the Free Software Foundation; either version 2 of the License or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program; see the file COPYING.LIB.  If not, write to
 *  the Free Software Foundation Inc., 59 Temple Place - Suite 330,
 *  Boston, MA  02111-1307 USA
 */
package blue.ui.core.soundObject.renderer;

import blue.ui.nbutilities.lazyplugin.ClassAssociationProcessor;
import blue.ui.nbutilities.lazyplugin.LazyPlugin;
import blue.ui.nbutilities.lazyplugin.LazyPluginFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author steven
 */
public class BarRendererCache {

    private static BarRendererCache instance = null;

    private final Map<Class, LazyPlugin<BarRenderer>> barRenderersMap = new HashMap<>();
    private final Map<Class, BarRenderer> barRenderersCache = new HashMap<>();

    private BarRendererCache() {
    }

    public static BarRendererCache getInstance() {
        if (instance == null) {
            instance = new BarRendererCache();

            List<LazyPlugin<BarRenderer>> plugins = LazyPluginFactory.loadPlugins(
                    "blue/score/barRenderers", BarRenderer.class,
                    new ClassAssociationProcessor("scoreObjectType"));

            for (LazyPlugin<BarRenderer> plugin : plugins) {
                instance.barRenderersMap.put(
                        (Class) plugin.getMetaData("association"),
                        plugin);
            }
        }
        return instance;
    }

    public BarRenderer getBarRenderer(Class clazz) {

        BarRenderer renderer = barRenderersCache.get(clazz);

        if (renderer == null) {

            for (Class c : barRenderersMap.keySet()) {
                if (c.isAssignableFrom(clazz)) {
                    LazyPlugin<BarRenderer> plugin = barRenderersMap.get(c);
                    renderer = plugin.getInstance();
                    barRenderersCache.put(clazz, renderer);
                    break;
                }
            }
        }

        return renderer;
    }
}
