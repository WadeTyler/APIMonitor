'use client';
import React, {useEffect, useState} from 'react';
import Link from "next/link";
import {useParams} from "next/navigation";
import {useQuery, useQueryClient} from "@tanstack/react-query";
import {fetchApplicationFromPublicToken} from "@/lib/util/ApplicationUtil";
import AuthProvider from "@/components/providers/AuthProvider";
import LoadingScreen from "@/components/util/LoadingScreen";
import {fetchAlerts} from "@/lib/util/AlertUtil";
import ApplicationInfoBar from "@/components/ApplicationInfoBar";
import {RiAlertLine, RiResetRightLine} from "@remixicon/react";
import AlertRow from "@/components/alerts/AlertRow";
import Pagination from "@/components/Pagination";
import NotFound from "@/app/not-found";

const Page = () => {
  // Params
  const params = useParams<{ publicToken: string }>();

  // States
  const [pageSize, setPageSize] = useState(50);
  const [currentPageNumber, setCurrentPageNumber] = useState(0);

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

  // Alerts
  const {
    data: alerts,
    isPending: isLoadingAlerts
  } = useQuery({
    queryKey: ['alerts', currentApplication?.id, pageSize, currentPageNumber],
    queryFn: () => fetchAlerts({
      appId: currentApplication?.id,
      pageSize: pageSize,
      pageNumber: currentPageNumber
    })
  });

  // Functions
  function refreshAlerts() {
    queryClient.invalidateQueries({queryKey: ['alerts', currentApplication?.id]});
  }

  // Use Effect
  useEffect(() => {
    // Refresh current application on public token change
    queryClient.invalidateQueries({queryKey: ['currentApplication']});
  }, [params.publicToken, queryClient]);

  useEffect(() => {
    if (currentApplication) {
      refreshAlerts();
    }
  }, [currentApplication, pageSize, currentPageNumber]);

  // Returns

  if (isLoadingCurrentApplication || isLoadingAlerts) return <LoadingScreen/>

  if (!currentApplication && !isLoadingCurrentApplication) {
    return <AuthProvider>
      <NotFound/>
    </AuthProvider>
  }

  if (currentApplication) return (
    <AuthProvider>
      <div className="page-padding w-full min-h-screen flex flex-col items-center">
        <div
          className="flex flex-col gap-4 max-w-[80rem] lg:max-h-[45rem] md:max-h-[50rem] sm:max-h-[52rem] h-full w-full bg-white shadow-md rounded-md lg:p-8 p-4">
          <div className="flex items-center justify-between w-full lg:text-base text-xs">
            <span className="text-xl font-semibold text-dark">Alerts</span>

            {/* Action Buttons */}
            <div className="flex items-center gap-4">
              {/* Config Button */}
              <Link href={`/applications/${params.publicToken}/alerts/config`} className="dashboard-btn">
                <RiAlertLine/>
                <span>Alert Config</span>
              </Link>

              {/* Refresh Button */}
              <button className="dashboard-btn" onClick={refreshAlerts}>
                <RiResetRightLine/>
              </button>

            </div>
          </div>

          {/* Info Bar */}
          {(currentApplication)
            ? <ApplicationInfoBar currentApplication={currentApplication}/>
            : <div className="animate-pulse w-full h-14 p-2 gap-4 bg-gray-300 rounded-md border-gray-200 border"/>
          }

          <div className="flex w-full items-center justify-between">
            {/*TODO: Implement Link to alert fields doc page*/}
            <Link href={"/docs/alerts"} className="app-link text-sm">
              How do alerts work?
            </Link>

            <select className="input-bar max-w-40" value={pageSize}
                    onChange={(e) => setPageSize(parseInt(e.target.value))}>
              <option value="10">10 per page</option>
              <option value="25">25 per page</option>
              <option value="50">50 per page</option>
              <option value="75">75 per page</option>
              <option value="100">100 per page</option>
            </select>
          </div>

          {/* Alerts */}
          <div className="h-full overflow-scroll rounded-md border border-gray-200 text-secondary">
            <table className="table-auto w-full md:text-sm text-xs">
              <thead className="">
              <tr className="">
                <th className="p-4 text-left rounded-tl-md bg-gray-50 border-b-gray-200 border-b">
                  ALERT FIELD
                </th>
                <th className="p-4 text-left bg-gray-50 border-b-gray-200 border-b">
                  PATH
                </th>
                <th className="p-4 text-left bg-gray-50 border-b-gray-200 border-b">
                  METHOD
                </th>
                <th className="p-4 text-left bg-gray-50 border-b-gray-200 border-b">
                  RESPONSE STATUS
                </th>
                <th className="p-4 text-left bg-gray-50 border-b-gray-200 border-b">
                  REMOTE ADDRESS
                </th>
                <th className="p-4 text-left rounded-tr-md bg-gray-50 border-b-gray-200 border-b">
                  TIMESTAMP
                </th>
              </tr>
              </thead>

              <tbody>
              {alerts?.content.map((alert) => (
                <AlertRow alert={alert} key={alert.id} />
              ))}
              </tbody>

            </table>
          </div>

          {alerts && (
            <div className="flex items-center justify-center gap-8 w-full">
              {alerts && <Pagination currentPageNum={alerts.pageable.pageNumber} setCurrentPageNum={setCurrentPageNumber} totalPages={alerts.totalPages} totalElements={alerts.totalElements} />}
            </div>
          )}
        </div>

      </div>
    </AuthProvider>
  )
    ;
};


export default Page;