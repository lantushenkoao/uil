package com.lantushenkoao.iul;

import com.lantushenkoao.iul.subsititutors.TemplateSubstitutor;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;

import java.io.*;
import java.util.List;

public class WordTemplateHelper {
    private MessageHandler handler;
    private List<FileDescriptor> files;
    private String destinationFileName;
    private XWPFDocument document;
    private TemplateSubstitutor templateSubstitutor;

    private int totalPages;

    public WordTemplateHelper(MessageHandler handler, List<FileDescriptor> files, String destinationFileName,
                              XWPFDocument document, TemplateSubstitutor templateSubstitutor){
        this.handler = handler;
        this.files = files;
        this.destinationFileName = destinationFileName;
        this.document = document;
        this.templateSubstitutor = templateSubstitutor;
    }


    public OutputStream createDestinationFile() throws Exception{
        File destination = new File(destinationFileName);
        if(destination.exists()){
            destination.delete();
        }
        destination.createNewFile();
        OutputStream os = new FileOutputStream(destinationFileName);
        return os;
    }

    public void insertLineBreakInTheEnd(){
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.addBreak(BreakType.PAGE);
    }

    public void insertLineBreakBeforeTable(XWPFTable table){
        document.insertNewParagraph(table.getCTTbl().newCursor())
                .createRun()
                .addBreak(BreakType.PAGE);
    }

    public void replaceInTable(XWPFTable table, String pattern, String value) {
        for(var row: table.getRows()){
            for(var cell: row.getTableCells()){
                for (int p = 0; p < cell.getBodyElements().size(); p++) {
                    IBodyElement elem = cell.getBodyElements().get(p);
                    if (!(elem instanceof XWPFParagraph)) {
                        continue;
                    }
                    XWPFParagraph par = (XWPFParagraph) elem;
                    for (int i=0; i<par.getRuns().size(); i++ ) {
                        XWPFRun run = par.getRuns().get(i);
                        String srcText = run.getText(0);
                        run.setText(srcText.replace(pattern, value), 0);
                    }
                }
            }
        }
    }

    public XWPFTable cloneTable(XWPFTable sourceTable, List<FileDescriptor> fds, int pageNum){
        XWPFTable newTable = document.insertNewTbl(sourceTable.getCTTbl().newCursor());
        newTable.getCTTbl().setTblPr(sourceTable.getCTTbl().getTblPr());
        newTable.getCTTbl().setTblGrid(sourceTable.getCTTbl().getTblGrid());

        for(var sourceRow: sourceTable.getRows()){
            XWPFTableRow newRow = newTable.createRow();
            newRow.getCtRow().setTrPr(sourceRow.getCtRow().getTrPr());

            for(var sourceCell: sourceRow.getTableCells()){
                XWPFTableCell newCell = newRow.createCell();

                String srcText = sourceCell.getText();
                String newText = templateSubstitutor.substitute(srcText, fds, pageNum, this.files.size());
                newCell.setText(newText);
                newCell.getCTTc().setTcPr(sourceCell.getCTTc().getTcPr());
                newCell.setWidth(Integer.toString(sourceCell.getWidth()));

                XmlCursor cellCursor = newCell.getParagraphArray(0).getCTP().newCursor();
                for (int p = 0; p < sourceCell.getBodyElements().size(); p++) {
                    IBodyElement elem = sourceCell.getBodyElements().get(p);
                    if (elem instanceof XWPFParagraph) {
                        XWPFParagraph targetPar = newCell.insertNewParagraph(cellCursor);
                        cellCursor.toNextToken();
                        XWPFParagraph par = (XWPFParagraph) elem;
                        copyParagraph(par, targetPar, fds, pageNum);
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
    private void copyParagraph(XWPFParagraph source, XWPFParagraph target, List<FileDescriptor> fds, int pageNum) {
        target.getCTP().setPPr(source.getCTP().getPPr());
        for (int i=0; i<source.getRuns().size(); i++ ) {
            XWPFRun run = source.getRuns().get(i);
            XWPFRun targetRun = target.createRun();
            //copy formatting
            targetRun.getCTR().setRPr(run.getCTR().getRPr());
            //no images just copy text
            String srcText = run.getText(0);
            String newText = templateSubstitutor.substitute(srcText, fds, pageNum, totalPages);
            targetRun.setText(newText);
        }
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
