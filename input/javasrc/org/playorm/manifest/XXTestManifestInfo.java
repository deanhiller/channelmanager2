package org.playorm.manifest;

import java.io.File;
import java.net.URL;
import java.util.jar.Manifest;

import biz.xsoftware.manifest.ManifestInfo;
import biz.xsoftware.manifest.ManifestUtilImpl;

import junit.framework.TestCase;

/**
 * This is purely so emma always always reports code coverage 
 * numbers on everything
 */
public class XXTestManifestInfo extends TestCase {

	private static final String DUMMY = "dummy";
	
	private File jarFile;
	
	/**
	 * @param arg0
	 */
	public XXTestManifestInfo(String arg0) {
		super(arg0);
	}
	public void setUp() {
		String jarLoc = System.getProperty("jar.name");
		if(jarLoc != null) //for tests run from ant
			jarFile = new File(jarLoc);
		else //for tests run from eclipse
			jarFile = new File("output/jardist/projectname.jar");
	}
	
	public void testManifestInfo() throws Throwable {
		FakeJarLocator mock = new FakeJarLocator(jarFile, "should.not.matter.class");
		
		ManifestInfo.setJarLocator(mock);
		
		ManifestInfo.main(new String[] {"-version"});
		
		ManifestInfo.main(new String[] {"-manifest"});
		
		String version = new ManifestInfo().getVersion();
		assertEquals("version from build.xml should equal version in jar", System.getProperty("version"), version);
	}
	
	public void testRunMainClass() throws Throwable {
		FakeJarLocator mock = new FakeJarLocator(jarFile, XXTestManifestInfo.class.getName());
		ManifestInfo.setJarLocator(mock);
		ManifestInfo.main(new String[] {DUMMY});
		
		assertTrue("should have one argument", argsLen == 1);
		assertEquals("variable should have been passed through", DUMMY, dummyArg);		
	}
	
	public void testExceptionFromMainClass() throws Throwable {
		FakeJarLocator mock = new FakeJarLocator(jarFile, ManifestUtilImpl.class.getName());
		ManifestInfo.setJarLocator(mock);
		ManifestInfo.main(new String[0]);
	}
	
	public void testClassNotFound() throws Throwable {
		FakeJarLocator mock = new FakeJarLocator(jarFile, "this.class.is.not.found");
		ManifestInfo.setJarLocator(mock);
		ManifestInfo.main(new String[0]);
	}
	public void testTOOLSJAVAMain() throws Throwable {
		FakeJarLocator mock = new FakeJarLocator(jarFile, " TOOLS.JAVA.Main ");
		ManifestInfo.setJarLocator(mock);
		ManifestInfo.main(new String[0]);		
	}
	public void testNullClassName() throws Throwable {
		FakeJarLocator mock = new FakeJarLocator(jarFile, null);
		ManifestInfo.setJarLocator(mock);
		ManifestInfo.main(new String[0]);		
	}	
	
	public void testGetMainClass() {
		Manifest mf = new Manifest();
		mf.getMainAttributes().putValue("SubMain-Class", "   xx   ");
		ManifestUtilImpl util = new ManifestUtilImpl();
		String s = util.getMainClass(mf);
		assertEquals("should have trimmed the value", "xx", s);
	}
	
	public void testGetFile() throws Exception {		
		File f = jarFile;
		assertTrue("file should exist before we test this", f.exists());
		URL url = f.toURL();
		URL urlToClassFile = new URL("file", null, -1, url+"!/biz/xsoftware/buildtemplate/Util.class");
		File tmp = new ManifestUtilImpl().getFile(urlToClassFile);
		
		assertNotNull("Should return a real file", tmp);
		assertTrue("file should still exist", tmp.exists());
	}
	
	private class FakeJarLocator implements ManifestInfo.ManifestUtil {
		private File file;
		private String mainClass;
		public FakeJarLocator(File f, String mainClass) {
			this.file = f;
			this.mainClass = mainClass;
		}
		public File getFile(URL url) {
			return file;
		}
		public void exit(int code) {
			//do nothing!!!
		}
		public String getMainClass(Manifest manifest) {
			return mainClass;
		}		
	}
	
	//dummy main method for testing!!!
	public static void main(String[] args) {
		argsLen = args.length;
		dummyArg = args[0];
	}
	private static int argsLen;
	private static String dummyArg;
}
