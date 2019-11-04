/*
 * blue - object composition environment for csound
 * Copyright (C) 2013
 * Steven Yi <stevenyi@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package blue.ui.core.score.object.actions;

import blue.ui.core.score.ModeManager;
import blue.ui.core.score.MultiLineScoreSelection;
import blue.ui.core.score.ScoreController;
import static blue.ui.core.score.ScoreMode.MULTI_LINE;
import static blue.ui.core.score.ScoreMode.SCORE;
import static blue.ui.core.score.ScoreMode.SINGLE_LINE;
import blue.ui.core.score.SingleLineScoreSelection;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Blue",
        id = "blue.ui.core.score.actions.CopyAction")
@ActionRegistration(
        displayName = "#CTL_CopyAction")
@Messages("CTL_CopyAction=&Copy")
@ActionReferences({
    @ActionReference(path = "blue/score/actions", position = 210, separatorAfter = 215)
    ,
@ActionReference(path = "blue/score/shortcuts", name = "D-C")
})
public final class CopyAction extends AbstractAction {

    public CopyAction() {
        super(NbBundle.getMessage(CopyAction.class, "CTL_CopyAction"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        final ScoreController scoreController = ScoreController.getInstance();

        switch (ModeManager.getInstance().getMode()) {
            case SCORE:
                ScoreController.getInstance().copyScoreObjects();
                break;
            case SINGLE_LINE:
                ScoreController.getInstance().copySingleLine();
                break;
            case MULTI_LINE:
                scoreController.copyMultiLine();
                break;
        }
    }

    @Override
    public boolean isEnabled() {
        switch (ModeManager.getInstance().getMode()) {
            case SCORE:
                return ScoreController.getInstance().getSelectedScoreObjects().size() > 0;
            case SINGLE_LINE:
                return SingleLineScoreSelection.getInstance().getSourceLine() != null;
            case MULTI_LINE:
                MultiLineScoreSelection selection = MultiLineScoreSelection.getInstance();
                return selection.getSelectedLayers().size() > 0;
        }
        return false;
    }

}
