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
package blue.ui.core.score.layers.soundObject.actions;

import blue.BlueData;
import blue.SoundLayer;
import blue.SoundObjectLibrary;
import blue.projects.BlueProjectManager;
import blue.score.ScoreObject;
import blue.score.TimeState;
import blue.score.layers.Layer;
import blue.soundObject.Instance;
import blue.soundObject.PolyObject;
import blue.soundObject.SoundObject;
import blue.ui.core.score.ScoreController;
import blue.utility.ScoreUtilities;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Blue",
        id = "blue.ui.core.score.layers.soundObject.actions.PasteAsPolyObjectAction")
@ActionRegistration(
        displayName = "#CTL_PasteAsPolyObjectAction")
@Messages("CTL_PasteAsPolyObjectAction=Paste as PolyObject")
@ActionReference(path = "blue/score/layers/soundObject/actions",
        position = 60, separatorAfter = 65)
public final class PasteAsPolyObjectAction extends AbstractAction implements ContextAwareAction {

    private List<? extends ScoreObject> scoreObjects;
    private List<Integer> layerIndexes;
    private Point p;
    private TimeState timeState;
    private Layer targetLayer;
    private PolyObject pObj = new PolyObject();

    public PasteAsPolyObjectAction() {
        this(null, null, null, null, null);
    }

    public PasteAsPolyObjectAction(List<? extends ScoreObject> scoreObjects,
            List<Integer> layerIndexes,
            Point p, TimeState timeState, Layer targetLayer) {
        super(NbBundle.getMessage(PasteAsPolyObjectAction.class,
                "CTL_PasteAsPolyObjectAction"));
        this.scoreObjects = scoreObjects;
        this.layerIndexes = layerIndexes;
        this.p = p;
        this.timeState = timeState;
        this.targetLayer = targetLayer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        BlueData data = BlueProjectManager.getInstance().getCurrentBlueData();
        SoundObjectLibrary sObjLib = data.getSoundObjectLibrary();
        List<Instance> instanceSoundObjects = new ArrayList<Instance>();
        ScoreController.ScoreObjectBuffer buffer
                = ScoreController.getInstance().getScoreObjectBuffer();

        float start = (float) p.x / timeState.getPixelSecond();

        if (timeState.isSnapEnabled()) {
            start = ScoreUtilities.getSnapValueStart(start,
                    timeState.getSnapValue());
        }

        int minLayer = Integer.MAX_VALUE;
        int maxLayer = Integer.MIN_VALUE;

        for (Integer layerIndex : layerIndexes) {
            if (layerIndex < minLayer) {
                minLayer = layerIndex;
            }
            if (layerIndex > maxLayer) {
                maxLayer = layerIndex;
            }
        }

        int numLayers = maxLayer - minLayer + 1;
        
        for(int i = 0; i < numLayers; i++) {
            pObj.newLayerAt(-1);
        }

        for (int i = 0; i < scoreObjects.size(); i++) {
            ScoreObject scoreObj = scoreObjects.get(i);
            int layerIndex = layerIndexes.get(i);
            SoundLayer layer = pObj.get(layerIndex - minLayer);
            
            SoundObject clone = (SoundObject) scoreObj.clone();
            layer.add(clone);

            if (clone instanceof Instance) {
                instanceSoundObjects.add((Instance) clone);
            }

        }

        sObjLib.checkAndAddInstanceSoundObjects(instanceSoundObjects);

        pObj.normalizeSoundObjects();

        pObj.setStartTime(start);
        ((SoundLayer) targetLayer).add(pObj);
    }

    @Override
    public boolean isEnabled() {
        for (ScoreObject scoreObj : scoreObjects) {
            if (!(scoreObj instanceof SoundObject)) {
                return false;
            }
        }

        return scoreObjects.size() > 0 && targetLayer != null && targetLayer.accepts(
                pObj);
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        Point p = actionContext.lookup(Point.class);
        ScoreController scoreController = ScoreController.getInstance();

        return new PasteAsPolyObjectAction(
                scoreController.getScoreObjectBuffer().scoreObjects,
                scoreController.getScoreObjectBuffer().layerIndexes,
                p,
                actionContext.lookup(TimeState.class),
                scoreController.getScore().getGlobalLayerForY(p.y)
        );
    }
}
