'use client';

import React from 'react';
import {useParams} from "next/navigation";


const Page = () => {

  const params = useParams<{ publicToken: string }>();

  return (
    <div className="page-padding w-full min-h-screen flex flex-col items-center">
      {params.publicToken}
    </div>
  );
};

export default Page;