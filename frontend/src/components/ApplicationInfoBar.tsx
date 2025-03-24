'use client';
import React, {useState} from 'react';
import {
  RiAppStoreLine,
  RiClipboardLine, RiEyeLine,
  RiFingerprintLine, RiInputMethodLine,
  RiKey2Line, RiLinksLine, RiRouteLine,
  RiSignalTowerLine,
} from "@remixicon/react";
import {Application} from "@/types/ApplicationTypes";
import toast from "react-hot-toast";
import PathsPanel from "@/components/PathsPanel";

const ApplicationInfoBar = ({currentApplication, searchForValue}: {
  currentApplication: Application;
  searchForValue: (value: string) => Promise<void>;
}) => {

  // States
  const [showPaths, setShowPaths] = useState(false);

  // Functions
  function copyPublicToken() {
    if (!currentApplication?.publicToken) return;
    navigator.clipboard.writeText(currentApplication?.publicToken);
    toast.success("Public Token copied to Clipboard.");
  }

  function copyApplicationId() {
    if (!currentApplication?.id) return;
    navigator.clipboard.writeText(currentApplication?.id);
    toast.success("Application ID copied to Clipboard.");
  }

  return (
    <div
      className="w-full p-4 flex md:flex-row flex-wrap justify-between gap-8 bg-gray-50 rounded-md border-gray-200 border lg:text-base text-sm">
      <div className="flex flex-col gap-4 lg:text-base text-xs">
        {/* App Name*/}
        <section className="inline-flex gap-2 font-semibold items-center">
          <RiAppStoreLine/>
          <span className="text-secondary">Application:</span>
          <span className="text-foreground">{currentApplication?.name}</span>
        </section>

        {/* App ID */}
        <section className="inline-flex gap-2 font-semibold items-center">
          <RiFingerprintLine/>
          <span className="text-secondary">ID:</span>
          <span className="text-foreground inline-flex gap-2">
                ****
                <RiClipboardLine
                  className="text-sm p-1 text-light bg-dark rounded-md relative cursor-pointer hover:bg-primary-dark duration-200"
                  onClick={copyApplicationId}/>
              </span>
        </section>

        {/* Public Token */}
        <section className="inline-flex gap-2 font-semibold items-center">
          <RiKey2Line/>
          <span className="text-secondary">Public Token:</span>
          <span className="text-foreground">
                <span className="text-foreground inline-flex gap-2">
                {currentApplication?.publicToken.substring(0, 8) + "..."}
                  <RiClipboardLine
                    className="text-sm p-1 text-light bg-dark rounded-md relative cursor-pointer hover:bg-primary-dark duration-200"
                    onClick={copyPublicToken}/>
              </span>
              </span>
        </section>
      </div>

      {/* Middle Section (Totals) */}
      <div className="flex flex-col gap-4 lg:text-base text-xs">
        {/* Total API Calls */}
        <section className="inline-flex gap-2 font-semibold items-center">
          <RiSignalTowerLine/>
          <span className="text-secondary">Total Requests:</span>
          <span className="text-foreground">{currentApplication.totalAPICalls}</span>
        </section>

        {/* Total Unique Addresses */}
        <section className="inline-flex gap-2 font-semibold items-center">
          <RiLinksLine/>
          <span className="text-secondary">Total Addresses:</span>
          <span className="text-foreground">{currentApplication.totalAPICalls}</span>
        </section>

        {/* Total Paths */}
        <section className="inline-flex gap-2 font-semibold items-center">
          <RiRouteLine/>
          <span className="text-secondary">Total Paths:</span>
          <span className="text-foreground">{currentApplication.uniquePaths?.length}</span>
          <RiEyeLine
            className="lg:text-base text-xs p-1 text-light bg-dark rounded-md relative cursor-pointer hover:bg-primary-dark duration-200"
            onClick={() => setShowPaths(prev => !prev)}
          />
        </section>
      </div>

      <div className="flex flex-col gap-1 lg:text-base text-xs">
        <section className="inline-flex gap-2 font-semibold items-center">
          <RiInputMethodLine/>
          <span className="text-secondary">Method Counts:</span>
          <span className="text-foreground">{currentApplication.methodCounts?.length}</span>
        </section>

        {currentApplication.methodCounts?.map((methodCount) => (
          <section
            key={methodCount.method}
            className="inline-flex gap-2 font-semibold items-center text-xs"
          >
            <span
              className="text-secondary hover:text-primary hover:underline duration-200 cursor-pointer"
              onClick={() => searchForValue(methodCount.method)}
            >
              {methodCount.method}
            </span>
            <span className="text-foreground">{methodCount.count}</span>
          </section>
        ))}
      </div>


      {showPaths && currentApplication.uniquePaths &&
        <PathsPanel paths={currentApplication.uniquePaths} close={() => setShowPaths(false)}
                    searchForValue={searchForValue}/>
      }

    </div>

  );
};

export default ApplicationInfoBar;