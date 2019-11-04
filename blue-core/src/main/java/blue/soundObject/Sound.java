/*
 * blue - object composition environment for csound Copyright (c) 2001-2016
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
package blue.soundObject;

import blue.components.lines.SoundObjectParameterLine;
import blue.*;
import blue.automation.Parameter;
import blue.automation.ParameterList;
import blue.components.lines.Line;
import blue.noteProcessor.NoteProcessorChain;
import blue.orchestra.BlueSynthBuilder;
import blue.plugin.SoundObjectPlugin;
import electric.xml.Element;
import electric.xml.Elements;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;

/**
 * Title: blue Description: an object composition environment for csound
 * Copyright: Copyright (c) 2001 Company: steven yi music
 *
 * @author steven yi
 * @created November 11, 2001
 * @version 1.0
 */
@SoundObjectPlugin(displayName = "Sound", live = false, position = 130)
public class Sound extends AbstractSoundObject {

    BlueSynthBuilder bsbObj;
    StringProperty comment;

    ListChangeListener<? super Parameter> paramListListener = e -> {
        while (e.next()) {
            for (Parameter param : e.getAddedSubList()) {
                adjustLineType(param);
            }
        }
    };

    public Sound() {
        setName("Sound");
        comment = new SimpleStringProperty("");
        setBlueSynthBuilder(new BlueSynthBuilder());
    }

    public Sound(Sound sound) {
        super(sound);
        comment = new SimpleStringProperty(sound.getComment());
        setBlueSynthBuilder(new BlueSynthBuilder(sound.bsbObj));
    }

    protected void adjustLineType(Parameter param) {
        final Line line = param.getLine();
        if (!(line instanceof SoundObjectParameterLine)) {
            final SoundObjectParameterLine soundObjectParameterLine
                    = new SoundObjectParameterLine(Sound.this, line);
            param.setLine(soundObjectParameterLine);
        }
    }

    @Override
    public double getObjectiveDuration() {
        return subjectiveDuration;
    }

    @Override
    public NoteProcessorChain getNoteProcessorChain() {
        return null;
    }

    @Override
    public void setNoteProcessorChain(NoteProcessorChain chain) {
    }

    public BlueSynthBuilder getBlueSynthBuilder() {
        return this.bsbObj;
    }

    public void setBlueSynthBuilder(BlueSynthBuilder bsbObj) {
        if (this.bsbObj != null) {
            ParameterList paramList = bsbObj.getParameterList();
            paramList.removeListener(paramListListener);
        }

        ParameterList paramList = bsbObj.getParameterList();
        for(Parameter param : paramList) {
            adjustLineType(param);
        }
        paramList.addListener(paramListListener);

        this.bsbObj = bsbObj;
    }

    public final void setComment(String value) {
        comment.set(value);
    }

    public final String getComment() {
        return comment.get();
    }

    public final StringProperty commentProperty() {
        return comment;
    }

    public NoteList generateNotes(int instrumentNumber, double renderStart, double renderEnd) throws SoundObjectException {
        NoteList n = new NoteList();

        String noteText = "i" + instrumentNumber + "\t" + startTime + "\t"
                + subjectiveDuration;

        Note tempNote = null;

        try {
            tempNote = Note.createNote(noteText);
        } catch (NoteParseException e) {
            throw new SoundObjectException(this, e);
        }

        if (tempNote != null) {
            n.add(tempNote);
        }

        return n;
    }

    @Override
    public int getTimeBehavior() {
        return SoundObject.TIME_BEHAVIOR_NOT_SUPPORTED;
    }

    @Override
    public void setTimeBehavior(int timeBehavior) {
    }

    @Override
    public double getRepeatPoint() {
        return -1.0f;
    }

    @Override
    public void setRepeatPoint(double repeatPoint) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see blue.soundObject.SoundObject#loadFromXML(electric.xml.Element)
     */
    public static SoundObject loadFromXML(Element data,
            Map<String, Object> objRefMap) throws Exception {
        Sound sObj = new Sound();

        SoundObjectUtilities.initBasicFromXML(data, sObj);

        Elements nodes = data.getElements();

        while (nodes.hasMoreElements()) {
            Element node = nodes.next();
            String nodeName = node.getName();
            switch (nodeName) {
                // For backwards compatibility with Blue versions < 2.6.0
                case "instrumentText":
                    sObj.bsbObj.setInstrumentText(node.getTextString());
                    break;
                case "instrument":
                    sObj.setBlueSynthBuilder((BlueSynthBuilder) BlueSynthBuilder.loadFromXML(node));
                    break;
                case "comment":
                    sObj.setComment(node.getTextString());
                    break;
            }

        }

        return sObj;
    }

    /*
     * (non-Javadoc)
     * 
     * @see blue.soundObject.SoundObject#saveAsXML()
     */
    @Override
    public Element saveAsXML(Map<Object, String> objRefMap) {
        Element retVal = SoundObjectUtilities.getBasicXML(this);
        retVal.addElement(bsbObj.saveAsXML());
        retVal.addElement("comment").setText(getComment());
        return retVal;
    }

    @Override
    public NoteList generateForCSD(CompileData compileData, double startTime,
            double endTime) throws SoundObjectException {

        bsbObj.getParameterList().clearCompilationVarNames();
        int instrNum = compileData.addInstrument(bsbObj);

        return generateNotes(instrNum, startTime, endTime);

    }

    @Override
    public Sound deepCopy() {
        Sound sound = new Sound();
        sound.subjectiveDuration = this.subjectiveDuration;
        sound.startTime = this.startTime;
        sound.name = this.name;
        sound.backgroundColor = this.backgroundColor;
        sound.setComment(this.getComment());

        sound.setBlueSynthBuilder(this.bsbObj.deepCopy());

        return sound;
    }

}
