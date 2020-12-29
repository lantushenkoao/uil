package com.lantushenkoao.iul.subsititutors;

import com.lantushenkoao.iul.FileDescriptor;

import java.text.SimpleDateFormat;
import java.util.List;

public class CombinedTemplateSubstitutor implements TemplateSubstitutor{
    public static String ITEM_NUMBER_PATTERN = "$item_num_%d";
    public static String PAGE_NUMBER = "$page_num";
    public static String TOTAL_PAGES = "$total_pages";
    public static String FILE_NAME_PATTERN = "$file_name_%d";
    public static String FILE_HASH_PATTERN = "$file_hash_%d";
    public static String FILE_UPDATED_DATE_PATTERN = "$file_updated_date_%d";
    public static String FILE_SIZE_PATTERN = "$file_size_%d";

    @Override
    public String substitute(String cellText, List<FileDescriptor> files, int pageNum, int totalPages) {
        String result = cellText;
        for (int i = 0; i < files.size(); i++) {
            FileDescriptor file = files.get(i);
            result = result.replace(PAGE_NUMBER, Integer.toString(pageNum))
                    .replace(TOTAL_PAGES, Integer.toString(totalPages))
                    .replace(String.format(ITEM_NUMBER_PATTERN, i), file.getItemNo())
                    .replace(String.format(FILE_NAME_PATTERN, i), file.getName())
                    .replace(String.format(FILE_HASH_PATTERN, i), file.getHash())
                    .replace(String.format(PAGE_NUMBER, i), Integer.toString(pageNum))
                    .replace(String.format(FILE_UPDATED_DATE_PATTERN, i), file.getUpdatedAt() != null
                            ? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(file.getUpdatedAt().toMillis()) : "")
                    .replace(String.format(FILE_SIZE_PATTERN, i), file.getSize());

        }
        return result;
    }
}
