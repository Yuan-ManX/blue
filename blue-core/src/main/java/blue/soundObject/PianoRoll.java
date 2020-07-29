package blue.soundObject;

import blue.score.ScoreObjectEvent;
import blue.*;
import blue.noteProcessor.NoteProcessorChain;
import blue.noteProcessor.NoteProcessorException;
import blue.plugin.SoundObjectPlugin;
import blue.soundObject.pianoRoll.Field;
import blue.soundObject.pianoRoll.FieldDef;
import blue.soundObject.pianoRoll.FieldType;
import blue.soundObject.pianoRoll.PianoNote;
import blue.soundObject.pianoRoll.Scale;
import blue.utility.ScoreUtilities;
import blue.utility.TextUtilities;
import com.sun.javafx.binding.StringFormatter;
import electric.xml.Element;
import electric.xml.Elements;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * @author steven yi
 */
@SoundObjectPlugin(displayName = "PianoRoll", live = true, position = 90)
public class PianoRoll extends AbstractSoundObject implements ListChangeListener<FieldDef> {

    public static final int DISPLAY_TIME = 0;

    public static final int DISPLAY_NUMBER = 1;

    public static final int GENERATE_FREQUENCY = 0;

    public static final int GENERATE_PCH = 1;

    public static final int GENERATE_MIDI = 2;

    private TimeBehavior timeBehavior;

    double repeatPoint = -1.0f;

    private NoteProcessorChain npc = new NoteProcessorChain();

    private Scale scale;

    private ObservableList<PianoNote> notes;

    private String noteTemplate;

    private String instrumentId;

    private int pixelSecond;

    private int noteHeight;

    private transient ArrayList listeners;

    private boolean snapEnabled = false;

    private double snapValue = 1.0f;

    private int timeDisplay = DISPLAY_TIME;

    private int pchGenerationMethod = GENERATE_FREQUENCY;

    private int timeUnit = 5;

    private int transposition = 0;

    private ObservableList<FieldDef> fieldDefinitions;

    ChangeListener<? super Number> fieldDefListener = (obs, old, newVal) -> {
        for (var fd : fieldDefinitions) {
            if (fd.minValueProperty() == obs) {
                for (var pn : notes) {
                    var fields = pn.getFields();
                    var field = fields.stream().filter(v -> v.getFieldDef() == fd).findFirst();
                    field.ifPresent(fld -> fld.setValue(Math.max(newVal.doubleValue(), fld.getValue())));
                }
                break;
            } else if (fd.maxValueProperty() == obs) {
                for (var pn : notes) {
                    var fields = pn.getFields();
                    var field = fields.stream().filter(v -> v.getFieldDef() == fd).findFirst();
                    field.ifPresent(fld -> fld.setValue(Math.min(newVal.doubleValue(), fld.getValue())));

                }
                break;
            }
        }
    };

    public PianoRoll() {
        this.setName("PianoRoll");
        timeBehavior = TimeBehavior.SCALE;
        scale = Scale.get12TET();
        notes = FXCollections.observableArrayList();
        noteTemplate = "i <INSTR_ID> <START> <DUR> <FREQ> <AMP>";
        instrumentId = "1";
        pixelSecond = 64;
        noteHeight = 15;
        fieldDefinitions = FXCollections.observableArrayList();

        // By default, add one field called AMP
        var ampField = new FieldDef();
        ampField.setFieldName("AMP");
        ampField.setFieldType(FieldType.CONTINUOUS);
        fieldDefinitions.add(ampField);

        ampField.minValueProperty().addListener(fieldDefListener);
        ampField.maxValueProperty().addListener(fieldDefListener);

        fieldDefinitions.addListener(this);
    }

