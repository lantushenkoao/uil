package com.lantushenkoao.iul.processors;

import com.lantushenkoao.iul.FileDescriptor;
import com.lantushenkoao.iul.MessageHandler;
import com.lantushenkoao.iul.TemplateFactory;
import com.lantushenkoao.iul.WordTemplateHelper;
import com.lantushenkoao.iul.subsititutors.SplitTemplateSubstitutor;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class SplitTemplateProcessor {

    private WordTemplateHelper wordProcessor;
    private TemplateFactory.Template template;
    private List<FileDescriptor> files;
    private String destinationFileName;
    private MessageHandler handler;

    public SplitTemplateProcessor(MessageHandler handler, List<FileDescriptor> files, String destinationFileName, File templateFile){
        this.files = files;
        this.handler = handler;
        this.template = new TemplateFactory.Template(templateFile.getName(), templateFile.getAbsolutePath());
        this.destinationFileName = destinationFileName;
    }

    public void writeFilesToTable(){
        try (XWPFDocument document = new XWPFDocument(
                new FileInputStream(template.getTemplate()))) {

            this.wordProcessor = new WordTemplateHelper(handler, files, destinationFileName,
                    document, new SplitTemplateSubstitutor());
            wordProcessor.setTotalPages(files.size());

            XWPFTable sourceTable = document.getTableArray(0);
            int pageNum = 1;
            for(FileDescriptor file: files){
                wordProcessor.cloneTable(sourceTable, Arrays.asList(file), pageNum++);
            }

            //remove original table
            int position = document.getPosOfTable( sourceTable );
            document.removeBodyElement( position );

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
