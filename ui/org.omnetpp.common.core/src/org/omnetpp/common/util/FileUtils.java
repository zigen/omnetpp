/*--------------------------------------------------------------*
  Copyright (C) 2006-2015 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  'License' for details on this and other legal matters.
*--------------------------------------------------------------*/

package org.omnetpp.common.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class FileUtils {

    private FileUtils() {
    }

    /**
     * Delete directory and its contents recursively
     */
    public static void deleteDir(File dir) {
        if (dir.exists()) {
            if (dir.isDirectory())
                for (File file : dir.listFiles())
                    deleteDir(file);
            dir.delete();
        }
    }

    /**
     * Delete the contents of the directory recursively
     */
    public static void deleteDirContents(File dir) {
        if (dir.exists()) {
            if (dir.isDirectory())
                for (File file : dir.listFiles())
                    deleteDir(file);
        }
    }

    private static byte[] createBuffer() {
        return new byte[4096];
    }

    public static void copyFile(File source, File target) throws IOException {
        copyFile(source, target, createBuffer());
    }

    public static void copyFile(File source, File target, byte[] buffer) throws IOException {
        FileInputStream in = new FileInputStream(source);
        copy(in, target, buffer);
    }

    public static void copy(InputStream in, File target) throws IOException {
        copy(in, target, createBuffer());
    }

    public static void copy(InputStream in, File target, byte[] buffer) throws IOException
    {
        try {
            FileOutputStream out = new FileOutputStream(target);
            try {
                int size;
                while ((size = in.read(buffer)) > 0)
                    out.write(buffer, 0, size);
            }
            finally {
                out.close();
            }
        }
        finally {
            in.close();
        }
    }

    public static void copyFilesOf(File sourceDir, final FileFilter filter, File targetDir, final byte[] buffer) throws IOException {
        File[] files = (filter != null) ? sourceDir.listFiles(filter) : sourceDir.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (targetDir.exists() == false)
                targetDir.mkdir();

            File source = files[i];
            File target = new File(targetDir, source.getName());

            if (source.isDirectory())
                copyFilesOf(source, filter, new File(targetDir, source.getName()), buffer);
            else
                copyFile(source, target, buffer);
        }
    }

    public static void copyFilesOf(File sourceDir, final FileFilter filter, File targetDir) throws IOException {
        copyFilesOf(sourceDir, filter, targetDir, new byte[4096]);
    }

    public static void copyFilesOf(File sourceDir, File targetDir) throws IOException {
        copyFilesOf(sourceDir, null, targetDir, new byte[4096]);
    }

    public static byte[] readBinaryFile(File file) throws IOException {
        return readBinaryFile(new FileInputStream(file));
    }

    public static byte[] readBinaryFile(String fileName) throws IOException {
        return readBinaryFile(new FileInputStream(fileName));
    }

    public static byte[] readBinaryFile(InputStream stream) throws IOException {
    	return IOUtils.toByteArray(stream);
    }

    public static String readTextFile(File file, String charset) throws IOException {
        return readTextFile(new FileInputStream(file), charset);
    }

    public static String readTextFile(String fileName, String charset) throws IOException {
        return readTextFile(new FileInputStream(fileName), charset);
    }

    public static String readTextFile(InputStream stream, String charset) throws IOException {
        if (charset == null)
            return new String(readBinaryFile(stream));
        else
            return new String(readBinaryFile(stream), charset);
    }

    public static void writeBinaryFile(String fileName, byte[] content) throws IOException {
        writeBinaryFile(new File(fileName), content);
    }

    public static void writeBinaryFile(File target, byte[] content) throws IOException {
        copy(new ByteArrayInputStream(content), target);
    }

    public static void writeTextFile(String fileName, String content, String charset) throws IOException {
        writeTextFile(new File(fileName), content, charset);
    }

    public static void writeTextFile(File target, String content, String charset) throws IOException {
        byte[] bytes = charset==null ? content.getBytes() : content.getBytes(charset);
        writeBinaryFile(target, bytes);
    }
}
