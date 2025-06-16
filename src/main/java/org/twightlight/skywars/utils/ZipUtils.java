package org.twightlight.skywars.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    public static void zip(File sourceDir, File zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
             zipFolderRecursive(sourceDir, "", zos);
        }
    }

    private static void zipFolderRecursive(File currentFile, String entryName, ZipOutputStream zos) throws IOException {
        String name = currentFile.getName();
        if (name.equals("uid.dat") || name.equals("session.lock")) return;

        if (currentFile.isDirectory()) {
            File[] children = currentFile.listFiles();
            if (children != null) {
                for (File child : children) {
                    String childEntryName = entryName.isEmpty() ? child.getName() : entryName + "/" + child.getName();
                    zipFolderRecursive(child, childEntryName, zos);
                }
            }
        } else {
            try (FileInputStream fis = new FileInputStream(currentFile)) {
                ZipEntry entry = new ZipEntry(entryName.replaceFirst("^/", ""));
                zos.putNextEntry(entry);

                byte[] buffer = new byte[4096];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, len);
                }

                zos.closeEntry();
            }
        }
    }

    public static void unzip(File zipFile, File destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, entry.getName());
                File canonicalDestDir = destDir.getCanonicalFile();
                File canonicalNewFile = newFile.getCanonicalFile();
                if (!canonicalNewFile.toPath().startsWith(canonicalDestDir.toPath())) {
                    throw new IOException("Malicious ZIP: Entry outside target directory!");
                }
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    newFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
}
