'use client';

import React, {useEffect, useState} from 'react';
import {useParams} from "next/navigation";
import AuthProvider from "@/components/providers/AuthProvider";
import {useQuery, useQueryClient} from "@tanstack/react-query";
import {fetchApplicationFromPublicToken} from "@/lib/util/ApplicationUtil";
import {
  RiResetRightLine, RiSearchLine,
} from "@remixicon/react";
import ApplicationInfoBar from "@/components/ApplicationInfoBar";
import NotFound from "@/app/not-found";
import {APICall} from "@/types/APICallTypes";
import {fetchApplicationAPICalls} from "@/lib/util/APICallUtil";
import PageType from "@/types/PageTypes";
import APICallRow from "@/components/APICallRow";
import Pagination from "@/components/Pagination";


const Page = () => {

  const params = useParams<{ publicToken: string }>();

  // States
  const [page, setPage] = useState<PageType<APICall[]> | undefined>(undefined);
  const [pageSize, setPageSize] = useState<number>(50);
  const [currentPageNum, setCurrentPageNum] = useState<number>(0);
  const [search, setSearch] = useState('');

  // QueryClient
  const queryClient = useQueryClient();

  const {
    data: currentApplication,
    isPending: isLoadingCurrentApplication
  } = useQuery({
    queryKey: ['currentApplication', params.publicToken],
    queryFn: () => fetchApplicationFromPublicToken(params.publicToken)
  });

  // Use Effect
  useEffect(() => {
    queryClient.invalidateQueries({queryKey: ['currentApplication']});
  }, [params.publicToken]);

  useEffect(() => {
    refreshAPICalls();
  }, [currentApplication, pageSize, currentPageNum]);

  // Functions
  async function refreshAPICalls() {
    if (currentApplication) {
      const newPage: PageType<APICall[]> = await fetchApplicationAPICalls(currentApplication.id, pageSize, currentPageNum, search);
      setPage(newPage);
      setCurrentPageNum(newPage.pageable.pageNumber);
    } else {
      setPage(undefined);
    }
  }

  async function searchForValue(value: string) {
    await setSearch(value);
    setTimeout(() => {
      refreshAPICalls();
    }, 300);
  }

  if (!currentApplication && !isLoadingCurrentApplication) {
    return <NotFound/>
  }

  return (
    <AuthProvider>
      <div className="page-padding w-full min-h-screen flex flex-col items-center">

        <div
          className="flex flex-col gap-4 max-w-[80rem] max-h-[45rem] h-fit w-full bg-white shadow-md rounded-md lg:p-8 p-4">
          <div className="flex items-center justify-between w-full">
            <span className="text-xl font-semibold text-dark">API Request Monitor</span>

            <button
              className="text-primary p-1 rounded-md hover:bg-dark duration-200 cursor-pointer"
              onClick={refreshAPICalls}
            >
              <RiResetRightLine/>
            </button>
          </div>

          {/* Info Bar */}
          {(currentApplication && page)
            ? <ApplicationInfoBar currentApplication={currentApplication} searchForValue={searchForValue}/>
            : <div className="animate-pulse w-full h-14 p-2 gap-4 bg-gray-300 rounded-md border-gray-200 border"/>
          }

          {/* Controls */}
          <div className="flex items-center justify-between gap-4 w-full">
            {/* Search Bar */}
            <form
              className="input-bar w-full inline-flex items-center gap-2 focus-within:border-primary!"
              onSubmit={(e) => {
                e.preventDefault();
                refreshAPICalls();
              }}
            >
              <RiSearchLine className="text-gray-300"/>
              <input type="text" placeholder="Search API paths, methods, or status codes..."
                     className="w-full focus:outline-0 placeholder:text-gray-300 group" value={search}
                     onChange={(e) => setSearch(e.target.value)}/>
            </form>

            <select className="input-bar max-w-40" value={pageSize}
                    onChange={(e) => setPageSize(parseInt(e.target.value))}>
              <option value="10">10 per page</option>
              <option value="25">25 per page</option>
              <option value="50">50 per page</option>
              <option value="75">75 per page</option>
              <option value="100">100 per page</option>
            </select>

          </div>

          <div className="h-full overflow-scroll rounded-md border border-gray-200 text-secondary">
            <table className="table-auto w-full text-sm">
              <thead className="">
              <tr className="">
                <th className="p-4 text-left rounded-tl-md bg-gray-50 border-b-gray-200 border-b">PATH</th>
                <th className="p-4 text-left bg-gray-50 border-b-gray-200 border-b">METHOD</th>
                <th className="p-4 text-left bg-gray-50 border-b-gray-200 border-b">STATUS</th>
                <th className="p-4 text-left bg-gray-50 border-b-gray-200 border-b">REMOTE ADDRESS</th>
                <th className="p-4 text-left rounded-tr-md bg-gray-50 border-b-gray-200 border-b">TIMESTAMP</th>
              </tr>
              </thead>
              <tbody>
              {page?.content.map((apiCall) => (
                <APICallRow key={apiCall.id} apiCall={apiCall}/>
              ))}
              </tbody>
            </table>
          </div>

          {page && <Pagination currentPageNum={currentPageNum} setCurrentPageNum={setCurrentPageNum}
                               totalPages={page.totalPages}/>}

        </div>
      </div>
    </AuthProvider>
  );
};

export default Page;