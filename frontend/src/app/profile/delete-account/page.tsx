'use client';
import React, {useEffect, useState} from 'react';
import {RiDeleteBin2Line} from "@remixicon/react";
import ConfirmPanel from "@/components/util/ConfirmPanel";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {attemptDeleteAccount, fetchAuthUser, sendDeleteAccountVerificationCode} from "@/lib/util/AuthUtil";
import {useRouter} from "next/navigation";
import LoadingScreen from "@/components/util/LoadingScreen";
import AuthProvider from "@/components/providers/AuthProvider";
import toast from "react-hot-toast";

const Page = () => {

  // Router
  const router = useRouter();

  // States
  const [verificationEmailSent, setVerificationEmailSent] = useState(false);
  const [verificationCode, setVerificationCode] = useState<string>('');
  const [password, setPassword] = useState<string>('');

  const [showConfirmPanel, setShowConfirmPanel] = useState(false);

  // Query
  const queryClient = useQueryClient();

  const {data:authUser} = useQuery({queryKey: ['authUser'], queryFn: fetchAuthUser});

  const {mutate: sendCode, isPending: isSendingCode, error: sendCodeError, isError: isSendCodeError} = useMutation({
    mutationFn: sendDeleteAccountVerificationCode,
    onSuccess: () => {
      setVerificationEmailSent(true);
      toast.success("A new verification code has been sent to your email!");
    }
  });

  const {mutate: deleteAccount, isPending:isDeletingAccount, error:deleteAccountError, isError:isDeleteAccountError} = useMutation({
    mutationFn: attemptDeleteAccount,
    onSuccess: async () => {
      await queryClient.invalidateQueries({queryKey: ['authUser']});
      router.push('/');
    }
  })


  // Functions
  function handleDeleteAccount() {
    if (isSendingCode || isDeletingAccount) return;
    setShowConfirmPanel(false);
    deleteAccount({password, verificationCode});
  }

  function handleShowPanel() {
    if (!verificationCode || verificationCode.length < 4 || !password || password.length < 6) return;
    setShowConfirmPanel(true);
  }

  // Use Effect

  // Send Code on render
  useEffect(() => {
    if (authUser) {
      sendCode();
    }
  }, [sendCode, authUser]);

  if (!verificationEmailSent && isSendingCode) {
    return <LoadingScreen/>
  }

  if (isSendCodeError) {
    return (
      <div className="page-padding flex items-center justify-center">
        <p>Error: {(sendCodeError).message}</p>
      </div>
    )
  }

  else return (
    <AuthProvider>
      <div className="page-padding w-full min-h-screen flex items-center justify-center">

        <div className="form-container max-w-96">

          <div className="flex flex-col gap-1">
            <h5 className="text-primary text-center text-lg">Delete your Account</h5>
            <p className="text-sm italic">Please enter the verification code sent to your attached email!</p>
          </div>
          <hr className="border w-full border-gray-300"/>

          <div className="input-container">
            <label className="input-label">Verification Code:</label>
            <input type="text" className="input-bar" required minLength={4} placeholder="Verification Code"
                   value={verificationCode}
                   onChange={(e) => setVerificationCode(e.target.value)}/>
          </div>

          <div className="input-container">
            <label className="input-label">Password:</label>
            <input type="password" className="input-bar" required minLength={6} maxLength={50} placeholder="Password"
                   value={password}
                   onChange={(e) => setPassword(e.target.value)}/>
          </div>

          <button
            className={`submit-btn2 ${(isDeletingAccount || isSendingCode) && 'bg-dark! hover:bg-dark!'}`}
            onClick={handleShowPanel}
            disabled={isDeletingAccount || isSendingCode}
          >
            <RiDeleteBin2Line/> Delete Account
          </button>

          {isDeleteAccountError && (
            <p className="text-sm text-danger text-center">{(deleteAccountError as Error).message}</p>
          )}

        </div>

        {showConfirmPanel && <ConfirmPanel
          title="You are about to delete your account!"
          description="This action is irreversible! Your application API Request data will be permanently deleted. Are you sure you want to delete your account?"
          confirmText="Yes, delete my account and data" confirm={handleDeleteAccount}
          cancelText="Nevermind" cancel={() => setShowConfirmPanel(false)}
        />
        }


      </div>
    </AuthProvider>
  );
};

export default Page;