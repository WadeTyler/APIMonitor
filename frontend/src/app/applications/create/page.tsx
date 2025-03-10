'use client';
import React, {useState} from 'react';
import AuthProvider from "@/components/providers/AuthProvider";
import {RiPencilLine} from "@remixicon/react";
import {useMutation} from "@tanstack/react-query";
import {createApplication} from "@/lib/util/ApplicationUtil";
import {useRouter} from "next/navigation";
import {Application} from "@/types/ApplicationTypes";

const Page = () => {

  const router = useRouter();

  const [name, setName] = useState('');

  const {
    mutate: createApplicationMutation,
    isPending: isCreatingApplication,
    error: createApplicationError,
    isError: isCreateApplicationError
  } = useMutation({
    mutationFn: createApplication,
    onSuccess: (application: Application) => {
      router.push(`/applications/${application.publicToken}`);
    }
  });

  function handleCreateApplication() {
    if (isCreatingApplication) return;

    createApplicationMutation({name});
  }

  return (
    <AuthProvider>
      <div className="w-full min-h-screen items-center justify-center flex">

        <form
          className="w-96 bg-white rounded-md shadow-lg lg:p-8 p-4 flex flex-col items-center lg:gap-8 gap-4"
          onSubmit={(e) => {
            e.preventDefault();
            handleCreateApplication();
          }}
        >
          <div className="flex items-center flex-col gap-2">
            <span className="text-primary text-xl">Create an Application</span>
            <p className="italic text-sm">Get started with adding your application.</p>
          </div>

          <hr className="border w-full border-gray-300"/>

          <div className="input-container">
            <label className="input-label">
              Application Name
            </label>
            <input type="text" className="input-bar" placeholder="Application Name" value={name}
                   onChange={(e) => setName(e.target.value)} required maxLength={50} minLength={3}/>
          </div>

          {isCreateApplicationError && (
            <p className="text-danger text-sm">{(createApplicationError).message}</p>
          )}

          <button className={`submit-btn2 ${isCreatingApplication && 'cursor-not-allowed! bg-primary-dark!'}`} disabled={isCreatingApplication}>
            <RiPencilLine/>
            <span>Create Application</span>
          </button>
        </form>

      </div>
    </AuthProvider>
  );
};

export default Page;