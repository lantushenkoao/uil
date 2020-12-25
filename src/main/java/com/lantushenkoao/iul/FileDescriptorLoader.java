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

public class FileDescriptorLoader {

    public List<FileDescriptor> load(String directoryPath) throws Exception{
        List<FileDescriptor> result = new ArrayList<>();
        File dir = new File(directoryPath);
        for(File file: Objects.requireNonNull(dir.listFiles())){
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
                    md5));
        }
        return result;
    }
}
