import jsPDF from 'jspdf';
import 'jspdf-autotable';

/**
 * PDF Generator for SplitBite with GST & Taxes
 */

export function downloadIndividualBillPDF(billData, participant) {
  const doc = new jsPDF();
  const dateStr = new Date().toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });

  const totals = billData.totals || { taxPercent: 5, taxAmount: 0 };

  // Header Banner
  doc.setFillColor(15, 23, 42); // Dark Navy (#0f172a)
  doc.rect(0, 0, 210, 38, 'F');

  // Title
  doc.setTextColor(255, 255, 255);
  doc.setFontSize(22);
  doc.setFont('helvetica', 'bold');
  doc.text('SplitBite', 14, 20);

  doc.setFontSize(10);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(16, 185, 129); // Brand Green
  doc.text('Smart Restaurant Bill Splitter (GST Included)', 14, 28);

  doc.setTextColor(203, 213, 225);
  doc.text(`Date: ${dateStr}`, 155, 20);

  // Restaurant & Participant Info Box
  doc.setDrawColor(226, 232, 240);
  doc.setFillColor(248, 250, 252);
  doc.roundedRect(14, 46, 182, 30, 3, 3, 'FD');

  doc.setTextColor(15, 23, 42);
  doc.setFontSize(12);
  doc.setFont('helvetica', 'bold');
  doc.text(`Restaurant: ${billData.restaurantName || 'Restaurant Bill'}`, 20, 57);

  doc.setFontSize(11);
  doc.setFont('helvetica', 'normal');
  doc.text(`Participant: ${participant.participantName}`, 20, 68);

  doc.setFont('helvetica', 'bold');
  doc.setTextColor(16, 185, 129);
  doc.text(`Total Payable: INR ${participant.finalPayable.toFixed(2)}`, 130, 68);

  // Items Table
  const tableRows = participant.assignedItems.map((item, idx) => [
    idx + 1,
    item.name,
    item.quantity,
    `INR ${parseFloat(item.price).toFixed(2)}`,
    item.isShared ? `Shared (1/${item.sharedWithCount})` : 'Individual',
    `INR ${item.itemCostForParticipant.toFixed(2)}`
  ]);

  doc.autoTable({
    startY: 84,
    head: [['#', 'Food Item', 'Qty', 'Unit Price', 'Portion', 'Share Cost']],
    body: tableRows,
    theme: 'striped',
    headStyles: {
      fillColor: [15, 23, 42],
      textColor: [255, 255, 255],
      fontStyle: 'bold'
    },
    styles: { font: 'helvetica', fontSize: 10, cellPadding: 4 },
    columnStyles: {
      0: { cellWidth: 10 },
      1: { cellWidth: 60 },
      2: { cellWidth: 15, halign: 'center' },
      3: { cellWidth: 30, halign: 'right' },
      4: { cellWidth: 35, halign: 'center' },
      5: { cellWidth: 32, halign: 'right' }
    }
  });

  const finalY = doc.lastAutoTable.finalY + 10;

  // Breakdown Table (Food Subtotal + GST + Total)
  doc.autoTable({
    startY: finalY,
    body: [
      ['Food Items Subtotal', `INR ${participant.foodSubtotal.toFixed(2)}`],
      [`GST / Tax Share (${totals.taxPercent}%)`, `INR ${participant.taxShare.toFixed(2)}`],
      ['Final Amount Payable', `INR ${participant.finalPayable.toFixed(2)}`]
    ],
    theme: 'grid',
    styles: { font: 'helvetica', fontSize: 10, cellPadding: 4 },
    columnStyles: {
      0: { cellWidth: 120, fontStyle: 'bold' },
      1: { cellWidth: 62, halign: 'right', fontStyle: 'bold', textColor: [16, 185, 129] }
    }
  });

  // Footer Note
  doc.setFontSize(9);
  doc.setTextColor(148, 163, 184);
  doc.setFont('helvetica', 'italic');
  doc.text('Generated with SplitBite – Accurate & Fair Dining Bill Splitting with GST', 14, 285);

  const cleanName = participant.participantName.replace(/[^a-zA-Z0-9]/g, '_');
  doc.save(`SplitBite_${cleanName}_Bill.pdf`);
}

export function downloadAllBillsPDF(billData) {
  const doc = new jsPDF();
  const dateStr = new Date().toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });

  const totals = billData.totals || { grandTotal: 0, taxPercent: 5 };

  // Cover / Header
  doc.setFillColor(15, 23, 42);
  doc.rect(0, 0, 210, 42, 'F');

  doc.setTextColor(255, 255, 255);
  doc.setFontSize(22);
  doc.setFont('helvetica', 'bold');
  doc.text('SplitBite – Master Group Bill', 14, 20);

  doc.setFontSize(10);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(16, 185, 129);
  doc.text(`Restaurant: ${billData.restaurantName || 'Restaurant Bill'} | Grand Total: INR ${totals.grandTotal.toFixed(2)}`, 14, 30);

  doc.setTextColor(203, 213, 225);
  doc.text(`Date: ${dateStr}`, 155, 20);

  // Summary Table of all participants
  const summaryRows = billData.participantBills.map((p, idx) => [
    idx + 1,
    p.participantName,
    `INR ${p.foodSubtotal.toFixed(2)}`,
    `INR ${p.taxShare.toFixed(2)}`,
    `INR ${p.finalPayable.toFixed(2)}`
  ]);

  doc.autoTable({
    startY: 50,
    head: [['#', 'Participant Name', 'Food Share', `GST (${totals.taxPercent}%)`, 'Final Payable']],
    body: summaryRows,
    theme: 'grid',
    headStyles: {
      fillColor: [16, 185, 129],
      textColor: [255, 255, 255],
      fontStyle: 'bold'
    },
    styles: { font: 'helvetica', fontSize: 10, cellPadding: 4 },
    columnStyles: {
      0: { cellWidth: 10 },
      1: { cellWidth: 60 },
      2: { cellWidth: 35, halign: 'right' },
      3: { cellWidth: 35, halign: 'right' },
      4: { cellWidth: 42, halign: 'right', fontStyle: 'bold' }
    }
  });

  doc.save('SplitBite_Complete_Group_Bill_with_GST.pdf');
}
