'use client';
import React, {useState} from 'react';
import {AlertField} from "@/types/AlertTypes";
import {RiCloseLine} from "@remixicon/react";
import ConfirmPanel from "@/components/util/ConfirmPanel";
import {useMutation} from "@tanstack/react-query";
import {removeAlertField} from "@/lib/util/AlertUtil";
import toast from "react-hot-toast";

const AlertFieldPanel = ({appId, alertField, refreshAlertConfig}: {
  appId: string;
  alertField: AlertField;
  refreshAlertConfig: () => void;
}) => {
  // States
  const [hovering, setHovering] = useState(false);
  const [confirmingRemove, setConfirmingRemove] = useState(false);

  const {mutate: removeAlertFieldMutation, isPending: isRemovingAlertField} = useMutation({
    mutationFn: removeAlertField,
    onSuccess: () => {
      refreshAlertConfig();
      setConfirmingRemove(false);
    },
    onError: (e) => {
      toast.error((e as Error).message || "Failed to remove field.");
    }
  })

  // Handle remove alert field
  async function handleRemoveAlertField() {
    if (isRemovingAlertField) return;

    removeAlertFieldMutation({appId, alertFieldId: alertField.id});
  }

  return (
    <div
      onMouseEnter={() => setHovering(true)}
      onMouseLeave={() => setHovering(false)}
      className="relative w-full h-full rounded-md border border-gray-300 p-4 duration-200 hover:bg-background hover:shadow-lg hover:border-primary cursor-pointer"
    >
      {hovering &&
        <button
          onClick={() => setConfirmingRemove(true)}
          disabled={isRemovingAlertField}
          className="absolute top-0 right-0 translate-x-1/2 -translate-y-1/2 bg-dark hover:scale-90 text-primary hover:text-white cursor-pointer duration-200 rounded-full">
          <RiCloseLine/>
        </button>
      }

      <div className="w-full h-full overflow-hidden">
        <p>Id: {alertField.id}</p>
        {alertField.path && <p>Path: {alertField.path}</p>}
        {alertField.method && <p>Method: {alertField.method}</p>}
        {alertField.responseStatus && <p>Response Status: {alertField.responseStatus}</p>}
        {alertField.remoteAddress && <p>Remote Address: {alertField.remoteAddress}</p>}
      </div>

      {confirmingRemove &&
        <ConfirmPanel
          title={"Remove Alert Field"}
          description={"You are about to remove an alert field. This action is irreversible. Are you sure you want to do this?"}
          confirmText={"Confirm"} confirm={handleRemoveAlertField}
          cancelText={"Nevermind"} cancel={() => setConfirmingRemove(false)}
        />
      }

    </div>
  );
};

export default AlertFieldPanel;
