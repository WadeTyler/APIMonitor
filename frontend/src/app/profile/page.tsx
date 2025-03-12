'use client';
import React, {useState} from 'react';
import AuthProvider from "@/components/providers/AuthProvider";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {fetchAuthUserProfile, logout} from "@/lib/util/AuthUtil";
import LoadingScreen from "@/components/util/LoadingScreen";
import NotFound from "@/app/not-found";
import ShowButton from "@/components/util/ShowButton";
import {RiDeleteBack2Fill, RiDeleteBin2Line, RiLockPasswordLine, RiLogoutBoxLine} from "@remixicon/react";
import {useRouter} from "next/navigation";
import Link from "next/link";

const Page = () => {

  // Routing
  const router = useRouter();

  // States
  const [showEmail, setShowEmail] = useState(false);

  // Query Data
  const queryClient = useQueryClient();

  const {data: userProfile, isPending: isLoadingUserProfile} = useQuery({
    queryKey: ['userProfile'],
    queryFn: fetchAuthUserProfile
  });

  const {mutate: logoutMutation, isPending: isLoggingOut} = useMutation({
    mutationFn: logout,
    onSuccess: () => {
      // Refresh authUser
      queryClient.invalidateQueries({queryKey: ['authUser']});
      console.log("Logout Successful");
    },
    onError: (error) => {
      console.error("Error during logout: ", error.message);
    }
  })

  function handleLogout() {
    logoutMutation();
  }

  if (isLoadingUserProfile) {
    return <LoadingScreen/>
  }

  if (!isLoadingUserProfile && !userProfile) {
    return <NotFound/>
  }

  if (userProfile) return (
    <AuthProvider>
      <div className="page-padding w-full min-h-screen flex flex-col items-center justify-center">

        <div className="max-w-[35rem] w-full bg-white rounded-md shadow-lg lg:p-8 p-4 flex flex-col lg:gap-8 gap-4">

          <div className="flex items-center flex-col gap-2">
            <span className="text-primary text-xl">User Profile</span>
          </div>

          <hr className="border w-full border-gray-300"/>

          {/* Info Panel */}
          <div className="flex flex-col gap-2 bg-gray-50 rounded-md border-gray-200 border p-2">
            {/* Email*/}
            <p className="flex items-center gap-4">
            <span><span
              className="text-secondary font-semibold">Email:</span> {showEmail ? userProfile.email : "*".repeat(userProfile.email.length)}
            </span>
              <ShowButton toggleShow={() => setShowEmail(prev => !prev)}/>
            </p>
            {/* Application Count*/}
            <p className="flex items-center">
            <span><span
              className="text-secondary font-semibold">Applications: </span>{userProfile.applicationCount}</span>
            </p>
            {/* Total API Requests */}
            <p className="flex items-center">
            <span><span
              className="text-secondary font-semibold">Total API Requests: </span>{userProfile.totalAPIRequests}</span>
            </p>
            {/* Total Unique Remote Addresses*/}
            <span className="flex items-center">
            <span><span
              className="text-secondary font-semibold">Total Unique Addresses: </span>{userProfile.totalUniqueRemoteAddresses}</span>
          </span>
          </div>

          {/* Action Buttons */}
          <div className="flex flex-col items-center gap-4 w-full">

            <Link href={"/profile/change-password"} className="submit-btn2"><RiLockPasswordLine/> Change Password</Link>
            <button className="submit-btn2" onClick={handleLogout}><RiLogoutBoxLine /> Logout</button>
            <Link href={"/profile/delete-account"} className="submit-btn2 bg-orange-500! hover:bg-orange-700!"><RiDeleteBin2Line /> Delete Account</Link>
          </div>

        </div>


      </div>
    </AuthProvider>
  );
};

export default Page;