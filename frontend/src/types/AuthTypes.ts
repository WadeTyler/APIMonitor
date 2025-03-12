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

export type ChangePasswordRequest = {
  currentPassword: string;
  newPassword: string;
  confirmNewPassword: string;
}