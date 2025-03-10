import APIResponse from "@/types/APIResponse";
import {LoginRequest, User} from "@/types/AuthTypes";

const API_URL = process.env.NEXT_PUBLIC_API_URL;

export async function fetchAuthUser() {
  try {
    const response = await fetch(`${API_URL}/user`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json"
      },
      credentials: "include"
    });

    const apiResponse: APIResponse<User> = await response.json();

    console.log(apiResponse);

    if (!response.ok || !apiResponse.success || !apiResponse.data) {
      throw new Error(apiResponse.message);
    }

    console.log("Authorized and Setting authUser.");

    return apiResponse.data;
  } catch (e) {
    console.log("Failed to Authenticated ", (e as Error).message);
    return null;
  }
}

export async function attemptLogin(loginRequest: LoginRequest) {
  try {
    const response = await fetch(`${API_URL}/user/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      credentials: "include",
      body: JSON.stringify(loginRequest)
    });

    const apiResponse: APIResponse<User> = await response.json();

    if (!response.ok || !apiResponse.success)
      throw new Error(apiResponse.message);

    return apiResponse.data;
  } catch (e) {
    throw new Error((e as Error).message || "Failed to Login. Try again later.");
  }
}

export async function logout() {
  try {
    const response = await fetch(`${API_URL}/user/logout`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      credentials: "include"
    });

    const apiResponse: APIResponse<undefined | null> = await response.json();

    if (!response.ok || !apiResponse.success)
      throw new Error(apiResponse.message);
  } catch (e) {
    throw new Error((e as Error).message || "Failed to Logout");
  }
}