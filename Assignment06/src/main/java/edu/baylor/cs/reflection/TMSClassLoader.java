package edu.baylor.cs.reflection;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TMSClassLoader {

    private static final String[] paths = {"inputs/cms", "inputs/ems", "inputs/qms", "inputs/ums"};

    public static List<Class> findAllClasses() {
        List<Class> classes = new ArrayList<>();
        for (String path : paths) {
            try {
                classes.addAll(findClasses(path));
            } catch (Exception | Error e) {
                log.error(e.toString());
            }
        }
        return classes;
    }

    private static List<Class> findClasses(String path) throws MalformedURLException {
        File file = new File(path);
        URL[] urls = new URL[]{file.toURI().toURL()};
        ClassLoader cl = new URLClassLoader(urls);
        return findRecursive(file, "", cl);
    }

    private static List<Class> findRecursive(File directory, String packageName, ClassLoader cl) {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                if (packageName.length() == 0) {
                    classes.addAll(findRecursive(file, file.getName(), cl));
                } else {
                    classes.addAll(findRecursive(file, packageName + "." + file.getName(), cl));
                }
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class cls = cl.loadClass(className);
                    classes.add(cls);
                } catch (Exception | Error e) {
                    log.error(e.toString());
                }
            }
        }

        return classes;
    }
}
