const API_BASE_URL = "http://localhost:8080";

// Helper function to handle API responses
const handleResponse = async (response) => {
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  return await response.json();
};

// Helper function to handle errors
const handleError = (error) => {
  console.error("API Error:", error);
  throw error;
};

export const api = {
  // Get all products
  getProducts: () => {
    return fetch(`${API_BASE_URL}/products`)
      .then(handleResponse)
      .catch(handleError);
  },

  // Get pricing suggestions
  getPricingSuggestions: () => {
    return fetch(`${API_BASE_URL}/pricing-suggestions`)
      .then(handleResponse)
      .catch(handleError);
  },

  // Get reorder suggestions
  getReorderSuggestions: () => {
    return fetch(`${API_BASE_URL}/reorder-suggestions`)
      .then(handleResponse)
      .catch(handleError);
  },

  // Simulate a sale
  simulateSale: (productId, quantity) => {
    return fetch(`${API_BASE_URL}/products/${productId}/orders`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ quantity }),
    })
      .then(handleResponse)
      .catch(handleError);
  },

  // Suggest pricing for a product
  suggestPricing: (productId) => {
    return fetch(`${API_BASE_URL}/products/${productId}/suggest-pricing`, {
      method: "POST",
    })
      .then(handleResponse)
      .catch(handleError);
  },

  // Suggest reorder for a product
  suggestReorder: (productId) => {
    return fetch(`${API_BASE_URL}/products/${productId}/suggest-reorder`, {
      method: "POST",
    })
      .then(handleResponse)
      .catch(handleError);
  },

  // Accept pricing suggestion
  acceptPricingSuggestion: (id) => {
    return fetch(`${API_BASE_URL}/pricing-suggestions/${id}`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ status: "ACCEPTED" }),
    })
      .then(handleResponse)
      .catch(handleError);
  },

  // Reject pricing suggestion
  rejectPricingSuggestion: (id) => {
    return fetch(`${API_BASE_URL}/pricing-suggestions/${id}`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ status: "REJECTED" }),
    })
      .then(handleResponse)
      .catch(handleError);
  },

  // Accept reorder suggestion
  acceptReorderSuggestion: (id) => {
    return fetch(`${API_BASE_URL}/reorder-suggestions/${id}`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ status: "ACCEPTED" }),
    })
      .then(handleResponse)
      .catch(handleError);
  },

  // Reject reorder suggestion
  rejectReorderSuggestion: (id) => {
    return fetch(`${API_BASE_URL}/reorder-suggestions/${id}`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ status: "REJECTED" }),
    })
      .then(handleResponse)
      .catch(handleError);
  },
};