// Copyright (C) 2019,2020  Roland Horsch <g x w orks @ma il .de>.
// License: GPLv3-or-later - see LICENSE file (github.com/gxworks/dibdib),
// plus the compatible full texts for further details.
// ABSOLUTELY NO WARRANTY. Formatted by Eclipse.

package io.github.gxworks.dibdib;

import java.io.File;
import net.sf.dibdib.config.*;
import net.sf.dibdib.generic.PlatformFuncIf;

public class Main implements PlatformFuncIf {

/**
 * @param args Command line arguments
 * @throws Exception
 */
public static void main( String[] args ) throws Exception {
//=====

	Main INSTANCE = new Main();
	Dib2Config.init( '0', "co", ".", INSTANCE, null );

	System.out.println( "Dib2Co version " + Dib2Constants.VERSION_STRING );
	System.out.println( Dib2Constants.NO_WARRANTY[ 0 ] );

	io.github.gxworks.dibdib.Test.INSTANCE.run( new String[ 0 ] );
}

@Override
public void invalidate() {
	// TODO Auto-generated method stub

}

//@Override
//public int getTextRow( int xYReal, int xRange ) {
//	// TODO Auto-generated method stub
//	return 0;
//}
//
//@Override
//public int getTextColumn( int xXReal, String xText ) {
//	// TODO Auto-generated method stub
//	return 0;
//}
//
//@Override
//public void setTouched4TextPos() {
//	// TODO Auto-generated method stub
//
//}

@Override
public String[] getLicense( String pre ) {
	// TODO Auto-generated method stub
	return null;
}

//@Override
//public boolean pushClipboard( String label, String text ) {
//	// TODO Auto-generated method stub
//	return false;
//}
//
//@Override
//public String getClipboard() {
//	// TODO Auto-generated method stub
//	return null;
//}

@Override
public File getFilesDir( String... parameters ) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public void log( String... aMsg ) {
	// TODO Auto-generated method stub

}

//@Override
//public void toast( String msg ) {
//	// TODO Auto-generated method stub
//
//}

//=====
}
