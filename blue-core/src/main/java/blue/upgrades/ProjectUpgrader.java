/*
 * blue - object composition environment for csound
 * Copyright (c) 2012 Steven Yi (stevenyi@gmail.com)
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
package blue.upgrades;

import electric.xml.Element;

/**
 * Class used to upgrade projects. VersionString given is for what version this
 * project upgrades to.
 * 
 * @author stevenyi
 */
public abstract class ProjectUpgrader {
    private final ProjectVersion version;
    
    public ProjectUpgrader(String versionString) {
        this.version = ProjectVersion.parseVersion(versionString);
    }
    
    
    /**
     * Code that upgrades project by modifying XML data before BlueData is loaded.
     * 
     * @param data
     * @return 
     */
    public abstract boolean performUpgrade(Element data);
    

    /**
     * @return the version
     */
    public ProjectVersion getVersion() {
        return version;
    }
}
