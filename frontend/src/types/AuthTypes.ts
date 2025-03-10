export type User = {
  id: string,
  email: string,
  password?: string | null
}

export type LoginRequest = {
  email: string,
  password: string
}