    public PianoRoll(PianoRoll pr) {
        super(pr);

        timeBehavior = pr.timeBehavior;
        repeatPoint = pr.repeatPoint;
        npc = new NoteProcessorChain(pr.npc);
        scale = new Scale(pr.scale);

        fieldDefinitions = FXCollections.observableArrayList();

        Map<FieldDef, FieldDef> srcToCloneMap = new HashMap<>();
        
        for (var fieldDef : pr.getFieldDefinitions()) {
            var clone = new FieldDef(fieldDef);
            fieldDefinitions.add(clone);
            clone.minValueProperty().addListener(fieldDefListener);
            clone.maxValueProperty().addListener(fieldDefListener);
            
            srcToCloneMap.put(fieldDef, clone);
        }

        notes = FXCollections.observableArrayList();

        for (PianoNote pn : pr.notes) {
            var newNote = new PianoNote(pn);
            notes.add(newNote);
            
            for(var f : newNote.getFields()) {
                f.setFieldDef(srcToCloneMap.get(f.getFieldDef()));
            }
        }

        noteTemplate = pr.noteTemplate;
        instrumentId = pr.instrumentId;
        pixelSecond = pr.pixelSecond;
        noteHeight = pr.noteHeight;
        snapEnabled = pr.snapEnabled;
        snapValue = pr.snapValue;
        timeDisplay = pr.timeDisplay;
        pchGenerationMethod = pr.pchGenerationMethod;
        timeUnit = pr.timeUnit;
        transposition = pr.transposition;

        fieldDefinitions.addListener(this);
    }

    @Override
    public NoteProcessorChain getNoteProcessorChain() {
        return npc;
    }

    @Override
    public void setNoteProcessorChain(NoteProcessorChain chain) {
        this.npc = chain;
    }

    // TODO - Implement using notes
    @Override
    public double getObjectiveDuration() {
        return this.getSubjectiveDuration();
    }

    public NoteList generateNotes(double renderStart, double renderEnd) throws SoundObjectException {
        NoteList nl = new NoteList();

        String instrId = instrumentId;

        if (instrId != null) {
            instrId = instrId.trim();
        }

        try {
            Integer.parseInt(instrumentId);
        } catch (NumberFormatException nfe) {
            instrId = "\"" + instrId + "\"";
        }

        for (var n : notes) {
            String freq = "";

            int octave = n.getOctave();
            int scaleDegree = n.getScaleDegree() + getTransposition();

            int numScaleDegrees;

            if (getPchGenerationMethod() == GENERATE_MIDI) {
                numScaleDegrees = 12;
            } else {
                numScaleDegrees = scale.getNumScaleDegrees();
            }

            if (scaleDegree >= numScaleDegrees) {
                octave += scaleDegree / numScaleDegrees;
                scaleDegree = scaleDegree % numScaleDegrees;
            }

            if (scaleDegree < 0) {

                int octaveDiff = (scaleDegree * -1) / numScaleDegrees;
                octaveDiff += 1;

                scaleDegree = scaleDegree % numScaleDegrees;

                octave -= octaveDiff;
                scaleDegree = numScaleDegrees + scaleDegree;
            }

            if (this.pchGenerationMethod == GENERATE_FREQUENCY) {
                double f = scale.getFrequency(octave, scaleDegree);
                freq = Double.toString(f);
            } else if (this.pchGenerationMethod == GENERATE_PCH) {
                freq = octave + "." + scaleDegree;
            } else if (this.pchGenerationMethod == GENERATE_MIDI) {
                freq = Integer.toString((octave * 12) + scaleDegree);
            }

            String template = n.getNoteTemplate();

            template = TextUtilities
                    .replaceAll(template, "<INSTR_ID>", instrId);
            template = TextUtilities.replaceAll(template, "<INSTR_NAME>",
                    instrumentId);
            template = TextUtilities.replaceAll(template, "<START>", Double
                    .toString(n.getStart()));
            template = TextUtilities.replaceAll(template, "<DUR>", Double
                    .toString(n.getDuration()));
            template = TextUtilities.replaceAll(template, "<FREQ>", freq);
            
            
            for(var field : n.getFields()) {
                var fieldDef = field.getFieldDef();
                var k = String.format("<%s>", fieldDef.getFieldName());
                var v = (fieldDef.getFieldType() == FieldType.CONTINUOUS) ?
                        Double.toString(field.getValue()) :
                        Long.toString(Math.round(field.getValue()));
                
                template = TextUtilities.replaceAll(template, k, v);
            }

            Note note = null;

            try {
                note = Note.createNote(template);
            } catch (NoteParseException e) {
                throw new SoundObjectException(this, e);
            }

            nl.add(note);
        }

        try {
            ScoreUtilities.applyNoteProcessorChain(nl, this.npc);
        } catch (NoteProcessorException e) {
            throw new SoundObjectException(this, e);
        }

        ScoreUtilities.applyTimeBehavior(nl, this.getTimeBehavior(), this
                .getSubjectiveDuration(), this.getRepeatPoint());

        ScoreUtilities.setScoreStart(nl, startTime);

        return nl;
    }

