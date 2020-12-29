package com.lantushenkoao.iul.subsititutors;

import com.lantushenkoao.iul.FileDescriptor;

import java.text.SimpleDateFormat;
import java.util.List;

public class SplitTemplateSubstitutor implements TemplateSubstitutor {
    public static String PAGE_NUMBER = "$page_num";
    public static String TOTAL_PAGES = "$total_pages";
    public static String FILE_NAME = "$file_name";
    public static String FILE_HASH = "$file_hash";
    public static String FILE_UPDATED_DATE = "$file_updated_date";
    public static String FILE_SIZE = "$file_size";
    public static String ITEM_NUMBER = "$item_num";

    @Override
    public String substitute(String cellText, List<FileDescriptor> fds, int pageNum, int totalPages) {
        FileDescriptor fd = fds.get(0);
        return cellText.replace(PAGE_NUMBER, Integer.toString(pageNum))
                .replace(TOTAL_PAGES, Integer.toString(totalPages))
                .replace(FILE_NAME, fd.getName())
                .replace(FILE_HASH, fd.getHash())
                .replace(ITEM_NUMBER, Integer.toString(pageNum))
                .replace(FILE_UPDATED_DATE, fd.getUpdatedAt() != null ? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(fd.getUpdatedAt().toMillis()): "")
                .replace(FILE_SIZE, fd.getSize());

    }

}
