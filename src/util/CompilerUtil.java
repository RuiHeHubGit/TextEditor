package util;

import java.io.File;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CompilerUtil {
	
	public static Class CompilerAndLoader(File file) throws Exception {
		
		Class<?> converteClass = null;
		String classPath = null;
		String name = null;
		if(file.getName().endsWith(".java")) {
			//把生.java文件编译成.class文件
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			if(compiler == null) {
				throw new Error("需要把JAVA_HOME路径由jdk改到jre,然后把lib下的tools.jar包复制到jre的lib下或者选择编译好的类文件才能添加转化器!");
			}
			StandardJavaFileManager manage = compiler.getStandardFileManager(null, null, null);
			JavaCompiler.CompilationTask task = compiler.getTask(null, manage, null, null, null,
					manage.getJavaFileObjects(file));
			task.call();
			manage.close();
			
			name = file.getName().replace(".java", "");
			classPath = CompilerUtil.class.getResource("").getPath()+File.separator+name+".class";
			
			file.delete();
		} else if(file.getName().endsWith(".class")) {
			classPath = file.getAbsolutePath();
			name = file.getName().replace(".class", "");
		} else {
			throw new Error("无效文件");
		}
		
		try {
			HRClassLoader loader = new HRClassLoader();
			converteClass = loader.loaderByPath(classPath);
		} catch (NoClassDefFoundError e) {
			throw new Error(e.getMessage());
		} catch (Exception e) {
			throw new Error(e.getMessage());
		}
		
		if(converteClass == null) {
			throw new Error("加载转化器类失败");
		}
		
		return converteClass;
	}
}
