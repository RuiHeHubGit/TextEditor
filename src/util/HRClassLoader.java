package util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
 
public class HRClassLoader extends ClassLoader{
	private String path;
	private String packageName;
	
	public HRClassLoader() {
	}
	
	public HRClassLoader(String path, String packageName) {
		this.path = path;
		this.packageName = packageName;
	}
	
	public Class<?> loaderByPath(String classPath) {
		byte[] cLassBytes = null;
		try {
			Path path = Paths.get(new URI("file:///"+classPath.replace("\\", "/")));
			cLassBytes = Files.readAllBytes(path);
			return defineClass(cLassBytes, 0, cLassBytes.length);
		} catch (URISyntaxException e) {
		} catch (IOException e) {
		}
		return null;
	}
 
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
    	
        if(path != null){
            File classFile = new File(path);
            if(classFile.exists()){
            	String className = packageName+ "." + name;
                FileInputStream in = null;
                ByteArrayOutputStream out = null;
                try{
                    in = new FileInputStream(classFile);
                    out = new ByteArrayOutputStream();
                    byte [] buff = new byte[1024];
                    int len;
                    while ((len = in.read(buff)) != -1){
                        out.write(buff,0,len);
                    }
                    return defineClass(className, out.toByteArray(), 0, out.size());
                }catch (Exception e){
                    throw new ClassNotFoundException(e.getMessage());
                }finally {
                    try {
						if(null != in){
							in.close();
						}
						if(out != null){
							out.close();
						}
					} catch (IOException e) {
					}
                }
            }
 
        }
 
        return null;
 
    }
}