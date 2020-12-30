/*
 * blue - object composition environment for csound Copyright (c) 2000-2003
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

import blue.*;
import blue.noteProcessor.NoteProcessorChain;
import blue.orchestra.GenericInstrument;
import blue.orchestra.Instrument;
import blue.plugin.SoundObjectPlugin;
import blue.utility.SoundFileUtilities;
import electric.xml.Element;
import java.io.IOException;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * @author Steven Yi
 *
 */
@SoundObjectPlugin(displayName = "AudioFile", live = false, position = 10)
public class AudioFile extends AbstractSoundObject {

    private String soundFileName;

    private String csoundPostCode;

    private boolean useCustomWindowSize = false;

    private int windowSize = 8;

    public AudioFile() {
        this.setName("Audio File");
        soundFileName = "";
        csoundPostCode = "\touts\taChannel1, aChannel1";
    }

    public AudioFile(AudioFile af) {
        super(af);
        soundFileName = af.soundFileName;
        csoundPostCode = af.csoundPostCode;
        useCustomWindowSize = af.useCustomWindowSize;
        windowSize = af.windowSize;
    }

    // TODO - EXCEPTION - Look at Code to determine if Exception is Needed
    public NoteList generateNotes(int instrumentNumber, double renderStart,
            double renderEnd) throws SoundObjectException {
        NoteList n = new NoteList();

        double newDur = subjectiveDuration;

        if (renderEnd > 0 && renderEnd < subjectiveDuration) {
            newDur = renderEnd;
        }

        newDur = newDur - renderStart;

        StringBuilder buffer = new StringBuilder();

        buffer.append("i").append(instrumentNumber);
        buffer.append("\t").append(startTime + renderStart);
        buffer.append("\t").append(newDur);
        buffer.append("\t").append(renderStart);

        Note tempNote = null;

        try {
            tempNote = Note.createNote(buffer.toString());
        } catch (NoteParseException e) {
            throw new SoundObjectException(this, e);
        }
        if (tempNote != null) {
            n.add(tempNote);
        }

        return n;
    }

    public Instrument generateInstrument() {

        String instrumentText = generateInstrumentText();

        if (instrumentText == null) {
            return null;
        }

        GenericInstrument temp = new GenericInstrument();
        temp.setName(this.name);
        temp.setText(instrumentText);

        return temp;
    }

    private String generateInstrumentText() {
        StringBuilder iText = new StringBuilder();
        String channelVariables = getChannelVariables();

        if (channelVariables == null) {
            return null;
        }

        String sfName = getSoundFileName().replace('\\', '/');

        iText.append(channelVariables).append("\tdiskin2\t\"");
        iText.append(sfName);
        iText.append("\", 1, p4\n");
        iText.append(getCsoundPostCode());

        return iText.toString();
    }

    private String getChannelVariables() {

        int numChannels;

        try {
            numChannels = SoundFileUtilities
                    .getNumberOfChannels(getSoundFileName());
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, BlueSystem
                    .getString("soundfile.infoPanel.error.couldNotOpenFile")
                    + " " + getSoundFileName());
            return null;
        } catch (javax.sound.sampled.UnsupportedAudioFileException uae) {
            JOptionPane.showMessageDialog(null, BlueSystem
                    .getString("soundfile.infoPanel.error.unsupportedAudio")
                    + " " + uae.getLocalizedMessage());
            return null;
        }

        if (numChannels <= 0) {
            return null;
        }

        String info = "aChannel1";

        int i = 1;

        while (i < numChannels) {
            i++;
            info += ", aChannel" + i;
        }

        return info;
    }

    public int findPowerOfTwo(double seconds) {
        int sr = 44100;
        int samples = (int) Math.round(seconds * sr);

        int powTwoSamples = 2;

        while (powTwoSamples < samples) {
            powTwoSamples = powTwoSamples * 2;
        }

        return powTwoSamples;
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

    @Override
    public TimeBehavior getTimeBehavior() {
        return TimeBehavior.NOT_SUPPORTED;
    }

    @Override
    public void setTimeBehavior(TimeBehavior timeBehavior) {
    }

    @Override
    public double getRepeatPoint() {
        return -1.0f;
    }

    @Override
    public void setRepeatPoint(double repeatPoint) {
    }

    // METHODS SPECIFIC FOR THIS SOUNDOBJECT
    public String getCsoundPostCode() {
        return csoundPostCode;
    }

    public void setCsoundPostCode(String string) {
        csoundPostCode = string;
    }

    public String getSoundFileName() {
        return soundFileName;
    }

    public void setSoundFileName(String string) {
        soundFileName = string;
    }

    /*
     * (non-Javadoc)
     *
     * @see blue.soundObject.SoundObject#loadFromXML(electric.xml.Element)
     */
    public static SoundObject loadFromXML(Element data,
            Map<String, Object> objRefMap) throws Exception {
        AudioFile aFile = new AudioFile();

        SoundObjectUtilities.initBasicFromXML(data, aFile);

        String sFileName = data.getElement("soundFileName")
                .getTextString();

        if (sFileName != null) {
            aFile.setSoundFileName(sFileName);
        }

        aFile.setCsoundPostCode(data.getElement("csoundPostCode")
                .getTextString());

        return aFile;

    }

    /*
     * (non-Javadoc)
     *
     * @see blue.soundObject.SoundObject#saveAsXML()
     */
    @Override
    public Element saveAsXML(Map<Object, String> objRefMap) {
        Element retVal = SoundObjectUtilities.getBasicXML(this);

        retVal.addElement("soundFileName").setText(this.getSoundFileName());
        retVal.addElement("csoundPostCode").setText(this.getCsoundPostCode());

        return retVal;
    }

    @Override
    public NoteList generateForCSD(CompileData compileData, double startTime,
            double endTime) throws SoundObjectException {
        Instrument instr = this.generateInstrument();
        if (instr == null) {
            throw new RuntimeException(new SoundObjectException(this, BlueSystem
                    .getString("audioFile.couldNotGenerate")
                    + " " + getSoundFileName()));
        }
        int instrNum = compileData.addInstrument(instr);
        NoteList nl = this.generateNotes(instrNum, startTime, endTime);
        return nl;
    }

    @Override
    public AudioFile deepCopy() {
        return new AudioFile(this);
    }

}
