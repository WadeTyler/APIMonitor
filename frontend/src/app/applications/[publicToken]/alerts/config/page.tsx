'use client';
import React, {useEffect, useState} from 'react';
import {useParams} from "next/navigation";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import AuthProvider from "@/components/providers/AuthProvider";
import {fetchApplicationFromPublicToken} from "@/lib/util/ApplicationUtil";
import LoadingScreen from "@/components/util/LoadingScreen";
import {fetchAppAlertConfig, toggleEmailAlerts} from "@/lib/util/AlertUtil";
import ApplicationInfoBar from "@/components/ApplicationInfoBar";
import {RiAlertLine, RiSettingsLine} from "@remixicon/react";
import AlertFieldPanel from "@/components/alerts/AlertFieldPanel";
import AddAlertFieldPanel from "@/components/alerts/AddAlertFieldPanel";
import Link from "next/link";
import toast from "react-hot-toast";
import NotFound from "@/app/not-found";

const Page = () => {
  // Params
  const params = useParams<{ publicToken: string }>();

  // QueryClient
  const queryClient = useQueryClient();

  // Current Application
  const {
    data: currentApplication,
    isPending: isLoadingCurrentApplication
  } = useQuery({
    queryKey: ['currentApplication', params.publicToken],
    queryFn: () => fetchApplicationFromPublicToken(params.publicToken)
  });

  // Alert Config
  const {
    data: alertConfig,
    isPending: isLoadingAlertConfig
  } = useQuery({
    queryKey: ['alertConfig', currentApplication?.id],
    queryFn: () => fetchAppAlertConfig(currentApplication?.id)
  });

  // Toggle Email Alerts mutation
  const { mutate: toggleEmailAlertsMutation, isPending: isTogglingEmailAlerts } = useMutation({
    mutationFn: toggleEmailAlerts,
    onSuccess: (newValue: boolean) => {
      if (alertConfig) {
        alertConfig.emailAlertsEnabled = newValue;
      } else {
        refreshAlertConfig();
      }
    },
    onError: (e) => {
      toast.error((e as Error).message || "Something went wrong. Try again later.");
    }
  })


  // States
  const [showAddAlertFieldPanel, setShowAddAlertFieldPanel] = useState(false);

  // Functions
  async function refreshAlertConfig() {
    await queryClient.invalidateQueries({queryKey: ['alertConfig', currentApplication?.id]});
  }

  function handleToggleEmailAlerts() {
    if (isTogglingEmailAlerts || !currentApplication) return;

    toggleEmailAlertsMutation(currentApplication.id);
  }

  // Use Effect
  useEffect(() => {
    // Refresh current application on public token change
    queryClient.invalidateQueries({queryKey: ['currentApplication']});
  }, [params.publicToken]);

  useEffect(() => {
    // Refresh alert config on application change
    if (currentApplication) {
      refreshAlertConfig();
    }
  }, [currentApplication]);

  // Returns

  if (isLoadingCurrentApplication || isLoadingAlertConfig) return <LoadingScreen/>

  if (!currentApplication && !isLoadingCurrentApplication) {
    return <AuthProvider>
      <NotFound/>
    </AuthProvider>
  }

  return (
    <AuthProvider>
      <div className="page-padding w-full min-h-screen flex flex-col items-center">
        <div
          className="flex flex-col gap-4 max-w-[80rem] lg:max-h-[45rem] md:max-h-[50rem] sm:max-h-[52rem] h-full w-full bg-white shadow-md rounded-md lg:p-8 p-4">
          <div className="flex items-center justify-between w-full lg:text-base text-xs">
            <span className="text-xl font-semibold text-dark">Alert Configuration</span>

            <Link href={`/applications/${params.publicToken}/alerts`}
                  className="text-primary p-1 rounded-md hover:bg-dark duration-200 cursor-pointer inline-flex gap-1 items-center">
              <RiAlertLine/>
              <span>Alerts</span>
            </Link>
          </div>

          {/* Info Bar */}
          {(currentApplication)
            ? <ApplicationInfoBar currentApplication={currentApplication}/>
            : <div className="animate-pulse w-full h-14 p-2 gap-4 bg-gray-300 rounded-md border-gray-200 border"/>
          }

          {/* Action Bar */}
          <div className="w-full flex justify-between items-center gap-8">

            {/*TODO: Implement Link to alert fields doc page*/}
            <Link href={"/docs/alerts/alert-fields"} className="app-link text-sm">
              How do alert fields work?
            </Link>

            {!alertConfig?.emailAlertsEnabled
              ? <button className={`submit-btn bg-orange-400! hover:bg-orange-600! ${isTogglingEmailAlerts && 'bg-orange-800! hover:bg-orange-800! cursor-not-allowed'}`} disabled={isTogglingEmailAlerts} onClick={handleToggleEmailAlerts}>Disable Email Alerts</button>
              : <button className={`submit-btn ${isTogglingEmailAlerts && 'bg-dark! hover:bg-dark! cursor-not-allowed'}`} disabled={isTogglingEmailAlerts} onClick={handleToggleEmailAlerts}>Enable Email Alerts</button>
            }

          </div>

          <p className="font-semibold text-dark text-lg">Alert Fields</p>

          {/* Grid */}
          <div className="h-full rounded-md text-secondary grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">

            {currentApplication && alertConfig?.alertFields.map((alertField) => (
              <AlertFieldPanel appId={currentApplication?.id} refreshAlertConfig={refreshAlertConfig}
                               alertField={alertField} key={alertField.id}/>
            ))}
          </div>

          <button className="submit-btn2" onClick={() => setShowAddAlertFieldPanel(true)}>
            <RiSettingsLine/>
            <span>Add Alert Field</span>
          </button>


        </div>

        {showAddAlertFieldPanel && currentApplication &&
          <AddAlertFieldPanel appId={currentApplication.id} refreshAlertConfig={refreshAlertConfig}
                              close={() => setShowAddAlertFieldPanel(false)}/>}

      </div>
    </AuthProvider>
  );
};

export default Page;