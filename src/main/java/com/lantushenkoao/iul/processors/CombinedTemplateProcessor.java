package com.lantushenkoao.iul.processors;

import com.lantushenkoao.iul.FileDescriptor;
import com.lantushenkoao.iul.MessageHandler;
import com.lantushenkoao.iul.TemplateFactory;
import com.lantushenkoao.iul.WordTemplateHelper;
import com.lantushenkoao.iul.subsititutors.CombinedTemplateSubstitutor;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombinedTemplateProcessor {
    private WordTemplateHelper wordProcessor;
    private TemplateFactory.Template template;
    private List<FileDescriptor> files;
    private String destinationFileName;
    private MessageHandler handler;

    public CombinedTemplateProcessor(MessageHandler handler, List<FileDescriptor> files, String destinationFileName) {
        this.files = files;
        this.handler = handler;
        this.template = new TemplateFactory().find(TemplateFactory.TEMPLATE_COMBINED);
        this.destinationFileName = destinationFileName;
    }

    public void writeFilesToTable() {
        try (XWPFDocument document = new XWPFDocument(
                getClass().getResourceAsStream(template.getTemplate()))) {

            this.wordProcessor = new WordTemplateHelper(handler, files, destinationFileName,
                    document, new CombinedTemplateSubstitutor());
            //we have 3 files per page + 1 page for contents
            int totalPages = 2 + (files.size() / 3);
            this.wordProcessor.setTotalPages(totalPages);

            List<FileDescriptor> filesNormalised = new ArrayList<>(files);
            while (filesNormalised.size() % 3 > 0) {
                //files array should be multiple to 3 because
                //there are 3 files per page
                filesNormalised.add(new FileDescriptor());
            }
            XWPFTable sourceTable = document.getTableArray(0);
            XWPFTable contentsTable = document.getTableArray(1);
            for (int i = 0; i < filesNormalised.size(); i = i + 3) {
                System.out.println("Cloning table for index " + i);

                List<FileDescriptor> fds = Arrays.asList(filesNormalised.get(i),
                        filesNormalised.get(i + 1),
                        filesNormalised.get(i + 2));
                XWPFTable table = wordProcessor.cloneTable(sourceTable, fds, (i + 3) / 3);

                if (i > 0) {
                    wordProcessor.insertLineBreakBeforeTable(table);
                }
            }

            //remove original table
            int position = document.getPosOfTable( sourceTable );
            document.removeBodyElement( position );

            wordProcessor.replaceInTable(contentsTable, CombinedTemplateSubstitutor.PAGE_NUMBER, Integer.toString(totalPages));
            wordProcessor.replaceInTable(contentsTable, CombinedTemplateSubstitutor.TOTAL_PAGES, Integer.toString(totalPages));

            try (OutputStream os = wordProcessor.createDestinationFile()) {
                document.write(os);
            }

            handler.handleMessage(String.format("Файл создан успешно. \nОбработано %d файлов. \n Путь к файлу с результатом: \n %s",
                    files.size(), new File(destinationFileName).getAbsolutePath()));
        } catch (Exception e) {
            handler.handleError(e);
        }
    }
}
