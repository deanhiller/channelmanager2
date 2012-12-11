package biz.xsoftware.manifest;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Dean Hiller
 */
public class ManifestInfo {
	
	private static final Logger log = Logger.getLogger(ManifestInfo.class.getName());
	
	private static ManifestUtil util = new ManifestUtilImpl();
	
	private Manifest manifest;
	
	/**
	 * The main program for Version that prints the version info from 
	 * the manifest file.
	 * 
	 * java -jar xxx.jar 
	 *       1. runs SubMain-Class in manifest file
	 * 		 2. If SubMain-Class is default value or "", prints usage info
	 *          for -manifest and -version
	 * 
	 * java -jar xxx.jar -version
	 *       1. prints version info
	 * 
	 * java -jar xxx.jar -manifest
	 *       1. prints all manifest contents.
	 * 
	 * @param args Ignores all arguments.
	 */
	public static void main(String[] args) throws Throwable {
		try {
			run(args);
		} catch(Throwable e) {
			log.log(Level.WARNING, "Exception occurred", e);
			throw e;
		}
	}
	
	public static void run(String[] args) throws IOException {
		ManifestInfo manifestInfo = new ManifestInfo();
		
		if(args.length > 0) {
			if("-manifest".equals(args[0])) {
				System.out.println(""+manifestInfo);
				return;
			} else if("-version".equals(args[0])) {
				System.out.println(manifestInfo.getFullVersionInfo());
				return;
			}
		}
		
		String className = util.getMainClass(manifestInfo.getManifest());
		if(className == null)
			className = "";
		if("TOOLS.JAVA.Main".equals(className.trim()) || "".equals(className)) {
			System.err.println("Usage:");
			System.err.println("1. java -jar <jarfile> -version");
			System.err.println("2. java -jar <jarfile> -manifest");
		} else {
			runProgram(className, args);
		}
		util.exit(1);
	}
	
	static void runProgram(String className, String[] args) {
		String msg = "";
		ClassLoader cl = ManifestInfo.class.getClassLoader();
		try {
		
			Class c = cl.loadClass(className);
			log.finest("class="+c);
			Method m = c.getMethod("main", new Class[] {String[].class});
			m.invoke(null, new Object[] { args });

		} catch(ClassNotFoundException e) {
			msg = "Class in manifest not found in classpath\n"
					  +"Fix the ant.properties file to refer to the\n"
					  +"main class or refer to nothing\n"
					  +"class="+className;
			System.out.println(msg);
		} catch(NoSuchMethodException e) {
			msg = "You have specified a class that doesn't"
						+"have a main method in ant.properties file."
						+"class="+className;
			System.out.println(msg);
		} catch(Exception e) {
			msg = "\n\n2. Unknown failure. Contact buildtemplate owner";
			log.log(Level.WARNING, "Exception occurred", e);
			System.out.println(msg);
		}
		util.exit(1);		
	}
	
	
	public static void setJarLocator(ManifestUtil l) {
		util = l;
	}
	
	/**
	 * Constructor that takes a class to get the version information
	 * from out of the manifest.  Uses the class's package to retrieve
	 * the manifest version info.
	 */
	public ManifestInfo() throws IOException {
		URL url = ManifestInfo.class.getResource("ManifestInfo.class");

		//set manifest from jar file
		File f = util.getFile(url);
		JarFile jarFile = new JarFile(f);
		manifest = jarFile.getManifest();
		
		//set the package of this guy(not really needed as we could get all this info
		//directly from manifest)
		//String name = ManifestInfo.class.getName();
		//int index = name.lastIndexOf(".");	
		//String packageName = name.substring(0, index);
		//thePackage = Package.getPackage(packageName);		
	}
	
	private Manifest getManifest() {
		return manifest;
	}
	
	public String getVersion() {
		Attributes attr = manifest.getMainAttributes();
		String version = attr.getValue("Implementation-Version");
		version = version.trim();		
		int index = version.indexOf(" ");
		version = version.substring(0, index);
		return version;
	}
	
	public String getFullVersionInfo() {
		Attributes attr = manifest.getMainAttributes();
		String retVal = attr.getValue("Implementation-Title")+" information...";
		retVal += "\nwebsite=   "+attr.getValue("Implementation-Vendor");
		retVal += "\nbuilt by=  "+attr.getValue("Built-By");
		retVal += "\nclasspath= "+attr.getValue("Class-Path");
		retVal += "\nversion=   "+attr.getValue("Implementation-Version")+"\n";
		
		return retVal;
	}
		
	/**
	 * Prints the version info the Version represents.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String manifestInfo = "\nManifest information...\n";
		
		Attributes attr = manifest.getMainAttributes();
		Set<Object> s = attr.keySet();
		
		Iterator<Object> iter = s.iterator();
		while(iter.hasNext()) {
			Object o = iter.next();
			manifestInfo += o+"="+attr.get(o)+"\n";
		}		

		return manifestInfo;
	}
	
	public interface ManifestUtil {
		public File getFile(URL url);
		/**
		 * @param manifest
		 */
		public String getMainClass(Manifest manifest);
		
		public void exit(int code);
	}
}
