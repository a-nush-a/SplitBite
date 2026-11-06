package com.splitbite.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.splitbite.dto.BillCalculationResponse;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfExportService {

    private static final Color NAVY = new Color(15, 23, 42);          // #0f172a
    private static final Color BRAND_GREEN = new Color(16, 185, 129); // #10b981
    private static final Color LIGHT_BG = new Color(248, 250, 252);   // #f8fafc
    private static final Color BORDER_GRAY = new Color(226, 232, 240); // #e2e8f0

    /**
     * Generates a PDF for a single participant's individual bill with GST breakdown
     */
    public byte[] generateIndividualBillPdf(BillCalculationResponse billData, String participantId) throws Exception {
        BillCalculationResponse.ParticipantBillDTO participant = null;
        for (BillCalculationResponse.ParticipantBillDTO p : billData.getParticipantBills()) {
            if (p.getParticipantId().equals(participantId)) {
                participant = p;
                break;
            }
        }

        if (participant == null) {
            throw new IllegalArgumentException("Participant not found with ID: " + participantId);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, out);

        document.open();

        // 1. Header Banner
        Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BRAND_GREEN);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);

        Paragraph header = new Paragraph("SplitBite", brandFont);
        header.add(new Chunk("  – Smart Restaurant Bill Splitting (GST Included)\n", subtitleFont));
        header.add(new Chunk("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + "\n\n", dateFont));
        document.add(header);

        // 2. Info Box
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(15);

        PdfPCell rCell = new PdfPCell(new Phrase("Restaurant: " + billData.getRestaurantName(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, NAVY)));
        rCell.setBackgroundColor(LIGHT_BG);
        rCell.setPadding(10);
        rCell.setBorderColor(BORDER_GRAY);

        PdfPCell pCell = new PdfPCell(new Phrase("Participant: " + participant.getParticipantName(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, NAVY)));
        pCell.setBackgroundColor(LIGHT_BG);
        pCell.setPadding(10);
        pCell.setBorderColor(BORDER_GRAY);

        infoTable.addCell(rCell);
        infoTable.addCell(pCell);
        document.add(infoTable);

        // 3. Items Table
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{40, 15, 15, 15, 15});
        table.setSpacingAfter(15);

        String[] headers = {"Food Item", "Quantity", "Unit Price", "Portion", "Share Cost"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
            cell.setBackgroundColor(NAVY);
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        for (BillCalculationResponse.AssignedItemDTO item : participant.getAssignedItems()) {
            table.addCell(createCell(item.getName(), Element.ALIGN_LEFT));
            table.addCell(createCell(String.valueOf(item.getQuantity()), Element.ALIGN_CENTER));
            table.addCell(createCell("INR " + item.getPrice(), Element.ALIGN_RIGHT));
            table.addCell(createCell(item.getIsShared() ? "Shared (1/" + item.getSharedWithCount() + ")" : "Individual", Element.ALIGN_CENTER));
            table.addCell(createCell("INR " + item.getItemCostForParticipant(), Element.ALIGN_RIGHT));
        }

        document.add(table);

        // 4. Tax Breakdown Table
        PdfPTable taxTable = new PdfPTable(2);
        taxTable.setWidthPercentage(60);
        taxTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        taxTable.setSpacingAfter(15);

        taxTable.addCell(createCell("Food Subtotal:", Element.ALIGN_LEFT));
        taxTable.addCell(createCell("INR " + participant.getFoodSubtotal(), Element.ALIGN_RIGHT));

        taxTable.addCell(createCell("GST Share (" + billData.getTaxPercent() + "%):", Element.ALIGN_LEFT));
        taxTable.addCell(createCell("+ INR " + participant.getTaxShare(), Element.ALIGN_RIGHT));

        if (participant.getServiceShare().compareTo(java.math.BigDecimal.ZERO) > 0) {
            taxTable.addCell(createCell("Service Charge Share:", Element.ALIGN_LEFT));
            taxTable.addCell(createCell("+ INR " + participant.getServiceShare(), Element.ALIGN_RIGHT));
        }

        PdfPCell totalLabel = new PdfPCell(new Phrase("Total Payable:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, NAVY)));
        totalLabel.setPadding(6);
        totalLabel.setBackgroundColor(LIGHT_BG);
        taxTable.addCell(totalLabel);

        PdfPCell totalVal = new PdfPCell(new Phrase("INR " + participant.getFinalPayable(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BRAND_GREEN)));
        totalVal.setPadding(6);
        totalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalVal.setBackgroundColor(LIGHT_BG);
        taxTable.addCell(totalVal);

        document.add(taxTable);

        // 5. Footer
        Paragraph footer = new Paragraph("Thank you for using SplitBite! Accurate & Fair Dining Bill Splitting with GST.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return out.toByteArray();
    }

    /**
     * Generates a combined PDF for all participants in the bill with GST breakdown
     */
    public byte[] generateAllBillsPdf(BillCalculationResponse billData) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, out);

        document.open();

        Paragraph header = new Paragraph("SplitBite – Master Group Bill", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BRAND_GREEN));
        header.add(new Chunk("\nRestaurant: " + billData.getRestaurantName() + " | Grand Total: INR " + billData.getTotalBill() + "\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, NAVY)));
        header.add(new Chunk("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + "\n\n", FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY)));
        document.add(header);

        PdfPTable summaryTable = new PdfPTable(4);
        summaryTable.setWidthPercentage(100);
        summaryTable.setWidths(new float[]{10, 45, 25, 20});
        summaryTable.setSpacingAfter(20);

        String[] headers = {"#", "Participant Name", "Food Subtotal", "Final Payable"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
            cell.setBackgroundColor(NAVY);
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            summaryTable.addCell(cell);
        }

        int count = 1;
        for (BillCalculationResponse.ParticipantBillDTO p : billData.getParticipantBills()) {
            summaryTable.addCell(createCell(String.valueOf(count++), Element.ALIGN_CENTER));
            summaryTable.addCell(createCell(p.getParticipantName(), Element.ALIGN_LEFT));
            summaryTable.addCell(createCell("INR " + p.getFoodSubtotal(), Element.ALIGN_RIGHT));
            summaryTable.addCell(createCell("INR " + p.getFinalPayable(), Element.ALIGN_RIGHT));
        }

        document.add(summaryTable);
        document.close();
        return out.toByteArray();
    }

    private PdfPCell createCell(String text, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 9, NAVY)));
        cell.setPadding(5);
        cell.setHorizontalAlignment(align);
        cell.setBorderColor(BORDER_GRAY);
        return cell;
    }
}
