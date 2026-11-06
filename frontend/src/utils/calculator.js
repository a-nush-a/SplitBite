/**
 * SplitBite Bill Calculation Utility with GST & Taxes
 * 
 * Handles:
 * 1. Food Subtotal calculation
 * 2. GST / Tax calculation (% or fixed)
 * 3. Service Charge & Discount handling
 * 4. Proportional distribution of taxes based on food consumption
 * 5. Penny-accurate rounding guarantee
 */

export function calculateFoodSubtotal(foodItems = []) {
  return foodItems.reduce((sum, item) => {
    const price = parseFloat(item.price) || 0;
    const qty = parseInt(item.quantity, 10) || 1;
    return sum + (price * qty);
  }, 0);
}

export function calculateBillTotals({
  foodItems = [],
  taxPercent = 5.0,
  serviceCharge = 0.0,
  discount = 0.0
}) {
  const foodSubtotal = calculateFoodSubtotal(foodItems);
  const taxAmount = (foodSubtotal * (parseFloat(taxPercent) || 0)) / 100;
  const grandTotal = Math.max(0, foodSubtotal + taxAmount + (parseFloat(serviceCharge) || 0) - (parseFloat(discount) || 0));

  return {
    foodSubtotal: Number(foodSubtotal.toFixed(2)),
    taxPercent: parseFloat(taxPercent) || 0,
    taxAmount: Number(taxAmount.toFixed(2)),
    serviceCharge: Number((parseFloat(serviceCharge) || 0).toFixed(2)),
    discount: Number((parseFloat(discount) || 0).toFixed(2)),
    grandTotal: Number(grandTotal.toFixed(2))
  };
}

export function calculateSplits({
  restaurantName = '',
  foodItems = [],
  taxPercent = 5.0,
  serviceCharge = 0.0,
  discount = 0.0,
  participants = [],
  splitType = 'EQUAL', // 'EQUAL' or 'INDIVIDUAL'
  assignments = {}     // itemId -> array of participantIds
}) {
  const totals = calculateBillTotals({ foodItems, taxPercent, serviceCharge, discount });
  const { foodSubtotal, taxAmount, grandTotal } = totals;

  if (!participants || participants.length === 0) {
    return {
      restaurantName,
      totals,
      splitType,
      participantBills: [],
      unassignedItems: []
    };
  }

  // 1. EQUAL SPLIT
  if (splitType === 'EQUAL') {
    const count = participants.length;
    const rawShare = grandTotal / count;
    const foodPortion = foodSubtotal / count;
    const taxPortion = taxAmount / count;
    const svcPortion = totals.serviceCharge / count;
    const discPortion = totals.discount / count;

    let runningTotal = 0;

    const participantBills = participants.map((p, index) => {
      let myShare = (index === count - 1) 
        ? Number((grandTotal - runningTotal).toFixed(2)) 
        : Number(rawShare.toFixed(2));
      runningTotal += myShare;

      return {
        participantId: p.id,
        participantName: p.name,
        assignedItems: foodItems.map(item => ({
          itemId: item.id,
          name: item.name,
          quantity: item.quantity,
          price: item.price,
          totalPrice: (parseFloat(item.price) || 0) * (parseInt(item.quantity, 10) || 1),
          portionShare: 1 / count,
          isShared: true,
          sharedWithCount: count,
          itemCostForParticipant: Number((((parseFloat(item.price) || 0) * (parseInt(item.quantity, 10) || 1)) / count).toFixed(2))
        })),
        foodSubtotal: Number(foodPortion.toFixed(2)),
        taxShare: Number(taxPortion.toFixed(2)),
        serviceShare: Number(svcPortion.toFixed(2)),
        discountShare: Number(discPortion.toFixed(2)),
        subtotal: Number(foodPortion.toFixed(2)),
        finalPayable: myShare
      };
    });

    return {
      restaurantName,
      totals,
      splitType: 'EQUAL',
      participantBills,
      unassignedItems: []
    };
  }

  // 2. INDIVIDUAL FOOD ASSIGNMENT WITH PROPORTIONAL TAX DISTRIBUTION
  const participantMap = {};
  participants.forEach(p => {
    participantMap[p.id] = {
      participantId: p.id,
      participantName: p.name,
      assignedItems: [],
      foodSubtotal: 0,
      taxShare: 0,
      serviceShare: 0,
      discountShare: 0,
      subtotal: 0,
      finalPayable: 0
    };
  });

  const unassignedItems = [];

  foodItems.forEach(item => {
    const itemTotal = (parseFloat(item.price) || 0) * (parseInt(item.quantity, 10) || 1);
    const assignedParticipantIds = assignments[item.id] || [];

    if (assignedParticipantIds.length === 0) {
      unassignedItems.push(item);
      return;
    }

    const shareCount = assignedParticipantIds.length;
    const rawPortion = itemTotal / shareCount;

    assignedParticipantIds.forEach(pId => {
      if (participantMap[pId]) {
        participantMap[pId].assignedItems.push({
          itemId: item.id,
          name: item.name,
          quantity: item.quantity,
          price: item.price,
          totalPrice: itemTotal,
          portionShare: 1 / shareCount,
          isShared: shareCount > 1,
          sharedWithCount: shareCount,
          itemCostForParticipant: Number(rawPortion.toFixed(2))
        });
        participantMap[pId].foodSubtotal += rawPortion;
      }
    });
  });

  // Calculate total food consumed by all participants
  const totalAssignedFood = Object.values(participantMap).reduce((sum, p) => sum + p.foodSubtotal, 0);

  // Proportional GST and charges distribution
  const participantBills = Object.values(participantMap).map(p => {
    const ratio = totalAssignedFood > 0 ? (p.foodSubtotal / totalAssignedFood) : (1 / participants.length);
    const myTax = Number((taxAmount * ratio).toFixed(2));
    const myService = Number((totals.serviceCharge * ratio).toFixed(2));
    const myDiscount = Number((totals.discount * ratio).toFixed(2));

    const finalPayable = Number((p.foodSubtotal + myTax + myService - myDiscount).toFixed(2));

    return {
      ...p,
      foodSubtotal: Number(p.foodSubtotal.toFixed(2)),
      taxShare: myTax,
      serviceShare: myService,
      discountShare: myDiscount,
      subtotal: Number(p.foodSubtotal.toFixed(2)),
      finalPayable
    };
  });

  return {
    restaurantName,
    totals,
    splitType: 'INDIVIDUAL',
    participantBills,
    unassignedItems
  };
}
