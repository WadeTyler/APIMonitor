

export type Application = {
  id: string;
  publicToken: string;
  userId: string;
  name: string;
  totalAPICalls?: number;
  totalUniqueRemoteAddr?: number;
  uniquePaths?: string[];
  methodCounts?: MethodCount[];
}

export type CreateApplicationRequest = {
  name: string;
}

type MethodCount = {
  method: string,
  count: number
}