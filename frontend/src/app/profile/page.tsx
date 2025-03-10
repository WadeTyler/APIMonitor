'use client';
import React from 'react';
import AuthProvider from "@/components/providers/AuthProvider";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {logout} from "@/lib/util/AuthUtil";

const Page = () => {

  const queryClient = useQueryClient();

  const {mutate:logoutMutation, isPending:isLoggingOut} = useMutation({
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

  return (
    <AuthProvider>
      <div className="page-padding w-full min-h-screen">

        <button className="submit-btn" disabled={isLoggingOut} onClick={handleLogout}>Logout</button>

      </div>
    </AuthProvider>
  );
};

export default Page;