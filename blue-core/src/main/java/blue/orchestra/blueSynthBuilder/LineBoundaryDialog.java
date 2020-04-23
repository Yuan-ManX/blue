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
package blue.orchestra.blueSynthBuilder;

import blue.jfx.BlueFX;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javax.swing.JOptionPane;

/**
 * Copied from blue-ui-core to deal with having scaling options called when
 * setting values using getter/setters and PropertySheet. This is definitely not
 * ideal to have UI stuff like this in the blue-core package and requires a
 * better solution. Using it for now...
 *
 * @author stevenyi
 */
public class LineBoundaryDialog {

    public static final String RESCALE = "Rescale";

    public static final String TRUNCATE = "Truncate";

    public static String getLinePointMethod() {
        String retVal = null;

        if (Platform.isFxApplicationThread()) {
            Alert alert = 
            new Alert(Alert.AlertType.NONE,
                    "Choose method for handling line points:", new ButtonType(
                            RESCALE), new ButtonType(TRUNCATE), ButtonType.CANCEL
            );
            alert.setTitle("Line Boundaries Changed");
            BlueFX.style(alert.getDialogPane());
            Optional<ButtonType> bType = alert.showAndWait();
            if(bType.isPresent() && !bType.get().getButtonData().isCancelButton()) {
               retVal = bType.get().getText();
            }
        } else {
            int ret = JOptionPane.showOptionDialog(null,
                    "Choose method for handling line points:",
                    "Line Boundaries Changed", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, new Object[]{RESCALE,
                        TRUNCATE}, RESCALE);

            switch (ret) {
                case 0:
                    retVal = RESCALE;
                    break;
                case 1:
                    retVal = TRUNCATE;
                    break;
            }
        }

        return retVal;
    }
}
