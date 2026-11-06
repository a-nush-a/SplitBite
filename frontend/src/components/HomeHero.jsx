import React from 'react';
import { ArrowDown, CheckCircle2, Receipt, Users, Calculator, FileDown } from 'lucide-react';

export default function HomeHero({ onGetStarted }) {
  return (
    <section className="pt-8 pb-10 sm:pt-12 sm:pb-14 border-b border-slate-200 bg-gradient-to-b from-white to-offwhite">
      <div className="max-w-4xl mx-auto px-4 text-center">
        <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-brand-light text-brand-hover text-xs font-semibold mb-4">
          <CheckCircle2 className="w-3.5 h-3.5" />
          <span>Fair & Accurate Dining Expense Splitter</span>
        </div>

        <h1 className="text-3xl sm:text-5xl font-extrabold text-navy-900 tracking-tight leading-tight mb-4">
          Split Restaurant Bills Without the <span className="text-brand">Math Headache</span>
        </h1>

        <p className="text-base sm:text-lg text-slate-600 max-w-2xl mx-auto mb-8">
          Add your food items, tag your friends, choose between equal split or individual dish assignment, 
          and download clean, individual PDF receipts for everyone.
        </p>

        <div className="flex flex-wrap items-center justify-center gap-3 sm:gap-4 mb-8">
          <button
            onClick={onGetStarted}
            className="inline-flex items-center space-x-2 bg-brand hover:bg-brand-hover text-white font-semibold px-6 py-3 rounded-xl shadow-lg shadow-brand/25 transition transform active:scale-95"
          >
            <span>Start Splitting Bill</span>
            <ArrowDown className="w-4 h-4" />
          </button>
        </div>

        {/* Feature Pills */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 max-w-3xl mx-auto pt-4 text-left">
          <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm flex items-center space-x-3">
            <div className="w-8 h-8 rounded-lg bg-emerald-50 text-brand flex items-center justify-center shrink-0">
              <Receipt className="w-4 h-4" />
            </div>
            <div>
              <div className="text-xs font-bold text-navy-900">Food Items</div>
              <div className="text-[11px] text-slate-500">Price & quantity</div>
            </div>
          </div>

          <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm flex items-center space-x-3">
            <div className="w-8 h-8 rounded-lg bg-blue-50 text-blue-600 flex items-center justify-center shrink-0">
              <Users className="w-4 h-4" />
            </div>
            <div>
              <div className="text-xs font-bold text-navy-900">Participants</div>
              <div className="text-[11px] text-slate-500">Group members</div>
            </div>
          </div>

          <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm flex items-center space-x-3">
            <div className="w-8 h-8 rounded-lg bg-amber-50 text-amber-600 flex items-center justify-center shrink-0">
              <Calculator className="w-4 h-4" />
            </div>
            <div>
              <div className="text-xs font-bold text-navy-900">Fair Split</div>
              <div className="text-[11px] text-slate-500">Equal or dish-by-dish</div>
            </div>
          </div>

          <div className="bg-white p-3.5 rounded-xl border border-slate-200 shadow-sm flex items-center space-x-3">
            <div className="w-8 h-8 rounded-lg bg-purple-50 text-purple-600 flex items-center justify-center shrink-0">
              <FileDown className="w-4 h-4" />
            </div>
            <div>
              <div className="text-xs font-bold text-navy-900">PDF Bills</div>
              <div className="text-[11px] text-slate-500">Individual & group</div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
