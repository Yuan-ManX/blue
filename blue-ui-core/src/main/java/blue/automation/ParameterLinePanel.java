/*
 * blue - object composition environment for csound
 * Copyright (c) 2000-2006 Steven Yi (stevenyi@gmail.com)
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
package blue.automation;

import blue.BlueSystem;
import blue.components.DragDirection;
import blue.components.lines.Line;
import blue.components.lines.LineEditorDialog;
import blue.components.lines.LinePoint;
import blue.score.TimeState;
import blue.ui.core.score.ModeListener;
import blue.ui.core.score.ModeManager;
import blue.ui.core.score.ScoreController;
import blue.ui.core.score.ScoreMode;
import blue.ui.core.score.ScoreTopComponent;
import blue.ui.core.score.SingleLineScoreSelection;
import blue.ui.utilities.FileChooserManager;
import blue.ui.utilities.UiUtilities;
import blue.utility.NumberUtilities;
import blue.utility.ScoreUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

/**
 * @author steven
 */
public class ParameterLinePanel extends JComponent implements
        TableModelListener, ListDataListener, ListSelectionListener,
        ModeListener {
    
    private static final List<ParameterLinePanel> ALL_PANELS = new ArrayList<>();

    private static final String FILE_BPF_IMPORT = "paramaterLinePanel.bpf_import";

    private static final String FILE_BPF_EXPORT = "paramaterLinePanel.bpf_export";

    private static final Stroke STROKE1 = new BasicStroke(1);

    private static final Stroke STROKE2 = new BasicStroke(2);

    private EditPointsPopup popup = null;

    LinePoint selectedPoint = null;

    double leftBoundaryTime = -1.0, rightBoundaryTime = -1.0;

    TableModelListener lineListener = null;

    ParameterIdList parameterIdList = null;

    ParameterList paramList = null;

    Parameter currentParameter = null;

    private TimeState timeState;

    LineCanvasMouseListener mouseListener = new LineCanvasMouseListener(this);

    ArrayList<double[]> selectionList = new ArrayList<>();

    int mouseDownInitialX = -1;
    
    double initialStartTime = -1.0;

    double transTime = 0;

    private double selectionStartTime = -1;

    private double selectionEndTime = -1;

    private double newSelectionStartTime = -1;

    private double newSelectionEndTime = -1;

    SingleLineScoreSelection selection = SingleLineScoreSelection.getInstance();

    public ParameterLinePanel() {
        lineListener = (TableModelEvent e) -> {
            repaint();
        };

        FileChooserManager fcm = FileChooserManager.getDefault();

        fcm.addFilter(FILE_BPF_IMPORT, new ExtensionFilter(
                "Break Point File", "*.bpf"));

        fcm.addFilter(FILE_BPF_EXPORT, new ExtensionFilter(
                "Break Point File", "*.bpf"));

        fcm.setDialogTitle(FILE_BPF_IMPORT, "Import BPF File");
        fcm.setDialogTitle(FILE_BPF_EXPORT, "Export BPF File");

    }

    public void setTimeState(TimeState timeState) {
        this.timeState = timeState;
    }

    public void setParameterIdList(ParameterIdList paramIdList) {
        if (this.parameterIdList != null) {
            this.parameterIdList.removeListDataListener(this);
            this.parameterIdList.removeListSelectionListener(this);

            if (this.paramList != null) {
                for (Parameter param : this.paramList) {
                    param.getLine().removeTableModelListener(lineListener);
                }
            }
        }

        this.parameterIdList = paramIdList;
        paramList = new ParameterList();

        AutomationManager automationManager = AutomationManager.getInstance();

        for (int i = 0; i < parameterIdList.size(); i++) {
            String paramId = parameterIdList.getParameterId(i);

            Parameter param = automationManager.getParameter(paramId);

            if (param != null) {
                paramList.add(param);
                param.getLine().addTableModelListener(lineListener);
            }
        }

        if (this.parameterIdList != null) {
            this.parameterIdList.addListDataListener(this);
            this.parameterIdList.addListSelectionListener(this);

            int index = this.parameterIdList.getSelectedIndex();
            if (index >= 0) {
                String id = this.parameterIdList.getParameterId(index);
                Parameter param = automationManager.getParameter(id);

                setSelectedParameter(param);
            }
        }

        // setSelectedParameter(null);
        selectedPoint = null;

        modeChanged(ModeManager.getInstance().getMode());
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
     */
    @Override
    public void tableChanged(TableModelEvent e) {
        repaint();
    }

    private void setSelectedParameter(Parameter param) {
        if (currentParameter == param) {
            repaint();
            return;
        }

        currentParameter = param;

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (paramList == null || paramList.size() == 0) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(STROKE2);

        RenderingHints hints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(hints);

        Color currentColor = null;

        ModeManager modeManager = ModeManager.getInstance();
        boolean editing = (modeManager.getMode() == ScoreMode.SINGLE_LINE);
//        boolean multiLineMode = (modeManager.getMode() == ModeManager.MODE_MULTI_LINE);
        boolean multiLineMode = !editing;

        for (Parameter param : paramList) {
            Line line = param.getLine();

            if (multiLineMode) {
                g2d.setColor(line.getColor().darker());
                drawSelectionLine(g2d, param);
            } else if (editing && param == currentParameter) {
                currentColor = line.getColor();
            } else {
                g2d.setColor(line.getColor().darker());
                drawLine(g2d, param, false);
            }
        }

        if (multiLineMode) {
            return;
        }

        if (currentColor != null) {
            g2d.setColor(currentColor);

            if (editing && paramList.containsLine(selection.getSourceLine())) {
                drawSelectionLine(g2d, currentParameter);
            } else {
                drawLine(g2d, currentParameter, true);
            }

        }

        if (editing && selectedPoint != null) {

            double min = currentParameter.getMin();
            double max = currentParameter.getMax();

            int x = doubleToScreenX(selectedPoint.getX());
            int y = doubleToScreenY(selectedPoint.getY(), min, max);

            g2d.setColor(Color.red);
            g2d.fillOval(x - 3, y - 3, 7, 7);
            g2d.setStroke(STROKE1);
            g2d.drawOval(x - 3, y - 3, 7, 7);
            g2d.setStroke(STROKE2);

            if (currentParameter != null) {
                drawPointInformation(g2d, x, y);
            }

        }
    }

    /**
     * @param g2d
     * @param x
     * @param y
     */
    private void drawPointInformation(Graphics2D g2d, int x, int y) {
        g2d.setColor(Color.white);

        // Line currentLine = currentParameter.getLine();
        double yVal = selectedPoint.getY();
        double xVal = selectedPoint.getX();

        String xText = "x: " + NumberUtilities.formatDouble(xVal);
        String yText = "y: " + NumberUtilities.formatDouble(yVal);

        String label = currentParameter.getLabel();

        if (label.length() > 0) {
            yText += " " + label;
        }

        int width = 95;
        int height = 28;

        int xLoc = x + 5;
        int yLoc = y + 5;

        if (x + width > this.getWidth()) {
            xLoc = x - width - 5;
        }

        if (y + height > this.getHeight()) {
            yLoc = y - 14 - 5;
        }

        g2d.drawString(xText, xLoc, yLoc);
        g2d.drawString(yText, xLoc, yLoc + 14);
    }

    private final void drawLine(Graphics g, Parameter p, boolean drawPoints) {
        Line line = p.getLine();

        Rectangle clipBounds = g.getClipBounds();

        if (line.size() == 0) {
            return;
        }

        if (line.size() == 1) {
            LinePoint lp = line.getLinePoint(0);

            double min = line.getMin();
            double max = line.getMax();

            int x = doubleToScreenX(lp.getX());
            int y = doubleToScreenY(lp.getY(), min, max);

            g.drawLine(0, y, getWidth(), y);

            if (drawPoints) {
                paintPoint(g, x, y);
            }
            return;
        }

        if (p.getResolution().doubleValue() <= 0) {

            int[] xValues = new int[line.size()];
            int[] yValues = new int[line.size()];

            double min = line.getMin();
            double max = line.getMax();

            for (int i = 0; i < line.size(); i++) {
                LinePoint point = line.getLinePoint(i);

                xValues[i] = doubleToScreenX(point.getX());
                yValues[i] = doubleToScreenY(point.getY(), min, max);

            }

            g.drawPolyline(xValues, yValues, xValues.length);

            final int lastX = xValues[xValues.length - 1];
            if (lastX < this.getWidth()) {
                int lastY = yValues[yValues.length - 1];
                g.drawLine(lastX, lastY, getWidth(), lastY);
            }

            if (drawPoints) {
                for (int i = 0; i < xValues.length; i++) {
                    paintPoint(g, xValues[i], yValues[i]);
                }
            }

        } else {

            LinePoint previous = null;

            int x, y;

            double min = p.getMin();
            double max = p.getMax();
            BigDecimal resolution = p.getResolution();

            for (int i = 0; i < line.size(); i++) {

                LinePoint point = line.getLinePoint(i);

                x = doubleToScreenX(point.getX());
                y = doubleToScreenY(point.getY(), min, max);

                if (drawPoints) {
                    paintPoint(g, x, y);
                }

                if (previous != null) {

                    double startVal = previous.getY();

                    int startX = doubleToScreenX(previous.getX());
                    int startY = doubleToScreenY(startVal, min, max);

                    int endX = doubleToScreenX(point.getX());
                    int endY = doubleToScreenY(point.getY(), min, max);

                    if (startVal == point.getY()) {
                        g.drawLine(startX, startY, endX, startY);
                        previous = point;
                        continue;
                    }

                    if (previous.getX() == point.getX()) {
                        if (startY != endY) {
                            g.drawLine(startX, startY, startX, endY);
                        }
                        previous = point;
                        continue;
                    }

                    int lastY = startY;
                    int lastX = startX;

                    for (int j = startX; j <= endX; j++) {
                        double timeVal = screenToDoubleX(j);
                        double val = line.getValue(timeVal);

                        int newY = doubleToScreenY(val, min, max);

                        if (endY > startY) {
                            if (newY < startY) {
                                newY = startY;
                            }
                        } else if (newY > startY) {
                            newY = startY;
                        }

                        if (newY != lastY) {
                            g.drawLine(lastX, lastY, j, lastY);
                            g.drawLine(j, lastY, j, newY);

                            lastX = j;
                            lastY = newY;
                        }
                    }
                    if (lastX != endX) {
                        g.drawLine(lastX, lastY, endX, lastY);
                        g.drawLine(endX, lastY, endX, endY);
                    }

                }

                previous = point;
            }

            if (previous.getX() < this.getWidth()) {
                int lastX = doubleToScreenX(previous.getX());
                int lastY = doubleToScreenY(previous.getY(), min, max);

                g.drawLine(lastX, lastY, getWidth(), lastY);
            }

        }

    }

    void paintSelectionPoint(Graphics g, int x, int y, double time,
            double selectionStart, double selectionEnd) {
        if (time >= selectionStart && time <= selectionEnd) {
            Graphics2D g2d = (Graphics2D) g;
            g.fillOval(x - 3, y - 3, 7, 7);
        } else {
            paintPoint(g, x, y);
        }
    }

    /**
     * Returns a line that has the points sorted and those masked removed when
     * handling SelectionMoving; not sure this will be good for long term as it
     * doesn't seem performant but using for initial work and can reevaluate
     * later.
     *
     * @param line
     * @return
     */
    private Line getSelectionSortedLine(Line line) {
        Line retVal = new Line(line);
        double[] selPoints = selectionList.get(0);

        double selectionStartTime = selPoints[0];
        double selectionEndTime = selPoints[1];
        retVal.processLineForSelectionDrag(selectionStartTime, selectionEndTime,
                transTime);

        return retVal;
    }

    /**
     * Returns a line that has the points sorted and those masked removed when
     * handling SelectionScaling; not sure this will be good for long term as it
     * doesn't seem performant but using for initial work and can reevaluate
     * later.
     *
     * @param line
     * @return
     */
    private Line getSelectionScalingSortedLine(Line line) {
        Line retVal = new Line(line);
        processLineForSelectionScale(retVal);

        return retVal;
    }

    private void processLineForSelectionScale(Line line) {
        if (selectionStartTime < 0) {
            return;
        }

        ArrayList<LinePoint> points = new ArrayList<>();

        for (Iterator<LinePoint> iter = line.iterator(); iter.hasNext();) {

            LinePoint lp = iter.next();

            if (line.isFirstLinePoint(lp)) {
                continue;
            }

            double pointTime = lp.getX();

            if (pointTime >= selectionStartTime && pointTime <= selectionEndTime) {
                points.add(lp);
                iter.remove();
            } else if (pointTime >= newSelectionStartTime && pointTime <= newSelectionEndTime) {
                iter.remove();
            }
        }

        double oldStart = selectionStartTime;
        double newStart = newSelectionStartTime;
        double oldRange = selectionEndTime - selectionStartTime;
        double newRange = newSelectionEndTime - newSelectionStartTime;

        for (Iterator<LinePoint> iterator = points.iterator(); iterator.hasNext();) {
            LinePoint lp = iterator.next();

            double newX = (lp.getX() - oldStart);
            newX = (newX / oldRange) * newRange;
            newX += newStart;

            lp.setLocation(newX, lp.getY());
            line.addLinePoint(lp);
        }

        line.sort();
    }

    /* Draws line when in selection mode (MULTILINE, SCORE when SCALING) */
    private final void drawSelectionLine(Graphics g, Parameter p) {
        Line tempLine = p.getLine();

        if (selectionList.size() > 0) {
            tempLine = getSelectionSortedLine(tempLine);
        } else if (newSelectionStartTime >= 0) {
            tempLine = getSelectionScalingSortedLine(tempLine);
        } else if (ModeManager.getInstance().getMode() == ScoreMode.SCORE) {
            drawLine(g, p, false);
            return;
        }

        Color currentColor = g.getColor();

        Rectangle clipBounds = g.getClipBounds();

        if (tempLine.size() == 0) {
            return;
        }

        double pixelSecond = (double) timeState.getPixelSecond();

        double selectionStart;
        double selectionEnd;

        if (newSelectionStartTime >= 0) {
            selectionStart = newSelectionStartTime;
            selectionEnd = newSelectionEndTime;
        } else {
            selectionStart = selection.getStartTime();
            selectionEnd = selection.getEndTime();
        }

        if (tempLine.size() == 1) {
            LinePoint lp = tempLine.getLinePoint(0);

            double min = tempLine.getMin();
            double max = tempLine.getMax();

            int x = doubleToScreenX(lp.getX());
            int y = doubleToScreenY(lp.getY(), min, max);

            g.setColor(currentColor);
            g.drawLine(0, y, getWidth(), y);

            paintSelectionPoint(g, x, y, lp.getX(), selectionStart,
                    selectionEnd);

            return;
        }

        if (p.getResolution().doubleValue() <= 0) {

            int[] xValues = new int[tempLine.size()];
            int[] yValues = new int[tempLine.size()];
            double[] pointX = new double[tempLine.size()];

            double min = tempLine.getMin();
            double max = tempLine.getMax();

            for (int i = 0; i < tempLine.size(); i++) {
                LinePoint point = tempLine.getLinePoint(i);

                pointX[i] = point.getX();
                xValues[i] = doubleToScreenX(pointX[i]);
                yValues[i] = doubleToScreenY(point.getY(), min, max);

            }

            g.drawPolyline(xValues, yValues, xValues.length);

            final int lastX = xValues[xValues.length - 1];
            if (lastX < this.getWidth()) {
                int lastY = yValues[yValues.length - 1];
                g.drawLine(lastX, lastY, getWidth(), lastY);
            }

            for (int i = 0; i < xValues.length; i++) {
                paintSelectionPoint(g, xValues[i], yValues[i],
                        pointX[i], selectionStart, selectionEnd);
            }
        } else {

            LinePoint previous = null;

            int x, y;

            double min = p.getMin();
            double max = p.getMax();
            BigDecimal resolution = p.getResolution();

            for (int i = 0; i < tempLine.size(); i++) {

                LinePoint point = tempLine.getLinePoint(i);

                x = doubleToScreenX(point.getX());
                y = doubleToScreenY(point.getY(), min, max);

                if (previous != null) {

                    double startVal = previous.getY();

                    int startX = doubleToScreenX(previous.getX());
                    int startY = doubleToScreenY(startVal, min, max);

                    int endX = doubleToScreenX(point.getX());
                    int endY = doubleToScreenY(point.getY(), min, max);

                    if (startVal == point.getY()) {
                        g.setColor(currentColor);
                        g.drawLine(startX, startY, endX, startY);
                        previous = point;
                        continue;
                    }

                    if (previous.getX() == point.getX()) {
                        if (startY != endY) {
                            g.setColor(currentColor);
                            g.drawLine(startX, startY, startX, endY);
                        }
                        previous = point;
                        continue;
                    }

                    int lastY = startY;
                    int lastX = startX;

                    for (int j = startX; j <= endX; j++) {
                        double timeVal = screenToDoubleX(j);
                        double val = tempLine.getValue(timeVal);

                        int newY = doubleToScreenY(val, min, max);

                        if (endY > startY) {
                            if (newY < startY) {
                                newY = startY;
                            }
                        } else if (newY > startY) {
                            newY = startY;
                        }

                        if (newY != lastY) {
                            g.setColor(currentColor);
                            g.drawLine(lastX, lastY, j, lastY);
                            g.drawLine(j, lastY, j, newY);

                            lastX = j;
                            lastY = newY;
                        }
                    }
                    if (lastX != endX) {
                        g.setColor(currentColor);
                        g.drawLine(lastX, lastY, endX, lastY);
                        g.drawLine(endX, lastY, endX, endY);
                    }

                }

                paintSelectionPoint(g, x, y, point.getX(), selectionStart,
                        selectionEnd);

                previous = point;
            }

            if (previous.getX() < this.getWidth()) {
                int lastX = doubleToScreenX(previous.getX());
                int lastY = doubleToScreenY(previous.getY(), min, max);

                g.setColor(currentColor);
                g.drawLine(lastX, lastY, getWidth(), lastY);
            }

        }

    }

    private final void paintPoint(Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;

        Color c = g.getColor();
        Stroke s = g2d.getStroke();

        g2d.setStroke(STROKE1);
        g.setColor(Color.BLACK);
        g.fillOval(x - 3, y - 3, 7, 7);
        g.setColor(c);
        g.drawOval(x - 3, y - 3, 7, 7);

        g2d.setStroke(s);
    }

    private int doubleToScreenX(double val) {
        if (timeState == null) {
            return -1;
        }
        return (int) Math.round(val * timeState.getPixelSecond());
    }

    private int doubleToScreenY(double yVal, double min, double max) {
        int height = this.getHeight() - 10;
        double range = max - min;
        double adjustedY = yVal - min;
        double percent = adjustedY / range;

        int y = (int) Math.round(height * (1.0f - percent)) + 5;

        return y;
    }

    private double screenToDoubleX(int val) {
        if (timeState == null) {
            return -1;
        }

        return (double) val / timeState.getPixelSecond();
    }

    private double screenToDoubleY(int val, double min, double max,
            BigDecimal resolution) {
        double height = this.getHeight() - 10;
        double percent = 1 - ((val - 5) / height);
        double range = max - min;

        double value = percent * range;

        if (resolution.doubleValue() > 0.0f) {
            BigDecimal v = new BigDecimal(value).setScale(resolution.scale(),
                    RoundingMode.HALF_UP);
            value = v.subtract(v.remainder(resolution)).doubleValue();
        }

        if (value > range) {
            value = range;
        }

        if (value < 0) {
            value = 0;
        }

        return value + min;
    }

    private void setBoundaryXValues() {

        Line currentLine = currentParameter.getLine();

        if (selectedPoint == currentLine.getLinePoint(0)) {
            leftBoundaryTime = 0;
            rightBoundaryTime = 0;
            return;
        } else if (selectedPoint == currentLine
                .getLinePoint(currentLine.size() - 1)) {
            LinePoint p1 = currentLine.getLinePoint(currentLine.size() - 2);

            leftBoundaryTime = p1.getX();
            rightBoundaryTime = screenToDoubleX(this.getWidth());
            return;
        }

        for (int i = 0; i < currentLine.size(); i++) {
            if (currentLine.getLinePoint(i) == selectedPoint) {
                LinePoint p1 = currentLine.getLinePoint(i - 1);
                LinePoint p2 = currentLine.getLinePoint(i + 1);
                leftBoundaryTime = p1.getX();
                rightBoundaryTime = p2.getX();
                return;
            }
        }

    }

    /**
     * Use by the MouseListener to add points
     *
     * @param x
     * @param y
     * @return
     */
    protected LinePoint insertGraphPoint(int x, int y) {
        LinePoint point = new LinePoint();

        double min = currentParameter.getMin();
        double max = currentParameter.getMax();

        point.setLocation(screenToDoubleX(x), screenToDoubleY(y, min, max,
                currentParameter.getResolution()));

        int index = 1;

        Line currentLine = currentParameter.getLine();

        LinePoint last = currentLine.getLinePoint(currentLine.size() - 1);

        if (point.getX() > last.getX()) {
            currentLine.addLinePoint(point);
            return point;
        }

        for (int i = 0; i < currentLine.size(); i++) {
            LinePoint p1 = currentLine.getLinePoint(i);
            LinePoint p2 = currentLine.getLinePoint(i + 1);

            if (point.getX() >= p1.getX() && point.getX() <= p2.getX()) {
                index = i + 1;
                break;
            }
        }

        currentLine.addLinePoint(index, point);

        return point;
    }

    public LinePoint findGraphPoint(int x, int y) {
        Line currentLine = currentParameter.getLine();

        double min = currentParameter.getMin();
        double max = currentParameter.getMax();

        for (int i = 0; i < currentLine.size(); i++) {
            LinePoint point = currentLine.getLinePoint(i);

            int tempX = doubleToScreenX(point.getX());
            int tempY = doubleToScreenY(point.getY(), min, max);

            if (tempX >= x - 2 && tempX <= x + 2 && tempY >= y - 2
                    && tempY <= y + 2) {
                return point;
            }

        }

        return null;
    }

    public LinePoint findGraphPoint(int x) {
        Line currentLine = currentParameter.getLine();

        for (int i = 0; i < currentLine.size(); i++) {
            LinePoint point = currentLine.getLinePoint(i);

            int tempX = doubleToScreenX(point.getX());

            if (tempX >= x - 2 && tempX <= x + 2) {
                return point;
            }

        }

        return null;
    }

    @Override
    public void addNotify() {
        super.addNotify();

        ModeManager.getInstance().addModeListener(this);
        
        ALL_PANELS.add(this);
    }

    @Override
    public void removeNotify() {
        if (parameterIdList != null) {
            parameterIdList.removeListDataListener(this);
            parameterIdList.removeListSelectionListener(this);
        }

        if (paramList != null) {
            for (Parameter param : paramList) {
                param.getLine().removeTableModelListener(this);
            }
        }

        parameterIdList = null;
        paramList = null;

        ModeManager.getInstance().removeModeListener(this);
        ALL_PANELS.remove(this);
        super.removeNotify();
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        // ignore - not used by parameterIdList
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        if (e.getSource() == parameterIdList) {
            String paramId = parameterIdList.getParameterId(e.getIndex0());
            Parameter param = AutomationManager.getInstance().getParameter(
                    paramId);
            paramList.add(param);
            param.getLine().addTableModelListener(this);
            repaint();
            // if (paramList.size() == 1) {
            // setSelectedParameter(param);
            // }
        }
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        if (e.getSource() == parameterIdList) {

            for (int i = 0; i < paramList.size(); i++) {
                Parameter param = paramList.get(i);

                if (!parameterIdList.contains(param.getUniqueId())) {
                    paramList.remove(i);
                    param.getLine().removeTableModelListener(this);
                    repaint();
                    return;
                }
            }
            //
            // Parameter param = paramList.getParameter(e.getIndex0());
            // paramList.removeParameter(e.getIndex0());
            // param.getLine().removeTableModelListener(this);

            // if (currentParameter == param) {
            // if (paramList.size() > 0) {
            // setSelectedParameter(paramList.getParameter(0));
            // } else {
            // setSelectedParameter(null);
            // }
            // }
        }
    }

    @Override
    public void modeChanged(ScoreMode mode) {
        removeMouseListener(mouseListener);
        removeMouseMotionListener(mouseListener);
        if (mode == ScoreMode.SINGLE_LINE) {
            addMouseListener(mouseListener);
            addMouseMotionListener(mouseListener);
        }
        repaint();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int index = e.getFirstIndex();

        if (index < 0) {
            setSelectedParameter(null);
            return;
        }

        String paramId = parameterIdList.getParameterId(index);

        Parameter param = AutomationManager.getInstance().getParameter(paramId);
        setSelectedParameter(param);
    }

    class LineCanvasMouseListener extends MouseAdapter {

        ParameterLinePanel lineCanvas;
        DragDirection direction = DragDirection.NOT_SET;
        Point pressPoint = null;
        boolean verticalShift = false;
        private int initialY;
        boolean justPasted = false;

        LineVerticalShifter vShifter = new LineVerticalShifter();

        public LineCanvasMouseListener(ParameterLinePanel lineCanvas) {
            this.lineCanvas = lineCanvas;
        }

        private boolean timeContains(double time, double start, double end) {
            return time >= start && time <= end;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (ModeManager.getInstance().getMode() != ScoreMode.SINGLE_LINE) {
                return;
            }

            e.consume();
            requestFocus();

            pressPoint = e.getPoint();

            final int x = e.getX();
            final double pixelSecond = (double) timeState.getPixelSecond();
            final double mouseTime = x / pixelSecond;

            if (paramList.containsLine(selection.getSourceLine())) {

                if (SwingUtilities.isLeftMouseButton(e)
                        && !timeContains(mouseTime, selection.getStartTime(), selection.getEndTime())) {
                    transTime = 0.0f;
                    mouseDownInitialX = -1;
                    clearSelectionDragRegions();
                    selection.updateSelection(null, -1.0, -1.0);
                    return;
                } else {

                    if (SwingUtilities.isLeftMouseButton(e)) {

                        mouseDownInitialX = e.getX();
                        transTime = 0.0f;

                        double marqueeLeft = selection.getStartTime();
                        double marqueeRight = selection.getEndTime();
                        
                        initialStartTime = marqueeLeft;

                        if (selectionList.size() == 0) {
                            double[] points = new double[]{
                                marqueeLeft, marqueeRight};
                            selectionList.add(points);
                        } else {
                            double[] points = selectionList.get(0);
                            points[0] = marqueeLeft;
                            points[1] = marqueeRight;
                        }

                        verticalShift = e.isControlDown();

                        if (verticalShift) {
                            vShifter.setup(currentParameter, marqueeLeft, marqueeRight);
                        }

                        initialY = e.getY();
                    }
                    return;

                }
            }

            transTime = 0.0f;
            mouseDownInitialX = -1;
            clearSelectionDragRegions();
            
            selection.updateSelection(null, -1.0, -1.0);

            if (currentParameter == null) {
                return;
            }

            Line currentLine = currentParameter.getLine();

            if (selectedPoint != null) {
                if (UiUtilities.isRightMouseButton(e)) {
                    LinePoint first = currentLine.getLinePoint(0);

                    if (selectedPoint != first) {
                        currentLine.removeLinePoint(selectedPoint);
                        selectedPoint = null;
                    }
                } else {
                    setBoundaryXValues();
                }
            } else if (SwingUtilities.isLeftMouseButton(e)) {

                int start = e.getX();

                double startTime = start / pixelSecond;

                if (timeState.isSnapEnabled() && !(e.isControlDown() && e.isShiftDown())) {
                    startTime = ScoreUtilities.getSnapValueStart(startTime, timeState.getSnapValue());
                }

                final int OS_CTRL_KEY = BlueSystem.getMenuShortcutKey();

                if ((e.getModifiers() & OS_CTRL_KEY) == OS_CTRL_KEY) {
                    ScoreController.getInstance().pasteSingleLine(startTime);
                    justPasted = true;
                    return;

                } else if (e.isShiftDown()) {
                    initialStartTime = startTime;
                    ScoreTopComponent scoreTC = (ScoreTopComponent) WindowManager.getDefault().findTopComponent("ScoreTopComponent");
                    Rectangle rect = new Rectangle(start, 0, 1, getHeight());
                    rect = SwingUtilities.convertRectangle(ParameterLinePanel.this, rect, scoreTC.getScorePanel());
                    scoreTC.getMarquee().setBounds(rect);
                    SingleLineScoreSelection selection
                            = SingleLineScoreSelection.getInstance();
                    selection.updateSelection(currentLine, startTime, startTime);

                } else {
                    selectedPoint = insertGraphPoint(start, e.getY());
                    setBoundaryXValues();
                }
            } else if (UiUtilities.isRightMouseButton(e)) {
                if (popup == null) {
                    popup = new EditPointsPopup();
                }
                popup.setLine(currentLine);
                popup.show((Component) e.getSource(), e.getX(), e.getY());
            }

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            direction = DragDirection.NOT_SET;
            vShifter.cleanup();
            boolean didVerticalShift = verticalShift;
            verticalShift = false;
            justPasted = false;

            if (ModeManager.getInstance().getMode() != ScoreMode.SINGLE_LINE) {
                return;
            }

            e.consume();

            if (currentParameter == null) {
                return;
            }

            if (selectedPoint == null && !didVerticalShift
                    && paramList.containsLine(selection.getSourceLine())
                    && selectionList.size() > 0
                    && SwingUtilities.isLeftMouseButton(e)) {

                double[] selPoints = selectionList.get(0);

                double selectionStartTime = selPoints[0];
                double selectionEndTime = selPoints[1];
                currentParameter.getLine().processLineForSelectionDrag(
                        selectionStartTime, selectionEndTime, transTime);
            }

            clearSelectionDragRegions();
            transTime = 0.0f;

            currentParameter.getLine().stripTimeDeadPoints();
            
            repaint();

        }

        private double getInitialStartTime() {
            if (selectionList.size() == 0) {
                return 0;
            }

            double retVal = Double.MAX_VALUE;

            for (int i = 0; i < selectionList.size(); i++) {
                double[] points = selectionList.get(i);
                if (points[0] < retVal) {
                    retVal = points[0];
                }
            }

            return retVal;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (ModeManager.getInstance().getMode() != ScoreMode.SINGLE_LINE) {
                return;
            }

            e.consume();

            if (currentParameter == null || justPasted) {
                return;
            }

            // check if selection currently is for line that is contained in this panel
            if (paramList.containsLine(selection.getSourceLine())) {
                int x = e.getX();
                double pixelSecond = (double) timeState.getPixelSecond();

                if (verticalShift) {

                    double height = getHeight() - 10;
                    double range = currentParameter.getMax() - currentParameter.getMin();
                    double percent = (initialY - e.getY()) / height;

                    double amount = percent * range;

                    vShifter.processVShift(amount);

                } else if (mouseDownInitialX > 0) { // MOVING OF SELECTION
                    if (SwingUtilities.isLeftMouseButton(e)) {

                        transTime = (x - mouseDownInitialX)/ pixelSecond;

                        double newTime = initialStartTime + transTime;

                        if (timeState.isSnapEnabled() && !e.isControlDown()) {
                            newTime = ScoreUtilities.getSnapValueMove(newTime,
                                    timeState.getSnapValue());
                            transTime = newTime - getInitialStartTime();
                        }

                        if (newTime < 0) {
                            transTime -= newTime;
                            newTime = 0;
                        }
                        
                        double endTime = newTime + (selection.getEndTime() - selection.getStartTime());

                        SingleLineScoreSelection selection
                                = SingleLineScoreSelection.getInstance();
                        selection.updateSelection(currentParameter.getLine(), newTime, endTime);
                    }
                } else {
                    if (x < 0) {
                        x = 0;
                    }

                    double startTime = initialStartTime;
                    double endTime = x / pixelSecond;
                    
                    if (timeState.isSnapEnabled() && !e.isControlDown()) {
                        endTime = ScoreUtilities.getSnapValueMove(endTime,
                                timeState.getSnapValue());
                    }
                    
                    if(endTime < startTime) {
                        double temp = startTime;
                        startTime = endTime;
                        endTime = temp;
                    }
                    
                    SingleLineScoreSelection selection
                            = SingleLineScoreSelection.getInstance();
                    selection.updateSelection(currentParameter.getLine(), startTime, endTime);
                }

                // check if there is a selected point, which means we're dragging a point
            } else if (selectedPoint != null) {

                int x = e.getX();
                int y = e.getY();

                if (direction == DragDirection.NOT_SET) {
                    int magx = Math.abs(x - (int) pressPoint.getX());
                    int magy = Math.abs(y - (int) pressPoint.getY());

                    direction = (magx > magy) ? DragDirection.LEFT_RIGHT
                            : DragDirection.UP_DOWN;
                }

                if (e.isControlDown()) {
                    if (direction == DragDirection.LEFT_RIGHT) {
                        y = (int) pressPoint.getY();
                    } else {
                        x = (int) pressPoint.getX();
                    }
                }

                int topY = 5;
                int bottomY = getHeight() - 5;

                if (y < topY) {
                    y = topY;
                } else if (y > bottomY) {
                    y = bottomY;
                }

                double pixelSecond = (double) timeState.getPixelSecond();
                double dragTime = x / pixelSecond;

                if (timeState.isSnapEnabled() && !e.isControlDown()) {
                    dragTime = ScoreUtilities.getSnapValueMove(dragTime,
                            timeState.getSnapValue());
                }
                
                dragTime = Math.max(leftBoundaryTime, 
                        Math.min(rightBoundaryTime, dragTime));

                double min = currentParameter.getMin();
                double max = currentParameter.getMax();

                if (selectedPoint != null) {
                    selectedPoint.setLocation(dragTime,
                            screenToDoubleY(y, min, max, currentParameter
                                    .getResolution()));
                }
            }
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (ModeManager.getInstance().getMode() != ScoreMode.SINGLE_LINE) {
                return;
            }

            if (currentParameter == null) {
                return;
            }

            int x = e.getX();
            int y = e.getY();

            LinePoint foundPoint = findGraphPoint(x, y);

            if (foundPoint != null) {
                if (selectedPoint != foundPoint) {
                    selectedPoint = foundPoint;
                    repaint();
                }
            } else if (selectedPoint != null) {
                selectedPoint = null;
                repaint();
            }
        }

    }

    /* MULTILINE MODE */
//    public void addSelectionDragRegion(double startTime, double endTime) {
//        selectionList.add(new double[] {startTime, endTime});
//    }
    public void setSelectionDragRegion(double startTime, double endTime) {
        if (selectionList.size() == 0) {
            selectionList.add(new double[2]);
        }

        double[] points = selectionList.get(0);
        points[0] = startTime;
        points[1] = endTime;
        repaint();
    }

    public void clearSelectionDragRegions() {
        selectionList.clear();
        repaint();
    }

    public void setMultiLineMouseTranslation(double transTime) {
        this.transTime = transTime;
        repaint();
    }

    public void commitMultiLineDrag() {
        if (this.paramList != null
                && selectionList.size() > 0) {
            double[] selPoints = selectionList.get(0);

            double selectionStartTime = selPoints[0];
            double selectionEndTime = selPoints[1];

            for (int i = 0; i < this.paramList.size(); i++) {
                Parameter param = paramList.get(i);

                param.getLine().processLineForSelectionDrag(
                        selectionStartTime, selectionEndTime, transTime);
            }
        }
//        clearSelectionDragRegions();
        transTime = 0.0f;
    }

    /* SCORE MODE*/
    public void initiateScoreScale(double startTime, double endTime) {
        this.selectionStartTime = startTime;
        this.newSelectionStartTime = startTime;
        this.selectionEndTime = endTime;
        this.newSelectionEndTime = endTime;
    }

    /**
     * Used by SCORE mode for scaling points when soundObject is scaled
     *
     * @param newSelectionStartX
     */
    public void setScoreScaleStart(double newSelectionStartTime) {
        this.newSelectionStartTime = newSelectionStartTime;
        repaint();
    }

    public void setScoreScaleEnd(double newSelectionEndTime) {
        this.newSelectionEndTime = newSelectionEndTime;
        repaint();
    }

    public void commitScoreScale() {
        if (this.paramList != null) {
            for (int i = 0; i < this.paramList.size(); i++) {
                Parameter param = paramList.get(i);
                processLineForSelectionScale(param.getLine());
            }

            selectionStartTime = selectionEndTime = -1;
            newSelectionStartTime = newSelectionEndTime = -1;
            transTime = 0;
        }
        repaint();
    }

    class EditPointsPopup extends JPopupMenu {

        Line line = null;

        JMenu selectParameterMenu;

        ActionListener paramItemListener;

        Action editPointsAction;

        Action importBPF;

        Action exportBPF;

        public EditPointsPopup() {

            selectParameterMenu = new JMenu("Select Parameter");

            editPointsAction = new AbstractAction("Edit Points") {

                @Override
                public void actionPerformed(ActionEvent e) {
                    Component root = SwingUtilities.getRoot(getInvoker());

                    LineEditorDialog dialog = LineEditorDialog
                            .getInstance(root);

                    dialog.setLine(line);
                    dialog.ask();
                }

            };

            paramItemListener = (ActionEvent e) -> {
                JMenuItem menuItem = (JMenuItem) e.getSource();
                Parameter param = (Parameter) menuItem
                        .getClientProperty("param");

                parameterIdList.setSelectedParameter(param.getUniqueId());
                ParameterLinePanel.this.repaint();
            };

            exportBPF = new AbstractAction("Export BPF") {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if (line != null && line.size() > 0) {
                        File retVal = FileChooserManager.getDefault().showSaveDialog(
                                FILE_BPF_EXPORT, SwingUtilities
                                        .getRoot(ParameterLinePanel.this));

                        if (retVal != null) {
                            File f = retVal;

                            try {
                                try (PrintWriter out = new PrintWriter(
                                        new FileWriter(f))) {
                                    out.print(line.exportBPF());

                                    out.flush();
                                }

                                JOptionPane.showMessageDialog(SwingUtilities
                                        .getRoot(ParameterLinePanel.this),
                                        "Line Exported as: "
                                        + f.getAbsolutePath());

                            } catch (IOException e) {
                                Exceptions.printStackTrace(e);
                            }

                        }
                    }

                }

            };

            importBPF = new AbstractAction("Import BPF") {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if (line != null && line.size() > 0) {
                        File retVal = FileChooserManager.getDefault().showSaveDialog(
                                FILE_BPF_IMPORT, SwingUtilities
                                        .getRoot(ParameterLinePanel.this));

                        if (retVal != null) {
                            File f = retVal;

                            if (!line.importBPF(f)) {
                                JOptionPane.showMessageDialog(SwingUtilities
                                        .getRoot(ParameterLinePanel.this),
                                        "Failed to import BPF from file "
                                        + f.getAbsolutePath());
                            }

                        }
                    }

                }

            };

            this.add(selectParameterMenu);
            this.add(editPointsAction);
            this.addSeparator();
            this.add(importBPF);
            this.add(exportBPF);
        }

        public void repopulate() {
            selectParameterMenu.removeAll();

            if (paramList == null || paramList.size() == 0) {
                return;
            }

            for (int i = 0; i < paramList.size(); i++) {
                Parameter param = paramList.get(i);

                JMenuItem item = new JMenuItem();
                item.setText(param.getName());
                item.setEnabled(param != currentParameter);
                item.putClientProperty("param", param);
                item.addActionListener(paramItemListener);

                selectParameterMenu.add(item);
            }
        }

        public void setLine(Line line) {
            this.line = line;
        }

        @Override
        public void show(Component invoker, int x, int y) {
            if (paramList != null) {
                repopulate();

                editPointsAction.setEnabled(this.line != null);

                boolean bpfEnabled = (this.line != null)
                        && (currentParameter.getResolution().doubleValue() <= 0);

                importBPF.setEnabled(bpfEnabled);
                exportBPF.setEnabled(bpfEnabled);

                super.show(invoker, x, y);
            }
        }

    }

    public static int[] getYHeight(Line line) {
        for(ParameterLinePanel panel : ALL_PANELS) {
            if(panel.paramList.containsLine(line)) {
                return new int[]{ panel.getY(), panel.getHeight()};
            }
        }
        return null;
    }
}
