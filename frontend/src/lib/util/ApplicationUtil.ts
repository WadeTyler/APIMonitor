import APIResponse from "@/types/APIResponse";
import {Application, CreateApplicationRequest, UpdateApplicationRequest} from "@/types/ApplicationTypes";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function fetchApplications() {
  try {
    const response = await fetch(`${API_URL}/applications/`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include"
    });

    const apiResponse: APIResponse<Application[]> = await response.json();

    if (!response.ok || !apiResponse.success || !apiResponse.data)
      throw new Error(apiResponse.message);

    return apiResponse.data;
  } catch (e) {
    throw new Error((e as Error).message || "Failed to retrieve applications.");
  }
}

export async function createApplication(createApplicationRequest: CreateApplicationRequest) {
  try {
    const response = await fetch(`${API_URL}/applications/create`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(createApplicationRequest)
    });

    const apiResponse: APIResponse<Application> = await response.json();

    if (!response.ok || !apiResponse.success || !apiResponse.data)
      throw new Error(apiResponse.message);

    return apiResponse.data;
  } catch (e) {
    throw new Error((e as Error).message || "Failed to create application.");
  }
}

export async function fetchApplicationFromPublicToken(publicToken: string) {
  try {
    const response = await fetch(`${API_URL}/applications/${publicToken}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json"
      },
      credentials: "include"
    });

    const apiResponse: APIResponse<Application> = await response.json();

    if (!response.ok || !apiResponse.success || !apiResponse.data)
      throw new Error(apiResponse.message);

    return apiResponse.data;
  } catch (e) {
    console.log("Error fetching Application: ", (e as Error).message || "Failed to fetch application.");
    return null;
  }
}

export async function deleteApplication(appId: string) {
  try {
    const response = await fetch(`${API_URL}/applications/delete`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
        "appId": appId
      },
      credentials: "include"
    });

    const apiResponse: APIResponse<null> = await response.json();

    if (!response.ok || !apiResponse.success)
      throw new Error(apiResponse.message);

    return apiResponse.message;
  } catch (e) {
    throw new Error((e as Error).message || "Failed to delete application");
  }
}

export async function updateApplication(updateData: { appId: string, updateRequest: UpdateApplicationRequest}) {
  try {
    const response = await fetch(`${API_URL}/applications/`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "appId": updateData.appId
      },
      credentials: "include",
      body: JSON.stringify(updateData.updateRequest)
    });

    const apiResponse: APIResponse<Application> = await response.json();

    if (!response.ok || !apiResponse.success || !apiResponse.data)
      throw new Error(apiResponse.message);

    return apiResponse.data;
  } catch (e) {
    throw new Error((e as Error).message || "Failed to update application");
  }
}