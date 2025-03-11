import APIResponse from "@/types/APIResponse";
import {APICall, ValidDirections, ValidSorts} from "@/types/APICallTypes";
import Page from "@/types/PageTypes";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function fetchApplicationAPICalls(appId: string, pageSize: number, pageNumber: number, search: string, sortBy: ValidSorts, direction: ValidDirections) {
  try {

    const apiUrl = `${API_URL}/apicalls?pageSize=${pageSize}&pageNumber=${pageNumber}&sortBy=${sortBy}&direction=${direction}${search && `&search=${search}`}`;

    const response = await fetch(apiUrl, {
      method: "GET",
      credentials: "include",
      headers: {
        "Content-Type": "applications/json",
        "appId": appId
      }
    });

    const apiResponse: APIResponse<Page<APICall[]>> = await response.json();

    if (!response.ok || !apiResponse.success || !apiResponse.data) {
      throw new Error(apiResponse.message);
    }

    return apiResponse.data;
  } catch (e) {
    throw new Error((e as Error).message || "Failed to load API Calls.");
  }
}