'use client';

import {useQuery} from "@tanstack/react-query";
import {fetchAuthUser} from "@/lib/util/AuthUtil";
import {useEffect} from "react";
import {User} from "@/types/AuthTypes";

const LoadQueries = () => {
  const {data: authUser} = useQuery<User | null>({
    queryKey: ["authUser"],
    queryFn: fetchAuthUser,
    retry: false
  });

  useEffect(() => {
    console.log("Auth User Updated: ", authUser);
  }, [authUser]);

  return null;
};

export default LoadQueries;