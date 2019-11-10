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

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;


/**
 * Utility class to create unique names given a prefix. Will check against a
 * UniqueNameCollection for valid names.
 * 
 * @author Steven
 */
public class UniqueNameManager  {
    private static MessageFormat NAME_FMT = new MessageFormat("{0}{1}");

    private UniqueNameCollection collection = null;

    private String defaultPrefix;

    public void setUniqueNameCollection(UniqueNameCollection collection) {
        this.collection = collection;
    }

    public UniqueNameCollection getUniqueNameCollection() {
        return collection;
    }

    public void setDefaultPrefix(String prefix) {
        this.defaultPrefix = prefix;
    }

    public boolean isUniquelyNamed(BSBObject bsbObj) {
        return isUniquelyNamed(bsbObj, 
                collection == null ? new HashSet<>() : collection.getNames());
    }

    private boolean isUniquelyNamed(BSBObject bsbObj, Set<String> names) {
        String[] keys = bsbObj.getReplacementKeys();

        for (int i = 0; i < keys.length; i++) {
            if (!isUnique(keys[i], names)) {
                return false;
            }
        }
        return true;
    }

    public void setUniqueName(BSBObject bsbObj) {
        String currentName = bsbObj.getObjectName();

        if (currentName == null || currentName.length() == 0) {
            return;
        }

        int nameIndex = 1;

        Object[] vals = new Object[2];
        vals[0] = getPrefix(currentName);
        vals[1] = new Integer(nameIndex);

        Set<String> names = collection.getNames();

        BSBObject clone = bsbObj.deepCopy();

        clone.setObjectName(NAME_FMT.format(vals));

        while (!isUniquelyNamed(clone, names)) {
            vals[1] = new Integer(++nameIndex);
            clone.setObjectName(NAME_FMT.format(vals));
        }

        bsbObj.setObjectName(clone.getObjectName());
    }

    protected String getPrefix(String name) {

        int index;

        for(index = name.length() - 1; index >= 0; index--) {
            if(!Character.isDigit(name.charAt(index))) {
                break;
            }
        }

        if(index < 0) {
            return "";
        }

        return name.substring(0, index + 1);
    }

    public boolean isUnique(String name) {
        return isUnique(name, collection.getNames());
    }

    private boolean isUnique(String name, Set<String> names) {
        return (name == null) || !names.contains(name);
    }

    //
    // public String getUniqueName() {
    // return getUniqueName(defaultPrefix);
    // }
    //
    // public String getUniqueName(String prefix) {
    // Object[] vals = new Object[2];
    // vals[0] = prefix;
    // vals[1] = new Integer(++nameIndex);
    //
    // ArrayList names = collection.getNames();
    //
    // String newName = NAME_FMT.format(vals);
    //
    // while (!isUnique(newName, names)) {
    // vals[1] = new Integer(++nameIndex);
    // newName = NAME_FMT.format(vals);
    // }
    //
    // return newName;
    // }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UniqueNameManager) {
            UniqueNameManager unm = (UniqueNameManager) obj;

            return defaultPrefix.equals(unm.defaultPrefix);
        }
        return false;
    }
}
