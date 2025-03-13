export type User = {
  id: string,
}



export type UserProfile = {
  id: string,
  email: string,
  applicationCount: number,
  totalAPIRequests: number,
  totalUniqueRemoteAddresses: number
}

export type LoginRequest = {
  email: string,
  password: string
}

export type SignupRequest = {
  email: string;
  password: string;
  confirmPassword: string;
  verificationCode?: string;
}

export type ChangePasswordRequest = {
  currentPassword: string;
  newPassword: string;
  confirmNewPassword: string;
}