    @Override
    public TimeBehavior getTimeBehavior() {
        return this.timeBehavior;
    }

    @Override
    public void setTimeBehavior(TimeBehavior timeBehavior) {
        this.timeBehavior = timeBehavior;
    }

    @Override
    public double getRepeatPoint() {
        return this.repeatPoint;
    }

    @Override
    public void setRepeatPoint(double repeatPoint) {
        this.repeatPoint = repeatPoint;

        ScoreObjectEvent event = new ScoreObjectEvent(this,
                ScoreObjectEvent.REPEAT_POINT);

        fireScoreObjectEvent(event);
    }

    public static SoundObject loadFromXML(Element data,
            Map<String, Object> objRefMap) throws Exception {

        PianoRoll p = new PianoRoll();
        p.fieldDefinitions.clear();

        SoundObjectUtilities.initBasicFromXML(data, p);

        Elements nodes = data.getElements();

        Map<String, FieldDef> fieldTypes = new HashMap<>();

        while (nodes.hasMoreElements()) {
            Element e = nodes.next();

            String nodeName = e.getName();
            switch (nodeName) {
                case "noteTemplate":
                    p.setNoteTemplate(e.getTextString());
                    break;
                case "instrumentId":
                    p.setInstrumentId(e.getTextString());
                    break;
                case "scale":
                    p.setScale(Scale.loadFromXML(e));
                    break;
                case "pixelSecond":
                    p.setPixelSecond(Integer.parseInt(e.getTextString()));
                    break;
                case "noteHeight":
                    p.setNoteHeight(Integer.parseInt(e.getTextString()));
                    break;
                case "snapEnabled":
                    p.setSnapEnabled(Boolean.valueOf(e.getTextString())
                            .booleanValue());
                    break;
                case "snapValue":
                    p.setSnapValue(Double.parseDouble(e.getTextString()));
                    break;
                case "timeDisplay":
                    p.setTimeDisplay(Integer.parseInt(e.getTextString()));
                    break;
                case "timeUnit":
                    p.setTimeUnit(Integer.parseInt(e.getTextString()));
                    break;
                case "fieldDef": {
                    var fd = FieldDef.loadFromXML(e);
                    fieldTypes.put(fd.getFieldName(), fd);
                    p.fieldDefinitions.add(fd);
                    break;
                }
                case "pianoNote":
                    // Assumes fieldDefs are loaded prior to pianoNotes
                    p.notes.add(PianoNote.loadFromXML(e, fieldTypes));
                    break;
                case "pchGenerationMethod":
                    p.setPchGenerationMethod(Integer.parseInt(e.getTextString()));
                    break;
                case "transposition":
                    p.setTransposition(Integer.parseInt(e.getTextString()));
                    break;
            }
        }

        return p;
    }

    @Override
    public Element saveAsXML(Map<Object, String> objRefMap) {
        Element retVal = SoundObjectUtilities.getBasicXML(this);

        retVal.addElement("noteTemplate").setText(getNoteTemplate());
        retVal.addElement("instrumentId").setText(getInstrumentId());
        retVal.addElement(scale.saveAsXML());

        retVal.addElement("pixelSecond").setText(
                Integer.toString(this.getPixelSecond()));
        retVal.addElement("noteHeight").setText(
                Integer.toString(this.getNoteHeight()));

        retVal.addElement("snapEnabled").setText(
                Boolean.toString(this.isSnapEnabled()));
        retVal.addElement("snapValue").setText(
                Double.toString(this.getSnapValue()));
        retVal.addElement("timeDisplay").setText(
                Integer.toString(this.getTimeDisplay()));
        retVal.addElement("timeUnit").setText(
                Integer.toString(this.getTimeUnit()));

        retVal.addElement("pchGenerationMethod").setText(
                Integer.toString(this.getPchGenerationMethod()));

        retVal.addElement("transposition").setText(
                Integer.toString(this.getTransposition()));

        for (var fieldDef : fieldDefinitions) {
            retVal.addElement(fieldDef.saveAsXML());
        }

        for (Iterator<PianoNote> iter = notes.iterator(); iter.hasNext();) {
            PianoNote note = iter.next();
            retVal.addElement(note.saveAsXML());
        }

        return retVal;
    }

