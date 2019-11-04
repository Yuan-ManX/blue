package blue.noteProcessor;

import blue.BlueSystem;
import blue.plugin.NoteProcessorPlugin;
import blue.soundObject.Note;
import blue.soundObject.NoteList;
import blue.soundObject.NoteParseException;
import electric.xml.Element;

/**
 * Title: blue Description: an object composition environment for csound
 * Copyright: Copyright (c) 2001 Company: steven yi music
 *
 * @author steven yi
 * @version 1.0
 */
@NoteProcessorPlugin(displayName = "InversionProcessor", position = 90)
public class InversionProcessor implements NoteProcessor {

    double value = 10;

    int pfield = 4;

    public InversionProcessor() {
    }

    public InversionProcessor(InversionProcessor ip) {
        value = ip.value;
        pfield = ip.pfield;
    }

    @Override
    public String toString() {
        // return "[add] pfield: " + pfield + " value: " + value;
        return "[inversion]";
    }

    public String getPfield() {
        return Integer.toString(pfield);
    }

    public void setPfield(String pfield) {
        this.pfield = Integer.parseInt(pfield);
    }

    public String getVal() {
        return Double.toString(value);
    }

    public void setVal(String value) {
        this.value = Double.parseDouble(value);
    }

    @Override
    public final void processNotes(NoteList in) throws NoteProcessorException {
        Note temp;
        for (int i = 0; i < in.size(); i++) {
            temp = in.get(i);
            try {
                double fieldVal = Double.parseDouble(temp.getPField(pfield));
                double addVal = -1 * (fieldVal - this.value);
                temp.setPField(Double.toString(this.value + addVal), pfield);
            } catch (NumberFormatException ex) {
                throw new NoteProcessorException(this, BlueSystem
                        .getString("noteProcessorException.pfieldNotDouble"),
                        pfield);
            } catch (Exception ex) {
                throw new NoteProcessorException(this, BlueSystem
                        .getString("noteProcessorException.missingPfield"),
                        pfield);
            }
        }
    }

    public static void main(String args[]) {
        NoteList n = new NoteList();

        for (int i = 0; i < 10; i++) {
            try {
                n.add(Note.createNote("i1 " + (i * 2) + " 2 3 4"));
            } catch (NoteParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println("before: \n\n" + n + "\n\n");

        InversionProcessor ap = new InversionProcessor();
        ap.setPfield("4");
        ap.setVal("1");
        try {
            ap.processNotes(n);
        } catch (NoteProcessorException ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        System.out.println("after: \n\n" + n + "\n\n");
    }

    public static NoteProcessor loadFromXML(Element data) {
        InversionProcessor ip = new InversionProcessor();

        ip.setPfield(data.getElement("pfield").getTextString());
        ip.setVal(data.getElement("value").getTextString());

        return ip;
    }

    /*
     * (non-Javadoc)
     * 
     * @see blue.noteProcessor.NoteProcessor#saveAsXML()
     */
    @Override
    public Element saveAsXML() {
        Element retVal = new Element("noteProcessor");
        retVal.setAttribute("type", this.getClass().getName());

        retVal.addElement("pfield").setText(this.getPfield());
        retVal.addElement("value").setText(this.getVal());

        return retVal;
    }

    @Override
    public InversionProcessor deepCopy() {
        return new InversionProcessor(this);
    }
}
