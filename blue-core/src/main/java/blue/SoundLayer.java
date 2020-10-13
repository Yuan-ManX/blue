/*
 * blue - object composition environment for csound Copyright (c) 2001-2003
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

package blue;

/**
 * Title: blue (Object Composition Environment) Description: Copyright:
 * Copyright (c) steven yi Company: steven yi music
 * 
 * @author steven yi
 * @version 1.0
 */

import blue.automation.ParameterIdList;
import blue.noteProcessor.NoteProcessorChain;
import blue.noteProcessor.NoteProcessorException;
import blue.score.ScoreObject;
import blue.score.layers.AutomatableLayer;
import blue.score.layers.ScoreObjectLayer;
import blue.soundObject.NoteList;
import blue.soundObject.SoundObject;
import blue.utility.ObjectUtilities;
import blue.utility.ScoreUtilities;
import electric.xml.Element;
import electric.xml.Elements;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

public final class SoundLayer extends ArrayList<SoundObject> 
        implements ScoreObjectLayer<SoundObject>, AutomatableLayer {

    private transient Vector<PropertyChangeListener> propListeners = null;

    private transient Vector<SoundLayerListener> layerListeners = null;

    private static final Comparator sObjComparator = new Comparator() {

        @Override
        public int compare(Object arg0, Object arg1) {
            SoundObject a = (SoundObject) arg0;
            SoundObject b = (SoundObject) arg1;

            double aStart = a.getStartTime();
            double bStart = b.getStartTime();

            if (aStart > bStart) {
                return 1;
            } else if (aStart < bStart) {
                return -1;
            }

            return 0;

        }

    };

    private final ParameterIdList automationParameters;

    private String name = "";

    private boolean muted = false;

    private boolean solo = false;

    private NoteProcessorChain npc; 

    private int heightIndex = 0;

    public SoundLayer() {
        automationParameters = new ParameterIdList(); 
        npc = new NoteProcessorChain();
    }

    public SoundLayer(SoundLayer sLayer) {
        name = sLayer.name;
        muted = sLayer.muted;
        solo = sLayer.solo;
        npc = new NoteProcessorChain(sLayer.npc);
        heightIndex = sLayer.heightIndex;
        automationParameters = new ParameterIdList(sLayer.automationParameters); 
        for (SoundObject sObj : sLayer) {
            this.add(sObj.deepCopy());
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isSolo() {
        return solo;
    }

    public void setSolo(boolean solo) {
        this.solo = solo;
    }

    @Override
    public void setName(String name) {
        String oldName = this.name;
        this.name = (name == null) ? "" : name;
        
        if(!this.name.equals(oldName)) {
            firePropertyChangeEvent(new PropertyChangeEvent(this, "name",
                    oldName, name));
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ParameterIdList getAutomationParameters() {
        return automationParameters;
    }

    @Override
    public boolean add(SoundObject sObj) {
        super.add(sObj);
        fireSoundObjectAdded(sObj);
        return true;
    }

    @Override
    public boolean remove(ScoreObject sObj) {
        if(super.remove(sObj)) {
            fireSoundObjectRemoved((SoundObject)sObj);
            return true;
        }
       return false; 
    }

    /**
     * called by PolyObject::getMaxTime()
     */

    public double getMaxTime() {
        return ScoreUtilities.getMaxTime(this);
    }

    public static SoundLayer loadFromXML(Element data,
            Map<String, Object> objRefMap) throws Exception {
        SoundLayer sLayer = new SoundLayer();

        sLayer.setName(data.getAttributeValue("name"));
        sLayer.setMuted(Boolean.valueOf(data.getAttributeValue("muted"))
                .booleanValue());
        sLayer.setSolo(Boolean.valueOf(data.getAttributeValue("solo"))
                .booleanValue());

        String heightIndexStr = data.getAttributeValue("heightIndex");
        if (heightIndexStr != null) {
            sLayer.setHeightIndex(Integer.parseInt(heightIndexStr));
        }

        Element npcNode = data.getElement("noteProcessorChain");

        if (npcNode != null) {
            sLayer.setNoteProcessorChain(NoteProcessorChain
                    .loadFromXML(npcNode));
        }

        Elements sObjects = data.getElements("soundObject");

        while (sObjects.hasMoreElements()) {
            Object obj = ObjectUtilities.loadFromXML(sObjects.next(),
                    objRefMap);
            sLayer.add((SoundObject) obj);
        }

        Elements parameters = data.getElements("parameterId");

        while (parameters.hasMoreElements()) {
            String id = parameters.next().getTextString();
            sLayer.automationParameters.addParameterId(id);
        }

        return sLayer;
    }

    public Element saveAsXML(Map<Object, String> objRefMap) {
        Element retVal = new Element("soundLayer");
        retVal.setAttribute("name", this.getName());
        retVal.setAttribute("muted", Boolean.toString(this.isMuted()));
        retVal.setAttribute("solo", Boolean.toString(this.isSolo()));
        retVal.setAttribute("heightIndex", Integer.toString(this
                .getHeightIndex()));

        retVal.addElement(npc.saveAsXML());

        for (SoundObject sObj : this) {
            retVal.addElement(sObj.saveAsXML(objRefMap));
        }

        for (Iterator iter = automationParameters.iterator(); iter.hasNext();) {
            String id = (String) iter.next();
            retVal.addElement("parameterId").setText(id);
        }

        return retVal;
    }

    public NoteProcessorChain getNoteProcessorChain() {
        return npc;
    }

    public void setNoteProcessorChain(NoteProcessorChain npc) {
        this.npc = npc;
    }

    @Override
    public int getHeightIndex() {
        return heightIndex;
    }

    @Override
    public void setHeightIndex(int heightIndex) {
        if (this.heightIndex == heightIndex) {
            return;
        }

        int oldHeight = this.heightIndex;
        this.heightIndex = heightIndex;

        PropertyChangeEvent pce = new PropertyChangeEvent(this, "heightIndex",
                new Integer(oldHeight), new Integer(heightIndex));

        firePropertyChangeEvent(pce);
    }

    public int getSoundLayerHeight() {
        return (heightIndex + 1) * LAYER_HEIGHT;
    }

    /* Property Change Event Code */

    private void firePropertyChangeEvent(PropertyChangeEvent pce) {
        if (propListeners == null) {
            return;
        }

        Iterator iter = new Vector(propListeners).iterator();

        while (iter.hasNext()) {
            PropertyChangeListener listener = (PropertyChangeListener) iter
                    .next();

            listener.propertyChange(pce);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        if (propListeners == null) {
            propListeners = new Vector<>();
        }

        if (propListeners.contains(pcl)) {
            return;
        }

        propListeners.add(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        if (propListeners == null) {
            return;
        }
        propListeners.remove(pcl);
    }

    /* Support for SoundLayerListeners, used by automation */

    public void addSoundLayerListener(SoundLayerListener listener) {
        if (layerListeners == null) {
            layerListeners = new Vector<>();
        }

        layerListeners.add(listener);
    }

    public void removeSoundLayerListener(SoundLayerListener listener) {
        if (layerListeners != null) {
            layerListeners.remove(listener);
        }
    }

    private void fireSoundObjectAdded(SoundObject sObj) {
        if (layerListeners != null) {
            Iterator iter = new Vector(layerListeners).iterator();

            while (iter.hasNext()) {
                SoundLayerListener listener = (SoundLayerListener) iter.next();
                listener.soundObjectAdded(this, sObj);
            }
        }
    }

    private void fireSoundObjectRemoved(SoundObject sObj) {
        if (layerListeners != null) {
            Iterator iter = new Vector(layerListeners).iterator();

            while (iter.hasNext()) {
                SoundLayerListener listener = (SoundLayerListener) iter.next();
                listener.soundObjectRemoved(this, sObj);
            }
        }
    }

    /* CLEANUP */

    public void clearListeners() {
        if (propListeners != null) {
            propListeners.clear();
            propListeners = null;
        }

        if (layerListeners != null) {
            layerListeners.clear();
            layerListeners = null;
        }
    }

    /**
     * Generates notes for the SoundLayer, skipping over soundObjects which do
     * not contribute notes between the startTime and endTime arguments.
     * 
     * StartTime and endTime is adjusted by the PolyObject before passing into
     * SoundLayer if possible, if not possible, will adjust to render everything
     * and filter on top layer.
     */
    public NoteList generateForCSD(CompileData compileData, double startTime, 
            double endTime) throws SoundLayerException {
        
        NoteList notes = new NoteList();
        
        Collections.sort(this, sObjComparator);

        for (SoundObject sObj : this) {
            try {
            
                double sObjStart = sObj.getStartTime();
                double sObjDur = sObj.getSubjectiveDuration();
                double sObjEnd = sObjStart + sObjDur;

                if (sObjEnd > startTime) {
                    if (endTime <= startTime) {

                        double adjustedStart = startTime - sObjStart;
                        if (adjustedStart < 0.0f) {
                            adjustedStart = 0.0f;
                        }

                        notes.merge((sObj).generateForCSD(compileData, adjustedStart, -1.0f));
                    } else if (sObjStart < endTime) {

                        double adjustedStart = startTime - sObjStart;
                        double adjustedEnd = endTime - sObjStart;

                        if (adjustedStart < 0.0f) {
                            adjustedStart = 0.0f;
                        }

                        if (adjustedEnd >= sObjDur) {
                            adjustedEnd = -1.0f;
                        }

                        notes.merge((sObj).generateForCSD(compileData, adjustedStart,
                                adjustedEnd));
                    }
                }
            } catch (Exception e) {
                throw new SoundLayerException(this, "Error in SoundLayer: "
                        + this.getName(), e);
            }
        }

        try {
            notes = ScoreUtilities.applyNoteProcessorChain(notes, this.npc);
        } catch (NoteProcessorException e) {
            throw new SoundLayerException(this, "Error in SoundLayer: "
                    + this.getName(), e);
        }
        

        return notes;
    }

    @Override
    public int getLayerHeight() {
        return LAYER_HEIGHT * (heightIndex + 1);    
    }

    @Override
    public boolean accepts(ScoreObject object) {
        return (object instanceof SoundObject);    
    }

    @Override
    public boolean contains(ScoreObject object) {
        if(!accepts(object)) {
            return false;
        }    

        return super.contains(object);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public void clearScoreObjects() {
        this.clear();
    }

    @Override
    public SoundLayer deepCopy() {
        return new SoundLayer(this);
    }
    
    
}