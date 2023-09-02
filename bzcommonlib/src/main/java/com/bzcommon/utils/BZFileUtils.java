package com.bzcommon.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class BZFileUtils {
    private final static String TAG = BZFileUtils.class.getSimpleName();

    /**
     * 计算文件的md5值
     */
    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            BZLogUtil.e("BZFileUtils", "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            BZLogUtil.e("BZFileUtils", "Exception while getting FileInputStream", e);
            return null;
        }

        //DigestInputStream

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                BZLogUtil.e("BZFileUtils", "Exception on closing MD5 input stream", e);
            }
        }
    }

    /**
     * 计算文件的md5值
     */
    public static String calculateMD5(File updateFile, int offset, int partSize) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            BZLogUtil.e("BZFileUtils", "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            BZLogUtil.e("BZFileUtils", "Exception while getting FileInputStream", e);
            return null;
        }

        //DigestInputStream
        final int buffSize = 8192;//单块大小
        byte[] buffer = new byte[buffSize];
        int read;
        try {
            if (offset > 0) {
                is.skip(offset);
            }
            int byteCount = Math.min(buffSize, partSize), byteLen = 0;
            while ((read = is.read(buffer, 0, byteCount)) > 0 && byteLen < partSize) {
                digest.update(buffer, 0, read);
                byteLen += read;
                //检测最后一块，避免多读数据
                if (byteLen + buffSize > partSize) {
                    byteCount = partSize - byteLen;
                }
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                BZLogUtil.e("BZFileUtils", "Exception on closing MD5 input stream", e);
            }
        }
    }

    /**
     * 检测文件是否可用
     */
    public static boolean fileIsEnable(File f) {
        return f != null && f.exists() && f.canRead() && (f.isDirectory() || (f.isFile() && f.length() > 0));
    }

    /**
     * 检测文件是否可用
     */
    public static boolean fileIsEnable(String path) {
        if (BZStringUtils.isNotEmpty(path)) {
            File f = new File(path);
            return f.exists() && f.canRead() && (f.isDirectory() || (f.isFile() && f.length() > 0));
        }
        return false;
    }


    public static long getFileSize(String fn) {
        File f = null;
        long size = 0;

        try {
            f = new File(fn);
            size = f.length();
        } catch (Exception e) {
            BZLogUtil.e(e);
        } finally {
            f = null;
        }
        return size < 0 ? 0 : size;
    }

    public static long getFileSize(File fn) {
        return fn == null ? 0 : fn.length();
    }

    public static String getFileType(String fn, String defaultType) {
        FileNameMap fNameMap = URLConnection.getFileNameMap();
        String type = fNameMap.getContentTypeFor(fn);
        return type == null ? defaultType : type;
    }

    public static String getFileType(String fn) {
        return getFileType(fn, "application/octet-stream");
    }

    public static String getFileExtension(String filename) {
        String extension = "";
        if (filename != null) {
            int dotPos = filename.lastIndexOf(".");
            if (dotPos >= 0 && dotPos < filename.length() - 1) {
                extension = filename.substring(dotPos + 1);
            }
        }
        return extension.toLowerCase();
    }

    public static void deleteFiles(List<String> filePathList) {
        if (null == filePathList || filePathList.isEmpty()) {
            return;
        }
        for (String path : filePathList) {
            deleteFile(path);
        }
    }

    public static boolean deleteFile(File f) {
        if (f != null && f.exists() && !f.isDirectory()) {
            return f.delete();
        }
        return false;
    }

    public static void deleteDir(File f) {
        if (f != null && f.exists() && f.isDirectory()) {
            for (File file : f.listFiles()) {
                if (file.isDirectory())
                    deleteDir(file);
                file.delete();
            }
            f.delete();
        }
    }

    public static void deleteDir(String f) {
        if (f != null && f.length() > 0) {
            deleteDir(new File(f));
        }
    }

    public static boolean deleteFile(String f) {
        if (f != null && f.length() > 0) {
            return deleteFile(new File(f));
        }
        return false;
    }

    /**
     * read file
     *
     * @param charsetName The name of a supported {@link java.nio.charset.Charset
     *                    </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static String readTextFile(File file, String charsetName) {
        StringBuilder fileContent = new StringBuilder();
        if (file == null || !file.exists() || !file.isFile()) {
            BZLogUtil.e("readTextFile file == null || !file.exists() || !file.isFile() path=" + (null == file ? null : file.getAbsolutePath()));
            return fileContent.toString();
        }

        FileInputStream inputStream = null;
        InputStreamReader is = null;
        BufferedReader reader = null;
        try {
            inputStream = new FileInputStream(file);
            is = new InputStreamReader(inputStream, charsetName);
            reader = new BufferedReader(is);
            String line;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            inputStream.close();
            is.close();
        } catch (Exception e) {
            BZLogUtil.e(e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    BZLogUtil.e(e.getMessage(), e);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    BZLogUtil.e(e.getMessage(), e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    BZLogUtil.e(e.getMessage(), e);
                }
            }
        }
        return fileContent.toString();
    }

    public static String readTextFile(File file) {
        return readTextFile(file, "utf-8");
    }

    public static String readTextFile(String filePath) {
        return readTextFile(new File(filePath));
    }

    public static String readAssetsFile(Context context, String filePath) {
        if (null == context || null == filePath) {
            return null;
        }
        String content = null;
        try {
            InputStream inputStream = context.getAssets().open(filePath);
            content = getString(inputStream);
            inputStream.close();
        } catch (Throwable e1) {
            BZLogUtil.e(e1);
        }
        return content;
    }


    public static String getString(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        try {
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(reader);

            char[] buffer = new char[2048];
            int length;
            while ((length = bufferedReader.read(buffer)) > 0) {
                sb.append(new String(buffer, 0, length));
            }
            reader.close();
            bufferedReader.close();
        } catch (Exception e) {
            BZLogUtil.e(e);
        }
        return sb.toString();
    }

    /**
     * 文件拷贝
     */
    @SuppressWarnings("all")
    public static boolean fileCopy(String from, String to) {
        try {
            createNewFile(to);
            FileInputStream fileInputStream = new FileInputStream(from);
            boolean fileCopy = fileCopy(fileInputStream, new FileOutputStream(to));
            fileInputStream.close();
            return fileCopy;
        } catch (Throwable e) {
            BZLogUtil.e(e);
        }
        return false;
    }

    /**
     * 文件移动
     */
    @SuppressWarnings("all")
    public static boolean fileMove(String from, String to) {
        try {
            createNewFile(to);
            FileInputStream fileInputStream = new FileInputStream(from);
            boolean fileCopy = fileCopy(fileInputStream, new FileOutputStream(to));
            fileInputStream.close();
            if (fileCopy) {
                new File((from)).delete();
            }
            return fileCopy;
        } catch (Throwable e) {
            BZLogUtil.e(e);
        }
        return false;
    }

    @SuppressWarnings("all")
    public static boolean fileCopy(InputStream in, String to) {
        if (null == in || null == to) {
            return false;
        }
        try {
            createNewFile(to);
            return fileCopy(in, new FileOutputStream(to));
        } catch (Throwable e) {
            BZLogUtil.e(e);
        }
        return false;
    }

    public static boolean fileCopy(InputStream in, OutputStream out) {
        if (null == in || null == out) {
            closeStream(in);
            closeStream(out);
            return false;
        }
        boolean result = false;
        try {
            int size = 4 * 1024;
            byte[] buffer = new byte[size];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            result = true;
        } catch (Throwable e) {
            BZLogUtil.e(e);
        } finally {
            closeStream(in);
            closeStream(out);
        }
        return result;
    }

    public static void closeStream(Closeable stream) {
        if (null == stream) {
            return;
        }
        try {
            stream.close();
        } catch (Throwable e) {
            BZLogUtil.e(e);
        }
    }

    @SuppressWarnings("all")
    public static File createNewFile(String fileWithPath) {
        File file = new File(fileWithPath);
        if (null == file.getParentFile()) {
            return null;
        }
        return createNewFile(file.getParentFile().getPath() + "/", getFileName(fileWithPath));
    }

    public static String getFileName(String filePath) {
        if (BZStringUtils.isEmpty(filePath)) {
            return filePath;
        } else {
            int filePosition = filePath.lastIndexOf(File.separator);
            return filePosition == -1 ? filePath : filePath.substring(filePosition + 1);
        }
    }

    public static File createNewFile(String fileSavePath, String fileName) {
        File fileDir = new File(fileSavePath);
        if (!fileDir.exists()) {
            boolean mkdirs = fileDir.mkdirs();
            BZLogUtil.i(TAG, "createNewFile mkdirs=" + mkdirs);
        }

        return new File(fileDir, fileName);
    }

    public static void ensureParentPathExist(String path) {
        File fileDir = new File(path);
        File parentFile = fileDir.getParentFile();
        if (null == parentFile) {
            return;
        }
        if (!parentFile.exists()) {
            boolean mkdirs = parentFile.mkdirs();
            BZLogUtil.d(TAG, "ensureParentPathExist mkdirs=" + mkdirs);
        }
    }

    public static void saveFile(String path, String content) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(path);
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            BZLogUtil.e(e);
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (Exception e) {
                    BZLogUtil.e(e);
                }
            }
        }
    }
}
