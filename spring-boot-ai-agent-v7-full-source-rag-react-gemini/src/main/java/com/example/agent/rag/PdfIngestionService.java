package com.example.agent.rag;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class PdfIngestionService {
    public String extractText(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream(); PDDocument document = PDDocument.load(inputStream)) {
            return new PDFTextStripper().getText(document);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to extract PDF text", e);
        }
    }
}
