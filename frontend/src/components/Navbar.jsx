import React from 'react';
import { UtensilsCrossed, RefreshCw } from 'lucide-react';

export default function Navbar({ onReset }) {
  return (
    <header className="sticky top-0 z-50 bg-white/95 backdrop-blur-md border-b border-slate-200 shadow-sm">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-xl bg-brand text-white flex items-center justify-center shadow-md shadow-brand/20">
            <UtensilsCrossed className="w-5 h-5" />
          </div>
          <div>
            <div className="flex items-center space-x-2">
              <span className="text-xl font-bold text-navy-900 tracking-tight">SplitBite</span>
              <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-brand-light text-brand-hover">
                v1.0
              </span>
            </div>
            <p className="text-xs text-slate-500 hidden sm:block">Smart Restaurant Bill Splitting</p>
          </div>
        </div>

        <div className="flex items-center space-x-3">
          <button
            onClick={onReset}
            className="inline-flex items-center space-x-1.5 text-xs font-semibold text-slate-600 hover:text-navy-900 bg-slate-100 hover:bg-slate-200 px-3 py-2 rounded-lg transition"
            title="Start new bill"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            <span>New Bill</span>
          </button>
        </div>
      </div>
    </header>
  );
}
