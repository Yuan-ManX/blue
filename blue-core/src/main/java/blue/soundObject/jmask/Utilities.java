/*
 * blue - object composition environment for csound
 * Copyright (c) 2007 Steven Yi (stevenyi@gmail.com)
 *
 * Based on CMask by Andre Bartetzki
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
package blue.soundObject.jmask;

public class Utilities {
    public static double round(double val, long dec) {
        double erg, p;

        p = Math.pow(10.0, (double) dec);
        erg = Math.floor(val * p + 0.5) / p;
        return erg;
    }

    public static double limit(double val, double lowerBound, double upperBound) {
        if (lowerBound == upperBound) {
            return lowerBound;
        }

        if (val < lowerBound) {
            return lowerBound;
        }

        if (val > upperBound) {
            return upperBound;
        }

        return val;
    }

    public static double wrap(double val, double lowerBound, double upperBound) {
        double xn;

        double range = upperBound - lowerBound;

        if (range == 0.0)
            val = lowerBound;
        if (val > upperBound) {
            xn = val - lowerBound;
            val = lowerBound + Math.IEEEremainder(xn, range);
        }
        if (val < lowerBound) {
            xn = upperBound - val;
            val = upperBound - Math.IEEEremainder(xn, range);
        }
        return val;
    }

    public static double mirror(double val, double lowerBound, double upperBound) {
        double xn;

        double range = upperBound - lowerBound;

        if (range == 0.0)
            val = lowerBound;
        if (val > upperBound) {
            xn = val - lowerBound;
            val = upperBound - Math.IEEEremainder(xn, range);
        }
        if (val < lowerBound) {
            xn = upperBound - val;
            val = lowerBound + Math.IEEEremainder(xn, range);
        }
        return val;
    }

}
