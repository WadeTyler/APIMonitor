interface APIResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
}

export default APIResponse;
