import React from 'react';
import { Divide, UserCheck, Check, Users, AlertCircle } from 'lucide-react';

export default function SplitOptionsSection({
  splitType,
  setSplitType,
  foodItems,
  participants,
  assignments,
  setAssignments,
  totalBill
}) {
  const handleToggleParticipant = (itemId, participantId) => {
    const current = assignments[itemId] || [];
    let updated;
    if (current.includes(participantId)) {
      // Remove
      updated = current.filter(id => id !== participantId);
    } else {
      // Add
      updated = [...current, participantId];
    }
    setAssignments({
      ...assignments,
      [itemId]: updated
    });
  };

  const handleSelectAllForDish = (itemId) => {
    const allIds = participants.map(p => p.id);
    const current = assignments[itemId] || [];
    // If all already selected, clear all; else select all
    if (current.length === allIds.length) {
      setAssignments({
        ...assignments,
        [itemId]: []
      });
    } else {
      setAssignments({
        ...assignments,
        [itemId]: allIds
      });
    }
  };

  return (
    <div className="bg-white rounded-2xl p-6 sm:p-8 border border-slate-200 shadow-sm mb-8">
      <div className="flex items-center space-x-3 mb-6 pb-4 border-b border-slate-100">
        <div className="w-10 h-10 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center font-bold text-lg">
          3
        </div>
        <div>
          <h2 className="text-xl font-bold text-navy-900">Choose Splitting Method</h2>
          <p className="text-xs text-slate-500">Divide equally or assign specific food items to people</p>
        </div>
      </div>

      {/* Split Method Toggle Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-6">
        <button
          type="button"
          onClick={() => setSplitType('EQUAL')}
          className={`p-5 rounded-2xl border-2 text-left transition relative ${
            splitType === 'EQUAL'
              ? 'border-brand bg-emerald-50/40 shadow-sm ring-1 ring-brand'
              : 'border-slate-200 bg-white hover:border-slate-300'
          }`}
        >
          <div className="flex items-center justify-between mb-2">
            <div className="w-9 h-9 rounded-xl bg-emerald-100 text-brand flex items-center justify-center font-bold">
              <Divide className="w-5 h-5" />
            </div>
            {splitType === 'EQUAL' && (
              <span className="w-6 h-6 rounded-full bg-brand text-white flex items-center justify-center">
                <Check className="w-3.5 h-3.5 stroke-[3]" />
              </span>
            )}
          </div>
          <h3 className="font-bold text-navy-900 text-base mb-1">Equal Bill Splitting</h3>
          <p className="text-xs text-slate-500 leading-relaxed">
            Total bill is divided equally among all {participants.length || 0} participants.
          </p>
          {participants.length > 0 && totalBill > 0 && (
            <div className="mt-3 text-xs font-semibold text-brand">
              ≈ ₹{(totalBill / participants.length).toFixed(2)} per person
            </div>
          )}
        </button>

        <button
          type="button"
          onClick={() => setSplitType('INDIVIDUAL')}
          className={`p-5 rounded-2xl border-2 text-left transition relative ${
            splitType === 'INDIVIDUAL'
              ? 'border-brand bg-emerald-50/40 shadow-sm ring-1 ring-brand'
              : 'border-slate-200 bg-white hover:border-slate-300'
          }`}
        >
          <div className="flex items-center justify-between mb-2">
            <div className="w-9 h-9 rounded-xl bg-blue-100 text-blue-600 flex items-center justify-center font-bold">
              <UserCheck className="w-5 h-5" />
            </div>
            {splitType === 'INDIVIDUAL' && (
              <span className="w-6 h-6 rounded-full bg-brand text-white flex items-center justify-center">
                <Check className="w-3.5 h-3.5 stroke-[3]" />
              </span>
            )}
          </div>
          <h3 className="font-bold text-navy-900 text-base mb-1">Individual Food Assignment</h3>
          <p className="text-xs text-slate-500 leading-relaxed">
            Tag who ate which dish. A dish can also be shared between 2 or more people!
          </p>
        </button>
      </div>

      {/* When Individual Assignment is Selected */}
      {splitType === 'INDIVIDUAL' && (
        <div className="mt-6 pt-6 border-t border-slate-200">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h4 className="text-sm font-bold text-navy-900">Assign Food Items to People</h4>
              <p className="text-xs text-slate-500">Click on names to tag who shared each dish</p>
            </div>
          </div>

          {participants.length === 0 ? (
            <div className="p-4 rounded-xl bg-amber-50 border border-amber-200 text-amber-800 text-xs flex items-center space-x-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>Please add at least one participant in Section 2 above to assign items.</span>
            </div>
          ) : foodItems.length === 0 ? (
            <div className="p-4 rounded-xl bg-slate-50 border border-slate-200 text-slate-600 text-xs">
              Please add food items in Section 1 above to assign them.
            </div>
          ) : (
            <div className="space-y-3">
              {foodItems.map(item => {
                const assigned = assignments[item.id] || [];
                const itemTotal = (parseFloat(item.price) || 0) * (parseInt(item.quantity, 10) || 1);
                const sharePerPerson = assigned.length > 0 ? (itemTotal / assigned.length).toFixed(2) : null;

                return (
                  <div
                    key={item.id}
                    className={`p-4 rounded-xl border transition ${
                      assigned.length === 0
                        ? 'border-amber-200 bg-amber-50/30'
                        : 'border-slate-200 bg-slate-50/50 hover:bg-slate-50'
                    }`}
                  >
                    <div className="flex flex-col sm:flex-row sm:items-center justify-between mb-3 gap-2">
                      <div>
                        <div className="flex items-center space-x-2">
                          <span className="font-bold text-navy-900 text-sm">{item.name}</span>
                          <span className="text-xs font-semibold px-2 py-0.5 rounded-md bg-white border border-slate-200 text-slate-600">
                            Qty: {item.quantity} &times; ₹{parseFloat(item.price).toFixed(2)}
                          </span>
                        </div>
                        <div className="text-xs font-bold text-brand mt-0.5">
                          Total: ₹{itemTotal.toFixed(2)}
                          {sharePerPerson && (
                            <span className="text-slate-500 font-normal ml-2">
                              (₹{sharePerPerson} each among {assigned.length} {assigned.length === 1 ? 'person' : 'people'})
                            </span>
                          )}
                        </div>
                      </div>

                      <button
                        type="button"
                        onClick={() => handleSelectAllForDish(item.id)}
                        className="text-xs font-semibold text-slate-600 hover:text-navy-900 bg-white border border-slate-200 hover:border-slate-300 px-2.5 py-1 rounded-lg self-start sm:self-auto transition"
                      >
                        {assigned.length === participants.length ? 'Deselect All' : 'Shared by Everyone'}
                      </button>
                    </div>

                    {/* Participant selector pills */}
                    <div className="flex flex-wrap gap-2">
                      {participants.map(p => {
                        const isSelected = assigned.includes(p.id);
                        return (
                          <button
                            key={p.id}
                            type="button"
                            onClick={() => handleToggleParticipant(item.id, p.id)}
                            className={`inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition ${
                              isSelected
                                ? 'bg-brand text-white shadow-sm ring-1 ring-brand'
                                : 'bg-white border border-slate-200 text-slate-600 hover:border-slate-300 hover:bg-slate-100/50'
                            }`}
                          >
                            <span className={`w-4 h-4 rounded-full text-[10px] flex items-center justify-center font-bold ${
                              isSelected ? 'bg-white/20 text-white' : 'bg-slate-100 text-slate-600'
                            }`}>
                              {p.name.charAt(0).toUpperCase()}
                            </span>
                            <span>{p.name}</span>
                            {isSelected && <Check className="w-3 h-3 stroke-[3]" />}
                          </button>
                        );
                      })}
                    </div>

                    {assigned.length === 0 && (
                      <p className="text-[11px] text-amber-600 font-medium mt-2 flex items-center space-x-1">
                        <AlertCircle className="w-3 h-3" />
                        <span>Not assigned yet. Select who ate this dish.</span>
                      </p>
                    )}
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
