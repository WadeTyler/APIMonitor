import React from 'react';
import {RiEyeLine} from "@remixicon/react";

const ShowButton = ({toggleShow}: {
  toggleShow: () => void;
}) => {
  return (
    <RiEyeLine
      className="text-sm p-1 text-light bg-dark rounded-md relative cursor-pointer hover:bg-primary-dark duration-200"
      onClick={toggleShow}
    />
  );
};

export default ShowButton;