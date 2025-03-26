'use client';
import React, {useState} from 'react';
import {AddAlertFieldRequest} from "@/types/AlertTypes";
import {RiSettingsLine} from "@remixicon/react";
import CloseButton from "@/components/util/CloseButton";
import {useMutation} from "@tanstack/react-query";
import {addAlertField} from "@/lib/util/AlertUtil";

const AddAlertFieldPanel = ({appId, refreshAlertConfig, close}: {
  appId: string;
  refreshAlertConfig: () => void;
  close: () => void;
}) => {

  // State
  const [addAlertFieldRequest, setAddAlertFieldRequest] = useState<AddAlertFieldRequest>({
    path: '',
    method: '',
    responseStatus: '',
    remoteAddress: '',
  });

  // Mutation
  const {mutate: addAlertFieldMutation, isPending: isAddingAlertField, error, isError} = useMutation({
    mutationFn: addAlertField,
    onSuccess: () => {
      refreshAlertConfig();
      close();
    }
  });

  // Functions
  async function handleAddAlertField() {
    if (isAddingAlertField) return;

    addAlertFieldMutation({appId, addAlertFieldRequest});
  }

  return (
    <div className="absolute page-padding w-96 h-screen right-0 top-0 flex flex-col gap-4 bg-white shadow-xl z-40 overflow-scroll">

      <div className="flex items-center justify-between">
        <h3 className="text-primary font-semibold text-lg">Add Alert Field</h3>
        <CloseButton close={close} />
      </div>
      <hr className="border w-full border-gray-300"/>
      <div className="input-container">
        <label className="input-label">Path:</label>
        <input type="text" className="input-bar" placeholder="Path" value={addAlertFieldRequest.path} onChange={(e) => setAddAlertFieldRequest(prev => ({...prev, path: e.target.value }))} />
      </div>
      <div className="input-container">
        <label className="input-label">Method:</label>
        <input type="text" className="input-bar" placeholder="Method" value={addAlertFieldRequest.method} onChange={(e) => setAddAlertFieldRequest(prev => ({...prev, method: e.target.value }))} />
      </div>
      <div className="input-container">
        <label className="input-label">Response Status:</label>
        <input type="text" className="input-bar" placeholder="Response Status" value={addAlertFieldRequest.responseStatus} onChange={(e) => setAddAlertFieldRequest(prev => ({...prev, responseStatus: e.target.value }))} />
      </div>
      <div className="input-container">
        <label className="input-label">Remote Address:</label>
        <input type="text" className="input-bar" placeholder="Remote Address" value={addAlertFieldRequest.remoteAddress} onChange={(e) => setAddAlertFieldRequest(prev => ({...prev, remoteAddress: e.target.value }))} />
      </div>

      {isError && (
        <p className="text-danger text-sm">{(error as Error).message}</p>
      )}

      <button className={`submit-btn2 ${isAddingAlertField && 'bg-dark!'}`} onClick={handleAddAlertField} disabled={isAddingAlertField}>
        <RiSettingsLine />
        <span>Add Alert Field</span>
      </button>


    </div>
  );
};

export default AddAlertFieldPanel;