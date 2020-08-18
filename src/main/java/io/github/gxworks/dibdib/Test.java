// Copyright (C) 2016,2017,2018,2019  Roland Horsch <g x w orks @ma il.de>.
// License: GPLv3-or-later - see LICENSE file (github.com/gxworks/dibdib),
// plus the compatible full texts for further details.
// ABSOLUTELY NO WARRANTY. Formatted by Eclipse.

package io.github.gxworks.dibdib;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import net.sf.dibdib.generic.*;
import net.sf.dibdib.thread_any.*;
import net.sf.dibdib.thread_any.QMapSto.QVal;

/**
 * Dib2x TEST
 */
public enum Test implements Callable<Integer> {
//=====

INSTANCE;

protected static final int maxCount = 1000;
protected static final int maxThreads = 6;

//=====
private static class QMapTester implements Callable<QBaton[]> { //extends QCallable {
//=====

public Mapping[] wArguments;
Mapping[] result;
public boolean print = false;
private int id;
private int count;
private ConcurrentHashMap<QVal, String> map;
private static final ConcurrentHashMap<String, Long> map2 = new ConcurrentHashMap<String, Long>();
private final Random rand = new Random();

public Thread.State start() {
	QResult.getThreadIndex();
	id = QMapSto.string4QVal( wArguments[0].uLabel).charAt( 0) & 0xf;
	count = 0;
	map = new ConcurrentHashMap<QVal, String>();
	return Thread.State.RUNNABLE;
}

public Thread.State step() {
	final QResult pooled = QResult.get8Pool();
	boolean ok = true;
	for (; count < maxCount; ++count) {
		if (0 == (count % 10)) {
			//			if (print) {
			//				System.out.println( "/" + id );
			//			}
			//			Thread.yield();
		}
		if (0 == (count % 50)) {
			System.out.print( "" + id);
		}
		if (print) {
			System.out.print( "" + id);
		}
		char[] dat = new char[rand.nextInt( 5) + 10];
		Arrays.fill( dat, (char) ('0' + dat.length));
		dat[0] = (char) ('0' + (count % 7));
		String str = new String( dat);
		Long cmp = map2.get( str);
		QVal handle = QMapSto.qval4AtomicValue( pooled, str);
		if ((null != cmp) && (cmp != (QVal.asQVal( handle) & 0xfffff))) {
			ok = false;
			System.out.println( "\nxx// " + (QVal.asQVal( handle) & 0xfffff) + ' ' + cmp + ' '
				+ map.get( handle) + " \t" + QMapSto.string4QVal( handle));
		}
		map.put( handle, str);
		map2.put( str, QVal.asQVal( handle) & 0xfffff);
	}
	Thread.yield();
	System.out.println( "\n:" + id + ' ' + map.size());
	for (QVal handle : map.keySet()) {
		if (!(map.get( handle).equals( QMapSto.string4QVal( handle)))) {
			ok = false;
			System.out
				.println( "--- " + (QVal.asQVal( handle) & 0xfffff) + ' ' + map.get( handle) + " \t"
					+ QMapSto.string4QVal( handle));
		}
		if (print) {
			System.out
				.println( "+++ " + (QVal.asQVal( handle) & 0xfffff) + ' ' + map.get( handle) + " \t"
					+ QMapSto.string4QVal( handle));
		}
	}
	result = new Mapping[] { Mapping.make( "" + id + ok + ' ' + map.size(), Mapping.Cats.DEFAULT, null, "" + id) };
	return Thread.State.TERMINATED;
}

@Override
public QBaton[] call() throws Exception {
	QBaton[] out = null;
	start();
	while (null == result) {
		step();
		out = result;
	}
	//	System.out.println( result[0] );
	return out;
}

//=====
}

//=====

public static char[] createRandomString( Random randGen, int len) {
	char[] rand = new char[len]; //88 ];
	for (int i0 = 0; i0 < rand.length; ++i0) {
		int type = (int) (randGen.nextDouble() * 15);
		switch (type) {
		case 0:
			rand[i0] = (0.5 < randGen.nextDouble()) ? '\n' : '\t';
			break;
		case 1:
			rand[i0] = (0.5 < randGen.nextDouble()) ? '\t' : '_';
			break;
		case 2:
			rand[i0] = (char) (1 + 32 * randGen.nextDouble());
			break;
		case 3:
			rand[i0] = (char) (0x20 + 0x20 * randGen.nextDouble());
			break;
		case 4:
			rand[i0] = '_';
			if ((i0 + 2) < rand.length) {
				final char ch0 = (0.3 < randGen.nextDouble()) ? '{' : '[';
				rand[i0++] = ch0;
				rand[i0++] = ch0;
			}
			break;
		case 5:
			final char ch0 = (0.3 < randGen.nextDouble()) ? '}' : ']';
			rand[i0] = ch0;
			if ((i0 + 2) < rand.length) {
				rand[i0++] = ch0;
				rand[i0++] = '_';
			}
			break;
		case 6:
			rand[i0] = (char) (0x7f + 0x90 * randGen.nextDouble());
			break;
		case 7:
			rand[i0] = (char) (0x100 + 0x300 * randGen.nextDouble());
			break;
		case 8:
			rand[i0] = (char) (0x2000 + 0x30 * randGen.nextDouble());
			break;
		default:
			rand[i0] = (char) (0x20 + (0x80 - 0x20) * randGen.nextDouble());
		}
		//	System.out.print( (' ' <= rand[ i0 ]) ? ("" + rand[ i0 ]) : ("^" + (char) (rand[ i0 ] + '@')) );
	}
	return rand;
}

protected boolean test_UtilMisc_createId() throws IOException {
	boolean ok = true;
	String cmp0 = MiscFunc.createId( "a");
	String cmp1 = MiscFunc.createId( "a");
	String cmpx = MiscFunc.createId( "b");
	ok = ok && (cmp0.compareTo( cmp1) < 0);
	ok = ok && (cmp0.length() == cmp1.length());
	ok = ok && (cmp0.substring( cmp0.length() - 4, cmp0.length() - 1)
		.equals( cmp1.substring( cmp1.length() - 4, cmp1.length() - 1)));
	System.out.println( "" + ok + " a a \t" + cmp0 + '\t' + cmp1);
	cmp0 = cmp1;
	cmp1 = cmpx;
	cmpx = MiscFunc.createId( "a");
	ok = ok && (cmp0.substring( cmp0.length() - 4, cmp0.length() - 1)
		.equals( cmpx.substring( cmpx.length() - 4, cmpx.length() - 1)));
	ok = ok && (cmp0.compareTo( cmp1) < 0);
	ok = ok && (cmp0.length() == cmp1.length());
	ok = ok && !(cmp0.substring( cmp0.length() - 4, cmp0.length() - 1)
		.equals( cmp1.substring( cmp1.length() - 4, cmp1.length() - 1)));
	System.out.println( "" + ok + " a b \t" + cmp0 + '\t' + cmp1);
	cmp0 = MiscFunc.createId( "b");
	cmp1 = MiscFunc.createId( "a");
	ok = ok && (cmpx.compareTo( cmp0) < 0);
	ok = ok && (cmp1.substring( cmp1.length() - 4, cmp1.length() - 1)
		.equals( cmpx.substring( cmpx.length() - 4, cmpx.length() - 1)));
	ok = ok && (cmp0.compareTo( cmp1) < 0);
	ok = ok && (cmp0.length() == cmp1.length());
	ok = ok && !(cmp0.substring( cmp0.length() - 4, cmp0.length() - 1)
		.equals( cmp1.substring( cmp1.length() - 4, cmp1.length() - 1)));
	System.out.println( "" + ok + " b a \t" + cmp0 + '\t' + cmp1);
	cmp0 = MiscFunc.createId( "a");
	cmp1 = MiscFunc.createId( "b");
	cmpx = cmp0.substring( 0, 5) + (char) (cmp0.charAt( 5) + 2) + cmp0.substring( 6);
	MiscFunc.initLastId( cmpx);
	cmp1 = MiscFunc.createId( "a");
	ok = ok && (cmpx.compareTo( cmp1) < 0);
	ok = ok && (cmp0.compareTo( cmp1) < 0);
	ok = ok && (cmp0.length() == cmp1.length());
	ok = ok && !(cmp0.substring( 4, cmp0.length() - 1).equals( cmp1.substring( 4, cmp1.length() - 1)));
	System.out.println( "" + ok + " x a \t" + cmpx + '\t' + cmp1);
	cmp0 = MiscFunc.createId( "a");
	cmpx = cmp0.substring( 0, 5) + "001" + (char) (cmp0.charAt( 6) + 2) + cmp0.substring( 7);
	MiscFunc.initLastId( cmpx);
	cmp1 = MiscFunc.createId( "a");
	ok = ok && (cmpx.compareTo( cmp1) < 0);
	ok = ok && (cmp0.length() == cmp1.length());
	ok = ok && (cmp0.substring( 0, 6).equals( cmp1.substring( 0, 6)));
	System.out.println( "" + ok + " x a \t" + cmpx + '\t' + cmp1);
	cmp0 = MiscFunc.createId( "a");
	cmpx = cmp0.substring( 0, 5) + (char) (cmp0.charAt( 6) + 2) + cmp0.substring( 7);
	cmpx += "001";
	MiscFunc.initLastId( cmpx);
	cmp1 = MiscFunc.createId( "a");
	ok = ok && (cmp0.compareTo( cmp1) < 0);
	ok = ok && (cmp0.length() < cmp1.length());
	ok = ok && (cmp0.substring( cmp0.length() - 4, cmp0.length() - 1)
		.equals( cmp1.substring( cmp1.length() - 4, cmp1.length() - 1)));
	System.out.println( "" + ok + " x a \t" + cmpx + '\t' + cmp1);
	cmpx = MiscFunc.createId( "aa");
	cmp0 = MiscFunc.createId( "b");
	cmp1 = MiscFunc.createId( "aa");
	ok = ok && (cmpx.compareTo( cmp0) < 0);
	ok = ok && (cmp1.substring( cmp1.length() - 4, cmp1.length() - 1)
		.equals( cmpx.substring( cmpx.length() - 4, cmpx.length() - 1)));
	ok = ok && (cmp0.compareTo( cmp1) < 0);
	ok = ok && (cmp0.length() == cmp1.length());
	System.out.println( "" + ok + " aa aa \t" + cmpx + '\t' + cmp1);
	cmpx = MiscFunc.createId( "aa");
	cmp0 = MiscFunc.createId( "b");
	cmp1 = MiscFunc.createId( "a");
	ok = ok && (cmpx.compareTo( cmp0) < 0);
	ok = ok && (cmp1.substring( 0, 10).equals( cmpx.substring( 0, 10)));
	ok = ok && (cmp0.compareTo( cmp1) < 0);
	ok = ok && (cmp0.length() == cmp1.length());
	System.out.println( "" + ok + " aa a \t" + cmpx + '\t' + cmp1);
	return ok;
}

protected boolean test_Util() {
	boolean ok = true;
	char[] conc = new char[0x101];
	for (char ch = 0; ch < 0x100; ++ch) {
		conc[ch] = (char) ((0 == (ch & 1)) ? (ch & 0x7f) : (0xa0 | ((ch & 0xff) >>> 1)));
		byte[] str = StringFunc.bytesUtf8( "" + ch);
		byte[] ascii = MiscFunc.asciiCompressed4Bytes( str);
		byte[] cx = MiscFunc.bytes4AsciiCompressed( ascii);
		String sx = StringFunc.string4Utf8( cx);
		if ((1 == sx.length()) && (sx.charAt( 0) == ch)) {
			System.out.print( (char) ('@' | (ch & 0x3f)));
		} else {
			System.out.print( "\n!! " + (int) ch);
			ok = false;
		}
		//		byte[] b64x = MiscFunc.base64x4Bytes( Arrays.copyOf( conc, ch ) );
	}
	System.out.println( "");
	String str = new String( conc);
	byte[] utf8 = StringFunc.bytesUtf8( str);
	if (!str.equals( StringFunc.string4Utf8( utf8))) {
		ok = false;
	}
	byte[] bad = StringFunc.bytesAnsi( str);
	if (!str.equals( StringFunc.string4Utf8( bad))) {
		ok = false;
	}
	for (int i0 = 0; i0 < bad.length; i0 += 2) {
		bad[i0] = (byte) (bad[i0] ^ 0xa6);
		conc[i0] = (char) (conc[i0] ^ 0xa6);
	}
	str = StringFunc.string4Utf8( bad);
	if (str.length() != bad.length) {
		ok = false;
	} else {
		for (int i0 = 0; i0 < bad.length; ++i0) {
			if (0x100 >= str.charAt( i0)) {
				if (str.charAt( i0) != conc[i0]) {
					ok = false;
				}
			}
		}
	}
	for (int i0 = 1; i0 < bad.length; i0 += 2) {
		bad[i0 - 1] = (byte) ((bad[i0 - 1] & 0xf3) | 0xc3);
		bad[i0] = (byte) ((bad[i0] & 0xbf) + (bad[i0] & 0x3));
	}
	String sx = StringFunc.string4Utf8( bad);
	if (str.length() <= sx.length()) {
		ok = false;
	} else {
		for (int i0 = 0; i0 < sx.length(); ++i0) {
			if (0x7f > sx.charAt( i0)) {
				ok = false;
			}
		}
	}
	return ok;
}

protected boolean test_QStr() {
	boolean ok = true;
	final long shashA = QStrFunc.shashBits4Literal( "A", true);
	final long shashB = QStrFunc.shashBits4Literal( "B", true);
	final long shashZ = QStrFunc.shashBits4Literal( "Z", true);
	final long shashAs = QStrFunc.shashBits4Literal( "a", true);
	final long shashAx = QStrFunc.shashBits4Ansi( "A", true);
	final long shash0 = QStrFunc.shashBits4Literal( "0", true);
	final long shash0x = QStrFunc.shashBits4Ansi( "0", true);
	final long shashBlank = QStrFunc.shashBits4Literal( " ", true);
	final long shashBlank2 = QStrFunc.shashBits4Literal( " A", true);
	final long shashDot = QStrFunc.shashBits4Literal( ".", true);
	final long shashS2 = QStrFunc.shashBits4Literal( "ss", true);
	final long shashSharpS = QStrFunc.shashBits4Literal( "\u00df", true);
	ok = ok && (shashA == (shashAs | 4));
	ok = ok && (shashA == shashAx);
	ok = ok && (shash0 == shash0x);
	ok = ok && (((shashBlank >>> (48 + 3)) | 0xe000) == QStrFunc.SHASH_FLITERAL);
	ok = ok && (((shashBlank2 >>> (48 + 3)) | 0xe000) >= QStrFunc.SHASH_LITERAL);
	ok = ok && ((shashBlank >>> 3) < (shashDot >>> 3));
	ok = ok && ((shashDot >>> 3) < (shash0 >>> 3));
	ok = ok && ((shash0 >>> 3) < (shashA >>> 3));
	ok = ok && ((shashA >>> 3) < (shashB >>> 3));
	ok = ok && ((shashB >>> 3) < (shashS2 >>> 3));
	ok = ok && ((shashS2 >>> 3) < (shashZ >>> 3));
	ok = ok && (shashS2 == shashSharpS);
	ok = ok && "A".equals( QStrFunc.string4ShashBits( shashA));
	ok = ok && "a".equals( QStrFunc.string4ShashBits( shashAs));
	ok = ok && "ss".equals( QStrFunc.string4ShashBits( shashS2));
	String strX;
	long shashX;
	strX = "ABC";
	shashX = QStrFunc.shashBits4Literal( strX, true);
	ok = ok && (shashX == (6 | QStrFunc.shashBits4Literal( strX, false)));
	ok = ok && strX.equals( QStrFunc.string4ShashBits( shashX));
	strX = "Abc";
	shashX = QStrFunc.shashBits4Literal( strX, true);
	ok = ok && (shashX == (4 | QStrFunc.shashBits4Literal( strX, false)));
	ok = ok && strX.equals( QStrFunc.string4ShashBits( shashX));
	strX = "123";
	shashX = QStrFunc.shashBits4Literal( strX, true);
	ok = ok && strX.equals( QStrFunc.string4ShashBits( shashX));
	ok = ok && (shashX == QStrFunc.shashBits4Literal( strX, false));
	ok = ok && (shashX != QStrFunc.shashBits4shash( QStrFunc.shash( strX), null));
	ok = ok && (QStrFunc.shashBits4DoubleD4( "123", 0) == QStrFunc.shashBits4shash( QStrFunc.shash( strX), null));
	if (!ok) {
		System.out.println( "Collation error!");
	}
	String testS = "abc(def[gh(ij)kl]mn)op zyx *abc* 0 1 a+b a+1 1+a 1+2 a +b a+ b \n"
		+ "8 -1 -2 1996-12-08 0x20 22:22 5_cm 96-01-01\n- abc\n-abc\n"
		+ "#123 #045 ##123 28.02.99 1.2.3 28.03.1950 *def* g\n123 axx AxX aXX AXX";
	System.out.println( testS);
	String[] els = QStrFunc.markedAtoms4String( testS);
	StringBuilder cmp = new StringBuilder( testS.length() + 42);
	for (int i0 = 0; i0 < els.length; ++i0) {
		String el = els[i0];
		el = (0 < el.length()) && (QStrFunc.SHASH_OFFS <= el.charAt( 0)) ? el.substring( 1) : el;
		cmp.append( el);
		String sh = QStrFunc.shash( false, el)[0];
		System.out.println( "" + el.replaceAll( "[^ -z]", "^") + "\t." + StringFunc.hexUtf16( sh)
			+ " \t" + (el.contains( "9") ? QStrFunc.date4ShashDate( sh)
				: (el.matches( "\\-?[0-9].*") ? QStrFunc.doubleD4oShashNum( sh) : QStrFunc.string4Shash( sh))));
		//			+ " \t==" + el.replaceAll( "[^ -z]", "^" ) + '\t' + StringFunc.hexUtf8( outT, true ) );
	}
	ok = ok && (cmp.toString().equals( testS));
	System.out.println( "" + ok + " cmp\n" + cmp.toString());
	return ok;
}

protected boolean test_QStr_OLD( boolean print) {
	String testS = "zyx *abc* 0 1   8 -1 -2 1996-12-08 0x20 22:22 5_cm 96-01-01\n- abc\n-abc\n"
		+ "#123 #045 ##123 28.02.99 28.03.1950 *def* g\n123 axx AxX aXX AXX";
	boolean ok = true;
	System.out.println( testS);
	String[] els = QStrFunc.markedAtoms4String( testS);
	StringBuilder cmp = new StringBuilder( testS.length() + 42);
	for (int i0 = 2; i0 < els.length; i0 += 3) {
		String el = els[i0];
		el = (0 < el.length()) && (QStrFunc.SHASH_OFFS <= el.charAt( 0)) ? el.substring( 1) : el;
		cmp.append( el);
		String sh = QStrFunc.shash( false, el)[0];
		String outT = "";
		outT += QStrFunc.SHASH_QM + els[i0 - 2];
		outT += QStrFunc.SHASH_QM + els[i0 - 1];
		outT += el;
		if (print) {
			System.out.println( "" + el.replaceAll( "[^ -z]", "^") + "\t." + StringFunc.hexUtf8( sh, true)
				+ " \t" + (el.contains( "9") ? QStrFunc.date4ShashDate( sh)
					: (el.matches( ".?[0-9].*") ? QStrFunc.doubleD4oShashNum( sh) : QStrFunc.string4Shash( sh)))
				+ " \t==" + outT.replaceAll( "[^ -z]", "^") + '\t' + StringFunc.hexUtf8( outT, true));
		}
	}
	ok = ok && (cmp.toString().equals( testS));
	System.out.println( "" + ok + " cmp\n" + cmp.toString());
	return ok;
}

protected boolean test_UtilMisc_Time() {
	boolean ok = true;
	final String DATE_FORMAT_ISO_Z = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	final SimpleDateFormat DATE_SDF = new SimpleDateFormat( DATE_FORMAT_ISO_Z);
	DATE_SDF.setTimeZone( TimeZone.getTimeZone( "UTC"));
	long tim = MiscFunc.currentTimeMillisLinearized();
	long cmp = System.currentTimeMillis();
	ok = ok & (tim <= cmp);
	cmp = MiscFunc.currentTimeMillisLinearized();
	ok = ok & (tim <= cmp);
	tim = cmp;
	System.out.println( "\t" + DATE_SDF.format( new Date( tim)) + ' ' + DATE_SDF.getTimeZone().getRawOffset());
	String ts = MiscFunc.date4Millis( false, tim);
	String tx = MiscFunc.dateShort4Millis( tim);
	tim = MiscFunc.millis4Date( ts);
	ok = ok && (cmp == tim);
	System.out.println( "" + ok + '\t' + ts + ' ' + tx + ' ' + tim);
	ts = "2017-01-01T00:00:00+0000";
	tim = MiscFunc.millis4Date( ts);
	tx = MiscFunc.dateShort4Millis( tim);
	System.out.println( "" + ok + '\t' + ts + ' ' + tx + ' ' + tim);
	tx = "20170101";
	tim = MiscFunc.millis4Date( tx);
	ok = ok && (tim == 1483272000000L);
	ts = MiscFunc.date4Millis( false, tim);
	ok = ok && "2017-01-01T12:00:00.000+00:00".equals( ts);
	System.out.println( "" + ok + '\t' + ts + ' ' + tx + ' ' + tim);
	tx = "20170102";
	tim = MiscFunc.slotSecond16oDateApprox( tx);
	cmp = (2017L * 366 * 24 * 3600) << 16;
	ok = ok && (cmp < tim) && (tim < (cmp + 2 * ((24L * 3600) << 16)));
	System.out.print( "" + ok + '\t' + tx + ' ' + tim);
	tx = MiscFunc.date4SlotSecond16Approx( tim);
	ok = ok && tx.startsWith( "2017-01-02");
	System.out.println( " " + tx);
	tx = "00000201";
	tim = MiscFunc.slotSecond16oDateApprox( tx);
	cmp = (31L * 24 * 3600) << 16;
	ok = ok && (cmp <= tim) && (tim < (cmp + 2 * ((24L * 3600) << 16)));
	System.out.print( "" + ok + '\t' + tx + ' ' + tim);
	tx = MiscFunc.date4SlotSecond16Approx( tim);
	ok = ok && tx.startsWith( "0000-02-01");
	System.out.println( " " + tx);
	tx = "-00001231";
	tim = MiscFunc.slotSecond16oDateApprox( tx);
	cmp = (1L * 365 * 24 * 3600) << 16;
	ok = ok && (cmp <= tim) && (tim < (cmp + 2 * ((24L * 3600) << 16)));
	System.out.print( "" + ok + '\t' + tx + ' ' + tim);
	tx = MiscFunc.date4SlotSecond16Approx( tim);
	ok = ok && tx.startsWith( "0000-12-31");
	System.out.println( " " + tx);
	tx = "-00011231";
	tim = MiscFunc.slotSecond16oDateApprox( tx);
	cmp = (-24L * 3600) << 16;
	ok = ok && (cmp == tim);
	System.out.print( "" + ok + '\t' + tx + ' ' + tim);
	tx = MiscFunc.date4SlotSecond16Approx( tim);
	ok = ok && tx.startsWith( "-0001-12-31");
	System.out.println( " " + tx);
	tx = "0000-01-01.0000+01:00";
	tim = MiscFunc.slotSecond16oDateApprox( tx);
	cmp = -1 + ((-1 * 3600) << 16);
	ok = ok && ((cmp >> 16) == (tim >> 16));
	System.out.print( "" + ok + '\t' + tx + ' ' + tim);
	tx = MiscFunc.date4SlotSecond16Approx( tim);
	ok = ok && tx.startsWith( "0000-01-01");
	System.out.println( " " + tx);
	tx = "-0001-12-31T23:58-01:00";
	tim = MiscFunc.slotSecond16oDateApprox( tx);
	cmp = (3600 - 120) << 16;
	ok = ok && ((cmp >> 16) == (tim >> 16));
	System.out.print( "" + ok + '\t' + tx + ' ' + tim);
	tx = MiscFunc.date4SlotSecond16Approx( tim);
	ok = ok && tx.startsWith( "-0001-12-31");
	System.out.println( " " + tx);
	return ok;
}

protected boolean test_QMap( boolean print) {
	final QResult pooled = QResult.get8Pool();
	boolean ok = true;
	Random randGen = new Random( 42);
	for (int round = 999; round >= 0; --round) {
		String sr = new String( createRandomString( randGen, randGen.nextInt( 20)));
		sr = sr.replaceAll( "[\\x00-\\x1f]", " ").trim();
		System.out.println( ":" + sr + ": " + StringFunc.hexUtf16( QStrFunc.shash( sr)));
		String[] atoms = QStrFunc.markedAtoms4String( sr);
		StringBuilder cmp = new StringBuilder( sr.length() + 42);
		for (String atom : atoms) {
			cmp.append( atom.substring(
				((0 < atom.length()) && (QStrFunc.SHASH_OFFS <= atom.charAt( 0))) ? 1 : 0));
			if ((QStrFunc.SHASH_OFFS <= atom.charAt( 0)) && (QStrFunc.SHASH_TEMP_BITLIST != atom.charAt( 0))) {
				char sh0 = QStrFunc.shash( atom.substring( 1)).charAt( 0);
				if ((sh0 != atom.charAt( 0))
					&& ((sh0 >= QStrFunc.SHASH_LITERAL) != (atom.charAt( 0) >= QStrFunc.SHASH_LITERAL))) {
					if ((QStrFunc.SHASH_TEMP_NUM_STRING == atom.charAt( 0))
						&& (QStrFunc.SHASH_NEG <= sh0) && (sh0 < QStrFunc.SHASH_LITERAL)) {
						continue;
					}
					if (// ...(sh0 != QStrFunc.SHASH_PUNCT)
					(QStrFunc.SHASH_SLITERAL != (atom.charAt( 0) & ~3))) {
						System.out.println( "=== shash !=: " + atom.substring( 1));
						ok = false;
					}
				}
			}
		}
		String sx = cmp.toString();
		if (sr.equals( sx)) {
			QVal sq = QMapSto.qval4String( pooled, sr);
			sx = QMapSto.string4QVal( sq);
		}
		if (!sr.equals( sx)) {
			ok = false;
			int inx = 0;
			for (; inx < sr.length() && inx < sx.length(); ++inx) {
				if (sr.charAt( inx) != sx.charAt( inx)) {
					break;
				}
			}
			System.out.println( "=== !=" + sr.charAt( inx) + '@' + inx + "===" + sr + "//" + sx);
		}
	}
	String[] strs = new String[100];
	for (int round = strs.length - 1; round >= 0; --round) {
		String sr = new String( createRandomString( randGen, 10 + randGen.nextInt( 60)));
		sr = sr.replaceAll( "[\\x00-\\x1f]", " ").trim();
		strs[round] = sr;
	}
	HashMap<String, Long> map = new HashMap<String, Long>();
	int hMax = 0;
	for (int round = 10 * strs.length; round >= 0; --round) {
		String sr = strs[randGen.nextInt( strs.length)];
		Long cmp = map.get( sr);
		//		if (null != cmp) {
		//			System.out.println( "=== h: " + (cmp & 0xfffff) + ' ' + sr );
		//		}
		QVal handle = QMapSto.qval4AtomicValue( pooled, sr);
		if (hMax < (0xfffff & QVal.asQVal( handle))) {
			hMax = (int) (0xfffff & QVal.asQVal( handle));
		}
		if (!sr.equals( QMapSto.string4QVal( handle))) {
			System.out.println( "=== != " + (cmp & 0xfffff) + ' ' + (0xfffff & QVal.asQVal( handle)));
			ok = false;
		}
		if ((null != cmp) && (cmp != QVal.asQVal( handle))) {
			System.out.println( "=== h: " + (cmp & 0xfffff) + ' ' + (0xfffff & QVal.asQVal( handle)));
			ok = false;
		}
		map.put( sr, QVal.asQVal( handle));
	}
	map = new HashMap<String, Long>();
	for (int round = 10 * strs.length; round >= 0; --round) {
		String sr = strs[randGen.nextInt( strs.length)];
		Long cmp = map.get( sr);
		//		if (null != cmp) {
		//			System.out.println( "=== h: " + (cmp & 0xfffff) + ' ' + sr );
		//		}
		QVal handle = QMapSto.qval4String( pooled, sr);
		if (hMax < (0xfffff & QVal.asQVal( handle))) {
			hMax = (int) (0xfffff & QVal.asQVal( handle));
		}
		if (!sr.equals( QMapSto.string4QVal( handle))) {
			long c0 = (null == cmp) ? 0 : (cmp & 0xfffff);
			System.out.println( "=== != " + c0 + ' ' + (0xfffff & QVal.asQVal( handle)));
			ok = false;
		}
		if ((null != cmp) && (cmp != QVal.asQVal( handle))) {
			System.out.println( "=== h: " + (cmp & 0xfffff) + ' ' + (0xfffff & QVal.asQVal( handle)));
			ok = false;
		}
		map.put( sr, QVal.asQVal( handle));
	}
	QMapSto.idle();
	map = new HashMap<String, Long>();
	final long hMaxOld = hMax;
	int reused = 0;
	for (int round = 10 * strs.length; round >= 0; --round) {
		String sr = strs[randGen.nextInt( strs.length)];
		Long cmp = map.get( sr);
		//		if (null != cmp) {
		//			System.out.println( "=== h: " + (cmp & 0xfffff) + ' ' + sr );
		//		}
		QVal handle = QMapSto.qval4String( pooled, sr);
		if (hMax < (0xfffff & QVal.asQVal( handle))) {
			hMax = (int) (0xfffff & QVal.asQVal( handle));
		}
		if (!sr.equals( QMapSto.string4QVal( handle))) {
			System.out.println( "=== != " + round + ' ' + (0xfffff & QVal.asQVal( handle)));
			ok = false;
		}
		if ((null != cmp) && (cmp == QVal.asQVal( handle))) {
			if (print) {
				System.out.println( "=== h: " + (cmp & 0xfffff) + ' ' + (0xfffff & QVal.asQVal( handle)));
			}
			++reused;
		}
		map.put( sr, QVal.asQVal( handle));
	}
	System.out.println( "Re-used: " + reused + "/ " + 10 * strs.length + " in " + hMax + "/ " + hMaxOld);
	return ok;
}

protected boolean test_QMap_threaded( boolean print) {
	boolean ok = true;
	int cThreads = maxThreads;
	final QResult pooled = QResult.get8Pool();
	if (print) {
		System.out.println( "<<< (offset) "
			+ (QVal.asQVal( QMapSto.qval4AtomicLiteral( pooled, "A) long string for QMap "))
				& ((1 << QMapSto.COMBINED_BITS__28) - 1)));
	}
	ExecutorService exec = Executors.newFixedThreadPool( cThreads);
	HashSet<QMapTester> tasks = new HashSet<QMapTester>();
	for (int i0 = 0; i0 < cThreads; ++i0) {
		QMapTester task = new QMapTester();
		task.print = print;
		task.wArguments = //new OidBaton[] { new OidBaton( "" + i0, 1, 0 ) };
			new Mapping[] { Mapping.make( "" + i0 + ":LABEL:......", Mapping.Cats.DEFAULT, null, "" + i0) };
		task.start();
		tasks.add( task);
	}
	try {
		List<Future<QBaton[]>> futs = exec.invokeAll( tasks);
		exec.shutdown();
		exec.awaitTermination( maxCount, TimeUnit.MILLISECONDS);
		for (Future<QBaton[]> fut : futs) {
			--cThreads;
			String label = QMapSto.string4QVal( ((Mapping) fut.get()[0]).uLabel);
			ok = ok && ('t' == label.charAt( 1));
			System.out.println( "." + label);
		}
		ok = ok && (0 == cThreads);
	} catch (Exception e) {
		System.out.println( e);
		ok = false;
	}
	if (print) {
		System.out.println( "<<< (35*2+2+offset) "
			+ (QVal.asQVal( QMapSto.qval4AtomicLiteral( pooled, "B) long string for QMap"))
				& ((1 << QMapSto.COMBINED_BITS__28) - 1)));
	}
	return ok;
}

private boolean rfcCheck( String in, String expected, boolean old) {
	String res0 = null;
	String res = null;
	if (old) {
		res = res0 = StringFunc.str4Mnemonics_OLD( in.replace( "_{{", "{{").replace( "_[[", "[[")
			.replace( "_<", "_{").replace( "}}_", "}}").replace( "]]_", "]]").replace( ">_", "}_"));
		//		if (expected.contains( "}}" )) {
		//			expected = expected.replace( "{{", "\u0002" ).replace( "}}", "\u0003" );
		//		}
	} else {
		//	expected = expected.replace( "\u0002", "{{" ).replace( "\u0003", "}}" );
		res = StringFunc.string4Mnemonics( in);
		String[] tmp = QStrFunc.markedAtoms4String( res);
		int len = QStrFunc.lists4MarkedAtoms( tmp);
		res = res0 = "";
		for (int i0 = 0; i0 < len; ++i0) {
			res0 += "^" + tmp[i0];
			if (expected.contains( "\u0002" + tmp[i0] + "\u0003") && !res.startsWith( "\u0002")) {
				res += "\u0002" + tmp[i0] + "\u0003";
			} else if ('\ue000' <= tmp[i0].charAt( 0)) {
				res += tmp[i0].substring( 1);
			} else {
				res += tmp[i0];
			}
		}
	}
	boolean ok = expected.equals( res);
	if (!ok) {
		System.out.println( "!!" + expected);
	}
	System.out.println( "" + ok + (old ? " X" : "") + '\t' + in + '\t' + res0);
	return ok;
}

protected boolean test_rfc1345() {
	boolean ok = true;
	Random randGen = new Random( 42);
	for (boolean old = false; true; old = !old) {
		ok = ok && rfcCheck( "abc_{{def}}_g", "abc\u0002def\u0003g", old);
		ok = ok && rfcCheck( "_{{abcdefg}}_", "abcdefg", old);
		ok = ok && rfcCheck( "_{{abcdef}}_g", "\u0002abcdef\u0003g", old);
		ok = ok && rfcCheck( "abc_{{defg}}_", "abc\u0002defg\u0003", old);
		ok = ok && rfcCheck( "ab{c_{{d{efg}}_", "ab{c\u0002d{efg\u0003", old);
		ok = ok && rfcCheck( "ab}c}_{{def}g}}_", "ab}c}\u0002def}g\u0003", old);
		ok = ok && rfcCheck( "_[[abcdefg]]_", "\u0004abcdefg\u0005", old);
		ok = ok && rfcCheck( "abc_{{def}}_gabc_{{def}}_g", "abc\u0002def\u0003gabc\u0002def\u0003g", old);
		System.out.println( "_K_abK_Y__aY_ => " + StringFunc.string4Alpha1345( "_K_abK_Y__aY_"));
		ok = ok && rfcCheck( "_[_K_abK_Y__aY_]_", "<abK'\u00e1", old);
		//		if (!old)
		//			break;
		//		ok = ok && rfcCheck( "_[_K_abK_Y__aY_]__A__[_K_abK_Y__aY]__[_K_abK_Y__aY__U]", "<abK'\u00e1A<abK'\u00e1<abK'\u00e1", old );
		//		ok = ok && rfcCheck( "'_a'__a:__aa_bc_{{a'a:_a'_{_a:_{{_}}a}abc}}_ge_a'__a:_'",
		//			"'\u00e1\u00e4\u00e5bc\u0002a'a:_a'_{_a:_{{_}}a}abc\u0003ge\u00e1\u00e4\'", old );
		//		ok = ok && rfcCheck( "'_A'__e:__AA_bc_{{a'a:_a'__{{_a:_a}}_abc}}_ge_a'__A:_'",
		//			"'\u00c1\u00eb\u00c5bc\u0002a'a:_a'_{{_a:_a}}_abc\u0003ge\u00e1\u00c4\'", old );
		//		ok = ok && rfcCheck( "_A:__12345678__(__123_(_12345678_(__12345678__(_12345678__A_A__A____A___A__A__",
		//			"\u00c412345678_(_123(12345678(12345678_(12345678_AAA_A_A_A_", old );
		String tCtrl = "L$ \t.";
		//	char[] aCtrl = tCtrl.toCharArray();
		//	StringFunc.test_replaceCtrlNOld( aCtrl, 0, aCtrl.length );
		ok = ok && tCtrl.equals( StringFunc.makePrintable( tCtrl)); //new String( aCtrl ) );
		for (int i1 = 0; (i1 < 33) && ok; ++i1) {
			char[] rand = createRandomString( randGen, 12); //88
			StringFunc.replaceCtrlNOld( rand, 0, rand.length);
			String xrand = new String( rand).replace( "\u0000", "");
			rand = xrand.toCharArray();
			String in = StringFunc.mnemonics4String( xrand, true, true);
			String out = StringFunc.string4Mnemonics( in.replace( "\n", ""));
			if (old) {
				in = StringFunc.mnemonics4String_OLD( xrand, true, true);
				out = StringFunc.str4Mnemonics_OLD( in.replace( "\n", ""));
			}
			if (!xrand.equals( out)) { //new String( axr ) )) {
				System.out.println( "!! " + (old ? 'X' : ' ') + '\t' + in.replaceAll( "[^ -z]", "^") + '\t'
					+ out.replaceAll( "[^ -z]", "^"));
				ok = false;
			}
			if (!old) {
				in = StringFunc.mnemonics4String( xrand, false, false);
				out = StringFunc.string4Mnemonics( in);
				//				in = StringFunc.mnemonics4String_OLD( xrand, false, false );
				//				out = StringFunc.str4Mnemonics_OLD( in );
				//			ok = ok && rfcCheck( in, out, old );
				if (!StringFunc.equalsRoughly( xrand, out)) { //new String( axr ) )) {
					System.out.println( "!! " + (old ? 'X' : ' ') + '\t' + in.replaceAll( "[^ -z]", "^") + '\t'
						+ out.replaceAll( "[^ -z]", "^"));
					ok = false;
				} else {
					System.out.println( "true " + (old ? 'X' : ' ') + '\t' + in.replaceAll( "[^ -z]", "^") + '\t'
						+ out.replaceAll( "[^ -z]", "^"));
				}
			}
		}
		if (!old) {
			break;
		}
	}
	ok = ok && rfcCheck( "abc{_{gabc_{{def}}_g}}_", "abc\u0002gabc\u0002def\u0003g\u0003", true);
	ok = ok && rfcCheck( "abc{_{gabc_{{def}}_g}}_", "abc{{gabcdefg}}_", false);
	ok = ok && rfcCheck( "abc_{{def_{{gabc}}_def}}_g", "abc\u0002def{{gabc}}def\u0003g", true);
	ok = ok && rfcCheck( "abc_{{def_{{gabc}}_def}}_g", "abc\u0002def\u0002gabc\u0003def\u0003g", false);
	ok = ok && rfcCheck( "abc{_{gabc[[def]]g}}_", "abc\u0002gabc\u0004def\u0005g\u0003", true);
	ok = ok && rfcCheck( "abc{_{gabc[[def]]g}}_", "abc{{gabc[[def]]g}}_", false);
	ok = ok && rfcCheck( "abc{_{{{gabc[[def]]g}}_}}_", "abc{{gabc[[def]]g}}_", false);
	ok = ok && rfcCheck( "abc{{gabc_[[def]]_g}}", "abc{{gabc\u0004def\u0005g}}", false);
	ok = ok && rfcCheck( "__x_ abc{_{def__gab_{c}}g_{{def__gabc}}g",
		"_xabc\u0002def_gab\u000ec\u000f}g\u0002def__gabc\u0003g", true);
	ok = ok && rfcCheck( "__x_ abc_{{def__gab_{c}}_g_{{def__gabc}}g", "_xabc\u0002def_gab{c\u0003g{{def_gabc}}g",
		false);
	ok = ok && rfcCheck( "abc{_ {gabc}}g", "abc{{gabc\u0003g", true);
	ok = ok && rfcCheck( "abc{_ {gabc}}g", "abc{{gabc}}g", false);
	ok = ok && rfcCheck( "_#42_{}{_ {}_ }}_}}_}", "B{}{{}\u0003\u0003}", true);
	ok = ok && rfcCheck( "_#42_{}{_ {}_ }}_}}_}", "B{}{{}}}}}", false);
	ok = ok && rfcCheck( "_#42__#20__#30__#1__#7B__#6__#40__#2020_", "B 0_{" + StringFunc.ZW + "@\u2020", true);
	ok = ok && rfcCheck( "_#42__#20__#30__#1__#7B__#6__#40__#2020_", "B 0\u0001{" + StringFunc.ZW + "@\u2020", false);
	return ok;
}

/////

@Override
public Integer call() throws Exception {
	boolean ok = true;
	System.out.println( "===== " + ok);
	ok = ok && test_UtilMisc_createId();
	System.out.println( "===== " + ok);
	ok = ok && test_Util();
	System.out.println( "===== " + ok);
	ok = ok && test_QStr();
	System.out.println( "===== " + ok);
	////	ok = ok && test_QStr_OLD( false );
	////	System.out.println( "===== " + ok );
	ok = ok && test_UtilMisc_Time();
	System.out.println( "===== " + ok);
	ok = ok && test_QMap( false);
	System.out.println( "===== " + ok);
	ok = ok && test_QMap_threaded( false);
	System.out.println( "===== " + ok);
	ok = ok && test_rfc1345();
	System.out.println( "===== " + ok);
	return ok ? 0 : 1;
}

public int run( String[] args) throws Exception {
	return call().intValue();
}

//=====
}
