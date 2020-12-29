package com.lantushenkoao.iul;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class FileDescriptorLoader {

    private MessageHandler messageHandler;

    public FileDescriptorLoader(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }


    public List<FileDescriptor> load(String directoryPath) throws Exception{
        List<FileDescriptor> result = new ArrayList<>();
        final AtomicInteger itemNo = new AtomicInteger(0);
        Files.walk(Paths.get(directoryPath))
                .filter(Files::isRegularFile)
                .forEach(f->{
                    try {
                        File file = f.toFile();
                        Path path = Paths.get(file.getAbsolutePath());
                        BasicFileAttributes attr =
                                Files.readAttributes(path, BasicFileAttributes.class);

                        String md5 = "";
                        try (InputStream is = Files.newInputStream(path)) {
                            md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
                        }

                        result.add(new FileDescriptor(file.getName(),
                                Files.size(path),
                                attr.lastModifiedTime(),
                                md5,
                                itemNo.incrementAndGet()));
                    }catch (Exception e){
                        this.messageHandler.handleError(e);
                    }
                });

//        for(File file: Files.walk(Paths.get(directoryPath))
//                .filter(Files::isRegularFile)){
//
////        Objects.requireNonNull(dir.listFiles())){
//            Path path = Paths.get(file.getAbsolutePath());
//            BasicFileAttributes attr =
//                    Files.readAttributes(path, BasicFileAttributes.class);
//
//            String md5 = "";
//            try (InputStream is = Files.newInputStream(path)) {
//                md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
//            }
//
//            result.add(new FileDescriptor(file.getName(),
//                    Files.size(path),
//                    attr.lastModifiedTime(),
//                    md5));
//        }
        return result;
    }
}
