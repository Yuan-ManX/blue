/*
 * blue - object composition environment for csound
 * Copyright (c) 2000-2007 Steven Yi (stevenyi@gmail.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by  the Free Software Foundation; either version 2 of the License or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; see the file COPYING.LIB.  If not, write to
 * the Free Software Foundation Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307 USA
 */
package blue.orchestra.editor.blueSynthBuilder;

import blue.orchestra.blueSynthBuilder.BSBGraphicInterface;
import blue.orchestra.blueSynthBuilder.Preset;
import blue.orchestra.blueSynthBuilder.PresetGroup;
import java.util.ArrayList;

public class PresetsUtilities {
    public static void synchronizePresets(PresetGroup presetGroup,
            BSBGraphicInterface gInterface) {
        if (presetGroup == null || gInterface == null) {
            System.err.println("Null error in PresetsUtilities");
            return;
        }

        ArrayList subGroups = presetGroup.getSubGroups();
        for (int i = 0; i < subGroups.size(); i++) {
            synchronizePresets((PresetGroup) subGroups.get(i), gInterface);
        }

        ArrayList presets = presetGroup.getPresets();
        for (int i = 0; i < presets.size(); i++) {
            Preset preset = (Preset) presets.get(i);
            preset.synchronizeWithInterface(gInterface);
        }
    }

}
