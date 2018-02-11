package com.yzl.framework.beam.codegen.core.service2interface.printer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class AbstractPrint {

    protected final String fileRootPath;

    protected final String sourcePackageName;

    protected final String className;

    public AbstractPrint(String fileRootPath, String sourcePackageName, String className){
        this.fileRootPath = fileRootPath;
        this.sourcePackageName = sourcePackageName;
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    public String getSourcePackageName() {
        return this.sourcePackageName;
    }

    protected abstract List<String> collectFileData();

    public void print() {
        String fileName = fileRootPath + "/" + StringUtils.replace(sourcePackageName.toLowerCase(), ".", "/") + "/"
                          + className + ".java";
        File javaFile = new File(fileName);
        List<String> fileData = collectFileData();
        if (fileData != null) {
            try {
                FileUtils.writeLines(javaFile, "UTF-8", fileData);
            } catch (IOException e) {
                throw new IllegalArgumentException("can not write file to" + fileName, e);
            }
        }
    }

}
