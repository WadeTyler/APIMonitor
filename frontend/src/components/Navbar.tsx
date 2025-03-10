'use client';

import React from 'react';
import {
  RiArticleLine,
  RiDashboardLine,
  RiLoginBoxLine,
  RiLoginCircleLine,
  RiRadarLine,
  RiRocketLine, RiUserLine
} from "@remixicon/react";
import Link from "next/link";
import {useQuery} from "@tanstack/react-query";
import {fetchAuthUser} from "@/lib/util/AuthUtil";
import {User} from "@/types/AuthTypes";

const Navbar = () => {

  const {data: authUser} = useQuery<User | undefined | null>({
    queryKey: ['authUser'],
    queryFn: fetchAuthUser
  })

  return (
    <div className="fixed top-0 left-0 w-full h-16 bg-dark px-8 py-2 flex items-center justify-between z-50">

      {/* Brand */}
      <Link href="/" className="inline-flex items-center gap-2 text-xl font-semibold text-light">
        <RiRadarLine className="text-primary" />
        <span>Vax Monitor</span>
      </Link>

      <nav className="flex items-center gap-8 absolute left-1/2 -translate-x-1/2">

        <Link href="/applications" className="inline-flex items-center gap-2 text-gray-300 font-semibold hover:text-light duration-200">
          <RiDashboardLine />
          <span>Applications</span>
        </Link>

        <Link href="/docs" className="inline-flex items-center gap-2 text-gray-300 font-semibold hover:text-light duration-200">
          <RiArticleLine />
          <span>Docs</span>
        </Link>

        <Link href="/docs/getting-started" className="inline-flex items-center gap-2 text-gray-300 font-semibold hover:text-light duration-200">
          <RiRocketLine />
          <span>Getting Started</span>
        </Link>
      </nav>

      <div className="flex items-center gap-4">

        {!authUser && (
          <>
            <Link href="/login" className="inline-flex items-center gap-2 text-gray-300 font-semibold hover:text-light duration-200">
              <RiLoginCircleLine />
              <span>Login</span>
            </Link>

            <Link href="/signup" className="submit-btn">
              <RiLoginBoxLine />
              <span>Signup</span>
            </Link>
          </>
        )}
        {authUser && (
          <Link href="/profile" className="rounded-md flex items-center justify-center py-1 px-2 hover:bg-secondary-transparent duration-200 cursor-pointer">
            <div className="bg-primary rounded-full p-1 text-light">
              <RiUserLine />
            </div>
          </Link>
        )}


      </div>

    </div>
  );
};

export default Navbar;
