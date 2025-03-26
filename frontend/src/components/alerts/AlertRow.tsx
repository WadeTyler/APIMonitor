import React from 'react';
import {Alert} from "@/types/AlertTypes";
import ResponseStatus from "@/components/ResponseStatus";

const AlertRow = ({alert}: {
  alert: Alert;
}) => {
  return (
    <tr className="hover:bg-gray-50 cursor-pointer text-xs">
      <td className="p-4 text-left border-b-gray-200 border-b">
        <span className="flex flex-col">
          {alert.alertField.path && <span>PATH: {alert.alertField.path}</span>}
          {alert.alertField.method && <span>METHOD: {alert.alertField.method}</span>}
          {alert.alertField.responseStatus && <span>STATUS: {alert.alertField.responseStatus}</span>}
          {alert.alertField.remoteAddress && <span>Address: {alert.alertField.remoteAddress}</span>}
        </span>
      </td>
      <td className="p-4 text-left border-b-gray-200 border-b">{alert.apiCall.path}</td>
      <td className="p-4 text-left border-b-gray-200 border-b">{alert.apiCall.method}</td>
      <td className="p-4 text-left border-b-gray-200 border-b"><ResponseStatus status={alert.apiCall.responseStatus} /></td>
      <td className="p-4 text-left border-b-gray-200 border-b">{alert.apiCall.remoteAddress}</td>
      <td className="p-4 text-left border-b-gray-200 border-b">{new Date(alert.apiCall.timestamp).toUTCString()}</td>
    </tr>
  );
};

export default AlertRow;