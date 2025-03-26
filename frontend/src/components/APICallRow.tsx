'use client';
import React from 'react';
import {APICall} from "@/types/APICallTypes";
import ResponseStatus from "@/components/ResponseStatus";

const ApiCallRow = ({apiCall}: {
  apiCall: APICall
}) => {

  return (
    <tr className="hover:bg-gray-50 cursor-pointer text-xs">
      <td className="p-4 text-left border-b-gray-200 border-b">{apiCall.path}</td>
      <td className="p-4 text-left border-b-gray-200 border-b">{apiCall.method}</td>
      <td className="p-4 text-left border-b-gray-200 border-b"><ResponseStatus status={apiCall.responseStatus} /></td>
      <td className="p-4 text-left border-b-gray-200 border-b">{apiCall.remoteAddress}</td>
      <td className="p-4 text-left border-b-gray-200 border-b">{new Date(apiCall.timestamp).toUTCString()}</td>
    </tr>
  );
};

export default ApiCallRow;
