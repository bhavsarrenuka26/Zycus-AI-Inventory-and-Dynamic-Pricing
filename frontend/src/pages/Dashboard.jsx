import { useState, useEffect, useCallback } from 'react';
import { api } from '../services/api';
import Navbar from '../components/Navbar';
import StatsCard from '../components/StatsCard';
import ProductCard from '../components/ProductCard';
import SuggestionCard from '../components/SuggestionCard';
import SaleModal from '../components/SaleModal';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';

const Dashboard = () => {
  const [products, setProducts] = useState([]);
  const [pricingSuggestions, setPricingSuggestions] = useState([]);
  const [reorderSuggestions, setReorderSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    totalProducts: 0,
    lowStock: 0,
    pendingPricing: 0,
    pendingReorder: 0,
  });
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('all');
  const [stockFilter, setStockFilter] = useState('all');
  const [error, setError] = useState(null);

  const updateData = (productsData, pricingData, reorderData) => {
    setProducts(productsData);
    setPricingSuggestions(pricingData);
    setReorderSuggestions(reorderData);

    const lowStockCount = productsData.filter(
      (product) =>
        product.stockLevel > 0 &&
        product.stockLevel < product.reorderThreshold
    ).length;

    const pendingPricingCount = pricingData.filter(
      (suggestion) => suggestion.status === 'PENDING'
    ).length;

    const pendingReorderCount = reorderData.filter(
      (suggestion) => suggestion.status === 'PENDING'
    ).length;

    setStats({
      totalProducts: productsData.length,
      lowStock: lowStockCount,
      pendingPricing: pendingPricingCount,
      pendingReorder: pendingReorderCount,
    });
  };

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const [productsData, pricingData, reorderData] = await Promise.all([
        api.getProducts(),
        api.getPricingSuggestions(),
        api.getReorderSuggestions(),
      ]);

      updateData(productsData, pricingData, reorderData);
    } catch (err) {
      console.error('Error fetching data:', err);
      setError(
        'Failed to load data from backend. Please check if the backend is running.'
      );
    } finally {
      setLoading(false);
    }
  }, []);

  const refreshData = useCallback(async () => {
    try {
      setError(null);

      const [productsData, pricingData, reorderData] = await Promise.all([
        api.getProducts(),
        api.getPricingSuggestions(),
        api.getReorderSuggestions(),
      ]);

      updateData(productsData, pricingData, reorderData);
    } catch (err) {
      console.error('Error refreshing data:', err);
      setError(
        'Failed to refresh data from backend. Please check if the backend is running.'
      );
    }
  }, []);

  useEffect(() => {
    fetchData();

    const handleOpenModal = (event) => {
      setSelectedProduct(event.detail);
      setIsModalOpen(true);
    };

    window.addEventListener('openSaleModal', handleOpenModal);

    return () => {
      window.removeEventListener('openSaleModal', handleOpenModal);
    };
  }, [fetchData]);

  const categories = [
    ...new Set(products.map((product) => product.category).filter(Boolean)),
  ];

  const filteredProducts = products.filter((product) => {
    const name = product.name?.toLowerCase() || '';
    const sku = product.sku?.toLowerCase() || '';
    const search = searchTerm.toLowerCase();

    const matchesSearch = name.includes(search) || sku.includes(search);

    const matchesCategory =
      categoryFilter === 'all' || product.category === categoryFilter;

    let matchesStock = true;

    if (stockFilter === 'healthy') {
      matchesStock = product.stockLevel >= product.reorderThreshold;
    } else if (stockFilter === 'low') {
      matchesStock =
        product.stockLevel > 0 &&
        product.stockLevel < product.reorderThreshold;
    } else if (stockFilter === 'out') {
      matchesStock = product.stockLevel === 0;
    }

    return matchesSearch && matchesCategory && matchesStock;
  });

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <main className="p-6">
          <div className="flex items-center justify-center h-64">
            <LoadingSpinner size="lg" />
          </div>
        </main>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <main className="p-6">
          <div className="max-w-2xl p-8 mx-auto text-center bg-white border border-gray-200 rounded-lg shadow-sm">
            <h2 className="mb-2 text-xl font-bold text-gray-900">
              Backend unavailable
            </h2>
            <p className="text-gray-600">{error}</p>
            <button
              onClick={fetchData}
              className="inline-flex items-center px-4 py-2 mt-4 text-sm font-medium text-white bg-purple-600 border border-transparent rounded-md hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-purple-500"
            >
              Retry
            </button>
          </div>
        </main>
      </div>
    );
  }

  const pendingPricingSuggestions = pricingSuggestions.filter(
    (suggestion) => suggestion.status === 'PENDING'
  );

  const pendingReorderSuggestions = reorderSuggestions.filter(
    (suggestion) => suggestion.status === 'PENDING'
  );

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <SaleModal
        product={selectedProduct}
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setSelectedProduct(null);
        }}
        onRefresh={refreshData}
      />

      <main className="p-6">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-900">
            Inventory Intelligence
          </h1>
          <p className="text-gray-600">
            Monitor inventory, simulate sales, and review AI-powered
            recommendations.
          </p>
        </div>

        <div className="mb-8">
          <button
            onClick={fetchData}
            className="inline-flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-purple-500"
          >
            Refresh Data
          </button>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 gap-6 mb-8 md:grid-cols-2 lg:grid-cols-4">
          <StatsCard
            title="Total Products"
            value={stats.totalProducts}
            description="All products in inventory"
          />
          <StatsCard
            title="Low Stock"
            value={stats.lowStock}
            description="Products below threshold"
          />
          <StatsCard
            title="Pending Pricing"
            value={stats.pendingPricing}
            description="Pricing suggestions"
          />
          <StatsCard
            title="Pending Reorder"
            value={stats.pendingReorder}
            description="Reorder suggestions"
          />
        </div>

        {/* Products Section */}
        <div className="mb-12">
          <div className="flex flex-col mb-6 md:flex-row md:items-center md:justify-between">
            <h2 className="text-xl font-bold text-gray-900">Inventory</h2>

            <div className="flex flex-col mt-4 sm:flex-row sm:space-x-4 md:mt-0">
              <div className="mb-2 sm:mb-0">
                <input
                  type="text"
                  placeholder="Search products..."
                  value={searchTerm}
                  onChange={(event) => setSearchTerm(event.target.value)}
                  className="w-full px-3 py-2 text-sm border border-gray-300 rounded-md shadow-sm sm:w-auto focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
                  aria-label="Search products"
                />
              </div>

              <div className="flex space-x-2">
                <select
                  value={categoryFilter}
                  onChange={(event) => setCategoryFilter(event.target.value)}
                  className="px-3 py-2 text-sm bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
                  aria-label="Filter by category"
                >
                  <option value="all">All Categories</option>
                  {categories.map((category) => (
                    <option key={category} value={category}>
                      {category}
                    </option>
                  ))}
                </select>

                <select
                  value={stockFilter}
                  onChange={(event) => setStockFilter(event.target.value)}
                  className="px-3 py-2 text-sm bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
                  aria-label="Filter by stock status"
                >
                  <option value="all">All Stock</option>
                  <option value="healthy">Healthy</option>
                  <option value="low">Low Stock</option>
                  <option value="out">Out of Stock</option>
                </select>
              </div>
            </div>
          </div>

          {filteredProducts.length === 0 ? (
            <EmptyState
              title="No products found"
              description="Try adjusting your search or filter criteria"
            />
          ) : (
            <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
              {filteredProducts.map((product) => (
                <ProductCard
                  key={product.id}
                  product={product}
                  onRefresh={refreshData}
                />
              ))}
            </div>
          )}
        </div>

        {/* AI Recommendations Section */}
        <div>
          <h2 className="mb-6 text-xl font-bold text-gray-900">
            AI Recommendations
          </h2>

          <div className="grid grid-cols-1 gap-8 lg:grid-cols-2">
            {/* Pricing Suggestions */}
            <div>
              <h3 className="mb-4 text-lg font-medium text-gray-900">
                Pricing Suggestions
              </h3>

              {pendingPricingSuggestions.length === 0 ? (
                <EmptyState
                  title="No pending pricing recommendations"
                  description="AI will generate pricing suggestions based on inventory and demand conditions."
                />
              ) : (
                <div className="space-y-4">
                  {pendingPricingSuggestions.map((suggestion) => (
                    <SuggestionCard
                      key={suggestion.id}
                      suggestion={suggestion}
                      type="pricing"
                      onRefresh={refreshData}
                    />
                  ))}
                </div>
              )}
            </div>

            {/* Reorder Suggestions */}
            <div>
              <h3 className="mb-4 text-lg font-medium text-gray-900">
                Reorder Suggestions
              </h3>

              {pendingReorderSuggestions.length === 0 ? (
                <EmptyState
                  title="No pending reorder recommendations"
                  description="AI will generate reorder suggestions based on demand forecasts and lead times."
                />
              ) : (
                <div className="space-y-4">
                  {pendingReorderSuggestions.map((suggestion) => (
                    <SuggestionCard
                      key={suggestion.id}
                      suggestion={suggestion}
                      type="reorder"
                      onRefresh={refreshData}
                    />
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default Dashboard;
