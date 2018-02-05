package nl.hypothermic.scfv_standalone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class scfvTest extends TestCase {

	public scfvTest( String testName ) {
        super( testName );
    }

    public static Test suite() {
        return new TestSuite( scfvTest.class );
    }

    public void testApp() {
        assertTrue( true );
    }
}
