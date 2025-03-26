import APIResponse from "@/types/APIResponse";
import {AddAlertFieldRequest, Alert, AlertConfig, AlertField} from "@/types/AlertTypes";
import PageType from "@/types/PageTypes";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function fetchAlerts(request: {appId: string | undefined, pageNumber: number, pageSize: number}) {
  try {
    if (!request.appId) throw new Error("App ID is required to fetch alerts.");

    const response = await fetch(`${API_URL}/alerts?pageNumber=${request.pageNumber}&pageSize=${request.pageSize}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "appId": request.appId
      },
      credentials: "include"
    });

    const apiResponse: APIResponse<PageType<Alert[]>> = await response.json();

    console.log(apiResponse)

    if (!response.ok || !apiResponse.success || !apiResponse.data) {
      throw new Error(apiResponse.message);
    }

    return apiResponse.data;
  } catch (e) {
    console.error("Failed to fetch alerts: ", e);
    return null;
  }
}

export async function fetchAppAlertConfig(appId: string | undefined) {
  try {
    if (!appId) {
      throw new Error("App ID is required to fetch alert config.");
    }

    const response = await fetch(`${API_URL}/alerts/config`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "appId": appId
      },
      credentials: "include"
    });

    const apiResponse: APIResponse<AlertConfig> = await response.json();

    console.log("Alert Config response: ", apiResponse);

    if (!response.ok || !apiResponse.success || !apiResponse.data) {
      throw new Error(apiResponse.message);
    }

    return apiResponse.data;
  } catch (e) {
    console.log("Failed to fetch alert config: ", e);
    return null;
  }
}

export async function addAlertField(request: { appId: string, addAlertFieldRequest: AddAlertFieldRequest }) {

  if (!request.addAlertFieldRequest.path) request.addAlertFieldRequest.path = undefined;
  if (!request.addAlertFieldRequest.method) request.addAlertFieldRequest.method = undefined;
  if (!request.addAlertFieldRequest.responseStatus) request.addAlertFieldRequest.responseStatus = undefined;
  if (!request.addAlertFieldRequest.remoteAddress) request.addAlertFieldRequest.remoteAddress = undefined;

  if (request.addAlertFieldRequest.responseStatus) {
    if (typeof request.addAlertFieldRequest.responseStatus === "string") {
      request.addAlertFieldRequest.responseStatus = parseInt(request.addAlertFieldRequest.responseStatus);
    }
  }

  const response = await fetch(`${API_URL}/alerts/config/add-field`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "appId": request.appId
    },
    credentials: "include",
    body: JSON.stringify(request.addAlertFieldRequest)
  });

  const apiResponse: APIResponse<AlertField> = await response.json();

  if (!response.ok || !apiResponse.success || !apiResponse.data) {
    throw new Error(apiResponse.message);
  }

  return apiResponse.data;
}

export async function removeAlertField(request: { appId: string, alertFieldId: number}) {
  const response = await fetch(`${API_URL}/alerts/config/remove-field/${request.alertFieldId}`, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
      "appId": request.appId
    },
    credentials: "include"
  });

  const apiResponse: APIResponse<null | undefined> = await response.json();

  if (!response.ok || !apiResponse.success) {
    throw new Error(apiResponse.message);
  }

  return apiResponse.message;
}

export async function toggleEmailAlerts(appId: string) {
  const response = await fetch(`${API_URL}/alerts/config/toggle-email-alerts`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      "appId": appId
    },
    credentials: "include"
  });

  const apiResponse: APIResponse<boolean> = await response.json();

  if (!response.ok || !apiResponse.success || apiResponse.data == null) {
    throw new Error(apiResponse.message);
  }
  return apiResponse.data;
}