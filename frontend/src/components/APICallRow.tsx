'use client';
import React from 'react';
import {APICall} from "@/types/APICallTypes";

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

const ResponseStatus = ({status}: {
  status: number;
}) => {

  function getHttpStatusMessage(code: number) {
    const statusMessages = new Map([
      [100, "CONTINUE"],
      [101, "SWITCHING PROTOCOLS"],
      [102, "PROCESSING"],
      [200, "OK"],
      [201, "CREATED"],
      [202, "ACCEPTED"],
      [203, "NON-AUTHORITATIVE INFORMATION"],
      [204, "NO CONTENT"],
      [205, "RESET CONTENT"],
      [206, "PARTIAL CONTENT"],
      [300, "MULTIPLE CHOICES"],
      [301, "MOVED PERMANENTLY"],
      [302, "FOUND"],
      [303, "SEE OTHER"],
      [304, "NOT MODIFIED"],
      [307, "TEMPORARY REDIRECT"],
      [308, "PERMANENT REDIRECT"],
      [400, "BAD REQUEST"],
      [401, "UNAUTHORIZED"],
      [403, "FORBIDDEN"],
      [404, "NOT FOUND"],
      [405, "METHOD NOT ALLOWED"],
      [408, "REQUEST TIMEOUT"],
      [409, "CONFLICT"],
      [410, "GONE"],
      [500, "INTERNAL SERVER ERROR"],
      [501, "NOT IMPLEMENTED"],
      [502, "BAD GATEWAY"],
      [503, "SERVICE UNAVAILABLE"],
      [504, "GATEWAY TIMEOUT"]
    ]);

    return statusMessages.has(code) ? `${code} ${statusMessages.get(code)}` : `${code}`;
  }

  return (
    <span className={`rounded-md px-2 py-1
    ${status >= 500
      ? 'text-danger bg-danger/20'
      : status >= 400
        ? 'text-warning bg-warning/20'
        : status >= 300
          ? 'text-blue-900 bg-blue-900/20'
          : status >= 200
            ? 'text-success bg-success/20'
            : ''
    }
    `}>
      {getHttpStatusMessage(status)}
    </span>
  )
}