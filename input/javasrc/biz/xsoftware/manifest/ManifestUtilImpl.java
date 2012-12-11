package biz.xsoftware.manifest;

import java.io.File;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Logger;

/**
 * @author Dean Hiller
 */
public class ManifestUtilImpl implements ManifestInfo.ManifestUtil {

	private static final Logger log = Logger.getLogger(ManifestUtilImpl.class.getName());
	
	public File getFile(URL url) {
		log.finest("url="+url);
		String filePart = url.getFile();
		log.finest("filePart from url="+filePart);
		String nameWithClass = filePart.substring(5, filePart.length());
		log.finest("nameWithClass="+nameWithClass);
		int index = nameWithClass.indexOf("!");
		log.finest("index="+index);
		String fileName = nameWithClass.substring(0, index);
		log.finest("fileName of buildtemplate="+fileName);
		
		File f = new File(fileName);
		
		if(!f.exists()) {
			log.warning("Bug, Exiting System.  Could not find file="+fileName);
			System.exit(1);
		}
	
		log.finer("returning file="+f.getAbsolutePath());
		return f;
	}

	/* (non-Javadoc)
	 * @see biz.xsoftware.manifest.ManifestInfo.SystemInterface#exit(int)
	 */
	public void exit(int code) {
		System.exit(code);
	}

	public String getMainClass(Manifest manifest) {
		Attributes attr = manifest.getMainAttributes();
		String s = attr.getValue("SubMain-Class");
		return s.trim();
	}

	//dummy main for testing purposes!!!
	public static void main(String[] args) {
		log.fine("just throwing an exception for testing purposes");
		throw new RuntimeException("Just for testing only");
	}
}
