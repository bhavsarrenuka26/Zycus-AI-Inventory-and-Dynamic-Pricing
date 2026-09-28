import { useState, useEffect } from 'react';

const EmptyState = ({ title, description, icon }) => {
  return (
    <div className="text-center py-12">
      {icon && <div className="mx-auto mb-4">{icon}</div>}
      <h3 className="text-lg font-medium text-gray-900 mb-1">{title}</h3>
      <p className="text-gray-500">{description}</p>
    </div>
  );
};

export default EmptyState;