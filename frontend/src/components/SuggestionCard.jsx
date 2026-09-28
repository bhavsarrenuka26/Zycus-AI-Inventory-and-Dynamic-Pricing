import { useState } from 'react';
import { api } from '../services/api';

const SuggestionCard = ({ suggestion, type, onRefresh }) => {
  const [isProcessing, setIsProcessing] = useState(false);

  const handleAccept = async () => {
    setIsProcessing(true);
    try {
      if (type === 'pricing') {
        await api.acceptPricingSuggestion(suggestion.id);
      } else {
        await api.acceptReorderSuggestion(suggestion.id);
      }
      onRefresh();
    } catch (error) {
      console.error("Error accepting suggestion:", error);
      setIsProcessing(false);
    }
  };

  const handleReject = async () => {
    setIsProcessing(true);
    try {
      if (type === 'pricing') {
        await api.rejectPricingSuggestion(suggestion.id);
      } else {
        await api.rejectReorderSuggestion(suggestion.id);
      }
      onRefresh();
    } catch (error) {
      console.error("Error rejecting suggestion:", error);
      setIsProcessing(false);
    }
  };

  const getConfidencePercentage = (confidence) => {
    if (confidence <= 1) {
      return `${Math.round(confidence * 100)}%`;
    }
    return `${Math.round(confidence)}%`;
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACCEPTED': return 'bg-green-100 text-green-800';
      case 'REJECTED': return 'bg-red-100 text-red-800';
      default: return 'bg-blue-100 text-blue-800';
    }
  };

  const getTriggerColor = (trigger) => {
    switch (trigger) {
      case 'INITIAL': return 'bg-purple-100 text-purple-800';
      case 'INVENTORY_LOW': return 'bg-yellow-100 text-yellow-800';
      case 'DEMAND_SPIKE': return 'bg-orange-100 text-orange-800';
      case 'MANUAL': return 'bg-indigo-100 text-indigo-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
      <div className="flex justify-between items-start">
        <div>
          <h3 className="font-medium text-gray-900">{suggestion.productName || suggestion.product?.name}</h3>
          <p className="text-sm text-gray-500">SKU: {suggestion.productSku || suggestion.product?.sku}</p>
        </div>
        <div className="flex space-x-2">
          <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(suggestion.status)}`}>
            {suggestion.status}
          </span>
          <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getTriggerColor(suggestion.triggerReason)}`}>
            {suggestion.triggerReason}
          </span>
        </div>
      </div>

      {type === 'pricing' ? (
        <div className="mt-4 grid grid-cols-2 gap-2 text-sm">
          <div>
            <p className="text-gray-500">Current Price</p>
            <p className="font-medium">${suggestion.currentPrice?.toFixed(2)}</p>
          </div>
          <div>
            <p className="text-gray-500">Recommended</p>
            <p className="font-medium">${suggestion.recommendedPrice?.toFixed(2)}</p>
          </div>
          <div>
            <p className="text-gray-500">Direction</p>
            <p className={`font-medium ${
              suggestion.direction === 'INCREASE' ? 'text-green-600' : 
              suggestion.direction === 'DECREASE' ? 'text-red-600' : 'text-gray-600'
            }`}>
              {suggestion.direction}
            </p>
          </div>
          <div>
            <p className="text-gray-500">Confidence</p>
            <p className="font-medium">{getConfidencePercentage(suggestion.confidence)}</p>
          </div>
        </div>
      ) : (
        <div className="mt-4 grid grid-cols-2 gap-2 text-sm">
          <div>
            <p className="text-gray-500">Current Stock</p>
            <p className="font-medium">{suggestion.currentStock}</p>
          </div>
          <div>
            <p className="text-gray-500">Recommended Qty</p>
            <p className="font-medium">{suggestion.recommendedQuantity}</p>
          </div>
          <div>
            <p className="text-gray-500">Lead Time</p>
            <p className="font-medium">{suggestion.suggestedLeadTime} days</p>
          </div>
          <div>
            <p className="text-gray-500">Confidence</p>
            <p className="font-medium">{getConfidencePercentage(suggestion.confidence)}</p>
          </div>
        </div>
      )}

      <div className="mt-3">
        <div className="w-full bg-gray-200 rounded-full h-1.5">
          <div 
            className="bg-purple-600 h-1.5 rounded-full" 
            style={{ width: getConfidencePercentage(suggestion.confidence) }}
          ></div>
        </div>
      </div>

      {suggestion.reasoning && (
        <div className="mt-3">
          <p className="text-xs text-gray-500">Reasoning</p>
          <p className="text-sm text-gray-700">{suggestion.reasoning}</p>
        </div>
      )}

      {suggestion.status === 'PENDING' && (
        <div className="mt-4 flex space-x-2">
          <button
            onClick={handleAccept}
            disabled={isProcessing}
            className={`inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded text-white bg-green-600 hover:bg-green-700 focus:outline-none ${
              isProcessing ? 'opacity-50 cursor-not-allowed' : ''
            }`}
          >
            {isProcessing ? 'Processing...' : 'Accept'}
          </button>
          <button
            onClick={handleReject}
            disabled={isProcessing}
            className={`inline-flex items-center px-3 py-1.5 border border-gray-300 text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50 focus:outline-none ${
              isProcessing ? 'opacity-50 cursor-not-allowed' : ''
            }`}
          >
            {isProcessing ? 'Processing...' : 'Reject'}
          </button>
        </div>
      )}
    </div>
  );
};

export default SuggestionCard;