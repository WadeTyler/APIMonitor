import APIResponse from "@/types/APIResponse";
import {Application} from "@/types/ApplicationTypes";

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