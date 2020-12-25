package com.lantushenkoao.iul;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class WordProcessor {
    public static final String TEMPLATE_NAME = "/template.docx";
    private MessageHandler handler;
    private List<FileDescriptor> files;
    private String destinationFileName;

    public WordProcessor(MessageHandler handler, List<FileDescriptor> files, String destinationFileName){
        this.handler = handler;
        this.files = files;
        this.destinationFileName = destinationFileName;
    }

    public void writeFilesToTable() {
        try (XWPFDocument document = new XWPFDocument(
                getClass().getResourceAsStream(TEMPLATE_NAME))) {
            XWPFTable sourceTable = document.getTableArray(0);
            int pageNum = 1;
            for(FileDescriptor file: files){
                cloneTable(document, sourceTable, file, pageNum++);

                if(pageNum < files.size()) {
                    // add page break
                    XWPFParagraph paragraph = document.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.addBreak(BreakType.PAGE);
                }
            }

            //remove original table
            int position = document.getPosOfTable( sourceTable );
            document.removeBodyElement( position );

            try (OutputStream os = createDestinationFile()) {
                document.write(os);
            }

            handler.handleMessage(String.format("Файл создан успешно. \nОбработано %d файлов. \n Путь к файлу с результатом: \n %s",
                    files.size(), new File(destinationFileName).getAbsolutePath()));
        } catch (Exception e) {
            handler.handleError(e);
        }
    }

    private OutputStream createDestinationFile() throws Exception{
        File destination = new File(destinationFileName);
        if(destination.exists()){
            destination.delete();
        }
        destination.createNewFile();
        OutputStream os = new FileOutputStream(destinationFileName);
        return os;
    }

    private XWPFTable cloneTable(XWPFDocument document, XWPFTable sourceTable, FileDescriptor fd, int pageNum){
        XWPFTable newTable = document.createTable();
        newTable.getCTTbl().setTblPr(sourceTable.getCTTbl().getTblPr());
        newTable.getCTTbl().setTblGrid(sourceTable.getCTTbl().getTblGrid());

        for(var sourceRow: sourceTable.getRows()){
            XWPFTableRow newRow = newTable.createRow();
            newRow.getCtRow().setTrPr(sourceRow.getCtRow().getTrPr());

            for(var sourceCell: sourceRow.getTableCells()){
                XWPFTableCell newCell = newRow.createCell();

                String srcText = sourceCell.getText();
                String newText = TemplateSubstitutor.substitute(srcText, fd, pageNum, this.files.size());
                newCell.setText(newText);

                newCell.getCTTc().setTcPr(sourceCell.getCTTc().getTcPr());
                newCell.setWidth(Integer.toString(sourceCell.getWidth()));

                XmlCursor cursor = newCell.getParagraphArray(0).getCTP().newCursor();
                for (int p = 0; p < sourceCell.getBodyElements().size(); p++) {
                    IBodyElement elem = sourceCell.getBodyElements().get(p);
                    if (elem instanceof XWPFParagraph) {
                        XWPFParagraph targetPar = newCell.insertNewParagraph(cursor);
                        cursor.toNextToken();
                        XWPFParagraph par = (XWPFParagraph) elem;
                        copyParagraph(par, targetPar, fd, pageNum);
                    }
                }
                //newly created cell has one default paragraph we need to remove
                newCell.removeParagraph(newCell.getParagraphs().size()-1);
            }
            newRow.removeCell(0);
        }
        newTable.removeRow(0);
        return newTable;
    }

    //based on https://stackoverflow.com/questions/48322534/apache-poi-how-to-copy-tables-from-one-docx-to-another-docx
    private void copyParagraph(XWPFParagraph source, XWPFParagraph target, FileDescriptor fd, int pageNum) {
        target.getCTP().setPPr(source.getCTP().getPPr());
        for (int i=0; i<source.getRuns().size(); i++ ) {
            XWPFRun run = source.getRuns().get(i);
            XWPFRun targetRun = target.createRun();
            //copy formatting
            targetRun.getCTR().setRPr(run.getCTR().getRPr());
            //no images just copy text
            String srcText = run.getText(0);
            String newText = TemplateSubstitutor.substitute(srcText, fd, pageNum, this.files.size());
            targetRun.setText(newText);
        }
    }

    public static class TemplateSubstitutor {
        public static String PAGE_NUMBER = "$page_num";
        public static String TOTAL_PAGES = "$total_pages";
        public static String FILE_NAME = "$file_name";
        public static String FILE_HASH = "$file_hash";
        public static String FILE_UPDATED_DATE = "$file_updated_date";
        public static String FILE_SIZE = "$file_size";

        public static String substitute(String cellText, FileDescriptor fd, int pageNum, int totalPages) {
            return cellText.replace(PAGE_NUMBER, Integer.toString(pageNum))
                    .replace(TOTAL_PAGES, Integer.toString(totalPages))
                    .replace(FILE_NAME, fd.getName())
                    .replace(FILE_HASH, fd.getHash())
                    .replace(FILE_UPDATED_DATE, fd.getUpdatedAt() != null ? new SimpleDateFormat().format(fd.getUpdatedAt().toMillis()): "")
                    .replace(FILE_SIZE, Long.toString(fd.getSize()));

        }
    }
}
