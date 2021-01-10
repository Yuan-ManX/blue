/*
 * blue - object composition environment for csound
 * Copyright (c) 2000-2004 Steven Yi (stevenyi@gmail.com)
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
package blue.orchestra.editor.blueSynthBuilder.swing;

import blue.components.ValueSlider;
import blue.orchestra.blueSynthBuilder.BSBHSlider;
import blue.utility.NumberUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;

public class BSBHSliderView extends BSBObjectView<BSBHSlider> implements
        PropertyChangeListener {

    private static final int VALUE_DISPLAY_HEIGHT = 30;

    private static final int VALUE_DISPLAY_WIDTH = 50;

    private final ValueSlider valSlider;

    ValuePanel valuePanel = new ValuePanel();

    private boolean updating = false;

    /**
     * @param slider
     */
    public BSBHSliderView(BSBHSlider slider) {
        super(slider);
        
        updating = true;

        valSlider = new ValueSlider();


        setSize(VALUE_DISPLAY_WIDTH + slider.getSliderWidth(),
                VALUE_DISPLAY_HEIGHT);

        valSlider.setOpaque(false);
        valSlider.addChangeListener((ChangeEvent e) -> {
            if (!updating) {
                updateValue();
            }
        });

        updateSliderSettings();

        valuePanel.setPreferredSize(new Dimension(VALUE_DISPLAY_WIDTH,
                VALUE_DISPLAY_HEIGHT));

        this.setLayout(new BorderLayout());
        this.add(valSlider, BorderLayout.CENTER);
        this.add(valuePanel, BorderLayout.EAST);

        updateValueText();

        slider.addPropertyChangeListener(this);
        updating = false;
        
        valuePanel.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if ("value".equals(evt.getPropertyName())) {
                try {
                    float val = Float.parseFloat(valuePanel.getPendingValue());
                    
                    updating = true;
                    
                    BSBHSliderView.this.getBSBObject().setValue(val);
                    
                    updateSliderSettings();
                    updateValueText();
                    
                    updating = false;
                    
                } catch (NumberFormatException nfe) {
                }
            }
        });
    }

    /**
     * @param slider
     */
    private void updateSliderSettings() {
        var slider = getBSBObject();
        double minimum = slider.getMinimum();
        double maximum = slider.getMaximum();
        double value = slider.getValue();
        int resolution;

//        if (slider.getResolution() > 0) {
//            resolution = (int) ((maximum - minimum) / slider.getResolution());
//        } else {
//            resolution = (int) ((maximum - minimum) * 100);
//        }

        valSlider.setMinimum(minimum);
        valSlider.setMaximum(maximum);
//        valSlider.setResolution(resolution);
        valSlider.setValue(value);
    }

    private void updateValueText() {
        double newVal;
        var slider = getBSBObject();
//        if (slider.getResolution() > 0.0f) {
//            newVal = (valSlider.getValue() * slider.getResolution())
//                    + this.slider.getMinimum();
//        } else {
            newVal = (valSlider.getValue() * .01) + slider.getMinimum();
//        }
        String valueStr = NumberUtilities.formatDouble(newVal);
        valuePanel.setValue(valueStr);
    }

    protected void updateValue() {
        double newVal;
        var slider = getBSBObject();

//        if (slider.getResolution() > 0) {
//            newVal = (valSlider.getValue() * slider.getResolution())
//                    + slider.getMinimum();
//        } else {
            newVal = (valSlider.getValue() * .01f) + slider.getMinimum();
//        }

        valuePanel.setValue(NumberUtilities.formatDouble(newVal));
        slider.setValue(newVal);
    }

//    public void setMinimum(float minimum) {
//        if (minimum >= slider.getMaximum()) {
//            JOptionPane.showMessageDialog(null, "Error: Min value "
//                    + "can not be set greater or equals to Max value.",
//                    "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        String retVal = LineBoundaryDialog.getLinePointMethod();
//
//        if (retVal == null) {
//            return;
//        }
//
//        slider.setMinimum(minimum, (retVal == LineBoundaryDialog.TRUNCATE));
//        updateSliderSettings();
//    }
//
//    /** used by BSBHSliderBankView */
//    public void setMinimum(float minimum, boolean truncate) {
//        slider.setMinimum(minimum, truncate);
//        updateSliderSettings();
//    }
//
//    public float getMinimum() {
//        return slider.getMinimum();
//    }
//
//    public float getMaximum() {
//        return slider.getMaximum();
//    }
//
//    public void setMaximum(float maximum) {
//        if (maximum <= slider.getMinimum()) {
//            JOptionPane.showMessageDialog(null, "Error: Max value "
//                    + "can not be set less than or " + "equal to Min value.",
//                    "Error", JOptionPane.ERROR_MESSAGE);
//
//            return;
//        }
//
//        String retVal = LineBoundaryDialog.getLinePointMethod();
//
//        if (retVal == null) {
//            return;
//        }
//
//        slider.setMaximum(maximum, (retVal == LineBoundaryDialog.TRUNCATE));
//        updateSliderSettings();
//    }
//
//    /** used by BSBHSliderBankView */
//    public void setMaximum(float maximum, boolean truncate) {
//        slider.setMaximum(maximum, truncate);
//        updateSliderSettings();
//    }
//
//    public float getResolution() {
//        return slider.getResolution();
//    }
//
//    public void setResolution(float resolution) {
//        updating = true;
//
//        slider.setResolution(resolution);
//        updateSliderSettings();
//        updateValueText();
//
//        updating = false;
//    }
//
//    public int getSliderWidth() {
//        return slider.getSliderWidth();
//    }
//
//    public void setSliderWidth(int sliderWidth) {
//        Dimension d = new Dimension(sliderWidth, valSlider.getHeight());
//
//        valSlider.setPreferredSize(d);
//        valSlider.setSize(d);
//
//        d = new Dimension(sliderWidth + valuePanel.getWidth(), 30);
//
//        this.setPreferredSize(d);
//        this.setSize(d);
//
//        slider.setSliderWidth(sliderWidth);
//
//        revalidate();
//    }


    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getSource() == getBSBObject()) {
            if (pce.getPropertyName().equals("updateValue")) {
                updating = true;

                updateSliderSettings();
                updateValueText();

                updating = false;
            }
        }
    }

//    @Override
//    public void cleanup() {
//        slider.removePropertyChangeListener(this);
//    }
}