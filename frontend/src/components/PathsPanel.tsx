import React from 'react';
import CloseButton from "@/components/util/CloseButton";

const PathsPanel = ({paths, close, searchForValue}: {
  paths: string[];
  close: () => void;
  searchForValue?: (value: string) => Promise<void>;
}) => {

  async function searchForPath(path: string) {
    if (!searchForValue) return;

    await searchForValue(path);
    close();
  }

  return (
    <div
      className="fixed right-0 top-0 z-40 sm:max-w-96 w-full h-screen flex flex-col gap-4 page-padding bg-light shadow-xl overflow-scroll">

      <div className="flex items-center justify-between">
        <h2 className="text-dark text-xl font-semibold">Paths</h2>
        {/* Close button */}
        <CloseButton close={close} />
      </div>

      <hr className="border w-full border-gray-300"/>
      <p className="text-sm italic">Click on a path to search for all results containing that path!</p>
      <hr className="border w-full border-gray-300"/>

      {/* Paths */}
      <div className="flex flex-col gap-2">
        {paths.map((path) => (
          <span
            key={path}
            className="hover:text-primary hover:underline cursor-pointer"
            onClick={() => searchForPath(path)}
          >
            {path}
          </span>
        ))}
      </div>

    </div>
  );
};

export default PathsPanel;