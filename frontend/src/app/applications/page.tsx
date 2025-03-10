'use client';
import React, {useEffect} from 'react';
import AuthProvider from "@/components/providers/AuthProvider";
import {useQuery, useQueryClient} from "@tanstack/react-query";
import {fetchApplications} from "@/lib/util/ApplicationUtil";
import {fetchAuthUser} from "@/lib/util/AuthUtil";
import Loader from "@/components/util/Loader";
import {
  RiAppStoreLine,
  RiLinksLine,
  RiPencilLine,
  RiResetRightLine,
  RiRouteLine,
  RiSignalTowerLine
} from "@remixicon/react";
import Link from "next/link";
import {useRouter} from "next/navigation";

const Page = () => {

  const router = useRouter();
  const queryClient = useQueryClient();

  const {
    data: applications,
    isLoading: isLoadingApplications,
    error: loadApplicationsError,
    isError: isLoadApplicationsError
  } = useQuery({
    queryKey: ['applications'],
    queryFn: fetchApplications,
  });

  const {data: authUser} = useQuery({queryKey: ['authUser'], queryFn: fetchAuthUser});

  useEffect(() => {
    queryClient.invalidateQueries({queryKey: ['applications']});
  }, [authUser]);

  function refreshApplications() {
    queryClient.invalidateQueries({ queryKey: ['applications']});
  }

  function navigateToApplication(publicToken: string) {
    router.push(`/applications/${publicToken}`);
  }

  return (
    <AuthProvider>
      <div className="page-padding w-full min-h-screen flex justify-center">

        {isLoadingApplications && (
          <Loader/>
        )}

        {isLoadApplicationsError && (
          <div className="w-full h-full flex items-center justify-center">
            <p className="text-danger">{(loadApplicationsError as Error).message}</p>
          </div>
        )}

        {applications && !isLoadingApplications && !loadApplicationsError && (
          <div className="flex flex-col gap-4 max-w-[65rem] max-h-[40rem] h-fit w-full bg-white shadow-md rounded-md lg:p-8 p-4">
            <div className="flex items-center justify-between w-full">
              <span className="text-xl font-semibold text-dark">Applications</span>

              <button
                className="text-primary p-1 rounded-md hover:bg-dark duration-200 cursor-pointer"
                onClick={refreshApplications}
              >
                <RiResetRightLine />
              </button>
            </div>

            <div className="max-h-96 overflow-scroll rounded-md border border-gray-200 text-dark">
              <table className="table-auto w-full">
                <thead className="">
                <tr className="">
                  <th className="p-4 text-left rounded-tl-md bg-gray-50 border-b-gray-200 border-b">
                    <span className="inline-flex gap-1"><RiAppStoreLine/> Name</span>
                  </th>
                  <th className="p-4 text-left bg-gray-50 border-b-gray-200 border-b">
                    <span className="inline-flex gap-1"><RiSignalTowerLine /> Total Calls</span>
                  </th>
                  <th className="p-4 text-left bg-gray-50 border-b-gray-200 border-b">
                    <span className="inline-flex gap-1"><RiRouteLine /> Total Paths</span>
                  </th>
                  <th className="p-4 text-left rounded-tr-md bg-gray-50 border-b-gray-200 border-b">
                    <span className="inline-flex gap-1"><RiLinksLine /> Total Addresses</span>
                  </th>
                </tr>
                </thead>
                <tbody>
                {applications.map((application) => (
                  <tr key={application.id} className="hover:bg-gray-50 cursor-pointer" onClick={() => navigateToApplication(application.publicToken)}>
                    <td className="p-4 text-left border-b-gray-200 border-b">{application.name}</td>
                    <td className="p-4 text-left border-b-gray-200 border-b">{application.totalAPICalls}</td>
                    <td className="p-4 text-left border-b-gray-200 border-b">{application.uniquePaths.length}</td>
                    <td className="p-4 text-left border-b-gray-200 border-b">{application.totalUniqueRemoteAddr}</td>
                  </tr>
                ))}
                </tbody>
              </table>
            </div>
            <Link href="/applications/create" className="submit-btn2">
              <RiPencilLine />
              <span>Create an Application</span>
            </Link>
          </div>
        )}

      </div>
    </AuthProvider>
  );
};

export default Page;