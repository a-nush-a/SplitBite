import React from 'react';
import { Download, FileText, CheckCircle2, AlertTriangle } from 'lucide-react';
import { downloadIndividualBillPDF, downloadAllBillsPDF } from '../utils/pdfGenerator';

export default function BillSummarySection({ billData }) {
  const {
    restaurantName,
    totals = { foodSubtotal: 0, taxPercent: 5, taxAmount: 0, grandTotal: 0 },
    splitType,
    participantBills = [],
    unassignedItems = []
  } = billData;

  const totalAssignedSum = participantBills.reduce((acc, p) => acc + (p.finalPayable || 0), 0);
  const isMatch = Math.abs(totals.grandTotal - totalAssignedSum) < 0.10;

  return (
    <div className="bg-white rounded-2xl p-6 sm:p-8 border border-slate-200 shadow-sm mb-12">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-6 border-b border-slate-200 gap-4">
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center font-bold text-lg">
            4
          </div>
          <div>
            <h2 className="text-xl font-bold text-navy-900">Individual Bill Summaries (GST Included)</h2>
            <p className="text-xs text-slate-500">
              {restaurantName || 'Restaurant Bill'} &bull; {splitType === 'EQUAL' ? 'Equal Split' : 'Individual Item Split with Proportional GST'}
            </p>
          </div>
        </div>

        {/* Download All Bills Button */}
        {participantBills.length > 0 && (
          <button
            type="button"
            onClick={() => downloadAllBillsPDF(billData)}
            className="inline-flex items-center space-x-2 bg-navy-900 hover:bg-slate-800 text-white font-semibold px-4 py-2.5 rounded-xl shadow-md transition text-xs shrink-0"
          >
            <Download className="w-4 h-4 text-brand" />
            <span>Download All Bills (Combined PDF)</span>
          </button>
        )}
      </div>

      {/* Accuracy & Validation Alert */}
      <div className="my-6">
        {unassignedItems.length > 0 && splitType === 'INDIVIDUAL' ? (
          <div className="p-4 rounded-xl bg-amber-50 border border-amber-200 text-amber-800 text-xs flex items-start space-x-2.5">
            <AlertTriangle className="w-4 h-4 text-amber-600 shrink-0 mt-0.5" />
            <div>
              <span className="font-bold">Unassigned Items Detected:</span> {unassignedItems.length} food item(s) have not been assigned to anyone yet.
            </div>
          </div>
        ) : isMatch && totals.grandTotal > 0 ? (
          <div className="p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-900 text-xs flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <CheckCircle2 className="w-4 h-4 text-brand shrink-0" />
              <span>
                <strong>100% Balanced:</strong> Grand Total with GST (₹{totals.grandTotal.toFixed(2)}) matches the sum of all individual shares (₹{totalAssignedSum.toFixed(2)}).
              </span>
            </div>
          </div>
        ) : null}
      </div>

      {/* Participant Cards Grid */}
      {participantBills.length === 0 ? (
        <div className="text-center py-12 border-2 border-dashed border-slate-200 rounded-xl bg-slate-50/50">
          <FileText className="w-10 h-10 text-slate-300 mx-auto mb-2" />
          <p className="text-sm font-semibold text-slate-600">No participant bills calculated yet.</p>
          <p className="text-xs text-slate-400 mt-1">Add items and participants above to see the breakdown.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          {participantBills.map((p) => (
            <div
              key={p.participantId}
              className="bg-slate-50/70 border border-slate-200 rounded-2xl p-5 hover:border-slate-300 transition flex flex-col justify-between"
            >
              <div>
                {/* Participant Header */}
                <div className="flex items-center justify-between pb-3 border-b border-slate-200 mb-3">
                  <div className="flex items-center space-x-2.5">
                    <div className="w-8 h-8 rounded-full bg-navy-900 text-white font-bold text-xs flex items-center justify-center">
                      {p.participantName.charAt(0).toUpperCase()}
                    </div>
                    <div>
                      <h3 className="font-bold text-navy-900 text-sm">{p.participantName}</h3>
                      <p className="text-[11px] text-slate-500">
                        {p.assignedItems.length} item(s) &bull; {splitType === 'EQUAL' ? 'Equal share' : 'Individual'}
                      </p>
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="text-[10px] uppercase font-bold text-slate-400">Total Payable</div>
                    <div className="text-lg font-extrabold text-brand">
                      ₹{p.finalPayable.toFixed(2)}
                    </div>
                  </div>
                </div>

                {/* Items List */}
                <div className="space-y-1.5 mb-3">
                  {p.assignedItems.length === 0 ? (
                    <p className="text-xs text-slate-400 italic py-2">No items assigned to this person.</p>
                  ) : (
                    p.assignedItems.map((item, idx) => (
                      <div key={idx} className="flex items-center justify-between text-xs py-1 border-b border-slate-100 last:border-0">
                        <div className="flex-1 pr-2">
                          <span className="font-medium text-slate-700">{item.name}</span>
                          {item.isShared && (
                            <span className="ml-1.5 text-[10px] text-blue-600 bg-blue-50 px-1.5 py-0.5 rounded font-medium">
                              1/{item.sharedWithCount}
                            </span>
                          )}
                          <span className="text-slate-400 ml-1">(&times;{item.quantity})</span>
                        </div>
                        <div className="font-semibold text-slate-800">
                          ₹{item.itemCostForParticipant.toFixed(2)}
                        </div>
                      </div>
                    ))
                  )}
                </div>

                {/* Taxes & GST Breakdown */}
                <div className="pt-2 border-t border-slate-200 text-xs space-y-1 text-slate-600 mb-4 bg-white/70 p-2.5 rounded-xl border border-slate-100">
                  <div className="flex justify-between">
                    <span>Food Subtotal:</span>
                    <span className="font-semibold text-navy-900">₹{p.foodSubtotal.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span>GST Share ({totals.taxPercent}%):</span>
                    <span className="font-semibold text-emerald-600">+₹{p.taxShare.toFixed(2)}</span>
                  </div>
                  {p.serviceShare > 0 && (
                    <div className="flex justify-between">
                      <span>Service Charge:</span>
                      <span className="font-semibold text-emerald-600">+₹{p.serviceShare.toFixed(2)}</span>
                    </div>
                  )}
                  {p.discountShare > 0 && (
                    <div className="flex justify-between">
                      <span>Discount:</span>
                      <span className="font-semibold text-rose-600">-₹{p.discountShare.toFixed(2)}</span>
                    </div>
                  )}
                </div>
              </div>

              {/* Card Footer with Individual PDF Download */}
              <div className="pt-2 flex items-center justify-between">
                <div className="text-[11px] text-slate-400">
                  Tax Incl.
                </div>
                <button
                  type="button"
                  onClick={() => downloadIndividualBillPDF(billData, p)}
                  className="inline-flex items-center space-x-1.5 text-xs font-semibold text-brand hover:text-brand-hover bg-brand-light/60 hover:bg-brand-light px-3.5 py-2 rounded-xl transition"
                >
                  <Download className="w-3.5 h-3.5" />
                  <span>Download PDF</span>
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
