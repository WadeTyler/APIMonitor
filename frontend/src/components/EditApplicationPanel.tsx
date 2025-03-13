import React, {useState} from 'react';
import {Application} from "@/types/ApplicationTypes";
import {RiDeleteBinLine, RiEdit2Line} from "@remixicon/react";
import CloseButton from "@/components/util/CloseButton";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {deleteApplication} from "@/lib/util/ApplicationUtil";
import toast from "react-hot-toast";
import {useRouter} from "next/navigation";

const EditApplicationPanel = ({close, currentApplication}: {
  close: () => void;
  currentApplication: Application;
}) => {

  // Routing
  const router = useRouter();

  // States
  const [showConfirmDelete, setShowConfirmDelete] = useState(false);
  const [isError, setIsError] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  // Query Data
  const queryClient = useQueryClient();


  const {mutate:deleteApplicationMutation, isPending:isDeletingApplication} = useMutation({
    mutationFn: deleteApplication,
    onError: (e) => {
      setIsError(true);
      setError(e);
    },
    onSuccess: () => {
      toast.success("Application Deleted Successfully.");
      queryClient.setQueryData(['currentApplication'], null);
      router.push('/applications');
    }
  })


  // Functions
  async function handleDeleteApplication() {
    if (isDeletingApplication) return;
    setIsError(false);
    setError(null);
    deleteApplicationMutation(currentApplication.id);
  }

  return (
    <div className="page-padding w-96 h-screen fixed right-0 top-0 bg-white shadow-lg flex flex-col gap-4">

      <div className="inline-flex items-center justify-between">
        <h1 className="text-dark font-semibold text-xl">Edit Application</h1>
        <CloseButton close={close} />
      </div>

      <hr className="border w-full border-secondary"/>

      <div className="input-container">
        <label className="input-label">Application Name:</label>
        <input type="text" className="input-bar" required minLength={3} maxLength={50} placeholder={"Application Name"} defaultValue={currentApplication.name} />
      </div>

      <button className="submit-btn2"><RiEdit2Line /> Save Changes</button>

      <button className={`submit-btn2 bg-orange-600! hover:bg-orange-700! ${isDeletingApplication && 'bg-orange-800! hover:bg-orange-800!'}`} onClick={() => setShowConfirmDelete(true)} disabled={isDeletingApplication}>
        <RiDeleteBinLine /> Delete Application
      </button>

      {isError && (
        <p className="text-danger text-sm">{(error as Error).message}</p>
      )}

      {showConfirmDelete && (
        <ConfirmDeleteApplicationPrompt handleDeleteApplication={handleDeleteApplication} cancel={() => setShowConfirmDelete(false)} />
      )}

    </div>
  );
};

export default EditApplicationPanel;

const ConfirmDeleteApplicationPrompt = ({handleDeleteApplication, cancel}: {
  handleDeleteApplication: () => void;
  cancel: () => void;
}) => {

  return (
    <div className="fixed top-0 left-0 w-full h-screen z-[60] bg-black/50 flex items-center justify-center">
      <div className="bg-white w-96 rounded-md shadow-md flex flex-col gap-2 overflow-hidden">

        <h4 className="text-primary text-lg px-4 pt-4">You are about to delete your application!</h4>
        <p className="text-secondary px-4 ">This action is irreversible. All of your monitored API Calls will be lost forever. Are you sure you want to delete your application?</p>

        <section className="w-full p-4  flex flex-col items-center gap-2 bg-dark">

          <button className="submit-btn2 text-sm!" onClick={handleDeleteApplication}>Yes, delete my application.</button>

          <button className="submit-btn2 text-sm! bg-gray-500! hover:bg-gray-600!" onClick={cancel}>
            Nevermind
          </button>
        </section>

      </div>

    </div>
  )

}