package com.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jboss.logging.Logger;

import java.io.InputStream;

@ApplicationScoped
public class CvTextExtractorService {

    private static final Logger log = Logger.getLogger(CvTextExtractorService.class);

    public String extractText(InputStream inputStream, String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("Tên file không được trống.");
        }
        
        String lowerName = fileName.toLowerCase();
        try {
            if (lowerName.endsWith(".pdf")) {
                return extractTextFromPdf(inputStream);
            } else if (lowerName.endsWith(".docx")) {
                return extractTextFromDocx(inputStream);
            } else {
                throw new IllegalArgumentException("Định dạng file không được hỗ trợ. Vui lòng tải lên file .pdf hoặc .docx.");
            }
        } catch (Exception e) {
            log.error("Lỗi khi trích xuất text từ file " + fileName, e);
            throw new RuntimeException("Không thể đọc được nội dung của file CV: " + e.getMessage(), e);
        }
    }

    public String extractTextFromPdf(InputStream inputStream) throws Exception {
        log.info("Bắt đầu đọc text từ file PDF...");
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.info("Đọc PDF thành công, số ký tự trích xuất: " + (text != null ? text.length() : 0));
            return text;
        }
    }

    public String extractTextFromDocx(InputStream inputStream) throws Exception {
        log.info("Bắt đầu đọc text từ file Word (.docx)...");
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            String text = extractor.getText();
            log.info("Đọc Word thành công, số ký tự trích xuất: " + (text != null ? text.length() : 0));
            return text;
        }
    }
}
