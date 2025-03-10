import React from 'react';
import {RiRadarLine} from "@remixicon/react";

const NotFound = () => {
  return (
    <div className="page-padding w-full h-screen flex flex-col gap-4 items-center justify-center">
      <RiRadarLine className="text-primary size-12 mb-4" />
      <h1 className="text-dark text-xl">404 | Page Not Found</h1>
      <p>Could not find requested page.</p>
    </div>
  );
};

export default NotFound;