import { useState, useEffect } from 'react';

const Navbar = () => {
  const [isBackendConnected, setIsBackendConnected] = useState(false);

  useEffect(() => {
    const checkBackendStatus = async () => {
      try {
        await fetch("http://localhost:8080/actuator/health");
        setIsBackendConnected(true);
      } catch (error) {
        setIsBackendConnected(false);
      }
    };

    checkBackendStatus();
    const interval = setInterval(checkBackendStatus, 30000); // Check every 30 seconds

    return () => clearInterval(interval);
  }, []);

  return (
    <nav className="bg-white border-b border-gray-200 px-4 py-3 flex items-center justify-between">
      <div>
        <h1 className="text-xl font-bold text-gray-900">StockPulse</h1>
        <p className="text-xs text-gray-500">AI Inventory Intelligence</p>
      </div>
      <div className="flex items-center space-x-4">
        <span className="text-sm font-medium">Dashboard</span>
        <div className="flex items-center">
          <span className={`inline-block w-2 h-2 rounded-full mr-2 ${isBackendConnected ? 'bg-green-500' : 'bg-red-500'}`}></span>
          <span className="text-xs text-gray-500">
            {isBackendConnected ? 'Backend Connected' : 'Backend Offline'}
          </span>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;