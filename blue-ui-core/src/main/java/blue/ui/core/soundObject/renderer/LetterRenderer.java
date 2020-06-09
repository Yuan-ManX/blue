package blue.ui.core.soundObject.renderer;

import blue.soundObject.SoundObject;
import blue.ui.core.score.layers.soundObject.SoundObjectView;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * <p>
 * Title: blue
 * </p>
 * <p>
 * Description: an object composition environment for csound
 * </p>
 * <p>
 * Copyright: Copyright (c) 2001
 * </p>
 * <p>
 * Company: steven yi music
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */
public abstract class LetterRenderer extends GenericRenderer {

    private static Font miniFont = new Font("Dialog", Font.BOLD, 10);

    String letter;

    public LetterRenderer(String letter) {
        this.letter = letter;
        this.labelOffset = 13;
    }

    @Override
    public void render(Graphics graphics, SoundObjectView sObjView,
            int pixelSeconds) {
        super.render(graphics, sObjView, pixelSeconds);

        Graphics2D g = (Graphics2D) graphics;

        Color boxColor;
        Color fontColor;

        SoundObject sObj = sObjView.getSoundObject();

        if (sObjView.isSelected()) {
            boxColor = Color.WHITE;
            fontColor = Color.BLACK;
        } else {
            Color bgColor = sObj.getBackgroundColor();
            boxColor = bgColor.brighter().brighter();

            int total = bgColor.getRed() + bgColor.getGreen()
                    + bgColor.getBlue();

            if (total > 128 * 3) {
                fontColor = Color.black;
            } else {
                fontColor = Color.white;
            }
        }

        // DRAW BOX
        g.setColor(boxColor);
        g.fillRect(2, 5, 9, 9);

        // DRAW LETTER
        g.setColor(fontColor);
        g.setFont(miniFont);
        g.drawString(letter, 3, 13);

    }
}