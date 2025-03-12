import React from 'react';
import Loader from "@/components/util/Loader";

const LoadingScreen = () => {
  return (
    <div className="flex items-center justify-center">
      <Loader />
    </div>
  );
};

export default LoadingScreen;