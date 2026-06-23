package com.example.v112.rag;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfTextService {
    public String extract(MultipartFile file) {
        try (var in = file.getInputStream(); var document = PDDocument.load(in)) {
            return new PDFTextStripper().getText(document);
        } catch (Exception e) {
            throw new IllegalStateException("PDF extraction failed", e);
        }
    }
}
