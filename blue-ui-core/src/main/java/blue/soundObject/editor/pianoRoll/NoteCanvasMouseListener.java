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
package blue.soundObject.editor.pianoRoll;

import blue.BlueSystem;
import blue.event.SelectionEvent;
import blue.event.SelectionListener;
import blue.soundObject.pianoRoll.PianoNote;
import blue.ui.utilities.UiUtilities;
import blue.utility.ScoreUtilities;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

/**
 * @author steven
 */
public class NoteCanvasMouseListener implements MouseListener,
        MouseMotionListener {

    private static final int EDGE = 5;

    private static final int OS_CTRL_KEY = BlueSystem.getMenuShortcutKey();

    private final Cursor RESIZE_CURSOR = Cursor
            .getPredefinedCursor(Cursor.E_RESIZE_CURSOR);

    private final Cursor NORMAL_CURSOR = Cursor
            .getPredefinedCursor(Cursor.DEFAULT_CURSOR);

    private Rectangle scrollRect = new Rectangle(0, 0, 1, 1);

    private PianoRollCanvas canvas;

    private ArrayList<SelectionListener<PianoNoteView>> listeners = new ArrayList<>();

    private Point start = null;

    private JPopupMenu pasteMenu = new JPopupMenu();
    int pasteX = 0;
    int pasteY = 0;

    public NoteCanvasMouseListener(PianoRollCanvas canvas) {
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        addSelectionListener(canvas.noteBuffer);
        this.canvas = canvas;

        pasteMenu.add(new AbstractAction("Paste") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(PianoRollCanvas.NOTE_COPY_BUFFER.size() > 0) {
                    pasteNotes(pasteX, pasteY);
                }
            }
        });
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        canvas.requestFocus();
        Component comp = canvas.getComponentAt(e.getPoint());

        if (UiUtilities.isRightMouseButton(e)) {
            if (comp instanceof PianoNoteView
                    && canvas.noteBuffer.contains(comp)) {

                canvas.showPopup(e.getX(), e.getY());
            } else if (!PianoRollCanvas.NOTE_COPY_BUFFER.isEmpty()) {
                pasteX = e.getX();
                pasteY = e.getY();
                pasteMenu.show(canvas, pasteX, pasteY);
            }
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (comp instanceof PianoNoteView) {
                PianoNoteView noteView = (PianoNoteView) comp;

                SelectionEvent<PianoNoteView> selEvt = null;

                if (canvas.getCursor() == RESIZE_CURSOR) {
                    selEvt = new SelectionEvent<>(noteView,
                            SelectionEvent.SELECTION_SINGLE);
                    fireSelectionEvent(selEvt);
                    canvas.noteBuffer.startResize();
                    start = new Point(
                            noteView.getX() + noteView.getWidth(), e.getY());
                } else if (canvas.noteBuffer.contains(noteView)) {
                    start = e.getPoint();
                    canvas.noteBuffer.startMove();
                } else {
                    start = null;

                    if (e.isShiftDown() && canvas.getCursor() == NORMAL_CURSOR
                            && canvas.noteBuffer.size() > 0) {
                        selEvt = new SelectionEvent<>(noteView,
                                SelectionEvent.SELECTION_ADD);
                    } else {
                        selEvt = new SelectionEvent<>(noteView,
                                SelectionEvent.SELECTION_SINGLE);
                    }

                    fireSelectionEvent(selEvt);
                }

            } else if (((e.getModifiers() & OS_CTRL_KEY) == OS_CTRL_KEY) && !e.isShiftDown()) {
                if (PianoRollCanvas.NOTE_COPY_BUFFER.isEmpty()) {
                    return;
                }

                int x = e.getX();
                int y = e.getY();

                pasteNotes(x, y);

            } else if (e.isShiftDown() && ((e.getModifiers() & OS_CTRL_KEY) != OS_CTRL_KEY)) {
                fireSelectionEvent(new SelectionEvent<>(null,
                        SelectionEvent.SELECTION_CLEAR));
                // start = null;

                int x = e.getX();
                int y = e.getY();

                double startTime = (double) x / canvas.p.getPixelSecond();
                double duration = 5.0f / canvas.p.getPixelSecond();

                if (canvas.p.isSnapEnabled()) {
                    double snapValue = canvas.p.getSnapValue();
                    startTime = ScoreUtilities.getSnapValueStart(startTime, snapValue);

                    duration = canvas.p.getSnapValue();
                }

                PianoNoteView noteView = canvas.addNote(startTime, y);
                SelectionEvent<PianoNoteView> selEvt = new SelectionEvent<>(noteView,
                        SelectionEvent.SELECTION_ADD);

                noteView.getPianoNote().setDuration(duration);

                start = new Point(noteView.getX() + noteView.getWidth(), y);

                fireSelectionEvent(selEvt);

                canvas.noteBuffer.startResize();
                canvas.setCursor(RESIZE_CURSOR);

            } else {
                fireSelectionEvent(new SelectionEvent<>(null,
                        SelectionEvent.SELECTION_CLEAR));
                start = null;

                canvas.marquee.setStart(e.getPoint());
                canvas.marquee.setVisible(true);
            }
        }
    }

    private void pasteNotes(int x, int y) {
        double startTime = (double) x / canvas.p.getPixelSecond();
        int[] pchBase = canvas.getOctaveScaleDegreeForY(y);
        double timeAdjust = Double.MAX_VALUE;
        int topPitchNum = Integer.MIN_VALUE;
        int bottomPitchNum = Integer.MAX_VALUE;
        int scaleDegrees = canvas.p.getScale().getNumScaleDegrees();
        
        if (canvas.p.isSnapEnabled()) {
            double snapValue = canvas.p.getSnapValue();
            startTime = ScoreUtilities.getSnapValueStart(startTime, snapValue);
        }
        
        for (PianoNote note : PianoRollCanvas.NOTE_COPY_BUFFER) {
            timeAdjust = Math.min(timeAdjust, note.getStart());
            int pitchNum = note.getOctave() * scaleDegrees + note.getScaleDegree();
            topPitchNum = Math.max(topPitchNum, pitchNum);
            bottomPitchNum = Math.min(bottomPitchNum, pitchNum);
        }
        
        fireSelectionEvent(new SelectionEvent<>(null,
                SelectionEvent.SELECTION_CLEAR));
        start = null;
        
        int basePitchNum = pchBase[0] * scaleDegrees + pchBase[1];
        int pitchNumAdjust = basePitchNum - topPitchNum;
        
        for (PianoNote note : PianoRollCanvas.NOTE_COPY_BUFFER) {
            PianoNote copy = new PianoNote(note);
            copy.setStart(startTime + (copy.getStart() - timeAdjust));
            
            int pitchNum = copy.getOctave() * scaleDegrees + copy.getScaleDegree();
            pitchNum += pitchNumAdjust;
            
            copy.setOctave(pitchNum / scaleDegrees);
            copy.setScaleDegree(pitchNum % scaleDegrees);
            
            canvas.addNote(copy);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (canvas.marquee.isVisible()) {
            endMarquee();
        }

        if (start != null) {
            if (!canvas.p.isSnapEnabled()) {
                if (canvas.getCursor() == NORMAL_CURSOR) {
                    canvas.noteBuffer.endMove();
                } else {
                    canvas.noteBuffer.endResize();
                }
            }
            start = null;
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        if (canvas.marquee.isVisible()) {
            canvas.marquee.setDragPoint(e.getPoint());
            checkScroll(e);
            return;
        }

        if (start == null) {
            return;
        }

        if (canvas.getCursor() == RESIZE_CURSOR) {
            resizeNote(e);
        } else if (canvas.getCursor() == NORMAL_CURSOR) {
            moveNotes(e);
        }

        checkScroll(e);
    }

    private void checkScroll(MouseEvent e) {
        Point temp = SwingUtilities.convertPoint(canvas, e.getPoint(), canvas
                .getParent());
        scrollRect.setLocation(temp);
        ((JViewport) (canvas.getParent())).scrollRectToVisible(scrollRect);
    }

    private void moveNotes(MouseEvent e) {
        int diffX = e.getPoint().x - start.x;

        int noteHeight = canvas.getNoteHeight();
        int layerDiff = (e.getPoint().y / noteHeight) - (start.y / noteHeight);

        if (canvas.p.isSnapEnabled()) {
            double timeAdjust = (double) diffX / canvas.p.getPixelSecond();

            double initialStartTime = canvas.noteBuffer.initialStartTimes[0];

            double tempStart = initialStartTime + timeAdjust;

            if (tempStart < 0.0f) {
                timeAdjust = -initialStartTime;
            } else {
                double snappedStart = ScoreUtilities.getSnapValueMove(tempStart,
                        canvas.p.getSnapValue());

                timeAdjust = snappedStart - initialStartTime;
            }

            canvas.noteBuffer.moveByTime(timeAdjust, layerDiff);
        } else {
            canvas.noteBuffer.move(diffX, layerDiff);
        }

    }

    private void resizeNote(MouseEvent e) {
        int mouseX = e.getPoint().x;

        if (canvas.p.isSnapEnabled()) {

            double snapValue = canvas.p.getSnapValue();

            double endTime = ScoreUtilities.getSnapValueMove((double) mouseX / canvas.p.getPixelSecond(),
                    snapValue);

            double minTime = ScoreUtilities.getSnapValueMove(
                    canvas.noteBuffer.initialStartTimes[0] + snapValue / 2,
                    snapValue);

            endTime = (endTime < minTime) ? minTime : endTime;

            double newDuration = endTime - canvas.noteBuffer.initialStartTimes[0];

            canvas.noteBuffer.setDuration(newDuration);

        } else {
            int diffX = mouseX - start.x;

            canvas.noteBuffer.resize(diffX);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        Component comp = canvas.getComponentAt(e.getPoint());
        if (comp instanceof PianoNoteView) {

            if (e.getX() > (comp.getX() + comp.getWidth() - EDGE)) {
                canvas.setCursor(RESIZE_CURSOR);
            } else {
                canvas.setCursor(NORMAL_CURSOR);
            }
        } else {
            canvas.setCursor(NORMAL_CURSOR);
        }
    }

    public void endMarquee() {
        canvas.marquee.setVisible(false);

        Component[] comps = canvas.getComponents();

        fireSelectionEvent(new SelectionEvent<>(null,
                SelectionEvent.SELECTION_CLEAR));

        for (int i = 0; i < comps.length; i++) {
            if (!(comps[i] instanceof PianoNoteView)) {
                continue;
            }

            if (canvas.marquee.intersects((JComponent) comps[i])) {

                boolean isFirst = canvas.noteBuffer.size() == 0;

                int selectionType = isFirst ? SelectionEvent.SELECTION_SINGLE
                        : SelectionEvent.SELECTION_ADD;

                SelectionEvent<PianoNoteView> selectionEvent = new SelectionEvent<>(
                        (PianoNoteView) comps[i],
                        selectionType);

                fireSelectionEvent(selectionEvent);
            }

        }

        canvas.marquee.setSize(1, 1);
        canvas.marquee.setLocation(-1, -1);
    }

    // SELECTION EVENT CODE
    public void fireSelectionEvent(SelectionEvent<PianoNoteView> se) {
        for (SelectionListener<PianoNoteView> listener : listeners) {
            listener.selectionPerformed(se);
        }
    }

    public void addSelectionListener(SelectionListener<PianoNoteView> sl) {
        listeners.add(sl);
    }

    public void removeSelectionListener(SelectionListener<PianoNoteView> sl) {
        listeners.remove(sl);
    }
}
