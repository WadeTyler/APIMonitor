'use client';
import React, {useState} from 'react';
import NotAuthProvider from "@/components/providers/NotAuthProvider";
import {
  RiArrowLeftLongLine,
  RiCheckboxLine,
  RiLoginBoxLine
} from "@remixicon/react";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {attemptSignup, attemptSignupVerification} from "@/lib/util/AuthUtil";
import toast from "react-hot-toast";
import {useRouter} from "next/navigation";
import Link from "next/link";

const Page = () => {

  // Routing
  const router = useRouter();

  // States
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [verificationEmailSent, setVerificationEmailSent] = useState(false);

  // Query Data
  const queryClient = useQueryClient();

  const { mutate:signup, isPending:isSigningUp, error:signupError, isError:isSignupError} = useMutation({
    mutationFn: attemptSignup,
    onSuccess: () => {
      setVerificationEmailSent(true);
      toast.success("Verification Code Sent! Check your email.");
    },
    onError: () => {
      setVerificationEmailSent(false);
    }
  });

  const { mutate:signupVerification, isPending:isVerifyingSignup, error:verificationError, isError:isVerificationError} = useMutation({
    mutationFn: attemptSignupVerification,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['authUser'] });
      router.push('/applications');
    }
  })

  // Functions
  function handleSignup() {
    if (isSigningUp) return;
    signup({email, password, confirmPassword});
  }

  function handleSignupVerification() {
    if (isVerifyingSignup || isSigningUp) return;
    signupVerification({email, password, confirmPassword, verificationCode});
  }

  return (
    <NotAuthProvider>
      <div className="w-full min-h-screen flex items-center justify-center page-padding">

        {!verificationEmailSent && (
          <form
            className="w-96 bg-white rounded-md shadow-lg lg:p-8 p-4 flex flex-col items-center lg:gap-8 gap-4"
            onSubmit={(e) => {
              e.preventDefault();
              handleSignup();
            }}
          >

            <div className="flex items-center flex-col gap-2">
              <span className="text-primary text-xl">Signup for Vax Monitor</span>
              <p className="italic text-sm">Start monitoring your API Requests in minutes!</p>
            </div>

            <hr className="border w-full border-gray-300"/>

            <div className="input-container">
              <label className="input-label">Email</label>
              <input type="email" className="input-bar" placeholder="Email" required maxLength={100}
                     onChange={(e) => setEmail(e.target.value)}/>
            </div>
            <div className="input-container">
              <label className="input-label">Password</label>
              <input type="password" className="input-bar" placeholder="Password" required minLength={6} maxLength={50}
                     onChange={(e) => setPassword(e.target.value)}/>
            </div>
            <div className="input-container">
              <label className="input-label">Confirm Password</label>
              <input type="password" className="input-bar" placeholder="Confirm Password" required minLength={6} maxLength={50}
                     onChange={(e) => setConfirmPassword(e.target.value)}/>
            </div>

            {isSignupError && (
              <p className="text-danger">{(signupError as Error).message}</p>
            )}

            <button className={`submit-btn2 ${isSigningUp && 'cursor-not-allowed! bg-dark!'}`} disabled={isSigningUp}>
              <RiLoginBoxLine />
              Signup
            </button>
            <p className="text-sm">Already have an account? <Link href={'/login'} className="text-primary hover:underline">Login</Link></p>

          </form>
        )}

        {verificationEmailSent && (
          <form
            className="w-96 bg-white rounded-md shadow-lg lg:p-8 p-4 flex flex-col items-center lg:gap-8 gap-4"
            onSubmit={(e) => {
              e.preventDefault();
              handleSignupVerification();
            }}
          >

            <div className="flex items-center flex-col gap-2">
              <span className="text-primary text-xl">Verification Code</span>
              <p className="italic text-sm">Check your email for a verification code!</p>
            </div>

            <hr className="border w-full border-gray-300"/>

            <div className="input-container">
              <label className="input-label">Verification Code:</label>
              <input type="text" className="input-bar" placeholder="Verification Code" required minLength={4} value={verificationCode} onChange={(e) => setVerificationCode(e.target.value)}/>
            </div>

            {isVerificationError && (
              <p className="text-danger">{(verificationError as Error).message}</p>
            )}

            <button className={`submit-btn2 ${(isSigningUp || isVerifyingSignup)  && 'cursor-not-allowed! bg-dark!'}`} disabled={isSigningUp || isVerifyingSignup}>
              <RiCheckboxLine />
              Finish Signup
            </button>

            <button className="submit-btn2 bg-gray-300! hover:bg-gray-500" onClick={() => setVerificationEmailSent(false)}><RiArrowLeftLongLine />Go Back</button>

            <p className="text-sm">Didn&#39;t receive a verifcation code? <span className="text-primary hover:underline cursor-pointer" onClick={handleSignup}>Resend</span></p>


          </form>
        )}

      </div>
    </NotAuthProvider>
  );
};

export default Page;