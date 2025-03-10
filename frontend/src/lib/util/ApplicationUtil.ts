import APIResponse from "@/types/APIResponse";
import {Application, CreateApplicationRequest} from "@/types/ApplicationTypes";

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
    console.error("Error fetching Application: ", (e as Error).message || "Failed to fetch application.");
    return null;
  }
}