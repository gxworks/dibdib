// CdDamm(Func). Original script by Ilmari Karonen (see below).
// For the presented form: Copyright (C) 2020  Roland Horsch <gx work s{at}mai l.de>.
// License: GPLv3-or-later - see LICENSE file (github.com/gxworks/dibdib),
// plus the compatible full texts for further details.
// ABSOLUTELY NO WARRANTY. Formatted by Eclipse.

package io.github.gxworks.joined;

/** Check digit, up to 30 bits.
 * Cmp. https://stackoverflow.com/questions/23431621/extending-the-damm-algorithm-to-base-32
 * (and http://www.hpl.hp.com/techreports/98/HPL-98-135.pdf etc.) @20200516:
 * Python script by Ilmari Karonen, based on Damm's dissertation.
 * Test for n=4 (hex): 5725.
 */
public final class CdDammFunc {
//=====

private static int[] zGfReduction = new int[] { 0, 0, 3, 3, 3, 5, 3, 3, 27, 3, 9, 5, 9, 27, 33,
	3, 43, 9, 9, 39, 9, 5, 3, 33, 27, 9, 27, 39, 3, 5, 3, // 9, 141,
};

/** Calculate Damm check digit for base 2^n, n<=30. */
public static int checkDigit( int[] digits, int bits ) {
	final int modulus = (1 << bits);
	final int mask = modulus | zGfReduction[ bits ];
	int cd = 0;
	for (int digit : digits) {
		cd ^= digit;
		cd <<= 1;
		if (cd >= modulus) {
			cd ^= mask;
		}
	}
	return cd;
}

public static int[][] lookupTable( int bits ) {
	int[] inv = new int[ 1 << bits ];
	for (int i0 = 0; i0 < (1 << bits); ++ i0) {
		inv[ checkDigit( new int[] { i0 }, bits ) ] = i0;
	}
	int[][] out = new int[ 1 << bits ][ 1 << bits ];
	for (int ir = 0; ir < (1 << bits); ++ ir) {
		for (int ic = 0; ic < (1 << bits); ++ ic) {
			out[ ir ][ ic ] = checkDigit( new int[] { inv[ ir ], ic }, bits );
		}
	}
	return out;
}

//=====
}