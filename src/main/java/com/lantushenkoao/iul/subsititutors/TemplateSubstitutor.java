package com.lantushenkoao.iul.subsititutors;

import com.lantushenkoao.iul.FileDescriptor;

import java.util.List;

public interface TemplateSubstitutor {
    String substitute(String cellText, List<FileDescriptor> fd, int pageNum, int totalPages);
}
