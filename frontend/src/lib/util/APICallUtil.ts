import APIResponse from "@/types/APIResponse";
import {APICall} from "@/types/APICallTypes";
import toast from "react-hot-toast";
import Page from "@/types/PageTypes";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function fetchApplicationAPICalls(appId: string, pageSize: number, pageNumber: number, search: string) {
  try {

    const apiUrl = `${API_URL}/apicalls?pageSize=${pageSize}&pageNumber=${pageNumber}${search && `&search=${search}`}`;

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
    toast.error((e as Error).message || "Failed to load API Calls.");
    return [];
  }
}