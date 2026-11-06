import React, { useState } from 'react';
import { Plus, Trash2, Store, ShoppingBag, Percent } from 'lucide-react';

export default function CreateBillSection({
  restaurantName,
  setRestaurantName,
  foodItems,
  setFoodItems,
  taxPercent,
  setTaxPercent,
  serviceCharge,
  setServiceCharge,
  discount,
  setDiscount,
  totals
}) {
  const [newItemName, setNewItemName] = useState('');
  const [newItemPrice, setNewItemPrice] = useState('');
  const [newItemQty, setNewItemQty] = useState(1);
  const [errorMsg, setErrorMsg] = useState('');

  const handleAddItem = (e) => {
    e.preventDefault();
    if (!newItemName.trim()) {
      setErrorMsg('Please enter a food item name.');
      return;
    }
    const priceNum = parseFloat(newItemPrice);
    if (isNaN(priceNum) || priceNum <= 0) {
      setErrorMsg('Please enter a valid positive price.');
      return;
    }
    const qtyNum = parseInt(newItemQty, 10);
    if (isNaN(qtyNum) || qtyNum < 1) {
      setErrorMsg('Quantity must be at least 1.');
      return;
    }

    const newItem = {
      id: 'item_' + Date.now() + '_' + Math.random().toString(36).substring(2, 6),
      name: newItemName.trim(),
      price: priceNum,
      quantity: qtyNum
    };

    setFoodItems([...foodItems, newItem]);
    setNewItemName('');
    setNewItemPrice('');
    setNewItemQty(1);
    setErrorMsg('');
  };

  const handleRemoveItem = (idToRemove) => {
    setFoodItems(foodItems.filter(item => item.id !== idToRemove));
  };

  const handleQtyChange = (id, delta) => {
    setFoodItems(foodItems.map(item => {
      if (item.id === id) {
        const nextQty = Math.max(1, (parseInt(item.quantity, 10) || 1) + delta);
        return { ...item, quantity: nextQty };
      }
      return item;
    }));
  };

  return (
    <div className="bg-white rounded-2xl p-6 sm:p-8 border border-slate-200 shadow-sm mb-8">
      {/* Restaurant Name */}
      <div className="mb-6">
        <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-2">
          Restaurant Name
        </label>
        <div className="relative">
          <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
            <Store className="w-5 h-5" />
          </div>
          <input
            type="text"
            value={restaurantName}
            onChange={(e) => setRestaurantName(e.target.value)}
            placeholder="e.g. Spice Haven Restaurant..."
            className="w-full pl-11 pr-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-brand/30 focus:border-brand text-navy-900 placeholder-slate-400 font-semibold transition text-sm"
          />
        </div>
      </div>

      {/* Add Food Item Form */}
      <form onSubmit={handleAddItem} className="bg-slate-50 p-4 sm:p-5 rounded-xl border border-slate-200/80 mb-6">
        <div className="text-xs font-bold text-navy-900 uppercase tracking-wider mb-3 flex items-center space-x-1.5">
          <ShoppingBag className="w-3.5 h-3.5 text-brand" />
          <span>Add / Edit Food Items</span>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-12 gap-3">
          <div className="sm:col-span-6">
            <label className="block text-[11px] font-semibold text-slate-600 mb-1">Item Name</label>
            <input
              type="text"
              value={newItemName}
              onChange={(e) => setNewItemName(e.target.value)}
              placeholder="e.g. Butter Naan, Chicken Biryani..."
              className="w-full px-3.5 py-2.5 rounded-lg border border-slate-200 bg-white focus:outline-none focus:border-brand text-sm"
            />
          </div>

          <div className="sm:col-span-3">
            <label className="block text-[11px] font-semibold text-slate-600 mb-1">Price per item (₹)</label>
            <input
              type="number"
              step="0.01"
              min="0"
              value={newItemPrice}
              onChange={(e) => setNewItemPrice(e.target.value)}
              placeholder="0.00"
              className="w-full px-3.5 py-2.5 rounded-lg border border-slate-200 bg-white focus:outline-none focus:border-brand text-sm"
            />
          </div>

          <div className="sm:col-span-3">
            <label className="block text-[11px] font-semibold text-slate-600 mb-1">Quantity</label>
            <div className="flex items-center space-x-2">
              <input
                type="number"
                min="1"
                value={newItemQty}
                onChange={(e) => setNewItemQty(e.target.value)}
                className="w-full px-3.5 py-2.5 rounded-lg border border-slate-200 bg-white focus:outline-none focus:border-brand text-sm"
              />
              <button
                type="submit"
                className="inline-flex items-center space-x-1 bg-brand hover:bg-brand-hover text-white font-semibold px-4 py-2.5 rounded-lg shadow-sm transition shrink-0"
              >
                <Plus className="w-4 h-4" />
                <span className="hidden sm:inline">Add</span>
              </button>
            </div>
          </div>
        </div>

        {errorMsg && (
          <p className="text-xs text-rose-500 font-medium mt-2.5">{errorMsg}</p>
        )}
      </form>

      {/* Food Items Table */}
      {foodItems.length === 0 ? (
        <div className="text-center py-6 border-2 border-dashed border-slate-200 rounded-xl mb-6">
          <p className="text-sm font-medium text-slate-500">No dishes listed yet.</p>
          <p className="text-xs text-slate-400 mt-1">Add dishes manually using the form above.</p>
        </div>
      ) : (
        <div className="overflow-x-auto rounded-xl border border-slate-200 mb-6">
          <table className="w-full text-left border-collapse text-sm">
            <thead>
              <tr className="bg-slate-100/80 text-slate-700 text-xs font-bold uppercase tracking-wider border-b border-slate-200">
                <th className="py-2.5 px-3">#</th>
                <th className="py-2.5 px-3">Item Name</th>
                <th className="py-2.5 px-3 text-right">Price</th>
                <th className="py-2.5 px-3 text-center">Qty</th>
                <th className="py-2.5 px-3 text-right">Subtotal</th>
                <th className="py-2.5 px-3 text-center">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 bg-white">
              {foodItems.map((item, index) => {
                const subtotal = (parseFloat(item.price) || 0) * (parseInt(item.quantity, 10) || 1);
                return (
                  <tr key={item.id} className="hover:bg-slate-50/80 transition">
                    <td className="py-2.5 px-3 text-slate-400 font-medium text-xs">{index + 1}</td>
                    <td className="py-2.5 px-3 font-semibold text-navy-900">{item.name}</td>
                    <td className="py-2.5 px-3 text-right font-medium text-slate-600">
                      ₹{parseFloat(item.price).toFixed(2)}
                    </td>
                    <td className="py-2.5 px-3 text-center">
                      <div className="inline-flex items-center space-x-1.5 border border-slate-200 rounded-lg px-2 py-0.5 bg-slate-50">
                        <button
                          type="button"
                          onClick={() => handleQtyChange(item.id, -1)}
                          className="text-slate-500 hover:text-navy-900 font-bold px-1"
                        >
                          -
                        </button>
                        <span className="font-semibold text-xs px-1">{item.quantity}</span>
                        <button
                          type="button"
                          onClick={() => handleQtyChange(item.id, 1)}
                          className="text-slate-500 hover:text-navy-900 font-bold px-1"
                        >
                          +
                        </button>
                      </div>
                    </td>
                    <td className="py-2.5 px-3 text-right font-bold text-navy-900">
                      ₹{subtotal.toFixed(2)}
                    </td>
                    <td className="py-2.5 px-3 text-center">
                      <button
                        type="button"
                        onClick={() => handleRemoveItem(item.id)}
                        className="text-slate-400 hover:text-rose-500 p-1.5 rounded-lg hover:bg-rose-50 transition"
                        title="Remove item"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {/* Taxes & GST */}
      <div className="p-4 sm:p-5 rounded-xl bg-slate-50 border border-slate-200 mb-6">
        <div className="text-xs font-bold text-navy-900 uppercase tracking-wider mb-3 flex items-center space-x-1.5">
          <Percent className="w-3.5 h-3.5 text-brand" />
          <span>Taxes & Additional Charges (GST, Service Charge, Discount)</span>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
          <div>
            <label className="block text-[11px] font-semibold text-slate-600 mb-1">GST / Tax (%)</label>
            <div className="relative">
              <input
                type="number"
                min="0"
                max="100"
                step="0.1"
                value={taxPercent}
                onChange={(e) => setTaxPercent(e.target.value)}
                placeholder="5"
                className="w-full px-3 py-2 rounded-lg border border-slate-200 bg-white text-sm font-semibold text-navy-900"
              />
              <span className="absolute right-3 top-2 text-xs font-bold text-slate-400">%</span>
            </div>
            <p className="text-[10px] text-slate-400 mt-1">Standard restaurant GST is 5%</p>
          </div>

          <div>
            <label className="block text-[11px] font-semibold text-slate-600 mb-1">Service Charge / Tip (₹)</label>
            <input
              type="number"
              min="0"
              step="1"
              value={serviceCharge}
              onChange={(e) => setServiceCharge(e.target.value)}
              placeholder="0.00"
              className="w-full px-3 py-2 rounded-lg border border-slate-200 bg-white text-sm font-semibold text-navy-900"
            />
            <p className="text-[10px] text-slate-400 mt-1">Optional fixed service amount</p>
          </div>

          <div>
            <label className="block text-[11px] font-semibold text-slate-600 mb-1">Discount (₹)</label>
            <input
              type="number"
              min="0"
              step="1"
              value={discount}
              onChange={(e) => setDiscount(e.target.value)}
              placeholder="0.00"
              className="w-full px-3 py-2 rounded-lg border border-slate-200 bg-white text-sm font-semibold text-navy-900"
            />
            <p className="text-[10px] text-slate-400 mt-1">Coupon or bill discount</p>
          </div>
        </div>
      </div>

      {/* Total Banner */}
      <div className="p-4 rounded-xl bg-navy-900 text-white space-y-2">
        <div className="flex justify-between text-xs text-slate-300">
          <span>Food Items Subtotal:</span>
          <span className="font-bold text-white">₹{totals.foodSubtotal.toFixed(2)}</span>
        </div>
        <div className="flex justify-between text-xs text-slate-300">
          <span>GST / Tax ({totals.taxPercent}%):</span>
          <span className="font-bold text-emerald-300">+₹{totals.taxAmount.toFixed(2)}</span>
        </div>
        {totals.serviceCharge > 0 && (
          <div className="flex justify-between text-xs text-slate-300">
            <span>Service Charge / Tip:</span>
            <span className="font-bold text-emerald-300">+₹{totals.serviceCharge.toFixed(2)}</span>
          </div>
        )}
        {totals.discount > 0 && (
          <div className="flex justify-between text-xs text-slate-300">
            <span>Discount:</span>
            <span className="font-bold text-rose-300">-₹{totals.discount.toFixed(2)}</span>
          </div>
        )}
        <div className="pt-2 border-t border-slate-700 flex justify-between items-center">
          <div>
            <div className="text-xs font-bold text-slate-300 uppercase">Grand Total Payable</div>
            <div className="text-[11px] text-slate-400">{foodItems.length} item(s) listed</div>
          </div>
          <div className="text-2xl font-extrabold text-brand">
            ₹{totals.grandTotal.toFixed(2)}
          </div>
        </div>
      </div>
    </div>
  );
}
