import React from 'react';

const ConfirmPanel = ({title, description, confirmText, confirm, cancelText, cancel}: {
  title: string;
  description: string;
  confirmText: string;
  confirm: () => void;
  cancelText: string;
  cancel: () => void;
}) => {
  return (
    <div className="fixed top-0 left-0 w-full h-screen bg-black/50 flex items-center justify-center z-40">

      <div className="max-w-[30rem] overflow-hidden bg-white shadow-md rounded-md flex flex-col gap-4">
        <h5 className="text-primary text-xl px-4 pt-4">{title}</h5>
        <p className="text-secondary px-4">{description}</p>

        <div className="flex p-4 items-center justify-end gap-4 bg-dark">
          <button className="submit-btn" onClick={confirm}>{confirmText}</button>
          <button className="submit-btn bg-gray-500! hover:bg-gray-600!" onClick={cancel}>{cancelText}</button>
        </div>

      </div>

    </div>
  );
};

export default ConfirmPanel;