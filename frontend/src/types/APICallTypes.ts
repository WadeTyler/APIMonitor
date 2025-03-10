export type APICall = {
  id: number,
  appId: string,
  path: string,
  method: string,
  responseStatus: number,
  remoteAddress?: string;
  timestamp: string
};