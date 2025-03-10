import React, {SetStateAction} from 'react';

const Pagination = ({currentPageNum, setCurrentPageNum, totalPages}: {
  currentPageNum: number;
  setCurrentPageNum: React.Dispatch<SetStateAction<number>>;
  totalPages: number;
}) => {

  const prevPage = () => {
    if (currentPageNum === 0) return;
    else {
      setCurrentPageNum(prev => prev - 1);
    }
  }

  const nextPage = () => {
    if (currentPageNum === totalPages - 1) return;
    else {
      setCurrentPageNum(prev => prev + 1);
    }
  }

  const navigateToPage = (pageNum: number) => {
    setCurrentPageNum(pageNum);
  }

  return (
    <div className="flex items-center justify-center gap-2 text-secondary">

      {/* Prev Button */}
      {currentPageNum > 0 && (
        <button
          className="w-10 h-10 rounded-md border-gray-300 border hover:bg-gray-100 duration-200 cursor-pointer"
          onClick={prevPage}
        >
          {"<"}
        </button>
      )}

      {/* Current Page */}
      <button
        className="bg-primary text-light w-10 h-10 rounded-md border-gray-300 border cursor-pointer"
      >
        {currentPageNum + 1}
      </button>

      {/* Current Page + 2 */}
      {totalPages >= currentPageNum + 2 && (
        <button
          className="w-10 h-10 rounded-md border-gray-300 border hover:bg-gray-100 duration-200 cursor-pointer"
          onClick={() => navigateToPage(currentPageNum + 1)}
        >
          {currentPageNum + 2}
        </button>
      )}

      {/* Current Page + 3 */}
      {totalPages >= currentPageNum + 3 && (
        <button
          className="w-10 h-10 rounded-md border-gray-300 border hover:bg-gray-100 duration-200 cursor-pointer"
          onClick={() => navigateToPage(currentPageNum + 2)}
        >
          {currentPageNum + 3}
        </button>
      )}

      {/* Current Page + 4 */}
      {totalPages >= currentPageNum + 4 && (
        <button
          className="w-10 h-10 rounded-md border-gray-300 border hover:bg-gray-100 duration-200 cursor-pointer"
          onClick={() => navigateToPage(currentPageNum + 3)}
        >
          {currentPageNum + 4}
        </button>
      )}

      <span className="text-sm mx-2">of {totalPages} pages</span>

      {/* Prev Button */}
      {currentPageNum < totalPages - 1 && (
        <button
          className="w-10 h-10 rounded-md border-gray-300 border hover:bg-gray-100 duration-200 cursor-pointer"
          onClick={nextPage}
        >
          {">"}
        </button>
      )}


    </div>
  );
};

export default Pagination;