    /**
     * @return Returns the notes.
     */
    public ObservableList<PianoNote> getNotes() {
        return notes;
    }

    public ObservableList<FieldDef> getFieldDefinitions() {
        return fieldDefinitions;
    }

    /**
     * @return Returns the noteTemplate.
     */
    public String getNoteTemplate() {
        return noteTemplate;
    }

    /**
     * @param noteTemplate The noteTemplate to set.
     */
    public void setNoteTemplate(String noteTemplate) {
        this.noteTemplate = noteTemplate;
    }

    /**
     * @return Returns the scale.
     */
    public Scale getScale() {
        return scale;
    }

    /**
     * @param scale The scale to set.
     */
    public void setScale(Scale scale) {
        PropertyChangeEvent pce = new PropertyChangeEvent(this, "scale",
                this.scale, scale);

        this.scale = scale;

        firePropertyChange(pce);
    }

    /* PROPERTY CHANGE EVENTS */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        checkListenersExists();
        this.listeners.add(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        checkListenersExists();
        this.listeners.remove(listener);
    }

    public void firePropertyChange(PropertyChangeEvent pce) {
        checkListenersExists();

        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            PropertyChangeListener listener = (PropertyChangeListener) iter
                    .next();
            listener.propertyChange(pce);
        }
    }

    private void checkListenersExists() {
        if (listeners == null) {
            listeners = new ArrayList();
        }
    }

    /**
     * @return Returns the pixelSecond.
     */
    public int getPixelSecond() {
        return pixelSecond;
    }

    /**
     * @param pixelSecond The pixelSecond to set.
     */
    public void setPixelSecond(int pixelSecond) {
        PropertyChangeEvent pce = new PropertyChangeEvent(this, "pixelSecond",
                new Integer(this.pixelSecond), new Integer(pixelSecond));

        this.pixelSecond = pixelSecond;

        firePropertyChange(pce);

    }

    /**
     * @return Returns the noteHeight.
     */
    public int getNoteHeight() {
        return noteHeight;
    }

    /**
     * @param pixelSecond The pixelSecond to set.
     */
    public void setNoteHeight(int noteHeight) {
        PropertyChangeEvent pce = new PropertyChangeEvent(this, "noteHeight",
                new Integer(this.noteHeight), new Integer(noteHeight));

        this.noteHeight = noteHeight;

        firePropertyChange(pce);

    }

    /**
     * @return Returns the snapEnabled.
     */
    public boolean isSnapEnabled() {
        return snapEnabled;
    }

    public void setSnapEnabled(boolean snapEnabled) {
        PropertyChangeEvent pce = new PropertyChangeEvent(this, "snapEnabled",
                Boolean.valueOf(this.snapEnabled), Boolean.valueOf(snapEnabled));

        this.snapEnabled = snapEnabled;

        firePropertyChange(pce);
    }

    public double getSnapValue() {
        return this.snapValue;
    }

    public void setSnapValue(double snapValue) {
        PropertyChangeEvent pce = new PropertyChangeEvent(this, "snapValue",
                new Double(this.snapValue), new Double(snapValue));

        this.snapValue = snapValue;

        firePropertyChange(pce);
    }

    public int getTimeDisplay() {
        return timeDisplay;
    }

    public void setTimeDisplay(int timeDisplay) {
        PropertyChangeEvent pce = new PropertyChangeEvent(this, "timeDisplay",
                new Integer(this.timeDisplay), new Integer(timeDisplay));

        this.timeDisplay = timeDisplay;

        firePropertyChange(pce);
    }

    public int getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(int timeUnit) {
        PropertyChangeEvent pce = new PropertyChangeEvent(this, "timeUnit",
                new Integer(this.timeUnit), new Integer(timeUnit));

        this.timeUnit = timeUnit;

        firePropertyChange(pce);
    }

    /**
     * @return Returns the instrumentId.
     */
    public String getInstrumentId() {
        return instrumentId;
    }

    /**
     * @param instrumentId The instrumentId to set.
     */
    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public int getPchGenerationMethod() {
        return pchGenerationMethod;
    }

    public void setPchGenerationMethod(int pchGenerationMethod) {
        PropertyChangeEvent pce = new PropertyChangeEvent(this,
                "pchGenerationMethod", new Integer(this.pchGenerationMethod),
                new Integer(pchGenerationMethod));

        this.pchGenerationMethod = pchGenerationMethod;

        firePropertyChange(pce);
    }

    public int getTransposition() {
        return transposition;
    }

    public void setTransposition(int transposition) {
        this.transposition = transposition;
    }

    @Override
    public NoteList generateForCSD(CompileData compileData, double startTime,
            double endTime) throws SoundObjectException {

        return generateNotes(startTime, endTime);

    }

    @Override
    public PianoRoll deepCopy() {
        return new PianoRoll(this);
    }

    // When there are changes to number of field definitions, ensure that
    // piano notes have values for each field definition
    @Override
    public void onChanged(Change<? extends FieldDef> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                for (var fd : change.getAddedSubList()) {
                    fd.minValueProperty().addListener(fieldDefListener);
                    fd.maxValueProperty().addListener(fieldDefListener);

                    for (var pn : notes) {
                        pn.getFields().add(new Field(fd));
                    }
                }
            } else if (change.wasRemoved()) {
                for (var fd : change.getRemoved()) {
                    fd.minValueProperty().removeListener(fieldDefListener);
                    fd.maxValueProperty().removeListener(fieldDefListener);

                    for (var pn : notes) {
                        pn.getFields().removeIf(field -> field.getFieldDef() == fd);
                    }
                }
            }

        }
    }

