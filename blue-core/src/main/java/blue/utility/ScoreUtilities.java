package blue.utility;

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
import blue.noteProcessor.NoteProcessor;
import blue.noteProcessor.NoteProcessorChain;
import blue.noteProcessor.NoteProcessorException;
import blue.soundObject.*;
import static blue.soundObject.TimeBehavior.NONE;
import static blue.soundObject.TimeBehavior.SCALE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScoreUtilities {

    private static final int RAMP_END_NOT_FOUND = -1;

    private static final int PFIELD_NOT_FLOAT = -2;

    enum ParseState {

        STARTING, COLLECTING
    }

    /**
     * gets will return a NoteList given a score input (String)
     *
     * @throws NoteParseException
     */
    public static NoteList getNotes(String in) throws NoteParseException {
        NoteList notes = new NoteList();
        Note previousNote = null;
        int start = -1, end = -1, lineNumber = 1, len, lastIndex;
        ParseState state = ParseState.STARTING;

        if (in == null || (len = in.length()) == 0) {
            return notes;
        }

        lastIndex = len - 1;

        for (int i = 0; i < len; i++) {
            char c = in.charAt(i);

//            if (c == '\r') {
//                i++;
//                if (i == len) {
//                    break;
//                }
//                c = in.charAt(i);
//            }
            if (c == '\n') {
                lineNumber++;
            }

            switch (state) {
                case STARTING:
                    if (c == ';') {
                        while (i < lastIndex) {
                            i++;
                            if (in.charAt(i) == '\n') {
                                break;
                            }
                        }

                    } else if (c == '/' && (i < len - 2)) {
                        if (in.charAt(i + 1) == '/') {
                            while (i < lastIndex) {
                                i++;
                                if (in.charAt(i) == '\n') {
                                    break;
                                }
                            }
                        } else if (in.charAt(i + 1) == '*') {
                            while (i < lastIndex) {
                                i++;
                                if (in.charAt(i) == '*' && (i < len - 2) && in.charAt(
                                        i + 1) == '/') {
                                    i++;
                                    break;
                                }
                            }
                        }
                    } else if (!Character.isWhitespace(c)) {
                        state = ParseState.COLLECTING;
                        start = i;
                    }

                    break;
                case COLLECTING:

                    if (c == ';') {
                        i = i - 1;
                        end = i;
                    } else if (c == '/' && (i < len - 2)) {
                        if (in.charAt(i + 1) == '/') {
                            end = i - 1;
                            i = end;
                        } else if (in.charAt(i + 1) == '*') {
                            int j = i;

                            // This needs to handle in-line multi-line comments
                            // that don't span lines, as well as reset to handle
                            // multline-comments
                            while (j < lastIndex) {
                                j++;
                                if (in.charAt(j) == '\n') {
                                    i = i - 1;
                                    end = i;
                                }
                                if (in.charAt(j) == '*' && (j < len - 2) && in.charAt(
                                        j + 1) == '/') {
                                    i = j + 1;
                                    break;
                                }
                            }
                        }
                    } else if (c == '\n') {
                        if (i < lastIndex) {
                            c = in.charAt(i + 1);

                            if (Character.isDigit(c) || c == '\"' || c == '.') {
                                break;
                            } else {
                                end = i;
                            }
                        } else {
                            end = i;
                        }
                    } else if (i == lastIndex) {
                        end = i;
                    }

                    if (end > 0) {
                        String noteText = TextUtilities.stripMultiLineComments(
                                in.substring(start, end + 1));

                        Note tempNote = null;
                        try {
                            if (noteText.charAt(0) == 'i') {
                                tempNote = Note.createNote(noteText,
                                        previousNote);
                            }
                        } catch (NoteParseException e) {
                            e.setLineNumber(lineNumber);
                            throw e;
                        }

                        if (tempNote != null) {
                            notes.add(tempNote);
                            previousNote = tempNote;
                        }

                        state = ParseState.STARTING;
                        start = -1;
                        end = -1;
                    }

                    break;
            }
        }

//        String clean = TextUtilities.stripMultiLineComments(in);
//
//        String[] lines = clean.split(("\n"));
//
//        for (int i = 0; i < lines.length; i++) {
//            lines[i] = lines[i].trim();
//        }
//
//        Note previousNote = null;
//        Note tempNote = null;
//
//        for (int i = 0; i < lines.length; i++) {
//            String line = lines[i];
//
//            if (line.length() > 0 && line.startsWith("i")) {
//                line = "i" + line.substring(1);
//
//                if (i < lines.length - 1) {
//                    do {
//                        String nextLine = lines[i + 1].trim();
//
//                        if (nextLine.length() == 0) {
//                            break;
//                        }
//
//                        char c = nextLine.charAt(0);
//                        if (Character.isDigit(c) || c == '\"' || c == '.') {
//                            line += " " + nextLine;
//                            i++;
//                        } else {
//                            break;
//                        }
//                    } while (i < lines.length - 1);
//                }
//
//                try {
//                    tempNote = Note.createNote(line, previousNote);
//                } catch (NoteParseException e) {
//                    e.setLineNumber(i + 1);
//                    throw e;
//                }
//
//                if (tempNote != null) {
//                    notes.add(tempNote);
//                    previousNote = tempNote;
//                }
//            }
//        }
        expandPluses(notes);
        expandRamps(notes);

        return notes;
    }

    private static void expandPluses(NoteList notes) {
        if (notes.size() < 2) {
            return;
        }

        Note previousNote = notes.get(0);

        for (int i = 1; i < notes.size(); i++) {
            // SWAP PLUS IN P2 WITH (P2 + P3) FROM PREVIOUS NOTE

            Note note = notes.get(i);

            if (note.getPField(2).equals("+")) {
                note.setPField(Double.toString(previousNote.getStartTime()
                        + previousNote.getSubjectiveDuration()), 2);
            }

            previousNote = note;
        }
    }

    // START RAMP EXPANDING CODE
    public static void expandRamps(NoteList nl) {
        Note currentNote;

        for (int i = 0; i < nl.size(); i++) {
            currentNote = nl.get(i);

            String pField;

            for (int j = 0; j < currentNote.getPCount(); j++) {
                pField = currentNote.getPField(j + 1);
                if (pField.equals(">") || pField.equals("<")) {
                    // System.out.println("found ramp in field " + j);
                    int headNoteIndex = findRampHead(nl, i, j + 1);
                    int tailNoteIndex = findRampTail(nl, i, j + 1);

                    if (headNoteIndex < 0 || tailNoteIndex < 0) {
                        System.err.println("Error finding ramp end");
                        continue;
                    }

                    Note startNote = nl.get(headNoteIndex);
                    Note endNote = nl.get(tailNoteIndex);

                    // y = mx + b, linear ramp over time
                    double b = Double.parseDouble(startNote.getPField(j + 1));

                    double rise = Double.parseDouble(endNote.getPField(j + 1))
                            - Double.parseDouble(startNote.getPField(j + 1));
                    double run = endNote.getStartTime()
                            - startNote.getStartTime();

                    double m = rise / run;

                    for (int k = headNoteIndex + 1; k < tailNoteIndex; k++) {
                        Note tempNote = nl.get(k);

                        double x = tempNote.getStartTime()
                                - startNote.getStartTime();

                        double newVal = (m * x) + b;

                        tempNote.setPField(Double.toString(newVal), j + 1);
                    }

                    /*
                     * System.out.println( "head: " + headNoteIndex + " tail: " +
                     * tailNoteIndex);
                     */
                }
            }
        }
    }

    private static int findRampHead(NoteList nl, int currentNoteIndex,
            int pFieldNum) {
        int previousNoteIndex = currentNoteIndex - 1;

        if (previousNoteIndex < 0) {
            return RAMP_END_NOT_FOUND;
        }

        String pField;
        try {
            pField = nl.get(previousNoteIndex).getPField(pFieldNum);
        } catch (IndexOutOfBoundsException iobe) {
            return RAMP_END_NOT_FOUND;
        }

        if (pField.equals(">") || pField.equals("<")) {
            return findRampHead(nl, previousNoteIndex, pFieldNum);
        }

        try {
            Double.parseDouble(pField);
        } catch (NumberFormatException nfe) {
            return PFIELD_NOT_FLOAT;
        }

        return previousNoteIndex;

    }

    private static int findRampTail(NoteList nl, int currentNoteIndex,
            int pFieldNum) {
        int nextNote = currentNoteIndex + 1;

        if (nextNote >= nl.size()) {
            return RAMP_END_NOT_FOUND;
        }

        String pField;
        try {
            pField = nl.get(nextNote).getPField(pFieldNum);
        } catch (IndexOutOfBoundsException iobe) {
            return RAMP_END_NOT_FOUND;
        }

        if (pField.equals(">") || pField.equals("<")) {
            return findRampTail(nl, nextNote, pFieldNum);
        }

        try {
            Double.parseDouble(pField);
        } catch (NumberFormatException nfe) {
            return PFIELD_NOT_FLOAT;
        }

        return nextNote;
    }

    // END RAMP EXPANDING CODE
    public static double getTotalDuration(NoteList notes) {
        int size = notes.size();
        double tempValue;
        double max = 0.0f;
        Note tempNote;

        for (int i = 0; i < size; i++) {
            tempNote = notes.get(i);
            tempValue = tempNote.getStartTime()
                    + tempNote.getObjectiveDuration();
            if (max < tempValue) {
                max = tempValue;
            }
        }
        return max;
    }

    /**
     * Finds the startTime of the first soundObject that is *not* a
     * frozenSoundObject. Useful to start processing for always-on instruments
     * only when necessary
     *
     * @param pObj
     * @return
     */
    public static double getProcessingStartTime(PolyObject pObj) {
        List<SoundObject> sObjects = pObj.getSoundObjects(false);
        Collections.sort(sObjects, new Comparator<SoundObject>() {
            @Override
            public int compare(SoundObject s1, SoundObject s2) {
                return (int) (s1.getStartTime() - s2.getStartTime());
            }
        });

        double time = Double.MAX_VALUE;

        SoundObject sObj;
        String className;
        // NoteList nl;

        for (int i = 0; i < sObjects.size(); i++) {

            sObj = sObjects.get(i);

            // System.out.println("StartTime: " + sObj.getStartTime());
            className = sObj.getClass().getName();

            if (className.equals("blue.soundObject.FrozenSoundObject")
                    || className.equals("blue.soundObject.Comment")) {
                continue;
            }

            if (sObj.getStartTime() < time) {
                // nl = sObj.generateNotes();
                // if (nl != null && nl.size() != 0) {
                time = sObj.getStartTime();
                // }
            }

        }

        if (time == Double.MAX_VALUE) {
            time = 0.0f;
        }

        return time;
    }

    public static void scaleScore(NoteList notes, double multiplier) {
        Note tempNote;

        for (int i = 0; i < notes.size(); i++) {
            tempNote = notes.get(i);
            tempNote.setStartTime(tempNote.getStartTime() * multiplier);
            tempNote.setSubjectiveDuration(tempNote.getObjectiveDuration()
                    * multiplier);
        }
    }

    public static void setScoreStart(NoteList notes, double start) {
        for (int i = 0; i < notes.size(); i++) {
            notes.get(i).setStartTime(
                    notes.get(i).getStartTime() + start);
        }
    }

    /**
     * Translates start time of notes so that notes will start from time 0
     *
     * @param notes
     */
    public static void normalizeNoteList(NoteList notes) {
        notes.sort();
        double minStart = notes.get(0).getStartTime();

        Note temp;

        for (int i = 0; i < notes.size(); i++) {
            temp = notes.get(i);
            temp.setStartTime(temp.getStartTime() - minStart);
        }
    }

    public static void applyNoteProcessorChain(NoteList notes,
            NoteProcessorChain npc) throws NoteProcessorException {
        for (int i = 0; i < npc.size(); i++) {
            NoteProcessor np = npc.get(i);
            // try {
            np.processNotes(notes);
            // } catch (NoteProcessorException ex) {
            // JOptionPane.showMessageDialog(
            // null,
            // ex.getMessage(),
            // ex.getProcessorName(),
            // JOptionPane.ERROR_MESSAGE);
            // throw new NoteProcessorRTException("An exception occured while
            // applying a note processor", ex);
            // }
        }
    }

    public static void applyTimeBehavior(NoteList notes, TimeBehavior timeBehavior,
            double subjectiveDuration, double repeatPoint) {

        applyTimeBehavior(notes, timeBehavior, subjectiveDuration, repeatPoint,
                -1.0f);

    }

    public static void applyTimeBehavior(NoteList notes, TimeBehavior timeBehavior,
            double subjectiveDuration, double repeatPoint, double durationForScale) {

        if (notes.size() == 0) {
            return;
        }

        if (timeBehavior == TimeBehavior.SCALE) {
            double dur = durationForScale;

            if (durationForScale < 0.0f) {
                dur = getTotalDuration(notes);
            }

            double multiplier = subjectiveDuration / dur;
            scaleScore(notes, multiplier);
        } else if (timeBehavior == TimeBehavior.REPEAT) {
            
            // Truncates notes to repeat window boundaries
            
            NoteList originalNotes = new NoteList(notes);
            originalNotes.sort();
            
            double objDur = durationForScale;

            if (durationForScale < 0.0f) {
                objDur = getTotalDuration(originalNotes);
            }

            double repeatDur = objDur;

            if (objDur > 0 && repeatPoint > 0.0f) {
                repeatDur = repeatPoint;
            }

            NoteList tempNL = null;

            if (repeatDur <= 0) {
                return;
            }

            notes.clear();
            
            double windowStart = 0.0f;
            double windowEnd = Math.min(repeatDur, subjectiveDuration);

            while (windowStart < subjectiveDuration) {
                tempNL = new NoteList(originalNotes);
                ScoreUtilities.setScoreStart(tempNL, windowStart);
                
                final var end = windowEnd;
                tempNL.removeIf(n -> n.getStartTime() >= end);
                
                for(var note: tempNL) {
                    var noteStart = note.getStartTime();
                    if(noteStart + note.getSubjectiveDuration() > windowEnd) {
                        note.setSubjectiveDuration(windowEnd - noteStart);
                    }
                }
                   
                notes.merge(tempNL);
                windowStart += repeatDur;
                windowEnd = Math.min(windowStart + repeatDur, subjectiveDuration);
            }

        } else if (timeBehavior == TimeBehavior.REPEAT_CLASSIC) {
            NoteList originalNotes = new NoteList(notes);
            originalNotes.sort();

            double objDur = durationForScale;

            if (durationForScale < 0.0f) {
                objDur = getTotalDuration(originalNotes);
            }

            double repeatDur = objDur;

            if (objDur > 0 && repeatPoint > 0.0f) {
                repeatDur = repeatPoint;
            }

            double startVal = 0.0f;

            NoteList tempNL = null;

            if (repeatDur <= 0) {
                return;
            }

            notes.clear();

            while (startVal + repeatDur < subjectiveDuration) {
                tempNL = new NoteList(originalNotes);
                ScoreUtilities.setScoreStart(tempNL, startVal);
                notes.merge(tempNL);
                startVal += repeatDur;
            }

            tempNL = new NoteList(originalNotes);
            Note tempNote = null;

            double remainingDur = subjectiveDuration - startVal;

            for (int i = 0; i < tempNL.size(); i++) {
                tempNote = new Note(tempNL.get(i));
                if (tempNote.getStartTime() + tempNote.getSubjectiveDuration() <= remainingDur) {
                    tempNote.setStartTime(tempNote.getStartTime() + startVal);
                    notes.add(tempNote);
                } else {
                    // stop processing of notes as the rest will be invalid
                    System.out.println("Not emitting note: " + tempNote);
                    break;
                }
            }

        } else if (timeBehavior == TimeBehavior.NONE) {
            return;
        }

    }

    /**
     * **************************************************************
     */
    public static double getMaxTime(SoundObject[] sObjects) {
        double max = 0.0f;

        for (int i = 0; i < sObjects.length; i++) {
            double val = sObjects[i].getStartTime()
                    + sObjects[i].getSubjectiveDuration();
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

    public static double getMaxTime(List<SoundObject> sObjects) {
        double max = 0.0f;

        SoundObject sObj;
        int size = sObjects.size();

        for (int i = 0; i < size; i++) {
            sObj = sObjects.get(i);
            double val = sObj.getStartTime() + sObj.getSubjectiveDuration();
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

    public static double getMaxTimeWithEmptyCheck(List<SoundObject> sObjects) {
        double max = 0.0f;

        SoundObject sObj;
        int size = sObjects.size();

        for (int i = 0; i < size; i++) {
            sObj = sObjects.get(i);

            if (sObj instanceof Comment) {
                continue;
            } else if (sObj instanceof PolyObject) {
                PolyObject pObj = (PolyObject) sObj;
                if (pObj.isScoreGenerationEmpty()) {
                    continue;
                }
            }

            double val = sObj.getStartTime() + sObj.getSubjectiveDuration();
            if (val > max) {
                max = val;
            }

        }
        return max;
    }

    public static double getMinTime(SoundObject[] sObjects) {
        double min = getMaxTime(sObjects);

        for (int i = 0; i < sObjects.length; i++) {
            double val = sObjects[i].getStartTime();
            if (val < min) {
                min = val;
            }
        }
        return min;

    }

    public static double getMinTime(ArrayList sObjects) {
        double min = getMaxTime(sObjects);

        SoundObject sObj;
        int size = sObjects.size();

        for (int i = 0; i < size; i++) {
            sObj = (SoundObject) (sObjects.get(i));
            double val = sObj.getStartTime();
            if (val < min) {
                min = val;
            }
        }
        return min;

    }

    public static String testNotesList(NoteList nl) {
        StringBuilder returnText = new StringBuilder();
        for (int i = 0; i < nl.size(); i++) {
            returnText.append("N").append(i).append(": s>").append(nl.get(i).getStartTime()).append(" d>").append(nl.get(i).getSubjectiveDuration()).append("\n");
        }
        return returnText.toString();
    }

    public static double getBaseTen(String pch) {
        int octave;
        double pitch;

        int index = pch.indexOf('.');

        if (index == -1) {
            octave = Integer.parseInt(pch);
            pitch = 0.0f;
        } else if (index == 0 || index == pch.length() - 1) {
            octave = Integer.parseInt("0" + pch.substring(0, index));
            pitch = Double.parseDouble("0" + pch.substring(index));
        } else {
            octave = Integer.parseInt(pch.substring(0, index));
            pitch = Double.parseDouble(pch.substring(index));
        }

        pitch = pitch * 100;

        return (octave * 12) + pitch;

    }

    public static double getSnapValueStart(double time, double snapValue) {
        return (int) (time / snapValue) * snapValue;
    }

    public static double getSnapValueMove(double time, double snapValue) {
        return Math.round(time / snapValue) * snapValue;
    }

    public static void main(String[] args) {
        NoteList n = new NoteList();

        try {
            n.add(Note.createNote("i1 0 2 3 0"));

            for (int i = 0; i < 10; i++) {
                n.add(Note.createNote("i1 " + (2 + (i * 2)) + " 2 3 <"));
            }

            n.add(Note.createNote("i1 22 2 3 4"));

        } catch (NoteParseException npe) {
            npe.printStackTrace();
        }

        System.out.println("before: \n\n" + n + "\n\n");

        expandRamps(n);

        System.out.println("after: \n\n" + n + "\n\n");
    }
}
