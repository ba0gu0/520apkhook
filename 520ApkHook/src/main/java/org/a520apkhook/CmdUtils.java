package org.a520apkhook;

import org.apache.commons.lang3.ArrayUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

public class CmdUtils {

    private static final String TAG = "CmdUtils";

    public static Boolean RunCommand(File file, String[] args) {
        return RunCommand(file, args, new StringBuilder());
    }

    public static Boolean RunCommand(File file, String[] args, StringBuilder output) {

        // 确保文件具有执行权限，仅在 Linux 和 macOS 上执行
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
            try {
                Path filePath = file.toPath();
                Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(filePath);
                if (!permissions.contains(PosixFilePermission.OWNER_EXECUTE)) {
                    permissions.add(PosixFilePermission.OWNER_EXECUTE);
                    Files.setPosixFilePermissions(filePath, permissions);
                    LogUtils.debug(TAG, "Added execute permission to " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                LogUtils.warn(TAG, "Failed to set execute permission: " + e.getMessage());
                return false;
            }
        }

        String[] fullCommand;
        if (file.getName().endsWith(".jar")) {
            fullCommand = (String[]) ArrayUtils.addAll(new String[]{"java", "-jar", file.getAbsolutePath()}, args);
        } else {
            fullCommand = (String[]) ArrayUtils.addAll(new String[]{file.getAbsolutePath()}, args);
        }

        LogUtils.debug(TAG, "执行命令：" + String.join(" ", fullCommand));

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(fullCommand);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 获取标准输出流
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());

                if (file.getName().endsWith(".jar")){
                    LogUtils.info(TAG, line);
                }
            }
            // 等待进程执行完毕
            int exitCode = process.waitFor();

            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            LogUtils.warn(TAG, "命令执行失败: " + e.getMessage());
            return false;
        }
    }

    static void ReplaceFileContent(Path filePath, String regex, String repl) throws IOException {
        String readFileData = Files.readString(filePath, StandardCharsets.UTF_8);
        readFileData = readFileData.replaceAll(regex, repl);

        try (BufferedWriter byteWrite = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            byteWrite.write(readFileData);
        }
    }

    public static void ExtractResource(String resourcePath, String outputPath) throws IOException {
        try (InputStream inputStream = Config.class.getResourceAsStream(resourcePath);
             OutputStream outputStream = new FileOutputStream(outputPath)) {

            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }
    }

    public static Document readXmlFile(File xmlFile) {
        SAXReader xmlReader = new SAXReader();
        xmlReader.setEncoding(StandardCharsets.UTF_8.name());
        Document document = null;

        try (InputStream inputStream = new FileInputStream(xmlFile);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            document = xmlReader.read(inputStreamReader);
        } catch (IOException | DocumentException e) {
            LogUtils.warn(TAG, "读取 XML 文件失败: " + e.getMessage());
        }

        return document;
    }

    public static boolean writeXmlFile(Document document, File xmlFile) {
        try (OutputStream outputStream = new FileOutputStream(xmlFile);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            // 创建格式化选项
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding(StandardCharsets.UTF_8.name());

            XMLWriter writer = new XMLWriter(outputStreamWriter, format);
            writer.write(document);
            writer.close();
//            LogUtils.info(TAG, "写入 XML 文件成功.");
        } catch (IOException e) {
            LogUtils.error(TAG, "写入 XML 文件失败: " + e.getMessage());
            return false;
        }
        return true;
    }
}
