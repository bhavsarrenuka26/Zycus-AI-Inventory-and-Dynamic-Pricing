import { useState, useEffect } from 'react';
import { api } from '../services/api';

const SaleModal = ({ product, isOpen, onClose, onRefresh }) => {
  const [quantity, setQuantity] = useState(1);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Reset quantity when modal opens
  useEffect(() => {
    if (isOpen) {
      setQuantity(1); // eslint-disable-line react-hooks/set-state-in-effect
    }
  }, [isOpen]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (quantity < 1) return;

    setIsSubmitting(true);
    try {
      await api.simulateSale(product.id, quantity);
      
      // Close modal and refresh data
      onClose();
      onRefresh();
      
      // Refresh suggestions after delays (2s, 4s, 6s)
      setTimeout(onRefresh, 2000);
      setTimeout(onRefresh, 4000);
      setTimeout(onRefresh, 6000);
    } catch (error) {
      console.error("Error simulating sale:", error);
      setIsSubmitting(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full">
        <div className="p-6">
          <h2 className="text-xl font-bold text-gray-900 mb-2">Simulate Sale</h2>
          
          <div className="mb-4">
            <h3 className="font-medium text-gray-900">{product.name}</h3>
            <p className="text-sm text-gray-500">SKU: {product.sku}</p>
          </div>

          <div className="mb-6">
            <p className="text-sm text-gray-500">Current Stock</p>
            <p className="text-lg font-medium">{product.stockLevel}</p>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="mb-6">
              <label htmlFor="quantity" className="block text-sm font-medium text-gray-700 mb-1">
                Quantity
              </label>
              <input
                type="number"
                id="quantity"
                min="1"
                max={product.stockLevel}
                value={quantity}
                onChange={(e) => setQuantity(Math.max(1, Math.min(product.stockLevel, parseInt(e.target.value) || 1)))}
                className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
              />
              <p className="mt-1 text-xs text-gray-500">
                Max: {product.stockLevel}
              </p>
            </div>

            <div className="flex justify-end space-x-3">
              <button
                type="button"
                onClick={onClose}
                className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={isSubmitting || quantity < 1}
                className={`inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-purple-600 hover:bg-purple-700 focus:outline-none ${
                  isSubmitting || quantity < 1 ? 'opacity-50 cursor-not-allowed' : ''
                }`}
              >
                {isSubmitting ? 'Processing...' : 'Simulate Sale'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default SaleModal;