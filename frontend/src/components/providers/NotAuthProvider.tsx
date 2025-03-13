'use client';
import React, {useEffect} from 'react';
import {useQuery} from "@tanstack/react-query";
import {User} from "@/types/AuthTypes";
import {fetchAuthUser} from "@/lib/util/AuthUtil";
import {useRouter} from "next/navigation";
import Loader from "@/components/util/Loader";

const NotAuthProvider = ({children, redirectTo = "/applications"}: {
  children: React.ReactNode;
  redirectTo?: string;
}) => {

  const router = useRouter();

  const {data: authUser, isPending: loadingAuth} = useQuery<User | undefined | null>({
    queryKey: ['authUser'],
    queryFn: fetchAuthUser
  });

  useEffect(() => {
    if (!loadingAuth && authUser) {
      router.push(redirectTo);
    }
  }, [loadingAuth, authUser, !authUser]);

  if (loadingAuth) {
    return (
      <div className="w-full h-screen flex items-center justify-center">
        <Loader />
      </div>
    )
  }
  if (!authUser) return (
    <>
      {children}
    </>
  )
};

export default NotAuthProvider;