//    @Override
//    public int hashCode() {
//        int hash = 7;
//        hash = 37 * hash + Objects.hashCode(this.timeBehavior);
//        hash = 37 * hash + (int) (Double.doubleToLongBits(this.repeatPoint) ^ (Double.doubleToLongBits(this.repeatPoint) >>> 32));
//        hash = 37 * hash + Objects.hashCode(this.npc);
//        hash = 37 * hash + Objects.hashCode(this.scale);
//        hash = 37 * hash + Objects.hashCode(this.notes);
//        hash = 37 * hash + Objects.hashCode(this.noteTemplate);
//        hash = 37 * hash + Objects.hashCode(this.instrumentId);
//        hash = 37 * hash + this.pixelSecond;
//        hash = 37 * hash + this.noteHeight;
//        hash = 37 * hash + (this.snapEnabled ? 1 : 0);
//        hash = 37 * hash + (int) (Double.doubleToLongBits(this.snapValue) ^ (Double.doubleToLongBits(this.snapValue) >>> 32));
//        hash = 37 * hash + this.timeDisplay;
//        hash = 37 * hash + this.pchGenerationMethod;
//        hash = 37 * hash + this.timeUnit;
//        hash = 37 * hash + this.transposition;
//        hash = 37 * hash + Objects.hashCode(this.fieldDefinitions);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final PianoRoll other = (PianoRoll) obj;
//        if (Double.doubleToLongBits(this.repeatPoint) != Double.doubleToLongBits(other.repeatPoint)) {
//            return false;
//        }
//        if (this.pixelSecond != other.pixelSecond) {
//            return false;
//        }
//        if (this.noteHeight != other.noteHeight) {
//            return false;
//        }
//        if (this.snapEnabled != other.snapEnabled) {
//            return false;
//        }
//        if (Double.doubleToLongBits(this.snapValue) != Double.doubleToLongBits(other.snapValue)) {
//            return false;
//        }
//        if (this.timeDisplay != other.timeDisplay) {
//            return false;
//        }
//        if (this.pchGenerationMethod != other.pchGenerationMethod) {
//            return false;
//        }
//        if (this.timeUnit != other.timeUnit) {
//            return false;
//        }
//        if (this.transposition != other.transposition) {
//            return false;
//        }
//        if (!Objects.equals(this.noteTemplate, other.noteTemplate)) {
//            return false;
//        }
//        if (!Objects.equals(this.instrumentId, other.instrumentId)) {
//            return false;
//        }
//        if (this.timeBehavior != other.timeBehavior) {
//            return false;
//        }
//        if (!Objects.equals(this.npc, other.npc)) {
//            return false;
//        }
//        if (!Objects.equals(this.scale, other.scale)) {
//            return false;
//        }
//        if (!Objects.equals(this.notes, other.notes)) {
//            return false;
//        }
//        if (!Objects.equals(this.fieldDefinitions, other.fieldDefinitions)) {
//            return false;
//        }
//        return true;
//    }

}
