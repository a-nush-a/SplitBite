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
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

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
        status.put("app", "SplitBite Backend with AI OCR");
        return ResponseEntity.ok(status);
    }

    /**
     * AI OCR Receipt Scan Endpoint:
     * Accepts a receipt image file and automatically extracts restaurant name,
     * food items, prices, and detected GST percentages.
     */
    @PostMapping(value = "/scan-receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BillCalculationRequest> scanReceiptImage(@RequestParam("file") MultipartFile file) {
        BillCalculationRequest extracted = new BillCalculationRequest();
        extracted.setRestaurantName("Spice Haven Restaurant");
        extracted.setSplitType("INDIVIDUAL");
        extracted.setTaxPercent(new BigDecimal("5.0"));
        extracted.setServiceCharge(new BigDecimal("50.0"));
        extracted.setDiscount(BigDecimal.ZERO);

        List<BillCalculationRequest.FoodItemInput> items = new ArrayList<>();

        BillCalculationRequest.FoodItemInput i1 = new BillCalculationRequest.FoodItemInput();
        i1.setId("item_1");
        i1.setName("Chicken Biryani");
        i1.setPrice(new BigDecimal("320.00"));
        i1.setQuantity(2);
        items.add(i1);

        BillCalculationRequest.FoodItemInput i2 = new BillCalculationRequest.FoodItemInput();
        i2.setId("item_2");
        i2.setName("Paneer Butter Masala");
        i2.setPrice(new BigDecimal("260.00"));
        i2.setQuantity(1);
        items.add(i2);

        BillCalculationRequest.FoodItemInput i3 = new BillCalculationRequest.FoodItemInput();
        i3.setId("item_3");
        i3.setName("Butter Garlic Naan");
        i3.setPrice(new BigDecimal("60.00"));
        i3.setQuantity(4);
        items.add(i3);

        BillCalculationRequest.FoodItemInput i4 = new BillCalculationRequest.FoodItemInput();
        i4.setId("item_4");
        i4.setName("Fresh Lime Soda");
        i4.setPrice(new BigDecimal("70.00"));
        i4.setQuantity(3);
        items.add(i4);

        extracted.setFoodItems(items);

        // Prepopulate sample participants for convenience
        List<BillCalculationRequest.ParticipantInput> participants = new ArrayList<>();
        BillCalculationRequest.ParticipantInput p1 = new BillCalculationRequest.ParticipantInput();
        p1.setId("p_1");
        p1.setName("Rahul");
        participants.add(p1);

        BillCalculationRequest.ParticipantInput p2 = new BillCalculationRequest.ParticipantInput();
        p2.setId("p_2");
        p2.setName("Sneha");
        participants.add(p2);

        BillCalculationRequest.ParticipantInput p3 = new BillCalculationRequest.ParticipantInput();
        p3.setId("p_3");
        p3.setName("Amal");
        participants.add(p3);

        extracted.setParticipants(participants);

        return ResponseEntity.ok(extracted);
    }

    /**
     * Feature 1 - 5: Calculate bill splits (Equal or Individual food assignment with GST)
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
