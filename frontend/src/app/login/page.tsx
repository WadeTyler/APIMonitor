'use client';
import React, {useState} from 'react';
import {RiLoginCircleLine} from "@remixicon/react";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {attemptLogin} from "@/lib/util/AuthUtil";
import {useRouter, useSearchParams} from "next/navigation";
import NotAuthProvider from "@/components/providers/NotAuthProvider";
import Link from "next/link";

const Page = () => {
  // Navigation
  const router = useRouter();
  const searchParams = useSearchParams(); // 'continueTo' is used to navigate to after success. If there is no continueTo then it will go to 'applications'

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const queryClient = useQueryClient();

  async function refreshAuthUser() {
    await queryClient.invalidateQueries({queryKey: ['authUser']});
  }

  const {mutate: login, error, isError, isPending} = useMutation({
    mutationFn: attemptLogin,
    onSuccess: () => {
      refreshAuthUser();
      router.push(searchParams.get('continueTo') || '/applications');
    }
  });

  const handleLogin = () => {
    login({email, password});
  }

  return (
    <NotAuthProvider redirectTo={searchParams.get('continueTo') || '/applications'}>
      <div className="w-full min-h-screen flex items-center justify-center page-padding">

        <form
          className="w-96 bg-white rounded-md shadow-lg lg:p-8 p-4 flex flex-col items-center lg:gap-8 gap-4"
          onSubmit={(e) => {
            e.preventDefault();
            handleLogin();
          }}
        >

          <div className="flex items-center flex-col gap-2">
            <span className="text-primary text-xl">Login to Vax Monitor</span>
            <p className="italic text-sm">Welcome back! Let&#39;s take a look at your APIs.</p>
          </div>

          <hr className="border w-full border-gray-300"/>

          <div className="input-container">
            <label className="input-label">Email</label>
            <input type="email" className="input-bar" placeholder="Email" required
                   onChange={(e) => setEmail(e.target.value)}/>
          </div>
          <div className="input-container">
            <label className="input-label">Password</label>
            <input type="password" className="input-bar" placeholder="Password" required
                   onChange={(e) => setPassword(e.target.value)}/>
          </div>

          {isError && (
            <p className="text-danger">{(error as Error).message}</p>
          )}

          <button className={`submit-btn2 ${isPending && 'cursor-not-allowed! bg-dark!'}`} disabled={isPending}>
            <RiLoginCircleLine/>
            Login
          </button>

          <p className="text-sm">Don&#39;t have an account? <Link href={'/signup'} className="text-primary hover:underline">Signup</Link></p>

        </form>

      </div>
    </NotAuthProvider>
  );
};

export default Page;
