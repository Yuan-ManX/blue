/*
 * ZakLineObject.java
 *
 * Created on July 14, 2005, 1:56 PM
 */

package blue.soundObject;

import blue.components.lines.Line;
import blue.plugin.SoundObjectPlugin;
import electric.xml.Element;
import electric.xml.Elements;
import java.util.Map;

/**
 * An implementation of AbstractLineObject specifically for lines whose output
 * will be fed into a zak k-rate channel
 *
 * @author mbechard
 */

@SoundObjectPlugin(displayName = "ZakLineObject", live=false, position = 150)
public class ZakLineObject extends AbstractLineObject {

    /** Creates a new instance of ZakLineObject */
    public ZakLineObject() {
        this.setName("ZakLineObject");
    }

    public ZakLineObject(ZakLineObject zlo) {
        super(zlo);
    }

    @Override
    protected String generateLineInstrument(Line line) {
        int channel = line.getChannel();

        StringBuilder buffer = new StringBuilder();

        buffer.append("kphase line p4, p3, p5\n");
        buffer.append("kline\ttablei kphase, p6, 1\n");
        buffer.append("zkw kline, ").append(channel);
        return buffer.toString();
    }

    /* SERIALIZATION */

    /**
     * Load object's state from XML
     */
    public static SoundObject loadFromXML(Element data,
            Map<String, Object> objRefMap) throws Exception {

        ZakLineObject lObj = new ZakLineObject();
        SoundObjectUtilities.initBasicFromXML(data, lObj);

        Elements lines = data.getElements();

        while (lines.hasMoreElements()) {
            Element node = lines.next();
            if (node.getName().equals("zakline")) {
                Line l = Line.loadFromXML(node);
                lObj.getLines().add(l);
            }
        }

        return lObj;
    }

    @Override
    public ZakLineObject deepCopy() {
        return new ZakLineObject(this);
    }

}
