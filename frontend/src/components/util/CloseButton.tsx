import React from 'react';
import {RiCloseLine} from "@remixicon/react";

const CloseButton = ({close}: {
  close: () => void;
}) => {
  return (
    <RiCloseLine className="text-primary hover:bg-dark rounded-md cursor-pointer duration-200" onClick={close} />
  );
};

export default CloseButton;