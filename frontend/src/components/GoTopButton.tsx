'use client'
import React, {useEffect, useState} from 'react';
import {RiArrowUpBoxLine} from "@remixicon/react";

const GoTopButton = ({cn}: {
  cn?: string
}) => {

  const [showButton, setShowButton] = useState(false);

  function scrollToTopOfPage() {
    window.scrollTo({
      top: 0,
      behavior: "instant"
    });
  };

  useEffect(() => {
    const handleVisibleButton = () => {
      const position = window.pageYOffset;

      if (position > 300) {
        return setShowButton(true);
      }
      return setShowButton(false);
    }

    window.addEventListener('scroll', handleVisibleButton);

    return () => {
      window.removeEventListener('scroll', handleVisibleButton);
    }
  }, []);

  if (showButton) return (
    <button
      className={`flex items-center justify-center w-8 h-8 fixed lg:bottom-16 lg:right-16 md:bottom-8 md:right-8 bottom-4 right-4 z-50 bg-dark text-light hover:bg-primary cursor-pointer rounded-md ${cn}`}
      onClick={scrollToTopOfPage}
    >
      <RiArrowUpBoxLine/>
    </button>
  );
};

export default GoTopButton;