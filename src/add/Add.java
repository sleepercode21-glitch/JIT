package add;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.nio.charset.StandardCharsets;
import java.util.zip.Inflater;
import java.util.zip.Deflater;
import java.util.zip.DataFormatException;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.nio.ByteBuffer;

public class Add {

    public Add() {}
    
    private static byte[] compress (byte[] bytes) {
        Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION);
        deflater.setInput(bytes);
        // indicates no more input data
        deflater.finish();
        
        ByteArrayOutputStream output = new ByteArrayOutputStream(bytes.length);
        // allot buffer size 8kb 
        byte[] buffer = new byte[8192];

        while (!deflater.finished()) {
            // count - how many bytes were read into the buffer
            int count = deflater.deflate(buffer);
            // only write buffer from index 0 to count
            output.write(buffer, 0, count);
        }
        
        deflater.end();
        return output.toByteArray();
    }

    public static byte[] decompress(byte[] compressed) {
        Inflater inflater = new Inflater();
        inflater.setInput(compressed);
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                output.write(buffer, 0, count);
            }
        } catch (DataFormatException e){
            throw new RuntimeException("Currupt compressed data", e);
        } finally {
            inflater.end();
        }

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

    private static boolean verify(String fileDirectory) {
       try (FileInputStream inputStream = new FileInputStream(fileDirectory)) {
          byte[] compressed = inputStream.readAllBytes();
          byte[] decompressed = decompress(compressed); 
          String hash = SHAsum(decompressed);
          System.out.println("hash generated: " + hash);
          String expectedHash = fileDirectory.substring(fileDirectory.lastIndexOf("/")
                                                        - 2).replace("/", ""); 
          return expectedHash.equals(hash);
        
       } catch (IOException e) {
          System.err.println("Failed to verify file: " + fileDirectory);
          e.printStackTrace();
       }
        
        return false;
    }
    public void execute(String[] files) {
        for (String filePath : files) {
            try (FileInputStream inputStream = new FileInputStream(filePath)) {
                
                byte[] byteContent = inputStream.readAllBytes();
                int size = byteContent.length;
                String stringHeader = "blob " + size + "\0"; 
                byte[] byteHeader = stringHeader.getBytes(StandardCharsets.US_ASCII); 
                byte[] byteFile = ByteBuffer.allocate(byteHeader.length + 
                                                      byteContent.length) 
                                                      .put(byteHeader)
                                                      .put(byteContent)
                                                      .array(); 
                String hash = SHAsum(byteFile);
                System.out.println("file hash: " + hash);
                byte[] compressed = compress(byteFile);
                String directory = ".jit/objects/" + hash.substring(0, 2);
                String fileDirectory = ".jit/objects/" + hash.substring(0, 2) + 
                                                     "/" + hash.substring(2);
                // making directory to save file 
                File objectDirectory = new File(directory);
                objectDirectory.mkdirs();
                
                // making a file directory
                File file = new File(fileDirectory); 

                if (!file.exists()) {
                    try (FileOutputStream out = new FileOutputStream(fileDirectory)) {
                        out.write(compressed);
                        if (verify(fileDirectory)) {
                            System.out.println("File verified!");
                        } else {
                            System.out.println("Could not verify file " + 
                            fileDirectory);
                        }
                    }
                }

                
            } catch (IOException e) {
                System.err.println("Failed to read file: " + filePath);
                e.printStackTrace();
            }
        }
    }
}
