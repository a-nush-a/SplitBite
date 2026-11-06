import React, { useState, useMemo, useRef } from 'react';
import Navbar from './components/Navbar';
import HomeHero from './components/HomeHero';
import CreateBillSection from './components/CreateBillSection';
import ParticipantsSection from './components/ParticipantsSection';
import SplitOptionsSection from './components/SplitOptionsSection';
import BillSummarySection from './components/BillSummarySection';
import { calculateBillTotals, calculateSplits } from './utils/calculator';
import { Sparkles, Utensils } from 'lucide-react';

export default function App() {
  const [restaurantName, setRestaurantName] = useState('');
  const [receiptPhotoData, setReceiptPhotoData] = useState(null);
  const [receiptFileName, setReceiptFileName] = useState('');
  const [foodItems, setFoodItems] = useState([]);
  const [taxPercent, setTaxPercent] = useState('5');
  const [serviceCharge, setServiceCharge] = useState('0');
  const [discount, setDiscount] = useState('0');
  const [participants, setParticipants] = useState([]);
  const [splitType, setSplitType] = useState('EQUAL'); // 'EQUAL' or 'INDIVIDUAL'
  const [assignments, setAssignments] = useState({}); // itemId -> array of participantIds

  const mainSectionRef = useRef(null);

  // Compute bill totals
  const totals = useMemo(() => {
    return calculateBillTotals({
      foodItems,
      taxPercent,
      serviceCharge,
      discount
    });
  }, [foodItems, taxPercent, serviceCharge, discount]);

  // Compute bill breakdown for participants
  const billData = useMemo(() => {
    return calculateSplits({
      restaurantName,
      foodItems,
      taxPercent,
      serviceCharge,
      discount,
      participants,
      splitType,
      assignments
    });
  }, [restaurantName, foodItems, taxPercent, serviceCharge, discount, participants, splitType, assignments]);

  // Clean up assignments if a participant is removed
  const handleParticipantRemoved = (removedId) => {
    const updatedAssignments = {};
    Object.keys(assignments).forEach(itemId => {
      updatedAssignments[itemId] = (assignments[itemId] || []).filter(id => id !== removedId);
    });
    setAssignments(updatedAssignments);
  };

  // Reset entire state
  const handleReset = () => {
    if (window.confirm('Are you sure you want to start a new bill? Current data will be cleared.')) {
      setRestaurantName('');
      setReceiptPhotoData(null);
      setReceiptFileName('');
      setFoodItems([]);
      setTaxPercent('5');
      setServiceCharge('0');
      setDiscount('0');
      setParticipants([]);
      setSplitType('EQUAL');
      setAssignments({});
    }
  };

  // Quick Demo / Sample data loader
  const handleLoadSample = () => {
    setRestaurantName('Spice Haven Restaurant');
    setTaxPercent('5');
    setServiceCharge('50');
    setDiscount('0');

    const sampleItems = [
      { id: 'item_1', name: 'Paneer Butter Masala', price: 260, quantity: 1 },
      { id: 'item_2', name: 'Butter Garlic Naan', price: 60, quantity: 4 },
      { id: 'item_3', name: 'Chicken Biryani', price: 320, quantity: 2 },
      { id: 'item_4', name: 'Fresh Lime Soda', price: 70, quantity: 3 }
    ];
    const sampleParticipants = [
      { id: 'p_1', name: 'Rahul' },
      { id: 'p_2', name: 'Sneha' },
      { id: 'p_3', name: 'Amal' }
    ];
    const sampleAssignments = {
      item_1: ['p_1', 'p_2'],        // Paneer shared by Rahul & Sneha
      item_2: ['p_1', 'p_2', 'p_3'], // Naan shared by all 3
      item_3: ['p_1', 'p_3'],        // Biryani for Rahul & Amal
      item_4: ['p_1', 'p_2', 'p_3']  // Lime Soda for each
    };

    setFoodItems(sampleItems);
    setParticipants(sampleParticipants);
    setSplitType('INDIVIDUAL');
    setAssignments(sampleAssignments);

    if (mainSectionRef.current) {
      mainSectionRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  };

  const handleScrollToStart = () => {
    if (mainSectionRef.current) {
      mainSectionRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <div className="min-h-screen bg-offwhite flex flex-col">
      <Navbar onReset={handleReset} />

      <HomeHero onGetStarted={handleScrollToStart} />

      <main ref={mainSectionRef} className="flex-1 max-w-4xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-12">
        {/* Sample Data Demo Banner */}
        <div className="mb-8 p-4 rounded-2xl bg-brand-light/50 border border-brand/20 flex flex-col sm:flex-row items-center justify-between gap-3">
          <div className="flex items-center space-x-2.5 text-navy-900 text-xs sm:text-sm">
            <Sparkles className="w-4 h-4 text-brand shrink-0" />
            <span>Want to test quickly? Click to load sample data with 5% GST and Service Charge.</span>
          </div>
          <button
            type="button"
            onClick={handleLoadSample}
            className="text-xs font-bold text-white bg-brand hover:bg-brand-hover px-4 py-2 rounded-xl shadow-sm transition shrink-0"
          >
            Load Sample Bill
          </button>
        </div>

        {/* Feature 1: Create Bill (Restaurant, Receipt Photo & Food Items) */}
        <CreateBillSection
          restaurantName={restaurantName}
          setRestaurantName={setRestaurantName}
          receiptPhotoData={receiptPhotoData}
          setReceiptPhotoData={setReceiptPhotoData}
          receiptFileName={receiptFileName}
          setReceiptFileName={setReceiptFileName}
          foodItems={foodItems}
          setFoodItems={setFoodItems}
          taxPercent={taxPercent}
          setTaxPercent={setTaxPercent}
          serviceCharge={serviceCharge}
          setServiceCharge={setServiceCharge}
          discount={discount}
          setDiscount={setDiscount}
          totals={totals}
        />

        {/* Feature 2: Add Participants */}
        <ParticipantsSection
          participants={participants}
          setParticipants={setParticipants}
          onParticipantRemoved={handleParticipantRemoved}
        />

        {/* Feature 3 & 4: Splitting Methods (Equal vs. Individual) */}
        <SplitOptionsSection
          splitType={splitType}
          setSplitType={setSplitType}
          foodItems={foodItems}
          participants={participants}
          assignments={assignments}
          setAssignments={setAssignments}
          totalBill={totals.grandTotal}
        />

        {/* Feature 5, 6, 7: Bill Summary & PDF Downloads */}
        <BillSummarySection billData={billData} />
      </main>

      {/* Footer */}
      <footer className="border-t border-slate-200 bg-white py-6 text-center text-xs text-slate-500">
        <div className="max-w-4xl mx-auto px-4 flex flex-col sm:flex-row items-center justify-between gap-2">
          <div className="flex items-center space-x-1.5 font-bold text-navy-900">
            <Utensils className="w-3.5 h-3.5 text-brand" />
            <span>SplitBite</span>
          </div>
          <p>Full Stack Project with GST Splitting & Receipt Photo Support</p>
          <p className="text-slate-400">&copy; {new Date().getFullYear()} SplitBite</p>
        </div>
      </footer>
    </div>
  );
}
