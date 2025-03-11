export type APICall = {
  id: number,
  appId: string,
  path: string,
  method: string,
  responseStatus: number,
  remoteAddress?: string;
  timestamp: string
};

export type ValidSorts = "path" | "method" | "responseStatus" | "remoteAddress" | "timestamp";
export type ValidDirections = "DESC" | "ASC";