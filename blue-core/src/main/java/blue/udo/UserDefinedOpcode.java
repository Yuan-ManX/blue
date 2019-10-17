/*
 * blue - object composition environment for csound
 * Copyright (c) 2000-2005 Steven Yi (stevenyi@gmail.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by  the Free Software Foundation; either version 2 of the License or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; see the file COPYING.LIB.  If not, write to
 * the Free Software Foundation Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307 USA
 */

package blue.udo;

import electric.xml.Element;
import electric.xml.Elements;

/**
 * @author Steven Yi
 */
public class UserDefinedOpcode {

    public String opcodeName = "newOpcode";

    public transient String commentText = null;

    public String outTypes = "";

    public String inTypes = "";

    public String codeBody = "";

    public String comments = "";

    public UserDefinedOpcode() {}

    public UserDefinedOpcode(UserDefinedOpcode udo) {
        opcodeName = udo.opcodeName;
        outTypes = udo.outTypes;
        inTypes = udo.inTypes;
        codeBody = udo.codeBody;
        comments = udo.comments;
    }


    public static UserDefinedOpcode loadFromXML(Element data) {
        UserDefinedOpcode retVal = new UserDefinedOpcode();

        Elements nodes = data.getElements();

        while (nodes.hasMoreElements()) {
            Element node = nodes.next();

            String val = node.getTextString();

            if (val == null) {
                val = "";
            }
            switch (node.getName()) {
                case "opcodeName":
                    retVal.opcodeName = val;
                    break;
                case "outTypes":
                    retVal.outTypes = val;
                    break;
                case "inTypes":
                    retVal.inTypes = val;
                    break;
                case "codeBody":
                    retVal.codeBody = val;
                    break;
                case "comments":
                    retVal.comments = val;
                    break;
            }
        }

        return retVal;
    }

    public electric.xml.Element saveAsXML() {
        Element retVal = new Element("udo");

        retVal.addElement("opcodeName").setText(opcodeName);
        retVal.addElement("outTypes").setText(outTypes);
        retVal.addElement("inTypes").setText(inTypes);
        retVal.addElement("codeBody").setText(codeBody);
        retVal.addElement("comments").setText(comments);

        return retVal;
    }

    public String generateCode() {
        StringBuilder buffer = new StringBuilder();

        buffer.append("\topcode ").append(opcodeName);
        buffer.append(",").append(outTypes);
        buffer.append(",").append(inTypes);
       
        if(commentText != null) {
            buffer.append(" ; ").append(commentText);
        }
        
        buffer.append("\n");

        // if(inArgs.trim().length() > 0) {
        // buffer.append(inArgs).append(" xin\n");
        // }
        //
        // if(useLocalKsmps) {
        // buffer.append("setksmps ").append(localKsmps).append("\n");
        // }

        buffer.append("\n").append(codeBody).append("\n\n");

        // if(outArgs.trim().length() > 0) {
        // buffer.append("xout ").append(outArgs).append("\n");
        // }

        buffer.append("\tendop");

        return buffer.toString();

    }

    @Override
    public String toString() {
        return opcodeName;
    }

    public String getOpcodeName() {
        return this.opcodeName;
    }

    public void setOpcodeName(String opcodeName) {
        this.opcodeName = opcodeName;
    }

    public static void main(String[] args) {
        UserDefinedOpcode udo = new UserDefinedOpcode();
        udo.opcodeName = "getFrequency";

        udo.outTypes = "i";
        udo.inTypes = "i";

        // udo.useLocalKsmps = false;
        // udo.localKsmps = 1;
        //
        // udo.inArgs = "ipch";
        // udo.outArgs = "iout";

        udo.codeBody = "ipch\t xin\niout	= (ipch < 15 ? cpspch(ipch) : ipch)\n\txout iout	";

        System.out.println(udo.toString());
        // System.out.println(udo.getArgs());
    }

    public boolean isEquivalent(UserDefinedOpcode udo) {
        if (udo == null) {
            return false;
        }
        return (this.inTypes.equals(udo.inTypes)
                && this.outTypes.equals(udo.outTypes) && this.codeBody
                .equals(udo.codeBody));
    }
}
