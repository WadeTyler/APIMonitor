import {APICall} from "@/types/APICallTypes";

export type AlertField = {
  id: number;
  path?: string;
  method?: string;
  responseStatus?: number;
  remoteAddress?: string;
}

export type AddAlertFieldRequest = {
  path?: string;
  method?: string;
  responseStatus?: number | string;
  remoteAddress?: string;
}

export interface AlertConfig {
  id: number;
  appId: string;
  emailAlertsEnabled: boolean;
  alertFields: AlertField[];
}

export interface Alert {
  id: number;
  alertField: AlertField;
  apiCall: APICall;
}