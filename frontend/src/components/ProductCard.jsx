import { useState } from 'react';
import { api } from '../services/api';

const ProductCard = ({ product, onRefresh }) => {
  const [isAnalyzingPricing, setIsAnalyzingPricing] = useState(false);
  const [isAnalyzingReorder, setIsAnalyzingReorder] = useState(false);

  const handleAnalyzePricing = async () => {
    setIsAnalyzingPricing(true);
    try {
      await api.suggestPricing(product.id);
      // Wait a bit for the backend to process and then refresh
      setTimeout(() => {
        onRefresh();
        setIsAnalyzingPricing(false);
      }, 2000);
    } catch (error) {
      console.error("Error analyzing pricing:", error);
      setIsAnalyzingPricing(false);
    }
  };

  const handleAnalyzeReorder = async () => {
    setIsAnalyzingReorder(true);
    try {
      await api.suggestReorder(product.id);
      // Wait a bit for the backend to process and then refresh
      setTimeout(() => {
        onRefresh();
        setIsAnalyzingReorder(false);
      }, 2000);
    } catch (error) {
      console.error("Error analyzing reorder:", error);
      setIsAnalyzingReorder(false);
    }
  };

  const getStatus = () => {
    if (product.stockLevel === 0) return "OUT OF STOCK";
    if (product.stockLevel < product.reorderThreshold) return "LOW STOCK";
    return "HEALTHY";
  };

  const statusColor = () => {
    const status = getStatus();
    if (status === "OUT OF STOCK") return "bg-red-100 text-red-800";
    if (status === "LOW STOCK") return "bg-yellow-100 text-yellow-800";
    return "bg-green-100 text-green-800";
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 hover:shadow-md transition-shadow">
      <div className="flex justify-between items-start">
        <div>
          <h3 className="font-medium text-gray-900">{product.name}</h3>
          <p className="text-sm text-gray-500">SKU: {product.sku}</p>
          <p className="text-sm text-gray-500">Category: {product.category}</p>
        </div>
        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusColor()}`}>
          {getStatus()}
        </span>
      </div>

      <div className="mt-4 grid grid-cols-2 gap-2 text-sm">
        <div>
          <p className="text-gray-500">Price</p>
          <p className="font-medium">${product.price?.toFixed(2)}</p>
        </div>
        <div>
          <p className="text-gray-500">Stock</p>
          <p className="font-medium">{product.stockLevel}</p>
        </div>
        <div>
          <p className="text-gray-500">Threshold</p>
          <p className="font-medium">{product.reorderThreshold}</p>
        </div>
        <div>
          <p className="text-gray-500">Velocity</p>
          <p className="font-medium">{product.demandVelocity}</p>
        </div>
      </div>

      <div className="mt-4 flex flex-wrap gap-2">
        <button 
          onClick={() => window.dispatchEvent(new CustomEvent('openSaleModal', { detail: product }))}
          className="inline-flex items-center px-3 py-1.5 border border-gray-300 shadow-sm text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50 focus:outline-none"
        >
          Simulate Sale
        </button>
        <button 
          onClick={handleAnalyzePricing}
          disabled={isAnalyzingPricing}
          className={`inline-flex items-center px-3 py-1.5 border border-gray-300 shadow-sm text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50 focus:outline-none ${
            isAnalyzingPricing ? 'opacity-50 cursor-not-allowed' : ''
          }`}
        >
          {isAnalyzingPricing ? 'Analyzing...' : 'Analyze Pricing'}
        </button>
        <button 
          onClick={handleAnalyzeReorder}
          disabled={isAnalyzingReorder}
          className={`inline-flex items-center px-3 py-1.5 border border-gray-300 shadow-sm text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50 focus:outline-none ${
            isAnalyzingReorder ? 'opacity-50 cursor-not-allowed' : ''
          }`}
        >
          {isAnalyzingReorder ? 'Analyzing...' : 'Analyze Reorder'}
        </button>
      </div>
    </div>
  );
};

export default ProductCard;