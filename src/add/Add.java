package add;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.nio.ByteBuffer;

public class Add {

    public Add() {}
    
    private static byte[] compress (byte[] bytes) {
        Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION);
        deflater.setInput(bytes);
        deflater.finish();

        ByteArrayOutputStream output = new ByteArrayOutputStream(bytes.length);
        byte[] buffer = new byte[8192];

        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            output.write(buffer, 0, count);
        }
        
        deflater.end();
        return output.toByteArray();
    }

    private static String SHAsum(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(data);
            return byteArrayToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            // Fatal error: hashing is a core invariant
            throw new RuntimeException("SHA-1 algorithm not available", e);
        }
    }

    private static String byteArrayToHex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public void execute(String[] files) {
        for (String filePath : files) {
            try (FileInputStream inputStream = new FileInputStream(filePath)) {
                
                int size = inputStream.available();
                String stringHeader = "blob " + size + "\0"; 
                byte[] byteHeader = stringHeader.getBytes(StandardCharsets.UTF_8); 
                byte[] byteContent = inputStream.readAllBytes();
                byte[] byteFile = ByteBuffer.allocate(byteHeader.length + 
                                                      byteContent.length) 
                                                      .put(byteHeader)
                                                      .put(byteContent)
                                                      .array(); 
                String hash = SHAsum(byteFile);
                byte[] compressed = compress(byteFile);
                String directory = ".jit/objects/" + hash.substring(0, 2);
                String fileDirectory = ".jit/objects/" + hash.substring(0, 2) + "/"
                                                     + "/" + hash.substring(2);
                // making directory to save file 
                File objectDirectory = new File(directory);
                objectDirectory.mkdirs();
                File file = new File(fileDirectory);
                
                if (!file.exists()) {
                    try (FileOutputStream out = new FileOutputStream(fileDirectory)) {
                        out.write(compressed);
                    }
                }         
            } catch (IOException e) {
                System.err.println("Failed to read file: " + filePath);
                e.printStackTrace();
            }
        }
    }
}
