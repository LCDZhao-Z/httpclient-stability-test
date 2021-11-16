package io.esastack.test.stability.command;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ExecutorManagerImpl implements ExecutorManager {
    private static final Map<String, Executor> executorMap =
            new HashMap<>();

    ExecutorManagerImpl() throws Exception {
        List<Class<AutoRegistryExecutor>> classes
                = getAllClass(AutoRegistryExecutor.class, AutoRegistryExecutor.SCAN_PACKAGE_NAME);
        for (Class<AutoRegistryExecutor> aClass : classes) {
            registry((Executor) aClass.getConstructors()[0].newInstance());
        }
    }

    @Override
    public Executor get(String type) {
        Executor executor = executorMap.get(type);
        if (executor == null) {
            throw new NullPointerException("The type(" + type + ") of executor is null!");
        }
        return executor;
    }

    @Override
    public void registry(Executor executor) {
        Executor existExecutor = executorMap.get(executor.type());
        if (existExecutor != null) {
            throw new IllegalStateException("The type(" + executor.type() + ") of executor existed before registry!");
        }
        executorMap.put(executor.type(), executor);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> ArrayList<Class<T>> getAllClass(Class<T> clazz, String packagePath) throws IOException {
        ArrayList<Class<T>> list = new ArrayList<>();
        ArrayList<Class<?>> allClass = getAllClass(packagePath);
        for (Class<?> aClass : allClass) {
            if (clazz.isAssignableFrom(aClass)) {
                if (!clazz.equals(aClass)) {//自身并不加进去
                    list.add((Class<T>) aClass);
                }
            }
        }
        return list;
    }

    private static ArrayList<Class<?>> getAllClass(String packageName) throws IOException {
        ArrayList<Class<?>> list = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');

        ArrayList<File> fileList = new ArrayList<>();
        Enumeration<URL> enumeration = classLoader.getResources("./" + path);
        while (enumeration.hasMoreElements()) {
            URL url = enumeration.nextElement();
            fileList.add(new File(url.getFile()));
        }
        for (File file : fileList) {
            list.addAll(findClass(file, packageName));
        }

        return list;
    }

    private static ArrayList<Class<?>> findClass(File file, String packageName) {
        ArrayList<Class<?>> list = new ArrayList<>();
        if (!file.exists()) {
            return list;
        }
        File[] files = file.listFiles();
        assert files != null;
        for (File temFile : files) {
            if (temFile.isDirectory()) {
                assert !temFile.getName().contains(".");//添加断言用于判断
                ArrayList<Class<?>> arrayList = findClass(temFile, packageName + "." + temFile.getName());
                list.addAll(arrayList);
            } else if (temFile.getName().endsWith(".class")) {
                try {
                    //保存的类文件不需要后缀.class
                    list.add(Class.forName(packageName + '.' + temFile.getName().substring(0,
                            temFile.getName().length() - 6)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
