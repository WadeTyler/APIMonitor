'use client';
import React, {useState} from 'react';
import AuthProvider from "@/components/providers/AuthProvider";
import {RiLockPasswordLine} from "@remixicon/react";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {attemptChangePassword} from "@/lib/util/AuthUtil";
import toast from "react-hot-toast";

const Page = () => {

  // States
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmNewPassword, setConfirmNewPassword] = useState('');
  const [showConfirmChangePrompt, setShowConfirmChangePrompt] = useState(false);

  // React Query
  const queryClient = useQueryClient();

  const {mutate:changePassword, isPending:isChangingPassword, isError:isChangePasswordError, error:changePasswordError } = useMutation({
    mutationFn: attemptChangePassword,
    onSuccess: async () => {
      // Log user out
      toast.success("Password Changed Successfully.");
      await queryClient.invalidateQueries({queryKey: ['authUser']});
    },
    onError: () => {
      // Reset values on error
      setCurrentPassword('');
      setNewPassword('');
      setConfirmNewPassword('');
    }
  });

  // Functions
  async function handleChangePassword() {
    if (isChangingPassword) return;
    setShowConfirmChangePrompt(false);
    changePassword({currentPassword, newPassword, confirmNewPassword});
  }

  return (
    <AuthProvider>
      <div className="page-padding w-full min-h-screen flex flex-col items-center justify-center">

        <form
          className="w-96 bg-white rounded-md shadow-lg lg:p-8 p-4 flex flex-col items-center lg:gap-8 gap-4"
          onSubmit={(e) => {
            e.preventDefault();
            if (isChangingPassword) return;
            setShowConfirmChangePrompt(true);
          }}
        >

          <div className="flex items-center flex-col gap-2">
            <span className="text-primary text-xl">Change your Password</span>
            <p className="italic text-sm">If you have forgotten your current password. Please logout then follow the Forgot Password prompt.</p>
          </div>

          <hr className="border w-full border-gray-300"/>

          <div className="input-container">
            <label className="input-label">Current Password:</label>
            <input type="password" required minLength={6} maxLength={50} placeholder="Current Password" className="input-bar" onChange={(e) => setCurrentPassword(e.target.value)} value={currentPassword} />
          </div>

          <div className="input-container">
            <label className="input-label">New Password:</label>
            <input type="password" required minLength={6} maxLength={50} placeholder="New Password" className="input-bar" onChange={(e) => setNewPassword(e.target.value)} value={newPassword} />
          </div>

          <div className="input-container">
            <label className="input-label">Confirm New Password:</label>
            <input type="password" required minLength={6} maxLength={50} placeholder="Confirm New Password" className="input-bar" onChange={(e) => setConfirmNewPassword(e.target.value)} value={confirmNewPassword} />
          </div>

          {isChangePasswordError && (
            <p className="text-danger text-sm">{(changePasswordError as Error).message}</p>
          )}

          <button className={`submit-btn2 ${isChangingPassword && 'bg-dark!'}`} disabled={isChangingPassword}><RiLockPasswordLine/> Change Password</button>
        </form>

        {showConfirmChangePrompt && <ConfirmChangePasswordPrompt handleChangePassword={handleChangePassword} cancel={() => setShowConfirmChangePrompt(false)} />}

      </div>
    </AuthProvider>
  );
};

export default Page;

const ConfirmChangePasswordPrompt = ({handleChangePassword, cancel}: {
  handleChangePassword: () => void;
  cancel: () => void;
}) => {

  return (
    <div className="fixed top-0 left-0 w-full h-screen z-[60] bg-black/50 flex items-center justify-center">
      <div className="bg-white w-96 rounded-md shadow-md flex flex-col gap-2 overflow-hidden">

        <h4 className="text-primary text-lg px-4 pt-4">You are about to change your password!</h4>
        <p className="text-secondary px-4 ">This action is irreversible. Are you sure you want to change your password?</p>

        <section className="w-full p-4  flex flex-col items-center gap-2 bg-dark">

          <button className="submit-btn2 text-sm!" onClick={handleChangePassword}>Yes, Change my Password</button>

          <button className="submit-btn2 text-sm! bg-gray-500! hover:bg-gray-600!" onClick={cancel}>
            Nevermind
          </button>
        </section>

      </div>

    </div>
  )

}