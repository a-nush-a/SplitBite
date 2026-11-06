package com.splitbite.controller;

import com.splitbite.dto.BillCalculationRequest;
import com.splitbite.dto.BillCalculationResponse;
import com.splitbite.service.BillCalculationService;
import com.splitbite.service.PdfExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
public class BillController {

    @Autowired
    private BillCalculationService billCalculationService;

    @Autowired
    private PdfExportService pdfExportService;

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("app", "SplitBite Backend");
        return ResponseEntity.ok(status);
    }

    /**
     * Feature 1 - 5: Calculate bill splits (Equal or Individual food assignment)
     */
    @PostMapping("/calculate")
    public ResponseEntity<BillCalculationResponse> calculateBill(@RequestBody BillCalculationRequest request) {
        BillCalculationResponse response = billCalculationService.calculateAndSaveBill(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Feature 6: Download individual participant bill as PDF
     */
    @PostMapping("/pdf/individual")
    public ResponseEntity<byte[]> downloadIndividualPdf(
            @RequestBody BillCalculationResponse billData,
            @RequestParam String participantId) {
        try {
            byte[] pdfBytes = pdfExportService.generateIndividualBillPdf(billData, participantId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "SplitBite_Individual_Bill.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Feature 7: Download combined bill for all participants as PDF
     */
    @PostMapping("/pdf/all")
    public ResponseEntity<byte[]> downloadAllBillsPdf(@RequestBody BillCalculationResponse billData) {
        try {
            byte[] pdfBytes = pdfExportService.generateAllBillsPdf(billData);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "SplitBite_Group_Bill.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
